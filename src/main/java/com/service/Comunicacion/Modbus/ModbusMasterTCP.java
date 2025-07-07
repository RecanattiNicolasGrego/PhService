package com.service.Comunicacion.Modbus;


import static android.content.ContentValues.TAG;


import android.util.Log;

import com.service.Comunicacion.Modbus.modbus4And.requset.ModbusParam;
import com.service.Comunicacion.Modbus.modbus4And.requset.ModbusReq;
import com.service.Comunicacion.Modbus.modbus4And.requset.OnRequestBack;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class ModbusMasterTCP {

    public static ModbusReq init(String ip, OnRequestBack<String> callback) throws Exception {
        ModbusReq modbusReq = ModbusReq.getInstance(); // Inicializa la instancia aquí
        CountDownLatch connectionLatch = new CountDownLatch(1);

        modbusReq.setParam(new ModbusParam()
                        .setHost(ip)
                        .setPort(502)
                        .setEncapsulated(false)
                        .setKeepAlive(true)
                        .setTimeout(1000)
                        .setRetries(0))
                .init(new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.d(TAG, "onSuccess " + s);
                        callback.onSuccess(s);
                        connectionLatch.countDown();
                    }

                    @Override
                    public void onFailed(String msg) {
                        Log.d(TAG, "onFailed " + msg);
                        callback.onFailed(msg);
                        connectionLatch.countDown();
                    }
                });

        connectionLatch.await();
        return modbusReq;
    }
    /*public static ModbusReq init(String ip) throws Exception {
        ModbusReq modbusReq=null;
        CountDownLatch connectionLatch = new CountDownLatch(1);
        ModbusReq.getInstance().setParam(new ModbusParam()
                        .setHost(ip)
                        .setPort(502)
                        .setEncapsulated(false)
                        .setKeepAlive(true)
                        .setTimeout(1000)
                        .setRetries(0))
                .init(new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.d(TAG, "onSuccess " + s);
                        connectionLatch.countDown();
                    }

                    @Override
                    public void onFailed(String msg) {
                        Log.d(TAG, "onFailed " + msg);
                        connectionLatch.countDown();
                    }
                });
        connectionLatch.await();
        return modbusReq;

    }*/
    public void Write_Register(int IdSlave,int Registro,int Value){
        ModbusReq.getInstance().writeRegister(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                Log.e(TAG, "writeRegister onSuccess " + s);
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "writeRegister onFailed " + msg);
            }
        },IdSlave,Registro,Value);
    }

    public String readCoil_100(int IdSlave) throws InterruptedException {

        final String[] registro = {"vacio"};
        CountDownLatch connectionLatch = new CountDownLatch(1);
        ModbusReq.getInstance().readHoldingRegisters(new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] data) {
                registro[0] =Arrays.toString(data);
                Log.d(TAG, "readHoldingRegisters onSuccess " + Arrays.toString(data));
                connectionLatch.countDown();
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "readHoldingRegisters onFailed " + msg);
                connectionLatch.countDown();
            }
        }, IdSlave, 0, 100);
        connectionLatch.await();
        return registro[0];
    }



    public String readCoil_Registro(int IdSlave,int Registro) throws InterruptedException {
        final String[] registro = {"vacio"};
        CountDownLatch connectionLatch = new CountDownLatch(1);
        ModbusReq.getInstance().readHoldingRegisters(new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] data) {
                registro[0] =Arrays.toString(data);
                Log.d(TAG, "readHoldingRegisters onSuccess " + Arrays.toString(data));
                connectionLatch.countDown();
            }

            @Override
            public void onFailed(String msg) {
                Log.e(TAG, "readHoldingRegisters onFailed " + msg);
                connectionLatch.countDown();
            }
        }, IdSlave, Registro-1, 1);
        connectionLatch.await();
        return registro[0];
    }
}