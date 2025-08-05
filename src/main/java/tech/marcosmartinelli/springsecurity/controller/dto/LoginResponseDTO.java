package tech.marcosmartinelli.springsecurity.controller.dto;

public record LoginResponseDTO(String accessToken, Long expiresIn) {
}
