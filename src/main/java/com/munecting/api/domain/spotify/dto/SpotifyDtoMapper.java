package com.munecting.api.domain.spotify.dto;

import com.munecting.api.domain.like.dto.response.GetLikedTrackResponseDto;
import com.munecting.api.domain.like.dto.response.LikedTrackArtistResponseDto;
import com.munecting.api.domain.track.dto.response.RecentlyPlayedTrackArtistInfo;
import com.munecting.api.domain.track.dto.response.RecentlyPlayedTrackResponseDto;
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
        MusicResponseDto responseDto = MusicResponseDto.builder()
                .trackUri(track.getUri())
                .trackId(track.getId())
                .trackTitle(track.getName())
                .trackPreview(track.getPreviewUrl())
                .images(track.getAlbum().getImages())
                .build();

        List<ArtistResponseDto> artistResponseDtos = Stream.of(track.getArtists())
                .map(artist -> ArtistResponseDto.builder()
                        .artistId(artist.getId())
                        .artistUri(artist.getUri())
                        .artistId(artist.getId())
                        .artistName(artist.getName())
                        .build())
                .collect(Collectors.toList());
        responseDto.setArtists(artistResponseDtos);

        return responseDto;
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
        MusicResponseDto responseDto = MusicResponseDto.builder()
                .trackUri(trackSimplified.getUri())
                .trackId(trackSimplified.getId())
                .trackTitle(trackSimplified.getName())
                .trackPreview(trackSimplified.getPreviewUrl())
                .build();

        List<ArtistResponseDto> artistResponseDtos = Stream.of(trackSimplified.getArtists())
                .map(artist -> ArtistResponseDto.builder()
                        .artistUri(artist.getUri())
                        .artistName(artist.getName())
                        .artistId(artist.getId())
                        .build())
                .collect(Collectors.toList());
        responseDto.setArtists(artistResponseDtos);

        return responseDto;
    }

    public AlbumResponseDto convertToAlbumResponseDto(AlbumSimplified albumSimplified) {
        AlbumResponseDto responseDto = AlbumResponseDto.builder()
                .albumId(albumSimplified.getId())
                .albumUri(albumSimplified.getUri())
                .albumTitle(albumSimplified.getName())
                .images(albumSimplified.getImages())
                .build();

        List<ArtistResponseDto> artistResponseDtos = Stream.of(albumSimplified.getArtists())
                .map(artist -> ArtistResponseDto.builder()
                        .artistUri(artist.getUri())
                        .artistId(artist.getId())
                        .artistName(artist.getName())
                        .build())
                .collect(Collectors.toList());
        responseDto.setArtists(artistResponseDtos);

        return responseDto;
    }

    public ArtistResponseDto convertToArtistResponseDto(Artist artist) {

        ArtistResponseDto artistResponseDto = ArtistResponseDto.builder()
                .artistId(artist.getId())
                .artistName(artist.getName())
                .artistUri(artist.getUri())
                .images(artist.getImages())
                .build();

        return artistResponseDto;
    }

    public RecentlyPlayedTrackResponseDto convertToRecentlyPlayedTrackResponseDto(Track track, Long recentlyPlayedId) {
        return RecentlyPlayedTrackResponseDto.builder()
                .trackPreview(track.getPreviewUrl())
                .trackTitle(track.getName())
                .trackId(track.getId())
                .images(track.getAlbum().getImages())
                .artists(Arrays.stream(track.getArtists())
                        .map(artist ->
                                RecentlyPlayedTrackArtistInfo.of(artist.getName()))
                        .collect(Collectors.toList()))
                .recentlyPlayedId(recentlyPlayedId)
                .build();
    }
}
