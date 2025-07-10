package com.service;
import static com.service.utilsPackage.Utils.Mensaje;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

import com.service.Comunicacion.GestorRecursos;
import com.service.Devices.Balanzas.Clases.ANDGF3000;
import com.service.Devices.Balanzas.Clases.BalanzaBase;
import com.service.Comunicacion.GestorPuertoSerie;
import com.service.Devices.Balanzas.Clases.ITW230II;
import com.service.Devices.Balanzas.Clases.ITW380;
import com.service.Devices.Balanzas.Clases.ITW410.ITW4102Bzas;
import com.service.Devices.Balanzas.Clases.ITW410FRM.ITW410_FORM;
import com.service.Devices.Balanzas.Clases.Minima.MINIMA_I;
import com.service.Devices.Balanzas.Clases.Optima.OPTIMA_I;
import com.service.Devices.Balanzas.Clases.PrSeries;
import com.service.Devices.Balanzas.Clases.R31P30_I;
import com.service.Devices.Balanzas.Clases.SPIDER3;
import com.service.Devices.Balanzas.Clases.zorra232;
import com.service.Devices.Dispositivos.Clases.MasterDispositivos;
import com.service.Devices.Dispositivos.Clases.SlaveDispositivos;
import com.service.Devices.Impresora.ImprimirEstandar;
import com.service.Interfaz.Dispositivo;
import com.service.utilsPackage.EnumReflexion;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.Devices.Escanneres.Clases.EscannerManager;
import com.service.Devices.Expansiones.Clases.ExpansionManager;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.Devices.Expansiones.Clases.AnalogicoC;
import com.service.Devices.Expansiones.Clases.EntradasC;
import com.service.Devices.Expansiones.Clases.ExpansionBase;
import com.service.Devices.Expansiones.Clases.MixtoC;
import com.service.Devices.Expansiones.Clases.SalidasC;
import com.service.Interfaz.Balanza;
import com.service.Interfaz.Printer;
import com.service.utilsPackage.Reflextion_Interface;
import com.service.Interfaz.dispositivoBase;
import com.service.estructuras.classDevice;
import com.service.utilsPackage.ComService;
import com.service.utilsPackage.PreferencesDevicesManager;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public  class BalanzaService implements Serializable {

   // public  Balanza Services;
    public com.service.Interfaz.ExpansionGestor Expansiones = new Expansiones();
    public Dispositivo.DeviceFachade Dispositivos=null;

    private InternalDispositivos DispositivosInstancia=null;
    public com.service.Interfaz.EscanerGestor Escaneres = new Escaneres();
    public Printer Impresoras  = new Impresoras();
    //leandrito
    protected GestorPuertoSerie Puertos = GestorPuertoSerie.getInstance();

    private static boolean initializeDevicesbool=true,initializeescannerbool=true, initializexpansionesbool =true;
    //holanda

    public enum ModelosClasesExpansiones {
        Entradas(EntradasC.class), Salidas(SalidasC.class),Combinadas(MixtoC.class),Analogicos(AnalogicoC.class);
        public Class<? extends ExpansionBase> clase;
        ModelosClasesExpansiones( Class<? extends ExpansionBase> clase) {
            this.clase = clase ;//.getDeclaredConstructor().newInstance();
        }

        public Class<? extends ExpansionBase> getClase() {
            return clase;
        }
        public ArrayList<String> getConfiguraciones() {
            try {
                ArrayList<String> list= new ArrayList<>();
                list.add(Reflextion_Interface.getFieldValueStr(clase , String.valueOf(EnumReflexion.Configuracion_Puerto.BAUD.nombre)));
                list.add(Reflextion_Interface.getFieldValueStr(clase ,String.valueOf(EnumReflexion.Configuracion_Puerto.STOP.nombre)));
                list.add(Reflextion_Interface.getFieldValueStr(clase ,String.valueOf(EnumReflexion.Configuracion_Puerto.DATA.nombre)));
                list.add(Reflextion_Interface.getFieldValueStr(clase ,String.valueOf(EnumReflexion.Configuracion_Puerto.PARITY.nombre)));
                return list;
            } catch (Exception e) {
                System.err.println("Error obteniendo configuraciones: " + e.getMessage());
                return new ArrayList<>(); // Devuelve una lista vacía en caso de error
            }
        }

    }

    public enum ModelosClasesDispositivos {
        ASCII(com.service.Devices.Dispositivos.Clases.Dispositivo.class), Master(MasterDispositivos.class),Slave(SlaveDispositivos.class);
        public Class<? extends com.service.Interfaz.Dispositivo> clase;
        ModelosClasesDispositivos(Class<? extends com.service.Interfaz.Dispositivo> clase) {
            this.clase = clase ;//.getDeclaredConstructor().newInstance();
        }

        public boolean compararInstancia(int ndevice) {
            try {
                dispositivoBase Device = BalanzaService.getInstance().Dispositivos.getDispositivo(ndevice);
                if(Device==null)return false;
                return clase.isInstance(Device);
            } catch (Exception e) {
                return false;
            }
        }
        public Class<? extends com.service.Interfaz.Dispositivo> getClase() {
            return clase;
        }
       public Boolean getTiene485(){
           return Reflextion_Interface.getFieldValueBool(clase , String.valueOf(EnumReflexion.Dispositivos.puede485.nombre));
       }
        public ArrayList<String> getConfiguraciones() {
            try {
                ArrayList<String> list= new ArrayList<>();
                list.add(Reflextion_Interface.getFieldValueStr(clase , String.valueOf(EnumReflexion.Configuracion_Puerto.BAUD.nombre)));
                list.add(Reflextion_Interface.getFieldValueStr(clase ,String.valueOf(EnumReflexion.Configuracion_Puerto.STOP.nombre)));
                list.add(Reflextion_Interface.getFieldValueStr(clase ,String.valueOf(EnumReflexion.Configuracion_Puerto.DATA.nombre)));
                list.add(Reflextion_Interface.getFieldValueStr(clase ,String.valueOf(EnumReflexion.Configuracion_Puerto.PARITY.nombre)));
                return list;
            } catch (Exception e) {
                System.err.println("Error obteniendo configuraciones: " + e.getMessage());
                return new ArrayList<>(); // Devuelve una lista vacía en caso de error
            }
        }

    }

    public enum ModelosClasesBzas {
            Optima(OPTIMA_I.class), Minima(MINIMA_I.class), R31p30(R31P30_I.class), ITW_410(ITW4102Bzas.class), Spider_3(SPIDER3.class), Andgf_3000(ANDGF3000.class), Zorra_232(zorra232.class),ITW_230_II(ITW230II.class),ITW_410_FRM(ITW410_FORM.class),OHAUS_PR_Serial(PrSeries.class),ITW_380_Caudal(ITW380.class);//, NuevaBza(NuevaBalanza.class);
            public Class<? extends BalanzaBase> clase;
            ModelosClasesBzas( Class<? extends BalanzaBase> clase) {
                this.clase = clase ;//.getDeclaredConstructor().newInstance();
            }
        public Boolean getTienePorDemanda(){
            System.out.println("Clase cargada: " + clase);
            return Reflextion_Interface.getFieldValueBool(clase , String.valueOf(EnumReflexion.Balanzas.puede485.nombre));
        }
        public boolean compararInstancia(int nbza) {
            try {
                Balanza balanza = BalanzaService.getInstance().Balanzas.getBalanza(nbza);
                return clase.isInstance((BalanzaBase)balanza);
            } catch (Exception e) {
                return false;
            }
        }
           public Class<? extends BalanzaBase> getClase() {
               return clase;
           }
           public int GetnumMultiBzas() {
               try {
                   return Reflextion_Interface.getFieldValueInt(clase , String.valueOf(EnumReflexion.Balanzas.nBalanzas.nombre));
               } catch (Exception e) {
                   System.err.println("Error obteniendo numeromultiplebalanza: " + e.getMessage());
                   return 1; // Devuelve una lista vacía en caso de error
               }
            }
            //aaaaaaaaaaaaaaaaaaaaaaOOOOOOOOO
        public boolean getTieneCal() {
            try {
                return  Reflextion_Interface.getFieldValueBool(clase , String.valueOf(EnumReflexion.Balanzas.TieneCal.nombre));
            } catch (Exception e) {
                System.err.println("Error obteniendo TieneCal: " + e.getMessage());
                return false;
            }
        }
           public ArrayList<String> getConfiguraciones() {
               try {
                   ArrayList<String> list= new ArrayList<>();
                           list.add(Reflextion_Interface.getFieldValueStr(clase , String.valueOf(EnumReflexion.Configuracion_Puerto.BAUD.nombre)));
                           list.add(Reflextion_Interface.getFieldValueStr(clase ,String.valueOf(EnumReflexion.Configuracion_Puerto.STOP.nombre)));
                           list.add(Reflextion_Interface.getFieldValueStr(clase ,String.valueOf(EnumReflexion.Configuracion_Puerto.DATA.nombre)));
                           list.add(Reflextion_Interface.getFieldValueStr(clase ,String.valueOf(EnumReflexion.Configuracion_Puerto.PARITY.nombre)));
                   return list;
               } catch (Exception e) {
                   System.err.println("Error obteniendo configuraciones: " + e.getMessage());
                   return new ArrayList<>(); // Devuelve una lista vacía en caso de error
               }
                }
    }

    private static ComService ComService ;
    private static BalanzaService Service=null;
    /**
     * Inicializa el servicio `BalanzaService` si aún no está inicializado.
     * Si el servicio ya está inicializado, retorna la instancia existente.
     *
     * @param activity La actividad que solicita la inicialización del servicio, que se usará en el contexto del servicio.
     * @param fragmentChangeListener El listener para manejar cambios de fragmento en la actividad.
     * @return La instancia de `BalanzaService`.
     */
    public static BalanzaService init(AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener){
        if(Service==null){
            if(isDeviceRooted()){
                runAsRoot("su -c 'iptables -t nat -A PREROUTING -p tcp --dport 502 -j REDIRECT --to-port 1502'");
            }else{
                Mensaje("Habilitar Acceso ROOT",R.layout.item_customtoasterror,activity);
            }
            Service= new BalanzaService();
            ComService = com.service.utilsPackage.ComService.init(activity,fragmentChangeListener);

            Service.init(false);
        }
        return Service;
    }
    private static boolean isDeviceRooted() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            process.getOutputStream().close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean runAsRoot(String command) {
        try {
            Process process = Runtime.getRuntime().exec("su");
            process.getOutputStream().write((command + "\n").getBytes());
            process.getOutputStream().write("exit\n".getBytes());
            process.getOutputStream().flush();
            process.getOutputStream().close();
            process.waitFor();
            return process.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene la instancia actual de `BalanzaService`.
     *
     * @return La instancia de `BalanzaService`.
     */
    public static BalanzaService getInstance(){
        return Service;
    }

    // inicio init funciones ---
    private void SettingsDef(){
        String tipo="";
        int num=0;int numbza=0;
        SharedPreferences Preferencias=ComService.activity.getApplicationContext().getSharedPreferences("devicesService",Context.MODE_PRIVATE);
        while(num<PreferencesDevicesManager.salidaMap.size()){
            classDevice x=new classDevice();
            tipo= Preferencias.getString("Tipo_"+num,"fin");
            if(tipo.equals("fin")&&num==0){
                tipo="Balanza";
            }
            Boolean seteo=false;
            seteo= Preferencias.getBoolean("seteo_"+num,false);
            switch (num){
                case 0:{
                    if(!seteo && tipo.equals("Balanza")){ // num siempre sera 0
                        ArrayList<String> arrayList=new ArrayList<>();
                        arrayList = ModelosClasesBzas.values()[num].getConfiguraciones();
                        x.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(num));
                        x.setND(0);
                        x.setNDL(1);
                        x.setDireccion(arrayList);
                        x.setSeteo(true);
                        x.setSalida("PuertoSerie 1");
                        x.setID(0);
                        PreferencesDevicesManager.addDevice(x,ComService.activity);
                    }
                    break;
                }
            }
            num++;
        }
        ;
    }
    protected void init(Boolean reset) {
    CountDownLatch latch = new CountDownLatch(1);
        if(reset != null && reset){
            Puertos.Reset();

            int i=1;
            for (BalanzaBase Balanza : balanzasInstancia.balanzas.values()) {
                Balanza.stop(i);
                i++;
            }
            balanzasInstancia.balanzas.clear();
            balanzasInstancia.balanzas= new HashMap<>();
            GestorRecursos.getinstance().reiniciar();
            DispositivosInstancia.stop();
            DispositivosInstancia=null;
            Dispositivos=null;
        }
       //Utils.clearCache(ComService.activity.getApplicationContext());
        SettingsDef();
        ArrayList<classDevice> Devicelist = PreferencesDevicesManager.get_listPorTipo(PreferencesDevicesManager.obtenerIndiceTipo("Dispositivo"),ComService.activity);
           System.out.println( "DEBUGGEO "+!Devicelist.isEmpty()+" "+ String.valueOf(Devicelist.get(0).getSeteo())+" "+ String.valueOf(DispositivosInstancia==null));
            if(!Devicelist.isEmpty() && Devicelist.get(0).getSeteo() && DispositivosInstancia==null){
                try {
                } catch (Exception e) {
                } finally {
                    DispositivosInstancia = new InternalDispositivos();
                    DispositivosInstancia.InitializateDevices(Devicelist);
                    Dispositivos = DispositivosInstancia;
                }
            }
        balanzasInstancia = new Balanzas(); // Tipo concreto
        Balanzas = balanzasInstancia;
            ArrayList<classDevice> balanzasList = PreferencesDevicesManager.get_listPorTipo(PreferencesDevicesManager.obtenerIndiceTipo("Balanza"),ComService.activity);
            if(!balanzasList.isEmpty() && balanzasList.get(0).getSeteo()){
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                balanzasInstancia.initializateBalanza(balanzasList);
                Balanzas = balanzasInstancia;
                latch.countDown();
                    }
                }).start();

            }else{
                Mensaje("El servicio tuvo error fatal",R.layout.item_customtoasterror,ComService.activity);
            }
        try {
            latch.await();
        } catch (InterruptedException e) {

        }
        //usbManagerEscanner USBMANAGERSCANNER  = new usbManagerEscanner(ComService.activity);
        //TecladoUsb usbTeclado = new TecladoUsb(ComService.activity.getApplicationContext());
        //usbTeclado.readAsyncFromDevice();
    }
    private class Impresoras implements Printer {
        /**
         * Envía una etiqueta para imprimir a la impresora configurada en el índice proporcionado.
         *
         * @param numImpresora El número de la impresora que se desea utilizar (1, 2, 3, etc.).
         * @param etiqueta El contenido de la etiqueta que se desea imprimir.
         */
        public  void ImprimirEstandar(Integer numImpresora, String etiqueta) {

            Integer numprint=numImpresora-1;
            ArrayList<classDevice> Impresoralista = PreferencesDevicesManager.get_listPorTipo(PreferencesDevicesManager.obtenerIndiceTipo("Impresora"),ComService.activity);
            try{
            if (Impresoralista != null & Impresoralista.get(numprint)!=null) {
                int type=0;
                PuertosSerie puerto=null;
                System.out.println("SALIDA IMPRESORA: "+Impresoralista.get(numprint).getSalida()+ " etiquet"+ etiqueta);
                switch (Impresoralista.get(numprint).getSalida()) {
                    case "PuertoSerie 1": {
                        puerto= Puertos.initPuertoSerie(PuertosSerie.StrPortA, 9600, 8, 1, 0, 0, 0);
                        type=1; break;
                    }
                    case "PuertoSerie 2": {
                        puerto=  Puertos.initPuertoSerie(PuertosSerie.StrPortB, 9600, 8, 1, 0, 0, 0);
                        type=2;
                        break;
                    }
                    case "PuertoSerie 3": {
                        puerto= Puertos.initPuertoSerie(PuertosSerie.StrPortC, 9600, 8, 1, 0, 0, 0);
                        type=3;
                        break;
                    }
                    case "USB": {
                        type=4;
                        break;
                    }
                    case "Red": {
                        type=5;
                        break;
                    }
                    case "Bluetooth": {
                        type=6;
                        break;
                    }
                }
                 ImprimirEstandar Impresora = new ImprimirEstandar(ComService.activity.getApplicationContext(), ComService.activity, etiqueta, numprint, puerto, type, Impresoralista.get(numprint));
                Impresora.EnviarEtiqueta();

            } else {
                ComService.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    //    Mensaje("No existe esta Impresora en configuracion Service", R.layout.item_customtoasterror, ComService.activity);
                    }
                });

            }
            }catch(Exception e){
                ComService.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                     //   Mensaje("No existe esta Impresora en configuracion Service", R.layout.item_customtoasterror, ComService.activity);
                    }
                });
            }
        }

    }

   private class Expansiones implements com.service.Interfaz.ExpansionGestor {

       @Override
       public Boolean CambiarSalida(int numero, Boolean estado) {
           return  ExpansionManager.getInstance().CambiarSalida(numero,estado);
       }

       @Override
       public Boolean LeerEntrada(int numero) {
           return ExpansionManager.getInstance().LeerEntrada(numero);
       }

       @Override
        public  void init(ExpansionManager.ExpansionesMessageListener Listener) {
            //System.out.println("Expansiones init");
            if(initializexpansionesbool) {
                initializexpansionesbool = false;
                ExpansionManager.getInstance().setListener(Listener);
                ArrayList<classDevice> ExpansionesList = PreferencesDevicesManager.get_listPorTipo(PreferencesDevicesManager.obtenerIndiceTipo("Expansion"),ComService.activity);
                int i = 0;
                PuertosSerie port=null;
                for (classDevice Expansion : ExpansionesList
                ) {
                    //System.out.println("Expansion SETEANDO");
                    String strpuerto="";
                    try {
                        switch (Expansion.getSalida()) {
                            case "PuertoSerie 1": {
                                strpuerto= PuertosSerie.StrPortA;
                                break;
                            }
                            case "PuertoSerie 2": {
                                strpuerto= PuertosSerie.StrPortB;
                                break;
                            }
                            case "PuertoSerie 3": {
                                strpuerto= PuertosSerie.StrPortC;
                                break;
                            }
                        }
                        port =  Puertos.initPuertoSerie(strpuerto, Integer.parseInt(Expansion.getDireccion().get(0)), Integer.parseInt(Expansion.getDireccion().get(1)), Integer.parseInt(Expansion.getDireccion().get(2)), Integer.parseInt(Expansion.getDireccion().get(3)),0,0);
                    } catch (NumberFormatException e) {
                    }finally {
                        for (int c = 0; c < ModelosClasesExpansiones.values().length; c++) {
                            //   System.out.println(Objects.equals(Expansion.getModelo(), ModelosClasesExpansiones.values()[c].name()));
                            if (Objects.equals(Expansion.getModelo(), ModelosClasesExpansiones.values()[c].name())) {
                                ExpansionBase Exp = null;
                                try {
                                    Exp = ModelosClasesExpansiones.values()[c].getClase().getDeclaredConstructor(PuertosSerie.class, String.class, AppCompatActivity.class).newInstance(port, String.valueOf(Expansion.getID()), ComService.activity);
                                } catch (Exception e) {
                                }finally{
                                    ExpansionManager.getInstance().addExpansion(i, Exp);

                                }
                            }
                        }
                        ExpansionManager.getInstance().init();
                    }

                }
            }else{
                ExpansionManager.getInstance().setListener(Listener);
            }
        }

    }

    private class InternalDispositivos implements Dispositivo.DeviceFachade, dispositivoBase {
        private Map<Integer, dispositivoBase> dispositivosMap = new HashMap<>();
        @Override
        public dispositivoBase getDispositivo(int nDispositivo) {
            System.out.println("get nDevice"+nDispositivo);

            return dispositivosMap.containsKey(nDispositivo) ? dispositivosMap.get(nDispositivo) : new dispositivoBase() {
                @Override
                public void stop() {

                }
            };
        }
        public  void InitializateDevices(ArrayList<classDevice> Devicelist) {
             CountDownLatch latch = new CountDownLatch(Devicelist.size());
            System.out.println("size nDevice"+Devicelist.size());

            for (classDevice Device : Devicelist) {
                    String strpuerto="";
                    switch (Device.getSalida()) {
                        case "PuertoSerie 1": {
                            strpuerto= PuertosSerie.StrPortA;
                            break;
                        }
                        case "PuertoSerie 2": {
                            strpuerto= PuertosSerie.StrPortB;
                            break;
                        }
                        case "PuertoSerie 3": {
                            strpuerto= PuertosSerie.StrPortC;
                            break;
                        }
                    }
                    System.out.println("MODELO"+ Device.getModelo()+ " "+(dispositivosMap.size()+1));
                    for (int x = 0; x < ModelosClasesDispositivos.values().length; x++) {
                        if(Objects.equals(Device.getModelo(),ModelosClasesDispositivos.values()[x].name())){
                            try {
                                int j=1;
                                Dispositivo Dispositivo = ModelosClasesDispositivos.values()[x].getClase().getDeclaredConstructor(String.class, classDevice.class, int.class).newInstance(strpuerto, Device,dispositivosMap.size()+1);
                                dispositivosMap.put(dispositivosMap.size()+1, (dispositivoBase)Dispositivo);
                                System.out.println("put nDevice"+dispositivosMap.size()+1);
                                latch.countDown();
                            } catch (IllegalAccessException | InvocationTargetException |
                                     InstantiationException | NoSuchMethodException e) {
                                System.out.println("OLA1?"+e.getMessage());
                            } finally {
                            }
                        }
                    }
                   /* switch (Device.getModelo()){
                        case "Master": {
                            MasterDispositivos x = new MasterDispositivos(strpuerto,Device,dispositivosMap.size()-1);
                            dispositivosMap.put(dispositivosMap.size()-1,x);
                            break;
                        }
                        case "Slave": {
                            SlaveDispositivos x = new SlaveDispositivos(strpuerto, Device,dispositivosMap.size()-1);
                            dispositivosMap.put(dispositivosMap.size()-1,x);
                            break;
                        }
                        case "Dispositivo":{
                            com.service.Devices.Dispositivos.Clases.Dispositivo x = new com.service.Devices.Dispositivos.Clases.Dispositivo(strpuerto,Device,dispositivosMap.size()-1);
                            dispositivosMap.put(dispositivosMap.size()-1,x);
                            break;
                        }
                    }*/
                }
            try {
                latch.await(Devicelist.size() * 2000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {

            }

        }


        @Override
        public void stop() {
            for (Map.Entry<Integer, dispositivoBase> entry : dispositivosMap.entrySet()) {
                try {
                    dispositivoBase valor = entry.getValue();
                    valor.stop();
                } catch (Exception e) {

                }
            }
        }
    }
    private class Escaneres implements com.service.Interfaz.EscanerGestor{

        @Override
        public  void init(EscannerManager.ScannerMessageListener Listener) {
            // System.out.println("escanner init");
            if(initializeescannerbool) {
                initializeescannerbool = false;
                EscannerManager.getInstance().setListener(Listener);
                ArrayList<classDevice> Escannerlist = PreferencesDevicesManager.get_listPorTipo(PreferencesDevicesManager.obtenerIndiceTipo("Escaner"),ComService.activity);
                int i = 0;
                for (classDevice Escaner : Escannerlist
                ) {
                    PuertosSerie port = null;
                    String strpuerto="";
//                System.out.println("Device SETEANDO");
                    switch (Escaner.getSalida()) {
                        case "PuertoSerie 1": {
                            strpuerto= PuertosSerie.StrPortA;
                            break;
                        }
                        case "PuertoSerie 2": {
                            strpuerto= PuertosSerie.StrPortB;
                            break;
                        }
                        case "PuertoSerie 3": {
                            strpuerto= PuertosSerie.StrPortC;
                            break;
                        }
                        default :{
                            strpuerto= null;
                        }
                    }
                    try {
                       port= Puertos.initPuertoSerie(strpuerto, Integer.parseInt(Escaner.getDireccion().get(0)), Integer.parseInt(Escaner.getDireccion().get(1)), Integer.parseInt(Escaner.getDireccion().get(2)), Integer.parseInt(Escaner.getDireccion().get(3)),0,0);
                    } catch (NumberFormatException e) {

                    }
                    if(strpuerto!=null) {
                        EscannerManager.getInstance().addScannerPort(i, port);
                        //System.out.println("escanner add in "+Escaner.getSalida());

                    }else{
                        EscannerManager.getInstance().addScannerUSB(i);
                    }
                    i++;

                }
            }else{
                EscannerManager.getInstance().setListener(Listener);
            }
        }
    }
    private Balanzas balanzasInstancia;
    public Balanza Balanzas;
    protected class Balanzas implements Balanza{
        private Map<Integer, BalanzaBase> balanzas = new HashMap<>();
        /**
         * Obtiene una balanza específica desde la lista de balanzas.
         *
         * @param numBza El índice de la balanza en la colección.
         * @return La balanza correspondiente al índice especificado.
         */
        @Override
        public Balanza getBalanza(int numBza){
            Balanza balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getBalanza(numBza);
            }else{
                return new BalanzaBase("",0,null,null,0);
            }
        };
        /**
         * Inicializa las balanzas con la lista proporcionada y configura sus puertos.
         *
         * @param balanzasList Una lista de dispositivos de clase `classDevice` que contienen la información de las balanzas a inicializar.
         */
        private void initializateBalanza(ArrayList<classDevice> balanzasList) {
            CountDownLatch latch = new CountDownLatch(balanzasList.size());
            for (classDevice balanza: balanzasList) {//for (int posicionBza = 0; posicionBza < balanzasList.size(); posicionBza++) {
                String puerto = "";
                switch (balanza.getSalida()) {
                    case "PuertoSerie 1": {
                        puerto = PuertosSerie.StrPortA;
                        break;
                    }
                    case "PuertoSerie 2": {

                        puerto = PuertosSerie.StrPortB;
                        break;
                    }
                    case "PuertoSerie 3": {
                        puerto = PuertosSerie.StrPortC;
                        break;
                    }
                    case "Red": {
                        puerto="REDIP ( o por ahi es ssid?)";
                    }
                    case "Bluetooth": {

                        puerto="Mac";
                    }
                    case "USB": {
                        puerto="";
                    }
                }
                for (int i = 0; i < ModelosClasesBzas.values().length; i++) {
                         System.out.println("DEBUG BALANZA MODELO "+balanza.getModelo()+"   "+ModelosClasesBzas.values()[i].name()+ "EQUAL "+Objects.equals(balanza.getModelo(),ModelosClasesBzas.values()[i].name()));
                    if(Objects.equals(balanza.getModelo(),ModelosClasesBzas.values()[i].name())){
                        try {
                            //int j=1;
                            int MultipleBZA = ModelosClasesBzas.values()[i].GetnumMultiBzas();
                            System.out.println("DEBUG CHANGE BZA " +MultipleBZA);
                            for (int j = 1; j < MultipleBZA+1; j++) {
                                System.out.println("DEBUG CHANGE BZA size " + balanzas.size());
                            BalanzaBase bza = ModelosClasesBzas.values()[i].getClase().getDeclaredConstructor(String.class, int.class, AppCompatActivity.class, OnFragmentChangeListener.class, int.class).newInstance(puerto, balanza.getID(), ComService.activity, ComService.fragmentChangeListener,j);
                            System.out.println("SIZEZING BZAS " + balanzas.size()+1);
                            bza.init(balanzas.size() + 1);
                            balanzas.put(balanzas.size()+1, bza);
                            }
                            latch.countDown();

                            // habria que controlar si tiene 2 bzas, si tiene modbus etc.
                        } catch (IllegalAccessException | InvocationTargetException |InstantiationException | NoSuchMethodException e) { //
                            System.out.println("OLA2?"+e.getMessage());
                            e.printStackTrace();
                        } finally {

                        }
                    }
                }
            }
            try{
              Boolean x =   latch.await(balanzasList.size() * 2000, TimeUnit.MILLISECONDS);
              if(!x){
                  System.out.println("HAY QUE FIJARSE ESTA WEA YA D YA");
              }
            } catch (InterruptedException e) {
                System.out.println("OLA3?"+e.getMessage());
            }
            GestorRecursos.getinstance().init();
        }
        public String getEstado(int numBza) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getEstado(numBza);
            }
            return null;
        }
        public void setEstado(int numBza, String estadoBZA) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.setEstado(numBza,estadoBZA);
            }
        }
        @Override
        public Float getNeto(int numBza) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getNeto(numBza);
            }
            return   null;
        }
        @Override
        public String getNetoStr(int numBza) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {

                return balanza.getNetoStr(numBza);
            }else{
            }
            return null;
        }

        @Override
        public Float getBruto(int numBza) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getBruto(numBza);
            }
            return null;
        }

        @Override
        public String getBrutoStr(int numBza) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getBrutoStr(numBza);
            }
            return null;
        }

        @Override
        public Float getTara(int numBza) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getTara(numBza);
            }
            return null;
        }

        @Override
        public String getTaraStr(int numBza) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getTaraStr(numBza);
            }
            return null;
        }

        @Override
        public void setTara(int numBza) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.setTara(numBza);
            }
        }

        @Override
        public void setCero(int numBza) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.setCero(numBza);
            }
        }

        @Override
        public void setTaraDigital(int numBza, float TaraDigital) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.setTaraDigital(numBza, TaraDigital);
            }
        }

        @Override
        public String getTaraDigital(int numBza) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getTaraDigital(numBza);
            }
            return null;
        }

        @Override
        public Boolean getEstable(int numBza) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getEstable(numBza);
            }
            return null;
        }

        @Override
        public String format(int numero, String peso) {
            BalanzaBase balanza = balanzas.get(numero);
            if (balanza != null) {
                return balanza.format(numero,peso);
            }
            return null;
        }

        @Override
        public String getUnidad(int numBza) {
            try {
                BalanzaBase balanza = balanzas.get(numBza);
                if (balanza != null) {
                    return balanza.getUnidad(numBza);
                }

            } catch (IllegalArgumentException e) {
                //mainComService.activity.Mensaje("Error:"+e.getMessage(), R.layout.item_customtoasterror);
            }
            return null;

        }

        public void openCalibracion(int numBza) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                balanza.openCalibracion(numBza);
            }else{
                Mensaje("Balanza "+numBza+" debe inicializarse",R.layout.item_customtoasterror,ComService.activity);
            }
        }
        @Override
        public Boolean getSobrecarga(int numBza) {
            BalanzaBase balanza = balanzas.get(numBza);
            if (balanza != null) {
                return balanza.getSobrecarga(numBza);
            }
            return null;
        }
    }


    //------------------------------------ Fin  inicializacion ---------------------------------------------------











    /**
     * Abre el fragmento del servicio y pasa los argumentos necesarios.
     */

}
