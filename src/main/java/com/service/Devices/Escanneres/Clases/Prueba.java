package com.service.Devices.Escanneres.Clases;

import android.content.Context;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.nio.ByteBuffer;

import android.hardware.usb.UsbRequest;

public class Prueba extends Thread {

    private UsbManager usbManager;

    private UsbDeviceConnection connection;
    private UsbEndpoint inEndpoint;
    private  UsbInterface usbInterface;
    public Prueba(Context context) {
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        findPrinterDevice();  // Encuentra el dispositivo
    }

    public void run(UsbDevice device) {
        if (device == null) {
            System.out.println("No se encontró el dispositivo.");
            return;
        }
        if (inEndpoint == null) {
            System.out.println("No se encontró un endpoint de entrada tipo INTERRUPT.");
            return;
        }
        try {
            connection = usbManager.openDevice(device);
            if (connection == null || !connection.claimInterface(usbInterface, true)) {
                return;
            }
        } catch (Exception e) {

        }

        byte[] buffer = new byte[inEndpoint.getMaxPacketSize()];  // Buffer to hold the received data
        UsbRequest request = new UsbRequest();
        int bytesRead=-1;
        request.initialize(connection, inEndpoint);
        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        request.queue(byteBuffer, buffer.length); // ACA ESTARIA EL PROBLEMA. DIOS QUE ALGUIEN ME MATE
        while (!Thread.interrupted()) {
            if (connection.requestWait() == request) {
                bytesRead = byteBuffer.position(); // Cantidad de datos leídos
                if (bytesRead > 0) {
                    System.out.println("Datos recibidos (hex): ");
                    for (int i = 0; i < bytesRead; i++) {
                        System.out.printf("%02X ", buffer[i]);
                    }
                    System.out.println("OA PUT");
                }
            } else {
             bytesRead = connection.bulkTransfer(inEndpoint, buffer, buffer.length, 1000); // Timeout de 1 segundo
            }
        if (bytesRead > 0) {
            System.out.println("Datos recibidos (hex): ");
            for (int i = 0; i < bytesRead; i++) {
                System.out.printf("%02X ", buffer[i]);
            }

            // Convierte los datos recibidos en texto (si es un teclado HID)
            String text = parseHidDataToText(buffer, bytesRead);
            System.out.println("Texto decodificado: " + text);
        } else {
            System.out.println("No hay datos nuevos.");
            try {
                Thread.sleep(100); // Esperar antes de volver a intentar
            } catch (InterruptedException e) {
                break;
                }
            }
        }
    }

// Función para convertir los datos HID en texto
private String parseHidDataToText(byte[] buffer, int length) {
    StringBuilder text = new StringBuilder();

    // Aquí interpretas los códigos de teclas del escáner, que son similares a los de un teclado HID.
    for (int i = 0; i < length; i++) {
        byte key = buffer[i];

        // Aquí deberías implementar la conversión de los códigos de teclas a caracteres
        // Esto depende del formato específico de tu escáner (por ejemplo, el uso de la tabla HID para teclados)
        if (key != 0) {  // Verifica que no sea un código de tecla nula
            // Convertir el código de tecla (esto es un ejemplo, debes ajustar según tu dispositivo)
            char keyChar = (char) key;
            text.append(keyChar);
        }

    }
    return text.toString();
       /* System.out.println("Max packet size: " + inEndpoint.getMaxPacketSize());
        while (!Thread.interrupted()) {
            boolean success=false;
            try {
                request.setClientData(buffer);  // Establece el buffer como los datos del cliente
                byte[] copy = Arrays.copyOf(buffer, buffer.length);
                ByteBuffer byteBuffer = ByteBuffer.wrap(copy);
                success     = request.queue(byteBuffer, copy.length);
                if (byteBuffer.remaining() == 0) {
                    System.out.println("El byteBuffer está vacío. No se puede agregar a la cola.");
                    return;
                }
                if (copy.length <= 0) {
                    System.out.println("El tamaño del copy es incorrecto: " + copy.length);
                    return;
                }
                if (buffer.length <= 0) {
                    System.out.println("El tamaño del buffer es incorrecto: " + copy.length);
                    return;
                }

                if (!success) {
                    System.out.println("Error al agregar el buffer a la cola.");
                }
            } catch (Exception e) {
                System.out.println("Excepción durante el procesamiento del buffer: " + e.getMessage());
                e.printStackTrace();
            }
            if (success) {
                connection.requestWait(); // Get the status of the request
                int result  =buffer.length;
                if (result > 0) {
                    // Procesar los datos recibidos
                    System.out.print("Datos recibidos (hex): ");
                    for (int i = 0; i < result; i++) {
                        System.out.printf("%02X ", buffer[i]);
                    }
                    System.out.println();

                    // Si deseas decodificar los datos como texto (por ejemplo, si el escáner actúa como un teclado HID):
                    String text = new String(buffer, 0, result, StandardCharsets.UTF_8);
                    System.out.println("Texto decodificado: " + text);
                } else if (result == 0) {
                    System.out.println("Sin datos por ahora.");
                    try {
                        Thread.sleep(100); // Esperar y seguir intentando
                    } catch (InterruptedException e) {
                        break;
                    }
                } else {
                    System.out.println("Error al leer datos: " + result);
                    break;
                }
            } else {
                    System.out.println("No se pudo encolar la solicitud.");
                    return;
                }
                break;
            }*/
        }

    // Detener el hilo de lectura
    public void stopReading() {
        this.interrupt();
    }
            private UsbDevice findPrinterDevice() {
                for (UsbDevice device : usbManager.getDeviceList().values()) {
                    for (int i = 0; i < device.getInterfaceCount(); i++) {
                        UsbInterface intf = device.getInterface(i);
                       if (intf.getInterfaceClass() == UsbConstants.USB_CLASS_HID && Integer.toHexString(intf.getInterfaceSubclass()).equals("1") ) {
                               this.inEndpoint =  device.getInterface(0).getEndpoint(0);
                               System.out.println("Estamos aca  pa o no ?");
                                run(device);
                               return device;
                       }
                    }
                }
                return null;
            }
        }
