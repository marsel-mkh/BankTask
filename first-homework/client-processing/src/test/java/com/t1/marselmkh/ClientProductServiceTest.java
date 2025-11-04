package com.t1.marselmkh;

import com.t1.marselmkh.dto.ClientProductDto.ClientProductCreateDto;
import com.t1.marselmkh.dto.ClientProductDto.ClientProductEventDto;
import com.t1.marselmkh.dto.ClientProductDto.ClientProductUpdateDto;
import com.t1.marselmkh.dto.ClientProductDto.ClientProductViewDto;
import com.t1.marselmkh.entity.ClientProduct;
import com.t1.marselmkh.entity.Product;
import com.t1.marselmkh.entity.ProductKey;
import com.t1.marselmkh.entity.Status;
import com.t1.marselmkh.mapper.ClientProductMapper;
import com.t1.marselmkh.repository.ClientProductRepository;
import com.t1.marselmkh.repository.ProductRepository;
import com.t1.marselmkh.service.ClientProductProducer;
import com.t1.marselmkh.service.ClientProductService;
import com.t1.marselmkh.validation.ClientProductValidation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientProductServiceTest {

    @InjectMocks
    private ClientProductService clientProductService;

    @Mock
    private ClientProductRepository clientProductRepository;

    @Mock
    private ClientProductMapper clientProductMapper;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ClientProductProducer clientProductProducer;

    @Mock
    private ClientProductValidation clientProductValidation;

    private final String clientProductTopic = "client-products-topic";
    private final String clientCreditProductTopic = "client-credit-products-topic";

    private final Long clientProductId = 1L;
    private final String clientId = "client-123";
    private final String productId = "product-456";

    private ClientProduct clientProduct;
    private ClientProductCreateDto createDto;
    private ClientProductUpdateDto updateDto;
    private ClientProductEventDto eventDto;
    private ClientProductViewDto viewDto;
    private Product product;

    @BeforeEach
    void setUp() {
        clientProduct = new ClientProduct();
        clientProduct.setId(clientProductId);
        clientProduct.setClientId(clientId);
        clientProduct.setProductId(productId);
        clientProduct.setStatus(Status.ACTIVE);

        createDto = new ClientProductCreateDto();
        createDto.setClientId(clientId);
        createDto.setProductId(productId);

        updateDto = new ClientProductUpdateDto();
        updateDto.setStatus(Status.CLOSED);
        updateDto.setCloseDate(LocalDate.now());

        eventDto = new ClientProductEventDto();
        eventDto.setClientId(clientId);
        eventDto.setProductId(productId);

        viewDto = new ClientProductViewDto();
        viewDto.setClientId(clientId);
        viewDto.setProductId(productId);

        product = new Product();
        product.setProductId(productId);
        product.setKey(ProductKey.DC);

        ReflectionTestUtils.setField(clientProductService, "clientProductTopic", clientProductTopic);
        ReflectionTestUtils.setField(clientProductService, "clientCreditProductTopic", clientCreditProductTopic);
    }

    @Test
    void givenValidCreateDto_whenCreate_thenClientProductIsSavedAndKafkaSent() {
        when(clientProductMapper.toEntity(createDto)).thenReturn(clientProduct);
        when(clientProductMapper.createToEventDto(createDto)).thenReturn(eventDto);
        when(clientProductMapper.toDto(clientProduct)).thenReturn(viewDto);
        when(productRepository.findByProductId(productId)).thenReturn(Optional.of(product));

        ClientProductViewDto result = clientProductService.create(createDto);

        verify(clientProductValidation).validateClientProduct(clientId, productId);
        verify(clientProductRepository).save(clientProduct);
        verify(clientProductProducer).send(clientProductTopic, eventDto);
        assertThat(result).isEqualTo(viewDto);
    }

    @Test
    void givenExistingId_whenGet_thenReturnViewDto() {
        when(clientProductRepository.findById(clientProductId)).thenReturn(Optional.of(clientProduct));
        when(clientProductMapper.toDto(clientProduct)).thenReturn(viewDto);

        ClientProductViewDto result = clientProductService.get(clientProductId);

        verify(clientProductRepository).findById(clientProductId);
        assertThat(result).isEqualTo(viewDto);
    }

    @Test
    void givenValidUpdateDto_whenUpdate_thenEntityUpdatedAndKafkaSent() {
        when(clientProductRepository.findById(clientProductId)).thenReturn(Optional.of(clientProduct));
        when(clientProductMapper.toDto(clientProduct)).thenReturn(viewDto);
        when(clientProductMapper.toEventDto(clientProduct)).thenReturn(eventDto);
        when(productRepository.findByProductId(productId)).thenReturn(Optional.of(product));

        ClientProductViewDto result = clientProductService.update(clientProductId, updateDto);

        verify(clientProductRepository).save(clientProduct);
        verify(clientProductProducer).send(clientProductTopic, eventDto);
        assertThat(clientProduct.getStatus()).isEqualTo(Status.CLOSED);
        assertThat(result).isEqualTo(viewDto);
    }

    @Test
    void givenExistingId_whenDelete_thenEntityDeletedAndKafkaSent() {
        when(clientProductRepository.findById(clientProductId)).thenReturn(Optional.of(clientProduct));
        when(clientProductMapper.toEventDto(clientProduct)).thenReturn(eventDto);
        when(productRepository.findByProductId(productId)).thenReturn(Optional.of(product));

        clientProductService.delete(clientProductId);

        verify(clientProductRepository).deleteById(clientProductId);
        verify(clientProductProducer).send(clientProductTopic, eventDto);
        assertThat(clientProduct.getStatus()).isEqualTo(Status.CLOSED);
    }

    @Test
    void givenCreditProductKey_whenSendToKafka_thenSendToCreditTopic() {
        product.setKey(ProductKey.PC);
        when(productRepository.findByProductId(productId)).thenReturn(Optional.of(product));

        Assertions.assertDoesNotThrow(() -> {
            var method = ClientProductService.class.getDeclaredMethod("sendToKafka", ClientProductEventDto.class);
            method.setAccessible(true);
            method.invoke(clientProductService, eventDto);
        });

        verify(clientProductProducer).send(clientCreditProductTopic, eventDto);
    }

}
