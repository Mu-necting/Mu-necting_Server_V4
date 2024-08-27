package com.munecting.api.domain.spotify.service;

import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import org.apache.hc.core5.http.ParseException;

import java.io.IOException;

@FunctionalInterface
public interface SpotifyApiCall<T> {
    T execute() throws IOException, ParseException, SpotifyWebApiException;
}
