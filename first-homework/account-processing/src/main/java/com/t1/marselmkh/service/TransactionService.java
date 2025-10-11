package com.t1.marselmkh.service;

import com.t1.marselmkh.dto.TransactionDto;
import com.t1.marselmkh.entity.*;
import com.t1.marselmkh.exception.AccountNotFoundException;
import com.t1.marselmkh.mapper.TransactionalMapper;
import com.t1.marselmkh.repository.AccountRepository;
import com.t1.marselmkh.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final AccountRepository accountRepository;
    private final PaymentScheduleService paymentScheduleService;
    private final FraudDetectionService fraudDetectionService;
    private final TransactionalMapper transactionMapper;
    private final TransactionRepository transactionRepository;

    /**
     * Обрабатывает транзакцию по аккаунту.
     * <p>
     * Логика метода:
     * <ul>
     *     <li>Проверяет статус аккаунта: если {@link Status#BLOCKED} или {@link Status#ARRESTED}, транзакция игнорируется.</li>
     *     <li>В зависимости от типа транзакции ({@link TransactionType#CREDIT} или {@link TransactionType#DEBIT}) изменяет баланс аккаунта.</li>
     *     <li>Если аккаунт кредитный и пересчитываемый ({@link Account#getIsRecalc()}), создаёт график платежей через {@link PaymentScheduleService#generateSchedule}.</li>
     *     <li>Проверяет карту на подозрительную активность через {@link FraudDetectionService#isSuspicious}; при подозрении блокирует аккаунт.</li>
     *     <li>Если транзакция кредитная и пересчитываемая, пытается списать минимальный платёж через {@link PaymentScheduleService#tryProcessPayment}.</li>
     * </ul>
     * <p>
     *
     * @param dto DTO транзакции с данными для обработки
     * @throws AccountNotFoundException если аккаунт с указанным ID не найден
     */
    @Transactional
    public void processTransaction(TransactionDto dto) {

        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + dto.getAccountId()));

        if (account.getStatus() == Status.BLOCKED || account.getStatus() == Status.ARRESTED) {
            log.warn("Transaction is blocked due to account status {}", account.getStatus());
            return;
        }

        if (dto.getType() == TransactionType.CREDIT) {
            account.setBalance(account.getBalance().add(dto.getAmount()));
        } else if (dto.getType() == TransactionType.DEBIT) {
            account.setBalance(account.getBalance().subtract(dto.getAmount()));
        }

        if (Boolean.TRUE.equals(account.getIsRecalc())) {
            paymentScheduleService.generateSchedule(account, dto);
        }

        if (fraudDetectionService.isSuspicious(dto.getCardId())) {
            account.setStatus(Status.BLOCKED);
            log.warn("Обнаружена подозрительная активность по карте {} → счёт {} заблокирован",
                    dto.getCardId(), account.getId());
        }

        if (dto.getType() == TransactionType.CREDIT && Boolean.TRUE.equals(account.getIsRecalc())) {
            paymentScheduleService.tryProcessPayment(account, dto);
        }

        Transaction transaction = transactionMapper.toEntity(dto);
        transaction.setStatus(TransactionStatus.COMPLETE);
        transactionRepository.save(transaction);
        accountRepository.save(account);
    }
}
