package com.t1.marselmkh;

import com.t1.marselmkh.dto.auth.JwtResponse;
import com.t1.marselmkh.dto.auth.LoginRequest;
import com.t1.marselmkh.service.jwt.AuthService;
import com.t1.marselmkh.service.jwt.UserDetailsImpl;
import com.t1.marselmkh.util.JwtUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;


    private LoginRequest loginRequest;
    private UserDetailsImpl userDetails;

    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "testpass";
    private static final String EMAIL = "test@example.com";
    private static final Long USER_ID = 42L;
    private static final String JWT_TOKEN = "mock-jwt-token";

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername(USERNAME);
        loginRequest.setPassword(PASSWORD);

        userDetails = new UserDetailsImpl(
                USER_ID,
                USERNAME,
                EMAIL,
                PASSWORD,
                List.of((GrantedAuthority) () -> "ROLE_USER")
        );
    }

    @Test
    void givenValidLoginRequest_whenAuthenticate_thenReturnJwtResponse() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(JWT_TOKEN);

        JwtResponse result = authService.authenticate(loginRequest);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authentication);
        assertThat(result.getToken()).isEqualTo(JWT_TOKEN);
        assertThat(result.getUsername()).isEqualTo(USERNAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
        assertThat(result.getId()).isEqualTo(USER_ID);
        assertThat(result.getRoles()).containsExactly("ROLE_USER");
    }

    @Test
    void givenAuthenticationFails_whenAuthenticate_thenThrowsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThatThrownBy(() -> authService.authenticate(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Invalid credentials");

        verify(jwtUtils, never()).generateJwtToken(any());
    }
}
