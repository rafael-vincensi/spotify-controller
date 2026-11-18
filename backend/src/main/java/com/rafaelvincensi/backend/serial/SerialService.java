package com.rafaelvincensi.backend.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.rafaelvincensi.backend.command.CommandProcessor;
import org.springframework.stereotype.Service;

@Service
public class SerialService {

    private final CommandProcessor commandProcessor;
    private SerialPort serialPort;

    public SerialService(CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
    }

    public void conectar() {

        serialPort = SerialPort.getCommPort("COM4");
        serialPort.setBaudRate(9600);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);

        if (serialPort.openPort()) {
            System.out.println("Arduino conectado na COM4!");
            iniciarLeitura();
        } else {
            System.err.println("Não foi possível conectar ao Arduino.");
        }
    }

    private void iniciarLeitura() {

        Thread thread = new Thread(() -> {
            StringBuilder buffer = new StringBuilder();

            try {
                while (!Thread.currentThread().isInterrupted()
                        && serialPort.isOpen()) {

                    int bytesDisponiveis = serialPort.bytesAvailable();

                    if (bytesDisponiveis > 0) {
                        byte[] dados = new byte[bytesDisponiveis];

                        int bytesLidos = serialPort.readBytes(dados, dados.length);

                        for (int i = 0; i < bytesLidos; i++) {
                            char caractere = (char) dados[i];

                            if (caractere == '\n') {
                                String mensagem = buffer.toString().trim();
                                buffer.setLength(0);

                                if (!mensagem.isBlank()) {
                                    processarMensagem(mensagem);
                                }

                            } else if (caractere != '\r') {
                                buffer.append(caractere);
                            }
                        }

                    } else {
                        Thread.sleep(10);
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            } catch (Exception e) {
                System.err.println(
                        "Erro na comunicação serial: " + e.getMessage());
            }
        });
        thread.setName("arduino-serial-reader");
        thread.setDaemon(true);
        thread.start();
    }

    private void processarMensagem(String mensagem) {
        if (mensagem.startsWith("COMANDO:")) {
            commandProcessor.processar(mensagem);
        }
    }
}