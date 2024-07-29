package com.munecting.api.domain.playListMusic.entity;


import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class PlaylistMusicId implements Serializable {

    @NotNull
    private Long playlistId;

    @NotNull
    private String trackId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistMusicId that = (PlaylistMusicId) o;
        return Objects.equals(playlistId, that.playlistId) && Objects.equals(trackId, that.trackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlistId, trackId);
    }

    public static PlaylistMusicId toEntity (Long playlistId, String trackId) {
        return PlaylistMusicId.builder()
                .playlistId(playlistId)
                .trackId(trackId)
                .build();
    }
}
