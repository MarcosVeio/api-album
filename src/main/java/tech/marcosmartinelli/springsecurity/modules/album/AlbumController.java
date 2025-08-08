package tech.marcosmartinelli.springsecurity.modules.album;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.marcosmartinelli.springsecurity.modules.album.dtos.AlbumRequestDTO;
import tech.marcosmartinelli.springsecurity.modules.album.dtos.AlbumResponseDTO;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @Transactional
    @PostMapping
    public ResponseEntity<AlbumResponseDTO> newAlbum(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("coverImage") MultipartFile coverImage,
            JwtAuthenticationToken token
            ) {
        AlbumRequestDTO newRequestDTO = new AlbumRequestDTO(title, description, coverImage);
        AlbumResponseDTO newAlbum = this.albumService.createAlbum(newRequestDTO, token);

        return ResponseEntity.ok(newAlbum);
    }
}
