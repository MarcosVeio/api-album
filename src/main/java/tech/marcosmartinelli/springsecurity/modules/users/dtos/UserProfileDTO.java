package tech.marcosmartinelli.springsecurity.modules.users.dtos;

import tech.marcosmartinelli.springsecurity.modules.role.Role;
import tech.marcosmartinelli.springsecurity.modules.role.dtos.RoleResponseDTO;

import java.util.List;
import java.util.UUID;

public record UserProfileDTO(UUID userId, String username, List<RoleResponseDTO> role){

}
