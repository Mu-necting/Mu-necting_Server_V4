package com.munecting.api.domain.spotify.dto;

import com.munecting.api.domain.like.dto.response.GetLikedTrackResponseDto;
import com.munecting.api.domain.like.dto.response.LikedTrackArtistResponseDto;
import com.munecting.api.domain.spotify.dto.response.AlbumResponseDto;
import com.munecting.api.domain.spotify.dto.response.ArtistResponseDto;
import com.munecting.api.domain.spotify.dto.response.MusicResponseDto;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class SpotifyDtoMapper {

    public MusicResponseDto convertToTrackResponseDto(Track track) {
        List<ArtistResponseDto> artistResponseDtos = Stream.of(track.getArtists())
                .map(ArtistResponseDto::of)
                .collect(Collectors.toList());

        return MusicResponseDto.of(track, artistResponseDtos);
    }

    public GetLikedTrackResponseDto convertToLikedTrackResponseDto(Track track, Long likeId) {
        return GetLikedTrackResponseDto.builder()
                .trackPreview(track.getPreviewUrl())
                .trackTitle(track.getName())
                .trackId(track.getId())
                .images(track.getAlbum().getImages())
                .artists(Arrays.stream(track.getArtists())
                        .map(artist ->
                                LikedTrackArtistResponseDto.of(artist.getName()))
                        .collect(Collectors.toList()))
                .likeId(likeId)
                .build();
    }

    public MusicResponseDto convertToTrackResponseDto(TrackSimplified trackSimplified) {
        List<ArtistResponseDto> artistResponseDtos = Stream.of(trackSimplified.getArtists())
                .map(ArtistResponseDto::of)
                .collect(Collectors.toList());
        return MusicResponseDto.of(trackSimplified, artistResponseDtos);
    }

    public AlbumResponseDto convertToAlbumResponseDto(AlbumSimplified albumSimplified) {

        List<ArtistResponseDto> artistResponseDtos = Stream.of(albumSimplified.getArtists())
                .map(ArtistResponseDto::of)
                .collect(Collectors.toList());

        AlbumResponseDto responseDto = AlbumResponseDto.of(albumSimplified, artistResponseDtos);

        return responseDto;
    }

    public ArtistResponseDto convertToArtistResponseDto(Artist artist) {
        return ArtistResponseDto.of(artist);
    }

}
