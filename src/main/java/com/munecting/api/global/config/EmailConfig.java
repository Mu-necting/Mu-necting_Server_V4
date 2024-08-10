package com.munecting.api.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Value("${naver.id}")
    private String id;
    @Value("${naver.password}")
    private String password;
    @Bean
    public JavaMailSender mailSender() {//JAVA MAILSENDER 인터페이스를 구현한 객체를 빈으로 등록하기 위함.

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();//JavaMailSender 의 구현체를 생성하고
        mailSender.setHost("smtp.naver.com");// 속성을 넣기 시작합니다. 이메일 전송에 사용할 SMTP 서버 호스트를 설정
        mailSender.setUsername(id);//네이버 ID를 넣습니다.
        mailSender.setPassword(password);//네이버 비밀번호를 넣습니다.
        mailSender.setPort(465);// 465로 포트를 지정

        mailSender.setJavaMailProperties(getMailProperites()); // 메일 인증서버 정보 가져오기
        mailSender.setDefaultEncoding("UTF-8");
        return mailSender;//빈으로 등록한다.
    }

    private Properties getMailProperites() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", "smtp"); // 프로토콜 설정
        properties.setProperty("mail.smtp.auth", "true"); // smtp 인증
        properties.setProperty("mail.smtp.starttls.enable", "true"); // smtp strattles 사용
        properties.setProperty("mail.debug", "true"); // 디버그 사용
        properties.setProperty("mail.smtp.ssl.trust","smtp.naver.com"); // ssl 인증 서버는 smtp.naver.com
        properties.setProperty("mail.smtp.ssl.enable", "true"); // ssl 사용
        return properties;
    }
}

