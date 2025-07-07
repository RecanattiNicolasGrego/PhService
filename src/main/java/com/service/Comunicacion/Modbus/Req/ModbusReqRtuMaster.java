package com.service.Comunicacion.Modbus.Req;

import static com.service.Comunicacion.PuertosSerie.PuertosSerie.StrPortA;
import static com.service.Comunicacion.PuertosSerie.PuertosSerie.StrPortB;
import static com.service.Comunicacion.PuertosSerie.PuertosSerie.StrPortC;

import android.util.Log;


import com.service.Comunicacion.Modbus.JSerialCommWrapper;
import com.service.Comunicacion.Modbus.modbus4And.ModbusFactory;
import com.service.Comunicacion.Modbus.modbus4And.ModbusMaster;
import com.service.Comunicacion.Modbus.modbus4And.exception.ModbusInitException;
import com.service.Comunicacion.Modbus.modbus4And.exception.ModbusTransportException;
import com.service.Comunicacion.Modbus.modbus4And.msg.ReadCoilsRequest;
import com.service.Comunicacion.Modbus.modbus4And.msg.ReadCoilsResponse;
import com.service.Comunicacion.Modbus.modbus4And.msg.ReadDiscreteInputsRequest;
import com.service.Comunicacion.Modbus.modbus4And.msg.ReadDiscreteInputsResponse;
import com.service.Comunicacion.Modbus.modbus4And.msg.ReadExceptionStatusRequest;
import com.service.Comunicacion.Modbus.modbus4And.msg.ReadExceptionStatusResponse;
import com.service.Comunicacion.Modbus.modbus4And.msg.ReadHoldingRegistersRequest;
import com.service.Comunicacion.Modbus.modbus4And.msg.ReadHoldingRegistersResponse;
import com.service.Comunicacion.Modbus.modbus4And.msg.ReadInputRegistersRequest;
import com.service.Comunicacion.Modbus.modbus4And.msg.ReadInputRegistersResponse;
import com.service.Comunicacion.Modbus.modbus4And.msg.ReportSlaveIdRequest;
import com.service.Comunicacion.Modbus.modbus4And.msg.ReportSlaveIdResponse;
import com.service.Comunicacion.Modbus.modbus4And.msg.WriteCoilRequest;
import com.service.Comunicacion.Modbus.modbus4And.msg.WriteCoilResponse;
import com.service.Comunicacion.Modbus.modbus4And.msg.WriteCoilsRequest;
import com.service.Comunicacion.Modbus.modbus4And.msg.WriteCoilsResponse;
import com.service.Comunicacion.Modbus.modbus4And.msg.WriteMaskRegisterRequest;
import com.service.Comunicacion.Modbus.modbus4And.msg.WriteMaskRegisterResponse;
import com.service.Comunicacion.Modbus.modbus4And.msg.WriteRegisterRequest;
import com.service.Comunicacion.Modbus.modbus4And.msg.WriteRegisterResponse;
import com.service.Comunicacion.Modbus.modbus4And.msg.WriteRegistersRequest;
import com.service.Comunicacion.Modbus.modbus4And.msg.WriteRegistersResponse;
import com.service.Comunicacion.Modbus.modbus4And.requset.ModbusParam;
import com.service.Comunicacion.Modbus.modbus4And.requset.OnRequestBack;
import com.service.Comunicacion.Modbus.modbus4And.serial.SerialPortWrapper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android_serialport_api.SerialPort;


/**
 * 创建者   zgkxzx
 * 创建日期 2017/6/12.
 * 功能描述
 */

public class ModbusReqRtuMaster {
    private final static String TAG = ModbusReqRtuMaster.class.getSimpleName();

    private static ModbusReqRtuMaster modbusReq;
    public String Puerto="";
    private ModbusMaster mModbusMaster;
    private ModbusParam modbusParam = new ModbusParam();

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    private boolean isInit = false;

    private ModbusReqRtuMaster() {

    }

    /**
     * get modbus instance
     *
     * @return
     */
    public static synchronized ModbusReqRtuMaster getInstance() {
        if (modbusReq == null)
            modbusReq = new ModbusReqRtuMaster();
        return modbusReq;
    }

