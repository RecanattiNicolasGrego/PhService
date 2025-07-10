package com.service.Devices.Escanneres.Clases;


import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;

import java.nio.charset.StandardCharsets;

public class TecladoUsb {

    /**
     * se conecta a dispositivo usb y lee los datos que envia el dispositivo
     */

    // debug
    private final UsbManager usbManager;
    private UsbDeviceConnection connection;

    public TecladoUsb(Context context) {
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public void readAsyncFromDevice() {
        UsbDevice device = findPrinterDevice();
        if (device == null) {
            return;
        }

        UsbInterface usbInterface = device.getInterface(0);
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint endpoint = usbInterface.getEndpoint(i);
            System.out.println("Endpoint " + i + " dirección: " + endpoint.getAddress());
        }
        UsbEndpoint inEndpoint = usbInterface.getEndpoint(0); // Endpoint de entrada (IN)

        connection = usbManager.openDevice(device);
        if (connection == null || !connection.claimInterface(usbInterface, true)) {
            return;
        }

        UsbRequest request = new UsbRequest();
        request.initialize(connection, inEndpoint);


        new Thread(() -> {
            byte[] buffer = new byte[8];
            while (true) {
                int length = connection.bulkTransfer(inEndpoint, buffer, buffer.length, 1000);
                if (length > 0) {
                    String receivedData = new String(buffer, 0, length, StandardCharsets.UTF_8);
                    System.out.println("Datos recibidos: " + receivedData);
                }
            }
        }).start();
    }

    private UsbDevice findPrinterDevice() {
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            System.out.println("dispositivo vendor:" + device.getVendorId());
            System.out.println("dispositivo product id:" + device.getProductId());
            System.out.println("dispositivo name:" + device.getProductName());
            if (device.getVendorId() == 1112 && device.getProductId() == 58) {
                System.out.println("dispositivo encontrado");
                return device;
            }
        }
        System.out.println("dispositivo no encontrado");
        return null;
    }

}