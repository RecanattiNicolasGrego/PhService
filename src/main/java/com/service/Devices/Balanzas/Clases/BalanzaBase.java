package com.service.Devices.Balanzas.Clases;

import android.os.Handler;
import android.os.HandlerThread;

import androidx.appcompat.app.AppCompatActivity;

import com.service.BalanzaService;
import com.service.utilsPackage.EnumReflexion;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.Interfaz.Balanza;
import com.service.utilsPackage.PreferencesDevicesManager;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class BalanzaBase  implements Balanza {
    String RegexSololetra = "[^a-zA-Z]+";
    String RegexSoloNumero = "[^0-9.]+";

    public static String M_VERIFICANDO_MODO="VERIFICANDO_MODO";
    protected Semaphore Semaforo = new Semaphore(0);
    public Boolean band485=false;
    Class<?> clazz;
   public Boolean bandmultiplebza=false;
   public String puertoseteado="";
    public  HandlerThread handlerThread;



    public BalanzaBase(String puerto, int id, AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener, int numMultipleBza) {
            this.ID = id;
            this.activity = activity;
            band485 = id>0;
            bandmultiplebza=numMultipleBza>1;
            this.puertoseteado = puerto;
            this.Service=BalanzaService.getInstance();
            this.fragmentChangeListener=fragmentChangeListener;
            clazz = this.getClass(); // Clase real de la instancia
        handlerThread = new HandlerThread(clazz.getName()+"_"+puertoseteado);
        handlerThread.start(); // error falta memoria

         /*

        Process: com.jws.jwsapi, PID: 19555
        java.lang.OutOfMemoryError: pthread_create (1040KB stack) failed: Try again
        at java.lang.Thread.nativeCreate(Native Method)
        at java.lang.Thread.start(Thread.java:733)
        at com.service.Devices.Balanzas.Clases.BalanzaBase.<init>(BalanzaBase.java:46)
        at com.service.BalanzaService$Balanzas.getBalanza(BalanzaService.java:839)
        at com.service.Comunicacion.GestorRecursos$1.run(GestorRecursos.java:122)
        at java.lang.Thread.run(Thread.java:764)
        */
        mHandler= new Handler(handlerThread.getLooper());
    }
     protected BalanzaService Service;
     public AppCompatActivity activity;
     public String Estado = M_VERIFICANDO_MODO;

     protected String NetoStr="";
     protected String TaraStr="";
     protected String BrutoStr="";
     protected String TaraDigitalStr ="";
     protected String ultimaCalibracion="";
     protected String Unidad="kg";
     protected float TaraDigital =0;
    protected float Bruto=0;
    protected float Tara=0;
    protected float Neto=0;
    protected float pesoBandaCero=0F;
    protected float pesoUnitario=0.5F;
     protected Integer PuntoDecimal =1;
    public Integer numBza=0;
    Integer ID=0;
     static final int nBalanzas=1;
     protected Handler mHandler;
     protected OnFragmentChangeListener fragmentChangeListener;

    /* VALORES DEFAULTS */
     static String Nombre="";
     static String Bauddef="9600";
     static String StopBdef="1";
     static String DataBdef="8";
     static String Paritydef="0";
     public static String M_MODO_CALIBRACION="MODO_CALIBRACION";
     public static String M_MODO_BALANZA="MODO_BALANZA";
     static int timeout = 0 ;
     static Boolean TienePorDemanda =false;
    /*********************************************/

    public CountDownLatch latch = new CountDownLatch(1);
     Boolean TieneCal=false;
    Boolean Tieneid=false;
    protected Boolean BandaCero =true;
    protected Boolean EstableBool =false;
    protected Boolean SobrecargaBool = false;

   /* @Override
     Balanza getBalanza(int numBza) {
        return this;
    }*/

    @Override
     public Balanza getBalanza(int numID) {
        return this;
    }

      Boolean is485(){
        return band485;
    }
    public CountDownLatch resetlatch() {
        if (latch == null || latch.getCount() <= 0) {
            latch = new CountDownLatch(1); // Reseteamos el latch
        }
        return latch; // Retornamos el latch actualizado
    }
    private int gettimeout() {
        try {
            Field field = clazz.getDeclaredField(EnumReflexion.Balanzas.timeout.name());
            field.setAccessible(true);
            return (Integer) field.get(null); // null porque es static
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    public void gestorBucles(boolean lock,CountDownLatch latch)  {
        int timeout = gettimeout();
        if(band485||bandmultiplebza) {
            if (lock) {
                try {
                    Semaforo.tryAcquire();
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            } else {
                try {
                    Semaforo.release();
                   /* Boolean z = latch.await(100, TimeUnit.MILLISECONDS); // MIN TIEMPO DE BUCLE
                    if(z){
                        System.out.println("Cuidado con la balanza"+ numBza+" Esta yendo muy rapido en 485");
                        Thread.sleep(100);
                        try {
                            latch.countDown();
                        } catch (Exception e) {
                        }
                    }*/
                    if(timeout>0) {
                        Boolean x = latch.await(timeout, TimeUnit.MILLISECONDS);
                        // SI ES POR TIME PODRIA TOMARLO ACA
                        if (!x) {
                            // SI SALE POR TIMEOUT LO AVISO POR ACA
                            Thread.sleep(200);
                            System.out.println("CUANDO DEJARA DE SALIR POR TIMEOUT=?!?!");
                        }
                    }else{
                        System.out.println("programdor,tas usando el timeout default");
                        latch.await(timeout, TimeUnit.MILLISECONDS);
                        // PODRIA CONTROLAR ESTO CON EL CALLBACK DE MODBUS JIJIJAJA
                    }
                } catch (InterruptedException e) {
                    Semaforo.tryAcquire();
                }
            }
        }
    }
     public void setID(int numID, int numBza) {
        ID =numID;
    }
     public Integer getID( int numBza) {
        return ID;
    }
    @Override public Float getNeto(int numBza) {
        return Neto;
    }
    @Override public String getNetoStr(int numBza) {
        return NetoStr.replaceAll(RegexSoloNumero,"");
    }
    @Override public Float getBruto(int numBza) {
        return Bruto;
    }
    @Override public String getBrutoStr(int numBza) {
        return BrutoStr.replaceAll(RegexSoloNumero,"");
    }
    @Override public Float getTara(int numBza) {
        return Tara;
    }
    @Override public String getTaraStr(int numBza) {
        return TaraStr.replaceAll(RegexSoloNumero,"");
    }

    @Override public void setCero(int numBza) {
        setTaraDigital(numBza,0);
        Tara=0;

    }
    public void setTaraDigital(float tara){
        TaraDigital=tara;
        TaraDigitalStr =String.valueOf(tara);
    }
    @Override public void setTaraDigital(int numBza, float TaraDigital) {
        setTaraDigital(TaraDigital);
    }
    @Override public String getTaraDigital(int numBza) {
        return TaraDigitalStr.replaceAll(RegexSoloNumero,"");
    }
    public void setBandaCero(int numBza, Boolean bandaCeroi) {
        BandaCero =bandaCeroi;

    }
     public Boolean getBandaCero(int numBza) {
        return BandaCero;
    }
     public Float getBandaCeroValue(int numBza) {
        return PreferencesDevicesManager.getBandaCeroValue(Nombre,numBza, activity);
    }
     public void setBandaCeroValue(int numBza, float bandaCeroValue) {
        pesoBandaCero=bandaCeroValue;
        PreferencesDevicesManager.setBandaCeroValue(Nombre,this.numBza,bandaCeroValue, activity);
    }
    @Override public Boolean getEstable(int numBza) {
        return EstableBool;
    }
    @Override public String format(int numero,String peso) {
        String formato = "0.";
        if(peso!=null) {
            try {
                StringBuilder capacidadBuilder = new StringBuilder(formato);
                for (int i = 0; i < PuntoDecimal; i++) {
                    capacidadBuilder.append("0");
                }
                formato = capacidadBuilder.toString();
                DecimalFormat df = new DecimalFormat(formato);
                String str = df.format(Double.parseDouble(peso));
                return str;
            } catch (NumberFormatException e) {
                System.err.println("Error: El número no es válido.");
                e.printStackTrace();
                return "0";
            }
        }else{
            return "0";
        }
    }
    @Override public String getUnidad(int numBza) {
        return Unidad.replaceAll(RegexSololetra,"");//PreferencesDevicesManager.getUnidad(Nombre,this.numBza, activity);
    }





     public void start(int numBza) {
        Estado =M_MODO_BALANZA;
    }
     public Boolean calibracionHabilitada(int numBza) {
        return false;
    }

     public void openCalibracion(int numero) {

    }
     public String getEstado(int numBza) {
        return Estado;
    }
    public void setEstado(int numBza, String estado) {
        this.Estado =estado;
    }

    //    ------------------------------------------------------------------------------------
     public void stop(int numBza) {}

    public void escribir(String msj, int numBza) {}

    public void init(int numBza){
    }
    @Override public Boolean getSobrecarga(int numBza) {
        return SobrecargaBool;
    }
    Runnable Bucle = new Runnable(){
        @Override
        public void run() {

        }
    };


    @Override public void setTara(int numBza) {

    }




}
