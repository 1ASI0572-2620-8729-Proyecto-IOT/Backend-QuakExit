package com.terraguard.quakexit.iam.controller;

import com.terraguard.quakexit.iam.dto.UserDtos.UserSummary;
import com.terraguard.quakexit.iam.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios")
public class UserController {
    private final UserService service;

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public List<UserSummary> list() {
        return service.list();
    }
}
