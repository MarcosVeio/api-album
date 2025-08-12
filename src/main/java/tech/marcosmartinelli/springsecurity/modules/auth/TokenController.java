package tech.marcosmartinelli.springsecurity.modules.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tech.marcosmartinelli.springsecurity.modules.auth.dtos.LoginRequestDTO;
import tech.marcosmartinelli.springsecurity.modules.auth.dtos.LoginResponseDTO;
import tech.marcosmartinelli.springsecurity.modules.auth.dtos.RefreshTokenRequestDTO;
import tech.marcosmartinelli.springsecurity.modules.role.Role;
import tech.marcosmartinelli.springsecurity.modules.users.User;
import tech.marcosmartinelli.springsecurity.modules.users.UserRepository;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final long ACCESS_TOKEN_EXPIRY = 300L; // 5 minutos
    private static final long REFRESH_TOKEN_EXPIRY = 604800L; // 7 dias


    @PostMapping("/login" )
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {

        var user = userRepository.findByUsername(loginRequest.username())
                .orElseThrow(() -> new BadCredentialsException("Usuário ou senha inválidos!"));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new BadCredentialsException("Usuário ou senha inválidos!");
        }

        // Gera o Access Token e o Refresh Token
        String accessToken = createAccessToken(user);
        String refreshToken = createRefreshToken(user);

        return ResponseEntity.ok(new LoginResponseDTO(
                user.getUsername(),
                user.getUserId(),
                accessToken,
                ACCESS_TOKEN_EXPIRY,
                refreshToken
        ));
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        try {
            Jwt decodedRefreshToken = jwtDecoder.decode(refreshTokenRequest.refreshToken());

            String tokenType = decodedRefreshToken.getClaimAsString("token_type");
            if (!"refresh_token".equals(tokenType)) {
                throw new BadCredentialsException("Tipo de token inválido.");
            }

            var user = userRepository.findById(UUID.fromString(decodedRefreshToken.getSubject()))
                    .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado."));

            String newAccessToken = createAccessToken(user);

            String newRefreshToken = createRefreshToken(user);

            return ResponseEntity.ok(new LoginResponseDTO(
                    user.getUsername(),
                    user.getUserId(),
                    newAccessToken,
                    ACCESS_TOKEN_EXPIRY,
                    newRefreshToken // Envia o novo refresh token
            ));

        } catch (JwtException e) {
            throw new BadCredentialsException("Refresh token inválido ou expirado.");
        }
    }

    private String createAccessToken(User user) {
        Instant now = Instant.now();
        String scopes = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("albumapi")
                .subject(user.getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(ACCESS_TOKEN_EXPIRY))
                .claim("scope", scopes)
                .claim("token_type", "access_token") // Claim para diferenciar os tokens
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String createRefreshToken(User user) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("albumapi")
                .subject(user.getUserId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(REFRESH_TOKEN_EXPIRY))
                .claim("token_type", "refresh_token") // Claim para identificar como refresh token
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}
