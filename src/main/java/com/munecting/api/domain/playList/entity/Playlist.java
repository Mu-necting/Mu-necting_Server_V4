package com.munecting.api.domain.playList.entity;

import com.munecting.api.domain.playList.dto.PlaylistResponseDto;
import com.munecting.api.domain.playListMusic.entity.PlaylistMusic;
import com.munecting.api.global.common.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(indexes = {@Index(name = "playlist_index", columnList = "userId")})
public class Playlist extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long userId;

    @NotNull
    private String name;

    public void updateName(String name) {
        this.name = name;
    }

    public static Playlist toEntity(String playlistName, Long userId) {
        return Playlist.builder()
                .userId(userId)
                .name(playlistName)
                .build();
    }

    public static PlaylistResponseDto toDto(Playlist playlist) {
        return PlaylistResponseDto.builder()
                .id(playlist.id)
                .name(playlist.name)
                .build();
    }
}
