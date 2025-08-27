package tech.marcosmartinelli.springsecurity.modules.album;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.marcosmartinelli.springsecurity.exception.ResourceNotFoundException;
import tech.marcosmartinelli.springsecurity.modules.album.dtos.*;
import tech.marcosmartinelli.springsecurity.modules.image.Image;
import tech.marcosmartinelli.springsecurity.modules.image.ImageService;
import tech.marcosmartinelli.springsecurity.modules.users.User;
import tech.marcosmartinelli.springsecurity.modules.users.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private static final String ALBUM_NOT_FOUND = "Álbum não encontrado";
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    public AlbumResponseDTO createAlbum(AlbumRequestDTO albumRequestDTO, JwtAuthenticationToken token) {
        User user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Album newAlbum = new Album();
        newAlbum.setTitle(albumRequestDTO.title());
        newAlbum.setDescription(albumRequestDTO.description());
        newAlbum.setUser(user);

        MultipartFile coverImage = albumRequestDTO.coverImage();

        albumRepository.save(newAlbum);

        assert coverImage != null;
        Image newImage = imageService.createImg(coverImage, newAlbum);

        newAlbum.setCoverImage(newImage);

        return new AlbumResponseDTO(newAlbum.getUser().getUserId(), newAlbum.getTitle(), newAlbum.getDescription());
    }

    public List<AlbumWithCoverDTO> getAlbumsByUser(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        List<Album> albums;
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"))) {
            albums = this.albumRepository.findAll();
        } else {
            albums = this.albumRepository.findAllByUser(user);
        }

        return albums.stream().map(album -> {
            ImageDTO coverImageDTO = null;
            if (album.getCoverImage() != null) {
                coverImageDTO = new ImageDTO(
                        album.getCoverImage().getImageId(),
                        album.getCoverImage().getImgUrl()
                );
            }
            return new AlbumWithCoverDTO(
                    album.getAlbumId(),
                    album.getTitle(),
                    album.getDescription(),
                    coverImageDTO
            );
        }).collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @albumSecurityService.isOwner(authentication, #albumId)")
    public AlbumWithCoverDTO getAlbumById(UUID albumId) {
        Album album = this.albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(ALBUM_NOT_FOUND));

        ImageDTO coverImageDTO = null;
        if (album.getCoverImage() != null) {
            coverImageDTO = new ImageDTO(
                    album.getCoverImage().getImageId(),
                    album.getCoverImage().getImgUrl()
            );
        }

        return new AlbumWithCoverDTO(
                    album.getAlbumId(),
                    album.getTitle(),
                    album.getDescription(),
                    coverImageDTO
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @albumSecurityService.isOwner(authentication, #albumId)")
    @Transactional
    public void deleteAlbum(UUID albumId) {
        Album albumToDelete = this.albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(ALBUM_NOT_FOUND));

        albumToDelete.getImages().forEach(image -> image.setAlbum(null));

        this.albumRepository.delete(albumToDelete);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN') or @albumSecurityService.isOwner(authentication, #albumId)")
    @Transactional
    public AlbumResponseDTO update(UUID albumId, AlbumRequestDTO albumRequestDTO) {
        Album album = this.albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException(ALBUM_NOT_FOUND));

        if (albumRequestDTO.title() != null && !albumRequestDTO.title().isBlank()) {
            album.setTitle(albumRequestDTO.title());
        }
        if (albumRequestDTO.description() != null) {
            album.setDescription(albumRequestDTO.description());
        }

        MultipartFile newCoverImage = albumRequestDTO.coverImage();
        if (newCoverImage != null && !newCoverImage.isEmpty()) {
            Image newImage = imageService.createImg(newCoverImage, album);
            album.setCoverImage(newImage);
        }

        this.albumRepository.save(album);

        return new AlbumResponseDTO(
                album.getUser().getUserId(),
                album.getTitle(),
                album.getDescription()
        );
    }
}
