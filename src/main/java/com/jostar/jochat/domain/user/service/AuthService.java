package com.jostar.jochat.domain.user.service;

import com.jostar.jochat.common.exception.BusinessException;
import com.jostar.jochat.common.exception.ErrorCode;
import com.jostar.jochat.domain.user.dto.SignupRequest;
import com.jostar.jochat.domain.user.entity.User;
import com.jostar.jochat.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(ErrorCode.USERNAME_DUPLICATED);
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .nickname(request.nickname())
                .build();

        userRepository.save(user);
    }
}