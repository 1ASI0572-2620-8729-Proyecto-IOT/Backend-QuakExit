package com.terraguard.quakexit.iam.dto;

import com.terraguard.quakexit.common.enums.DomainEnums.*;
import jakarta.validation.constraints.*;

public final class AuthDtos {
    private AuthDtos() {}
    public record RegisterRequest(@NotBlank @Size(max=120) String fullName, @NotBlank @Email String email, @NotBlank @Pattern(regexp="^(?=.*[A-Z])(?=.*\\d).{8,}$") String password, @Pattern(regexp="^\\+?[1-9]\\d{7,14}$") String phoneNumber, String role, PropertyType propertyType, OwnershipRole ownershipRole) {
        public RegisterRequest(String fullName, String email, String password, String phoneNumber, Role role) {
            this(fullName, email, password, phoneNumber, role == null ? null : role.name(), null, null);
        }
    }
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
    public record AuthResponse(String token, long expiresInMs, Long userId, String fullName, String email, Role role) {}
    public record UserResponse(Long id, String fullName, String email, String phoneNumber, Role role) {}
}
