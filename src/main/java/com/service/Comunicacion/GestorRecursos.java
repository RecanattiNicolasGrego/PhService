package com.service.Comunicacion;


import com.service.PHService;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.Devices.Balanzas.Clases.BalanzaBase;
import com.service.utilsPackage.PreferencesDevicesManager;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GestorRecursos {
    ArrayList<Thread> hilos = new ArrayList<Thread>() ;

    ArrayList<Integer> Puerto1= new ArrayList<>();
    ArrayList<Integer> Puerto2= new ArrayList<>();
    ArrayList<Integer> Puerto3= new ArrayList<>();
    Boolean[] isrunning= {false,false,false};
    private static GestorRecursos Instance;
    public static GestorRecursos getinstance(){
        if(Instance==null){
            Instance= new GestorRecursos();

        }
        return Instance;
    }
private int Lockeartodos(ArrayList<Integer> x,int id) {
        CountDownLatch latch =new CountDownLatch(1);
        int i =0;
    for(Integer z:x){
        BalanzaBase bza = (BalanzaBase) PHService.Instancia().Balanzas.getBalanza(z);
        if(bza.activity != null) {
            try {
                bza.gestorBucles(true, new CountDownLatch(1));  // Bloquea la balanza
                System.out.println("LOCK " + z);
                i++;
            } catch (Exception e) {
                System.out.println("⚠️ Error al bloquear balanza " + z+" errmens "+e.getMessage());
            }
        }
        if(z.equals(x.get(x.size()-1))){
            latch.countDown();
        }
    }
    try {
        latch.await(1000, TimeUnit.MILLISECONDS);
        isrunning[id]=true;
    } catch (InterruptedException e) {

    }

    return i;
}
    private void iniciarRecursos(){
         Puerto1= new ArrayList<>();
         Puerto2= new ArrayList<>();
         Puerto3= new ArrayList<>();
        BalanzaBase bzs = (BalanzaBase) PHService.Instancia().Balanzas.getBalanza(1);
        int i=2;
        while(bzs.activity!=null){
            if(Objects.equals(PuertosSerie.StrPortA, bzs.puertoseteado)){ //  && (bzas.get(i).getID()>0) //PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(0))
                Puerto1.add(i-1);
            }
            if(Objects.equals(PuertosSerie.StrPortB,  bzs.puertoseteado) ){ // && bzas.get(i).getID()>0 PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(1))
                Puerto2.add(i-1);
            }
            if(Objects.equals(PuertosSerie.StrPortC, bzs.puertoseteado) ){ // && bzas.get(i).getID()>0 PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(2))
                Puerto3.add(i-1);
            }
            System.out.println("i: "+(i-1)+" "+PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(0))+bzs.puertoseteado+"");
            bzs =(BalanzaBase) PHService.Instancia().Balanzas.getBalanza(i);
            i++;
        }
        // o:
       /* ArrayList<classDevice> bzas=  PreferencesDevicesManager.get_listPorTipo(0,ComService.getInstance().activity);
        for(int i=0;i<bzas.size();i++){
           // BalanzaBase x = BalanzaService.getInstance().Balanzas.getBalanza(i);
                // PODRIA ACCEDER A NBALANZAS
            System.out.println(i+" "+bzas.get(i).getSalida()+" "+bzas.get(i).getID()+"    "+PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(0)));
            if(Objects.equals(PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(0)), bzas.get(i).getSalida())){ //  && (bzas.get(i).getID()>0)
                Puerto1.add(i+1);
            }
            if(Objects.equals(PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(1)), bzas.get(i).getSalida()) ){ // && bzas.get(i).getID()>0
                Puerto2.add(i+1);
            }
            if(Objects.equals(PreferencesDevicesManager.salidaMap.get(PreferencesDevicesManager.listaKeySalidaMap.get(2)), bzas.get(i).getSalida()) ){ // && bzas.get(i).getID()>0
                Puerto3.add(i+1);
            }

        }*/
        initpuertogestor(Puerto1,0);
        initpuertogestor(Puerto2,1);
        initpuertogestor(Puerto3,2);
    }
    public void initpuertogestor(final ArrayList<Integer> arrayList, final int id){
        Boolean band485= null;
        try {
            BalanzaBase x = (BalanzaBase) PHService.Instancia().Balanzas.getBalanza(arrayList.get(0));
            band485 = false;
            if(x.activity!=null){
                band485 = x.band485;
                if(!band485){
                    band485= x.bandmultiplebza;
                }
            }
            System.out.println("BAND485 dat"+band485+" "+arrayList.size()+x.band485+" "+x.bandmultiplebza);
        } catch (Exception e) {
        }
        System.out.println("BAND485"+band485+" "+arrayList.size()+ "ola ? "+id);
        if(!arrayList.isEmpty() && Boolean.TRUE.equals(band485)) {
            Runnable runnable = new Runnable() {
                public void run() {
                    getinstance().Lockeartodos(arrayList,id);
                    while(isrunning[id]) {
                        for (Integer i = 0; arrayList.size() > i; i++) {
                            System.out.println("UNLOCK " +id+" "+ arrayList.get(i));
                            BalanzaBase z = (BalanzaBase) PHService.Instancia().Balanzas.getBalanza(arrayList.get(i));
                            if(z.Estado.equals(BalanzaBase.M_MODO_BALANZA)) {
                                CountDownLatch latch = z.resetlatch();
                                z.gestorBucles(false,latch);
                            }
                        }
                    }
                }
            };

            System.out.println("SIZE: "+ hilos.size()+" id:"+id);
            Thread hiloExistente = hilos.get(id);

            if (hiloExistente != null && hiloExistente.isAlive()) {
                hiloExistente.interrupt();
            }
            Thread nuevoHilo = new Thread(runnable);
            hilos.set(id, nuevoHilo);
            nuevoHilo.start();
        }
    }
    public void shutdown(int id){
        System.out.println("shutdown "+id);
        if(hilos.get(id-1)!=null) {
            if (hilos.get(id-1).isAlive()) {
                hilos.get(id-1).interrupt();
                isrunning[id-1]=false;
            }
        }
    }
    public void unshutdown(int id){
        System.out.println("unshutdown "+id);
        isrunning[id-1 ]=true;
        switch (id){
            case 1:{
                initpuertogestor(Puerto1,id-1);
                break;
            }
            case 2:{
                initpuertogestor(Puerto2,id-1);
                break;
            }
            case 3:{
                initpuertogestor(Puerto3,id-1);
                break;
            }
        }


    }
    public void stop(){
        for(Thread hilo: hilos){
           int  id=0;
            if(hilo!=null) {
                if (hilo.isAlive()) {
                    hilo.interrupt();
                }
            }
            isrunning[id]=false;
            id++;
        }

    }
    public void init(){
       // isrunning=true;
        while (hilos.size() < 3) {
            hilos.add(null);
        }
        iniciarRecursos();



    }
    public void reiniciar() {
        stop();
        hilos.clear();
      //  init();
    }
}
