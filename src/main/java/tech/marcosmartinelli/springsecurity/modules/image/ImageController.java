package tech.marcosmartinelli.springsecurity.modules.image;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.marcosmartinelli.springsecurity.modules.album.AlbumRepository;
import tech.marcosmartinelli.springsecurity.modules.image.dtos.ImagesByAlbumResponseDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;
    private AlbumRepository albumRepository;

    @Transactional
    @PostMapping("/{albumId}")
    public ResponseEntity<String> uploadMultipleFiles(
            @RequestParam("files") List<MultipartFile> files,
            @PathVariable("albumId") UUID albumId
    ) {
        this.imageService.uploadMultipleFiles(files, albumId);

        return ResponseEntity.ok("Upload realizado com sucesso!");
    }

    @Transactional
    @GetMapping("/{albumId}")
    public ResponseEntity<List<ImagesByAlbumResponseDTO>> uploadMultipleFiles(
            @PathVariable("albumId") UUID albumId
    ) {
        List<ImagesByAlbumResponseDTO> images = this.imageService.findAllImagesByAlbumId(albumId);

        return ResponseEntity.ok(images);
    }

    @DeleteMapping("/{albumId}/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable("albumId") UUID albumId,
            @PathVariable("imageId") UUID imageId
    ) {
        this.imageService.deleteById(albumId, imageId);

        return ResponseEntity.accepted().build();
    }
}
