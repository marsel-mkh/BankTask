package com.t1.marselmkh.repository;

import com.t1.marselmkh.entity.ErrorLog;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorLogRepository  extends CrudRepository<ErrorLog, String> {
}
