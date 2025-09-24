package com.t1.marselmkh.service;

import com.t1.marselmkh.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {
    private final TransactionRepository transactionRepository;

    private static final int MAX_TRANSACTIONS = 5;
    private static final Duration PERIOD = Duration.ofMinutes(10);


    /**
     * Проверяет карту на подозрительную активность.
     * @param cardId ID карты
     * @return true, если подозрительно
     */
    public boolean isSuspicious(Long cardId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minus(PERIOD);

        int count = transactionRepository.countByCardIdAndTimestampBetween(cardId, from, now);

        return count > MAX_TRANSACTIONS;
    }
}
