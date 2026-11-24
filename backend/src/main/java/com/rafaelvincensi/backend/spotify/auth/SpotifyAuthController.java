
package com.rafaelvincensi.backend.spotify.auth;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SpotifyAuthController {

    private final SpotifyAuthService spotifyAuthService;

    public SpotifyAuthController(SpotifyAuthService spotifyAuthService) {
        this.spotifyAuthService = spotifyAuthService;
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
}
