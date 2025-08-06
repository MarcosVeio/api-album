package tech.marcosmartinelli.springsecurity.modules.role.enums;

public enum RolesEnum {
    BASIC(1L),
    ADMIN(2L);

    long roleId;

    RolesEnum(long roleId) {
        this.roleId = roleId;
    }
}
