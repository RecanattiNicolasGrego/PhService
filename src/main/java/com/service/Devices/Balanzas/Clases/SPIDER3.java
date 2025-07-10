package com.service.Devices.Balanzas.Clases;

import androidx.appcompat.app.AppCompatActivity;

import com.service.Comunicacion.GestorPuertoSerie;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.utilsPackage.PreferencesDevicesManager;
import com.service.utilsPackage.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.concurrent.CountDownLatch;

public class SPIDER3 extends BalanzaBase{
    /***
     *  DEBO ACTUALIZAR ESTE ARCHIVO DESDE 3/2/25 CON :
     *
     */
    /*
public class SPIDER3 {
    private final Context context;
    private final PuertosSerie serialPort;
    private MainActivity mainActivity;
    Handler mHandler= new Handler();
    final String nombre="SPIDER3";
    public String estado="VERIFICANDO_MODO";
    private static final String CONSULTA_PUNITARIO="XU2\r";//??????
    private static final String CONSULTA_PIEZAS="PU\r\n";
    private static final String CONSULTA_BRUTO="SI\r\n";
    private static final String CONSULTA_NETO="XN2\r";
    public static final String M_VERIFICANDO_MODO="VERIFICANDO_MODO";
    public static final String M_MODO_BALANZA="MODO_BALANZA";
    public static final String M_MODO_CALIBRACION="MODO_CALIBRACION";
    public static final String M_ERROR_COMUNICACION="M_ERROR_COMUNICACION";
    public float taraDigital=0,Bruto=0,Tara=0,Neto=0,pico=0;
    public String estable="";
    float pesoUnitario=0.5F;
    float pesoBandaCero=0F;
    public int piezas=0;
    public Boolean contador =false;
    int runnableIndice=0;
    public Boolean bandaCero =true,stopcomunicacion=false;
    public Boolean inicioBandaPeso=false;
    public int puntoDecimal=1;
    public String ultimaCalibracion="",calculoPesoUnitario="Error";
    public String brutoStr="0",netoStr="0",taraStr="0",taraDigitalStr="0",picoStr="0";
    public int acumulador=0,numero=1;
    public String unidad="gr";

    public SPIDER3(Context context, PuertosSerie serialPort, MainActivity activity, int numero) {
        this.context = context;
        this.serialPort = serialPort;
        this.mainActivity = activity;
        this.numero=numero;
    }

    public void init(){
        estado=M_MODO_BALANZA;
        pesoUnitario=getPesoUnitario();
        pesoBandaCero=getPesoBandaCero();
        puntoDecimal=get_PuntoDecimal();
        ultimaCalibracion=get_UltimaCalibracion();
        if(serialPort!=null){
            GET_PESO_cal_bza.run();


        }
    }

    public void setTara(float tara){
        Tara=tara;
        taraStr=String.valueOf(tara);
    }

    public void setTaraDigital(float tara){
        taraDigital=tara;
        taraDigitalStr=String.valueOf(tara);

    }
    public void sendPuntoDecimal(){
        String punto=String.valueOf(get_PuntoDecimal());
        System.out.println("ENVIANDO PUNTO DECIMAL: "+punto);
        serialPort.write("Pdu"+punto+"\r");

    }


    public void setTaraDigitalSerialPort(float tara){
        String pesostr =floatToStringFormat(tara,puntoDecimal);
        pesostr=completarFormato(pesostr,6);
        pesostr=pesostr.replace(".","");
        serialPort.write("YTD"+pesostr+"\r");

    }

    public void setPesoBandaCero(float peso){
        pesoBandaCero=peso;
        SharedPreferences preferencias=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat("pbandacero", peso);
        ObjEditor.apply();
    }

    public float getPesoBandaCero() {
        SharedPreferences preferences=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        return (preferences.getFloat("pbandacero",5.0F));
    }

    public String Cero(){
        if(serialPort!=null){
            serialPort.write("Z\r\n");
        }

        return "Z\r\n";
    }
    public String Tara(){
        if(serialPort!=null){
            serialPort.write("T\r\n");
        }
        return "T\r\n";
    }
    public void ConsultaPesoUnitario(){
        serialPort.write(CONSULTA_PUNITARIO);
    }
    public String Guardar_cal(){
        return "\u0005S\r";
    }
    public String Consultar_configuracion_memoria(){
        return "\u0005O\r";
    }
    public String Peso_conocido(String pesoconocido,String PuntoDecimal){
        if(pesoconocido.length()+Integer.parseInt(PuntoDecimal)>5){
            return null;
        }
        StringBuilder capacidadBuilder = new StringBuilder(pesoconocido);
        for(int i=0;i<Integer.parseInt(PuntoDecimal);i++){
            capacidadBuilder.append("0");
        }
        pesoconocido = capacidadBuilder.toString();
        if(pesoconocido.length()<5){
            StringBuilder capacidadBuilder1 = new StringBuilder(pesoconocido);
            while(capacidadBuilder1.length()!=5){
                capacidadBuilder1.insert(0, "0");
            }
            pesoconocido = capacidadBuilder1.toString();
        }
        return "\u0005L"+pesoconocido+"\r";
    }
    public String Cero_cal(){
        return "\u0005U\r";
    }
    public String Recero_cal(){
        return "\u0005Z\r";
    }
    public String CapacidadMax_DivMin_PDecimal(String capacidad, String DivMin, String PuntoDecimal){
        if(capacidad.length()+Integer.parseInt(PuntoDecimal)>5){
            return null;
        }
        StringBuilder capacidadBuilder = new StringBuilder(capacidad);
        for(int i=0;i<Integer.parseInt(PuntoDecimal);i++){
            capacidadBuilder.append("0");
        }
        capacidad = capacidadBuilder.toString();
        if(capacidad.length()<5){
            StringBuilder capacidadBuilder1 = new StringBuilder(capacidad);
            while(capacidadBuilder1.length()!=5){
                capacidadBuilder1.insert(0, "0");
            }
            capacidad = capacidadBuilder1.toString();
        }
        return "\u0005D"+capacidad+"0"+DivMin+""+PuntoDecimal+"\r";
    }
    public String Salir_cal(){
        return "\u0005E \r";
    }
    public void setStopcomunicacion() throws IOException {
        stopcomunicacion=true;
        if(serialPort.HabilitadoLectura()){
            serialPort.read_2();
        }
    }
    public String Errores(String lectura){
        if(lectura!=null){
            if(lectura.charAt(0)==6&&lectura.charAt(2)!=32){
                String Error="";
                switch (lectura.charAt(1)) {
                    case 'C':
                        Error="C_CAL_";
                        break;
                    case 'S':
                        Error="S_SAVE_";
                        break;
                    case 'P':
                        Error="P_PARAM_";
                        break;
                    case 'D':
                        Error="D_CAPMAX_";
                        break;
                    case 'U':
                        Error="U_CERO_";
                        break;
                    case 'L':
                        Error="L_CARGA";
                        break;
                    case 'Z':
                        Error="Z_FIN";
                        break;
                    case 'M':
                        Error="M_RECERO";
                        break;
                    case 'R':
                        Error="R_RELOJ";
                        break;
                    case 'A':
                        Error="A_DAC";
                        break;
                    case 'I':
                        Error="I_I.D_";
                        break;
                    case 'O':
                        Error="O_OPTIONS";
                        break;
                    default:
                        return null;
                }

                switch (lectura.charAt(2)) {
                    case 'a':
                        Error=Error+"ERR AJUSTE";
                        break;
                    case 'b':
                        Error=Error+"BAD LEN COMMNAD";
                        break;
                    case 'c':
                        Error=Error+"ERR CERO";
                        break;
                    case 'd':
                        Error=Error+"ERR PARTES";
                        break;
                    case 'e':
                        Error=Error+"ERR ESCRITURA EEPROM";
                        break;
                    case 'f':
                        Error=Error+"BAD ASCII_CHARACTER";
                        break;
                    case 'g':
                        Error=Error+"NOT CAP.MAX.";
                        break;
                    case 'h':
                        Error=Error+"NOT CAP.MAX./INICIAL";
                        break;
                    case 'i':
                        Error=Error+"NOT CAP.MAX./INICIAL/PES.PAT./SPAN_FINAL";
                        break;
                    case 'j':
                        Error=Error+"NOT END CALIB";
                        break;
                    case 'k':
                        Error=Error+"NOT DEVICE HABILITADO";
                        break;
                    case 'l':
                        Error=Error+"ERR LECTURA EEPROM";
                        break;
                    case 'p':
                        Error=Error+"ERR PESO PATRON";
                        break;
                    default:
                        return null;
                }
                return Error;
            }
        }
        return null;
    }
    public String Error_a(){
        return "ERR AJUSTE";
    }
    public String Error_b(){
        return "BAD LEN COMMNAD";
    }
    public String Error_c(){
        return "ERR CERO";
    }
    public String Error_d(){
        return "ERR PARTES";
    }
    public String Error_e(){
        return "ERR ESCRITURA EEPROM";
    }
    public String Error_f(){
        return "BAD ASCII_CHARACTER";
    }
    public String Error_g(){
        return "NOT CAP.MAX.";
    }
    public String Error_h(){
        return "NOT CAP.MAX./INICIAL";
    }
    public String Error_i(){
        return "NOT CAP.MAX./INICIAL/PES.PAT./SPAN_FINAL";
    }
    public String Error_j(){
        return "NOT END CALIB";
    }
    public String Error_k(){
        return "NOT DEVICE HABILITADO";
    }
    public String Error_l(){
        return "ERR LECTURA EEPROM";
    }
    public String Error_P(){
        return "ERR PESO PATRON";
    }



    public void setCantPiezas(int piezas){
        String muestras=completarFormato2(String.valueOf(piezas));
        serialPort.write("YMU"+muestras+"\r");
    }


    public void setPesoUnitario(float peso){
        System.out.println("EL PESO UNITARIO"+peso);
        float pesox1000=peso*1000;
        String pesostr="";
        if(puntoDecimal==0||puntoDecimal==1){
            pesostr =floatToStringFormat(pesox1000,puntoDecimal);
        }else{
            pesostr =floatToStringFormat(pesox1000,puntoDecimal-1);
        }

        pesostr=completarFormato(pesostr,7);
        serialPort.write("YPU"+pesostr+"\r");
        pesoUnitario=peso;
        SharedPreferences preferencias=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat("punitario", peso);
        ObjEditor.apply();
    }

    public float getPesoUnitario() {
        SharedPreferences preferences=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        return (preferences.getFloat("punitario",0.5F));
    }


    public void setUnidad(String Unidad){
        SharedPreferences preferencias=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putString("unidad",Unidad);
        ObjEditor.apply();
    }

    public String getUnidad() {
        return unidad;
    }

    public void set_DivisionMinima(int divmin){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt("div",divmin);
        ObjEditor.apply();

    }
    public void set_PuntoDecimal(int puntoDecimal){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt("pdecimal",puntoDecimal);
        ObjEditor.apply();

    }
    public void set_UltimaCalibracion(String ucalibracion){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString("ucalibracion",ucalibracion);
        ObjEditor.apply();

    }
    public String get_UltimaCalibracion(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        return Preferencias.getString("ucalibracion","");

    }
    public void set_CapacidadMax(String capacidad){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString("capacidad",capacidad);
        ObjEditor.apply();

    }
    public void set_PesoConocido(String pesoConocido){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString("pconocido",pesoConocido);
        ObjEditor.apply();

    }

    public int get_DivisionMinima(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        return Preferencias.getInt("div",0);

    }
    public int get_PuntoDecimal(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        return Preferencias.getInt("pdecimal",1);

    }
    public String get_CapacidadMax(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        return Preferencias.getString("capacidad","100");
    }
    public String get_PesoConocido(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        return Preferencias.getString("pconocido","100");
    }

    public void stopRuning(){
        estado=M_MODO_CALIBRACION;
    }
    public void startRuning(){
        estado=M_MODO_BALANZA;
    }

    public float redondear(float numero) {
        float factor = (float) Math.pow(10, puntoDecimal);
        return Math.round(numero * factor) / factor;
    }
    public static String floatToStringFormat(float numero, int puntodecimal) {
        String format = "%." + puntodecimal + "f";

        String resultado = String.format(format, numero);

        return resultado;
    }

    public static String completarFormato2(String numero) {

        int cerosFaltantes = 5 - numero.length();

        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < cerosFaltantes; i++) {
            resultado.append("0");
        }
        resultado.append(numero);

        return resultado.toString();

    }


    public static String completarFormato(String numero,int cantidad) {
        if (numero.length() > cantidad) {
            numero = numero.substring(numero.length() - cantidad-1);
        } else {
            int cerosFaltantes = cantidad - numero.length();

            StringBuilder resultado = new StringBuilder();
            for (int i = 0; i < cerosFaltantes; i++) {
                resultado.append("0");
            }
            resultado.append(numero);

            return resultado.toString();
        }

        return numero;
    }
    public String getCalculoPesoUnitario(){
        String copia=calculoPesoUnitario;
        calculoPesoUnitario="Error";
        return copia;
    }
    public static String removeLeadingZeros(BigDecimal number) {
        String formatted = number.toPlainString();
        if (formatted.contains(".")) {
            formatted = formatted.replaceFirst("^0+(?!\\.)", "");
            if (formatted.matches("^\\..*")) {
                formatted = "0" + formatted;
            }
        }
        return formatted;
    }

    public Runnable GET_PESO_cal_bza = new Runnable() {

        int contador=0,puntoDecimal=0;
        String read="",read2="";

        String[] array;
        @Override
        public void run() {


            try {
                if(serialPort.HabilitadoLectura()){
                    acumulador=0;
                    read=serialPort.read_2();
                    String filtro="\r\n";

                    if(read!=null){

                        if((read.toLowerCase().contains(filtro.toLowerCase()))){
                            estado=M_MODO_BALANZA;

                            if(read.toLowerCase().contains("S S".toLowerCase())){
                                estable="E";
                            }else if(read.toLowerCase().contains("S".toLowerCase())){
                                estable="S";
                            }else{
                                estable="";
                            }


                            array= read.split(filtro);

                            if(array.length>0){

                                if(read.contains("g")){
                                    unidad="gr";
                                }
                                if(read.contains("gr")){
                                    unidad="gr";
                                }
                                if(read.contains("kg")){
                                    unidad="kg";
                                }
                                if(read.contains("k")){
                                    unidad="kg";
                                }
                                read2=array[0];
                                read2=read2.replace(" ","");
                                read2=read2.replace("\r\n","");
                                read2=read2.replace("\r","");
                                read2=read2.replace("D","");
                                read2=read2.replace("S D","");
                                read2=read2.replace("\n","");
                                read2=read2.replace("\\u0007","");
                                read2=read2.replace("O","");
                                read2=read2.replace("E","");
                                read2=read2.replace("kg","");
                                read2=read2.replace("g","");
                                read2=read2.replace("gr","");
                                read2=read2.replace("?","");
                                read2=read2.replace(" ","");
                                read2=read2.replace("\r\n","");
                                read2=read2.replace("\r","");
                                read2=read2.replace("\n","");
                                read2=read2.replace("\\u0007","");
                                read2=read2.replace("O","");
                                read2=read2.replace("S","");
                                read2=read2.replace("kg","");
                                read2=read2.replace("g","");
                                read2=read2.replace("gr","");
                                read2=read2.replace("ST","");
                                read2=read2.replace("US","");
                                if(read2.contains("Err")){
                                    netoStr="Error";
                                    brutoStr="Error";
                                }
                                if(read2.contains("N")){
                                    setTaraDigital(0);
                                }
                                read2=read2.replace("N","");
                                read=read2.replace(".","");


                                if(Utils.isNumeric(read2)){
                                    int index = read2.indexOf('.'); // Busca el índice del primer punto en la cadena
                                    puntoDecimal = read.length() - index;
                                    brutoStr=read2;
                                    BigDecimal number = new BigDecimal(brutoStr);
                                    brutoStr = removeLeadingZeros(number);
                                    Bruto=Float.parseFloat(read2);

                                    if(taraDigital==0){
                                        Neto=Bruto-Tara;
                                        netoStr=String.valueOf(Neto);
                                        if(index==-1){
                                            netoStr=netoStr.replace(".0","");
                                        }
                                    }else{
                                        Neto=Bruto-taraDigital;
                                        netoStr=String.valueOf(Neto);
                                        if(index==-1){
                                            netoStr=netoStr.replace(".0","");
                                        }
                                    }
                                    if(index!=-1&&puntoDecimal>0){
                                        String formato="0.";

                                        StringBuilder capacidadBuilder = new StringBuilder(formato);
                                        for(int i=0;i<puntoDecimal;i++){
                                            capacidadBuilder.append("0");
                                        }
                                        formato = capacidadBuilder.toString();
                                        DecimalFormat df = new DecimalFormat(formato);
                                        netoStr = df.format(Neto);
                                        taraDigitalStr = df.format(taraDigital);
                                        //taraStr = df.format(ta);
                                    }
                                    if(Neto>pico){
                                        pico=Neto;
                                        picoStr=netoStr;
                                    }

                                    if(Bruto<pesoBandaCero){
                                        bandaCero =true;
                                    }
                                    else{
                                        if(inicioBandaPeso){
                                            bandaCero =false;
                                        }

                                    }


                                }
                                read="";

                            }


                        }
                    }
                }else{
                    if(acumulador==5){
                        mainActivity.Mensaje(M_ERROR_COMUNICACION, R.layout.item_customtoasterror);
                        netoStr="Negativo";
                        brutoStr="Negativo";
                    }
                    serialPort.write(CONSULTA_PIEZAS);
                    acumulador++;
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            serialPort.write(CONSULTA_BRUTO);
            mHandler.postDelayed(GET_PESO_cal_bza,200);

        }
    };
}
*///-----------------FIN DE ACTUALIZACION DEL 3/2/25 --------------------------------------------
    /** si ponemos tara digital, entonces toma la tara como tara digital,
     * si le damos a tara normal la tara digital pasa a cero y la tara es la tara
     *
     *
     */
    //PARA CONTADORA #005P03000102050000#013 TERMINAL
    private PuertosSerie serialPort;
    public static final int nBalanzas=1;
    private static final String CONSULTA_PIEZAS="PU\r\n", CONSULTA_BRUTO="SI\r\n";/* CONSULTA_PUNITARIO="XU2\r", CONSULTA_NETO="XN2\r",*/
    public static Boolean /*Tieneid=false,*/TieneCal =false;
    public Boolean inicioBandaPeso=false;/*, contador =false;,stopcomunicacion=false;*/
   // public String calculoPesoUnitario="Error",
    public int acumulador=0; /*runnableIndice=0,,piezas=0*/
    public static String Nombre="SPIDER3";
    public static String  Bauddef="9600";
    public static String  StopBdef="1";
    public static String   DataBdef="8";
    public static String   Paritydef="0";
    public static Boolean  TienePorDemanda =false;//true; SI TIENE PERO NO HECHO
   // public static int timeout=0;

