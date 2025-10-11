package com.t1.marselmkh.repository;

import com.t1.marselmkh.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByAccountIdAndPaymentDate(Long id, LocalDateTime date);

    List<Payment> findAllByAccountId(Long id);
}
