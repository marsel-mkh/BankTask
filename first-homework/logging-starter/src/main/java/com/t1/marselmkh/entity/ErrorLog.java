package com.t1.marselmkh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "error_log")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErrorLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceName;
    private OffsetDateTime timestamp;

    @Column(name = "method_signature", length = 2000)
    private String methodSignature;

    @Lob
    @Column(name = "stack_trace")
    private String stackTrace;

    @Column(name = "exception_message", length = 2000)
    private String exceptionMessage;

    @Lob
    @Column(name = "payload")
    private String payload;
}
