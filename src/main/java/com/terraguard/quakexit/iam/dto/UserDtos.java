package com.terraguard.quakexit.iam.dto;

import com.terraguard.quakexit.common.enums.DomainEnums.Role;

public final class UserDtos {
    private UserDtos() {}

    public record UserSummary(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        Role role,
        boolean enabled
    ) {}
}
