package com.service.Devices.Dispositivos.Clases;
import com.service.Comunicacion.GestorPuertoSerie;
import com.service.Comunicacion.Modbus.Req.BasicProcessImageSlave;
import com.service.Comunicacion.Modbus.Req.ModbusReqRtuSlave;
import com.service.Comunicacion.Modbus.Req.ModbusReqTCPslave;
import com.service.Comunicacion.Modbus.modbus4And.ModbusSlaveSet;
import com.service.Comunicacion.Modbus.modbus4And.ProcessImageListener;
import com.service.Comunicacion.Modbus.modbus4And.exception.IllegalDataAddressException;
import com.service.Comunicacion.Modbus.modbus4And.exception.ModbusInitException;
import com.service.Comunicacion.Modbus.modbus4And.requset.OnRequestBack;
import com.service.Interfaz.Dispositivo;
import com.service.Interfaz.dispositivoBase;
import com.service.estructuras.classDevice;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class SlaveDispositivos  extends DispositivoBase implements Dispositivo, dispositivoBase.Modbus.Slave {
    private ModbusSlaveSet SlaveTCP;
    private Modbus.Slave.DeviceMessageListenerM_Slave listener;
    private ModbusSlaveSet SlaveRTU;
    Boolean  isinit=false;
    public static Boolean  puede485=true;

    static int limit = ((int) ((int)Short.MAX_VALUE + 1) * 2);
    public SlaveDispositivos(String strpuerto, classDevice Device,int ndevice){
        super(strpuerto,Device,ndevice);
    }


    static BasicProcessImageSlave getModscanProcessImage(BasicProcessImageSlave image, int ndevice, DeviceMessageListenerM_Slave listener) {

        BasicProcessImageSlave processImage = image;//getBasicProcessImage(slaveId);
        // Add an image listener.
        processImage.addListener(new ProcessImageListener() {
            @Override
            public void coilWrite(int i, boolean b, boolean b1) {
                listener.CoilChange(ndevice, i+1, b, b1);
            }

            @Override
            public void holdingRegisterWrite(int i, short i1, short i2) {
                listener.RegisterChange(ndevice, i+1, i1, i2);
            }
        });
        return processImage;
    }



    @Override
    public Boolean publicarCoil( Integer registro, Boolean valor) {
        if(isinit) {
            try {
                short x = 0;
                if (valor) {
                    x = 1;
                }
                if (Device.getSalida().equals("Red")) {
                    SlaveRTU.getProcessImage(Slaveid).writeHoldingRegister(registro, x);
                } else {
                    SlaveRTU.getProcessImage(Slaveid).writeHoldingRegister(registro, x);
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public String leerHoldingRegister(Integer registro, ClasesModbus clase) {
        if(isinit) {
            try {
                switch (clase) {
                    case Long: {
                        long x = 0;
                        if (Device.getSalida().equals("Red")) {
                            x = SlaveTCP.getProcessImage(Slaveid).getHoldingRegister(registro) & 0xFFFF;
                            x += ((long) (limit) * (SlaveTCP.getProcessImage(Slaveid).getHoldingRegister(registro + 1)));
                        } else {
                            x = SlaveRTU.getProcessImage(Slaveid).getHoldingRegister(registro) & 0xFFFF;
                            x += ((long) (limit) * (SlaveRTU.getProcessImage(Slaveid).getHoldingRegister(registro + 1)));
                        }
                        if (x > Long.MAX_VALUE) {
                            x = Long.MAX_VALUE - x;
                        }
                        return String.valueOf(x);
                    }
                    case Unsigned_Int: {
                        Integer x = 0;
                        if (Device.getSalida().equals("Red")) {
                            x = SlaveTCP.getProcessImage(Slaveid).getHoldingRegister(registro) & 0xFFFF;
                        } else {
                            x = SlaveRTU.getProcessImage(Slaveid).getHoldingRegister(registro) & 0xFFFF;
                        }
                        return String.valueOf(x);
                    }
                    case Signed_Int: {
                        short x = 0;
                        if (Device.getSalida().equals("Red")) {
                            x = SlaveTCP.getProcessImage(Slaveid).getHoldingRegister(registro);
                        } else {
                            x = SlaveRTU.getProcessImage(Slaveid).getHoldingRegister(registro);
                        }
                        return String.valueOf(x);
                    }
                    case Float: {
                        Integer a = 0;
                        Integer b = 0;
                        if (Device.getSalida().equals("Red")) {
                            a = SlaveTCP.getProcessImage(Slaveid).getHoldingRegister(registro) & 0xFFFF;
                            b = SlaveTCP.getProcessImage(Slaveid).getHoldingRegister(registro + 1) & 0xFFFF;
                        } else {
                            a = SlaveRTU.getProcessImage(Slaveid).getHoldingRegister(registro) & 0xFFFF;
                            b = SlaveRTU.getProcessImage(Slaveid).getHoldingRegister(registro + 1) & 0xFFFF;
                        }
                        int bits = (b << 16) | a;
                        return String.valueOf(Float.intBitsToFloat(bits));
                    }
                    default:
                        return null;
                }
            } catch (IllegalDataAddressException e) {
                return null;
            }
        }
        return null;
    }
    @Override
    public Boolean leerCoil(Integer registro) {
        if(isinit) {
            try {
                Boolean x = false;
                if (Device.getSalida().equals("Red")) {
                    x = SlaveTCP.getProcessImage(Slaveid).getCoil(registro);
                } else {
                    x = SlaveRTU.getProcessImage(Slaveid).getCoil(registro);
                }
                return x;//String.valueOf(x);

            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
    @Override
    public BasicProcessImageSlave getImageBasic() {
        System.out.println("SLAVE ID ?!?!?!"+ Slaveid);
        return new BasicProcessImageSlave(Slaveid);
    }
    @Override
    public void init(DeviceMessageListenerM_Slave ListenerM_Slave, BasicProcessImageSlave image) {
        System.out.println(("OLA !?!?")+ (image == null) );
        if(image.getSlaveId() == Device.getID()){
            CountDownLatch latch = new CountDownLatch(1);
            listener = ListenerM_Slave;
            if (strpuerto != null) {
                if (Device.getSalida().equals("Red")) {
                    try {
                               try {
                                   SlaveTCP = GestorPuertoSerie.getInstance().initializateSlaveTCPmodbus(Device.getDireccion().get(0),getModscanProcessImage(image, ndevice, listener),new OnRequestBack<String>() {
                                       @Override
                                       public void onSuccess(String s) {
                                           System.out.println(s);
                                           isinit = true;
                                           latch.countDown();
                                       }

                                       @Override
                                       public void onFailed(String s) {
                                           System.out.println(s);
                                           latch.countDown();
                                       }
                                   });
                               } catch (ModbusInitException e) {
                                   System.out.println("ERROR MODBUS Init Red "+e.getMessage());
                               }
                    } catch (Exception e) {
                    }
                } else {
                try {
                    SlaveRTU = GestorPuertoSerie.getInstance().initializateSlaveRTUmodbus(strpuerto, Integer.parseInt(Device.getDireccion().get(0)), Integer.parseInt(Device.getDireccion().get(1)), Integer.parseInt(Device.getDireccion().get(2)), Integer.parseInt(Device.getDireccion().get(3)),
                            getModscanProcessImage(image, ndevice, listener),new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            isinit = true;
                            latch.countDown();
                        }

                        @Override
                        public void onFailed(String s) {
                            latch.countDown();
                        }
                    });
                } catch (Exception e) {
                    System.out.println("ERROR MODBUS Init  RTU"+e.getMessage());
                }}
            }
            try {
                latch.await(2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {

            }
        }else{
          //  Utils.Mensaje("ERROR Modbus imageSlave != Device ID", R.layout.item_customtoasterror, ComService.getInstance().activity);
        }
    }
    @Override
    public Boolean publicarHoldingRegister(Integer registro, ClasesModbus clase, String valor) {
        if(isinit) {
            try {
                //dependiendo de la @param clase format value
                // long int, float con y sin signo
                byte[] value = new byte[0];
                switch (clase) {
                    case Long: {
                        long x = Long.parseLong(valor);

                        if (x >= 0) {
                            long low = ((x % limit));
                            long high = (x / limit);
                            if (Device.getSalida().equals("Red")) {
                                SlaveTCP.getProcessImage(Slaveid).setHoldingRegister(registro, (short) low);
                                SlaveTCP.getProcessImage(Slaveid).setHoldingRegister(registro + 1, (short) high);
                            } else {
                                SlaveRTU.getProcessImage(Slaveid).setHoldingRegister(registro, (short) low);
                                SlaveRTU.getProcessImage(Slaveid).setHoldingRegister(registro + 1, (short) high);
                            }
                        } else {
                            long lowx = ((limit) + (x % limit));
                            long highx = (limit - 1) + (x / limit);
                            if (lowx < (limit) && highx < (limit)) {
                                if (Device.getSalida().equals("Red")) {
                                    SlaveTCP.getProcessImage(Slaveid).setHoldingRegister(registro, (short) lowx);
                                    SlaveTCP.getProcessImage(Slaveid).setHoldingRegister(registro + 1, (short) highx);
                                } else {
                                    SlaveRTU.getProcessImage(Slaveid).setHoldingRegister(registro, (short) lowx);
                                    SlaveRTU.getProcessImage(Slaveid).setHoldingRegister(registro + 1, (short) highx);
                                }
                            }
                        }
                        long limitmod = 2147483646;
                        if (x > (-1 * limitmod) && x < limitmod) {
                            return true;
                        } else {
                            System.out.println("ERROR MODBUS, LONG FUERA DE RANGO   ----   Rango: " + Long.MIN_VALUE + "-" + Long.MAX_VALUE + " Valor: " + valor);
                            return false;
                        }
                    }
                    case Float: {
                    /*
                    * int restoredValue = (high << 16) | (low & 0xFFFF);
    float restoredFloat = Float.intBitsToFloat(restoredValue);
                    * */
                        //  Float x =Utils.ModbusFormatter.floatToIEEE754(Float.parseFloat(valor));
                        int valuex = Float.floatToIntBits(Float.parseFloat(valor));// value = Utils.ModbusFormatter.formatValue(x,x<0);
                        int high = (valuex >> 16) & 0xFFFF;  // Los primeros 16 bits (bits altos)
                        int low = valuex & 0xFFFF;
                        if (Device.getSalida().equals("Red")) {
                            SlaveTCP.getProcessImage(Slaveid).setHoldingRegister(registro, (short) low);
                            SlaveTCP.getProcessImage(Slaveid).setHoldingRegister(registro + 1, (short) (high));
                        } else {

                            SlaveRTU.getProcessImage(Slaveid).setHoldingRegister(registro, (short) low);
                            SlaveRTU.getProcessImage(Slaveid).setHoldingRegister(registro + 1, (short) (high));
                        }

                        if (Float.parseFloat(valor) > Float.MIN_VALUE && Float.parseFloat(valor) < Float.MAX_VALUE) {
                            return true;
                        } else {
                            System.out.println("ERROR MODBUS, FLOAT FUERA DE RANGO   ----   Rango: " + Float.MIN_VALUE + "-" + Float.MAX_VALUE + " Valor: " + valor);
                            return false;
                        }
                    }
                    case Signed_Int:
                    case Unsigned_Int: {
                        if (Device.getSalida().equals("Red")) {
                            SlaveTCP.getProcessImage(Slaveid).setHoldingRegister(registro, (short) (Integer.parseInt(valor)));
                        } else {
                            SlaveRTU.getProcessImage(Slaveid).setHoldingRegister(registro, (short) (Integer.parseInt(valor)));
                        }
                        Integer limitemod1 = 0;
                        Integer limitemod2 = 0;
                        if (clase == ClasesModbus.Signed_Int) {
                            limitemod1 = (limit / 2) - 1;
                            limitemod2 = (-1 * limit / 2);
                        } else {
                            limitemod1 = limit - 1;
                            limitemod2 = 0;
                        }
                        if (Integer.parseInt(valor) < limitemod1 && Integer.parseInt(valor) > limitemod2) {
                            return true;
                        } else {
                            System.out.println("ERROR MODBUS, " + clase.getTipo() + " FUERA DE RANGO    ----   Rango: " + limitemod2 + "-" + limitemod1 + " Valor: " + valor);
                            return false;
                        }
                    }
                    default:
                        return false;
                }
            } catch (NumberFormatException e) {
                System.out.println("ERROR MODBUS, " + e.getMessage());
                return false;
            }
        }
        return false;
    }
    @Override
    public void stop() {
        if (Device.getSalida().equals("Red")){
            try {
                SlaveTCP.stop();
            } catch (Exception e) {
            }
        }else{
            try {
                SlaveRTU.stop();
            } catch (Exception e) {

            }
        }
    }
}
