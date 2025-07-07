package com.service.Comunicacion.Modbus;


import com.service.Comunicacion.Modbus.modbus4And.serial.SerialPortWrapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android_serialport_api.SerialPort;



public class JSerialCommWrapper implements SerialPortWrapper {
    private SerialPort serialPort;
    private final int baudRate;
    private final int dataBits;
    private final int stopBits;
    private final int parity;
    private final String puerto;

    public JSerialCommWrapper(String puerto, SerialPort serialPort, int baudRate, int dataBits, int stopBits, int parity) {
        this.puerto=puerto;
        this.serialPort = serialPort;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;

    }

    @Override
    public void close() throws Exception {
        serialPort.close();
    }

    @Override
    public void open() throws Exception {

        File archivo=new File(puerto);

        if(archivo.exists()){
            try {
                serialPort=new SerialPort(archivo, baudRate,stopBits,dataBits,parity,0,0);
            } catch (
                    IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public InputStream getInputStream() {
        return serialPort.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() {
        return serialPort.getOutputStream();
    }

    @Override
    public int getBaudRate() {
        return baudRate;
    }

    @Override
    public int getFlowControlIn() {
        return 0;
    }

    @Override
    public int getFlowControlOut() {
        return 0;
    }

    @Override
    public int getDataBits() {
        return dataBits;
    }

    @Override
    public int getStopBits() {
        return stopBits;
    }

    @Override
    public int getParity() {
        return parity;
    }
}
