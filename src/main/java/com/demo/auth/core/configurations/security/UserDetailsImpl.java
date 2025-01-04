package com.demo.auth.core.configurations.security;

import com.demo.auth.user.UserRole;
import lombok.Builder;
import lombok.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Value
@Builder
public class UserDetailsImpl implements UserDetails {
    Long id;
    String username;
    String password;
    UserRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(
                new MyGrantedAuthority(role)
        );
    }

    private record MyGrantedAuthority(UserRole role) implements GrantedAuthority {

        @Override
        public String getAuthority() {
            return role.name();
        }
    }
}
