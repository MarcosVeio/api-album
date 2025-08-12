package tech.marcosmartinelli.springsecurity.modules.album;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import tech.marcosmartinelli.springsecurity.exception.ResourceNotFoundException;
import tech.marcosmartinelli.springsecurity.modules.users.UserRepository;

import java.util.UUID;

@Service("albumSecurityService")
@RequiredArgsConstructor
public class AlbumSecurityService {
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    public boolean isOwner(Authentication authentication, UUID albumId) {
        String username = authentication.getName();

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Álbum não encontrado"));;

        return album.getUser().getUsername().equals(username);
    }
}
