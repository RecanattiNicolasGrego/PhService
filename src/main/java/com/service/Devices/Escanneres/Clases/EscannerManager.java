package com.service.Devices.Escanneres.Clases;
import com.service.utilsPackage.ComService;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EscannerManager  {

    private static EscannerManager instance;
    private Map<Integer, PuertosSerie> scannerMap = new HashMap<>();
    private EscanerListener listener;
    private EscanerUsb escanerUsb;
    public interface EscanerListener {
        void MensajeEscaneres(int num, String data);
    }
    public void setListener(EscanerListener Listener){
        this.listener=Listener;
    }


    public static synchronized EscannerManager getInstance() {
        if (instance == null) {
            instance = new EscannerManager();
        }
        return instance;
    }
    public void stop(){
        try {
            escanerUsb.stop();
        } catch (Exception e) {

        }
        try {
            for (PuertosSerie scanner : scannerMap.values()) {
                scanner.close();
            }
        } catch (IOException e) {
        }
    }
    public void addScannerUSB(int num){
      //  scannerMap.put(num, scanner);
        System.out.println("DEBUG ESCANNER "+ num);
        escanerUsb =new EscanerUsb(ComService.getInstance().activity,null);
        escanerUsb.readAsyncFromDevice();
        escanerUsb.setListener(new com.service.Interfaz.EscanerListener() {
            @Override
            public void newData(String data) {
                if (listener != null) {
                    listener.MensajeEscaneres(num+1, data);
                }
            }
        });
    }
    public void addScannerPort(int num, PuertosSerie scanner) {
        scannerMap.put(num, scanner);
        System.out.println("DEBUG ESCANNER "+ num);
        PuertosSerie.SerialPortReader reader = new PuertosSerie.SerialPortReader(scanner.getInputStream(), new PuertosSerie.PuertosSerieListener() {

            @Override
            public void onMsjPort(String data) {
                // Cuando recibes el mensaje, le añades el id
                System.out.println("DEBUG ESCANNER "+data);
                if (listener != null) {
                    listener.MensajeEscaneres(num+1, data);
                }
            }
        });
        reader.startReading();
    }

}