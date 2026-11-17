package com.abes.defenceprocurement.user;

import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public record AppUserPrincipal(Long id, String username, String displayName, String email,
                               String passwordHash, boolean active, List<String> roleCodes)
        implements UserDetails, java.io.Serializable {
    private static final long serialVersionUID = 1L;
    public static AppUserPrincipal from(AppUser user) {
        List<String> codes = user.getRoles().stream().map(AppRole::getCode).sorted().toList();
        return new AppUserPrincipal(user.getId(), user.getUsername(), user.getDisplayName(), user.getEmail(),
                user.getPasswordHash(), user.isActive(), codes);
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleCodes.stream().map(SimpleGrantedAuthority::new).toList();
    }
    @Override public String getUsername() { return username; }
    @Override public String getPassword() { return passwordHash; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return active; }
}
