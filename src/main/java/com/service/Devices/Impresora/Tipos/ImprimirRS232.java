package com.service.Devices.Impresora.Tipos;


import com.service.Comunicacion.PuertosSerie.PuertosSerie;

import java.util.concurrent.CountDownLatch;

public class ImprimirRS232 {
    private final PuertosSerie serialPort;
    public ImprimirRS232(PuertosSerie serialPort) {
        this.serialPort = serialPort;
    }
    public void Imprimir(String etiqueta){
        serialPort.write(etiqueta);

    }


}
