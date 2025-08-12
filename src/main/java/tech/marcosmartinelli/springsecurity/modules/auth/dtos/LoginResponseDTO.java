package tech.marcosmartinelli.springsecurity.modules.auth.dtos;

import java.util.UUID;

public record LoginResponseDTO(String username, UUID userId, String accessToken, Long expiresIn, String refreshToken) {
}