    public SPIDER3(String Puerto, int id,AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener,int idaux) {
        super(Puerto,id,activity,fragmentChangeListener,nBalanzas);
        System.out.println("Init SPIDER3 "+id);
        try{
            this.serialPort = GestorPuertoSerie.getInstance().initPuertoSerie(Puerto,Integer.parseInt(Bauddef),Integer.parseInt(StopBdef),Integer.parseInt(DataBdef),Integer.parseInt(Paritydef),0,0);
        }finally {
            this.numBza = (this.serialPort.get_Puerto()*100)+ id;
        }}

    @Override public void setTara(int numBza) {
        if(serialPort!=null){
            serialPort.write("T\r\n");
        }
    }
    @Override public void setCero(int numBza) {
        if(serialPort!=null){
            serialPort.write("Z\r\n");
        }
        setTaraDigital(0);
    }


    @Override public void init(int numBza) {
        Estado =M_MODO_BALANZA;
        pesoUnitario=PreferencesDevicesManager.getPesoUnitario(Nombre,this.numBza,activity);
        pesoBandaCero= PreferencesDevicesManager.getPesoBandaCero(Nombre, this.numBza,activity);
        PuntoDecimal =PreferencesDevicesManager.getPuntoDecimal(Nombre,this.numBza,activity);
        ultimaCalibracion=PreferencesDevicesManager.getUltimaCalibracion(Nombre,this.numBza,activity);


        // Asociar el Handler al Looper del nuevo hilo
        if(serialPort!=null){
            Bucle.run();
            /*if(contador){
                setPesoUnitario(pesoUnitario);
                sendPuntoDecimal();
                GET_PESO_cal_bza.run();
            }else{
                GET_PESO_cal_bza.run();
            }*/

        }
    }
    @Override public void escribir(String msj,int numBza) {
        serialPort.write(msj);
    }
    @Override public void stop(int numBza) {
        try {
            serialPort.close();
        } catch (IOException e) {

        }
        serialPort=null;
        Estado =M_VERIFICANDO_MODO;
        try {
            mHandler.removeCallbacks(Bucle);
            handlerThread.quit();
        } catch (Exception e) {

        }
    }


