package com.abes.defenceprocurement.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.abes.defenceprocurement.user.AppUserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;

class AuthenticationServiceTest {
    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final SecurityContextRepository contextRepository = mock(SecurityContextRepository.class);
    private final AuthenticationService service = new AuthenticationService(authenticationManager, contextRepository);

    @AfterEach
    void clearSecurityContext() { SecurityContextHolder.clearContext(); }

    @Test
    void loginAuthenticatesUserAndPersistsSessionContext() {
        AppUserPrincipal principal = new AppUserPrincipal(7L, "a.sharma", "Aarav Sharma", "aarav@example.invalid",
                "{bcrypt}placeholder", true, List.of("ROLE_SAFETY_OFFICER"));
        when(authenticationManager.authenticate(org.mockito.ArgumentMatchers.any()))
                .thenReturn(UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.getAuthorities()));
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        UserProfile profile = service.login(new LoginRequest("a.sharma", "not-a-real-password"), request, response);

        assertEquals("a.sharma", profile.username());
        assertEquals(List.of("ROLE_SAFETY_OFFICER"), profile.roles());
        verify(contextRepository).saveContext(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.same(request),
                org.mockito.ArgumentMatchers.same(response));
    }

    @Test
    void failedAuthenticationDoesNotPersistASession() {
        when(authenticationManager.authenticate(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new BadCredentialsException("invalid"));

        assertThrows(BadCredentialsException.class, () -> service.login(
                new LoginRequest("unknown", "invalid"), mock(HttpServletRequest.class), mock(HttpServletResponse.class)));

        verify(contextRepository, never()).saveContext(org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
