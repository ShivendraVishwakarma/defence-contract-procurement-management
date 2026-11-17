package com.abes.defenceprocurement.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

class AuthControllerTest {
    private final AuthenticationService authenticationService = mock(AuthenticationService.class);
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        mvc = MockMvcBuilders.standaloneSetup(new AuthController(authenticationService))
                .setControllerAdvice(new ApiExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void validLoginReturnsUserProfile() throws Exception {
        when(authenticationService.login(any(LoginRequest.class), any(), any()))
                .thenReturn(new UserProfile(1L, "c.mehta", "Charu Mehta", "charu@example.invalid",
                        List.of("ROLE_CONTRACTS_MANAGER")));

        mvc.perform(post("/api/auth/login").contentType("application/json")
                        .content("{\"username\":\"c.mehta\",\"password\":\"a-placeholder-only-value\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("c.mehta"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_CONTRACTS_MANAGER"));
    }

    @Test
    void invalidCredentialsReturnGenericUnauthorizedMessage() throws Exception {
        when(authenticationService.login(any(LoginRequest.class), any(), any()))
                .thenThrow(new BadCredentialsException("sensitive detail"));

        mvc.perform(post("/api/auth/login").contentType("application/json")
                        .content("{\"username\":\"unknown\",\"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void blankLoginFieldsReturnBadRequest() throws Exception {
        mvc.perform(post("/api/auth/login").contentType("application/json")
                        .content("{\"username\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username and password are required and must be within allowed lengths"));
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mvc.perform(post("/api/auth/login").contentType("application/json").content("{invalid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Request body must be valid JSON"));
    }
}
