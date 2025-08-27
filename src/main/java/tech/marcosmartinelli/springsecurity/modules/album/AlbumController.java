package tech.marcosmartinelli.springsecurity.modules.album;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.marcosmartinelli.springsecurity.modules.album.dtos.AlbumRequestDTO;
import tech.marcosmartinelli.springsecurity.modules.album.dtos.AlbumResponseDTO;
import tech.marcosmartinelli.springsecurity.modules.album.dtos.AlbumWithCoverDTO;

import java.util.List;
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

    @GetMapping
    public ResponseEntity<List<AlbumWithCoverDTO>> getAlbumsByUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        List<AlbumWithCoverDTO> getAlbums = this.albumService.getAlbumsByUser(jwt);

        return ResponseEntity.ok(getAlbums);
    }

    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumWithCoverDTO> getAlbumById(
            @PathVariable("albumId") UUID id
    ) {
        AlbumWithCoverDTO getAlbums = this.albumService.getAlbumById(id);

        return ResponseEntity.ok(getAlbums);
    }

    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumResponseDTO> updateAlbum(
            @PathVariable("albumId") UUID albumId,
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage
    ) {
        AlbumRequestDTO requestDTO = new AlbumRequestDTO(title, description, coverImage);
        AlbumResponseDTO updated = this.albumService.update(albumId, requestDTO);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> deleteAlbum(
            @PathVariable("albumId") UUID albumId
    ) {
        this.albumService.deleteAlbum(albumId);

        return ResponseEntity.noContent().build();
    }
}
