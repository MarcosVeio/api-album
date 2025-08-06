package tech.marcosmartinelli.springsecurity.modules.album.dtos;

import java.util.UUID;

public record AlbumRequestDTO(UUID userId, String title, String description) {
}
