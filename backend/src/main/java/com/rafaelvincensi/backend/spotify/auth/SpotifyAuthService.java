package com.rafaelvincensi.backend.spotify.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Service
public class SpotifyAuthService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${SPOTIFY_CLIENT_SECRET}")
    private String secret;

    @Value("${SPOTIFY_URL_REDIRECT}")
    private String redirectUrl;

    public String gerarUrlAuthorization(HttpSession session){
        String state = UUID.randomUUID().toString();

        session.setAttribute("spotify_oauth_state", state);

        String scopes = String.join(" ",
                "user-modify-playback-state",
                "user-read-playback-state",
                "user-library-modify"
        );

        return UriComponentsBuilder
                .fromUriString("https://accounts.spotify.com/authorize")
                .queryParam("client_id", clientId)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUrl)
                .queryParam("scope", scopes)
                .queryParam("state", state)
                .build()
                .encode()
                .toUriString();
    }
}

