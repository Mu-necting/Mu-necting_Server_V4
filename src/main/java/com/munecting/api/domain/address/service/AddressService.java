package com.munecting.api.domain.address.service;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    @Value("${KAKAO_MAP_API_KEY}")
    private String apiKey;

    private final String BASE_URL = "https://dapi.kakao.com/v2/local/geo/coord2address";
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonNode getAddressByLocation(Double latitude, Double longitude) {
        String requestUrl = BASE_URL + "?x=" + longitude + "&y=" + latitude;
        try {
            String auth = "KakaoAK " + apiKey;
            JsonNode address = getJSONData(requestUrl, auth);
            return address;
        }catch (Exception e) {
            log.warn("Kakao Map Api request Fail");
            e.printStackTrace();
        }
        return null;
    }

    private JsonNode getJSONData(String requestUrl, String token) throws JsonProcessingException {
        ResponseEntity<String> response = restClient.get()
                .uri(requestUrl)
                .header("Authorization", token)
                .retrieve()
                .toEntity(String.class);

        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode;

    }
}
