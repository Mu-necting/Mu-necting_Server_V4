package com.munecting.api.domain.playListMusic.entity;


import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PlaylistMusicId implements Serializable {

    @NotNull
    private Long playlistId;

    @NotNull
    private String musicUri;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistMusicId that = (PlaylistMusicId) o;
        return Objects.equals(playlistId, that.playlistId) && Objects.equals(musicUri, that.musicUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlistId, musicUri);
    }
}
