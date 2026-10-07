package com.terraguard.quakexit.iam.service;

import com.terraguard.quakexit.iam.dto.UserDtos.UserSummary;
import com.terraguard.quakexit.iam.entity.User;
import com.terraguard.quakexit.iam.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository users;

    @Transactional(readOnly = true)
    public List<UserSummary> list() {
        return users.findAll().stream().map(this::summary).toList();
    }

    private UserSummary summary(User user) {
        return new UserSummary(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getRole(),
            user.isEnabled()
        );
    }
}
