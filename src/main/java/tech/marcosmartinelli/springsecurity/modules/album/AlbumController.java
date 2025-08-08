package tech.marcosmartinelli.springsecurity.modules.album;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('SCOPE_BASIC')")
    public ResponseEntity<AlbumResponseDTO> newAlbum(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("userId") UUID userId,
            @RequestParam("coverImage") MultipartFile coverImage
            ) {
        AlbumRequestDTO newRequestDTO = new AlbumRequestDTO(userId, title, description, coverImage);
        AlbumResponseDTO newAlbum = this.albumService.createAlbum(newRequestDTO);

        return ResponseEntity.ok(newAlbum);
    }
}
