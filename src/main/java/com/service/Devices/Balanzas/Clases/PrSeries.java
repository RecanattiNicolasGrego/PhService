package com.service.Devices.Balanzas.Clases;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.appcompat.app.AppCompatActivity;

import com.service.Comunicacion.GestorPuertoSerie;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.PreferencesDevicesManager;
import com.service.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrSeries extends  BalanzaBase implements  Serializable {
    PuertosSerie.PuertosSerieListener receiver = null;

    public PuertosSerie serialPort = null;
    private Boolean isRunning = false;
    public static final int nBalanzas = 1;
    CountDownLatch latch = new CountDownLatch(1);
    public PuertosSerie.SerialPortReader readers = null;
    public static Boolean /*Tieneid=false,*/TieneCal = false;
    public static String Nombre = "Ohaus PR Series";
    public static String StopBdef = "1";
    public static String Bauddef = "9600";
    public static String DataBdef = "8";
    public static String Paritydef = "0";

    public static Boolean TienePorDemanda = false; // SI TIENE PERO NO CREO QUE TENGA 485
    public static int timeout = 800;
    Boolean imgbool = false, inicioBandaPeso = false, estadoNeto = false, estadoPesoNeg = false, estadoBajoCero = false, estadoBzaEnCero = false, estadoBajaBat = false, estadoCentroCero = false;
    public int acumulador = 0;
    String strid = "";

    public PrSeries(String puerto, int id, AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener, int idaux) {
        super(puerto, id, activity, fragmentChangeListener, nBalanzas);
        try {
            System.out.print("INIT PrSeries" + id);
            this.serialPort = GestorPuertoSerie.getInstance().initPuertoSerie(puerto,Integer.parseInt(Bauddef),Integer.parseInt(DataBdef),Integer.parseInt(StopBdef),Integer.parseInt(Paritydef),0,0);
            Thread.sleep(300);

        } catch (InterruptedException e) {

        } finally {
            this.numBza = (this.serialPort.get_Puerto() * 100) + id; // si no tiene id seria :  10,20,3x  ; SI PUERTO 1 y 2 TIENEN ID ->(puerto.get_Puerto()*100)+numero;  Y CONTROLAR DE ALGUNA FORMA QUE ID NO TENGA 3 CIFRAS
        }
    }

    /* @Override
     public Balanza getBalanza(int numBza) {
         return this;
     }*/
    private void initbucle() {


        // Asociar el Handler al Looper del nuevo hilo
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Aquí va tu lógica que se repite (el "bucle")
                synchronized (this) {
                    try {
                        BucleporDemanda.run();  // O lo que necesites hacer
                        // Reprogramar el bucle cada X milisegundos

                    } catch (Exception e) {
                        latch.countDown();
                    }

                }
                mHandler.postDelayed(this, 1); // 1000 ms = 1 segundo
            }
        }, 1);

    }

    Runnable BucleporDemanda = new Runnable() {
        public void run() {
            String data2="";
            Integer auxPuntoDecimal = null;
            String auxBrutoStr = null;
            Float auxBruto =0f;
            char[] ArrByt = null;
            String auxNetoStr="";
            Float auxTara = 0f;
            String auxTaraString="";
            Float auxNeto=0f;
            latch = new CountDownLatch(1);
            try {
             data2 = serialPort.writendwaitStr("P\r\n", 200);

                if(data2!=null) {
                   data2 = limpiardata(data2);
                    String[] arrdata = data2.split("%");
                    for (int i = 0; i < arrdata.length; i++) {
                        switch (i){
                            case 0:{
                                if(arrdata[0].contains(".")) {
                                    int index = arrdata[0].indexOf('.');
                                    auxPuntoDecimal = arrdata[0].length() - (index+1);
                                }else{
                                    auxPuntoDecimal=0;
                                }
                                System.out.println("PUNTO DECIMAL "+PuntoDecimal);
                                auxNeto = Float.valueOf(arrdata[0]);
                                auxNetoStr = format(1,String.valueOf(auxNeto));
                                break;
                            }
                            case 1 :{
                                if(TaraDigital==0f) {
                                    auxTara = Float.valueOf(arrdata[i]);
                                    auxTaraString = format(1,String.valueOf(auxTara));
                                }else{
                                    auxTara = TaraDigital;
                                    auxTaraString = format(1,String.valueOf(auxTara));
                                }
                                break;
                            }

                        }
                    }
                    try {
                        auxBruto =auxNeto+auxTara;
                        auxBrutoStr = format(1,String.valueOf(auxBruto));
                    } catch (Exception e) {

                    }
                    if(arrdata.length>1){
                        Bruto=auxBruto;
                        BrutoStr=auxBrutoStr;
                        Neto=auxNeto;
                        NetoStr=auxNetoStr;
                        Tara=auxTara;
                        TaraStr=auxTaraString;
                        PuntoDecimal=auxPuntoDecimal ;
                    }
                }
                latch.countDown();
            } catch (Exception e) {
                System.out.println("error PR Series"+e.getMessage());
                latch.countDown();
            }
            try{
                latch.await(500, TimeUnit.MILLISECONDS);
            }catch (InterruptedException e){

            }
        }
    };

    private String limpiardata(String data) {
        String data2 = "";
        String[] lineas = data.split("\n");  // Corrección aquí
        String neto = "", tara = "";
        System.out.println("=== INICIANDO DEBUG DE limpiardata ===");
        System.out.println("DATA DE ENTRADA:\n" + data);
        for (String linea : lineas) {
            String x = linea.trim();
            System.out.println("Procesando línea: '" + x + "'");
            Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
            Matcher matcher = pattern.matcher(x);
            if (linea.contains("Net:") && matcher.find()){
                neto = matcher.group();
                String Unidadaux = null;
                try {
                    Unidadaux = linea.substring(linea.indexOf(neto)+neto.length(), linea.length()-2).trim().replace("?","");
                } catch (Exception e) {
                }
                System.out.println("Detectado NETO: " + neto);
                System.out.println("UNIDAD extraída: " + Unidadaux);
                Unidad = Unidadaux;
            }else{
                if(!linea.isEmpty()&& Utils.isNumeric(linea)) {
                    neto = linea.trim();
                }
            }

            if (linea.contains("Tare:") && matcher.find()) {
                tara = matcher.group();
                System.out.println("Detectado TARA: " + tara);
            }else{
                if(!linea.isEmpty() ) {
                    tara = format(1,"0");
                }
            }
        }

        data2 = neto + "%" + tara;
        System.out.println("RESULTADO FINAL: " + data2);
        System.out.println("=== FIN DEBUG ===");

        return data2;
    }

    private HandlerThread handlerThread;

    @Override
    public void init(int numBza) {
        new Thread(new Runnable() {
            @Override
            public void run() {
        handlerThread = new HandlerThread("HiloVerificandoModo");
        mHandler.post(() -> {
            Estado = M_VERIFICANDO_MODO;
            isRunning = true;

            pesoUnitario = PreferencesDevicesManager.getPesoUnitario(Nombre, numBza, activity);
            pesoBandaCero = PreferencesDevicesManager.getPesoBandaCero(Nombre, numBza, activity);
            PuntoDecimal = PreferencesDevicesManager.getPuntoDecimal(Nombre, numBza, activity);
            ultimaCalibracion = PreferencesDevicesManager.getUltimaCalibracion(Nombre, numBza, activity);
            System.out.println("serialPort es null ?"+ (serialPort==null));
            if (serialPort != null) {
                initbucle();
                //iniciarBucle();
            }
        });

            }
        }).start();
    }




    @Override
    public void escribir(String msj, int numBza) {
        serialPort.write(msj);
    }

    //    public String Cero(){
