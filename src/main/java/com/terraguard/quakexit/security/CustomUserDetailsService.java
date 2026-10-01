package com.terraguard.quakexit.security;

import com.terraguard.quakexit.iam.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override public UserDetails loadUserByUsername(String email) {
        var user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return User.withUsername(user.getEmail()).password(user.getPasswordHash()).roles(user.getRole().name()).disabled(!user.isEnabled()).build();
    }
}
