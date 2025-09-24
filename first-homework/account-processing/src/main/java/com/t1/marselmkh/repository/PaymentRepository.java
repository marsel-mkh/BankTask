package com.t1.marselmkh.repository;

import com.t1.marselmkh.entity.Payment;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByAccountIdAndPaymentDate(Long id, @NotNull(message = "date is required") LocalDateTime date);

    List<Payment> findAllByAccountId(Long id);
}
