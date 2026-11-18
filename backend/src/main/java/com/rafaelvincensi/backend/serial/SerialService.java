package com.rafaelvincensi.backend.serial;

import com.fazecast.jSerialComm.SerialPort;
import org.springframework.stereotype.Service;

@Service
public class SerialService {

    private SerialPort serialPort;

    public void conectar() {

        serialPort = SerialPort.getCommPort("COM4");
        serialPort.setBaudRate(9600);
        serialPort.setNumDataBits(8);
        serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        serialPort.setParity(SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 1000, 0);

        if (serialPort.openPort()) {
            System.out.println("Arduino conectado na COM4!");
            iniciarLeitura();
        } else {
            System.out.println("Nao foi possivel conectar ao Arduino.");
        }
    }

    private void iniciarLeitura() {

        Thread thread = new Thread(() -> {

            try {
                while (!Thread.currentThread().isInterrupted()
                        && serialPort.isOpen()) {

                    int bytesDisponiveis = serialPort.bytesAvailable();

                    if (bytesDisponiveis > 0) {

                        byte[] buffer = new byte[bytesDisponiveis];

                        int bytesLidos = serialPort.readBytes(buffer, buffer.length);

                        if (bytesLidos > 0) {
                            String dados = new String(buffer,0,bytesLidos);
                            System.out.print(dados);
                        }

                    } else {
                        Thread.sleep(10);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            } catch (Exception e) {
                System.err.println(
                        "Erro na comunicação serial: " + e.getMessage()
                );
            }
        });

        thread.setName("arduino-serial-reader");
        thread.setDaemon(true);
        thread.start();
    }
}
