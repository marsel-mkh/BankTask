package com.t1.marselmkh.repository;

import com.t1.marselmkh.entity.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction,Long> {
    int countByCardIdAndTimestampBetween(Long cardId, LocalDateTime from, LocalDateTime now);
}
