package com.service.Devices.Dispositivos.Clases;

import com.service.Comunicacion.GestorPuertoSerie;
import com.service.Comunicacion.Modbus.Req.ModbusReqRtuMaster;
import com.service.Comunicacion.Modbus.modbus4And.requset.ModbusReq;
import com.service.Comunicacion.Modbus.modbus4And.requset.OnRequestBack;
import com.service.Interfaz.Dispositivo;
import com.service.Interfaz.dispositivoBase;
import com.service.utilsPackage.Utils;
import com.service.estructuras.classDevice;

import java.util.concurrent.CountDownLatch;

public class MasterDispositivos  extends DispositivoBase implements Dispositivo, dispositivoBase.Modbus.Master {
    private ModbusReqRtuMaster ModbusRtuMaster;
    private ModbusReq ModbusTCPMaster;
    Boolean isinit =false;

    public static Boolean   puede485=true;
    static int limit = ((int) ((int)Short.MAX_VALUE + 1) * 2);
    public MasterDispositivos(String strpuerto, classDevice Device,int ndevice) {
        super(strpuerto, Device, ndevice);
    }
    @Override
    public void init() {
        if (strpuerto != null) {
            if (Device.getSalida().equals("Red")) {
                try {
                    ModbusTCPMaster = GestorPuertoSerie.getInstance().initializateMasterTCPmodbus(Device.getDireccion().get(0), new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            isinit=true;
                        }

                        @Override
                        public void onFailed(String s) {

                        }
                    });
                } catch (Exception e) {
                    System.out.println("ERROR MODBUS Init Red " + e.getMessage());
                }
            } else {
                try { // OJO ACA QUE POR AHI HAY UN TEMITA DE DATABIT Y STOPBIT 26/6
                    ModbusRtuMaster  = GestorPuertoSerie.getInstance().initializateMasterRTUmodbus(strpuerto, Integer.parseInt(Device.getDireccion().get(0)), Integer.parseInt(Device.getDireccion().get(1)), Integer.parseInt(Device.getDireccion().get(2)), Integer.parseInt(Device.getDireccion().get(3)));
                    isinit=true;
                }catch(Exception e){
                    System.out.println("ERROR MODBUS Init RTU " + e.getMessage());
                }
            }
        }
        System.out.println("INIT");
    }




    @Override
    public void LeerHoldingRegister(final Integer registro, final Modbus.ClasesModbus clase, final Modbus.RegisterCallback Callback) {
                if(isinit) {
                    Utils.EsHiloSecundario();
                    String result = "";
                    CountDownLatch latch = new CountDownLatch(1);
                    try {
                        switch (clase) {
                            case Long: {
                                final long[] x = {0};
                                if (Device.getSalida().equals("Red")) {
                                    ModbusTCPMaster.readHoldingRegisters(new OnRequestBack<short[]>() {
                                        @Override
                                        public void onSuccess(short[] shorts) {
                                            x[0] = shorts[0] & 0xFFFF;
                                            x[0] += (long) (limit) * shorts[1];
                                            Callback.finish(String.valueOf(x[0]));
                                            latch.countDown();
                                        }

                                        @Override
                                        public void onFailed(String s) {
                                            Callback.finish(null);
                                            System.out.println("ERR s"+s);
                                            latch.countDown();
                                        }
                                    }, Slaveid, registro-1, 2);
                                } else {
                                    ModbusRtuMaster.readHoldingRegisters(new OnRequestBack<short[]>() {
                                        @Override
                                        public void onSuccess(short[] shorts) {
                                            x[0] = shorts[0] & 0xFFFF;
                                            x[0] += (long) (limit) * shorts[1];
                                            Callback.finish(String.valueOf(x[0]));
                                            latch.countDown();
                                        }

                                        @Override
                                        public void onFailed(String s) {

                                            Callback.finish(null);
                                            latch.countDown();

                                            System.out.println("ERR s"+s);
                                        }
                                    }, Slaveid, registro-1, 2);
                                }
                                if (x[0] > Long.MAX_VALUE) {
                                    x[0] = Long.MAX_VALUE - x[0];
                                }
                                break;
                            }
                            case enteroSinSigno: {
                                final Integer[] x = {0};
                                if (Device.getSalida().equals("Red")) {
                                    ModbusTCPMaster.readHoldingRegisters(new OnRequestBack<short[]>() {
                                        @Override
                                        public void onSuccess(short[] shorts) {
                                            Callback.finish(String.valueOf( shorts[0] & 0xFFFF));
                                            latch.countDown();
                                        }

                                        @Override
                                        public void onFailed(String s) {

                                            Callback.finish(null);
                                            latch.countDown();

                                            System.out.println("ERR s"+s);
                                        }
                                    }, Slaveid, registro-1, 1);
                                } else {
                                    ModbusRtuMaster.readHoldingRegisters(new OnRequestBack<short[]>() {
                                        @Override
                                        public void onSuccess(short[] shorts) {
                                            Callback.finish(String.valueOf( shorts[0] & 0xFFFF));
                                            latch.countDown();
                                        }

                                        @Override
                                        public void onFailed(String s) {

                                            Callback.finish(null);
                                            latch.countDown();

                                            System.out.println("ERR s"+s);
                                        }
                                    }, Slaveid, registro-1, 1);
                                }
                                break;
                            }
                            case enteroConSigno: {
                                final short[] x = {0};
                                if (Device.getSalida().equals("Red")) {
                                    ModbusTCPMaster.readHoldingRegisters(new OnRequestBack<short[]>() {
                                        @Override
                                        public void onSuccess(short[] shorts) {
                                            Callback.finish(String.valueOf( shorts[0] ));
                                            latch.countDown();
                                         }

                                        @Override
                                        public void onFailed(String s) {

                                            Callback.finish(null);
                                            latch.countDown();

                                            System.out.println("ERR s"+s);
                                        }
                                    }, Slaveid, registro-1, 1);
                                } else {
                                    ModbusRtuMaster.readHoldingRegisters(new OnRequestBack<short[]>() {
                                        @Override
                                        public void onSuccess(short[] shorts) {
                                            Callback.finish(String.valueOf(shorts[0]));
                                            latch.countDown();
                                        }

                                        @Override
                                        public void onFailed(String s) {

                                            Callback.finish(null);
                                            latch.countDown();
                                            System.out.println("ERR s"+s);
                                        }
                                    }, Slaveid, registro-1, 1);
                                }
                                break;
                            }
                            case Float: {
                                final Integer[] a = {0};
                                final Integer[] b = {0};
                                if (Device.getSalida().equals("Red")) {
                                    ModbusTCPMaster.readHoldingRegisters(new OnRequestBack<short[]>() {
                                        @Override
                                        public void onSuccess(short[] shorts) {
                                            a[0] = shorts[0] & 0xFFFF;
                                            b[0] = shorts[1] & 0xFFFF;
                                            int bits = (b[0] << 16) | a[0];
                                            Callback.finish(String.valueOf(Float.intBitsToFloat(bits)));
                                            latch.countDown();
                                        }

                                        @Override
                                        public void onFailed(String s) {

                                            Callback.finish(null);
                                            latch.countDown();
                                            System.out.println("ERR s"+s);
                                        }
                                    }, Slaveid, registro-1, 2);
                                } else {
                                    ModbusRtuMaster.readHoldingRegisters(new OnRequestBack<short[]>() {
                                        @Override
                                        public void onSuccess(short[] shorts) {
                                            a[0] = shorts[0] & 0xFFFF;
                                            b[0] = shorts[1] & 0xFFFF;
                                            int bits = (b[0] << 16) | a[0];
                                            Callback.finish(String.valueOf(Float.intBitsToFloat(bits)));
                                            latch.countDown();
                                        }
                                        @Override
                                        public void onFailed(String s) {

                                            Callback.finish(null);
                                            latch.countDown();
                                            System.out.println("ERR s"+s);
                                        }
                                    }, Slaveid, registro-1, 2);
                                }break;
                            } default:}
                    } catch (Exception e) {

                        System.out.println("ERR s"+e.getMessage());
                        Callback.finish(null);
                        latch.countDown();
                    }
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                    }
                }else{
                    System.out.println("ERR not init");
                    Callback.finish(null);
                }
            }
    @Override
    public void LeerCoil(Integer registro, Modbus.CoilCallback Callback ) {
        if(isinit) {
            Utils.EsHiloSecundario();
            CountDownLatch latch = new CountDownLatch(1);
            try {
                final Boolean[] x = {false};
                if (Device.getSalida().equals("Red")) {
                    ModbusTCPMaster.readCoil(new OnRequestBack<boolean[]>() {
                        @Override
                        public void onSuccess(boolean[] booleans) {
                            Callback.finish(booleans[0]);
                            latch.countDown();

                        }

                        @Override
                        public void onFailed(String s) {

                            System.out.println("ERR s "+s);
                            Callback.finish(null);
                            latch.countDown();
                        }
                    }, Slaveid, registro-1, 1);
                } else {
                    ModbusRtuMaster.readCoil(new OnRequestBack<boolean[]>() {
                        @Override
                        public void onSuccess(boolean[] booleans) {
                            Callback.finish(booleans[0]);
                            latch.countDown();
                        }

                        @Override
                        public void onFailed(String s) {

                            System.out.println("ERR s "+s);
                            Callback.finish(null);
                            latch.countDown();
                        }
                    }, Slaveid, registro-1, 1);
                }

            } catch (Exception e) {
                System.out.println("ERR s "+e.getMessage());
                Callback.finish(null);
                latch.countDown();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {

            }
        }else{

            System.out.println("ERR s not init");
            Callback.finish(null);

        }
    }

    @Override
    public void LeerMultiplesHoldingRegisters(Integer registro, Integer Alcance, RegistersCallback callbackCrudo) {
        if(isinit) {
            Utils.EsHiloSecundario();
            String result = "";
            CountDownLatch latch = new CountDownLatch(1);
            try {
                        if (Device.getSalida().equals("Red")) {
                            ModbusTCPMaster.readHoldingRegisters(new OnRequestBack<short[]>() {
                                @Override
                                public void onSuccess(short[] shorts) {
                                    callbackCrudo.finish(shorts);
                                    latch.countDown();
                                }

                                @Override
                                public void onFailed(String s) {
                                    callbackCrudo.finish(null);
                                    latch.countDown();
                                    System.out.println("ERR s"+s);
                                }
                            }, Slaveid, registro-1, Alcance);
                        } else {
                            ModbusRtuMaster.readHoldingRegisters(new OnRequestBack<short[]>() {
                                @Override
                                public void onSuccess(short[] shorts) {
                                    callbackCrudo.finish(shorts);
                                    latch.countDown();
                                }

                                @Override
                                public void onFailed(String s) {

                                    callbackCrudo.finish(null);
                                    latch.countDown();
                                    System.out.println("ERR s"+s);
                                }
                            }, Slaveid, registro-1, Alcance);
                        }

            } catch (Exception e) {
                System.out.println("ERR s"+e.getMessage());
                callbackCrudo.finish(null);
                latch.countDown();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }else{
            System.out.println("ERR not init");
            callbackCrudo.finish(null);
        }
    }

    @Override
    public void LeerMultiplesCoils(Integer registro, Integer Alcance, CoilsCallback callbackCrudo) {
        if(isinit) {
            Utils.EsHiloSecundario();
            CountDownLatch latch = new CountDownLatch(1);
            try {
                final Boolean[] x = {false};
                if (Device.getSalida().equals("Red")) {
                    ModbusTCPMaster.readCoil(new OnRequestBack<boolean[]>() {
                        @Override
                        public void onSuccess(boolean[] booleans) {
                            callbackCrudo.finish(booleans);
                            latch.countDown();

                        }

                        @Override
                        public void onFailed(String s) {

                            System.out.println("ERR s "+s);
                            callbackCrudo.finish(null);
                            latch.countDown();
                        }
                    }, Slaveid, registro-1, Alcance);
                } else {
                    ModbusRtuMaster.readCoil(new OnRequestBack<boolean[]>() {
                        @Override
                        public void onSuccess(boolean[] booleans) {
                            callbackCrudo.finish(booleans);
                            latch.countDown();
                        }

                        @Override
                        public void onFailed(String s) {

                            System.out.println("ERR s "+s);
                            callbackCrudo.finish(null);
                            latch.countDown();
                        }
                    }, Slaveid, registro-1, Alcance);
                }

            } catch (Exception e) {
                System.out.println("ERR s "+e.getMessage());
                callbackCrudo.finish(null);
                latch.countDown();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {

            }
        }else{

            System.out.println("ERR s not init");
            callbackCrudo.finish(null);

        }
    }

    @Override
    public Boolean EscribirMultiplesHoldingRegister(Integer registro, short[] valor) {
        System.out.println("WRITING");
        final Boolean[] res = {false};
        if(isinit) {
            System.out.println("WRITING init");
            Utils.EsHiloSecundario();
            CountDownLatch latch = new CountDownLatch(1);
            try {
                final Boolean[] x = {false};
                if (Device.getSalida().equals("Red")) {
                    ModbusTCPMaster.writeRegisters(new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            res[0] =true;
                            latch.countDown();
                        }

                        @Override
                        public void onFailed(String msg) {
                            res[0] =false;
                            latch.countDown();
                        }
                    },Slaveid, registro-1, valor);
                } else {
                    ModbusRtuMaster.writeRegisters(new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            res[0] =true;
                            latch.countDown();
                        }
                        @Override
                        public void onFailed(String msg) {
                            res[0] =false;
                            latch.countDown();
                        }
                    },Slaveid, registro-1, valor);
                }
            } catch (Exception e) {
                System.out.println("ERR s "+e.getMessage());
                latch.countDown();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {
            }
        }else{
            System.out.println("ERR s not init");
        }
        return    res[0];
    }

    @Override
    public Boolean EscribirMultiplesCoils(Integer registro, boolean[] valor) {
        final Boolean[] res = {false};
        if(isinit) {
            Utils.EsHiloSecundario();
            CountDownLatch latch = new CountDownLatch(1);
            try {
                final Boolean[] x = {false};
                if (Device.getSalida().equals("Red")) {
                    ModbusTCPMaster.writeCoils(
                            new OnRequestBack<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    res[0] =true;
                                    latch.countDown();
                                }

                                @Override
                                public void onFailed(String msg) {
                                    res[0] =false;
                                    latch.countDown();
                                }
                            }, Slaveid, registro-1, valor);
                } else {
                    ModbusRtuMaster.writeCoils(new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            res[0]=true;
                            latch.countDown();
                        }

                        @Override
                        public void onFailed(String msg) {
                            res[0] =false;
                            latch.countDown();
                        }
                    }, Slaveid, registro-1, valor);
                }

            } catch (Exception e) {
                System.out.println("ERR s "+e.getMessage());
                latch.countDown();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {

            }
        }else{

            System.out.println("ERR s not init");
        }
        return  res[0];
    }


    @Override
    public Boolean EscribirHoldingRegister(Integer registro, Integer valor) {

        System.out.println("WRITING");
        final Boolean[] res = {false};
        if(isinit) {

            System.out.println("WRITING init");
            Utils.EsHiloSecundario();
            CountDownLatch latch = new CountDownLatch(1);
            try {
                final Boolean[] x = {false};
                if (Device.getSalida().equals("Red")) {
                    ModbusTCPMaster.writeRegister(new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            res[0] =true;
                            latch.countDown();
                        }

                        @Override
                        public void onFailed(String msg) {
                            res[0] =false;
                            latch.countDown();
                        }
                    },Slaveid, registro-1, valor);
                } else {
                    ModbusRtuMaster.writeRegister(new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            res[0] =true;
                            latch.countDown();
                        }

                        @Override
                        public void onFailed(String msg) {
                            res[0] =false;
                            latch.countDown();
                        }
                    },Slaveid, registro-1, valor);
                }

            } catch (Exception e) {
                System.out.println("ERR s "+e.getMessage());
                latch.countDown();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {

            }
        }else{
            System.out.println("ERR s not init");

        }
        return    res[0];
    }

    @Override
    public Boolean EscribirCoil(Integer registro, Boolean valor) {
        final Boolean[] res = {false};
        if(isinit) {
            Utils.EsHiloSecundario();
            CountDownLatch latch = new CountDownLatch(1);
            try {
                final Boolean[] x = {false};
                if (Device.getSalida().equals("Red")) {
                    ModbusTCPMaster.writeCoil(
                            new OnRequestBack<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    res[0] =true;
                                    latch.countDown();
                                }

                                @Override
                                public void onFailed(String msg) {
                                    res[0] =false;
                                    latch.countDown();
                                }
                            }, Slaveid, registro-1, valor);
                } else {
                    ModbusRtuMaster.writeCoil(new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String s) {
                            res[0]=true;
                            latch.countDown();
                        }

                        @Override
                        public void onFailed(String msg) {
                            res[0] =false;
                            latch.countDown();
                        }
                    }, Slaveid, registro-1, valor);
                }

            } catch (Exception e) {
                System.out.println("ERR s "+e.getMessage());
                latch.countDown();
            }
            try {
                latch.await();
            } catch (InterruptedException e) {

            }
        }else{

            System.out.println("ERR s not init");
        }
        return  res[0];
    }

    @Override
    public void stop() {
        if (Device.getSalida().equals("Red")) {
            try {
                ModbusTCPMaster.destory();
            } catch (Exception e) {
            }
        } else {
            try {
                ModbusRtuMaster.destroy();
            } catch (Exception e) {
            }
        }
    }
}
