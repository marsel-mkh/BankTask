package com.t1.marselmkh;

import com.t1.marselmkh.dto.TransactionDto;
import com.t1.marselmkh.entity.*;
import com.t1.marselmkh.mapper.TransactionalMapper;
import com.t1.marselmkh.repository.AccountRepository;
import com.t1.marselmkh.repository.TransactionRepository;
import com.t1.marselmkh.service.FraudDetectionService;
import com.t1.marselmkh.service.PaymentScheduleService;
import com.t1.marselmkh.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PaymentScheduleService paymentScheduleService;

    @Mock
    private FraudDetectionService fraudDetectionService;

    @Mock
    private TransactionalMapper transactionMapper;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private static final long ACCOUNT_ID = 1L;
    private static final long CARD_ID = 100L;
    private static final BigDecimal AMOUNT = new BigDecimal("500.00");

    private Account account;
    private TransactionDto transactionDto;
    private Transaction transactionEntity;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(ACCOUNT_ID);
        account.setBalance(new BigDecimal("1000.00"));
        account.setStatus(Status.ACTIVE);
        account.setIsRecalc(true);

        transactionDto = new TransactionDto();
        transactionDto.setAccountId(ACCOUNT_ID);
        transactionDto.setCardId(CARD_ID);
        transactionDto.setAmount(AMOUNT);
        transactionDto.setType(TransactionType.CREDIT);

        transactionEntity = new Transaction();
    }

    @Test
    void givenCreditTransaction_whenProcessTransaction_thenBalanceIncreasedAndTransactionSaved() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(java.util.Optional.of(account));
        when(transactionMapper.toEntity(transactionDto)).thenReturn(transactionEntity);
        when(fraudDetectionService.isSuspicious(CARD_ID)).thenReturn(false);

        transactionService.processTransaction(transactionDto);

        assertEquals(new BigDecimal("1500.00"), account.getBalance());
        assertEquals(TransactionStatus.COMPLETE, transactionEntity.getStatus());

        verify(paymentScheduleService).generateSchedule(account, transactionDto);
        verify(paymentScheduleService).tryProcessPayment(account, transactionDto);
        verify(transactionRepository).save(transactionEntity);
        verify(accountRepository).save(account);
    }

    @Test
    void givenDebitTransaction_whenProcessTransaction_thenBalanceDecreased() {
        transactionDto.setType(TransactionType.DEBIT);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(java.util.Optional.of(account));
        when(transactionMapper.toEntity(transactionDto)).thenReturn(transactionEntity);
        when(fraudDetectionService.isSuspicious(CARD_ID)).thenReturn(false);

        transactionService.processTransaction(transactionDto);

        assertEquals(new BigDecimal("500.00"), account.getBalance());

        verify(paymentScheduleService).generateSchedule(account, transactionDto);
        verify(paymentScheduleService, never()).tryProcessPayment(any(), any());
        verify(transactionRepository).save(transactionEntity);
        verify(accountRepository).save(account);
    }

    @Test
    void givenSuspiciousCard_whenProcessTransaction_thenAccountBlocked() {
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(java.util.Optional.of(account));
        when(transactionMapper.toEntity(transactionDto)).thenReturn(transactionEntity);
        when(fraudDetectionService.isSuspicious(CARD_ID)).thenReturn(true);

        transactionService.processTransaction(transactionDto);

        assertEquals(Status.BLOCKED, account.getStatus());
        verify(transactionRepository).save(transactionEntity);
        verify(accountRepository).save(account);
    }

    @Test
    void givenBlockedAccount_whenProcessTransaction_thenTransactionIgnored() {
        account.setStatus(Status.BLOCKED);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(java.util.Optional.of(account));

        transactionService.processTransaction(transactionDto);

        assertEquals(Status.BLOCKED, account.getStatus());
        verify(transactionRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
        verify(paymentScheduleService, never()).generateSchedule(any(), any());
    }

    @Test
    void givenArrestedAccount_whenProcessTransaction_thenTransactionIgnored() {
        account.setStatus(Status.ARRESTED);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(java.util.Optional.of(account));

        transactionService.processTransaction(transactionDto);

        assertEquals(Status.ARRESTED, account.getStatus());
        verify(transactionRepository, never()).save(any());
        verify(accountRepository, never()).save(any());
        verify(paymentScheduleService, never()).generateSchedule(any(), any());
    }
}