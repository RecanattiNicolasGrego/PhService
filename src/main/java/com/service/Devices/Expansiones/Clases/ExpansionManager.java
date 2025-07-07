package com.service.Devices.Expansiones.Clases;

import com.service.Interfaz.Expansion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExpansionManager implements Expansion,ListenerIntermediario.Intermediario{
        ArrayList<Integer> Estados = new ArrayList<>();
        Integer  Salidas=0;
        Integer  Entradas=0;
        private static ExpansionManager instance;
        private Map<Integer, ExpansionBase> ExpansionMap = new HashMap<>();
        private ExpansionesMessageListener listener;
        protected Boolean isRunning = true;
    public static ExpansionManager getInstance() {
        if (instance == null) {
            instance = new ExpansionManager();

        }
        return instance;
    }



    @Override
    public void ListenerIntermediario(int numEstadoNuevo,int NumeroExpansion, ArrayList<Integer> dato) {
        if (listener != null) {
            int NEstados =0;
            Integer valor =null;
            for (Map.Entry<Integer, ExpansionBase> entry : ExpansionMap.entrySet()) {
                if(entry.getKey()< NumeroExpansion){
                    NEstados = NEstados+entry.getValue().getEstados().size();
                };
                if(entry.getKey()== NumeroExpansion){
                    listener.ExpansionListener((NEstados+numEstadoNuevo+1), dato);
                }
            }
        }
    }

    @Override
    public Boolean CambiarSalida(int numero, Boolean estado) {
        try {
            SetearSalida(estado,numero,0);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Boolean LeerEntrada(int numero) {
       return LeerEntrada(numero, 0) == 1;
    }


    public interface ExpansionesMessageListener {
        void ExpansionListener(int num, ArrayList<Integer> data);
    }
    public void setListener(ExpansionManager.ExpansionesMessageListener Listener){
        this.listener=Listener; // LEAN LISTENER
    }
    private void ActualizarEstados(){

        Estados.clear();
        for (Map.Entry<Integer, ExpansionBase> entry : ExpansionMap.entrySet()) {
           Estados.addAll(entry.getValue().getEstados());
           System.out.print("Estados: ");
           for (int i = 0; i < Estados.size(); i++) {
                System.out.print(Estados.get(i));
            }
        }
    }

        public void init(){

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                while(isRunning){
                        ActualizarEstados();
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                    }

                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        actualizarsalidasentradas();
        }
        public void addExpansion(int num, ExpansionBase Expansion) {
            ExpansionMap.put(num, Expansion);
            try {
                Expansion.setListener(this,num);
            } catch (Exception e) {
                System.out.println("_AL"+e.getMessage());
            }


        }
    public void actualizarsalidasentradas(){
        for (Map.Entry<Integer, ExpansionBase> entry : ExpansionMap.entrySet()) {
            ExpansionBase Expansion = entry.getValue();
            Salidas = (Salidas + Expansion.getSalidas());
            Entradas = (Entradas + Expansion.getEntradas());
            if (Expansion.getEntradas() > 0 || Expansion.getSalidas() > 0 && !Expansion.isRunning) {
                Expansion.ActualizarEstados();
            }
            System.out.println("Salidas:" + Salidas + " Entradas:" + Entradas);
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
    public void Stop() {
        isRunning=false;
        for (Map.Entry<Integer, ExpansionBase> entry : ExpansionMap.entrySet()) {
            entry.getValue().Stop();
        }

    }
    @Override
    public Integer LeerEntrada(int numEntrada, int id) {
        Integer valor;

        if(numEntrada< Estados.size()){
            valor= Estados.get(numEntrada);
        }else{
            valor=null;

        }
        int i=0;
        int NEntradas =0;
         while(ExpansionMap.size()>i && valor==null) {
            int XEntradas = ExpansionMap.get(i).getEntradas();
            int Entrada = NEntradas +numEntrada-1;
            if(Entrada >XEntradas){// FIJARSE SI NO ES >=
                valor =ExpansionMap.get(i).LeerEntrada(numEntrada-1,ExpansionMap.get(i).getIdModulo());
            }else{
                NEntradas = NEntradas + XEntradas;
            }
            i++;
        }
        return valor;
    }

    @Override
    public Boolean SetearSalida(Boolean valor, int numSalida, int id) {
        int i=0;
        Boolean res = null;
        int NSalidas=0;
        while(ExpansionMap.size()>i && res==null) {
            int XSalidas = ExpansionMap.get(i).getSalidas();
            int Salida = NSalidas+numSalida-1;
            if(Salida>XSalidas){ // FIJARSE SI NO ES >=
                res=ExpansionMap.get(i).SetearSalida(valor,Salida-1,ExpansionMap.get(i).getIdModulo());

            }else{
                NSalidas=NSalidas+XSalidas;
            }
            i++;
        }
        return res;
    }
}
