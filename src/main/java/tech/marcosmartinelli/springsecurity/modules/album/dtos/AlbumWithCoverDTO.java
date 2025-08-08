package tech.marcosmartinelli.springsecurity.modules.album.dtos;

import java.util.UUID;

public record AlbumWithCoverDTO(
        UUID albumId,
        String title,
        String description,
        ImageDTO coverImage
) {
}
