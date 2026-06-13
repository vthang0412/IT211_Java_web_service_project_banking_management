package com.rikkei.bank.security;

import com.rikkei.bank.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@AllArgsConstructor
public class UserPrincipal
        implements UserDetails {

    private Long id;

    private String username;

    private String password;

    private Boolean active;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserPrincipal create(
            User user
    ) {

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getIsActive(),
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" +
                                        user.getRole()
                                                .getName()
                                                .toUpperCase()
                        )
                )
        );
    }

    @Override
    public Collection<? extends GrantedAuthority>
    getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return active;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}