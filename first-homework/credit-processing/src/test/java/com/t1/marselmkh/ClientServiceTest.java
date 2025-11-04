package com.t1.marselmkh;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.t1.marselmkh.dto.ClientInfoDto;
import com.t1.marselmkh.dto.ClientProductDto.ClientProductEventDto;
import com.t1.marselmkh.exception.ClientNotFoundException;
import com.t1.marselmkh.repository.PaymentRegistryRepository;
import com.t1.marselmkh.repository.ProductRegistryRepository;
import com.t1.marselmkh.service.ClientService;
import com.t1.marselmkh.service.TokenBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@WireMockTest(httpPort = 8084)
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        LiquibaseAutoConfiguration.class})
public class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @MockitoBean
    private TokenBuilder tokenBuilder;

    @MockitoBean
    private ProductRegistryRepository productRegistryRepository;

    @MockitoBean
    private PaymentRegistryRepository paymentRegistryRepository;

    private static final String CLIENT_ID = "123";

    @BeforeEach
    void setup() {
        when(tokenBuilder.generateServiceToken(anyString())).thenReturn("test-token");
    }

    @Test
    void givenExistingClient_whenGetClient_thenReturnClientInfo() {
        String clientJson = """
                {
                    "email": "john.doe@example.com",
                    "firstName": "John",
                    "middleName": "M",
                    "lastName": "Doe",
                    "dateOfBirth": "1990-05-20",
                    "documentType": "PASSPORT",
                    "documentId": "A1234567",
                    "documentPrefix": "AB",
                    "documentSuffix": "01"
                }
                """;

        stubFor(get(urlEqualTo("/clients/" + CLIENT_ID))
                .withHeader("Authorization", equalTo("Bearer test-token"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(clientJson)
                        .withStatus(200)));

        ClientProductEventDto eventDto = new ClientProductEventDto();
        eventDto.setClientId(CLIENT_ID);

        ClientInfoDto client = clientService.getClient(eventDto);

        assertNotNull(client);
        assertEquals("john.doe@example.com", client.getEmail());
        assertEquals("John", client.getFirstName());
        assertEquals("M", client.getMiddleName());
        assertEquals("Doe", client.getLastName());
        assertEquals(LocalDate.parse("1990-05-20"), client.getDateOfBirth());
        assertEquals("PASSPORT", client.getDocumentType().name());
        assertEquals("A1234567", client.getDocumentId());
        assertEquals("AB", client.getDocumentPrefix());
        assertEquals("01", client.getDocumentSuffix());

        verify(getRequestedFor(urlEqualTo("/clients/" + CLIENT_ID))
                .withHeader("Authorization", equalTo("Bearer test-token")));
    }

    @Test
    void givenNonExistingClient_whenGetClient_thenThrowClientNotFoundException() {
        stubFor(get(urlEqualTo("/clients/" + CLIENT_ID))
                .withHeader("Authorization", equalTo("Bearer test-token"))
                .willReturn(aResponse().withStatus(404)));

        ClientProductEventDto eventDto = new ClientProductEventDto();
        eventDto.setClientId(CLIENT_ID);

        assertThrows(ClientNotFoundException.class, () -> clientService.getClient(eventDto));
    }
}
