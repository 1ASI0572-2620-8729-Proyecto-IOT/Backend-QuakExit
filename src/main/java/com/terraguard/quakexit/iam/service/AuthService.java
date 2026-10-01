package com.terraguard.quakexit.iam.service;

import com.terraguard.quakexit.common.exception.ApiExceptions.*;
import com.terraguard.quakexit.audit.service.AuditService;
import com.terraguard.quakexit.iam.dto.AuthDtos.*;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.iam.repository.UserRepository;
import com.terraguard.quakexit.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class AuthService {
    private final UserRepository users; private final PasswordEncoder encoder; private final AuthenticationManager authenticationManager; private final JwtUtil jwtUtil; private final AuditService audit;
    @Value("${app.jwt.expiration-ms}") private long expirationMs;
    @Transactional public AuthResponse register(RegisterRequest request) {
        var role = resolveRole(request);
        var ownershipRole = resolveOwnershipRole(request);
        if (role == com.terraguard.quakexit.common.enums.DomainEnums.Role.SYSTEM_ADMIN) throw new BusinessRuleException("No se puede registrar SYSTEM_ADMIN publicamente");
        if (users.existsByEmailIgnoreCase(request.email())) throw new DuplicateResourceException("El email ya esta registrado");
        User user = users.save(User.builder().fullName(request.fullName()).email(request.email().toLowerCase()).phoneNumber(request.phoneNumber()).passwordHash(encoder.encode(request.password())).role(role).propertyType(request.propertyType()).ownershipRole(ownershipRole).build());
        var response = response(user, details(user));
        audit.record(user, "LOGIN", "USER", user.getId(), null, null, null, null);
        return response;
    }
    private com.terraguard.quakexit.common.enums.DomainEnums.Role resolveRole(RegisterRequest request) {
        if (request.ownershipRole() == com.terraguard.quakexit.common.enums.DomainEnums.OwnershipRole.BUILDING_ADMIN) return com.terraguard.quakexit.common.enums.DomainEnums.Role.BUILDING_ADMIN;
        if (request.role() != null) {
            try { return com.terraguard.quakexit.common.enums.DomainEnums.Role.valueOf(request.role()); }
            catch (IllegalArgumentException ignored) { if (request.role().equals("OWNER") || request.role().equals("RENTER")) return com.terraguard.quakexit.common.enums.DomainEnums.Role.HOMEOWNER; if (request.role().equals("BUILDING_ADMIN")) return com.terraguard.quakexit.common.enums.DomainEnums.Role.BUILDING_ADMIN; throw new BusinessRuleException("Rol de registro no valido"); }
        }
        return com.terraguard.quakexit.common.enums.DomainEnums.Role.HOMEOWNER;
    }
    private com.terraguard.quakexit.common.enums.DomainEnums.OwnershipRole resolveOwnershipRole(RegisterRequest request) {
        if (request.ownershipRole() != null) return request.ownershipRole();
        if (request.role() == null) return null;
        try { return com.terraguard.quakexit.common.enums.DomainEnums.OwnershipRole.valueOf(request.role()); }
        catch (IllegalArgumentException ignored) { return null; }
    }
    public AuthResponse login(LoginRequest request) {
        var user = users.findByEmailIgnoreCase(request.email()).orElseThrow(InvalidCredentialsException::new);
        try { authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.email(), request.password())); } catch (AuthenticationException ex) { throw new InvalidCredentialsException(); }
        return response(user, details(user));
    }
    private UserDetails details(User user) { return org.springframework.security.core.userdetails.User.withUsername(user.getEmail()).password(user.getPasswordHash()).roles(user.getRole().name()).build(); }
    private AuthResponse response(User user, UserDetails details) { return new AuthResponse(jwtUtil.generateToken(details, user.getId(), user.getRole().name()), expirationMs, user.getId(), user.getFullName(), user.getEmail(), user.getRole()); }
}
