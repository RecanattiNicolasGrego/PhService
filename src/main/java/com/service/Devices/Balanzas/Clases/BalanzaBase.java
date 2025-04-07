package com.service.Devices.Balanzas.Clases;

import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.service.BalanzaService;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.Interfaz.Balanza;
import com.service.PreferencesDevicesManager;

import java.text.DecimalFormat;

public class BalanzaBase  implements Balanza {
    public static String M_MODO_CALIBRACION="MODO_CALIBRACION";
    public static String M_MODO_BALANZA="MODO_BALANZA";
    public static String M_VERIFICANDO_MODO="VERIFICANDO_MODO";

    public BalanzaBase(String puerto, int id, AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener,int numMultipleBza) {
            this.ID = id;
            this.activity = activity;
            this.Service=BalanzaService.getInstance();
            this.fragmentChangeListener=fragmentChangeListener;
    }
    public BalanzaService Service;
    public AppCompatActivity activity;
    public String Estado = M_VERIFICANDO_MODO,Nombre="",NetoStr="",TaraStr="",BrutoStr="",picoStr="", TaraDigitalStr ="",ultimaCalibracion="",Unidad="gr";
    public float TaraDigital =0,Bruto=0,Tara=0,Neto=0,pico=0,pesoBandaCero=0F,pesoUnitario=0.5F;
    public Integer PuntoDecimal =1,numBza=0,ID=0,nBalanzas=1;
    public Handler mHandler= new Handler();
    public OnFragmentChangeListener fragmentChangeListener;
    public static final  String Bauddef="9600",StopBdef="1",DataBdef="8", Paritydef="0";
    public Boolean TieneCal=false,Tieneid=false, BandaCero =true, EstableBool =false, SobrecargaBool = false;

   /* @Override
    public Balanza getBalanza(int numBza) {
        return this;
    }*/

    @Override
    public BalanzaBase getBalanza(int numID) {
        return this;
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
        return NetoStr;
    }
    @Override public Float getBruto(int numBza) {
        return Bruto;
    }
    @Override public String getBrutoStr(int numBza) {
        return BrutoStr;
    }
    @Override public Float getTara(int numBza) {
        return Tara;
    }
    @Override public String getTaraStr(int numBza) {
        return TaraStr;
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
        return TaraDigitalStr;
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
    }
    @Override public String getUnidad(int numBza) {
        return PreferencesDevicesManager.getUnidad(Nombre,this.numBza, activity);
    }
    @Override public String getPicoStr(int numBza) {
        return picoStr;
    }
    @Override public Float getPico(int numBza) {
        return pico;
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
