package com.abes.defenceprocurement.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.abes.defenceprocurement.user.AppUserPrincipal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AuthenticationApiFlowTest {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(4);
        AppUserPrincipal account = new AppUserPrincipal(4L, "c.mehta", "Charu Mehta", "charu@example.invalid",
                encoder.encode("unit-test-only-secret"), true, List.of("ROLE_CONTRACTS_MANAGER"));
        UserDetailsService users = username -> account;
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(users);
        provider.setPasswordEncoder(encoder);
        AuthenticationService authenticationService = new AuthenticationService(
                new ProviderManager(provider), new HttpSessionSecurityContextRepository());
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(authenticationService))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void bcryptCredentialsAuthenticateAndPersistTheSessionContext() throws Exception {
        MvcResult result = mvc.perform(post("/api/auth/login").contentType("application/json")
                        .content("{\"username\":\"c.mehta\",\"password\":\"unit-test-only-secret\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("c.mehta"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_CONTRACTS_MANAGER"))
                .andReturn();

        assertNotNull(result.getRequest().getSession(false)
                .getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));
    }

    @Test
    void wrongPasswordIsRejectedByTheAuthenticationProvider() throws Exception {
        mvc.perform(post("/api/auth/login").contentType("application/json")
                        .content("{\"username\":\"c.mehta\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }
}