   public Runnable Bucle = new Runnable() {
        int contador=0,puntoDecimal=0;
        String read="",read2="";
        String[] array;
        @Override
        public void run() {
            try {
              //  System.out.println("MENSAJE SPIDER3"+read.toString());
                if(serialPort.HabilitadoLectura()){
                    acumulador=0;
                    //   System.out.println("punto decimal: "+puntoDecimal);

                    latch = new CountDownLatch(1);
                    Semaforo.acquireUninterruptibly();
                    read=serialPort.read_2();
                    String filtro="\r\n";

                    // read=read.replace("\r\n","");
                    if(read!=null){
                        //     System.out.println("peso unit: "+getPesoUnitario());
                        //    System.out.println("MINIMA INDICE: "+runnableIndice);
                        if((read.toLowerCase().contains(filtro.toLowerCase()))){
                            Estado =M_MODO_BALANZA;
                            if(read.toLowerCase().contains("S S".toLowerCase())){
                                EstableBool =true;
                                SobrecargaBool=false;
                            }else if(read.toLowerCase().contains("S".toLowerCase())){
                                EstableBool =false;
                                SobrecargaBool=true;
                            }else{
                                EstableBool =false;
                                SobrecargaBool=false;
                            }
                            array= read.split(filtro);

                            if(array.length>0){

                                if(read.contains("g")){
                                    Unidad ="gr";
                                }
                                if(read.contains("gr")){
                                    Unidad ="gr";
                                }
                                if(read.contains("kg")){
                                    Unidad ="kg";
                                }
                                if(read.contains("k")){
                                    Unidad ="kg";
                                }
                                read2=array[0];
                                read2=read2.replace(" ","");
                                read2=read2.replace("\r\n","");
                                read2=read2.replace("\r","");
                                read2=read2.replace("D","");
                                read2=read2.replace("S D","");
                                read2=read2.replace("\n","");
                                read2=read2.replace("\\u0007","");
                                read2=read2.replace("O","");
                                read2=read2.replace("E","");
                                read2=read2.replace("kg","");
                                read2=read2.replace("g","");
                                read2=read2.replace("gr","");
                                read2=read2.replace("?","");
                                read2=read2.replace(" ","");
                                read2=read2.replace("\r\n","");
                                read2=read2.replace("\r","");
                                read2=read2.replace("\n","");
                                read2=read2.replace("\\u0007","");
                                read2=read2.replace("O","");
                                read2=read2.replace("S","");
                                read2=read2.replace("kg","");
                                read2=read2.replace("g","");
                                read2=read2.replace("gr","");
                                read2=read2.replace("ST","");
                                read2=read2.replace("US","");
                                if(read2.contains("Err")){
                                    NetoStr ="Error";
                                    BrutoStr ="Error";
                                }
                                if(read2.contains("N")){
                                    setTaraDigital(0);
                                }
                                read2=read2.replace("N","");
                                read=read2.replace(".","");


                                if(Utils.isNumeric(read2)){
                                    int index = read2.indexOf('.'); // Busca el índice del primer punto en la cadena
                                    puntoDecimal = read.length() - index;
                                    // uso bigdecimal porque si restaba me daba numeros raros detras de la coma
                                    BrutoStr =read2;
                                    BigDecimal number = new BigDecimal(BrutoStr);
                                    BrutoStr = Utils.removeLeadingZeros(number);
                                    Bruto=Float.parseFloat(read2);

                                    //Bruto= redondear(Bruto);
                                    //muestreoinstantaneo= bbruto.floatValue();
                                    if(TaraDigital ==0){
                                        Neto=Bruto-Tara;
                                        //Neto= redondear(Neto);
                                        NetoStr =String.valueOf(Neto);
                                        if(index==-1){
                                            NetoStr = NetoStr.replace(".0","");
                                        }
                                    }else{
                                        Neto=Bruto- TaraDigital;
                                        //Neto= redondear(Neto);
                                        NetoStr =String.valueOf(Neto);
                                        if(index==-1){
                                            NetoStr = NetoStr.replace(".0","");
                                        }
                                    }
                                    if(index!=-1&&puntoDecimal>0){
                                        String formato="0.";
                                        StringBuilder capacidadBuilder = new StringBuilder(formato);
                                        for(int i=0;i<puntoDecimal;i++){
                                            capacidadBuilder.append("0");
                                        }
                                        formato = capacidadBuilder.toString();
                                        DecimalFormat df = new DecimalFormat(formato);
                                        NetoStr = df.format(Neto);
                                        TaraDigitalStr = df.format(TaraDigital);
                                        //taraStr = df.format(ta);
                                    }

                                    if(Bruto<pesoBandaCero){
                                        BandaCero =true;
                                    }
                                    else{
                                        if(inicioBandaPeso){
                                            BandaCero =false;
                                        }
                                    }
                                }
                                read="";
                            }
                        }
                    }
                }else{
                    if(acumulador==5){
                        //mainActivity.Mensaje(M_ERROR_COMUNICACION, R.layout.item_customtoasterror);
                        NetoStr ="Negativo";
                        BrutoStr ="Negativo";
                    }
                    serialPort.write(CONSULTA_PIEZAS);
                    acumulador++;
                }
                latch.countDown();
                Semaforo.release();
            } catch (IOException e) {
                latch.countDown();
                Semaforo.release();
                //System.out.println("ERROR DE MINIMA, TRY-CATCH: "+e);
                e.printStackTrace();
            }
            serialPort.write(CONSULTA_BRUTO);
           if(mHandler!=null) {
               mHandler.postDelayed(this, 200);
           }
        }
    };


