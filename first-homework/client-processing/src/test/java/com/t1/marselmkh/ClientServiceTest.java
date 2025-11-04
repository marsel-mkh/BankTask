package com.t1.marselmkh;


import com.t1.marselmkh.dto.ClientCreateDto;
import com.t1.marselmkh.dto.ClientViewDto;
import com.t1.marselmkh.dto.UserViewDto;
import com.t1.marselmkh.entity.Client;
import com.t1.marselmkh.entity.Role;
import com.t1.marselmkh.entity.RoleEnum;
import com.t1.marselmkh.entity.User;
import com.t1.marselmkh.exception.BlacklistedUserException;
import com.t1.marselmkh.mapper.ClientMapper;
import com.t1.marselmkh.mapper.UserMapper;
import com.t1.marselmkh.repository.ClientRepository;
import com.t1.marselmkh.repository.RoleRepository;
import com.t1.marselmkh.repository.UserRepository;
import com.t1.marselmkh.service.ClientService;
import com.t1.marselmkh.validation.ClientValidation;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @InjectMocks
    private ClientService clientService;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClientValidation clientValidation;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    private ClientCreateDto clientCreateDto;
    private User user;
    private Client client;
    private UserViewDto userViewDto;
    private ClientViewDto clientViewDto;
    private Role currentClientRole;
    private Role blockedRole;

    private static final String CLIENT_ID = "client-123";
    private static final String DOCUMENT_ID = "1234567890";
    private static final String EMAIL = "test@example.com";
    private static final String ENCODED_PASS = "encodedPass";

    @BeforeEach
    void setUp() {
        clientCreateDto = new ClientCreateDto();
        clientCreateDto.setClientId(CLIENT_ID);
        clientCreateDto.setDocumentId(DOCUMENT_ID);
        clientCreateDto.setEmail(EMAIL);
        clientCreateDto.setPassword("password");

        user = new User();
        user.setId(1L);
        user.setEmail(EMAIL);

        client = new Client();
        client.setId(10L);
        client.setClientId(CLIENT_ID);

        userViewDto = new UserViewDto();
        userViewDto.setEmail(EMAIL);

        clientViewDto = new ClientViewDto();

        currentClientRole = new Role(RoleEnum.CURRENT_CLIENT);
        blockedRole = new Role(RoleEnum.BLOCKED_CLIENT);
    }

    @Test
    void givenValidClient_whenUserRegistration_thenUserAndClientAreSaved() {
        when(clientMapper.toUserEntity(clientCreateDto)).thenReturn(user);
        when(passwordEncoder.encode(clientCreateDto.getPassword())).thenReturn(ENCODED_PASS);
        when(clientValidation.isBlackListed(DOCUMENT_ID)).thenReturn(false);
        when(roleRepository.findByName(RoleEnum.CURRENT_CLIENT)).thenReturn(Optional.of(currentClientRole));
        when(userMapper.toUserViewDto(user)).thenReturn(userViewDto);
        when(clientMapper.toClientEntity(clientCreateDto)).thenReturn(client);

        UserViewDto result = clientService.userRegistration(clientCreateDto);

        verify(passwordEncoder).encode("password");
        verify(userRepository).save(user);
        verify(clientRepository).save(client);
        verify(roleRepository).findByName(RoleEnum.CURRENT_CLIENT);
        assertThat(user.getRoles()).contains(currentClientRole);
        assertThat(result).isEqualTo(userViewDto);
    }

    @Test
    void givenBlacklistedClient_whenUserRegistration_thenUserSavedAndExceptionThrown() {
        when(clientMapper.toUserEntity(clientCreateDto)).thenReturn(user);
        when(passwordEncoder.encode(clientCreateDto.getPassword())).thenReturn(ENCODED_PASS);
        when(clientValidation.isBlackListed(DOCUMENT_ID)).thenReturn(true);
        when(roleRepository.findByName(RoleEnum.BLOCKED_CLIENT)).thenReturn(Optional.of(blockedRole));

        assertThatThrownBy(() -> clientService.userRegistration(clientCreateDto))
                .isInstanceOf(BlacklistedUserException.class);

        verify(userRepository).save(user);
        verify(roleRepository).findByName(RoleEnum.BLOCKED_CLIENT);
        verify(clientRepository, never()).save(any());
        assertThat(user.getRoles()).contains(blockedRole);
    }

    @Test
    void givenExistingClientId_whenGetByClientId_thenReturnViewDto() {
        when(clientRepository.findByClientId(CLIENT_ID)).thenReturn(Optional.of(client));
        when(clientMapper.toViewDto(client)).thenReturn(clientViewDto);

        ClientViewDto result = clientService.getByClientId(CLIENT_ID);

        verify(clientRepository).findByClientId(CLIENT_ID);
        assertThat(result).isEqualTo(clientViewDto);
    }

    @Test
    void givenNonExistingClientId_whenGetByClientId_thenThrowsException() {
        when(clientRepository.findByClientId(CLIENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.getByClientId(CLIENT_ID))
                .isInstanceOf(ResponseStatusException.class);

        verify(clientRepository).findByClientId(CLIENT_ID);
    }
}
