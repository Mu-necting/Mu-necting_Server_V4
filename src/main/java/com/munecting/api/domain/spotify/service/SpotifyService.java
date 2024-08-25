package com.munecting.api.domain.spotify.service;


import com.munecting.api.domain.like.dto.response.GetLikedTrackResponseDto;
import com.munecting.api.domain.spotify.dto.AlbumResponseDto;
import com.munecting.api.domain.spotify.dto.ArtistResponseDto;
import com.munecting.api.domain.spotify.dto.MusicResponseDto;
import com.munecting.api.domain.spotify.dto.SpotifyDtoMapper;
import com.munecting.api.global.error.exception.EntityNotFoundException;
import com.munecting.api.global.common.dto.response.Status;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Artist;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.data.albums.GetAlbumsTracksRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import com.wrapper.spotify.requests.data.search.SearchItemRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotifyService {

    private final SpotifyApi spotifyApi;
    private final SpotifyDtoMapper spotifyDtoMapper;

    public List<MusicResponseDto> searchTracks(String keyword, Integer limit, Integer offset) {
        SearchItemRequest searchItemRequest = spotifyApi.searchItem(keyword, ModelObjectType.TRACK.getType())
                .limit(limit)
                .offset(offset)
                .market(CountryCode.KR)
                .build();

        try {
            final SearchResult searchResult = searchItemRequest.execute();
            Paging<Track> trackPaging = searchResult.getTracks();
            Track[] tracks = trackPaging.getItems();

            return Stream.of(tracks)
                    .map(track -> spotifyDtoMapper.convertToTrackResponseDto(track))
                    .collect(Collectors.toList());

        } catch (IOException | ParseException | SpotifyWebApiException ex) {
            throw new RuntimeException(ex);
        }
    }

    public List<AlbumResponseDto> searchAlbums(String keyword, Integer limit, Integer offset) {
        SearchItemRequest searchItemRequest = spotifyApi.searchItem(keyword, ModelObjectType.ALBUM.getType())
                .limit(limit)
                .offset(offset)
                .market(CountryCode.KR)
                .build();

        try {
            final SearchResult searchResult = searchItemRequest.execute();

            Paging<AlbumSimplified> albumPaging = searchResult.getAlbums();
            AlbumSimplified[] albums = albumPaging.getItems();

            return Stream.of(albums)
                    .map(album -> spotifyDtoMapper.convertToAlbumResponseDto(album))
                    .collect(Collectors.toList());

        } catch (IOException | ParseException | SpotifyWebApiException ex) {
            throw new RuntimeException(ex);
        }

    }

    public List<ArtistResponseDto> searchArtists(String keyword, Integer limit, Integer offset) {
        SearchItemRequest searchItemRequest = spotifyApi.searchItem(keyword, ModelObjectType.ARTIST.getType())
                .limit(limit)
                .offset(offset)
                .market(CountryCode.KR)
                .build();

        try {
            final SearchResult searchResult = searchItemRequest.execute();

            Paging<Artist> ArtistPaging = searchResult.getArtists();
            Artist[] artists = ArtistPaging.getItems();

            return Stream.of(artists)
                    .map(artist -> spotifyDtoMapper.convertToArtistResponseDto(artist))
                    .collect(Collectors.toList());

        } catch (IOException | ParseException | SpotifyWebApiException ex) {
            throw new RuntimeException(ex);
        }

    }

    public List<MusicResponseDto> getTracksByAlbumId(String albumId, Integer limit, Integer offset) {
        GetAlbumsTracksRequest getAlbumsTracksRequest = spotifyApi.getAlbumsTracks(albumId)
                .limit(limit)
                .offset(offset)
                .market(CountryCode.KR)
                .build();

        try {
            Paging<TrackSimplified> trackSimplifiedPaging = getAlbumsTracksRequest.execute();
            TrackSimplified[] trackSimplifieds = trackSimplifiedPaging.getItems();

            return Stream.of(trackSimplifieds)
                    .map(trackSimplified -> spotifyDtoMapper.convertToTrackResponseDto(trackSimplified))
                    .collect(Collectors.toList());
        } catch (IOException | ParseException | SpotifyWebApiException ex) {
            throw new EntityNotFoundException(Status.ALBUM_NOT_FOUND);
        }

    }

    public List<AlbumResponseDto> getAlbumsByArtistId(String artistId, Integer limit, Integer offset) {
        GetArtistsAlbumsRequest getArtistsAlbumsRequest = spotifyApi.getArtistsAlbums(artistId)
                .limit(limit)
                .offset(offset)
                .market(CountryCode.KR)
                .build();

        try {
            Paging<AlbumSimplified> albumSimplifiedPaging = getArtistsAlbumsRequest.execute();
            AlbumSimplified[] albumSimplifieds = albumSimplifiedPaging.getItems();

            return Stream.of(albumSimplifieds)
                    .map(albumSimplified -> spotifyDtoMapper.convertToAlbumResponseDto(albumSimplified))
                    .collect(Collectors.toList());
        } catch (IOException | ParseException | SpotifyWebApiException ex) {
            throw new EntityNotFoundException(Status.ARTIST_NOT_FOUND);
        }

    }

    public MusicResponseDto getTrack(String trackId) {
        GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackId)
                .market(CountryCode.KR)
                .build();

        try {
            Track track = getTrackRequest.execute();
            return spotifyDtoMapper.convertToTrackResponseDto(track);
        } catch (IOException | ParseException | SpotifyWebApiException ex) {
            throw new EntityNotFoundException(Status.TRACK_NOT_FOUND);
        }

    }

    public GetLikedTrackResponseDto getTrackForLiked(String trackId, Long likeId) {
        GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackId)
                .market(CountryCode.KR)
                .build();

        return handleSpotifyApiCall(() -> {
            Track track = getTrackRequest.execute();
            return spotifyDtoMapper.convertToLikedTrackResponseDto(track, likeId);

        }, Status.TRACK_NOT_FOUND);
    }

    private <T> T handleSpotifyApiCall(SpotifyApiCall<T> apiCall, Status status) {
        try {
            return apiCall.execute();
        } catch (IOException | ParseException | SpotifyWebApiException ex) {
            throw new EntityNotFoundException(status);
        }
    }

    public void validateTrackId(String trackId) {
        getTrack(trackId);
    }
}