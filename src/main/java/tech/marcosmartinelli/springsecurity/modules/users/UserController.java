package tech.marcosmartinelli.springsecurity.modules.users;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.marcosmartinelli.springsecurity.modules.users.dtos.UserRequestDTO;
import tech.marcosmartinelli.springsecurity.modules.users.dtos.UserResponseDTO;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Transactional
    @PostMapping
    public ResponseEntity<UserResponseDTO> newUser(@RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO newUser = this.userService.createUser(userRequestDTO);

        return ResponseEntity.ok(newUser);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public ResponseEntity<List<User>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        List<User> allUsers = this.userService.getUpcomingUsers(page, size);

        return ResponseEntity.ok(allUsers);
    }
}
