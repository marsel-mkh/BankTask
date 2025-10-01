package com.t1.marselmkh.service;

import com.t1.marselmkh.dto.ErrorLogEventDto;
import com.t1.marselmkh.entity.ErrorLog;
import com.t1.marselmkh.mapper.ErrorLogMapper;
import com.t1.marselmkh.repository.ErrorLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorLogPersistenceService {

    private final ErrorLogRepository errorLogRepository;
    private final ErrorLogMapper errorLogMapper;

    @Transactional
    public void saveToDb(ErrorLogEventDto errorLogEventDto, String payload, boolean flag) {
        ErrorLog errorLog = errorLogMapper.toEntity(errorLogEventDto);

        if(flag)
            errorLog.setPayload(payload);

        errorLogRepository.save(errorLog);
    }
}