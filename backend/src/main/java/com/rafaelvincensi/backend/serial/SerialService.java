package com.rafaelvincensi.backend.serial;

import com.fazecast.jSerialComm.SerialPort;
import org.springframework.stereotype.Service;

@Service
public class SerialService {

    public void listarPortas() {

        System.out.println("===== PORTAS SERIAIS =====");

        SerialPort[] portas = SerialPort.getCommPorts();

        for (SerialPort porta : portas) {
            System.out.println(
                    porta.getSystemPortName() + " - " +
                            porta.getDescriptivePortName()
            );
        }

        System.out.println("==========================");
    }
}