         /*@Override
    public Balanza getBalanza(int numBza) {
        return this;
    }*/
//    public void sendPuntoDecimal(){
//        String punto=String.valueOf(get_PuntoDecimal());
//        System.out.println("ENVIANDO PUNTO DECIMAL: "+punto);
//        serialPort.write("Pdu"+punto+"\r");
//
//    }
//    public void setTaraDigitalSerialPort(float tara){
//        String pesostr =floatToStringFormat(tara,puntoDecimal);
//        pesostr=completarFormato(pesostr,6);
//        pesostr=pesostr.replace(".","");
//        serialPort.write("YTD"+pesostr+"\r");
//
//    }
//    public void setPesoBandaCero(float peso){
//        pesoBandaCero=peso;
//        SharedPreferences preferencias=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
//        SharedPreferences.Editor ObjEditor=preferencias.edit();
//        ObjEditor.putFloat("pbandacero", peso);
//        ObjEditor.apply();
//    }
//    public void ConsultaPesoUnitario(){
//        serialPort.write(CONSULTA_PUNITARIO);
//    }
//    public String Guardar_cal(){
//        return "\u0005S\r";
//    }
//    public String Consultar_configuracion_memoria(){
//        return "\u0005O\r";
//    }
//    public String Peso_conocido(String pesoconocido,String PuntoDecimal){
//        if(pesoconocido.length()+Integer.parseInt(PuntoDecimal)>5){
//            return null;
//        }
//        StringBuilder capacidadBuilder = new StringBuilder(pesoconocido);
//        for(int i=0;i<Integer.parseInt(PuntoDecimal);i++){
//            capacidadBuilder.append("0");
//        }
//        pesoconocido = capacidadBuilder.toString();
//        if(pesoconocido.length()<5){
//            StringBuilder capacidadBuilder1 = new StringBuilder(pesoconocido);
//            while(capacidadBuilder1.length()!=5){
//                capacidadBuilder1.insert(0, "0");
//            }
//            pesoconocido = capacidadBuilder1.toString();
//        }
//        return "\u0005L"+pesoconocido+"\r";
//    }
//    public String Cero_cal(){
//        return "\u0005U\r";
//    }
//    public String Recero_cal(){
//        return "\u0005Z\r";
//    }
//    public String CapacidadMax_DivMin_PDecimal(String capacidad, String DivMin, String PuntoDecimal){
//        if(capacidad.length()+Integer.parseInt(PuntoDecimal)>5){
//            return null;
//        }
//        StringBuilder capacidadBuilder = new StringBuilder(capacidad);
//        for(int i=0;i<Integer.parseInt(PuntoDecimal);i++){
//            capacidadBuilder.append("0");
//        }
//        capacidad = capacidadBuilder.toString();
//        if(capacidad.length()<5){
//            StringBuilder capacidadBuilder1 = new StringBuilder(capacidad);
//            while(capacidadBuilder1.length()!=5){
//                capacidadBuilder1.insert(0, "0");
//            }
//            capacidad = capacidadBuilder1.toString();
//        }
//        return "\u0005D"+capacidad+"0"+DivMin+""+PuntoDecimal+"\r";
//    }
//    public String Salir_cal(){
//
//
//       // Service.openServiceFragment();
//        return "\u0005E \r";
//    }
//    public void setStopcomunicacion() throws IOException {
//        stopcomunicacion=true;
//        if(serialPort.HabilitadoLectura()){
//            serialPort.read_2();
//        }
//    }
//    public String Errores(String lectura){
//        if(lectura!=null){
//            if(lectura.charAt(0)==6&&lectura.charAt(2)!=32){
//                String Error="";
//                switch (lectura.charAt(1)) {
//                    case 'C':
//                        Error="C_CAL_";
//                        break;
//                    case 'S':
//                        Error="S_SAVE_";
//                        break;
//                    case 'P':
//                        Error="P_PARAM_";
//                        break;
//                    case 'D':
//                        Error="D_CAPMAX_";
//                        break;
//                    case 'U':
//                        Error="U_CERO_";
//                        break;
//                    case 'L':
//                        Error="L_CARGA";
//                        break;
//                    case 'Z':
//                        Error="Z_FIN";
//                        break;
//                    case 'M':
//                        Error="M_RECERO";
//                        break;
//                    case 'R':
//                        Error="R_RELOJ";
//                        break;
//                    case 'A':
//                        Error="A_DAC";
//                        break;
//                    case 'I':
//                        Error="I_I.D_";
//                        break;
//                    case 'O':
//                        Error="O_OPTIONS";
//                        break;
//                    default:
//                        return null;
//                }
//
//                switch (lectura.charAt(2)) {
//                    case 'a':
//                        Error=Error+"ERR AJUSTE";
//                        break;
//                    case 'b':
//                        Error=Error+"BAD LEN COMMNAD";
//                        break;
//                    case 'c':
//                        Error=Error+"ERR CERO";
//                        break;
//                    case 'd':
//                        Error=Error+"ERR PARTES";
//                        break;
//                    case 'e':
//                        Error=Error+"ERR ESCRITURA EEPROM";
//                        break;
//                    case 'f':
//                        Error=Error+"BAD ASCII_CHARACTER";
//                        break;
//                    case 'g':
//                        Error=Error+"NOT CAP.MAX.";
//                        break;
//                    case 'h':
//                        Error=Error+"NOT CAP.MAX./INICIAL";
//                        break;
//                    case 'i':
//                        Error=Error+"NOT CAP.MAX./INICIAL/PES.PAT./SPAN_FINAL";
//                        break;
//                    case 'j':
//                        Error=Error+"NOT END CALIB";
//                        break;
//                    case 'k':
//                        Error=Error+"NOT DEVICE HABILITADO";
//                        break;
//                    case 'l':
//                        Error=Error+"ERR LECTURA EEPROM";
//                        break;
//                    case 'p':
//                        Error=Error+"ERR PESO PATRON";
//                        break;
//                    default:
//                        return null;
//                }
//                return Error;
//            }
//        }
//        return null;
//    }
//    public String Error_a(){
//        return "ERR AJUSTE";
//    }
//    public String Error_b(){
//        return "BAD LEN COMMNAD";
//    }
//    public String Error_c(){
//        return "ERR CERO";
//    }
//    public String Error_d(){
//        return "ERR PARTES";
//    }
//    public String Error_e(){
//        return "ERR ESCRITURA EEPROM";
//    }
//    public String Error_f(){
//        return "BAD ASCII_CHARACTER";
//    }
//    public String Error_g(){
//        return "NOT CAP.MAX.";
//    }
//    public String Error_h(){
//        return "NOT CAP.MAX./INICIAL";
//    }
//    public String Error_i(){
//        return "NOT CAP.MAX./INICIAL/PES.PAT./SPAN_FINAL";
//    }
//    public String Error_j(){
//        return "NOT END CALIB";
//    }
//    public String Error_k(){
//        return "NOT DEVICE HABILITADO";
//    }
//    public String Error_l(){
//        return "ERR LECTURA EEPROM";
//    }
//    public String Error_P(){
//        return "ERR PESO PATRON";
//    }
//    public void setCantPiezas(int piezas){
//        String muestras=completarFormato2(String.valueOf(piezas));
//        serialPort.write("YMU"+muestras+"\r");
//    }
//    public void setPesoUnitario(float peso){
//        System.out.println("EL PESO UNITARIO"+peso);
//        float pesox1000=peso*1000;
//        String pesostr="";
//        if(puntoDecimal==0||puntoDecimal==1){
//            pesostr =floatToStringFormat(pesox1000,puntoDecimal);
//        }else{
//            pesostr =floatToStringFormat(pesox1000,puntoDecimal-1);
//        }
//
//        pesostr=completarFormato(pesostr,7);
//        serialPort.write("YPU"+pesostr+"\r");
//        pesoUnitario=peso;
//        SharedPreferences preferencias=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
//        SharedPreferences.Editor ObjEditor=preferencias.edit();
//        ObjEditor.putFloat("punitario", peso);
//        ObjEditor.apply();
//    }
//    public void set_PuntoDecimal(int puntoDecimal){
//
//        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
//        SharedPreferences.Editor ObjEditor=Preferencias.edit();
//        ObjEditor.putInt("pdecimal",puntoDecimal);
//        ObjEditor.apply();
//
//    }
//    public void set_UltimaCalibracion(String ucalibracion){
//
//        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
//        SharedPreferences.Editor ObjEditor=Preferencias.edit();
//        ObjEditor.putString("ucalibracion",ucalibracion);
//        ObjEditor.apply();
//
//    }

//    public void set_CapacidadMax(String capacidad){
//
//        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
//        SharedPreferences.Editor ObjEditor=Preferencias.edit();
//        ObjEditor.putString("capacidad",capacidad);
//        ObjEditor.apply();
//
//    }
//    public void set_PesoConocido(String pesoConocido){
//
//        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
//        SharedPreferences.Editor ObjEditor=Preferencias.edit();
//        ObjEditor.putString("pconocido",pesoConocido);
//        ObjEditor.apply();
//
//    }
//    public String get_PesoConocido(){
//        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
//        return Preferencias.getString("pconocido","100");
//    }
//    public int get_DivisionMinima(){
//        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
//        return Preferencias.getInt("div",0);
//
//    }

//    public String get_CapacidadMax(){
//        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
//        return Preferencias.getString("capacidad","100");
//    }
//    public void stopRuning(){
//        estado=M_MODO_CALIBRACION;
//    }
//    public void startRuning(){
//        estado=M_MODO_BALANZA;
//    }
//    public float redondear(float numero) {
//        float factor = (float) Math.pow(10, puntoDecimal);
//        return Math.round(numero * factor) / factor;
//    }
//    public static String floatToStringFormat(float numero, int puntodecimal) {
//        // Crear el formato de cadena con la cantidad de decimales deseada
//        String format = "%." + puntodecimal + "f";
//
//        // Formatear el número y convertirlo a una cadena
//        String resultado = String.format(format, numero);
//
//        return resultado;
//    }
//    public static String completarFormato2(String numero) {
//
//            // Si es menor, calcular cuántos ceros se deben agregar
//            int cerosFaltantes = 5 - numero.length();
//
//            // Construir la cadena completa con ceros a la izquierda
//            StringBuilder resultado = new StringBuilder();
//            for (int i = 0; i < cerosFaltantes; i++) {
//                resultado.append("0");
//            }
//            resultado.append(numero);
//
//            return resultado.toString();
//
//    }
//    public static String completarFormato(String numero,int cantidad) {
//        // Verificar si la longitud de la cadena es mayor a 6
//        if (numero.length() > cantidad) {
//            // Si es mayor, eliminar caracteres de la izquierda hasta que tenga 6 caracteres
//            numero = numero.substring(numero.length() - cantidad-1);
//        } else {
//            // Si es menor, calcular cuántos ceros se deben agregar
//            int cerosFaltantes = cantidad - numero.length();
//
//            // Construir la cadena completa con ceros a la izquierda
//            StringBuilder resultado = new StringBuilder();
//            for (int i = 0; i < cerosFaltantes; i++) {
//                resultado.append("0");
//            }
//            resultado.append(numero);
//
//            return resultado.toString();
//        }
//        return numero;
//    }
//    public String getCalculoPesoUnitario(){
//        String copia=calculoPesoUnitario;
//        calculoPesoUnitario="Error";
//        return copia;
//    }

//------------------------------------------------------------------------------------------
}
