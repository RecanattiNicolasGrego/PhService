package com.service.Devices.Balanzas.Clases.ITW410;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.service.utilsPackage.ComService;
import com.service.Comunicacion.GestorPuertoSerie;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.Devices.Balanzas.Clases.BalanzaBase;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.utilsPackage.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
public class ITW4102Bzas extends BalanzaBase implements Serializable {
    Boolean estadoEstable = false,estadoBajaBat = false,estadoBzaEnCero = false,estadoBajoCero = false,estadoPesoNeg = false,estadoNeto = false,imgbool=true,estadoCentroCero = false,estadoSobrecarga = false,PorDemandaBool=false;
    int estado410=0,numerobza=1;
    public com.service.Interfaz.OnFragmentChangeListener fragmentChangeListener;

    public static Boolean tieneid=false,TieneCal=true;
    PuertosSerie serialport;
    public static final int nBalanzas=2;


    public AppCompatActivity activity;

    String NOMBRE="ITW410";

    public static Boolean   TienePorDemanda =false;
    public static final String M_VERIFICANDO_MODO="VERIFICANDO_MODO";
    public static final String M_MODO_BALANZA="MODO_BALANZA";
    public static final String M_MODO_CALIBRACION="MODO_CALIBRACION";
    public static final String M_ERROR_COMUNICACION="M_ERROR_COMUNICACION";
    public float taraDigital=0,Bruto=0,Tara=0,Neto=0,pico=0;
    public String estable="";
    float pesoUnitario=0.5F;
    public PuertosSerie.SerialPortReader readers=null;
    float pesoBandaCero=0F;
    ExecutorService thread = Executors.newFixedThreadPool(2);

    public Boolean bandaCero =true;
    public Boolean inicioBandaPeso=false;
    public int puntoDecimal=1;
    public String ultimaCalibracion="";
    public String brutoStr="0",netoStr="0",taraStr="0",taraDigitalStr="0",picoStr="0";
    public int acumulador=0;
    private ArrayList<String> configuracionModbus;
    public  int subnombre;
    public static int timeout = 800;

    public static final String Bauddef="19200",StopBdef="1",DataBdef="8",Paritydef="0";//115200
    private ITW4102Bzas returnthiscontext(){
        return this;
    }