//        serialPort.write("KZERO\r\n");
//        setTaraDigital(0);
//        return "KZERO\r\n";
//    }


    @Override
    public void setTaraDigital(int numBza, float TaraDigital) {
        if (serialPort != null) {
            serialPort.write( TaraDigital+"T" + "\r\n");
        }
    }


    @Override
    public void setTara(int numBza) {
        if (serialPort != null) {
            serialPort.write( "T" + "\r\n");
        }
    }

    @Override
    public Float getTara(int numBza) {
        return Tara;
    }

    @Override
    public String getTaraStr(int numBza) {
        return TaraStr;
    }

    @Override
    public void setCero(int numBza) {
        if (serialPort != null) {
            serialPort.write("Z" + "\r\n");
        }
        setTaraDigital(numBza, 0);
        Tara = 0;
    }


    @Override
    public void stop(int numBza) {
        isRunning = false;
        try {
            serialPort.close();
        } catch (IOException e) {

        }
        serialPort = null;
        try {
            readers.stopReading();
        } catch (Exception e) {

        }
        readers = null;
        Estado = M_VERIFICANDO_MODO;
        mHandler.removeCallbacks(BucleporDemanda);
        handlerThread.quit();
    }


    @Override
    public Boolean calibracionHabilitada(int numBza) {
        return true;
    }

}


    //    public String format(String numero) {
//        String formato = "0.";
//        try {
//            StringBuilder capacidadBuilder = new StringBuilder(formato);
//            for (int i = 0; i <puntoDecimal; i++) {
//                capacidadBuilder.append("0");
//            }
//            formato = capacidadBuilder.toString();
//            DecimalFormat df = new DecimalFormat(formato);
//            String str = df.format(Double.parseDouble(numero));
//            return str;
//        } catch (NumberFormatException e) {
//            System.err.println("Error: El número no es válido.");
//            e.printStackTrace();
//            return "0";
//        }
//    }
//    @Override

    //    ------------------------------------------------------------------------------------

