package com.t1.marselmkh.service;

import com.t1.marselmkh.dto.PaymentDto;
import com.t1.marselmkh.entity.Account;
import com.t1.marselmkh.entity.Payment;
import com.t1.marselmkh.entity.PaymentType;
import com.t1.marselmkh.exception.AccountNotFoundException;
import com.t1.marselmkh.repository.AccountRepository;
import com.t1.marselmkh.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final AccountRepository accountRepository;
    private final PaymentRepository paymentRepository;

    /**
     * Обрабатывает платеж по кредитному счёту.
     * Если сумма платежа = сумме задолженности, пересчитывает баланс,
     * обновляет записи Payment и фиксирует погашение.
     *
     * @param dto DTO платежа
     */
    @Transactional
    public void processPayment(PaymentDto dto) {
        Account account = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + dto.getAccountId()));

        if (!Boolean.TRUE.equals(account.getIsRecalc())) {
            log.info("Account {} is not credit/recalculable, skipping payment", account.getId());
            return;
        }
        List<Payment> payments = paymentRepository.findAllByAccountId(account.getId());

        BigDecimal totalDebt = payments.stream()
                .filter(p -> p.getPayedAt() == null)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (dto.getAmount().compareTo(totalDebt) == 0) {
            account.setBalance(account.getBalance().subtract(totalDebt));
            accountRepository.save(account);
            LocalDateTime now = LocalDateTime.now();

            // Создаём новый Payment, фиксирующий погашение
            Payment payment = new Payment();
            payment.setAccountId(account.getId());
            payment.setAmount(dto.getAmount());
            payment.setIsCredit(true);
            payment.setType(PaymentType.FULL_REPAYMENT);
            payment.setPaymentDate(now);
            payment.setPayedAt(LocalDateTime.now());

            for (Payment p : payments) {
                if (p.getPayedAt() == null) {
                    p.setPayedAt(now);
                }
            }
            paymentRepository.saveAll(payments);
            log.info("Payment {} applied for account {}", dto.getAmount(), account.getId());
        } else {
            log.warn("Payment amount {} does not match total debt {} for account {}, skipping",
                    dto.getAmount(), totalDebt, account.getId());
        }
    }
}
