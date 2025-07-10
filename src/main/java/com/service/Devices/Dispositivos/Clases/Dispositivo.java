package com.service.Devices.Dispositivos.Clases;

import com.service.Comunicacion.GestorPuertoSerie;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.Interfaz.dispositivoBase;
import com.service.estructuras.classDevice;

import java.io.IOException;

public class Dispositivo  extends DispositivoBase implements com.service.Interfaz.Dispositivo, dispositivoBase.ASCII {

    PuertosSerie port ;
    int Num;
    public static Boolean         puede485=true;
    DispositivoAsciiListener listener;

    public Dispositivo(String strpuerto, classDevice Device,int ndevice) {
        super(strpuerto, Device,ndevice);

    }

    @Override
    public void stop() {
        try {
            port.close();
        } catch (IOException e) {

        }
        super.stop();
    }

    @Override
    public void Iniciar(DispositivoAsciiListener Listenerrs232) {
        listener = Listenerrs232;
        System.out.println("OLA?"
                + Integer.parseInt(Device.getDireccion().get(1))+" "
                + Integer.parseInt(Device.getDireccion().get(2))+" "
                +Integer.parseInt(Device.getDireccion().get(3)));
        port =  GestorPuertoSerie.getInstance().initPuertoSerie(strpuerto, Integer.parseInt(Device.getDireccion().get(0)), Integer.parseInt(Device.getDireccion().get(1)), Integer.parseInt(Device.getDireccion().get(2)), Integer.parseInt(Device.getDireccion().get(3)),0,0);
        PuertosSerie.SerialPortReader reader = new PuertosSerie.SerialPortReader(port.getInputStream(), new PuertosSerie.PuertosSerieListener() {

            @Override
            public void onMsjPort(String data) {
                // Cuando recibes el mensaje, le añades el Num
                System.out.println("DEBUG Device "+data);
                if (listener != null) {
                    listener.MensajeAscii(Num, data);
                }
            }
        });
        reader.startReading();
    }

    @Override
    public void Escribir(String Msj) {
        port.write(Msj);
    }

}
