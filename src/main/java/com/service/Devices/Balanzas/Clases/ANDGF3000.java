package com.service.Devices.Balanzas.Clases;

import androidx.appcompat.app.AppCompatActivity;

import com.service.Comunicacion.GestorPuertoSerie;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.PreferencesDevicesManager;
import com.service.Utils;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Objects;

public class ANDGF3000 extends BalanzaBase{

/***
 * ES NECESARIO ACTUALIZAR DESDE EL 3/2/25 CON :
 */
/*
public class ANDGF3000 {
private final Context context;
    private final PuertosSerie serialPort;
    private MainActivity mainActivity;
    Handler mHandler= new Handler();

    public String estado="VERIFICANDO_MODO";
    public static final String M_VERIFICANDO_MODO="VERIFICANDO_MODO";
    public static final String M_MODO_BALANZA="MODO_BALANZA";
    public static final String M_MODO_CALIBRACION="MODO_CALIBRACION";
    public static final String M_ERROR_COMUNICACION="M_ERROR_COMUNICACION";
    public float taraDigital=0,Bruto=0,Tara=0,Neto=0,pico=0;
    public String estable="";
    final String nombre="ANDGF3000";
    float pesoUnitario=0.5F;
    float pesoBandaCero=0F;
    public Boolean bandaCero =true;
    public Boolean inicioBandaPeso=false;
    public int puntoDecimal=1;
    public String ultimaCalibracion="";
    public String brutoStr="0",netoStr="0",taraStr="0",taraDigitalStr="0",picoStr="0";
    public int acumulador=0;
    public int numero=1;
    public String unidad="gr";

    public ANDGF3000(Context context, PuertosSerie serialPort, MainActivity activity, int numero) {
        this.context = context;
        this.serialPort = serialPort;
        this.mainActivity = activity;
        this.numero= numero;
    }

    public void init(){
        estado=M_VERIFICANDO_MODO;
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

    public void setPesoBandaCero(float peso){
        pesoBandaCero=peso;
        SharedPreferences preferencias=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numero)+"_"+"pbandacero", peso);
        ObjEditor.apply();
    }

    public float getPesoBandaCero() {
        SharedPreferences preferences=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numero)+"_"+"pbandacero",5.0F));
    }

    public String Cero(){
        serialPort.write("Z\r\n");
        Tara=0;
        setTaraDigital(0);
        return "Z\r\n";
    }
    public String Tara(){
        if(serialPort!=null){
            serialPort.write("T\r\n");
        }
        return "T\r\n";
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


    public void setPesoUnitario(float peso){
        pesoUnitario=peso;
        SharedPreferences preferencias=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numero)+"_"+"punitario", 0.5F);
        ObjEditor.apply();
    }

    public float getPesoUnitario() {
        SharedPreferences preferences=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numero)+"_"+"punitario",0.5F));
    }


    public void setUnidad(String Unidad){
        SharedPreferences preferencias=context.getSharedPreferences(nombre, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=preferencias.edit();
        ObjEditor.putString(String.valueOf(numero)+"_"+"unidad",Unidad);
        ObjEditor.apply();
    }

    public String getUnidad() {
        return unidad;
    }

    public void set_DivisionMinima(int divmin){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numero)+"_"+"div",divmin);
        ObjEditor.apply();

    }
    public void set_PuntoDecimal(int puntoDecimal){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numero)+"_"+"pdecimal",puntoDecimal);
        ObjEditor.apply();

    }
    public void set_UltimaCalibracion(String ucalibracion){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numero)+"_"+"ucalibracion",ucalibracion);
        ObjEditor.apply();

    }
    public String get_UltimaCalibracion(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numero)+"_"+"ucalibracion","");

    }
    public void set_CapacidadMax(String capacidad){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numero)+"_"+"capacidad",capacidad);
        ObjEditor.apply();

    }
    public void set_PesoConocido(String pesoConocido){

        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor=Preferencias.edit();
        ObjEditor.putString(String.valueOf(numero)+"_"+"pconocido",pesoConocido);
        ObjEditor.apply();

    }

    public int get_DivisionMinima(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        return Preferencias.getInt(String.valueOf(numero)+"_"+"div",0);

    }
    public int get_PuntoDecimal(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        int lea=Preferencias.getInt(String.valueOf(numero)+"_"+"pdecimal",1);
        System.out.println("ANDGF CALIBRACION PUNTO DECIMAL: "+String.valueOf(lea));
        return lea;

    }
    public String get_CapacidadMax(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numero)+"_"+"capacidad","100");
    }
    public String get_PesoConocido(){
        SharedPreferences Preferencias=context.getSharedPreferences(nombre,Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numero)+"_"+"pconocido","100");
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

    public Runnable GET_PESO_cal_bza= new Runnable() {

        int contador=0,puntoDecimal=0;
        String read="",read2="";
        String[] array;
        @Override
        public void run() {

            if(!Objects.equals(estado, M_MODO_CALIBRACION)){
                try {
                    if(serialPort.HabilitadoLectura()){
                        System.out.println("AND GF HABILITADO LECTURA");
                        read=serialPort.read_2();
                        String filtro="\r\n";
                        if(read!=null){
                            System.out.println("AND GF: "+read);
                            if(read.toLowerCase().contains(filtro.toLowerCase())){
                                estado=M_MODO_BALANZA;
                                if(read.toLowerCase().contains("ST".toLowerCase())){
                                    estable="E";
                                }else if(read.toLowerCase().contains("US".toLowerCase())){
                                    estable="S";
                                }else{
                                    estable="";
                                }

                                if(read.contains("g")){
                                    unidad="gr";
                                }
                                if(read.contains("kg")){
                                    unidad="kg";
                                }
                                if(read.contains("k")){
                                    unidad="kg";
                                }

                                array= read.split(filtro);

                                if(array.length>0){


                                    read2=array[0];
                                    read2=read2.replace(" ","");
                                    read2=read2.replace("\r\n","");
                                    read2=read2.replace("\r","");
                                    read2=read2.replace("\n","");
                                    read2=read2.replace("\\u0007","");
                                    read2=read2.replace("O","");
                                    read2=read2.replace("E","");
                                    read2=read2.replace("kg","");
                                    read2=read2.replace("g","");
                                    read2=read2.replace("gr","");
                                    read2=read2.replace("ST","");
                                    read2=read2.replace(",","");
                                    read2=read2.replace("US","");
                                    read=read2.replace(".","");

                                    if(Utils.isNumeric(read2)){
                                        int index = read2.indexOf('.'); // Busca el índice del primer punto en la cadena
                                        puntoDecimal = read.length() - index;
                                        // uso bigdecimal porque si restaba me daba numeros raros detras de la coma
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
                                        acumulador++;

                                    }
                                    read="";

                                }

                            }else if (read.contains("\u0006C \r")){

                                estado=M_MODO_CALIBRACION;
                            }
                        }
                    }
                    else if (contador<=8&& Objects.equals(estado, M_VERIFICANDO_MODO)) {
                        if (contador == 0) {
                            System.out.println("ANDGF:BUSCANDO CALIBRACION");
                            serialPort.write("\u0006C \r");
                        } else {
                            System.out.println("ANDGF:BUSCANDO CALIBRACION");
                            serialPort.write("\u0005C \r");
                        }

                        contador++;
                    }

                    if (contador==8){
                        mainActivity.Mensaje(M_ERROR_COMUNICACION, R.layout.item_customtoasterror);
                        contador++;
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
                mHandler.postDelayed(GET_PESO_cal_bza,100);

            }
            else {
                mHandler.postDelayed(GET_PESO_cal_bza,50);
            }
        }
    };
}
*/// -----------------------------FIN  DE ACTUALIZACION DEL 3/2/25-------------------------------------
    /** si ponemos tara digital, entonces toma la tara como tara digital,
     * si le damos a tara normal la tara digital pasa a cero y la tara es la tara
     *
     *
     */
    private  PuertosSerie serialPort;
    public static Boolean /*Tieneid=false,*/TieneCal =false;
//    public static final String M_ERROR_COMUNICACION="M_ERROR_COMUNICACION";
    public static String Nombre ="ANDGF3000";
    public Boolean inicioBandaPeso=false;
    public static Integer nBalanzas=1;
    public static final  String Bauddef="9600",StopBdef="1",DataBdef="8",Paritydef="0";
    public Integer acumulador=0;
    public ANDGF3000(String Puerto, int id, AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener,int idaux) {
        super(Puerto,id, activity,fragmentChangeListener,idaux);
        try {
            this.serialPort = GestorPuertoSerie.getInstance().initPuertoSerie(Puerto,Integer.parseInt(Bauddef),Integer.parseInt(DataBdef),Integer.parseInt(StopBdef),Integer.parseInt(Paritydef),0,0);
        } finally {
            this.numBza = (this.serialPort.get_Puerto()*10)+ id;
        }
    }
    @Override public void init(int numBza) {
        Estado =M_VERIFICANDO_MODO;
        pesoUnitario=PreferencesDevicesManager.getPesoUnitario(Nombre,this.numBza,activity);
        pesoBandaCero=PreferencesDevicesManager.getPesoBandaCero(Nombre,this.numBza,activity);
        PuntoDecimal =PreferencesDevicesManager.getPuntoDecimal(Nombre,this.numBza,activity);
        ultimaCalibracion=PreferencesDevicesManager.getUltimaCalibracion(Nombre,this.numBza,activity);
        if(serialPort!=null){
            Bucle.run();
        }
    }
    @Override public void escribir(String msj,int numBza) {serialPort.write(msj);}
    @Override public void stop(int numBza) {
        try {
            serialPort.close();
        } catch (IOException e) {

        }
        serialPort=null;
        Estado =M_VERIFICANDO_MODO;
        mHandler.removeCallbacks(Bucle);
    }
    @Override public void setTara(int numBza) {
        if(serialPort!=null){
            serialPort.write("T\r\n");
        }

        super.setTara(numBza);
    }
    /*@Override
    public Balanza getBalanza(int numBza) {
        return this;
    }*/

