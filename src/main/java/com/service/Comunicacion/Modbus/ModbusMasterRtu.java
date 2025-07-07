package com.service.Comunicacion.Modbus;


import static android.content.ContentValues.TAG;

import android.util.Log;

import com.service.Comunicacion.Modbus.Req.ModbusReqRtuMaster;
import com.service.Comunicacion.Modbus.modbus4And.requset.OnRequestBack;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

public class ModbusMasterRtu {
    public CountDownLatch connectionLatch = new CountDownLatch(1);
/***    EJEMPLO PARA INICIAR EN ACTIVIDAD, ESCRIBIR REGISTRO 3 CON EL VALOR 15
     *                 ModbusReqRtuMaster modbusReqRtuMaster;
     *                 ModbusMasterRtu modbusMasterRtu;
     *                 modbusMasterRtu= new ModbusMasterRtu();
     *                 try {
     *                     modbusReqRtuMaster = modbusMasterRtu.init();
     *                     modbusMasterRtu.Write_Register(1,2,15);
     *                 } catch (Exception e) {
     *                     throw new RuntimeException(e);
     *                 }

     ***/

public ModbusReqRtuMaster init(String puerto, int baudrate, int dataBits, int stopbit, int parity) throws Exception {
    connectionLatch = new CountDownLatch(1);
    ModbusReqRtuMaster[] modbusReq = new ModbusReqRtuMaster[1];  // Usar un array para permitir modificación en el callback

    ModbusReqRtuMaster.getInstance().init(new OnRequestBack<String>() {
        @Override
        public void onSuccess(String s) {
            Log.d(TAG, "onSuccess " + s);
            modbusReq[0] = ModbusReqRtuMaster.getInstance(); // Asignar la instancia después de inicializar
            connectionLatch.countDown();
        }

        @Override
        public void onFailed(String msg) {
            Log.d(TAG, "onFailed " + msg);
            connectionLatch.countDown();
        }
    }, puerto, baudrate, dataBits,stopbit , parity);

    connectionLatch.await();
    return modbusReq[0];
}
    public void Write_Register(int IdSlave,int Registro,int Value){
        ModbusReqRtuMaster.getInstance().writeRegister(new OnRequestBack<String>() {
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
        ModbusReqRtuMaster.getInstance().readHoldingRegisters(new OnRequestBack<short[]>() {
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

    public void destroy(){
        ModbusReqRtuMaster.getInstance().destroy();
    }

    public String readCoil_Registro(int IdSlave,int Registro) throws InterruptedException {
        final String[] registro = {"vacio"};
        CountDownLatch connectionLatch = new CountDownLatch(1);
        ModbusReqRtuMaster.getInstance().readHoldingRegisters(new OnRequestBack<short[]>() {
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