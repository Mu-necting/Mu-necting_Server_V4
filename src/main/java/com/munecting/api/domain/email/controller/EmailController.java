package com.munecting.api.domain.email.controller;

import com.munecting.api.domain.email.service.MailServiceImpl;
import com.munecting.api.global.common.dto.response.ApiResponse;
import com.munecting.api.global.common.dto.response.Status;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/api/mail")
public class EmailController {
    private final MailServiceImpl mailService;

    @PostMapping("")
    @Operation(summary = "이메일 코드 발송")
    public ApiResponse<?> mailConfirm(
            @RequestParam(name = "email") String email) throws Exception {
        String code = mailService.sendSimpleMessage(email);
        log.info("사용자에게 발송한 인증코드 ==> " + code);
        return ApiResponse.onSuccess(Status.OK.getCode(),
                Status.CREATED.getMessage(), code); //data 부분은 회의후 설정
    }

    @GetMapping("/verifications")
    @Operation(summary = "이메일 코드 검증")
    public ApiResponse<?> verificationEmail(
            @RequestParam("code") String code) {
        ResponseEntity<String> body = ResponseEntity.ok().body(mailService.verifyCode(code));
        return ApiResponse.onSuccess(Status.OK.getCode(),
                Status.CREATED.getMessage(), body);
    }
}
