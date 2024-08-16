package com.munecting.api.domain.uploadedMusic.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicRequestDto {

    @NotNull(message = "위도는 필수값입니다.")
    private Double latitude;

    @NotNull(message = "경도는 필수값입니다.")
    private Double longitude;

    private Long userId; // 인증 작업 리팩토링 끝나면 연동 할 예정

    @NotNull(message = "노래 아이디는 필수값입니다.")
    private String trackId;

    @NotNull(message = "업로드 기간은 필수값입니다.")
    private Integer uploadDuration;

}
