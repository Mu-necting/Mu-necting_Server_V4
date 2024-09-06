package com.munecting.api.domain.spotify.service;


import com.munecting.api.domain.like.dto.response.GetLikedTrackResponseDto;
import com.munecting.api.domain.spotify.dto.response.AlbumResponseDto;
import com.munecting.api.domain.spotify.dto.response.ArtistResponseDto;
import com.munecting.api.domain.spotify.dto.response.MusicResponseDto;
import com.munecting.api.domain.spotify.dto.SpotifyDtoMapper;
import com.munecting.api.global.error.exception.EntityNotFoundException;
import com.munecting.api.global.error.exception.GeneralException;
import com.munecting.api.global.error.exception.InternalServerException;
import com.neovisionaries.i18n.CountryCode;
import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.enums.ModelObjectType;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import com.wrapper.spotify.model_objects.special.SearchResult;
import com.wrapper.spotify.model_objects.specification.AlbumSimplified;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.model_objects.specification.TrackSimplified;
import com.wrapper.spotify.requests.data.albums.GetAlbumsTracksRequest;
import com.wrapper.spotify.requests.data.artists.GetArtistsAlbumsRequest;
import com.wrapper.spotify.requests.data.search.SearchItemRequest;
import com.wrapper.spotify.requests.data.tracks.GetTrackRequest;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Service;

import static com.munecting.api.global.common.dto.response.Status.*;
import static com.wrapper.spotify.enums.ModelObjectType.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SpotifyService {

    private final SpotifyApi spotifyApi;
    private final SpotifyDtoMapper spotifyDtoMapper;

    public List<MusicResponseDto> searchTracks(String keyword, Integer limit, Integer offset) {
        return searchSpotify(
                TRACK,
                keyword,
                limit,
                offset,
                searchResult -> searchResult.getTracks().getItems(),
                spotifyDtoMapper::convertToTrackResponseDto
        );
    }

    public List<AlbumResponseDto> searchAlbums(String keyword, Integer limit, Integer offset) {
        return searchSpotify(
                ALBUM,
                keyword,
                limit,
                offset,
                searchResult -> searchResult.getAlbums().getItems(),
                spotifyDtoMapper::convertToAlbumResponseDto);
    }

    public List<ArtistResponseDto> searchArtists(String keyword, Integer limit, Integer offset) {
        return searchSpotify(
                ARTIST,
                keyword,
                limit,
                offset,
                searchResult -> searchResult.getArtists().getItems(),
                spotifyDtoMapper::convertToArtistResponseDto);
    }

    private <T, R> List<R> searchSpotify(
            ModelObjectType modelType,
            String keyword,
            Integer limit,
            Integer offset,
            Function<SearchResult, T[]> resultItemsExtractor,
            Function<T, R> dtoConverter
    ) {
        SearchItemRequest searchItemRequest = spotifyApi.searchItem(keyword, modelType.getType())
                .limit(limit)
                .offset(offset)
                .market(CountryCode.KR)
                .build();

        final SearchResult searchResult = handleSpotifyApiCall(
                searchItemRequest::execute,
                new InternalServerException());

        T[] items = resultItemsExtractor.apply(searchResult);

        return Stream.of(items)
                .map(dtoConverter)
                .collect(Collectors.toList());
    }

    public List<MusicResponseDto> getTracksByAlbumId(String albumId, Integer limit, Integer offset) {
        GetAlbumsTracksRequest getAlbumsTracksRequest = spotifyApi.getAlbumsTracks(albumId)
                .limit(limit)
                .offset(offset)
                .market(CountryCode.KR)
                .build();

        final Paging<TrackSimplified> trackSimplifiedPaging = handleSpotifyApiCall(
                getAlbumsTracksRequest::execute,
                new EntityNotFoundException(ALBUM_NOT_FOUND));

        return Stream.of(trackSimplifiedPaging.getItems())
                .map(spotifyDtoMapper::convertToTrackResponseDto)
                .collect(Collectors.toList());
    }

    public List<AlbumResponseDto> getAlbumsByArtistId(String artistId, Integer limit, Integer offset) {
        GetArtistsAlbumsRequest getArtistsAlbumsRequest = spotifyApi.getArtistsAlbums(artistId)
                .limit(limit)
                .offset(offset)
                .market(CountryCode.KR)
                .build();

        final Paging<AlbumSimplified> albumPaging = handleSpotifyApiCall(
                getArtistsAlbumsRequest::execute,
                new EntityNotFoundException(ARTIST_NOT_FOUND));

        return Stream.of(albumPaging.getItems())
                .map(spotifyDtoMapper::convertToAlbumResponseDto)
                .collect(Collectors.toList());
    }

    public MusicResponseDto getTrack(String trackId) {
        Track track = fetchTrackById(trackId);
        return spotifyDtoMapper.convertToTrackResponseDto(track);
    }

    public GetLikedTrackResponseDto getLikedTrack(String trackId, Long likeId) {
        Track track = fetchTrackById(trackId);
        return spotifyDtoMapper.convertToLikedTrackResponseDto(track, likeId);
    }

    public void validateTrackExists(String trackId) {
        fetchTrackById(trackId);
    }

    private Track fetchTrackById(String trackId) {
        GetTrackRequest getTrackRequest = spotifyApi.getTrack(trackId)
                .market(CountryCode.KR)
                .build();

        return handleSpotifyApiCall(
                getTrackRequest::execute,
                new EntityNotFoundException(TRACK_NOT_FOUND));
    }

    /**
     *
     * @param apiCall 스포티파이 api 호출
     * @param apiFailureException 스포티파이 api 요청 실패 시 throw 할 예외
     * @return 스포티파이 api 호출 결과
     */
    private <T> T handleSpotifyApiCall(SpotifyApiCall<T> apiCall, GeneralException apiFailureException) {
        try {
            return apiCall.execute();

        } catch (IOException | ParseException | SpotifyWebApiException ex) {
            log.warn("spotify api call error : {}", ex.getMessage());
            throw apiFailureException;
        }
    }

    @FunctionalInterface
    private interface SpotifyApiCall<T> {

        T execute() throws IOException, ParseException, SpotifyWebApiException;
    }
}