package tech.marcosmartinelli.springsecurity.modules.album;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.marcosmartinelli.springsecurity.modules.album.dtos.AlbumRequestDTO;
import tech.marcosmartinelli.springsecurity.modules.album.dtos.AlbumResponseDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/albums")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    @Transactional
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_BASIC')")
    public ResponseEntity<AlbumResponseDTO> newAlbum(@RequestBody AlbumRequestDTO albumRequestDTO) {
        AlbumResponseDTO newAlbum = this.albumService.createAlbum(albumRequestDTO);

        return ResponseEntity.ok(newAlbum);
    }
}
