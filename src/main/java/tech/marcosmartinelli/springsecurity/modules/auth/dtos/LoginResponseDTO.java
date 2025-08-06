package tech.marcosmartinelli.springsecurity.modules.auth.dtos;

public record LoginResponseDTO(String accessToken, Long expiresIn) {
}