    public Runnable Bucle = new Runnable() {

        int contador=0;
        String read="",read2="";
        String[] array;
        @Override
        public void run() {

            if(!Objects.equals(Estado, M_MODO_CALIBRACION)){
                try {
                    if(serialPort.HabilitadoLectura()){
              //          System.out.println("AND GF HABILITADO LECTURA");
                        read=serialPort.read_2();

                        String filtro="\r\n";
                        // read=read.replace("\r\n","");

                        if(read!=null){
                    //        System.out.println("AND GF: "+read);
                            if(read.toLowerCase().contains(filtro.toLowerCase())){
                                Estado =M_MODO_BALANZA;
                                if(read.toLowerCase().contains("ST".toLowerCase())){
                                    EstableBool =true;
                                    SobrecargaBool=false;
                                }else if(read.toLowerCase().contains("US".toLowerCase())){

                                    EstableBool =false;
                                    SobrecargaBool=true;
                                }else{
                                    EstableBool =false;
                                    SobrecargaBool=false;
                                }

                                if(read.contains("g")){
                                    Unidad ="gr";
                                }
                                if(read.contains("kg")){
                                    Unidad ="kg";
                                }
                                if(read.contains("k")){
                                    Unidad ="kg";
                                }

                                array= read.split(filtro);

                                if(array.length>0){


                                    read2=array[0];
                                    read2=read2.replace(" ","");
                                    read2=read2.replace("\r\n","");
                                    read2=read2.replace("\r","");
                                    read2=read2.replace("\n","");
                                    read2=read2.replace("\\u0007","");
                                    read2=read2.replace("O","");
                                    read2=read2.replace("E","");
                                    read2=read2.replace("kg","");
                                    read2=read2.replace("g","");
                                    read2=read2.replace("gr","");
                                    read2=read2.replace("ST","");
                                    read2=read2.replace(",","");
                                    read2=read2.replace("US","");
                                    read=read2.replace(".","");

                                    if(Utils.isNumeric(read2)){
                                        int index = read2.indexOf('.'); // Busca el índice del primer punto en la cadena
                                        PuntoDecimal = read.length() - index;
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
                                        if(index!=-1&& PuntoDecimal >0){
                                            String formato="0.";

                                            StringBuilder capacidadBuilder = new StringBuilder(formato);
                                            for(int i = 0; i< PuntoDecimal; i++){
                                                capacidadBuilder.append("0");
                                            }
                                            formato = capacidadBuilder.toString();
                                            DecimalFormat df = new DecimalFormat(formato);
                                            NetoStr = df.format(Neto);
                                            TaraDigitalStr = df.format(TaraDigital);
                                            //taraStr = df.format(ta);
                                        }
                                        if(Neto>pico){
                                            pico=Neto;
                                            picoStr= NetoStr;
                                        }

                                        if(Bruto<pesoBandaCero){
                                            BandaCero =true;
                                        }
                                        else{
                                            if(inicioBandaPeso){
                                                BandaCero =false;
                                            }

                                        }
                                        acumulador++;

                                    }
                                    read="";

                                }

                            }else if (read.contains("\u0006C \r")){
                                //entro a calibracion
                                   /* if(numero>0){
                                        mainActivity.MainClass.openFragment(new CalibracionANDGF1Fragment());
                                    }else{
                                        mainActivity.MainClass.openFragment(new CalibracionANDGFFragment());
                                    }*/

                                Estado =M_MODO_CALIBRACION;
                            }
                        }
                    }
                    else if (contador<=8&& Objects.equals(Estado, M_VERIFICANDO_MODO)) {
                        if (contador == 0) {
                      //      System.out.println("ANDGF:BUSCANDO CALIBRACION");
                            serialPort.write("\u0006C \r");
                        } else {
                      //      System.out.println("ANDGF:BUSCANDO CALIBRACION");
                            serialPort.write("\u0005C \r");
                        }

                        contador++;
                    }

                    if (contador==8){
                        //mainActivity.Mensaje(M_ERROR_COMUNICACION, R.layout.item_customtoasterror);
                        contador++;
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                }
                mHandler.postDelayed(Bucle,100);

            }
            else {
                mHandler.postDelayed(Bucle,50);
            }
        }
    };
    @Override public void setCero(int numBza) {
        serialPort.write("Z\r\n");
        setTaraDigital(0);
    }
    //
//    public String Cero(){
//        serialPort.write("Z\r\n");
//        Tara=0;
//        setTaraDigital(numeroBZA,0);
//        return "Z\r\n";
//    }
//    public String Guardar_cal(){
//        return "\u0005S\r";
//    }
//    public String Consultar_configuracion_memoria(){
//        return "\u0005O\r";
//    }
//public String Peso_conocido(String pesoconocido,String PuntoDecimal){
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
//


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
//        return "\u0005E \r";
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
//    public void stopRuning(){
//        estado=M_MODO_CALIBRACION;
//    }
//    public void startRuning(){
//        estado=M_MODO_BALANZA;
//    }
//    public Float redondear(float numero) {
//        float factor = (float) Math.pow(10, puntoDecimal);
//        return Math.round(numero * factor) / factor;
//    }


//    @Override public String format(int numero, String peso) {return "";}


    //--------------------------------------------------------------------------------
}
