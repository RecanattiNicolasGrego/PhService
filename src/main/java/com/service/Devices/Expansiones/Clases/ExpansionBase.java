package com.service.Devices.Expansiones.Clases;

import androidx.appcompat.app.AppCompatActivity;

import com.service.BalanzaService;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.Interfaz.Expansion;
import com.service.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;

public class ExpansionBase implements Expansion,ListenerIntermediario{
    ArrayList<Integer> Estados = new ArrayList<>(Arrays.asList(0,0,0,0,0,0,0,0,0,0,0,0));
    Integer NumeroExpansion = 0;
    static Integer Salidas=0;
    static Integer Entradas=0;
    Intermediario listener ;
    public static String Bauddef="115200";
    public static String StopBdef="1";
    public static String DataBdef="8";
    public static String Paritydef="0";
    String idModulo= "\u0001";

    Boolean isRunning=false;
    private ScheduledExecutorService scheduler;
    BalanzaService Service= BalanzaService.getInstance();
    AppCompatActivity activity=null;
    PuertosSerie serialport=null;

    public ExpansionBase(PuertosSerie Puerto,String id,AppCompatActivity activity){
    this.activity=activity;
  //  System.out.println(id);
    if(id!=null && !id.equals("1")) {
        int numero = 1;
        char caracter = (char) numero;
    //    System.out.println("El carácter para el número " + numero + " es: " + caracter);
        idModulo = String.valueOf(caracter);
    }
      //  System.out.println("id" +idModulo);
    serialport =Puerto;



    }
    @Override
    public Integer LeerEntrada(int numEntrada, int id) {
        Integer res;
        if(numEntrada<Estados.size()-1){
            res= Estados.get(numEntrada);
        }else{
            res=null;
        }
        return res;
    }

    @Override
    public Boolean SetearSalida(Boolean valor, int numSalida, int id) {
        Boolean res= false;
        Estados.set(numSalida, (valor ? 1 : 0));
        ArrayList<Integer> auxEstado =  new ArrayList<>(Estados);
        ArrayList<Integer> x= new ArrayList<Integer>();
        for (int i = 0;(auxEstado.size()+i)<16; i++) {
            x.add(0);
        }
        auxEstado.addAll(x);
        String value=Utils.bitListToString(auxEstado);
        serialport.write("@"+idModulo+"S"+value);
        return res;
    }

    @Override
    public void Stop()  {
        try {
            serialport.close();
            isRunning = ExpansionManager.getInstance().isRunning;
        } catch (IOException e) {

        }
    }

    @Override
    public Integer getSalidas() {
        return Salidas;
    }
    @Override
    public Integer getEntradas() {
        return Entradas;
    }

    @Override
    public ArrayList<Integer> getEstados() {
        return Estados;
    }

    @Override
    public void setListener(Intermediario Listener,int NumeroExpansion) {
        if (Listener != null) {
            listener = Listener;
        }
        this.NumeroExpansion = NumeroExpansion;
    }
   public void ActualizarEstados() {
        // Programar una tarea que se ejecute cada 100ms
       isRunning=true;
       new Thread(new Runnable() {
           @Override
           public void run() {

           while(isRunning) {

                   try {
                       serialport.write("@" + idModulo + "M");
                   } catch (Exception e) {
                   //    System.out.println("error escritura");


                   }
               String respuesta = null;
               try {
                   respuesta = serialport.read_2();
               } catch (Exception e) {
                //   System.out.println("error lectura");

               }
               if (respuesta != null && !respuesta.isEmpty()) {
                   respuesta = respuesta.replace(idModulo,"");
                   respuesta = respuesta.replace("@","");
                   respuesta = respuesta.replace("M","");

                //   System.out.println("Respuesta recibida: " + respuesta);
                   try {
                       String x = respuesta;
                       String parte1 = x.substring(0, 2);
                       String parte2 = x.substring(2);
                       Integer[] Estado1 = Utils.stringToBitArray(parte1+parte2);
                       if (Estado1 != null) {
                           ArrayList<Integer> auxEstado = new ArrayList<>(Arrays.asList(Estado1));
                           auxEstado.subList(auxEstado.size() - 4, auxEstado.size()).clear();
                           if (!auxEstado.equals(Estados)) {
                      //         System.out.println("SYSSIZEWOK"+auxEstado.size()+"DJJDJ"+Estados.size());
                               for (int i = 0; i < Estados.size()-1; i++) {
                                   if (!auxEstado.get(i).equals(Estados.get(i))) {
                                       listener.ListenerIntermediario(i,NumeroExpansion, auxEstado);

                                           }
                        //           System.out.println("ESTADO ACTUALIZADO"+auxEstado.get(i)+"ESTADO ANTERIOR"+Estados.get(i)+ "POSITION "+i);

                               }
                               Estados.clear();
                               if(Estados.size()==0 && auxEstado.size()==12) {
                                   Estados.addAll(auxEstado);
                               }else{
                         //          System.out.println("SIZERROR"+ auxEstado.size() + "jdfods"+Estados.size());
                               }
                           }
                       }
                   } catch (Exception e) {
                    //   System.out.println("error proceso datos"+e.getMessage());
                   }
               }else{
                      // System.out.println("vacio ...");
                   }

               try {
                   Thread.sleep(400);
               } catch (InterruptedException e) {

               }
           }
    }}).start();
    }
}

