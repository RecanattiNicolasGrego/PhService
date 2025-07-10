package com.service.Devices.Impresora.Tipos;

import android.content.Context;
import android.hardware.usb.UsbManager;

import androidx.appcompat.app.AppCompatActivity;
import com.service.Devices.Impresora.more.DiscoveredPrinterListAdapter;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.printer.FieldDescriptionData;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class ImprimirUSB {

    private final AppCompatActivity context;
    List<FieldDescriptionData> variablesList = new ArrayList<>();
    Connection connection;
    DiscoveredPrinterListAdapter discoveredPrinterListAdapter;
    Map<Integer, String> vars;
    public ImprimirUSB(AppCompatActivity context) {
        this.context = context;
    }


    public void print(String label, AppCompatActivity context, Boolean memory, List<String> memoryList) {
        Runnable myRunnable = () -> {
            try {
                discoveredPrinterListAdapter = new DiscoveredPrinterListAdapter(context);
                UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                    UsbPrinterManager printerManager = new UsbPrinterManager(context);
                    boolean success = printerManager.printLabel(label);
                    int i=0;
                    while(!success && i<3){
                        try{
                            success = printerManager.printLabel(label);
                            Thread.sleep(500);
                         //   System.out.println("reintentando imprimir");
                            i++;
                        }catch (Exception e){}
                    }
                    System.out.println(success);
                    if (success) {
                        System.out.println("printeo");
                    } else {

                        System.out.println("no printeo");
              //          Utils.Mensaje("Impresora no encontrada en usb",R.layout.item_customtoasterror,context);
                    }
            } catch (Exception e) {
                System.out.println("me suicido : no printeo");
            //    Utils.Mensaje("usb init:" + e.getMessage(), R.layout.item_customtoasterror, context);
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();


    }
}
