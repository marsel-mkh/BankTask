package com.t1.marselmkh;

import com.t1.marselmkh.dto.ClientProductDto.ClientProductEventDto;
import com.t1.marselmkh.dto.ClientInfoDto;
import com.t1.marselmkh.entity.PaymentRegistry;
import com.t1.marselmkh.entity.ProductRegistry;
import com.t1.marselmkh.exception.ClientCreditIsExpiredException;
import com.t1.marselmkh.exception.ClientNotFoundException;
import com.t1.marselmkh.exception.CreditLimitedException;
import com.t1.marselmkh.mapper.ProductRegistryMapper;
import com.t1.marselmkh.repository.PaymentRegistryRepository;
import com.t1.marselmkh.repository.ProductRegistryRepository;
import com.t1.marselmkh.service.ClientService;
import com.t1.marselmkh.service.CreditProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreditProcessingServiceTest {

    @Mock
    private ProductRegistryRepository productRegistryRepository;

    @Mock
    private PaymentRegistryRepository paymentRegistryRepository;

    @Mock
    private ProductRegistryMapper productRegistryMapper;

    @Mock
    private ClientService clientService;

    @InjectMocks
    private CreditProcessingService creditProcessingService;

    @Captor
    private ArgumentCaptor<List<PaymentRegistry>> paymentListCaptor;

    private ClientProductEventDto clientProductEventDto;
    private ClientInfoDto clientInfoDto;
    private ProductRegistry productRegistry;

    private static final String CLIENT_ID = "client-1";
    private static final String PRODUCT_ID = "product-1";
    private static final long PRODUCT_REGISTRY_ID = 10L;
    private static final BigDecimal LOAN_AMOUNT = new BigDecimal("1000.00");
    private static final Double INTEREST_RATE = 12.0;
    private static final Integer MONTHS = 12;
    private static final BigDecimal CREDIT_LIMIT = new BigDecimal("5000.00");

    @BeforeEach
    void setUp() {
        clientProductEventDto = new ClientProductEventDto();
        clientProductEventDto.setClientId(CLIENT_ID);
        clientProductEventDto.setProductId(PRODUCT_ID);
        clientProductEventDto.setLoanAmount(LOAN_AMOUNT);
        clientProductEventDto.setInterestRate(INTEREST_RATE);
        clientProductEventDto.setMonthCount(MONTHS);

        clientInfoDto = new ClientInfoDto();
        clientInfoDto.setDocumentId("12345");

        productRegistry = new ProductRegistry();
        productRegistry.setId(PRODUCT_REGISTRY_ID);
        productRegistry.setClientId(CLIENT_ID);
        productRegistry.setProductId(PRODUCT_ID);
        productRegistry.setMonthCount(MONTHS);
        productRegistry.setInterestRate(INTEREST_RATE);

        ReflectionTestUtils.setField(creditProcessingService, "creditLimit", CREDIT_LIMIT);
    }

    @Test
    void givenValidClientAndCredit_whenProcessCredit_thenProductAndPaymentsSaved() {
        when(clientService.getClient(clientProductEventDto)).thenReturn(clientInfoDto);
        when(productRegistryMapper.toEntity(any(ClientProductEventDto.class))).thenReturn(productRegistry);
        when(productRegistryRepository.save(any(ProductRegistry.class))).thenReturn(productRegistry);
        when(productRegistryRepository.findAllByClientId(anyString())).thenReturn(List.of());
        when(paymentRegistryRepository.existsOverduePayments(anyList(), any())).thenReturn(false);

        creditProcessingService.processCredit(clientProductEventDto);

        verify(clientService).getClient(clientProductEventDto);
        verify(productRegistryRepository).save(productRegistry);
        verify(paymentRegistryRepository).saveAll(paymentListCaptor.capture());

        List<PaymentRegistry> payments = paymentListCaptor.getValue();
        assertEquals(MONTHS, payments.size(), "Должно быть создано платежей столько, сколько месяцев");

        payments.forEach(p -> {
            assertEquals(PRODUCT_REGISTRY_ID, p.getProductRegistryId());
            assertFalse(p.getExpired());
            assertNotNull(p.getPaymendDate());
            assertNotNull(p.getAmount());
            assertNotNull(p.getDebtAmount());
            assertNotNull(p.getInterestRateAmount());
        });
    }

    @Test
    void givenExceedingCreditLimit_whenProcessCredit_thenThrowCreditLimitedException() {
        when(clientService.getClient(clientProductEventDto)).thenReturn(clientInfoDto);
        when(productRegistryRepository.findAllByClientId(CLIENT_ID)).thenReturn(List.of(productRegistry));
        when(paymentRegistryRepository.sumOutstandingByProductIds(anyList())).thenReturn(new BigDecimal("4500.00"));

        assertThrows(CreditLimitedException.class,
                () -> creditProcessingService.processCredit(clientProductEventDto));

        verify(clientService).getClient(clientProductEventDto);
    }

    @Test
    void givenOverduePayment_whenProcessCredit_thenThrowClientCreditIsExpiredException() {
        when(clientService.getClient(clientProductEventDto)).thenReturn(clientInfoDto);
        when(productRegistryRepository.findAllByClientId(CLIENT_ID)).thenReturn(List.of(productRegistry));
        when(paymentRegistryRepository.sumOutstandingByProductIds(anyList())).thenReturn(BigDecimal.ZERO);
        when(paymentRegistryRepository.existsOverduePayments(anyList(), any())).thenReturn(true);

        assertThrows(ClientCreditIsExpiredException.class,
                () -> creditProcessingService.processCredit(clientProductEventDto));

        verify(clientService).getClient(clientProductEventDto);
    }

    @Test
    void givenClientNotFound_whenProcessCredit_thenThrowClientNotFoundException() {
        when(clientService.getClient(clientProductEventDto)).thenThrow(
                new ClientNotFoundException("Client not found with id: " + CLIENT_ID));

        assertThrows(ClientNotFoundException.class,
                () -> creditProcessingService.processCredit(clientProductEventDto));

        verify(clientService).getClient(clientProductEventDto);
    }
}
