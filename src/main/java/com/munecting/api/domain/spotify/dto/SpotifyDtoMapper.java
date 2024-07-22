package com.munecting.api.domain.spotify.dto;

import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

}
