package com.demo.auth.core.configurations.security;

import com.demo.auth.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> UserDetailsImpl.builder()
                        .id(user.getId())
                        .username(username)
                        .password(user.getPassword())
                        .role(user.getRole())
                        .build()
                )
                .orElseThrow(
                        () -> new UsernameNotFoundException("Username or password is incorrect")
                );
    }
}
