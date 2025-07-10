package com.service.Comunicacion.Modbus.Req;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.service.Comunicacion.Modbus.JSerialCommWrapper;
import com.service.Comunicacion.Modbus.modbus4And.ModbusFactory;
import com.service.Comunicacion.Modbus.modbus4And.ModbusSlaveSet;
import com.service.Comunicacion.Modbus.modbus4And.exception.IllegalDataAddressException;
import com.service.Comunicacion.Modbus.modbus4And.exception.ModbusInitException;
import com.service.Comunicacion.Modbus.modbus4And.requset.ModbusParam;
import com.service.Comunicacion.Modbus.modbus4And.requset.OnRequestBack;
import com.service.Comunicacion.Modbus.modbus4And.serial.SerialPortWrapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android_serialport_api.SerialPort;


public class ModbusReqRtuSlave {
    private final static String TAG = ModbusReqRtuSlave.class.getSimpleName();

    private static ModbusReqRtuSlave modbusReq;
    private ModbusSlaveSet mModbusSlave;
    private ModbusParam modbusParam = new ModbusParam();
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    private boolean isInit = false;

    private ModbusReqRtuSlave() {

    }

    /**
     * get modbus instance
     *
     * @return
     */
    public static synchronized ModbusReqRtuSlave getInstance() {
        if (modbusReq == null)
            modbusReq = new ModbusReqRtuSlave();
        return modbusReq;
    }

    /**
     * set modbus param
     *
     * @param modbusParam
     */
    public ModbusReqRtuSlave setParam(ModbusParam modbusParam) {
        this.modbusParam = modbusParam;
        return modbusReq;
    }

    /**
     * init modbus
     *
     * @throws ModbusInitException
     */
    public ModbusSlaveSet init(final OnRequestBack<String> onRequestBack, MatrizSlave image, String puerto, int baudrate, int dataBits, int stopbit, int parity) throws Exception {
        ModbusFactory mModbusFactory = new ModbusFactory();
        SerialPort serialPort = null;
        SerialPortWrapper serialPortWrapper = new JSerialCommWrapper(puerto,serialPort, baudrate, dataBits, stopbit, parity);
        serialPortWrapper.open();
        mModbusSlave = mModbusFactory.createRtuSlave(serialPortWrapper);
        //mModbusSlave = mModbusFactory.createRtuSlave(serialPortWrapper);
        mModbusSlave.addProcessImage(image);
        Handler mainHandler = new Handler(Looper.getMainLooper());
                try {
                    mModbusSlave.start();
                    Log.d(TAG, "Modbus4Android init success");
                    isInit = true;

                    // Pasar la llamada de éxito al hilo principal
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onRequestBack.onSuccess("Modbus4Android init success");
                        }
                    });
                } catch (ModbusInitException e) {
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