    @Override
    public String getUnidad(int numBza) {
        SharedPreferences preferences= ComService.getInstance().activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getString(String.valueOf(numBza)+"_"+"unidad","kg"));
    }

    @Override
    public void init(int numBza) {

            thread.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Estado=M_MODO_BALANZA;
                        pesoBandaCero= getBandaCeroValue(numBza);
                        puntoDecimal=get_PuntoDecimal();
                        ultimaCalibracion=get_UltimaCalibracion();
                        initbucle();

                        //                    }

                    } catch (Exception e) {
                        System.out.println(" ola asjdfasdf ? "+e.getMessage());
                    }
                }
            });
    }
    private void initbucle() {


        // Asociar el Handler al Looper del nuevo hilo
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Aquí va tu lógica que se repite (el "bucle")
                synchronized (this) {
                    try {
                        GET_PESO_cal_bza.run();  // O lo que necesites hacer
                        // Reprogramar el bucle cada X milisegundos
                    } catch (Exception e) {
                        System.out.println(" ola asjdfasdf ? "+e.getMessage());
                        latch.countDown();
                    }
                }
                mHandler.postDelayed(this, 1); // 1000 ms = 1 segundo
            }
        }, 1);

    }
    Runnable GET_PESO_cal_bza = new Runnable() {

        @Override
        public void run() {
                        try {
                            Semaforo.acquire();
                            latch = resetlatch();
                                String dat = serialport.writendwaitStr(Neto_bza1(), 50);
                            if(dat!=null) {
                                String x = dat;
                                Float Netoaux = Float.parseFloat(dat);

                                dat = serialport.writendwaitStr(Bruto_bza1(), 50);
                                if(dat!=null) {
                                    Float Brutoaux = Float.parseFloat(dat);
                                    String brutoStraux = String.valueOf(Brutoaux);

                                System.out.println(subnombre+" WTF "+x);
                            System.out.println(subnombre+" NETO " + Neto + " BRUTO " + Bruto + "Tara " + Tara + "");

                            if (taraDigital == 0) {
                                Neto = Netoaux;
                                netoStr = format(1,String.valueOf(Neto));
                            } else {
                                Neto = Brutoaux - taraDigital;
                                    netoStr =format(1,String.valueOf(Neto));
                            }
                            Bruto= Brutoaux;
                            brutoStr =format(1,brutoStraux);
                                }}
                                latch.countDown();
                        } catch (Exception e) {
                            latch.countDown();
                            System.out.println("ERR BUCLE  sub"+" "+subnombre+" "+e.getMessage());
                        }

        }

    };

    @Override
    public void stop(int numBza) {
        if(serialport!=null){
        }
        Estado=M_MODO_CALIBRACION;
        try {
            handlerThread.quit();
        } catch (Exception e) {

        }
    }

    @Override
    public void start(int numBza) {
        Estado=M_MODO_BALANZA;
    }

    @Override
    public Boolean calibracionHabilitada(int numBza) {
        return false;
    }



    @Override
    public void openCalibracion(int numero) {
        Estado=M_MODO_CALIBRACION;
        calibracionitw410Fragment fragment = calibracionitw410Fragment.newInstance(this, Service);
        Bundle args = new Bundle();
        args.putSerializable("instance", this);
        args.putSerializable("instanceService",Service);

        fragmentChangeListener.AbrirServiceFragment(fragment,args);
    }

    @Override
    public Boolean getSobrecarga(int numBza) {
        return null;
    }



    public ITW4102Bzas(String puerto, int id, AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener, int idaux) {
        super(puerto,id,activity,fragmentChangeListener,nBalanzas);
        try {
            try {
                serialport = GestorPuertoSerie.getInstance().initPuertoSerie(puerto,Integer.parseInt(Bauddef),Integer.parseInt(DataBdef),Integer.parseInt(StopBdef),Integer.parseInt(Paritydef),0,0);
            } finally {
            this.subnombre =idaux;
            this.numerobza = numBza;
            this.activity = activity;
            this.fragmentChangeListener=fragmentChangeListener;

            }
        } catch (NumberFormatException e) {
            System.out.println("OLAAAAAAAAAAAAAAAAAAAA!?!?!?!"+e.getMessage());
        }
    }
    public void write(String cmd){
        String data =serialport.writendwaitStr(cmd,100);
        System.out.println(cmd+" "+Errores(data));
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {

        }
    }
    public String set_cal_Division_minima(String division){
        return "\u0002"+"SKD"+subnombre+division+"\u0003";
    }
    public String set_cal_Punto_decimal(String puntodecimal){
        return "\u0002"+"SKP"+subnombre+puntodecimal+"\u0003";
    }
    public String set_cal_Capacidad(String capacidad){
        return "\u0002"+"SKM"+subnombre+capacidad +"\u0003";
    }

    public String set_cal_Peso_conocido(String pesoconocido){
        return "\u0002"+"SKV"+subnombre+pesoconocido +"\u0003";
    }
    public String set_cal_Filtro_entrada(String filtro){
        return "\u0002"+"SFU"+subnombre+filtro +"\u0003";
    }
    public String set_cal_Filtro_intermedio(String filtro){
        return "\u0002"+"SFD"+subnombre+filtro +"\u0003";
    }
    public String set_cal_Filtro_salida(String filtro){
        return "\u0002"+"SFT"+subnombre+filtro +"\u0003";
    }


    public String set_cal_Cero_inicial(String ceroinicial){
        return "\u0002"+"SKI"+subnombre+ceroinicial+"\u0003";
    }


    public String set_cal_Cero(){
        return "\u0002"+"SKC"+subnombre+"\u0003"+"k";
    }
    public String set_cal_Recero(){
        return "\u0002"+"SKZ"+subnombre+"\u0003"+"r";
    }
    public String set_cal_Span(){
        return "\u0002"+"SKS"+subnombre+"\u0003"+"{";
    }

    public String Grabar_parametros(){
        return "\u0002"+"SGP"+subnombre+"\u0003";
    }

    public String Bruto_bza1(){
        return "\u0002"+"SBA"+subnombre+"?"+"\u0003";
    }

    public String Neto_bza1(){
        return "\u0002"+"SNA"+subnombre+"?"+"\u0003";
    }
    public String Cero(){
        return "\u0002"+"SCE"+subnombre+"\u0003";
    }
    public String Errores(String lectura){
        if(lectura!=null){
            if(lectura.contains("E\r\n")){
                return "MSJ_ERROR";
            }
            if(lectura.contains("E1\r\n")){
                return "MSJ_ERROR_MEMORIA";
            }
            if(lectura.contains("E2\r\n")){
                return "MSJ_ERROR_COMANDO_INICIO";
            }
            if(lectura.contains("E3\r\n")){
                return "MSJ_ERROR_COMANDO_FIN";
            }
            if(lectura.contains("E4\r\n")){
                return "MSJ_ERROR_FALTA_CHECKSUM";
            }
            if(lectura.contains("E5\r\n")){
                return "MSJ_ERROR_CHECKSUM_INCORRECTO";
            }
            if(lectura.contains("E6\r\n")){
                return "MSJ_ERROR_APLICACION_INVALIDA";
            }
            if(lectura.contains("E7\r\n")){
                return "MSJ_ERROR_COMANDO_INVALIDO";
            }
            if(lectura.contains("E8\r\n")){
                return "MSJ_ERROR_VALOR_INVALIDO";
            }
            if(lectura.contains("E9\r\n")){
                return "MSJ_ERROR_CONFIG_PERIFERICO";
            }
            if(lectura.contains("E10\r\n")){
                return "MSJ_ERROR_TX_ACTIVA";
            }
            if(lectura.contains("E11\r\n")){
                return "MSJ_ERROR_PRODUCTOS";
            }
            if(lectura.contains("E12\r\n")){
                return "MSJ_ERROR_ENTRADA_SALIDA";
            }
            if(lectura.contains("O\r\n")){
                return "OK";

            }
            return "";
        }
        return null;
    }
    public void setPesoBandaCero(float peso){
        pesoBandaCero=peso;
        SharedPreferences preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numerobza)+"_"+"pbandacero", peso);
        ObjEditor.apply();
    }

    public float getPesoBandaCero() {
        SharedPreferences preferences=ComService.getInstance().activity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numerobza)+"_"+"pbandacero",5.0F));
    }


    public String Tara(){
        thread.execute(new Runnable() {
            @Override
            public void run() {

            }
        });

        return "";
    }

    void setFiltro(String valor, int numeroFiltro) {
        SharedPreferences preferencias = ComService.getInstance().activity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString(String.valueOf(numerobza) + "_filtro" + numeroFiltro, valor);
        editor.apply(); // O .commit() si necesitas que sea sincrónico
    }
    String get_filtro1(){
        SharedPreferences Preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numerobza)+"_"+"filtro1","OFF");
    }
    String get_filtro2(){
        SharedPreferences Preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numerobza)+"_"+"filtro2","OFF");
    }
    String get_filtro3(){
        SharedPreferences Preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numerobza)+"_"+"filtro3","OFF");
    }
    public void enviarParametros(final ArrayList<String> listavalores){ //Division minim,Pesoconocido,filtro1,filtro2,filtro3 5vals

        CountDownLatch latch2 = new CountDownLatch(1);
        try {
            ArrayList<String> lisauxx =  listavalores;
            ArrayList<Integer> lispos =new ArrayList<>(Arrays.asList(3,4,5));
            setFiltro(listavalores.get(3),1);
            setFiltro(listavalores.get(4),2);
            setFiltro(listavalores.get(5),3);
            int x = 0;
            for (int i = 0; i < lispos.size(); i++) {
                String filtro = String.valueOf(listavalores.get(lispos.get(i)));
                if(filtro.equals("OFF")){
                    lisauxx.set(lispos.get(i),"0");
                }
            }
            Runnable runnable = new Runnable() {
                public void run() {
                    write(set_cal_Capacidad(lisauxx.get(2)));
                    set_CapacidadMax(lisauxx.get(2));
                    write(set_cal_Peso_conocido(lisauxx.get(1)));
                    set_PesoConocido(lisauxx.get(1));
                    write(set_cal_Division_minima(lisauxx.get(0)));
                    set_Divmin(lisauxx.get(0));
                    write(set_cal_Filtro_entrada(lisauxx.get(3)));
                    write(set_cal_Filtro_intermedio(lisauxx.get(4)));
                    write(set_cal_Filtro_salida(lisauxx.get(5)));
                    latch2.countDown();
                    }
            };
            thread.execute(runnable);
            try {
                latch2.await();
            } catch (InterruptedException e) {

            }
        } catch (Exception e) {
            latch2.countDown();
            System.out.println("ERR EN ENVIAR PARAMETROS");
        }
    }
    public String Guardar_cal(){
        Runnable runnable = new Runnable() {
            public void run() {
              write(Grabar_parametros());
            }
        };
        thread.execute(runnable);
        return "\u0005S\r";
    }

    public String Recero_cal(){

        Runnable runnable = new Runnable() {
            public void run() {
                CountDownLatch latch = new
                        CountDownLatch(1);
                write(set_cal_Recero());
                try{
                    latch.await(1000,TimeUnit.MILLISECONDS);
                }catch (Exception e){
                    Thread.currentThread().interrupt();
                }
            }
        };
        thread.execute(runnable);
        return "";
    }
    public void setPesoUnitario(float peso){
        pesoUnitario=peso;
        SharedPreferences preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numerobza)+"_"+"punitario", 0.5F);
        ObjEditor.apply();
    }

    public float getPesoUnitario() {
        SharedPreferences preferences=ComService.getInstance().activity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numerobza)+"_"+"punitario",0.5F));
    }


    public void setUnidad(String Unidad){
        SharedPreferences preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putString(String.valueOf(numerobza)+"_"+"unidad",Unidad);
        ObjEditor.apply();
    }

    public String getUnidad() {
        //Trae la unidad producto guardado en memoria
        SharedPreferences preferences=ComService.getInstance().activity.getSharedPreferences("ITW410", Context.MODE_PRIVATE);

        return (preferences.getString(String.valueOf(numerobza)+"_"+"unidad","kg"));
    }

    public void set_DivisionMinima(int divmin){
        set_Divmin(String.valueOf(divmin));
        SharedPreferences Preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numerobza)+"_"+"div",divmin);
        ObjEditor.apply();

    }
    public void set_PuntoDecimal(int puntoDecimal){
        this.puntoDecimal=puntoDecimal;
        write(set_cal_Punto_decimal(String.valueOf(puntoDecimal)));
        SharedPreferences Preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numerobza)+"_"+"pdecimal",puntoDecimal);
        ObjEditor.apply();

    }
    public void set_UltimaCalibracion(String ucalibracion){
        SharedPreferences Preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numerobza)+"_"+"ucalibracion",ucalibracion);
        ObjEditor.apply();

    }
    public String get_UltimaCalibracion(){
        SharedPreferences Preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numerobza)+"_"+"ucalibracion","");

    }
    public void set_CapacidadMax(String capacidad){
        write(set_cal_Capacidad(capacidad));
        SharedPreferences Preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numerobza)+"_"+"capacidad",capacidad);
        ObjEditor.apply();

    }
    public void enviar_PesoConocido(String pesoCon){

    }
    public void set_Divmin(String Divmin){
        write(set_cal_Division_minima(Divmin));
        }
    public void set_PesoConocido(String pesoConocido){

        SharedPreferences Preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numerobza)+"_"+"pconocido",pesoConocido);
        ObjEditor.apply();

    }

    public int get_DivisionMinima(){
        SharedPreferences Preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getInt(String.valueOf(numerobza)+"_"+"div",0);

    }
    public Integer get_PuntoDecimal(){
        SharedPreferences Preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        int lea=Preferencias.getInt(String.valueOf(numerobza)+"_"+"pdecimal", 1);
        return lea;

    }
    public String get_CapacidadMax(){
        SharedPreferences Preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numerobza)+"_"+"capacidad","100");
    }
    public String get_PesoConocido(){
        puntoDecimal=get_PuntoDecimal();
        SharedPreferences Preferencias=ComService.getInstance().activity.getSharedPreferences("ITW410",Context.MODE_PRIVATE);
        String str= Preferencias.getString(String.valueOf(numerobza)+"_"+"pconocido","100");

        return str;
    }

    public void stopRuning(){
        Estado=M_MODO_CALIBRACION;
    }
    public void startRuning(){
        Estado=M_MODO_BALANZA;
    }

    @Override
    public void setID(int numID,int numBza) {
        this.numerobza=numID;
    }

    @Override
    public Integer getID(int numBza) {
        return this.numerobza;
    }

    public Float getNeto(int numBza) {
        return Neto;
    }
    @Override
    public String getNetoStr(int numBza) {
        return netoStr;
    }

    @Override
    public Float getBruto(int numBza) {return Bruto;
    }

    @Override
    public String getBrutoStr(int numBza) {
        return brutoStr;
    }

    @Override
    public Float getTara(int numBza) {
        return null;
    }

    @Override
    public String getTaraStr(int numBza) {
        return "0";
    }

    @Override
    public void setTara(int numBza) {
        setTaraDigital(numBza, Bruto);
    }
    public void setRecerocal(){
        Runnable runnable = new Runnable() {
            public void run() {
                if(serialport !=null){
                    CountDownLatch latch = new CountDownLatch(1);
                    Tara=0;
                    setTaraDigital(numerobza,taraDigital);
                   write(set_cal_Recero());
                    try{
                        latch.await(1000, TimeUnit.MILLISECONDS);
                    }catch (Exception e){
                        Thread.currentThread().interrupt();
                    }

                }
            }
        };
        thread.execute(runnable);
    }
    protected void salir_cal(){
        // open principal
        ComService.getInstance().fragmentChangeListener.AbrirFragmentPrincipal();
    }
    public  void setSpancal(){
        if(serialport !=null){

            Runnable runnable = new Runnable() {
                public void run() {
                    CountDownLatch latch = new CountDownLatch(1);
                    Tara=0;
                    setTaraDigital(numerobza,taraDigital);
                    write(set_cal_Span());
                    try{
                        latch.await(1000, TimeUnit.MILLISECONDS);
                    }catch (Exception e){
                        Thread.currentThread().interrupt();
                    }
                }
            };
            thread.execute(runnable);
        }

    }
    public  void setCerocal(){
        Runnable runnable = new Runnable() {
            public void run() {
                if(serialport !=null){
                    Utils.EsHiloSecundario();
                    CountDownLatch latch = new CountDownLatch(1);
                    Tara=0;
                    String data ;
                    setTaraDigital(numerobza,taraDigital);
                      write(set_cal_Cero());
                    try{
                        latch.await(1000, TimeUnit.MILLISECONDS);
                    }catch (Exception e){
                        Thread.currentThread().interrupt();
                    }

                }
            }
        };
         thread.execute(runnable);
    }
    @Override
    public void setCero(int numBza) {
        if(serialport !=null){

            Runnable runnable = new Runnable() {
                public void run() {
                    Tara=0;
                    setTaraDigital(numerobza,taraDigital);
                    write(Cero());
                }
            };
            thread.execute(runnable);
        }
        taraDigitalStr="0";
    }

    @Override
    public void setTaraDigital(int numBza, float TaraDigital) {
        taraDigital = TaraDigital;
        taraDigitalStr=String.valueOf(TaraDigital);

    }

    @Override
    public String getTaraDigital(int numBza) {
        return taraDigitalStr;
    }

    @Override
    public void setBandaCero(int numBza, Boolean bandaCeroi) {
        bandaCero=bandaCeroi;
    }

    @Override
    public Boolean getBandaCero(int numBza) {
        return bandaCero;
    }

    @Override
    public Float getBandaCeroValue(int numBza) {
        SharedPreferences preferences=ComService.getInstance().activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numBza)+"_"+"pbandacero",5.0F));
    }
    @Override
    public void setBandaCeroValue(int numBza, float bandaCeroValue) {
        pesoBandaCero=bandaCeroValue;
        SharedPreferences preferencias=ComService.getInstance().activity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numBza)+"_"+"pbandacero",bandaCeroValue);
        ObjEditor.apply();
    }

    @Override
    public Boolean getEstable(int numBza) {
        return null;
    }
    @Override
    public String getEstado(int numBza) {
        return Estado;
    }
    @Override
    public void setEstado(int numBza, String Estado) {
        this.Estado=Estado;
    }

};

