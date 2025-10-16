package com.t1.marselmkh;

import com.t1.marselmkh.dto.PaymentDto;
import com.t1.marselmkh.entity.Account;
import com.t1.marselmkh.entity.Payment;
import com.t1.marselmkh.repository.AccountRepository;
import com.t1.marselmkh.repository.PaymentRepository;
import com.t1.marselmkh.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    private static final long ACCOUNT_ID = 1L;
    private static final BigDecimal BALANCE = new BigDecimal("1000.00");
    private static final BigDecimal PAYMENT_AMOUNT = new BigDecimal("300.00");

    private Account account;
    private PaymentDto paymentDto;
    private Payment unpaidPayment1;
    private Payment unpaidPayment2;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(ACCOUNT_ID);
        account.setBalance(BALANCE);
        account.setIsRecalc(true);

        paymentDto = new PaymentDto();
        paymentDto.setAccountId(ACCOUNT_ID);
        paymentDto.setAmount(PAYMENT_AMOUNT);

        unpaidPayment1 = new Payment();
        unpaidPayment1.setAmount(new BigDecimal("100.00"));
        unpaidPayment1.setPayedAt(null);

        unpaidPayment2 = new Payment();
        unpaidPayment2.setAmount(new BigDecimal("200.00"));
        unpaidPayment2.setPayedAt(null);
    }

    @Test
    void givenFullDebt_whenProcessPayment_thenBalanceUpdatedAndPaymentsMarkedAsPayed() {
        List<Payment> payments = List.of(unpaidPayment1, unpaidPayment2);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(java.util.Optional.of(account));
        when(paymentRepository.findAllByAccountId(ACCOUNT_ID)).thenReturn(payments);

        paymentService.processPayment(paymentDto);

        assertEquals(new BigDecimal("700.00"), account.getBalance());

        assertNotNull(unpaidPayment1.getPayedAt());
        assertNotNull(unpaidPayment2.getPayedAt());

        verify(accountRepository).save(account);

        ArgumentCaptor<List<Payment>> captor = ArgumentCaptor.forClass(List.class);
        verify(paymentRepository).saveAll(captor.capture());
        List<Payment> savedPayments = captor.getValue();
        assertTrue(savedPayments.containsAll(payments));
    }

    @Test
    void givenPartialPayment_whenProcessPayment_thenNoChanges() {
        List<Payment> payments = List.of(unpaidPayment1, unpaidPayment2);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(java.util.Optional.of(account));
        when(paymentRepository.findAllByAccountId(ACCOUNT_ID)).thenReturn(payments);

        PaymentDto partialPaymentDto = new PaymentDto();
        partialPaymentDto.setAccountId(ACCOUNT_ID);
        partialPaymentDto.setAmount(new BigDecimal("50.00"));

        paymentService.processPayment(partialPaymentDto);

        assertEquals(BALANCE, account.getBalance());
        assertNull(unpaidPayment1.getPayedAt());
        assertNull(unpaidPayment2.getPayedAt());

        verify(accountRepository, never()).save(account);
        verify(paymentRepository, never()).saveAll(anyList());
    }

    @Test
    void givenNonRecalcAccount_whenProcessPayment_thenSkipProcessing() {
        account.setIsRecalc(false);
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(java.util.Optional.of(account));

        paymentService.processPayment(paymentDto);

        assertEquals(BALANCE, account.getBalance());
        verify(accountRepository, never()).save(any());
        verify(paymentRepository, never()).saveAll(anyList());
    }
}
