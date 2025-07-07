package com.service.Devices.Escanneres.Clases;


import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;

import java.util.HashMap;
import java.util.Map;

public class EscanerUsb {
    private static final Map<Integer, Character> normalKeys = new HashMap<>();
    private static final Map<Integer, Character> shiftedKeys = new HashMap<>();

    static {
        normalKeys.put(30, '1'); shiftedKeys.put(30, '!');
        normalKeys.put(31, '2'); shiftedKeys.put(31, '@');
        normalKeys.put(32, '3'); shiftedKeys.put(32, '#');
        normalKeys.put(33, '4'); shiftedKeys.put(33, '$');
        normalKeys.put(34, '5'); shiftedKeys.put(34, '%');
        normalKeys.put(35, '6'); shiftedKeys.put(35, '^');
        normalKeys.put(36, '7'); shiftedKeys.put(36, '&');
        normalKeys.put(37, '8'); shiftedKeys.put(37, '*');
        normalKeys.put(38, '9'); shiftedKeys.put(38, '(');
        normalKeys.put(39, '0'); shiftedKeys.put(39, ')');
        normalKeys.put(40, '\n'); shiftedKeys.put(40, '\n'); // Enter
        normalKeys.put(44, ' '); shiftedKeys.put(44, ' ');   // Space
        normalKeys.put(45, '-'); shiftedKeys.put(45, '_');
        normalKeys.put(46, '='); shiftedKeys.put(46, '+');
        normalKeys.put(47, '['); shiftedKeys.put(47, '{');
        normalKeys.put(48, ']'); shiftedKeys.put(48, '}');
        normalKeys.put(49, '\\'); shiftedKeys.put(49, '|');
        normalKeys.put(51, ';'); shiftedKeys.put(51, ':');
        normalKeys.put(52, '\''); shiftedKeys.put(52, '"');
        normalKeys.put(53, '`'); shiftedKeys.put(53, '~');
        normalKeys.put(54, ','); shiftedKeys.put(54, '<');
        normalKeys.put(55, '.'); shiftedKeys.put(55, '>');
        normalKeys.put(56, '/'); shiftedKeys.put(56, '?');

        // Letras a-z
        for (int i = 4; i <= 29; i++) {
            char c = (char) ('a' + i - 4);
            normalKeys.put(i, c);
            shiftedKeys.put(i, Character.toUpperCase(c));
        }
    }

    private final UsbManager usbManager;
    private UsbDeviceConnection connection;
    private EscanerListener listener;
    public EscanerUsb(Context context, EscanerListener listener) {
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        this.listener = listener;
    }

    public void setListener(EscanerListener listener) {
        this.listener = listener;
    }

    public void readAsyncFromDevice() {
        UsbDevice device = findPrinterDevice();
        if (device == null) {
            return;
        }

        UsbInterface usbInterface = device.getInterface(0);
        System.out.println("Class: " + usbInterface.getInterfaceClass());
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
            StringBuilder scannedText = new StringBuilder();

            while (true) {
                System.out.println("Leyendo...");
                int length = connection.bulkTransfer(inEndpoint, buffer, buffer.length, 1000);
                if (length > 0) {
                    int modifier = buffer[0] & 0xFF;
                    int keyCode = buffer[2] & 0xFF;

                    if (keyCode == 0) continue;

                    boolean shift = (modifier & 0x22) != 0; // Shift izquierdo o derecho

                    Character c = shift ? shiftedKeys.get(keyCode) : normalKeys.get(keyCode);
                    if (c != null) {
                        if (c == '\n') {
                            System.out.println("Código escaneado: " + scannedText.toString().trim());
                            listener.newData(scannedText.toString().trim());
                            scannedText.setLength(0); // Limpiar para el próximo escaneo
                        } else {
                            scannedText.append(c);
                        }
                    }
                }
            }
        }).start();
    }

    private UsbDevice findPrinterDevice() {
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            for (int i = 0; i < device.getInterfaceCount(); i++) {

                UsbInterface intf = device.getInterface(i);
                System.out.println("dispositivo vendor:" + device.getVendorId());
                System.out.println("dispositivo product id:" + device.getProductId());
                System.out.println("dispositivo name:" + device.getProductName());
                System.out.println("------------ INTERFAZ USB ------------");
                System.out.println("Interface number: " + i);
                System.out.println("Alternate setting: " + intf.getAlternateSetting());
                System.out.println("Interface class: 0x" + Integer.toHexString(intf.getInterfaceClass()));
                System.out.println("Interface subclass: 0x" + Integer.toHexString(intf.getInterfaceSubclass()));
                System.out.println("Interface protocol: 0x" + Integer.toHexString(intf.getInterfaceProtocol()));
                if (intf.getInterfaceClass() == 0x03) {
                    System.out.print("Dispositivo HID detectado - ");
                    switch (intf.getInterfaceProtocol()) {
                        case 0x01 :{
                            System.out.println("Tipo: Teclado");
                            break;
                        }
                        case 0x02 : {
                            System.out.println("Tipo: Ratón");
                            break;
                        }
                        default : {
                            System.out.println("Tipo: Otro HID");
                            break;
                        }
                    }
                } else {
                    System.out.println("No es un dispositivo HID");
                }

                if (intf.getInterfaceClass() == UsbConstants.USB_CLASS_HID && Integer.toHexString(intf.getInterfaceSubclass()).equals("1") ) {
                    System.out.println("Se encontró interfaz HID");
                    System.out.println("Interface count: " + device.getInterfaceCount());
                    System.out.println("Endpoints en interfaz seleccionada: " + intf.getEndpointCount());

                    return device;
                }
            }
        }
        return null;
    }

    /*    public UsbDevice findPrinterDevice() {
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            System.out.println("dispositivo vendor:" + device.getVendorId());
            System.out.println("dispositivo product id:" + device.getProductId());
            System.out.println("dispositivo name:" + device.getProductName());
            if (device.getVendorId() == 1409 && device.getProductId() == 284) {
                System.out.println("dispositivo encontrado");
                return device;
            }
        }
        System.out.println("dispositivo no encontrado");
        return null;
    }*/


}