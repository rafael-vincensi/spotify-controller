
package com.rafaelvincensi.backend.command;

import com.rafaelvincensi.backend.spotify.SpotifyService;
import org.springframework.stereotype.Component;

@Component
public class CommandProcessor {

    private final SpotifyService spotifyService;

    public CommandProcessor(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    public void processar(String comando) {

        if (comando == null || comando.isBlank()) {
            return;
        }

        try {
            switch (comando.trim()) {

                case "COMANDO: PLAY" -> {
                    spotifyService.alternarReproducao();
                    System.out.println("Spotify: estado de reproducao alternado");
                }
                case "COMANDO: PAUSE" -> {
                    spotifyService.alternarReproducao();
                    System.out.println("Spotify: estado de reproducao alternado");
                }

                case "COMANDO: NEXT" -> {
                    spotifyService.proximaMusica();
                    System.out.println("Spotify: proxima musica");
                }

                case "COMANDO: PREVIOUS" -> {
                    spotifyService.musicaAnterior();
                    System.out.println("Spotify: musica anterior");
                }

                case "COMANDO: LIKE" -> {
                    spotifyService.alternarCurtidaMusicaAtual();
                }

                default ->
                        System.out.println("Comando desconhecido: " + comando);
            }

        } catch (Exception e) {
            System.err.println(
                    "Erro ao executar comando " + comando + ": " + e.getMessage()
            );
        }
    }
}
