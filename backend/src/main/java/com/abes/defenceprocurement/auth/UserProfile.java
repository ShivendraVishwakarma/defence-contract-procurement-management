package com.abes.defenceprocurement.auth;

import com.abes.defenceprocurement.user.AppUserPrincipal;
import java.util.List;

public record UserProfile(Long id, String username, String displayName, String email, List<String> roles) {
    public static UserProfile from(AppUserPrincipal principal) {
        return new UserProfile(principal.id(), principal.username(), principal.displayName(),
                principal.email(), principal.roleCodes());
    }
}
