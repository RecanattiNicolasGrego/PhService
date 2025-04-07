package com.service;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.service.estructuras.classDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public  class PreferencesDevicesManager {


    public static final Map<String, List<String>> deviceMap = new LinkedHashMap<>();
    public static final Map<String, String> salidaMap = new HashMap<>();

    public static final ArrayList<String> DefConfig = new ArrayList<String>();
    static {
        DefConfig.add("9600");
        DefConfig.add("1");
        DefConfig.add("8");
        DefConfig.add("0");
//-----
        salidaMap.put("Puerto Serie 1", "PuertoSerie 1");
        salidaMap.put("Puerto Serie 2", "PuertoSerie 2");
        salidaMap.put("Puerto Serie 3", "PuertoSerie 3");
        salidaMap.put("Red", "Red");
        salidaMap.put("Bluetooth", "Bluetooth");
        salidaMap.put("USB", "USB");
        //---------
        deviceMap.put("Balanza", obtenerAliasDeModelos(BalanzaService.ModelosClasesBzas.values()));
        deviceMap.put("Impresora", Arrays.asList("ZEBRA"));
        deviceMap.put("Expansion", obtenerAliasDeModelos(BalanzaService.ModelosClasesExpansiones.values()));
        deviceMap.put("Escaner", Arrays.asList("Escaner"));
        deviceMap.put("Dispositivo",obtenerAliasDeModelos(BalanzaService.ModelosClasesDispositivos.values()));
        deviceMap.put("default",Arrays.asList( "default"));

    }
    private static List<String> listaTipos = obtenerListaTipos();private static String ultimaClave = listaTipos.get(listaTipos.size() - 1);
    public static String  valordef = deviceMap.get(ultimaClave).get(0);
    //------------------------------------------funciones Data -----------------------------------

 /* private ArrayList<classDevice> ordenarLista(ArrayList<classDevice> lista){
        ArrayList<classDevice> lisaux0=new ArrayList<>();
        ArrayList<classDevice> lisaux1=new ArrayList<>();
        ArrayList<classDevice> lisaux2=new ArrayList<>();
        ArrayList<classDevice> lisaux3=new ArrayList<>();
        ArrayList<classDevice> lisaux4=new ArrayList<>();
        ArrayList<classDevice> lisres=new ArrayList<>();
        for (classDevice devic:lista) {
            switch (devic.getTipo()){
                case "Balanza":{
                    lisaux0.add(devic);
                    break;
                }
                case "Impresora":{
                    lisaux1.add(devic);
                    break;
                }
                case "Expansion":{
                    lisaux2.add(devic);
                    break;
                }case "Escaner":{
                    lisaux3.add(devic);
                    break;
                }
                case "Dispositivo":{
                    lisaux4.add(devic);
                    break;
                }
            }
        }
        for (classDevice devic:lisaux0) {
            lisres.add(devic);
        }
        for (classDevice devic:lisaux1) {
            lisres.add(devic);
        }
        for (classDevice devic:lisaux2) {
            lisres.add(devic);
        }
        for (classDevice devic:lisaux3) {
            lisres.add(devic);
        }
        for (classDevice devic:lisaux4) {
            lisres.add(devic);
        }
        return lisres;
    }
    public static List<classDevice> organizarDispositivos(List<classDevice> lista) {
        Map<String, List<classDevice>> dispositivosPorTipo = new LinkedHashMap<>();
        for (classDevice device : lista) {
            String tipo = device.getTipo();
            if (!dispositivosPorTipo.containsKey(tipo)) {
                dispositivosPorTipo.put(tipo, new ArrayList<>());
            }
            dispositivosPorTipo.get(tipo).add(device);
        }
        List<classDevice> dispositivosOrganizados = new ArrayList<>();
        for (Map.Entry<String, List<classDevice>> entry : dispositivosPorTipo.entrySet()) {
            dispositivosOrganizados.addAll(entry.getValue());
        }
        return dispositivosOrganizados;
    }*/


    public static String obtenerClavePorValor(Map<String, String> map, String valor) {
     for (Map.Entry<String, String> entry : map.entrySet()) {
         if (entry.getValue().equals(valor)) {
             return entry.getKey();
         }
     }
     return null; // Si no se encuentra el valor
 }
    public static String obtenerValorPorClave(Map<String, String> map, String clave) {
        return map.get(clave); // Devuelve el valor o null si no existe la clave
    }

    public static ArrayList<Integer> getBalanzas(AppCompatActivity activity) {
     SharedPreferences Preferencias = activity.getSharedPreferences("devicesService", Context.MODE_PRIVATE);
     String tipo="";
     ArrayList<Integer> balanzas = new ArrayList<>();
     int num=0;
     while(!tipo.equals("fin")){
         tipo= Preferencias.getString("Tipo_"+num,"fin");
         if(tipo.equals("fin")&&num==0){
             tipo="Balanza";
         }
         if(tipo.contains("Balanza")){
             String Modelobza = Preferencias.getString("Modelo_"+num,"Optima");
             balanzas.add(obtenerIndiceModeloPorTipo(0,Modelobza));
         }
         num++;
     }
     return balanzas;
 }
    public static List<String> obtenerListaTipos(){
        List<String> ordenTipos = new ArrayList<>();
        ordenTipos.addAll(deviceMap.keySet());
        return ordenTipos;
    };
    public static List<classDevice> organizarDispositivos(List<classDevice> lista ) {
        Map<String, List<classDevice>> dispositivosPorTipo = new LinkedHashMap<>();
        List<String> ordenTipos = obtenerListaTipos();
        for (classDevice device : lista) {
            String tipo = device.getTipo();
            if (!dispositivosPorTipo.containsKey(tipo)) {
                dispositivosPorTipo.put(tipo, new ArrayList<>());
            }
            dispositivosPorTipo.get(tipo).add(device);
        }
        List<classDevice> dispositivosOrganizados = new ArrayList<>();
        for (String tipo : ordenTipos) {
            if (dispositivosPorTipo.containsKey(tipo)) {
                dispositivosOrganizados.addAll(dispositivosPorTipo.get(tipo));
            }
        }
        return dispositivosOrganizados;
    }
    public static List<String>  obtenerAliasDeModelos(Enum<?>[] modelos) {
        List<String> aliasList = new ArrayList<>();
        for (Enum<?> modelo : modelos) {
            aliasList.add(modelo.name());
        }
        return aliasList;
    }
    public static  void addDevice(classDevice x, AppCompatActivity activity){
        int cont =0; //get_nborrados();
        int count=0;
        int size=0;
        String Tipo=x.getTipo();
        String Modelo=x.getModelo();
        int balanzalenght= (x.getND()); //pos;//List.size();
        SharedPreferences Preferencias= activity.getApplicationContext().getSharedPreferences("devicesService", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();

        ObjEditor.putBoolean(String.valueOf("seteo_"+balanzalenght), x.getSeteo());
        if(Tipo.contains(valordef)){
            ObjEditor.putString(String.valueOf("Baud_"+balanzalenght), x.getDireccion().get(0));
            ObjEditor.putString(String.valueOf("DataB_"+balanzalenght), x.getDireccion().get(2));
            ObjEditor.putString(String.valueOf("StopB_"+balanzalenght), x.getDireccion().get(1));
            ObjEditor.putString(String.valueOf("Parity_"+balanzalenght), x.getDireccion().get(3));
            ObjEditor.putString(String.valueOf("Direccion_"+balanzalenght), x.getDireccion().get(0));
        }
        if(x.getSalida().contains("PuertoSerie")){
            ObjEditor.putString(String.valueOf("Baud_"+balanzalenght), x.getDireccion().get(0));
            ObjEditor.putString(String.valueOf("DataB_"+balanzalenght), x.getDireccion().get(2));
            ObjEditor.putString(String.valueOf("StopB_"+balanzalenght), x.getDireccion().get(1));
            ObjEditor.putString(String.valueOf("Parity_"+balanzalenght), x.getDireccion().get(3));
        }else if(x.getSalida().contains("Red")||x.getSalida().contains("Bluetooth")){
            ObjEditor.putString(String.valueOf("Direccion_"+balanzalenght), x.getDireccion().get(0));
        } else if (x.getSalida().contains("USB")) {}
        ObjEditor.putString(String.valueOf("Tipo_"+balanzalenght), Tipo);
        ObjEditor.putString(String.valueOf("Salida_"+balanzalenght),x.getSalida());
        if(Modelo!=null)ObjEditor.putString(String.valueOf("Modelo_"+balanzalenght),Modelo);
        ObjEditor.putInt(String.valueOf("ID_"+balanzalenght),x.getID());
        ObjEditor.apply();
    }
    public static int obtenerIndiceModeloPorTipo(int tipo, String modelo) {
        List<String> keys = new ArrayList<>(deviceMap.keySet());

        String tipox= keys.get(tipo);
       /* switch (tipo){
            case 0:{
                tipox ="Balanza";
                break;
            }
            case 1:{
                tipox="Impresora";
                break;
            }
            case 2:{tipox="Expansion" ;
            break;
            }
            case 3:{
                tipox="Escaner";
                break;
            }
            case 4:{
                tipox="Dispositivo";
                break;
            }
        }*/

        List<String> modelos = deviceMap.get(tipox);
        if (modelos != null) {
            return modelos.indexOf(modelo);
        }
        return -1;
    }
    public static int obtenerIndiceTipo(String tipo) {
        List<String> tipos = new ArrayList<>(deviceMap.keySet());
        return tipos.indexOf(tipo);
    }
  /*  public static boolean[] get_numeroSalidasBZA(AppCompatActivity activity){
        SharedPreferences Preferencias = activity.getApplicationContext().getSharedPreferences("devicesService", Context.MODE_PRIVATE);
        String tipo="";
        int num=0;
        int numeroSalidas=0;
        boolean[] ArrayBoolNumSalidas = new boolean[7];
        while(!tipo.equals("fin")) {
            tipo = Preferencias.getString("Tipo_" + num, "fin");
            if(tipo.contains("fin") && num==0){
                tipo="Balanza";
            }
            if(!tipo.equals(valordef)&&!tipo.equals("fin")) {
                String Salida = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
                Collection<String> values = salidaMap.values();
                List<String> valueList = new ArrayList<>(values);
                if (salidaMap.containsKey(Salida)) {
                    int index = valueList.indexOf(Salida);
                    if (!ArrayBoolNumSalidas[index]) {
                        ArrayBoolNumSalidas[index] = true;
                        numeroSalidas++;
                    }
                }
            }
            num++;
        }
        return ArrayBoolNumSalidas;//numeroSalidas;
    }*/
  public static boolean[] get_numeroSalidasBZA(AppCompatActivity activity){
      SharedPreferences Preferencias = activity.getSharedPreferences("devicesService", Context.MODE_PRIVATE);
      String tipo="";
      int num=0;
      int numeroSalidas=0;
      boolean[] array = new boolean[salidaMap.size()];
      while(!tipo.equals("fin")) {
          tipo = Preferencias.getString("Tipo_" + num, "fin");
          /*if(tipo.contains("fin") && num==0){
              tipo="Balanza";
          }*/
          if(!tipo.equals("Borrado")&&!tipo.equals("fin")) {
              String Salida = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
              int index =0;
              for (String key : salidaMap.keySet()) {
                  if (Objects.equals(salidaMap.get(key), Salida)) {
                       if (!array[index]) {
                          array[index] = true;
                          numeroSalidas++;
                      }
                  }
                  index++;
              }
          }
          num++;
      }
      return array;//numeroSalidas;*/
  }
    public static ArrayList<classDevice> get_listIndexPorTipo(int TipoDevice,AppCompatActivity activity){
        String tipodevicestr=obtenerTipoPorIndice(TipoDevice);

        ArrayList<classDevice> ArrayBalanzas = new ArrayList<classDevice>();
        SharedPreferences Preferencias = activity.getApplicationContext().getSharedPreferences("devicesService", Context.MODE_PRIVATE);
        String tipo="";
        int numdevl=1;
        int num=0;
        int numbza=0;
        while(!tipo.equals("fin")) {
            classDevice balanza= new classDevice();
            tipo = Preferencias.getString("Tipo_" + num, "fin");

            if(tipo.contains("fin") && num==0){
                tipo="Balanza";
            }

            String Salidaaux = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
            if(!tipo.equals(tipodevicestr)){ // es probable que sea mejor (!tipo.equals(tipodevicestr))
                numbza++;
            }else if(!tipo.equals("fin")) {
                    balanza.setND(numbza);
                    balanza.setNDL(numdevl);
                    numdevl++;
                    numbza++;
                    balanza.setSalida(Salidaaux);
                    ArrayBalanzas.add(balanza);
            }
            if(tipo.equals("fin") && ArrayBalanzas.isEmpty()){
                classDevice balanx = new classDevice();
                balanx.setID(-1);
                balanx.setSalida("-1");
                ArrayList<String> diraux= new ArrayList<String>();
                balanx.setDireccion(diraux);
                balanx.setTipo(obtenerTipoPorIndice(TipoDevice));
                balanx.setND(numbza-1);
                balanx.setNDL(0);
                balanx.setSeteo(false);
                ArrayBalanzas.add(balanx);
            }
            num++;
        }
        return ArrayBalanzas;
    }
    public static ArrayList<classDevice> get_listIndex(AppCompatActivity activity){
        ArrayList<classDevice> ArrayBalanzas = new ArrayList<classDevice>();
        SharedPreferences Preferencias = activity.getApplicationContext().getSharedPreferences("devicesService", Context.MODE_PRIVATE);
        String tipo="";
        int num=0;
        int numbza=0;
        int numdevl=1;
        while(!tipo.equals("fin")) {
            classDevice balanza= new classDevice();
            tipo = Preferencias.getString("Tipo_" + num, "fin");
            if(tipo.contains("fin") && num==0){
                tipo="Balanza";
            }

            if(tipo.equals(valordef)){ // es probable que sea mejor (!tipo.equals(tipodevicestr))
                numbza++;
            }else{
                if(!tipo.equals("fin")) {
                    balanza.setND(numbza);
                    balanza.setNDL(numdevl);
                    numdevl++;
                    numbza++;
                    ArrayBalanzas.add(balanza);
                }
            }
            if(tipo.equals("fin") && ArrayBalanzas.isEmpty()){
                classDevice balanx = new classDevice();
                balanx.setID(-1);
                balanx.setSalida("-1");
                ArrayList<String> diraux= new ArrayList<String>();
                balanx.setDireccion(diraux);
                balanx.setTipo(valordef);
                balanx.setND(numbza-1);
                balanx.setNDL(0);
                balanx.setSeteo(false);
                ArrayBalanzas.add(balanx);
            }
            num++;
        }
        return ArrayBalanzas;
    }
    public static  ArrayList<classDevice> get_listPorTipo(int TipoDevice,AppCompatActivity activity){
        String tipodevicestr=obtenerTipoPorIndice(TipoDevice);
        ArrayList<classDevice> ArrayBalanzas = new ArrayList<classDevice>();
        SharedPreferences Preferencias = activity.getApplicationContext().getSharedPreferences("devicesService", Context.MODE_PRIVATE);
        String tipo="";
        int num=0;
        int numbza=0;
        int numdevl=1;
        while(!tipo.equals("fin")) {
            classDevice balanza= new classDevice();
            tipo = Preferencias.getString("Tipo_" + num, "fin");
            if(tipo.contains("fin") && num==0){
                tipo="Balanza";
            }
            if(!tipo.equals(tipodevicestr)){
                numbza++;
            }
            if(tipo.equals(tipodevicestr)) {
                balanza.setND(numbza);
                balanza.setNDL(numdevl);
                numbza++;
                numdevl++;
                Boolean seteo = Preferencias.getBoolean("seteo_" + num, false);
                String Modelobza="";
                Modelobza = Preferencias.getString("Modelo_" + num, BalanzaService.ModelosClasesBzas.values()[0].name());
                String Salidaaux = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
                int NumeroID = Preferencias.getInt("ID_" + num, 1);
                ArrayList<String> listaux = new ArrayList<String>();
                if (Salidaaux.contains("PuertoSerie")) {
                    String baud = Preferencias.getString("Baud_" + num, "9600");
                    String dataB = Preferencias.getString("DataB_" + num, "1");
                    String stopB = Preferencias.getString("StopB_" + num, "8");
                    String parity = Preferencias.getString("Parity_" + num, "0");
                    listaux.add(baud);

                    listaux.add(dataB);
                    listaux.add(stopB);
                    listaux.add(parity);
                }else if(Salidaaux.contains("Red")||Salidaaux.contains("Bluetooth")){
                    String Direccion = Preferencias.getString("Direccion_" + num, "1.1.1.1");
                    listaux.add(Direccion);
                }
                if ((ArrayBalanzas.isEmpty())) {
                    listaux.add("");
                    listaux.add("");
                    listaux.add("");
                    listaux.add("");
                    listaux.add("");

                }

                balanza.setID(NumeroID);
                balanza.setSalida(Salidaaux);
                balanza.setDireccion(listaux);

                if(existeTipo(tipo)){
                    if(obtenerModelosDeTipo(tipo).contains(Modelobza)){
                        balanza.setModelo(Modelobza);
                    }else{
                        balanza.setModelo(valordef);

                    }
                    balanza.setTipo(tipo);
                }else{
                    balanza.setModelo(valordef);
                    balanza.setTipo(valordef);
                }
                balanza.setSeteo(seteo);
                ArrayBalanzas.add(balanza);
            }
            num++;
        }
        return ArrayBalanzas;
    }

    public  static ArrayList<classDevice> get_list(AppCompatActivity activity){
        ArrayList<classDevice> ArrayBalanzas = new ArrayList<classDevice>();
        SharedPreferences Preferencias = activity.getApplicationContext().getSharedPreferences("devicesService", Context.MODE_PRIVATE);
        String tipo="";
        int num=0;
        Map<String, Integer> tipoCounters = new HashMap<>();
        for (String tipox : deviceMap.keySet()) {
            tipoCounters.put(tipox, 1);
        }
        while(!tipo.equals("fin")) {
            classDevice balanza= new classDevice();
            tipo = Preferencias.getString("Tipo_" + num, "fin");
            if(!tipo.equals(valordef) && !tipo.equals("fin")) {
                balanza.setND(tipoCounters.get("Balanza"));
                Boolean seteo = Preferencias.getBoolean("seteo_" + num, false);
                String Modelobza = "";
                Modelobza = Preferencias.getString("Modelo_" + num, BalanzaService.ModelosClasesBzas.values()[0].name());
                String Salidaaux = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
                int NumeroID = Preferencias.getInt("ID_" + num, 1);
                ArrayList<String> listaux = new ArrayList<String>();
                if (Salidaaux.contains("PuertoSerie")) {
                    String baud = Preferencias.getString("Baud_" + num, "9600");
                    String dataB = Preferencias.getString("DataB_" + num, "1");
                    String stopB = Preferencias.getString("StopB_" + num, "8");
                    String parity = Preferencias.getString("Parity_" + num, "0");
                    listaux.add(baud);
                    listaux.add(dataB);
                    listaux.add(stopB);
                    listaux.add(parity);
                } else if (Salidaaux.contains("Red") || Salidaaux.contains("Bluetooth")) {
                    String Direccion = Preferencias.getString("Direccion_" + num, "1.1.1.1");
                    listaux.add(Direccion);
                }
                if ((ArrayBalanzas.isEmpty())) {
                    listaux.add("");
                    listaux.add("");
                    listaux.add("");
                    listaux.add("");
                    listaux.add("");
                }
                balanza.setModelo(Modelobza);
                balanza.setID(NumeroID);
                balanza.setSalida(Salidaaux);
                balanza.setDireccion(listaux);
                if(tipoCounters.containsKey(tipo)){
                    balanza.setTipo(tipo);
                    balanza.setNDL(tipoCounters.get(tipo));
                    tipoCounters.put(tipo, tipoCounters.get(tipo) + 1);
                    balanza.setSeteo(seteo);
                    ArrayBalanzas.add(balanza);
                }
            }
            num++;
        }
        return ArrayBalanzas;
    }
    public static  ArrayList<classDevice> get_listPorSalida(String Salida, int TipoDevice,AppCompatActivity activity){
        String tipodevicestr=obtenerTipoPorIndice(TipoDevice);
        String Salidastr = "";
        if (salidaMap.containsKey(Salida)) {
            Salidastr = salidaMap.get(Salida);
        }

        ArrayList<classDevice> ArrayBalanzas = new ArrayList<classDevice>();
        SharedPreferences Preferencias = activity.getApplicationContext().getSharedPreferences("devicesService", Context.MODE_PRIVATE);
        String tipo="";
        int num=0;
        int numbza=0;
        int numdevl=1;
        while(!tipo.equals("fin")) {
            classDevice balanza= new classDevice();
            tipo = Preferencias.getString("Tipo_" + num, "fin");
            if(tipo.contains("fin") && num==0){
                tipo="Balanza";
            }
            if(!tipo.equals(tipodevicestr)){
                numbza++;
            }
            if(tipo.equals(tipodevicestr)) {
                balanza.setND(numbza);
                balanza.setNDL(numdevl);
                numdevl++;
                numbza++;
                Boolean seteo = Preferencias.getBoolean("seteo_" + num, false);
                String Modelobza="";
                Modelobza = Preferencias.getString("Modelo_" + num, BalanzaService.ModelosClasesBzas.values()[0].name());
                String Salidaaux = Preferencias.getString("Salida_" + num, "PuertoSerie 1");
                int NumeroID = Preferencias.getInt("ID_" + num, 1);
                ArrayList<String> listaux = new ArrayList<String>();
                if (Salidastr.contains("PuertoSerie")) {
                    String baud = Preferencias.getString("Baud_" + num, "9600");
                    String dataB = Preferencias.getString("DataB_" + num, "1");
                    String stopB = Preferencias.getString("StopB_" + num, "8");
                    String parity = Preferencias.getString("Parity_" + num, "0");
                    listaux.add(baud);
                    listaux.add(dataB);
                    listaux.add(stopB);
                    listaux.add(parity);
                }else if(Salidastr.contains("Red")||Salidastr.contains("Bluetooth")){
                    String Direccion = Preferencias.getString("Direccion_" + num, "1.1.1.1");
                    listaux.add(Direccion);
                }
                if ((ArrayBalanzas.size() < 1)) {
                    listaux.add("");
                    listaux.add("");
                    listaux.add("");
                    listaux.add("");
                    listaux.add("");

                }
                if(obtenerModelosDeTipo(tipo).contains(Modelobza) && Salidastr.equals(Salidaaux)) {
                    balanza.setModelo(Modelobza);
                    balanza.setSeteo(seteo);
                    balanza.setDireccion(listaux);

                    balanza.setID(NumeroID);
                    balanza.setSalida(Salida);
                    ArrayBalanzas.add(balanza);


                }

            }

            num++;

        }
        return ArrayBalanzas;
    }
    public static boolean existeTipo(String tipo) {
        return deviceMap.containsKey(tipo);
    }
    public  static List<String> obtenerModelosDeTipo(String tipo) {
        if (deviceMap.containsKey(tipo)) {
            return deviceMap.get(tipo);
        }
        return new ArrayList<>();
    }
    public static  boolean existeModelo(String tipo, String modelo) {
        List<String> modelos = deviceMap.get(tipo); // Obtiene la lista de modelos de un tipo
        return modelos != null && modelos.contains(modelo); // Verifica si el modelo está en la lista
    }
    public static String obtenerTipoPorIndice(int indice) {
        List<String> tipos = new ArrayList<>(deviceMap.keySet()); // Obtener las claves (tipos) como lista

        if (indice >= 0 && indice < tipos.size()) {
            return tipos.get(indice); // Devuelve el tipo en el índice especificado
        }
        return null; // Si el índice es inválido
    }
    // --------------------- fin funciones data ----------------------------------------------------------------------
    // ---------------------  funciones Para Balanzas ESTANDAR ----------------------------------------------------------------------
    public static void setSharedPreferenceEstandar(String Modelo, String nombrevariable, String variable, AppCompatActivity activity) {
        SharedPreferences Preferencias= activity.getApplicationContext().getSharedPreferences(Modelo, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(nombrevariable, variable);
        ObjEditor.apply();
    }
    public static String getSharedPreferenceEstandar(String Modelo, String nombrevariable, AppCompatActivity activity) {
        SharedPreferences Preferencias= activity.getApplicationContext().getSharedPreferences(Modelo, Context.MODE_PRIVATE);
        return Preferencias.getString(nombrevariable,"");
    }
    public static void setPesoUnitario(String Modelo, int numBza, float peso, AppCompatActivity activity){
        SharedPreferences preferencias=activity.getApplicationContext().getSharedPreferences(Modelo, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numBza)+"_"+"punitario", peso);
        ObjEditor.apply();
    }
    public static  void setUnidad(String Modelo, int numBza, String Unidad, AppCompatActivity activity){
        SharedPreferences preferencias=activity.getApplicationContext().getSharedPreferences(Modelo, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putString(String.valueOf(numBza)+"_"+"unidad",Unidad);
        ObjEditor.apply();
    }
    public static  void setDivisionMinima(String Modelo, int numBza, int divmin, AppCompatActivity activity){
        SharedPreferences Preferencias=activity.getApplicationContext().getSharedPreferences(Modelo,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numBza)+"_"+"div",divmin);
        ObjEditor.apply();
    }
    public static void setPuntoDecimal(String Modelo, int numBza, int puntoDecimal, AppCompatActivity activity){
        SharedPreferences Preferencias=activity.getApplicationContext().getSharedPreferences(Modelo,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numBza)+"_"+"pdecimal",puntoDecimal);
        ObjEditor.apply();
    }
    public static void setUltimaCalibracion(String Modelo, int numBza, String ucalibracion, AppCompatActivity activity){
        SharedPreferences Preferencias=activity.getApplicationContext().getSharedPreferences(Modelo,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numBza)+"_"+"ucalibracion",ucalibracion);
        ObjEditor.apply();
    }
    public static void setCapacidadMax(String Modelo, int numBza, String capacidad, AppCompatActivity activity){
        SharedPreferences Preferencias=activity.getApplicationContext().getSharedPreferences(Modelo,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numBza)+"_"+"capacidad",capacidad);
        ObjEditor.apply();
    }
    public static void setPesoConocido(String Modelo, int numBza, String pesoConocido, AppCompatActivity activity){
        SharedPreferences Preferencias=activity.getApplicationContext().getSharedPreferences(Modelo,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numBza)+"_"+"pconocido",pesoConocido);
        ObjEditor.apply();
    }
    public static void setBandaCeroValue(String Modelo, int numBza, float bandaCeroValue, AppCompatActivity activity) {

        SharedPreferences preferencias=activity.getApplicationContext().getSharedPreferences(Modelo, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numBza)+"_"+"pbandacero",bandaCeroValue);
        ObjEditor.apply();
    }
    public static int getPuntoDecimal(String Modelo, int numBza, AppCompatActivity activity){
        SharedPreferences Preferencias=activity.getApplicationContext().getSharedPreferences(Modelo,Context.MODE_PRIVATE);
        return Preferencias.getInt(String.valueOf(numBza)+"_"+"pdecimal",1);
    }
    public static String getCapacidadMax(String Modelo, int numBza, AppCompatActivity activity){
        SharedPreferences Preferencias=activity.getApplicationContext().getSharedPreferences(Modelo,Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numBza)+"_"+"capacidad","100");
    }
    public static float getPesoBandaCero(String Modelo, int numero, AppCompatActivity activity) {
        SharedPreferences preferences=activity.getApplicationContext().getSharedPreferences(Modelo, Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numero)+"_"+"pbandacero",5.0F));
    }
    public static float getPesoUnitario(String Modelo, int numero, AppCompatActivity activity) {
        SharedPreferences preferences=activity.getApplicationContext().getSharedPreferences(Modelo, Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numero)+"_"+"punitario",0.5F));
    }
    public static Float getBandaCeroValue(String Modelo, int numBza, AppCompatActivity activity) {
        SharedPreferences preferences=activity.getApplicationContext().getSharedPreferences(Modelo, Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numBza)+"_"+"pbandacero",5.0F));
    }
    public static String getUnidad(String Modelo, int numBza, AppCompatActivity activity) {
        SharedPreferences preferences=activity.getApplicationContext().getSharedPreferences(Modelo, Context.MODE_PRIVATE);
        return (preferences.getString(String.valueOf(numBza)+"_"+"unidad","kg"));
    }
    public static String getPesoConocido(String Modelo, int numBza, AppCompatActivity activity){
        SharedPreferences Preferencias=activity.getApplicationContext().getSharedPreferences(Modelo,Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numBza)+"_"+"pconocido","100");
    }
    public static String getUltimaCalibracion(String Modelo, int numBza, AppCompatActivity activity){
        SharedPreferences Preferencias=activity.getApplicationContext().getSharedPreferences(Modelo,Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numBza)+"_"+"ucalibracion","");
    }
    public static String getFiltro(String Modelo, int numBza,int numeroFiltro, AppCompatActivity activity){
        SharedPreferences Preferencias= activity.getApplicationContext().getSharedPreferences(Modelo,Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numBza)+"_"+"filtro"+String.valueOf(numeroFiltro),"null");
    }

    public static void setFiltro(String Modelo, int numBza, int numeroFiltro, String filtro, AppCompatActivity activity) {

    }
}
