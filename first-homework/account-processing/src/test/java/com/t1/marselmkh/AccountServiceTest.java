package com.t1.marselmkh;


import com.t1.marselmkh.dto.ClientProductDto.ClientProductEventDto;
import com.t1.marselmkh.entity.Account;
import com.t1.marselmkh.entity.Status;
import com.t1.marselmkh.mapper.AccountMapper;
import com.t1.marselmkh.repository.AccountRepository;
import com.t1.marselmkh.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {


    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @InjectMocks
    private AccountService accountService;

    private ClientProductEventDto dto;
    private Account mappedAccount;
    private static final String CLIENT_ID = "card-111";
    private static final String PRODUCT_ID = "product-456";


    @BeforeEach
    void setUp() {
        dto = new ClientProductEventDto();
        dto.setClientId(CLIENT_ID);
        dto.setProductId(PRODUCT_ID);
        dto.setStatus(Status.CLOSED);

        mappedAccount = new Account();
        mappedAccount.setClientId(dto.getClientId());
        mappedAccount.setProductId(dto.getProductId());
    }

    @Test
    void givenValidDto_whenCreateAccount_thenAccountIsMappedSavedAndActive() {
        when(accountMapper.toEntity(dto)).thenReturn(mappedAccount);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            acc.setId(1L);
            return acc;
        });

        accountService.createAccount(dto);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountMapper, times(1)).toEntity(dto);
        verify(accountRepository, times(1)).save(accountCaptor.capture());

        Account saved = accountCaptor.getValue();
        assertEquals(Status.ACTIVE, saved.getStatus());
        assertEquals(CLIENT_ID, saved.getClientId());
        assertEquals(PRODUCT_ID, saved.getProductId());
    }
}
