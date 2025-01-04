package com.demo.auth.user;

import com.demo.auth.core.exceptions.BadRequestException;
import com.demo.auth.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDTO findByUserId(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(
                        () -> new BadRequestException("User not found")
                );
    }

}
