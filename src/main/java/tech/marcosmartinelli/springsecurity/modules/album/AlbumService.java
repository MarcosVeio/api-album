package tech.marcosmartinelli.springsecurity.modules.album;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tech.marcosmartinelli.springsecurity.modules.album.dtos.AlbumRequestDTO;
import tech.marcosmartinelli.springsecurity.modules.album.dtos.AlbumResponseDTO;
import tech.marcosmartinelli.springsecurity.modules.image.Image;
import tech.marcosmartinelli.springsecurity.modules.image.ImageRepository;
import tech.marcosmartinelli.springsecurity.modules.image.ImageService;
import tech.marcosmartinelli.springsecurity.modules.users.User;
import tech.marcosmartinelli.springsecurity.modules.users.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ImageService imageService;


    public AlbumResponseDTO createAlbum(AlbumRequestDTO albumRequestDTO, JwtAuthenticationToken token) {
        User user = userRepository.findById(UUID.fromString(token.getName()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY));

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
}
