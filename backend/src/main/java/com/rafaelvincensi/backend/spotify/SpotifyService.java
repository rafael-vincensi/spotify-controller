
package com.rafaelvincensi.backend.spotify;

import com.rafaelvincensi.backend.auth.SpotifyAuthService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;

import java.net.URI;

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

        restClient.post()
                .uri("https://api.spotify.com/v1/me/player/next")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity();
    }

    public void musicaAnterior() {
        String accessToken = spotifyAuthService.obterAccessToken();

        restClient.post()
                .uri("https://api.spotify.com/v1/me/player/previous")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .toBodilessEntity();
    }


    public void alternarCurtidaMusicaAtual() {
        String accessToken = spotifyAuthService.obterAccessToken();

        JsonNode estado = restClient.get()
                .uri("https://api.spotify.com/v1/me/player")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(JsonNode.class);

        if (estado == null || estado.path("item").isMissingNode()
                || estado.path("item").isNull()) {
            throw new IllegalStateException("Nenhuma musica encontrada na reproducao atual!");
        }

        JsonNode musica = estado.path("item");

        if (!"track".equals(musica.path("type").asText())) {
            throw new IllegalStateException("O conteudo atual nao eh uma musica!");
        }

        String trackUri = musica.path("uri").asText();

        URI uriConsulta = UriComponentsBuilder
                .fromUriString("https://api.spotify.com/v1/me/library/contains")
                .queryParam("uris", trackUri)
                .build()
                .encode()
                .toUri();

        Boolean[] resultado = restClient.get()
                .uri(uriConsulta)
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .body(Boolean[].class);

        boolean jaCurtida = resultado != null && resultado.length > 0  && Boolean.TRUE.equals(resultado[0]);

        URI uriBiblioteca = UriComponentsBuilder
                .fromUriString("https://api.spotify.com/v1/me/library")
                .queryParam("uris", trackUri)
                .build()
                .encode()
                .toUri();

        if (jaCurtida) {
            restClient.delete()
                    .uri(uriBiblioteca)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .toBodilessEntity();

            System.out.println("Musica removida das curtidas!");
        } else {
            restClient.put()
                    .uri(uriBiblioteca)
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .toBodilessEntity();

            System.out.println("Musica adicionada as curtidas!");
        }
    }

}
