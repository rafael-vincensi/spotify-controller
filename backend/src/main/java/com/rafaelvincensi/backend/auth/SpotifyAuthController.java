
package com.rafaelvincensi.backend.auth;

import com.rafaelvincensi.backend.spotify.SpotifyService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SpotifyAuthController {

    private final SpotifyAuthService spotifyAuthService;
    private final SpotifyService spotifyService;

    public SpotifyAuthController(SpotifyAuthService spotifyAuthService, SpotifyService spotifyService) {
        this.spotifyAuthService = spotifyAuthService;
        this.spotifyService = spotifyService;
    }

    @GetMapping("/spotify/login")
    public String iniciarAutorizacao(HttpSession session) {
        String authorizationUrl = spotifyAuthService.gerarUrlAuthorization(session);

        return "redirect:" + authorizationUrl;
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            HttpSession session) {

        String result = spotifyAuthService.processarCallback(code, state, error, session);

        return ResponseEntity.ok(result);
    }

    @GetMapping("spotify/status")
    public ResponseEntity<String> consultarEstadoReproducao(){
        String resposta = spotifyAuthService.consultarEstadoReproducao();

        if (resposta == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(resposta);
    }

    @PostMapping("/spotify/playback/toggle")
    public ResponseEntity<Void> alternarReproducao() {
        spotifyService.alternarReproducao();
        return ResponseEntity.noContent().build();
    }
}
