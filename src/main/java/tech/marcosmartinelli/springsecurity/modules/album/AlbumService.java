package tech.marcosmartinelli.springsecurity.modules.album;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tech.marcosmartinelli.springsecurity.exception.ResourceNotFoundException;
import tech.marcosmartinelli.springsecurity.modules.album.dtos.*;
import tech.marcosmartinelli.springsecurity.modules.image.Image;
import tech.marcosmartinelli.springsecurity.modules.image.ImageRepository;
import tech.marcosmartinelli.springsecurity.modules.image.ImageService;
import tech.marcosmartinelli.springsecurity.modules.users.User;
import tech.marcosmartinelli.springsecurity.modules.users.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ImageService imageService;
    private final ImageRepository imageRepository;

    public AlbumResponseDTO createAlbum(AlbumRequestDTO albumRequestDTO, JwtAuthenticationToken token) {
        User user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));;

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
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));;

        List<Album> albums = this.albumRepository.findAllByUser(user);

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
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado"));;

        List<Image> imagesFromDb = this.imageRepository.findAllByAlbum(album);

        ImageDTO coverImageDTO = null;
        if (album.getCoverImage() != null) {
            coverImageDTO = new ImageDTO(
                    album.getCoverImage().getImageId(),
                    album.getCoverImage().getImgUrl()
            );
        }

        assert album.getCoverImage() != null;
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
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado"));;

        albumToDelete.getImages().forEach(image -> image.setAlbum(null));

        this.albumRepository.delete(albumToDelete);
    }
}
