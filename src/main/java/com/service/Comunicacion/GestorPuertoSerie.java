package com.service.Comunicacion;

import com.service.Comunicacion.Modbus.ModbusMasterRtu;
import com.service.Comunicacion.Modbus.ModbusMasterTCP;
import com.service.Comunicacion.Modbus.Req.BasicProcessImageSlave;
import com.service.Comunicacion.Modbus.Req.ModbusReqRtuMaster;
import com.service.Comunicacion.Modbus.Req.ModbusReqRtuSlave;
import com.service.Comunicacion.Modbus.Req.ModbusReqTCPslave;
import com.service.Comunicacion.Modbus.modbus4And.Modbus;
import com.service.Comunicacion.Modbus.modbus4And.ModbusSlaveSet;
import com.service.Comunicacion.Modbus.modbus4And.exception.ModbusInitException;
import com.service.Comunicacion.Modbus.modbus4And.requset.ModbusReq;
import com.service.Comunicacion.Modbus.modbus4And.requset.OnRequestBack;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GestorPuertoSerie {
    static GestorPuertoSerie Instance = null;
    public ModbusReqRtuMaster ModbusMRA = null, ModbusMRB = null, ModbusMRC = null;
    public ModbusSlaveSet ModbusSTA = null;

    public ModbusSlaveSet ModbusSRA = null, ModbusSRB = null, ModbusSRC = null;
    public PuertosSerie serialPortA = null, serialPortC = null, serialPortB = null;

    /**
     * Obtiene la instancia actual de `GestorPuertoSerie`.
     *
     * @return La instancia de `GestorPuertoSerie`.
     */
    public static GestorPuertoSerie getInstance() {
        if (Instance == null) {
            Instance = new GestorPuertoSerie();
        }
        return Instance;
    }

    /**
     * Inicializa un puerto serie con los parámetros proporcionados.
     * Si el puerto ya ha sido inicializado, reutiliza la instancia existente.
     *
     * @param Puerto   El nombre del puerto serie a inicializar (por ejemplo, "PuertoSerie 1", "PuertoSerie 2", "PuertoSerie 3").
     * @param baudrate La velocidad de transmisión en baudios.
     * @param databits El número de bits de datos.
     * @param stopbit  El número de bits de parada (generalmente 1 o 2).
     * @param parity   El tipo de paridad (Ninguna, Paridad Par, Paridad Impar).
     * @param flowcon  El control de flujo (generalmente 0 para desactivado).
     * @param flags    Otros flags relacionados con la configuración del puerto.
     * @return La instancia del objeto `PuertosSerie` inicializado.
     */
    public PuertosSerie initPuertoSerie(String Puerto, int baudrate, int databits, int stopbit, int parity, int flowcon, int flags) {
        synchronized (Puerto) {
            PuertosSerie puertoserie = new PuertosSerie();
            CountDownLatch latch = new CountDownLatch(1);
            switch (Puerto) {
                case PuertosSerie.StrPortA: {
                    if (serialPortA == null) {
                        puertoserie.open(Puerto, baudrate, stopbit, databits, parity, flowcon, flags);
                        serialPortA = puertoserie;
                    } else {
                        puertoserie = serialPortA;
                    }
                    latch.countDown();
                    break;
                }
                case PuertosSerie.StrPortB: {
                    if (serialPortB == null) {
                        puertoserie.open(Puerto, baudrate, stopbit, databits, parity, flowcon, flags);
                        serialPortB = puertoserie;
                    } else {
                        puertoserie = serialPortB;
                    }
                    latch.countDown();
                    break;
                }
                case PuertosSerie.StrPortC: {
                    if (serialPortC == null) {
                        puertoserie.open(Puerto, baudrate, stopbit, databits, parity, flowcon, flags);
                        serialPortC = puertoserie;
                    } else {
                        puertoserie = serialPortC;
                    }
                    latch.countDown();
                    break;
                }
            }
            if (puertoserie.get_Puerto() != 0) {
                //   System.out.println( Puerto+" INICIALIZADO PUERTO"+baudrate+" "+stopbit+" "+databits+" "+parity);
            }
            try {
                latch.await(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return puertoserie;
        }
    }

    /**
     * Inicializa la conexión Modbus en el puerto especificado con los parámetros dados.
     *
     * @param Port    El puerto serie a utilizar para la conexión Modbus (puede ser "PuertoSerie 1", "PuertoSerie 2", "PuertoSerie 3").
     * @param Baud    La velocidad de transmisión en baudios.
     * @param Stopbit El número de bits de parada (generalmente 1 o 2).
     * @param databit El número de bits de datos.
     * @param Parity  El tipo de paridad (Ninguna, Paridad Par, Paridad Impar).
     * @return El objeto `ModbusReqRtuMaster` configurado con los parámetros dados.
     */
    public ModbusReqRtuMaster initializateMasterRTUmodbus(String Port, int Baud, int databit, int Stopbit, int Parity)  {
        synchronized (Port) {
                CountDownLatch latch = new CountDownLatch(1);
                ModbusReqRtuMaster Modbus = null;
                try {
                    ModbusMasterRtu modbusMasterRtu = new ModbusMasterRtu();
                    switch (Port) {
                        case PuertosSerie.StrPortA: {
                            if (ModbusMRA == null) {
                                ModbusMRA = modbusMasterRtu.init(Port, Baud, databit, Stopbit, Parity);
                            }
                            Modbus = ModbusMRA;
                            break;
                        }
                        case PuertosSerie.StrPortB: {
                            if (ModbusMRB == null) {
                                ModbusMRB = modbusMasterRtu.init(Port, Baud, databit, Stopbit, Parity);
                            }
                            Modbus = ModbusMRB;
                            break;
                        }
                        case PuertosSerie.StrPortC: {
                            if (ModbusMRC == null) {
                                ModbusMRC = modbusMasterRtu.init(Port, Baud, databit, Stopbit, Parity);
                            }
                            Modbus = ModbusMRC;
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("ERROR MODBUS" + e.getMessage());
                } finally {
//                System.out.println("SETEADO MODBUS");
                    latch.countDown();
                }
                try {
                    latch.await(3000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.getMessage();
                }
                //   System.out.println("INIT MODBUS "+ModbusA.get_Puerto());//+" "+ModbusB.get_Puerto()+" "+ModbusC.get_Puerto());
                return Modbus;

            }
    }
    public ModbusReq initializateMasterTCPmodbus(String IP,OnRequestBack<String> Callback) {
            CountDownLatch latch = new CountDownLatch(1);
            ModbusReq Modbus =null;
            try {
                            Modbus = ModbusMasterTCP.init(IP, new OnRequestBack<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    Callback.onSuccess(s);
                                    latch.countDown();

                                }

                                @Override
                                public void onFailed(String msg) {
                                    Callback.onFailed(msg);
                                    latch.countDown();

                                }
                            });
            } catch (Exception e) {
                System.out.println("ERROR MODBUS" + e.getMessage());
            }
            try {
                latch.await(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.getMessage();
            }
            return Modbus;
    }
    public ModbusSlaveSet initializateSlaveTCPmodbus(String IP,BasicProcessImageSlave img,OnRequestBack<String> Callback) throws ModbusInitException {
       synchronized (IP) {
           CountDownLatch latch = new CountDownLatch(1);
           ModbusSlaveSet Modbus = null;
           System.out.println(" CAMINOS ? "+(ModbusSTA == null));
           if (ModbusSTA == null) {
               try {
                   Modbus = ModbusReqTCPslave.getInstance().init(new OnRequestBack<String>() {
                       @Override
                       public void onSuccess(String s) {
                           latch.countDown();
                       }

                       @Override
                       public void onFailed(String msg) {
                           latch.countDown();
                       }
                   }, img, IP);
                   ModbusSTA = Modbus;
               } catch (ModbusInitException e) {

                   System.out.println("ERROR MODBUS" + e.getMessage());
                   throw e;
               }
           } else {
               ModbusSTA.addProcessImage(img);
               Modbus = ModbusSTA;
               latch.countDown();
           }
           try {
               latch.await(2000, TimeUnit.MILLISECONDS);
           } catch (InterruptedException e) {
               Thread.currentThread().interrupt();
               e.getMessage();
           }
           return Modbus;
       }
    }
        public ModbusSlaveSet initializateSlaveRTUmodbus (String Port, int Baud, int databit, int Stopbit, int Parity, BasicProcessImageSlave img,OnRequestBack<String> Callback) {
            synchronized (Port) {
                CountDownLatch latch = new CountDownLatch(1);
                ModbusSlaveSet Modbus = null;
                try {
                    switch (Port) {
                        case PuertosSerie.StrPortA: {
                            if (ModbusSRA == null) {
                                ModbusSRA = ModbusReqRtuSlave.getInstance().init(new OnRequestBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        Callback.onSuccess(s);
                                        latch.countDown();

                                    }

                                    @Override
                                    public void onFailed(String msg) {
                                        Callback.onFailed(msg);
                                        latch.countDown();

                                    }
                                },img,Port, Baud, databit, Stopbit, Parity);
                            }else{
                                ModbusSRA.addProcessImage(img);
                            }
                            break;
                        }
                        case PuertosSerie.StrPortB: {
                            if (ModbusSRB == null) {
                                ModbusSRB = ModbusReqRtuSlave.getInstance().init(new OnRequestBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        Callback.onSuccess(s);
                                        latch.countDown();

                                    }

                                    @Override
                                    public void onFailed(String msg) {
                                        Callback.onFailed(msg);
                                        latch.countDown();

                                    }
                                },img, Port, Baud, databit, Stopbit, Parity);
                            }else{
                                ModbusSRB.addProcessImage(img);
                            }
                            break;
                        }
                        case PuertosSerie.StrPortC: {
                            if (ModbusSRC == null) {
                                ModbusSRC = ModbusReqRtuSlave.getInstance().init(new OnRequestBack<String>() {
                                    @Override
                                    public void onSuccess(String s) {
                                        Callback.onSuccess(s);
                                        latch.countDown();

                                    }

                                    @Override
                                    public void onFailed(String msg) {
                                        Callback.onFailed(msg);
                                        latch.countDown();

                                    }
                                },img, Port, Baud, databit, Stopbit, Parity);
                            }else{
                                ModbusSRC.addProcessImage(img);
                            }
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.out.println("ERROR MODBUS" + e.getMessage());
                }
                try {
                    latch.await(3000, TimeUnit.MILLISECONDS);
                    switch (Port) {
                        case PuertosSerie.StrPortA: {
                            Modbus = ModbusSRA;
                            break;
                        }
                        case PuertosSerie.StrPortB: {
                            Modbus = ModbusSRB;
                            break;

                        }
                        case PuertosSerie.StrPortC: {
                            Modbus = ModbusSRC;
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.getMessage();
                }
                return Modbus;
            }
        }
    }
