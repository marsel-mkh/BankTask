package com.t1.marselmkh;


import com.t1.marselmkh.repository.TransactionRepository;
import com.t1.marselmkh.service.FraudDetectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FraudDetectionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FraudDetectionService fraudDetectionService;

    private static final long CARD_ID = 123L;
    private static final long MAX_TRANSACTIONS = 5L;
    private static final long PERIOD_MINUTES = 60L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fraudDetectionService, "maxTransactions", MAX_TRANSACTIONS);
        ReflectionTestUtils.setField(fraudDetectionService, "periodMinutes", PERIOD_MINUTES);
    }

    @Test
    void givenTransactionsAboveLimit_whenCheckCard_thenSuspicious() {
        when(transactionRepository.countByCardIdAndTimestampBetween(anyLong(), any(), any()))
                .thenReturn(MAX_TRANSACTIONS + 1);

        boolean result = fraudDetectionService.isSuspicious(CARD_ID);

        assertTrue(result, "Карта должна считаться подозрительной при превышении лимита");
        verify(transactionRepository).countByCardIdAndTimestampBetween(eq(CARD_ID), any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void givenTransactionsWithinLimit_whenCheckCard_thenNotSuspicious() {
        when(transactionRepository.countByCardIdAndTimestampBetween(anyLong(), any(), any()))
                .thenReturn(MAX_TRANSACTIONS);

        boolean result = fraudDetectionService.isSuspicious(CARD_ID);

        assertFalse(result, "Карта не должна считаться подозрительной при нормальной активности");
        verify(transactionRepository).countByCardIdAndTimestampBetween(eq(CARD_ID), any(LocalDateTime.class), any(LocalDateTime.class));
    }
}