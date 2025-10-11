package com.t1.marselmkh.service;

import com.t1.marselmkh.annotation.Cached;
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
import com.t1.marselmkh.validation.ClientValidation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    public final ClientValidation clientValidation;
    private final ClientMapper clientMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional(noRollbackFor = {BlacklistedUserException.class})
    public UserViewDto userRegistration(ClientCreateDto clientCreateDto) {
        log.info("Запуск регистрации пользователя: {}", clientCreateDto.getEmail());

        User user = clientMapper.toUserEntity(clientCreateDto);
        user.setPassword(passwordEncoder.encode(clientCreateDto.getPassword()));

        if (clientValidation.isBlackListed(clientCreateDto.getDocumentId())) {
            log.warn("Попытка регистрации пользователя из черного списка: {}", clientCreateDto.getEmail());

            Role blockedRole = roleRepository.findByName(RoleEnum.BLOCKED_CLIENT)
                    .orElseGet(() -> roleRepository.save(new Role(RoleEnum.BLOCKED_CLIENT)));

            user.setRoles(Set.of(blockedRole));
            userRepository.save(user);

            throw new BlacklistedUserException("Пользователь находится в Back List");
        } else {
            Role currentClientRole = roleRepository.findByName(RoleEnum.CURRENT_CLIENT)
                    .orElseGet(() -> roleRepository.save(new Role(RoleEnum.CURRENT_CLIENT)));

            user.setRoles(Set.of(currentClientRole));
            userRepository.save(user);
            log.debug("Пользователь успешно сохранён: id={}, email={}", user.getId(), user.getEmail());

            Client client = clientMapper.toClientEntity(clientCreateDto);
            client.setUserId(user.getId());
            clientRepository.save(client);
            log.debug("Клиент успешно сохранён: id={}, userId={}", client.getId(), user.getId());

        }
        UserViewDto userViewDto = userMapper.toUserViewDto(user);
        log.info("Регистрация завершена успешно: userId={}", user.getId());
        return userViewDto;
    }

    @Cached
    @PreAuthorize("hasAuthority('SERVICE')")
    public ClientViewDto getByClientId(String id) {
        Client client = clientRepository.findByClientId(id)
                .orElseThrow(() -> {
                    log.error("Клиент с id {} не найден", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found");
                });
        log.info("Клиент найден: id={}", id);
        return clientMapper.toViewDto(client);
    }
}
