package com.farmer.Form.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
 
@Service
public class CountryStateCityService {
    @Value("${countrystatecity.api.key}")
    private String API_KEY;
    @Value("${countrystatecity.api.url}")
    private String BASE_URL;
 
    private RestTemplate restTemplate = new RestTemplate();
 
    private HttpEntity<String> createHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-CSCAPI-KEY", API_KEY);
        return new HttpEntity<>(headers);
    }
 
    public String getCountries() {
        ResponseEntity<String> response = restTemplate.exchange(BASE_URL + "/countries",
                org.springframework.http.HttpMethod.GET, createHttpEntity(), String.class);
        return response.getBody();
    }
 
    public String getStates(String countryCode) {
        ResponseEntity<String> response = restTemplate.exchange(BASE_URL + "/countries/" + countryCode + "/states",
                org.springframework.http.HttpMethod.GET, createHttpEntity(), String.class);
        System.out.println(BASE_URL + "/countries/" + countryCode + "/states");
        System.out.println(response.getBody());
        return response.getBody();
    }
 
    public String getDistricts(String countryCode, String stateCode) {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL + "/countries/" + countryCode + "/states/" + stateCode + "/cities",
                org.springframework.http.HttpMethod.GET, createHttpEntity(), String.class);
        return response.getBody();
    }
}
