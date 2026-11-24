
package com.rafaelvincensi.backend.spotify;

import com.rafaelvincensi.backend.auth.SpotifyAuthService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Service
public class SpotifyService {

    private final SpotifyAuthService spotifyAuthService;
    private final RestClient restClient = RestClient.create();

    public SpotifyService(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
    }

    public void alternarReproducao() {
        String accessToken = spotifyAuthService.obterAccessToken();

        tools.jackson.databind.JsonNode estado = restClient.get()
                .uri("https://api.spotify.com/v1/me/player")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(tools.jackson.databind.JsonNode.class);

        if (estado == null) {
            throw new IllegalStateException("Nao existe uma reproducao ativa no Spotify");
        }

        boolean estaReproduzindo = estado
                .path("is_playing")
                .asBoolean();

        String endpoint = estaReproduzindo
                ? "https://api.spotify.com/v1/me/player/pause"
                : "https://api.spotify.com/v1/me/player/play";

        restClient.put()
                .uri(endpoint)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity();
    }

    public void proximaMusica() {
        String accessToken = spotifyAuthService.obterAccessToken();

        restClient.put()
                .uri("https://api.spotify.com/v1/me/player/next")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity();
    }

    public void musicaAnterior() {
        String accessToken = spotifyAuthService.obterAccessToken();

        restClient.put()
                .uri("https://api.spotify.com/v1/me/player/previous")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity();
    }
}
