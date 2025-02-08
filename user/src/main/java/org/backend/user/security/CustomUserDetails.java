package org.backend.user.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.backend.user.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
public class CustomUserDetails implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Long userID;
    private final String username;

    @JsonIgnore
    private final String password;
    private final boolean isEnabled;
    private final boolean isLocked;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.userID = user.getId();
        this.username = user.getUserName();
        this.password = user.getPassword();
        this.isEnabled = user.getIsEnabled();
        this.isLocked = user.getIsLocked();
        this.authorities = getAuthorities(user);
    }

    protected CustomUserDetails() {
        this.userID = null;
        this.username = null;
        this.password = null;
        this.isEnabled = false;
        this.isLocked = false;
        this.authorities = null;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleName())));
        user.getPermissions().forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getPermissionName().name())));
        return authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    public Long getUsersID() {
        return userID;
    }
}