package com.t1.marselmkh;

import com.t1.marselmkh.dto.TransactionDto;
import com.t1.marselmkh.entity.Account;
import com.t1.marselmkh.entity.Payment;
import com.t1.marselmkh.entity.PaymentType;
import com.t1.marselmkh.repository.PaymentRepository;
import com.t1.marselmkh.service.PaymentScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentScheduleServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentScheduleService paymentScheduleService;

    private static final long ACCOUNT_ID = 1L;
    private static final BigDecimal BALANCE = new BigDecimal("1000.00");
    private static final double INTEREST_RATE = 0.12; // 12% годовых
    private static final double MIN_PAYMENT_RATE = 0.1; // 10%
    private static final LocalDateTime TRANSACTION_DATE = LocalDateTime.of(2025, 10, 15, 12, 0);

    private Account account;
    private TransactionDto transactionDto;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(ACCOUNT_ID);
        account.setBalance(BALANCE);
        account.setInterestRate(INTEREST_RATE);

        transactionDto = new TransactionDto();
        transactionDto.setDate(TRANSACTION_DATE);

        ReflectionTestUtils.setField(paymentScheduleService, "minPaymentRate", MIN_PAYMENT_RATE);
    }

    @Test
    void givenAccount_whenGenerateSchedule_thenPaymentSavedCorrectly() {
        paymentScheduleService.generateSchedule(account, transactionDto);

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(captor.capture());

        Payment savedPayment = captor.getValue();

        BigDecimal expectedInterest = BALANCE
                .multiply(BigDecimal.valueOf(INTEREST_RATE)).divide(BigDecimal.valueOf(12), BigDecimal.ROUND_HALF_UP);

        BigDecimal expectedMinPayment = BALANCE
                .multiply(BigDecimal.valueOf(MIN_PAYMENT_RATE)).setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal expectedTotal = expectedInterest.add(expectedMinPayment);

        assertEquals(ACCOUNT_ID, savedPayment.getAccountId());
        assertEquals(expectedTotal, savedPayment.getAmount());
        assertTrue(savedPayment.getIsCredit());
        assertEquals(PaymentType.MONTHLY, savedPayment.getType());
        assertEquals(transactionDto.getDate().plusMonths(1).toLocalDate().atStartOfDay(), savedPayment.getPaymentDate());
    }

    @Test
    void givenSufficientBalance_whenTryProcessPayment_thenMarkedAsPayedAndBalanceReduced() {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("100.00"));
        payment.setAccountId(ACCOUNT_ID);
        payment.setPaymentDate(TRANSACTION_DATE);

        when(paymentRepository.findAllByAccountIdAndPaymentDate(ACCOUNT_ID, TRANSACTION_DATE))
                .thenReturn(List.of(payment));

        paymentScheduleService.tryProcessPayment(account, transactionDto);

        assertNotNull(payment.getPayedAt());
        assertEquals(new BigDecimal("900.00"), account.getBalance());

        verify(paymentRepository).save(payment);
    }

    @Test
    void givenInsufficientBalance_whenTryProcessPayment_thenMarkedAsExpired() {
        account.setBalance(new BigDecimal("50.00"));

        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("100.00"));
        payment.setAccountId(ACCOUNT_ID);
        payment.setPaymentDate(TRANSACTION_DATE);

        when(paymentRepository.findAllByAccountIdAndPaymentDate(ACCOUNT_ID, TRANSACTION_DATE))
                .thenReturn(List.of(payment));

        paymentScheduleService.tryProcessPayment(account, transactionDto);

        assertTrue(payment.getExpired());
        assertNull(payment.getPayedAt());

        verify(paymentRepository).save(payment);
    }
}