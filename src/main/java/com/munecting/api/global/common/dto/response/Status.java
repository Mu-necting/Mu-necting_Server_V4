package com.munecting.api.global.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum Status {

    //공통 정상 응답
    OK(HttpStatus.OK, "COMMON200", "성공입니다."),
    CREATED(HttpStatus.CREATED, "COMMON201", "생성되었습니다."),

    //공통 오류 응답
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    CONFLICT(HttpStatus.CONFLICT, "COMMON409", "이미 생성되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버에 오류가 발생했습니다."),

    //Spotify 오류 응답
    TRACK_NOT_FOUND(HttpStatus.NOT_FOUND, "SPOTIFY_TRACK404", "스포티파이에서 track 정보를 찾을 수 없습니다."),
    ARTIST_NOT_FOUND(HttpStatus.NOT_FOUND, "SPOTIFY_ARTIST404", "스포티파이에서 artist 정보를 찾을 수 없습니다."),
    ALBUM_NOT_FOUND(HttpStatus.NOT_FOUND, "SPOTIFY_ALBUM404", "스포티파이에서 album 정보를 찾을 수 없습니다."),

    //Playlist 오류 응답
    PLAY_LIST_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAYLIST404", "플레이 리스트가 존재하지 않습니다."),

    //PlaylistMusic 오류 응답
    PLAY_LIST_MUSIC_NOT_FOUND(HttpStatus.NOT_FOUND, "PLAYLISTMUSIC404", "플레이 리스트에 해당 노래가 존재하지 않습니다."),


    //User 오류 응답
    ALREADY_EXISTS_EMAIL(HttpStatus.CONFLICT, "Conflict409", "이미 존재하는 이메일입니다."),
    ALREADY_EXISTS_NICKNAME(HttpStatus.CONFLICT,"Conflict409","이미 존재하는 닉네임입니다.")
    ;


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public Body getBody() {
        return Body.builder()
                .message(message)
                .code(code)
                .isSuccess(httpStatus.is2xxSuccessful())
                .httpStatus(httpStatus)
                .build();
    }

}
