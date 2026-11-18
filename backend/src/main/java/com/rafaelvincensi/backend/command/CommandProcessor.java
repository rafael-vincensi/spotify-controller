
package com.rafaelvincensi.backend.command;

import org.springframework.stereotype.Component;

@Component
public class CommandProcessor {

    public void processar(String comando) {

        if (comando == null || comando.isBlank()) {
            return;
        }

        switch (comando.trim()) {

            case "COMANDO: PLAY" ->
                    System.out.println("Acao identificada: PLAY");

            case "COMANDO: PAUSE" ->
                    System.out.println("Acao identificada: PAUSE");

            case "COMANDO: NEXT" ->
                    System.out.println("Acao identificada: NEXT");

            case "COMANDO: PREVIOUS" ->
                    System.out.println("Acao identificada: PREVIOUS");

            case "COMANDO: LIKE" ->
                    System.out.println("Acao identificada: LIKE");

            case "COMANDO: UNLIKE" ->
                    System.out.println("Acao identificada: UNLIKE");

            case "COMANDO: SHUFFLE_ON" ->
                    System.out.println("Acao identificada: SHUFFLE_ON");

            case "COMANDO: SHUFFLE_OFF" ->
                    System.out.println("Acao identificada: SHUFFLE_OFF");

            default ->
                    System.out.println("Comando desconhecido: " + comando);
        }
    }
}
