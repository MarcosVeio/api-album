package tech.marcosmartinelli.springsecurity.modules.album.dtos;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record AlbumRequestDTO(String title, String description, MultipartFile coverImage) {
}
