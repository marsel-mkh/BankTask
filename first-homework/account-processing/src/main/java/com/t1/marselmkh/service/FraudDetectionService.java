package com.t1.marselmkh.service;

import com.t1.marselmkh.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {
    private final TransactionRepository transactionRepository;

    @Value("${fraud_detection.max_transactions}")
    private long maxTransactions;

    @Value("${fraud_detection.period}")
    private long periodMinutes;


    /**
     * Проверяет карту на подозрительную активность.
     * @param cardId ID карты
     * @return true, если подозрительно
     */
    public boolean isSuspicious(Long cardId) {
        Duration period = Duration.ofMinutes(periodMinutes);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.minus(period);

        long count = transactionRepository.countByCardIdAndTimestampBetween(cardId, from, now);

        return count > maxTransactions;
    }
}
