package org.sytac.cucumber.example.util;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Component
public class RestClient {

    private final RestTemplate restTemplate;

    public RestClient() {
        this.restTemplate = new RestTemplate();
    }

    public <E> ResponseEntity<E> post(final String url, final E payload, Class<E> clazz){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<E> entity = new HttpEntity(payload, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, clazz);
    }

    public <E> ResponseEntity<E> get(final String url, final Map<String,String> params, Class<E> clazz){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        for(Map.Entry<String,String> entry : params.entrySet()){
            builder = builder.queryParam(entry.getKey(), entry.getValue());
        }

        ResponseEntity<E> response = restTemplate.exchange(
                builder.build().encode().toUri(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                clazz
        );

        return response;
    }

}
