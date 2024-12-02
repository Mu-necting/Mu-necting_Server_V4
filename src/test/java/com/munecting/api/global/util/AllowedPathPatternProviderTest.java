package com.munecting.api.global.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AllowedPathPatternProviderTest {

    @Autowired
    private AllowedPathPatternProvider patternProvider;

    @DisplayName("주어진 경로가 화이트리스트에 등록되어 있는지 확인한다.")
    @Test
    public void isPathWhitelisted(){
        //given
        String requestPath1 = "/api/auth/allowed";
        String requestPath2 = "/actuator/health/not-allowed";

        //when
        boolean result1 = patternProvider.isPathWhitelisted(requestPath1);
        boolean result2 = patternProvider.isPathWhitelisted(requestPath2);

        //then
        assertThat(result1).isTrue();
        assertThat(result2).isFalse();
    }

}