package com.t1.marselmkh.service;

import com.t1.marselmkh.dto.TransactionDto;
import com.t1.marselmkh.entity.Account;
import com.t1.marselmkh.entity.Payment;
import com.t1.marselmkh.entity.PaymentType;
import com.t1.marselmkh.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentScheduleService {
    private final PaymentRepository paymentRepository;


    // минимальный платёж (10% от задолженности)
    @Value("${payment_schedule_service.min_payment_rate}")
    private double minPaymentRate;

    /**
     * Создаёт график платежей для кредитного счёта.
     */
    public void generateSchedule(Account account, TransactionDto dto) {

        BigDecimal interest = account.getBalance()
                .multiply(BigDecimal.valueOf(account.getInterestRate()))
                .divide(BigDecimal.valueOf(12), RoundingMode.HALF_UP);

        BigDecimal minPayment = account.getBalance()
                .multiply(BigDecimal.valueOf(minPaymentRate))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = minPayment.add(interest);

        Payment payment = new Payment();
        payment.setAccountId(account.getId());
        payment.setPaymentDate(dto.getDate().plusMonths(1).toLocalDate().atStartOfDay());
        payment.setAmount(total);
        payment.setIsCredit(true);
        payment.setType(PaymentType.MONTHLY);


        paymentRepository.save(payment);
    }

    /**
     * Пытается списать платеж в день X.
     */
    public void tryProcessPayment(Account account, TransactionDto dto) {
        List<Payment> duePayments = paymentRepository
                .findAllByAccountIdAndPaymentDate(account.getId(), dto.getDate());

        for (Payment p : duePayments) {
            if (account.getBalance().compareTo(p.getAmount()) >= 0) {
                account.setBalance(account.getBalance().subtract(p.getAmount()));
                p.setPayedAt(LocalDateTime.now());
            } else {
                p.setExpired(true);
            }
            paymentRepository.save(p);
        }
    }
}
