package com.demo.auth.auth;

import com.demo.auth.auth.dto.AuthenticationRequest;
import com.demo.auth.auth.dto.CreateUserDTO;
import com.demo.auth.core.configurations.JwtConfig;
import com.demo.auth.auth.jwt.JwtService;
import com.demo.auth.core.configurations.security.UserDetailsImpl;
import com.demo.auth.core.exceptions.BadRequestException;
import com.demo.auth.core.exceptions.InternalServerErrorException;
import com.demo.auth.core.exceptions.InvalidCredentialsException;
import com.demo.auth.core.exceptions.UnauthorizedException;
import com.demo.auth.user.User;
import com.demo.auth.user.UserRepository;
import com.demo.auth.user.UserRole;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;

    public record TokenResponse(String accessToken, String refreshToken) {
    }

    @Transactional
    public void register(CreateUserDTO dto) {
        Optional<User> existsUser = userRepository.findByUsername(dto.getUsername());

        if (existsUser.isPresent()) {
            throw new BadRequestException("User already exists");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(UserRole.USER).build();

        userRepository.save(user);
    }

    @Transactional
    public TokenResponse authenticate(AuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            if (authentication == null) {
                throw new InvalidCredentialsException();
            }

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            return generateToken(userDetails);
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException();
        }
    }

    public TokenResponse refreshToken(String refreshToken) {
        UserDetailsImpl userDetails = this.getPayload(refreshToken);
        UserDetailsImpl newUserDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(userDetails.getUsername());

        return generateToken(newUserDetails);
    }

    public TokenResponse generateToken(UserDetailsImpl userDetails) {
        String accessToken = jwtService.generateToken(userDetails, jwtConfig.getAccessTokenExpiration());
        String refreshToken = jwtService.generateToken(userDetails, jwtConfig.getRefreshTokenExpiration());

        return new TokenResponse(accessToken, refreshToken);
    }

    public UserDetailsImpl getPayload(String token) {
        try {
            return jwtService.getPayload(token);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException("Token has expired");
        } catch (MalformedJwtException e) {
            throw new UnauthorizedException("Malformed token");
        } catch (JwtException e) {
            throw new UnauthorizedException("Occurred error while parsing token");
        } catch (Exception e) {
            throw new InternalServerErrorException("Occurred error while parsing token");
        }
    }
}