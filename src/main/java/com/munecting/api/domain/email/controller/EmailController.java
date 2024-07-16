package com.munecting.api.domain.email.controller;

import com.munecting.api.domain.email.service.MailServiceImpl;
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
    public void mailConfirm(@RequestParam(name = "email") String email) throws Exception {
        String code = mailService.sendSimpleMessage(email);
        log.info("사용자에게 발송한 인증코드 ==> " + code);
    }

    @GetMapping("/verifications")
    public ResponseEntity<?> verificationEmail(@RequestParam("code") String code) {
        return ResponseEntity.ok().body(mailService.verifyCode(code));
    }
}
