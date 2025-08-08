package tech.marcosmartinelli.springsecurity.modules.users;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.marcosmartinelli.springsecurity.modules.users.dtos.UserProfileDTO;
import tech.marcosmartinelli.springsecurity.modules.users.dtos.UserRequestDTO;
import tech.marcosmartinelli.springsecurity.modules.users.dtos.UserResponseDTO;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @PostMapping
    public ResponseEntity<UserResponseDTO> newUser(@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO newUser = this.userService.createUser(userRequestDTO);

        return ResponseEntity.ok(newUser);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        return userRepository.findById(userId)
                .map(user -> ResponseEntity.ok(new UserProfileDTO(user)))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNPROCESSABLE_ENTITY, "User not found."));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        List<User> allUsers = this.userService.getUpcomingUsers(page, size);

        return ResponseEntity.ok(allUsers);
    }
}
