package com.service.Devices.Balanzas.Clases.ITW410;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.service.BalanzaService;
import com.service.Comunicacion.Modbus.modbus4And.requset.OnRequestBack;
import com.service.Devices.Balanzas.Clases.BalanzaBase;
import com.service.Comunicacion.GestorPuertoSerie;
import com.service.ComService;
import com.service.Comunicacion.Modbus.Req.ModbusReqRtuMaster;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.Interfaz.Balanza;
import com.service.PreferencesDevicesManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ITW410_FORM extends BalanzaBase implements Balanza.ITW410, Serializable {
    int estado410=0,numeroSlave=1;
    public static Integer nBalanzas=2;
    public static Boolean /*Tieneid=false,*/TieneCal =true;
    public static final String Bauddef="115200",StopBdef="1",DataBdef="8",Paritydef="0";
    private ModbusReqRtuMaster ModbusRtuMaster;
    public static String Nombre ="ITW410";
    private final int numBzaMultiple410;
    private final ITW410_FORM context;
    public ITW410_FORM( String Puerto,int id,AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener, int numbza410) {
        super(Puerto,id,activity,fragmentChangeListener,numbza410);
        try {
            Service= BalanzaService.getInstance();
            this.ModbusRtuMaster = GestorPuertoSerie.getInstance().initializatemodbus(Puerto,Integer.parseInt(Bauddef),Integer.parseInt(DataBdef),Integer.parseInt(StopBdef),Integer.parseInt(Paritydef));
            Thread.sleep(200);
        } catch (InterruptedException e) {
        } finally {
           // System.out.println("410id "+id+" 410subnum "+ numbza410);
           if(id==0){
               numeroSlave=1;
           }else{
               numeroSlave=id;
           }
            int inx=ModbusRtuMaster.get_Puerto();
            this.numBza=(inx*10)*numbza410; // HAY QUE CAMBIARLO EN EL CASO DE QUE HAYA BALAMZAS 410 MULTIPLES. OSEA 410 CON 485
            this.numBzaMultiple410 =numbza410;
            context =this;
        }
    }

    /*@Override
    public Balanza getBalanza(int numBza) {
        return this;
    }*/
     @Override public void init(int numBza) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Estado =M_MODO_BALANZA;
                    Bucle.run();
                    pesoBandaCero= getBandaCeroValue(numBza);
                    PuntoDecimal =PreferencesDevicesManager.getPuntoDecimal(Nombre, context.numBza,activity);
                    ultimaCalibracion=PreferencesDevicesManager.getUltimaCalibracion(Nombre, context.numBza,activity);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    Runnable Bucle = new Runnable() {

        @Override
        public void run() {
            Runnable runnable = new Runnable() {
                public void run() {
                    if(Objects.equals(Estado, M_MODO_BALANZA)) {
                        try {
                            if (numBzaMultiple410 == 1) {
                                final float[] response = {0.0F};
                                OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
                                    @Override
                                    public void onSuccess(short[] result) {
                                        Bruto = formatpuntodec(result[0]);
                                        BrutoStr =format(PuntoDecimal, String.valueOf(Bruto));
                                        Neto = formatpuntodec(result[1]);
                                        NetoStr = format(PuntoDecimal,String.valueOf(Neto));
                                        Tara = formatpuntodec(result[3]);
                                        TaraStr = format(PuntoDecimal,String.valueOf(Tara));
                                        estado410 = result[3];
                                    }
                                    @Override
                                    public void onFailed(String error) {
                                    }
                                };
                                ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 20, 4);
                            } else if (numBzaMultiple410 == 2) {
                                final float[] response = {0.0F};
                                OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
                                    @Override
                                    public void onSuccess(short[] result) {
                                        Bruto = result[0];
                                        BrutoStr = String.valueOf(Bruto);
                                    }
                                    @Override
                                    public void onFailed(String error) {
                                    }
                                };
                                ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 3, 2);
                                Thread.sleep(200);
                                callback = new OnRequestBack<short[]>() {
                                    @Override
                                    public void onSuccess(short[] result) {
                                        Neto = result[0];
                                    }
                                    @Override
                                    public void onFailed(String error) {
                                    }
                                };
                                ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 4, 1);
                                Thread.sleep(200);
                                callback = new OnRequestBack<short[]>() {
                                    @Override
                                    public void onSuccess(short[] result) {
                                        Tara = result[0];
                                        TaraStr = String.valueOf(Tara);
                                    }
                                    @Override
                                    public void onFailed(String error) {
                                    }
                                };
                                ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 6, 1);
                                Thread.sleep(200);
                                callback = new OnRequestBack<short[]>() {
                                    @Override
                                    public void onSuccess(short[] result) {
                                        // Maneja el resultado exitoso aquí
                                        int peso_cal = result[0];
                                        int div_min = result[1];
                                        int filter1 = result[2];
                                        int filter2 = result[3];
                                        int filter3 = result[4];

                                    }

                                    @Override
                                    public void onFailed(String error) {
                                        // Maneja el error aquí


                                    }
                                };
                                ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 12, 5);
                                Thread.sleep(500);


                            };
                        } catch (Exception e) {}
                    }

                    mHandler.postDelayed(Bucle,500);
                }
            };
            new Thread(runnable).start();

        }

    };
    @Override public void escribir(String msj,int numBza) {}
    @Override public void stop(int numBza) {
        mHandler.removeCallbacks(Bucle);
        if(ModbusRtuMaster!=null){
            ModbusRtuMaster.destroy();
            ModbusRtuMaster=null;
            mHandler.removeCallbacks(Bucle);
        }
        Estado =M_VERIFICANDO_MODO;

    }


    @Override public void Itw410FrmSetTiempoEstabilizacion(int numBza, final int Tiempo) {
        Runnable runnable = new Runnable() {
            public void run() {
                try{
                    final String[] res = {""};
                    OnRequestBack<String> callback2 = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Maneja el resultado exitoso aquí
                            res[0] =result;
                        }

                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí
                            res[0] =error;

                        }
                    };
                    ModbusRtuMaster.writeRegister(callback2,numeroSlave,31,Tiempo);
                }catch (Exception e){

                };
            }
        };
        new Thread(runnable).start();

    }

    @Override public String getFiltro1(int numBza) {
        return PreferencesDevicesManager.getFiltro(Nombre,this.numBza,1,activity);
    }
    @Override public String getFiltro2(int numBza) {
        return PreferencesDevicesManager.getFiltro(Nombre,this.numBza,2,activity);
    }
    @Override public String getFiltro3(int numBza) {
        return PreferencesDevicesManager.getFiltro(Nombre,this.numBza,3,activity);
    }
    @Override public String getFiltro4(int numBza) {
        return PreferencesDevicesManager.getFiltro(Nombre,this.numBza,4,activity);
    }

    @Override public Boolean Itw410FrmSetear(int numero, String setPoint, int Salida) {
        String valueFormateado="";

        final Boolean[] Resulte = {false};
        valueFormateado = format(PuntoDecimal,setPoint);

        final String[] res = {""};
        CountDownLatch latch= new CountDownLatch(1);
        OnRequestBack<String> callback2 = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                latch.countDown();
                Resulte[0] =true;
            }
            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                res[0] =error;
                latch.countDown();
                Resulte[0] =false;
            }
        };
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                ModbusRtuMaster.writeRegister(callback2,numeroSlave,30,Salida);

            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                res[0] =error;
                Resulte[0] =false;
            }
        };

        ModbusRtuMaster.writeRegister(callback,numeroSlave,29,Integer.parseInt(valueFormateado.replace(".","")));
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            Resulte[0] =false;
        }
        return Resulte[0];
    }
    @Override public String Itw410FrmGetSetPoint(int numBza) {
        final short[][] res = {new short[0]};
        final String[] valueformater = new String[1];
        CountDownLatch latch = new CountDownLatch(1);
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                int v = result[0];
                valueformater[0]= String.valueOf(format(PuntoDecimal,String.valueOf(result[0])));
                latch.countDown();
            }
            @Override
            public void onFailed(String error) {
                latch.countDown();
            }
        };
        ModbusRtuMaster.readHoldingRegisters(callback,numeroSlave,29,1);
        try {
            latch.await(1000, TimeUnit.MILLISECONDS); // Espera hasta que el callback se complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Maneja la excepción si es necesario
        }
        return valueformater[0];
    }
    @Override public Integer Itw410FrmGetSalida(int numBza) {
        final short[][] res = {new short[0]};
        final int[] response = {-1};
        CountDownLatch latch = new CountDownLatch(1);
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                response[0] =result[0];
                latch.countDown();
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                response[0] =-1;
                latch.countDown();

            }
        };
        ModbusRtuMaster.readHoldingRegisters(callback,numeroSlave,30,1);

        try {
            latch.await(1000, TimeUnit.MILLISECONDS); // Espera hasta que el callback se complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Maneja la excepción si es necesario
        }
        return response[0];
    }
    @Override public void Itw410FrmStart(int numBza) {
        Runnable runnable = new Runnable() {
            public void run() {
                final String[] res = {""};

                OnRequestBack<String> callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        res[0] =result;
                    }

                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí
                        res[0] =error;

                    }
                };
                ModbusRtuMaster.writeCoil(callback,numeroSlave,11,true);
            }
        };
        new Thread(runnable).start();
    }
    @Override public Integer Itw410FrmGetEstado(int numBza) {
        return estado410;
    }
    @Override public String Itw410FrmGetUltimoPeso(int numBza) {
        final short[][] res = {new short[0]};
        final CountDownLatch latch = new CountDownLatch(1);
        final float[] response = {0};
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                response[0] = formatpuntodec(Integer.parseInt(String.valueOf(result[0])));
                latch.countDown();
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                response[0] =0;
                latch.countDown();


            }
        };
        ModbusRtuMaster.readHoldingRegisters(callback,numeroSlave,25,1);
        try {
            latch.await(1000, TimeUnit.MILLISECONDS); // Espera hasta que el callback se complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Maneja la excepción si es necesario
        }

        return String.valueOf(response[0]);
    }

    @Override public Integer Itw410FrmGetUltimoIndice(int numBza) {
        final short[][] res = {new short[0]};
        final int[] response = {-1};
        final CountDownLatch latch = new CountDownLatch(1);
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                res[0] =result;
                response[0] =result[0];
                latch.countDown(); // Libera el lock
            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                response[0] =-1;
                latch.countDown();

            }
        };

        ModbusRtuMaster.readHoldingRegisters(callback,numeroSlave,24,1);
        try {
            latch.await(1000, TimeUnit.MILLISECONDS); // Espera hasta que el callback se complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Maneja la excepción si es necesario
        }

        return response[0];
    }
    @Override public void itw410FrmPause(int numBza) {
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí

            }
            @Override
            public void onFailed(String error) {
                // Maneja el error aquí


            }
        };

        ModbusRtuMaster.writeCoil(callback, numeroSlave, 12, true);
    }
    @Override public void itw410FrmStop(int numBza) {
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí

                OnRequestBack<String> callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                    }
                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí


                    }
                };

                ModbusRtuMaster.writeCoil(callback, numeroSlave, 12, true);
            }
            @Override
            public void onFailed(String error) {
                // Maneja el error aquí


            }
        };

        ModbusRtuMaster.writeCoil(callback, numeroSlave, 12, true);

    }
    @Override public void setTara(int numBza) {
        if(ModbusRtuMaster !=null){

            Runnable runnable = new Runnable() {
                public void run() {
                    if(numBzaMultiple410 ==1) {
                        OnRequestBack<String> callback = new OnRequestBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                // Maneja el resultado exitoso aquí

                            }
                            @Override
                            public void onFailed(String error) {
                                // Maneja el error aquí


                            }
                        };

                        ModbusRtuMaster.writeCoil(callback, numeroSlave, 1, true);
                    }else if (numBzaMultiple410 ==2) {
                        OnRequestBack<String>  callback = new OnRequestBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                // Maneja el resultado exitoso aquí
                                String peso_cal = result;

                            }

                            @Override
                            public void onFailed(String error) {
                                // Maneja el error aquí


                            }
                        };
                        ModbusRtuMaster.writeCoil(callback,numeroSlave,3,true);
                    }
                }
            };
            new Thread(runnable).start();
        }
    }

    @Override public void setCero(int numBza) {
        if(ModbusRtuMaster !=null){

            Runnable runnable = new Runnable() {
                public void run() {
                    if(numBzaMultiple410 ==1) {
                        OnRequestBack<String> callback = new OnRequestBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                // Maneja el resultado exitoso aquí
                                String peso_cal = result;
                                TaraDigital = 0;
                                Tara=0;
                                TaraDigitalStr =String.valueOf(0);
                            }
                            @Override
                            public void onFailed(String error) {
                                // Maneja el error aquí


                            }
                        };
                        ModbusRtuMaster.writeCoil(callback, numeroSlave, 0, true);
                    }else if (numBzaMultiple410 ==2) {
                        OnRequestBack<String>  callback = new OnRequestBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                // Maneja el resultado exitoso aquí
                                String peso_cal = result;
                                TaraDigital = 0;
                                TaraDigitalStr =String.valueOf(0);

                            }

                            @Override
                            public void onFailed(String error) {
                                // Maneja el error aquí

                            }
                        };
                        ModbusRtuMaster.writeCoil(callback,numeroSlave,3,true);
                    }
                }
            };
            new Thread(runnable).start();
        }

    }
    @Override public void openCalibracion(int numero) {
        Estado =M_MODO_CALIBRACION;
        CalibracionItw410Fragment fragment = CalibracionItw410Fragment.newInstance(context, Service);
        Bundle args = new Bundle();
        args.putSerializable("instance", context);
        args.putSerializable("instanceService", Service);
        fragmentChangeListener.openFragmentService(fragment,args);
    }



    //    -------------------------------------------------------------------------------
    protected float formatpuntodec(int numero) {
        float respuesta =  numero / (float) Math.pow(10, PuntoDecimal);
        return Float.parseFloat(format(PuntoDecimal,String.valueOf(respuesta)));
    }
    protected ArrayList<String> Pedirparam() { // NUEVO
        ArrayList<String> list = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);
        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
            @Override
            public void onSuccess(short[] result) {
                // Maneja el resultado exitoso aquí
                list.add(Integer.toString(result[0]));
                list.add(Integer.toString(result[1]));

                list.add(Integer.toString(result[2]));

                list.add(Integer.toString(result[3]));

                list.add(Integer.toString(result[4]));

                latch.countDown();

            }

            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                list.add("");
                list.add("");
                list.add("");
                list.add("");
                list.add("");
                list.add("");
                latch.countDown();


            }
        };
        ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 6, 5);
        try {
            latch.await(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return list;
    }

    protected void enviarParametros(final ArrayList<Integer> listavalores){ //Division minim,Pesoconocido,filtro1,filtro2,filtro3 5vals

        Runnable runnable = new Runnable() {
            public void run() {
                if(ModbusRtuMaster!=null) {
                    CountDownLatch latch = new CountDownLatch(1);
                    OnRequestBack<String>  callback5 = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Maneja el resultado exitoso aquí
                            latch.countDown();

                        }

                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí
                            latch.countDown();

                        }
                    };
                    OnRequestBack<String>  callback4 = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Maneja el resultado exitoso aquí
                            ModbusRtuMaster.writeRegister(callback5,numeroSlave,10,listavalores.get(4));


                        }

                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí


                        }
                    };
                    OnRequestBack<String>  callback3 = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Maneja el resultado exitoso aquí
                            ModbusRtuMaster.writeRegister(callback4,numeroSlave,9,listavalores.get(3));


                        }

                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí


                        }
                    };
                    OnRequestBack<String>  callback2 = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Maneja el resultado exitoso aquí
                            ModbusRtuMaster.writeRegister(callback3,numeroSlave,8,listavalores.get(2));
                            // System.out.println("ITW410 pesoconocido"+result+listavalores.get(1).toString());
                        }
                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí


                        }
                    };
                    OnRequestBack<String>  callback = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Maneja el resultado exitoso aquí
                            ModbusRtuMaster.writeRegister(callback2,numeroSlave,6,listavalores.get(1));
                        }
                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí
                        }
                    };
                    ModbusRtuMaster.writeRegister(callback,numeroSlave,7,listavalores.get(0));
                    try {
                        latch.await(1000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        new Thread(runnable).start();
    }
    protected String Guardar_cal(){
        Runnable runnable = new Runnable() {
            public void run() {
                OnRequestBack<String>  callback = new OnRequestBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // Maneja el resultado exitoso aquí
                        String peso_cal = result;
                    }
                    @Override
                    public void onFailed(String error) {
                        // Maneja el error aquí
                    }
                };
                ModbusRtuMaster.writeCoil(callback,numeroSlave,10,true);
            }
        };
        new Thread(runnable).start();
        return "\u0005S\r";
    }
    protected String Recero_cal(){
        Runnable runnable = new Runnable() {
            public void run() {
                CountDownLatch latch = new
                        CountDownLatch(1);
                if(numBzaMultiple410 ==1) {
                    OnRequestBack<String> callback = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            latch.countDown();
                        }
                        @Override
                        public void onFailed(String error) {
                            latch.countDown();
                        }
                    };
                    ModbusRtuMaster.writeCoil(callback, numeroSlave, 6, true);
                }else if (numBzaMultiple410 ==2) {
                    OnRequestBack<String>  callback = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            latch.countDown();
                        }
                        @Override
                        public void onFailed(String error) {
                            latch.countDown();
                        }
                    };
                    ModbusRtuMaster.writeCoil(callback,numeroSlave,9,true);
                }
                try{
                    latch.await(1000,TimeUnit.MILLISECONDS);
                }catch (Exception e){
                    Thread.currentThread().interrupt();
                }
            }
        };
        new Thread(runnable).start();
        return "";
    }
    protected String get_PesoConocido(){
        PuntoDecimal =PreferencesDevicesManager.getPuntoDecimal(Nombre,this.numBza,activity);
        String str = PreferencesDevicesManager.getPesoConocido(Nombre,numBza,activity);
        return format(PuntoDecimal,str);
    }

    protected void setRecerocal(){
        Runnable runnable = new Runnable() {
            public void run() {
                if(ModbusRtuMaster !=null){
                    CountDownLatch latch = new CountDownLatch(1);
                    if(numBzaMultiple410 ==1) {
                        OnRequestBack<String> callback = new OnRequestBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                // Maneja el resultado exitoso aquí
                                String peso_cal = result;
                                latch.countDown();


                            }

                            @Override
                            public void onFailed(String error) {
                                // Maneja el error aquí
                                latch.countDown();


                            }
                        };
                        ModbusRtuMaster.writeCoil(callback, numeroSlave, 6, true);
                    }else if (numBzaMultiple410 ==2) {
                        OnRequestBack<String>  callback = new OnRequestBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                // Maneja el resultado exitoso aquí
                                String peso_cal = result;

                                latch.countDown();

                            }

                            @Override
                            public void onFailed(String error) {
                                // Maneja el error aquí
                                latch.countDown();

                            }
                        };
                        ModbusRtuMaster.writeCoil(callback,numeroSlave,9,true);
                    }
                    try{
                        latch.await(1000, TimeUnit.MILLISECONDS);
                    }catch (Exception e){
                        Thread.currentThread().interrupt();
                    }

                }
            }
        };
        new Thread(runnable).start();
    }
    protected  void setSpancal(){
        if(ModbusRtuMaster !=null){

            Runnable runnable = new Runnable() {
                public void run() {
                    CountDownLatch latch = new CountDownLatch(1);
                    if(numBzaMultiple410 ==1) {
                        OnRequestBack<String> callback = new OnRequestBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                // Maneja el resultado exitoso aquí
                                String peso_cal = result;
                                latch.countDown();


                            }

                            @Override
                            public void onFailed(String error) {
                                // Maneja el error aquí
                                latch.countDown();


                            }
                        };
                        ModbusRtuMaster.writeCoil(callback, numeroSlave, 5, true);
                    }else if (numBzaMultiple410 ==2) {
                        OnRequestBack<String>  callback = new OnRequestBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                // Maneja el resultado exitoso aquí
                                String peso_cal = result;

                                latch.countDown();

                            }

                            @Override
                            public void onFailed(String error) {
                                // Maneja el error aquí
                                latch.countDown();

                            }
                        };
                        ModbusRtuMaster.writeCoil(callback,numeroSlave,8,true);
                    }
                    try{
                        latch.await(1000, TimeUnit.MILLISECONDS);
                    }catch (Exception e){
                        Thread.currentThread().interrupt();
                    }
                }
            };
            new Thread(runnable).start();
        }

    }
    protected void salir_cal(){
        // open principal
        ComService.getInstance().fragmentChangeListener.openFragmentPrincipal();
    }
    protected   void setCerocal(){
        Runnable runnable = new Runnable() {
            public void run() {
                if(ModbusRtuMaster !=null){
                    CountDownLatch latch = new CountDownLatch(1);
                    if(numBzaMultiple410 ==1) {
                        OnRequestBack<String> callback = new OnRequestBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                // Maneja el resultado exitoso aquí
                                String peso_cal = result;
                                latch.countDown();

                            }

                            @Override
                            public void onFailed(String error) {
                                // Maneja el error aquí
                                latch.countDown();


                            }
                        };
                        ModbusRtuMaster.writeCoil(callback, numeroSlave, 4, true);
                    }else if (numBzaMultiple410 ==2) {
                        OnRequestBack<String>  callback = new OnRequestBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                // Maneja el resultado exitoso aquí
                                String peso_cal = result;

                                latch.countDown();

                            }

                            @Override
                            public void onFailed(String error) {
                                // Maneja el error aquí
                                latch.countDown();

                            }
                        };
                        ModbusRtuMaster.writeCoil(callback,numeroSlave,7,true);
                    }
                    try{
                        latch.await(1000, TimeUnit.MILLISECONDS);
                    }catch (Exception e){
                        Thread.currentThread().interrupt();
                    }

                }
            }
        };
        new Thread(runnable).start();
    }

 /*public String Tara(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(subnombre==1){

                    OnRequestBack<String> callback = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Maneja el resultado exitoso aquí

                        }

                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí


                        }
                    };
                    ModbusRtuMaster.writeCoil(callback,numeroSlave,1,true);

                } else if (subnombre==2) {
                    OnRequestBack<String>  callback = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Maneja el resultado exitoso aquí


                        }

                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí


                        }
                    };
                    ModbusRtuMaster.writeCoil(callback,numeroSlave,4,true);

                }
            }
        }).start();

        return "";
    }
*/

};

