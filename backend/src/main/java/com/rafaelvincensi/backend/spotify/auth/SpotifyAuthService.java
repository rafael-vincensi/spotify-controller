package com.rafaelvincensi.backend.spotify.auth;

import com.rafaelvincensi.backend.spotify.auth.dto.SpotifyTokenResponseDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@Service
public class SpotifyAuthService {

    @Value("${spotify.client.id}")
    private String clientId;

    @Value("${spotify.client.secret}")
    private String secret;

    @Value("${spotify.redirect.url}")
    private String redirectUrl;

    private final RestClient restClient = RestClient.create();
    private SpotifyTokenResponseDTO tokenResponse;

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

    private SpotifyTokenResponseDTO trocarCodigoPorToken(String code) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();

        formData.add("grant_type", "authorization_code");
        formData.add("code", code);
        formData.add("redirect_uri", redirectUrl);

        return restClient.post()
                .uri("https://accounts.spotify.com/api/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .headers(headers -> headers.setBasicAuth(clientId, secret))
                .body(formData)
                .retrieve()
                .body(SpotifyTokenResponseDTO.class);
    }

    public String processarCallback(String code, String state, String error, HttpSession session) {
        if (error != null) {
            throw new IllegalArgumentException("autorizacaos negada pelo spotify: " + error);
        }

        String stateEsperado = (String) session.getAttribute("spotify_oauth_state");

        if (stateEsperado == null || !stateEsperado.equals(state)) {
            throw new IllegalArgumentException("parametro state invalido");
        }

        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("authorization code nao recebido");
        }

        SpotifyTokenResponseDTO response = trocarCodigoPorToken(code);
        this.tokenResponse = response;
        session.removeAttribute("spotify_oauth_state");

        return "autenticação spotify realizada com sucesso!!";
    }

    public String consultarEstadoReproducao() {
        if (tokenResponse == null || tokenResponse.accessToken() == null) {
            throw new IllegalStateException("Sdpotify nao autenticado");
        }

        return restClient.get()
                .uri("https://api.spotify.com/v1/me/player")
                .headers(headers -> headers.setBearerAuth(tokenResponse.accessToken()))
                .retrieve()
                .body(String.class);
    }

}

