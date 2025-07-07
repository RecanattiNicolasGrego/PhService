package com.service.Devices.Balanzas.Clases;

import androidx.appcompat.app.AppCompatActivity;

import com.service.BalanzaService;
import com.service.Comunicacion.GestorPuertoSerie;
import com.service.Comunicacion.Modbus.Req.ModbusReqRtuMaster;
import com.service.Comunicacion.Modbus.modbus4And.requset.OnRequestBack;
import com.service.Interfaz.Balanza;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.PreferencesDevicesManager;
import com.service.Utils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ITW380 extends BalanzaBase implements Balanza.ITW380, Serializable {
        int estado410=0,numeroSlave=1;
        public static final int nBalanzas=1;
        public static Boolean /*Tieneid=false,*/ TieneCal =true;
        private ModbusReqRtuMaster ModbusRtuMaster;

        public static String   Nombre ="ITW380";
        public static String   Bauddef="9600";
        public static String   StopBdef="1";
        public static String    DataBdef="8";
        public static String   Paritydef="0";
        public static int  timeout = 500;
        public static Boolean   TienePorDemanda =true;
    ExecutorService thread = Executors.newFixedThreadPool(2);

    private final com.service.Devices.Balanzas.Clases.ITW380  context;
    private long CaudalInst=0;
    private int VelocidadSinEncoder=0;
    private int Encoder=0;
    private int Turno4=0;
    private int Turno3=0;
    private int ResultadoPID = 0;
    private int Turno2=0;
    private int Año=0;
    private int Hora=0;
    private int DiaMes=0;
    private int Turno1=0;
    private long Setpoint5Acum=0;
    private long Setpoint4Acum=0;
    private long Setpoint3Acum=0;
    private long Setpoint2Acum=0;
    private long Setpoint1Acum=0;
    private long Setpoint=0;
    private long PIDSetpoint=0;
    private int TiempoCinta=0;
    private int VelocidadCinta=0;
    private int Pulso=0;
    private int RPM=0;
    private long Turno4Acum=0;
    private long Turno3Acum=0;
    private int RegTurno=0;
    private long Turno1Acum=0;
    private long Turno2Acum=0;
    private long AlarmaAcum=0;
    private long AlarmaCaudMin=0;
    private long AlarmaCaudMax=0;
    private long AcumuladoTotal=0;
    private long AcumuladoParcial=0;

    private static int adjustId(int id) {
            return id == 0 ? 1 : id;
        }
        public ITW380(String Puerto, int id, AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener, int numbza) {
            super(Puerto,adjustId(id),activity,fragmentChangeListener,nBalanzas);
            try {
                Service= BalanzaService.getInstance();
                this.ModbusRtuMaster = GestorPuertoSerie.getInstance().initializateMasterRTUmodbus(Puerto,Integer.parseInt(Bauddef),Integer.parseInt(DataBdef),Integer.parseInt(StopBdef),Integer.parseInt(Paritydef));
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.out.println("happenning something?"+e.getMessage());
            } finally {
                if(id==0){
                    numeroSlave=1;
                }else{
                    numeroSlave=id;
                }
                int inx=ModbusRtuMaster.get_Puerto();
                this.numBza=(inx*10000+numeroSlave*100)+ numbza; // HAY QUE CAMBIARLO EN EL CASO DE QUE HAYA BALAMZAS 380 MULTIPLES. OSEA 380 CON 485

                context =this;
            }
        }

    /*@Override
    public Balanza getBalanza(int numBza) {
        return this;
    }*/
        // Crear y arrancar el hilo con Looper propio

        @Override
        public void init(int numBza) {



            // Asociar el Handler al Looper del nuevo hilo

            // Ejecutar tu lógica dentro del hilo secundario
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        Estado = M_MODO_BALANZA;
                        pesoBandaCero = getBandaCeroValue(numBza);
                        PuntoDecimal = PreferencesDevicesManager.getPuntoDecimal(Nombre, context.numBza, activity);
                        ultimaCalibracion = PreferencesDevicesManager.getUltimaCalibracion(Nombre, context.numBza, activity);

                        // Iniciar el bucle personalizado
                        iniciarBucle();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });}
        private void iniciarBucle() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Aquí va tu lógica que se repite (el "bucle")
                    Bucle.run();  // O lo que necesites hacer
                    // Reprogramar el bucle cada X milisegundos
                    mHandler.postDelayed(this, 1); // 1000 ms = 1 segundo
                    try {
                        resetlatch().await();
                    } catch (InterruptedException e) {

                    }
                }
            }, 1);
        }
        private void  leerpeso(List<Short> list){
            try {
                Neto=   (float) leerdobleregistro(list)/1000 ;
                System.out.println("NETO = "+Neto);
                NetoStr=String.valueOf(Neto);
                Bruto = Neto;
                BrutoStr = String.valueOf(Bruto);
            } catch (Exception e) {
            }
        }
        Runnable Bucle = new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("ESPERANDO"+getID(1));
                    Semaforo.acquire();
                    latch = resetlatch();
                    System.out.println("Running"+getID(1));
                        OnRequestBack<short[]> callback = new OnRequestBack<short[]>() {
                            @Override
                            public void onSuccess(short[] result) {
                                    leerpeso(Arrays.asList(result[0],result[1]));
                                    leerCaudalInstantaneo(Arrays.asList(result[2],result[3]));
                                    leerAcumuladoP(Arrays.asList(result[4],result[5]));
                                    leerAcumuladoT(Arrays.asList(result[6],result[7]));
                                    leerAlarmCaudMin(Arrays.asList(result[8],result[9]));
                                    leerAlarmCaudMax(Arrays.asList(result[10],result[11]));
                                    leerAlarmAcum(Arrays.asList(result[12],result[13]));
                                    leerTurno1Acum(Arrays.asList(result[14],result[15]));
                                    leerTurno2Acum(Arrays.asList(result[16],result[17]));
                                    leerTurno3Acum(Arrays.asList(result[18],result[19]));
                                    leerTurno4Acum(Arrays.asList(result[20],result[21]));
                                    leerRegistroTurno(result[22]);
                                    leerRPM(result[23]);
                                    leerVelocidad(result[24]);
                                    leerPulsos(result[25]);
                                    leerTiempoCinta(result[26]);
                                    leerResultadoPID(result[27]);
                                    leerPIDSetpoint(Arrays.asList(result[28],result[29]));
                                    leerSetpoint(Arrays.asList(result[30],result[31]));
                                    leetSetpoint1Acum(Arrays.asList(result[32],result[33]));
                                    leerSetpoint2Acum(Arrays.asList(result[34],result[35]));
                                    leerSetpoint3Acum(Arrays.asList(result[36],result[37]));
                                    leerSetpoint4Acum(Arrays.asList(result[38],result[39]));
                                    leerSetpoint5Acum(Arrays.asList(result[40],result[41]));
                                    leerDiaMes(result[42]);
                                    leerAño(result[43]);
                                    leerHora(result[44]);
                                    leerTurno1(result[45]);
                                    leerTurno2(result[46]);
                                    leerTurno3(result[47]);
                                    leerTurno4(result[48]);
                                    leerEncoder(result[49]);
                                    leerVelocidadSinEncoder(result[50]);
                                try {
                                    System.out.println("Running"+getID(1)+"RESPONDE");
                                    latch.countDown();
                                } catch (Exception e) {

                                }
                            }
                            @Override
                            public void onFailed(String error) {
                                latch.countDown();
                            }
                        };
                        ModbusRtuMaster.readHoldingRegisters(callback, numeroSlave, 1, 52);
                } catch (Exception e) {
                    latch.countDown();
                }
            }

        };

    @Override
    public void setTaraDigital(float tara) {

    }

    private List<Integer> AltoBajoLista(Integer valor){
        int high = ((valor) >> 16) & 0xFFFF;
        int low = valor & 0xFFFF;
        return Arrays.asList(high,low);
    }
    long leerdobleregistro(List<Short> arr){
        int high = arr.get(0);
        int low =  arr.get(1);

        return ((long)(high << 16) | low);
    }
    private void leerVelocidadSinEncoder(short i) {
        VelocidadSinEncoder=i;
    }

    private void leerEncoder(short i) {
        Encoder=i;
        System.out.println("Encoder = "+Encoder);

    }

    private void leerTurno4(short i) {
        Turno4=i;
        System.out.println("Turno4 = "+Turno4);
    }

    private void leerTurno3(short i) {
        Turno3=i;
        System.out.println("Turno3 = "+Turno3);

    }

    private void leerTurno2(short i) {
        Turno2=i;
        System.out.println("Turno2 = "+Turno2);


    }

    private void leerTurno1(short i) {
        Turno1=i;
        System.out.println("Turno1 = "+Turno1);

    }

    private void leerHora(short i) {
        Hora=i;
        System.out.println("Hora = "+Hora);

    }

    private void leerAño(short i) {
        Año=i;
        System.out.println("Año = "+Año);

    }

    private void leerDiaMes(short i) {
        DiaMes =i;
        System.out.println("DiaMes = "+DiaMes);

    }

    private void leerSetpoint5Acum(List<Short> list) {
        Setpoint5Acum =leerdobleregistro(list);
        System.out.println("Setpoint5Acum = "+Setpoint5Acum);


    }

    private void leerSetpoint4Acum(List<Short> list) {
        Setpoint4Acum =leerdobleregistro(list);
        System.out.println("Setpoint4Acum = "+Setpoint4Acum);

    }

    private void leerSetpoint3Acum(List<Short> list) {
        Setpoint3Acum =leerdobleregistro(list);
        System.out.println("Setpoint3Acum = "+Setpoint3Acum);

    }

    private void leerSetpoint2Acum(List<Short> list) {
        Setpoint2Acum =leerdobleregistro(list);
        System.out.println("Setpoint2Acum = "+Setpoint2Acum);

    }

    private void leetSetpoint1Acum(List<Short> list) {
        Setpoint1Acum =leerdobleregistro(list);
        System.out.println("Setpoint1Acum = "+Setpoint1Acum);

    }

    private void leerSetpoint(List<Short> list) {
        Setpoint =leerdobleregistro(list);
        System.out.println("Setpoint = "+Setpoint);

    }

    private void leerPIDSetpoint(List<Short> list) {
        PIDSetpoint =leerdobleregistro(list);
        System.out.println("PIDSetpoint = "+PIDSetpoint);

    }

    private void leerTiempoCinta(short i) {

        TiempoCinta =i;
        System.out.println("TiempoCinta = "+TiempoCinta);

    }
    public void leerResultadoPID(short i){
        ResultadoPID =i;
        System.out.println("ResultadoPID = "+TiempoCinta);
    }

    private void leerPulsos(short i) {

        Pulso =i;
        System.out.println("Pulso = "+Pulso);

    }

    private void leerRPM(short i) {

        RPM = i;
        System.out.println("RPM = "+RPM);

    }

    private void leerVelocidad(short i) {

        VelocidadCinta = i;
        System.out.println("Velocidad = "+VelocidadCinta);

    }

    private void leerRegistroTurno(short i) {

        RegTurno =i;
        System.out.println("RegTurno = "+RegTurno);

    }

    private void leerTurno4Acum(List<Short> list) {

        Turno4Acum = leerdobleregistro(list);
        System.out.println("Turno4Acum = "+Turno4Acum);

    }

    private void leerTurno3Acum(List<Short> list) {

        Turno3Acum = leerdobleregistro(list);
        System.out.println("Turno3Acum = "+Turno3Acum);

    }

    private void leerTurno2Acum(List<Short> list) {

        Turno2Acum = leerdobleregistro(list);
        System.out.println("Turno2Acum = "+Turno2Acum);

    }

    private void leerTurno1Acum(List<Short> list) {

        Turno1Acum = leerdobleregistro(list);
        System.out.println("Turno1Acum = "+Turno1Acum);

    }

    private void leerAlarmAcum(List<Short> list) {

        AlarmaAcum = leerdobleregistro(list);
        System.out.println("AlarmaAcum = "+AlarmaAcum);

    }

    private void leerAlarmCaudMax(List<Short> list) {

        AlarmaCaudMax = leerdobleregistro(list);
        System.out.println("AlarmaCaudMax = "+AlarmaCaudMax);

    }

    private void leerAlarmCaudMin(List<Short> list) {
        AlarmaCaudMin = leerdobleregistro(list);
        System.out.println("AlarmaCaudMin = "+AlarmaCaudMin);

    }

    private void leerAcumuladoT(List<Short> list) {
        AcumuladoTotal = leerdobleregistro(list);
        System.out.println("AcumuladoTotal = "+AcumuladoTotal);

    }

    private void leerAcumuladoP(List<Short> list) {
        AcumuladoParcial = leerdobleregistro(list);
        System.out.println("AcumuladoParcial = "+AcumuladoParcial);

    }

    private void leerCaudalInstantaneo(List<Short> list) {
        CaudalInst = leerdobleregistro(list);
        System.out.println("CaudalInst = "+CaudalInst);

    }

        @Override public void stop(int numBza) {
            mHandler.removeCallbacks(Bucle);
            if(ModbusRtuMaster!=null){
                ModbusRtuMaster.destroy();
                ModbusRtuMaster=null;
                mHandler.removeCallbacks(Bucle);
            }
            Estado =M_VERIFICANDO_MODO;

            handlerThread.quit();
        }


    @Override
    public String getCaudalInstantaneo() {
        return String.valueOf(CaudalInst);
    }

    @Override
    public String getAcumuladoParcial() {
        return String.valueOf(AcumuladoParcial);
    }

    @Override
    public String getResultadoPID() {
        return String.valueOf(ResultadoPID);
    }

    @Override
    public String getProducto() {
        return "Todavia no hay brobeto";
    }

    @Override
    public String getAcumuladoTotal() {
        return String.valueOf(AcumuladoTotal);
    }

    @Override
    public String getAlarmaCaudalMinima() {
        try {
            return String.valueOf(AlarmaCaudMin/1000);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAlarmaCaudalMaxima() {
        try {
            return String.valueOf(AlarmaCaudMax/1000);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAlarmaAcumulado() {
        try {
            return String.valueOf(AlarmaAcum/1000);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getTurno1Acumulado() {
        return String.valueOf(Turno1Acum);
    }

    @Override
    public String getTurno2Acumulado() {
        return String.valueOf(Turno2Acum);
    }

    @Override
    public String getTurno3Acumulado() {
        return String.valueOf(Turno3Acum);
    }

    @Override
    public String getTurno4Acumulado() {
        return String.valueOf(Turno4Acum);
    }

    @Override
    public String getRegistroTurno() {
        return String.valueOf(RegTurno);
    }

    @Override
    public String getRPM() {
        return String.valueOf(RPM);
    }

    @Override
    public String getVelocidad() {
        return String.valueOf(VelocidadCinta);
    }

    @Override
    public String getPulsos() {
        return String.valueOf(Pulso);
    }

    @Override
    public String getTiempoCinta() {
        return String.valueOf(TiempoCinta);
    }

    @Override
    public String getPIDSetpoint() {
        try {
            return String.valueOf(PIDSetpoint/1000);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getSetpoint() {
        try {
            return String.valueOf(Setpoint/1000);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getSetpoint1Acumulado() {
        return String.valueOf(Setpoint1Acum);
    }

    @Override
    public String getSetpoint2Acumulado() {
        return String.valueOf(Setpoint2Acum);
    }

    @Override
    public String getSetpoint3Acumulado() {
        return String.valueOf(Setpoint3Acum);
    }

    @Override
    public String getSetpoint4Acumulado() {
        return String.valueOf(Setpoint4Acum);
    }

    @Override
    public String getSetpoint5Acumulado() {
        return String.valueOf(Setpoint5Acum);
    }

    @Override
    public String getDiaMes() {
        return String.valueOf(DiaMes);
    }

    @Override
    public String getAño() {
        return String.valueOf(Año);
    }

    @Override
    public String getHora() {
        return String.valueOf(Hora);
    }

    @Override
    public String getTurno1() {
        return String.valueOf(Turno1);
    }

    @Override
    public String getTurno2() {
        return String.valueOf(Turno2);
    }

    @Override
    public String getTurno3() {
        return String.valueOf(Turno3);
    }

    @Override
    public String getTurno4() {
        return String.valueOf(Turno4);
    }

    @Override
    public String getEncoder() {
        return String.valueOf(Encoder);
    }

    @Override
    public String getVelocidadSinEncoder() {
        try {
            return String.valueOf(VelocidadSinEncoder/100);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean setAcumula(Boolean Acumula) {
        Utils.EsHiloSecundario();
        List<Boolean> res=Arrays.asList(false);
        if(ModbusRtuMaster !=null){
            CountDownLatch latch = new CountDownLatch(1);
            Runnable runnable = new Runnable() {
                public void run() {
                    OnRequestBack<String> callback = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Maneja el resultado exitoso aquí
                                res.set(0, true);
                                latch.countDown();
                        }
                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí
                            res.set(0, false);

                            latch.countDown();

                        }
                    };
                    ModbusRtuMaster.writeCoil(callback, numeroSlave, 18, Acumula);
                }
            };
            try{
                latch.await(1000,TimeUnit.MILLISECONDS);
            }catch(Exception e){
                Thread.currentThread().interrupt();
                res.set(0, false);
            }
            thread.execute(runnable);
        }
        return res.get(0);
    }


    @Override
    public boolean setParadaArranque(Boolean ParadaArranque) {
        List<Boolean> res=Arrays.asList(false);
        Utils.EsHiloSecundario();
        if(ModbusRtuMaster !=null){
            CountDownLatch latch = new CountDownLatch(1);
            Runnable runnable = new Runnable() {
                public void run() {
                    OnRequestBack<String> callback = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Maneja el resultado exitoso aquí
                            res.set(0, true);
                            latch.countDown();
                        }
                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí
                            res.set(0, false);

                            latch.countDown();

                        }
                    };
                    ModbusRtuMaster.writeCoil(callback, numeroSlave, 15, ParadaArranque);
                }
            };
            try{
                latch.await(1000,TimeUnit.MILLISECONDS);
            }catch(Exception e){
                Thread.currentThread().interrupt();
                res.set(0, false);
            }
            thread.execute(runnable);
        }
        return res.get(0);
    }

    @Override
    public boolean setResetAcumTotal() {
        List<Boolean> res=Arrays.asList(false);
        Utils.EsHiloSecundario();
        if(ModbusRtuMaster !=null){
            CountDownLatch latch = new CountDownLatch(1);
            Runnable runnable = new Runnable() {
                public void run() {
                    OnRequestBack<String> callback = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Maneja el resultado exitoso aquí
                            res.set(0, true);
                            latch.countDown();
                        }
                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí
                            res.set(0, false);

                            latch.countDown();

                        }
                    };
                    ModbusRtuMaster.writeCoil(callback, numeroSlave, 17, true);
                }
            };
            try{
                latch.await(1000,TimeUnit.MILLISECONDS);
            }catch(Exception e){
                Thread.currentThread().interrupt();
                res.set(0, false);
            }
            thread.execute(runnable);
        }
        return res.get(0);
    }

    @Override
    public boolean setResetAcumParcial() {
        List<Boolean> res=Arrays.asList(false);
        Utils.EsHiloSecundario();
        if(ModbusRtuMaster !=null){
            CountDownLatch latch = new CountDownLatch(1);
            Runnable runnable = new Runnable() {
                public void run() {
                    OnRequestBack<String> callback = new OnRequestBack<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // Maneja el resultado exitoso aquí
                            res.set(0, true);
                            latch.countDown();
                        }
                        @Override
                        public void onFailed(String error) {
                            // Maneja el error aquí
                            res.set(0, false);

                            latch.countDown();

                        }
                    };
                    ModbusRtuMaster.writeCoil(callback, numeroSlave, 16, true);
                }
            };
            try{
                latch.await(1000,TimeUnit.MILLISECONDS);
            }catch(Exception e){
                Thread.currentThread().interrupt();
                res.set(0, false);
            }
            thread.execute(runnable);
        }
        return res.get(0);
    }

    @Override
    public boolean setAlarmaCaudalMin(Integer AlarmaCaudalMin) {
            final List<Boolean> res = Arrays.asList(false,false);
            CountDownLatch latch= new CountDownLatch(1);
            List<Integer> AltoBajoLista = AltoBajoLista(AlarmaCaudalMin*1000);
        OnRequestBack<String> callback2 = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                res.set(0, true);
                latch.countDown();
            }
            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                res.set(0, false);
                latch.countDown();
            }
        };
        OnRequestBack<String> callback = new OnRequestBack<String>() {
                @Override
                public void onSuccess(String result) {

                    ModbusRtuMaster.writeRegister(callback2,numeroSlave,10, AltoBajoLista.get(1));
                }
                @Override
                public void onFailed(String error) {
                }
            };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,9, AltoBajoLista.get(0));// Integer.parseInt(valueFormateado.replace(".",""))
        try{
                latch.await(2000,TimeUnit.MILLISECONDS);
            }catch(Exception e){
                Thread.currentThread().interrupt();
                res.set(0, false);
            }
            return res.get(0);
    }

    @Override
    public boolean setAlarmaCaudalMax(Integer AlarmaCaudalMax) {
        final List<Boolean> res = Arrays.asList(false,false);
        CountDownLatch latch= new CountDownLatch(1);
        List<Integer> AltoBajoLista = AltoBajoLista(AlarmaCaudalMax*1000);
        OnRequestBack<String> callback2 = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                res.set(0, true);
                latch.countDown();
            }
            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                res.set(0, false);
                latch.countDown();
            }
        };
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {

                ModbusRtuMaster.writeRegister(callback2,numeroSlave,12, AltoBajoLista.get(1));
            }
            @Override
            public void onFailed(String error) {
            }
        };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,11, AltoBajoLista.get(0));// Integer.parseInt(valueFormateado.replace(".",""))
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            res.set(0, false);
        }
        return res.get(0);
    }

    @Override
    public boolean setAlarmaAcum(Integer AlarmaAcum) {
        final List<Boolean> res = Arrays.asList(false,false);
        CountDownLatch latch= new CountDownLatch(1);
        List<Integer> AltoBajoLista = AltoBajoLista(AlarmaAcum*1000);
        OnRequestBack<String> callback2 = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                res.set(0, true);
                latch.countDown();
            }
            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                res.set(0, false);
                latch.countDown();
            }
        };
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {

                ModbusRtuMaster.writeRegister(callback2,numeroSlave,13, AltoBajoLista.get(1));
            }
            @Override
            public void onFailed(String error) {
            }
        };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,9, AltoBajoLista.get(0));// Integer.parseInt(valueFormateado.replace(".",""))
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            res.set(0, false);
        }
        return res.get(0);
    }

    @Override
    public boolean setPIDSetpoint(Integer PIDSetpoint) {
        final List<Boolean> res = Arrays.asList(false,false);
        CountDownLatch latch= new CountDownLatch(1);
        List<Integer> AltoBajoLista = AltoBajoLista(PIDSetpoint*1000);
        OnRequestBack<String> callback2 = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                res.set(0, true);
                latch.countDown();
            }
            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                res.set(0, false);
                latch.countDown();
            }
        };
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {

                ModbusRtuMaster.writeRegister(callback2,numeroSlave,30, AltoBajoLista.get(1));
            }
            @Override
            public void onFailed(String error) {
            }
        };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,29, AltoBajoLista.get(0));// Integer.parseInt(valueFormateado.replace(".",""))
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            res.set(0, false);
        }
        return res.get(0);
    }

    @Override
    public boolean setSetpoint(Integer Setpoint) {
        final List<Boolean> res = Arrays.asList(false,false);
        CountDownLatch latch= new CountDownLatch(1);
        List<Integer> AltoBajoLista = AltoBajoLista(Setpoint*1000);
        OnRequestBack<String> callback2 = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                // Maneja el resultado exitoso aquí
                res.set(0, true);
                latch.countDown();
            }
            @Override
            public void onFailed(String error) {
                // Maneja el error aquí
                res.set(0, false);
                latch.countDown();
            }
        };
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {

                ModbusRtuMaster.writeRegister(callback2,numeroSlave,32, AltoBajoLista.get(1));
            }
            @Override
            public void onFailed(String error) {
            }
        };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,31, AltoBajoLista.get(0));// Integer.parseInt(valueFormateado.replace(".",""))
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            res.set(0, false);
        }
        return res.get(0);
    }

    @Override
    public boolean setDiaMes(Integer DiaMes) {
        final List<Boolean> res = Arrays.asList(false,false);
        CountDownLatch latch= new CountDownLatch(1);
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                res.set(0, true);
            }
            @Override
            public void onFailed(String error) {
                res.set(0, false);
            }
        };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,43, DiaMes);// Integer.parseInt(valueFormateado.replace(".",""))
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            res.set(0, false);
        }
        return res.get(0);
    }



    @Override
    public boolean setAño(Integer Año) {
        final List<Boolean> res = Arrays.asList(false,false);
        CountDownLatch latch= new CountDownLatch(1);
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                res.set(0, true);
            }
            @Override
            public void onFailed(String error) {
                res.set(0, false);
            }
        };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,44, Año);// Integer.parseInt(valueFormateado.replace(".",""))
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            res.set(0, false);
        }
        return res.get(0);
    }

    @Override
    public boolean setHora(Integer Hora) {
        final List<Boolean> res = Arrays.asList(false,false);
        CountDownLatch latch= new CountDownLatch(1);
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                res.set(0, true);
            }
            @Override
            public void onFailed(String error) {
                res.set(0, false);
            }
        };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,45, Hora);// Integer.parseInt(valueFormateado.replace(".",""))
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            res.set(0, false);
        }
        return res.get(0);
    }

    @Override
    public boolean setTurno1(Integer Turno1) {
        final List<Boolean> res = Arrays.asList(false,false);
        CountDownLatch latch= new CountDownLatch(1);
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                res.set(0, true);
            }
            @Override
            public void onFailed(String error) {
                res.set(0, false);
            }
        };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,46, Turno1);// Integer.parseInt(valueFormateado.replace(".",""))
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            res.set(0, false);
        }
        return res.get(0);
    }

    @Override
    public boolean setTurno2(Integer Turno2) {
        final List<Boolean> res = Arrays.asList(false,false);
        CountDownLatch latch= new CountDownLatch(1);
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                res.set(0, true);
            }
            @Override
            public void onFailed(String error) {
                res.set(0, false);
            }
        };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,47, Turno2);// Integer.parseInt(valueFormateado.replace(".",""))
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            res.set(0, false);
        }
        return res.get(0);
    }

    @Override
    public boolean setTurno3(Integer Turno3) {
        final List<Boolean> res = Arrays.asList(false,false);
        CountDownLatch latch= new CountDownLatch(1);
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                res.set(0, true);
            }
            @Override
            public void onFailed(String error) {
                res.set(0, false);
            }
        };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,48, Turno3);// Integer.parseInt(valueFormateado.replace(".",""))
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            res.set(0, false);
        }
        return res.get(0);
    }

    @Override
    public boolean setTurno4(Integer Turno4) {
        final List<Boolean> res = Arrays.asList(false,false);
        CountDownLatch latch= new CountDownLatch(1);
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                res.set(0, true);
            }
            @Override
            public void onFailed(String error) {
                res.set(0, false);
            }
        };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,49, Turno4);// Integer.parseInt(valueFormateado.replace(".",""))
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            res.set(0, false);
        }
        return res.get(0);
    }

    @Override
    public boolean setEncoder(Boolean Encoder) {
        final List<Boolean> res = Arrays.asList(false,false);
        CountDownLatch latch= new CountDownLatch(1);
        // CASTEAR A INT PARA ENVIAR AL MODBUS
        int response = 0;
        if(Encoder){
            response=1;
        }
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                res.set(0, true);
            }
            @Override
            public void onFailed(String error) {
                res.set(0, false);
            }
        };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,50, response);// Integer.parseInt(valueFormateado.replace(".",""))
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            res.set(0, false);
        }
        return res.get(0);
    }

    @Override
    public boolean setVelocidadSinEncoder(Integer VelocidadSinEncoder) {
        final List<Boolean> res = Arrays.asList(false,false);
        CountDownLatch latch= new CountDownLatch(1);
        // CASTEAR A INT PARA ENVIAR AL MODBUS
        OnRequestBack<String> callback = new OnRequestBack<String>() {
            @Override
            public void onSuccess(String result) {
                res.set(0, true);
            }
            @Override
            public void onFailed(String error) {
                res.set(0, false);
            }
        };
        ModbusRtuMaster.writeRegister(callback,numeroSlave,51, VelocidadSinEncoder*100);// Integer.parseInt(valueFormateado.replace(".",""))
        try{
            latch.await(2000,TimeUnit.MILLISECONDS);
        }catch(Exception e){
            Thread.currentThread().interrupt();
            res.set(0, false);
        }
        return res.get(0);
    }

    @Override public void setTara(int numBza) {
            if(ModbusRtuMaster !=null){
                Runnable runnable = new Runnable() {
                    public void run() {
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

                            ModbusRtuMaster.writeCoil(callback, numeroSlave, 20, true);
                    }
                };
                thread.execute(runnable);
            }
        }

        @Override public void setCero(int numBza) {
            if(ModbusRtuMaster !=null){
                Runnable runnable = new Runnable() {
                    public void run() {
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
                            ModbusRtuMaster.writeCoil(callback, numeroSlave, 19, true);
                    }
                };
                thread.execute(runnable);
            }

        }
}
