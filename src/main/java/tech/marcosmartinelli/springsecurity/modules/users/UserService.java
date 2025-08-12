package tech.marcosmartinelli.springsecurity.modules.users;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tech.marcosmartinelli.springsecurity.exception.ResourceNotFoundException;
import tech.marcosmartinelli.springsecurity.modules.album.Album;
import tech.marcosmartinelli.springsecurity.modules.users.dtos.UserRequestDTO;
import tech.marcosmartinelli.springsecurity.modules.users.dtos.UserResponseDTO;
import tech.marcosmartinelli.springsecurity.modules.role.Role;
import tech.marcosmartinelli.springsecurity.modules.role.enums.RolesEnum;
import tech.marcosmartinelli.springsecurity.modules.role.RoleRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        Role basicRole = roleRepository.findByName(RolesEnum.BASIC.name());
        Optional<User> userFromDb = userRepository.findByUsername(userRequestDTO.username());

        if(userFromDb.isPresent()){
            throw new IllegalArgumentException("Esse username já existe");
        }

        User newUser = new User();
        newUser.setUsername(userRequestDTO.username());
        newUser.setPassword(passwordEncoder.encode(userRequestDTO.password()));
        newUser.setRoles(Set.of(basicRole));

        userRepository.save(newUser);

        return new UserResponseDTO(newUser.getUsername(), basicRole.getName());
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<User> getUpcomingUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findUpcomingUsers(pageable);

        return usersPage.stream().toList();
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    @Transactional
    public void deleteUser(UUID userId) {
        User userToDelete = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));;

        this.userRepository.delete(userToDelete);
    }
}
