package tech.marcosmartinelli.springsecurity.modules.album.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record ImageDTO(
        UUID imageId,
        String imgUrl
) {
}
