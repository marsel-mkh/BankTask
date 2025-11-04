package com.t1.marselmkh.service;

import com.t1.marselmkh.dto.ClientInfoDto;
import com.t1.marselmkh.dto.ClientProductDto.ClientProductEventDto;
import com.t1.marselmkh.exception.ClientNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ClientService {

    @Value("${ms1.getUrl}")
    private String ms1GetUrl;

    @Value("${spring.application.name}")
    private String msName;

    private final RestTemplate restTemplate;
    private final TokenBuilder tokenBuilder;

    public ClientInfoDto getClient(ClientProductEventDto clientProductEventDto) {
        String token = tokenBuilder.generateServiceToken(msName);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url = ms1GetUrl + clientProductEventDto.getClientId();
        try {
            ResponseEntity<ClientInfoDto> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ClientInfoDto.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.NotFound e) {
            throw new ClientNotFoundException("Client not found with id: " + clientProductEventDto.getClientId());
        }
    }
}
