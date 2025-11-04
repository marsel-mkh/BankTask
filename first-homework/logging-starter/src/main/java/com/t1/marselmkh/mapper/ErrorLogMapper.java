package com.t1.marselmkh.mapper;

import com.t1.marselmkh.dto.BaseLogEvent;
import com.t1.marselmkh.entity.ErrorLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ErrorLogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "payload", ignore = true)
    ErrorLog toEntity(BaseLogEvent dto);
}