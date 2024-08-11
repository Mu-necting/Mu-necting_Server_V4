package com.munecting.api.domain.uploadedMusic.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicRequestDto {

    private Double latitude;

    private Double longitude;

    private Long userId; // 인증 작업 리팩토링 끝나면 연동 할 예정

    private String trackId;

    private Integer uploadDuration;

}
