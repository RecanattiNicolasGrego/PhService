package com.service.Devices.Expansiones.Clases;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;

import java.util.HashMap;
import java.util.Map;

public class EscannerManager  {

    private static EscannerManager instance;
    private Map<Integer, PuertosSerie> scannerMap = new HashMap<>();
    private ScannerMessageListener listener;

    public interface ScannerMessageListener {
        void EscannerListener(int num, String data);
    }
    public void setListener(EscannerManager.ScannerMessageListener Listener){
        this.listener=Listener;
    }


    public static synchronized EscannerManager getInstance() {
        if (instance == null) {
            instance = new EscannerManager();
        }
        return instance;
    }

    public void addScanner(int num, PuertosSerie scanner) {
        scannerMap.put(num, scanner);
        System.out.println("DEBUG ESCANNER "+ num);
        PuertosSerie.SerialPortReader reader = new PuertosSerie.SerialPortReader(scanner.getInputStream(), new PuertosSerie.PuertosSerieListener() {

            @Override
            public void onMsjPort(String data) {
                // Cuando recibes el mensaje, le añades el id
                System.out.println("DEBUG ESCANNER "+data);
                if (listener != null) {
                    listener.EscannerListener(num, data);
                }
            }
        });
        reader.startReading();
    }

}