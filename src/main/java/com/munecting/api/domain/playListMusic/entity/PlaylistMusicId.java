package com.munecting.api.domain.playListMusic.entity;


import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PlaylistMusicId implements Serializable {

    @NotNull
    private Long playlistId;

    //스포티 파이에서 제공하는 자료형 확인 후 수정 예정
    @NotNull
    private Long musicId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistMusicId that = (PlaylistMusicId) o;
        return Objects.equals(playlistId, that.playlistId) && Objects.equals(musicId, that.musicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlistId, musicId);
    }
}
