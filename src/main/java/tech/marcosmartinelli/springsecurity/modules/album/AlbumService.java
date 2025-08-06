package tech.marcosmartinelli.springsecurity.modules.album;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import tech.marcosmartinelli.springsecurity.modules.album.dtos.AlbumRequestDTO;
import tech.marcosmartinelli.springsecurity.modules.album.dtos.AlbumResponseDTO;
import tech.marcosmartinelli.springsecurity.modules.users.User;
import tech.marcosmartinelli.springsecurity.modules.users.UserRepository;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AlbumService {
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AlbumResponseDTO createAlbum(AlbumRequestDTO albumRequestDTO) {
        User user = userRepository.findById(albumRequestDTO.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY));

        Album newAlbum = new Album();
        newAlbum.setTitle(albumRequestDTO.title());
        newAlbum.setDescription(albumRequestDTO.description());
        newAlbum.setUser(user);

        albumRepository.save(newAlbum);

        return new AlbumResponseDTO(newAlbum.getUser().getUserId(), newAlbum.getTitle(), newAlbum.getDescription());
    }
}
