package com.service.Comunicacion;

import com.service.Comunicacion.Modbus.ModbusMasterRtu;
import com.service.Comunicacion.Modbus.Req.ModbusReqRtuMaster;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GestorPuertoSerie {
    static GestorPuertoSerie Instance = null;
     public ModbusReqRtuMaster ModbusA = null,ModbusB = null,ModbusC = null;
    public PuertosSerie serialPortA=null,serialPortC=null,serialPortB=null;

    /**
     * Obtiene la instancia actual de `GestorPuertoSerie`.
     *
     * @return La instancia de `GestorPuertoSerie`.
     */
    public static GestorPuertoSerie getInstance(){
        if(Instance==null){
            Instance = new GestorPuertoSerie();
        }
        return Instance;
    }
    /**
     * Inicializa un puerto serie con los parámetros proporcionados.
     * Si el puerto ya ha sido inicializado, reutiliza la instancia existente.
     *
     * @param Puerto El nombre del puerto serie a inicializar (por ejemplo, "PuertoSerie 1", "PuertoSerie 2", "PuertoSerie 3").
     * @param baudrate La velocidad de transmisión en baudios.
     * @param databits El número de bits de datos.
     * @param stopbit El número de bits de parada (generalmente 1 o 2).
     * @param parity El tipo de paridad (Ninguna, Paridad Par, Paridad Impar).
     * @param flowcon El control de flujo (generalmente 0 para desactivado).
     * @param flags Otros flags relacionados con la configuración del puerto.
     * @return La instancia del objeto `PuertosSerie` inicializado.
     */
    public PuertosSerie initPuertoSerie(String Puerto, int baudrate, int databits, int stopbit, int parity, int flowcon, int flags){
        PuertosSerie puertoserie = new PuertosSerie();
        CountDownLatch latch = new CountDownLatch(1);
        switch (Puerto){
            case PuertosSerie.StrPortA:{
                if(serialPortA==null) {
                    puertoserie.open(Puerto,baudrate,stopbit,databits,parity,flowcon,flags);
                    serialPortA = puertoserie;
                }else{
                    puertoserie=serialPortA;
                }
                latch.countDown();
                break;
            }
            case PuertosSerie.StrPortB:{
                if(serialPortB==null) {
                    puertoserie.open(Puerto,baudrate,stopbit,databits,parity,flowcon,flags);
                    serialPortB = puertoserie;
                }else{
                    puertoserie=serialPortB;
                }
                latch.countDown();
                break;
            }
            case PuertosSerie.StrPortC:{
                if(serialPortC==null) {
                    puertoserie.open(Puerto,baudrate,stopbit,databits,parity,flowcon,flags);
                    serialPortC = puertoserie;
                }else{
                    puertoserie=serialPortC;
                }
                latch.countDown();
                break;
            }
        }
        if(puertoserie.get_Puerto()!=0){
            //   System.out.println( Puerto+" INICIALIZADO PUERTO"+baudrate+" "+stopbit+" "+databits+" "+parity);
        }
        try {
            latch.await(2000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
        return puertoserie;
    }
    /**
     * Inicializa la conexión Modbus en el puerto especificado con los parámetros dados.
     *
     * @param Port El puerto serie a utilizar para la conexión Modbus (puede ser "PuertoSerie 1", "PuertoSerie 2", "PuertoSerie 3").
     * @param Baud La velocidad de transmisión en baudios.
     * @param Stopbit El número de bits de parada (generalmente 1 o 2).
     * @param databit El número de bits de datos.
     * @param Parity El tipo de paridad (Ninguna, Paridad Par, Paridad Impar).
     * @return El objeto `ModbusReqRtuMaster` configurado con los parámetros dados.
     */
    public ModbusReqRtuMaster initializatemodbus(String Port, int Baud, int databit, int Stopbit, int Parity) {
        CountDownLatch latch = new CountDownLatch(1);
        ModbusReqRtuMaster Modbus = null;
        try {
            ModbusMasterRtu modbusMasterRtu = new ModbusMasterRtu();
            switch (Port) {
                case PuertosSerie.StrPortA: {
                    // if (ModbusA == null) {
                    ModbusA = modbusMasterRtu.init(Port, Baud, databit, Stopbit, Parity);
                    //}
                    Modbus = ModbusA;
                    break;
                }
                case PuertosSerie.StrPortB: {
                    //if (ModbusB == null) {
                    ModbusB = modbusMasterRtu.init(Port, Baud, databit, Stopbit, Parity);
                    // }
                    Modbus = ModbusB;
                    break;
                }
                case PuertosSerie.StrPortC: {
                    //  if (ModbusC == null) {
                    ModbusC = modbusMasterRtu.init(Port, Baud, databit, Stopbit, Parity);
                    // }
                    Modbus = ModbusC;
                    break;
                }
            }
        } catch (Exception e) {
              System.out.println("ERROR MODBUS" +e.getMessage());
        } finally {
//                System.out.println("SETEADO MODBUS");
            latch.countDown();
        }
        try {
            latch.await(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.getMessage();
        }
        //   System.out.println("INIT MODBUS "+ModbusA.get_Puerto());//+" "+ModbusB.get_Puerto()+" "+ModbusC.get_Puerto());
        return Modbus;
    }



}
