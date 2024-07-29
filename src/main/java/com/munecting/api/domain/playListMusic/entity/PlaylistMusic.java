package com.munecting.api.domain.playListMusic.entity;

import com.munecting.api.global.common.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PlaylistMusic extends BaseEntity {

    @EmbeddedId
    private PlaylistMusicId id;

    @Column(name = "play_order")
    private Long playOrder;

    public static PlaylistMusic toEntity (PlaylistMusicId playlistMusicId) {
        return PlaylistMusic.builder()
                .id(playlistMusicId)
                .build();
    }

    public void updatePlayOrder (Long order) {
        this.playOrder = order;
    }
}