    public int get_Puerto(){
        switch(Puerto){
            case StrPortA:{
                return 1;
            }
            case StrPortB:{
                return 2;
            }
            case StrPortC:{
                return 3;
            }
        }
        return 0;
    }
    /**
     * set modbus param
     *
     * @param modbusParam
     */
    public ModbusReqRtuMaster setParam(ModbusParam modbusParam) {
        this.modbusParam = modbusParam;
        return modbusReq;
    }
    SerialPortWrapper serialPortWrapper=null;


    /**
     * init modbus
     *
     * @throws ModbusInitException
     */
    public void init(final OnRequestBack<String> onRequestBack, String puerto, int baudrate, int dataBits, int stopbit, int parity) throws Exception {
        ModbusFactory mModbusFactory = new ModbusFactory();
        executorService = Executors.newFixedThreadPool(1);
       // SerialPort serialPort = SerialPort.getCommPort("/dev/ttyXRUSB2"); // Ejemplo: Obtener una instancia de SerialPort con el puerto serie deseado
      //  int baudRate = 9600; // Ejemplo: Velocidad de transmisión (baud rate)

        SerialPort serialPort = null;
        Puerto=puerto;
        serialPortWrapper = new JSerialCommWrapper(puerto,serialPort, baudrate, dataBits, stopbit, parity);
        serialPortWrapper.open();
        mModbusMaster = mModbusFactory.createRtuMaster(serialPortWrapper);
        //mModbusMaster = mModbusFactory.createTcpMaster(params, modbusParam.keepAlive);
        mModbusMaster.setRetries(modbusParam.retries);
        mModbusMaster.setTimeout(modbusParam.timeout);
        if (!executorService.isShutdown()) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    mModbusMaster.init();
                } catch (ModbusInitException e) {
                    mModbusMaster.destroy();
                    ModbusReqRtuMaster.getInstance().destroy();
                    isInit = false;
                    Log.d(TAG, "Modbus4Android init failed " + e);
                    onRequestBack.onFailed("Modbus4Android init failed ");
                }
                Log.d(TAG, "Modbus4Android init success");
                isInit = true;
                onRequestBack.onSuccess("Modbus4Android init success");

            }
        });
        }

    }

    /**
     * destory the modbus4Android instance
     */
    public void destroy() {
        try {
            executorService.shutdown();
            if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {

        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            executorService = Executors.newFixedThreadPool(1);// Detener el hilo de ejecución si está en uso
        }
        if (mModbusMaster != null) {
            try {
                mModbusMaster.destroy();
                ModbusReqRtuMaster.getInstance().destroy();
                // Destruir la instancia de ModbusMaster
            } catch (Exception e) {
                Log.e(TAG, "Error al destruir ModbusMaster: " + e.getMessage());
            }
        }



        // Cerrar puerto serial si es necesario
        if (serialPortWrapper != null) {
            try {
                serialPortWrapper.close();  // Cerrar el puerto serial
                serialPortWrapper=null;
            } catch (Exception e) {
                Log.e(TAG, "Error al cerrar el puerto serial: " + e.getMessage());
            }
        }

        // Aquí podrías agregar lógica adicional si es necesario, como limpiar otros recursos.
        isInit = false;
    }



    /**
     * Function Code 1
     * Read Coil Register
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param start         start address
     * @param len           length
     */
    public void readCoil(final OnRequestBack<boolean[]> onRequestBack, final int slaveId, final int start, final int len) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }
        if (!executorService.isShutdown()) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        ReadCoilsRequest request = new ReadCoilsRequest(slaveId, start, len);
                        ReadCoilsResponse response = (ReadCoilsResponse) mModbusMaster.send(request);

                        if (response.isException())
                            onRequestBack.onFailed(response.getExceptionMessage());
                        else {
                            boolean[] booleanData = response.getBooleanData();
                            boolean[] resultByte = new boolean[len];
                            System.arraycopy(booleanData, 0, resultByte, 0, len);
                            onRequestBack.onSuccess(resultByte);
                        }

                    } catch (ModbusTransportException e) {
                        e.printStackTrace();
                        onRequestBack.onFailed(e.toString());
                    }

                }
            });
        }

    }

    /**
     * Function Code 2
     * Read DiscreteInput Register
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param start         start address
     * @param len           length
     */
    public void readDiscreteInput(final OnRequestBack<boolean[]> onRequestBack, final int slaveId, final int start, final int len) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }

        if (!executorService.isShutdown()) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        ReadDiscreteInputsRequest request = new ReadDiscreteInputsRequest(slaveId, start, len);
                        ReadDiscreteInputsResponse response = (ReadDiscreteInputsResponse) mModbusMaster.send(request);

                        if (response.isException())
                            onRequestBack.onFailed(response.getExceptionMessage());
                        else {
                            boolean[] booleanData = response.getBooleanData();
                            boolean[] resultByte = new boolean[len];
                            System.arraycopy(booleanData, 0, resultByte, 0, len);
                            onRequestBack.onSuccess(resultByte);
                        }
                    } catch (ModbusTransportException e) {
                        e.printStackTrace();
                        onRequestBack.onFailed(e.toString());
                    }

                }
            });
        }

    }

    /**
     * Function Code 3
     * Read Holding Registers
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param start         start address
     * @param len           length
     */
    public void readHoldingRegisters(final OnRequestBack<short[]> onRequestBack, final int slaveId, final int start, final int len) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }
        if (!executorService.isShutdown()) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        ReadHoldingRegistersRequest request = new ReadHoldingRegistersRequest(slaveId, start, len);
                        ReadHoldingRegistersResponse response = (ReadHoldingRegistersResponse) mModbusMaster.send(request);

                        if (response.isException())
                            onRequestBack.onFailed(response.getExceptionMessage());
                        else
                            onRequestBack.onSuccess(response.getShortData());
                    } catch (ModbusTransportException e) {
                        e.printStackTrace();
                        onRequestBack.onFailed(e.toString());
                    }

                }
            });
        }
    }

    /**
     * Function Code 4
     * Read Input Registers
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param start         start address
     * @param len           length
     */
    public void readInputRegisters(final OnRequestBack<short[]> onRequestBack, final int slaveId, final int start, final int len) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }
        if (!executorService.isShutdown()) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        ReadInputRegistersRequest request = new ReadInputRegistersRequest(slaveId, start, len);
                        ReadInputRegistersResponse response = (ReadInputRegistersResponse) mModbusMaster.send(request);

                        if (response.isException())
                            onRequestBack.onFailed(response.getExceptionMessage());
                        else
                            onRequestBack.onSuccess(response.getShortData());
                    } catch (ModbusTransportException e) {
                        e.printStackTrace();
                        onRequestBack.onFailed(e.toString());
                    }
                }
            });
        }
    }

    /**
     * Function Code 5
     * Write Coil
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param offset        offset address
     * @param value         value
     */
    public void writeCoil(final OnRequestBack<String> onRequestBack, final int slaveId, final int offset, final boolean value) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }
        if (!executorService.isShutdown()) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        WriteCoilRequest request = new WriteCoilRequest(slaveId, offset, value);
                        WriteCoilResponse response = (WriteCoilResponse) mModbusMaster.send(request);

                        if (response.isException())
                            onRequestBack.onFailed(response.getExceptionMessage());
                        else
                            onRequestBack.onSuccess("Success");
                    } catch (ModbusTransportException e) {
                        e.printStackTrace();
                        onRequestBack.onFailed(e.toString());
                    }
                }
            });
        }
    }

    /**
     * Function Code 15
     * Write Coils
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param start         start address
     * @param values        values
     */
    public void writeCoils(final OnRequestBack<String> onRequestBack, final int slaveId, final int start, final boolean[] values) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }
        if (!executorService.isShutdown()) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        WriteCoilsRequest request = new WriteCoilsRequest(slaveId, start, values);
                        WriteCoilsResponse response = (WriteCoilsResponse) mModbusMaster.send(request);

                        if (response.isException())
                            onRequestBack.onFailed(response.getExceptionMessage());
                        else
                            onRequestBack.onSuccess("Success");
                    } catch (ModbusTransportException e) {
                        e.printStackTrace();
                        onRequestBack.onFailed(e.toString());
                    }
                }
            });
        }
    }

    /**
     * Function Code 6
     * Write Register
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param offset        offset address
     * @param value         value
     */
    public void writeRegister(final OnRequestBack<String> onRequestBack, final int slaveId, final int offset, final int value) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }
        if (!executorService.isShutdown()) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        WriteRegisterRequest request = new WriteRegisterRequest(slaveId, offset, value);
                        WriteRegisterResponse response = (WriteRegisterResponse) mModbusMaster.send(request);

                        if (response.isException())
                            onRequestBack.onFailed(response.getExceptionMessage());
                        else
                            onRequestBack.onSuccess("Success");
                    } catch (ModbusTransportException e) {
                        e.printStackTrace();
                        onRequestBack.onFailed(e.toString());
                    }
                }
            });
        }
    }


    /**
     * Function Code 16
     * Write Registers
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param start         start address
     * @param values        values
     */
    public void writeRegisters(final OnRequestBack<String> onRequestBack, final int slaveId, final int start, final short[] values) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }
        if (!executorService.isShutdown()) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        WriteRegistersRequest request = new WriteRegistersRequest(slaveId, start, values);
                        WriteRegistersResponse response = (WriteRegistersResponse) mModbusMaster.send(request);

                        if (response.isException())
                            onRequestBack.onFailed(response.getExceptionMessage());
                        else
                            onRequestBack.onSuccess("Success");
                    } catch (ModbusTransportException e) {
                        e.printStackTrace();
                        onRequestBack.onFailed(e.toString());
                    }
                }
            });
        }
    }

    /**
     * Function Code 7
     * Read Exceptioin Status
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     */
    public void readExceptionStatus(final OnRequestBack<Byte> onRequestBack, final int slaveId) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }
        if (!executorService.isShutdown()) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        ReadExceptionStatusRequest request = new ReadExceptionStatusRequest(slaveId);
                        ReadExceptionStatusResponse response = (ReadExceptionStatusResponse) mModbusMaster.send(request);

                        if (response.isException())
                            onRequestBack.onFailed(response.getExceptionMessage());
                        else
                            onRequestBack.onSuccess(response.getExceptionStatus());
                    } catch (ModbusTransportException e) {
                        e.printStackTrace();
                        onRequestBack.onFailed(e.toString());
                    }
                }
            });
        }
    }

    /**
     * Function Code 17
     * Report Slave Id
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     */
    public void reportSlaveId(final OnRequestBack<byte[]> onRequestBack, final int slaveId) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }
        if (!executorService.isShutdown()) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        ReportSlaveIdRequest request = new ReportSlaveIdRequest(slaveId);
                        ReportSlaveIdResponse response = (ReportSlaveIdResponse) mModbusMaster.send(request);

                        if (response.isException())
                            onRequestBack.onFailed(response.getExceptionMessage());
                        else
                            onRequestBack.onSuccess(response.getData());
                    } catch (ModbusTransportException e) {
                        e.printStackTrace();
                        onRequestBack.onFailed(e.toString());
                    }
                }
            });
        }
    }

    /**
     * Function Code 22
     * Mask Write 4X Register
     *
     * @param onRequestBack callback
     * @param slaveId       slave id
     * @param offset        offset address
     * @param and           and
     * @param and           or
     */
    public void writeMaskRegister(final OnRequestBack<String> onRequestBack, final int slaveId, final int offset, final int and, final int or) {
        if (!isInit) {
            onRequestBack.onFailed("Modbus master is not inited successfully...");
            return;
        }
        if (!executorService.isShutdown()) {
            executorService.submit(new Runnable() {

                @Override
                public void run() {
                    try {
                        WriteMaskRegisterRequest request = new WriteMaskRegisterRequest(slaveId, offset, and, or);
                        WriteMaskRegisterResponse response = (WriteMaskRegisterResponse) mModbusMaster.send(request);

                        if (response.isException())
                            onRequestBack.onFailed(response.getExceptionMessage());
                        else
                            onRequestBack.onSuccess("Success");
                    } catch (ModbusTransportException e) {
                        e.printStackTrace();
                        onRequestBack.onFailed(e.toString());
                    }
                }
            });
        }
    }
}
