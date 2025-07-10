package com.service.Comunicacion.Modbus.Req;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.service.Comunicacion.Modbus.modbus4And.ModbusFactory;
import com.service.Comunicacion.Modbus.modbus4And.ModbusSlaveSet;
import com.service.Comunicacion.Modbus.modbus4And.exception.IllegalDataAddressException;
import com.service.Comunicacion.Modbus.modbus4And.exception.ModbusInitException;
import com.service.Comunicacion.Modbus.modbus4And.ip.IpParameters;
import com.service.Comunicacion.Modbus.modbus4And.requset.ModbusParam;
import com.service.Comunicacion.Modbus.modbus4And.requset.OnRequestBack;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ModbusReqTCPslave {
    private final static String TAG = ModbusReqTCPslave.class.getSimpleName();

    private static ModbusReqTCPslave modbusReq;
    private ModbusSlaveSet mModbusSlave;
    private final ModbusParam modbusParam = new ModbusParam();

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    private boolean isInit = false;

    /**
     * get modbus instance
     *
     * @return
     */
    public static synchronized ModbusReqTCPslave getInstance() {
        if (modbusReq == null)
            modbusReq = new ModbusReqTCPslave();
        return modbusReq;
    }

    /**
     * set modbus param
     *
     * @param modbusParam
     */

    /**
     * init modbus
     *
     * @throws ModbusInitException
     */
    public ModbusSlaveSet init(final OnRequestBack<String> onRequestBack, MatrizSlave image, String host) throws ModbusInitException {

        ModbusFactory mModbusFactory = new ModbusFactory();
        IpParameters params = new IpParameters();
        params.setHost(modbusParam.host);
        params.setPort(modbusParam.port);
        params.setEncapsulated(modbusParam.encapsulated);
        //mModbusSlave = new com.zgkxzx.modbus4And.ip.tcp.TcpSlave(8000,modbusParam.encapsulated);
        mModbusSlave= mModbusFactory.createTcpSlave(modbusParam.encapsulated);
        mModbusSlave.addProcessImage(image);
        Handler mainHandler = new Handler(Looper.getMainLooper());
        try {
            Runnable runnable = new Runnable() {
                public void run() {
                    try {
                        mModbusSlave.start();
                    } catch (ModbusInitException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            Thread.sleep(1000);
            new Thread(runnable).start();
            Log.d(TAG, "Modbus4Android init success");
            isInit = true;

            // Pasar la llamada de éxito al hilo principal
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    onRequestBack.onSuccess("Modbus4Android init success");
                }
            });
        } catch (Exception e) {
            mModbusSlave.stop();
            isInit = false;
            Log.d(TAG, "Modbus4Android init failed " + e);

            // Pasar la llamada de error al hilo principal
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    onRequestBack.onFailed("Modbus4Android init failed ");
                }
            });
        }
        return mModbusSlave;
    }
    /**
     * destory the modbus4Android instance
     */
    public void destory() {
        modbusReq = null;
        if (mModbusSlave != null) {
            mModbusSlave.stop();
        }
        isInit = false;
    }
    public short readRegister(int register) throws IllegalDataAddressException {
        final short[] registro = {123};
        if (!isInit) {
            return 123;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    registro[0] = mModbusSlave.getProcessImage(1).getHoldingRegister(register);
                } catch (IllegalDataAddressException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        return registro[0];
    }

    public void setRegister(int register,short value) throws IllegalDataAddressException {
        if (!isInit) {
            return ;
        }
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                mModbusSlave.getProcessImage(1).setHoldingRegister(register,value);
            }
        });

    }



}
