package com.t1.marselmkh.mapper;

import com.t1.marselmkh.dto.TransactionDto;
import com.t1.marselmkh.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status",  ignore = true)
    @Mapping(target = "timestamp", source = "date")
    Transaction toEntity(TransactionDto dto);

}
