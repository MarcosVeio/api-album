package tech.marcosmartinelli.springsecurity.modules.album.dtos;

import java.util.UUID;

public record AlbumResponseDTO(UUID userId, String tile, String description) {
}
