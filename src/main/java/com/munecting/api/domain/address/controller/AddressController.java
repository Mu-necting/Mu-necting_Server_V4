package com.munecting.api.domain.address.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.munecting.api.domain.comment.dto.CommentRequestDto;
import com.munecting.api.domain.address.service.AddressService;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/address")
@Tag(name = "address", description = "주소 관련 api")
public class AddressController {

    private final AddressService addressService;

    @GetMapping("")
    @Operation(summary = "위도/경도로 주소 조회하기")
    public ApiResponse<?> getAddressByLocation(
            @RequestParam(name = "latitude") Double latitude,
            @RequestParam(name = "longitude") Double longitude) {
        JsonNode address = addressService.getAddressByLocation(latitude, longitude);
        return ApiResponse.onSuccess(Status.OK.getCode(), Status.OK.getMessage(), address);
    }

}
