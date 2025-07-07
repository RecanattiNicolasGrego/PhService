package com.service.Devices.Impresora.Tipos;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.nio.charset.StandardCharsets;

public class UsbPrinterManager {
    //private static final int VENDOR_ID_SATO = 0x0828;
    //private static final int PRODUCT_ID_SATO = 0x0101;
    private final UsbManager usbManager;
    private UsbDeviceConnection connection;
    UsbDevice deviceglob;

    public UsbPrinterManager(Context context) {
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public boolean sendControlTransferCommand(String message) {
        UsbInterface usbInterface = deviceglob.getInterface(0);
        UsbEndpoint endpoint = usbInterface.getEndpoint(0);  // Endpoint de control
        byte[] data = message.getBytes();  // Convertir el String a un array de bytes
        connection = usbManager.openDevice(deviceglob);
        if (connection == null || !connection.claimInterface(usbInterface, true)) {
            return false;
        }
        // Parámetros del control transfer:
        // bRequest (comando), wValue, wIndex, data (payload de datos)
        int requestType = UsbConstants.USB_TYPE_VENDOR | UsbConstants.USB_DIR_OUT;
        int request = 0x01; // Comando personalizado, varía según el dispositivo
        int value = 0; // Usualmente depende del dispositivo
        int index = 0; // Usualmente depende del dispositivo

        int result = connection.controlTransfer(requestType, request, value, index, data, data.length, 1000);

        connection.releaseInterface(usbInterface);
        connection.close();

        return result >= 0;
    }

    public boolean printLabelTermical(String label) {
        UsbDevice device = findPrinterDevice();
        if (device == null) {
            return false;
        }

        UsbInterface usbInterface = device.getInterface(0);
        UsbEndpoint outEndpoint = usbInterface.getEndpoint(0);

        connection = usbManager.openDevice(device);
        if (connection == null || !connection.claimInterface(usbInterface, true)) {
            return false;
        }

        // Comandos ESC/POS para impresora térmica
        byte[] escPosCommand = new byte[]{0x1B, 0x40};  // Comando para inicializar impresora
        byte[] labelBytes = label.getBytes(StandardCharsets.UTF_8);
        byte[] data = new byte[escPosCommand.length + labelBytes.length];
        System.arraycopy(escPosCommand, 0, data, 0, escPosCommand.length);
        System.arraycopy(labelBytes, 0, data, escPosCommand.length, labelBytes.length);

        int result = connection.bulkTransfer(outEndpoint, data, data.length, 1000);

        connection.releaseInterface(usbInterface);
        connection.close();

        return result >= 0;
    }

    public boolean printLabel(String label) {
        UsbDevice device = findPrinterDevice();
        if (device == null) {
            return false;
        }

        UsbInterface usbInterface = device.getInterface(0);
        UsbEndpoint outEndpoint = usbInterface.getEndpoint(0);

        connection = usbManager.openDevice(device);
        if (connection == null || !connection.claimInterface(usbInterface, true)) {
            return false;
        }

        byte[] data = label.getBytes(StandardCharsets.UTF_8);
        int result = connection.bulkTransfer(outEndpoint, data, data.length, 1000);
        connection.releaseInterface(usbInterface);
        connection.close();
        System.out.println(result+ " device ");
        return result >= 0;
    }

    private UsbDevice findPrinterDevice() {
        for (UsbDevice device : usbManager.getDeviceList().values()) {

            for (int i = 0; i < device.getInterfaceCount(); i++) {
                UsbInterface usbInterface = device.getInterface(i);
                if (usbInterface.getInterfaceClass() ==UsbConstants.USB_CLASS_PRINTER) {
                    return device;
                }
            }
        }
        return null;
    }
}