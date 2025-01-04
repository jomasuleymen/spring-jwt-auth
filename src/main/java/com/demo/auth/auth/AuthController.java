package com.demo.auth.auth;

import com.demo.auth.auth.dto.AuthenticationRequest;
import com.demo.auth.auth.dto.AuthenticationResponse;
import com.demo.auth.auth.dto.CreateUserDTO;
import com.demo.auth.auth.dto.RefreshTokenRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody @Valid CreateUserDTO dto) {
        authService.register(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new HashMap<>(
                Map.of("message", "user created")
        ));
    }

    @PostMapping(value = "/signin", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<AuthenticationResponse> signIn(@Valid AuthenticationRequest request) {
        var tokens = authService.authenticate(request);

        return ResponseEntity.ok(
                AuthenticationResponse.builder()
                        .accessToken(tokens.accessToken())
                        .refreshToken(tokens.refreshToken())
                        .build()
        );
    }

    @PostMapping(value = "/refresh-token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        var tokens = authService.refreshToken(request.getRefreshToken());

        return ResponseEntity.ok(
                AuthenticationResponse.builder()
                        .accessToken(tokens.accessToken())
                        .refreshToken(tokens.refreshToken())
                        .build()
        );
    }

}
