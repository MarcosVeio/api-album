package tech.marcosmartinelli.springsecurity.modules.image.dtos;

import java.util.UUID;

public record ImagesByAlbumResponseDTO(
        UUID imageId,
        String imgUrl,
        String fileName,
        String fileType,
        Long fileSize
) {
}
