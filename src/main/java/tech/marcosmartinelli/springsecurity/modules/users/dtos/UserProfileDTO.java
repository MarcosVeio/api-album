package tech.marcosmartinelli.springsecurity.modules.users.dtos;

import lombok.Getter;
import tech.marcosmartinelli.springsecurity.modules.role.Role;
import tech.marcosmartinelli.springsecurity.modules.users.User;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class UserProfileDTO {
    private final UUID userId;
    private final String username;
    private final List<String> roles;

    public UserProfileDTO(User user) {
        this.userId = user.getUserId();
        this.username = user.getUsername();
        this.roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}
