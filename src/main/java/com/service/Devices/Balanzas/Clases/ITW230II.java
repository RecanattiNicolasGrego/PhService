package com.service.Devices.Balanzas.Clases;



import static com.service.utilsPackage.Utils.removeLeadingZeros;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.service.BalanzaService;
import com.service.Comunicacion.GestorPuertoSerie;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.Interfaz.Balanza;
import com.service.utilsPackage.PreferencesDevicesManager;
import com.service.R;
import com.service.utilsPackage.Utils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ITW230II  extends BalanzaBase implements Balanza, Serializable {

    /**
     * si ponemos tara digital, entonces toma la tara como tara digital,
     * si le damos a tara normal la tara digital pasa a cero y la tara es la tara
     */
    PuertosSerie.PuertosSerieListener receiver = null;
    public com.service.Interfaz.OnFragmentChangeListener fragmentChangeListener;
    public AppCompatActivity mainActivity;
    public PuertosSerie serialPort = null;
    public String ultimaCalibracion = "", brutoStr = "0", netoStr = "0", taraStr = "0", taraDigitalStr = "0", picoStr = "0", estable = "", estado = "VERIFICANDO_MODO";
    public static final String NOMBRE = "ITW230II", M_MODO_CALIBRACION = "MODO_CALIBRACION", M_MODO_BALANZA = "MODO_BALANZA", M_ERROR_COMUNICACION = "M_ERROR_COMUNICACION", M_VERIFICANDO_MODO = "VERIFICANDO_MODO";
    public float pesoBandaCero = 0F, pesoUnitario = 0.5F, taraDigital = 0, Bruto = 0, Tara = 0, Neto = 0, pico = 0;
    BalanzaService Service;
    public PuertosSerie.SerialPortReader readers = null;
    public static Boolean  TieneCal = true,TienePorDemanda = false;
    Boolean imgbool = false, Establebool = false, SobrecargaBool = false, inicioBandaPeso = false, bandaCero = true, estadoSobrecarga = false, estadoNeto = false, estadoPesoNeg = false, estadoBajoCero = false, estadoBzaEnCero = false, estadoBajaBat = false, estadoCentroCero = false, estadoEstable = false;
    public int puntoDecimal = 1, acumulador = 0, numero = 1, numeroid = 0;
    private ITW230II context = null;
    public static final int nBalanzas = 1;
    public static String Nombre = "ITW230II";
    public static String Bauddef = "9600";
    public static String StopBdef = "1";
    public static String DataBdef = "8";
    public static String Paritydef = "0";

    public ITW230II(String puerto, int id, AppCompatActivity activity, com.service.Interfaz.OnFragmentChangeListener fragmentChangeListener, int idaux) {
        super(puerto,id,activity,fragmentChangeListener,nBalanzas);
        this.serialPort = GestorPuertoSerie.getInstance().initPuertoSerie(puerto,Integer.parseInt(Bauddef),Integer.parseInt(DataBdef),Integer.parseInt(StopBdef),Integer.parseInt(Paritydef),0,0);
        this.numero = numero;
        this.mainActivity = activity;
        this.activity= activity;
        this.fragmentChangeListener = fragmentChangeListener;
        context = this;
    }
    @Override
    public void init(int numBza) {
        System.out.println("ITW230II init");
        estado = M_VERIFICANDO_MODO;
        pesoUnitario=PreferencesDevicesManager.getPesoUnitario(Nombre,this.numBza,activity);
        pesoBandaCero= PreferencesDevicesManager.getPesoBandaCero(Nombre,this.numBza,activity);
        PuntoDecimal =PreferencesDevicesManager.getPuntoDecimal(Nombre,this.numBza,activity);
        ultimaCalibracion=PreferencesDevicesManager.getUltimaCalibracion(Nombre,this.numBza,activity);
        if (this.serialPort != null) {
//           GET_PESO_cal_bza.run();
            receiverinit();
        }
    }
    public void receiverinit(){
        String filtro = "\r\n";
        receiver = new PuertosSerie.PuertosSerieListener() {
            @Override
            public void onMsjPort(String data) {
                System.out.println("ITW230II DATA: " + data);
                String[] array = new ArrayList<>().toArray(new String[0]);
                if (estado == M_MODO_BALANZA) {
                    String data2 = "";


                    array = data.split(filtro);
                    if (array.length > 0) {
                        data2 = array[0];
                        data2 = data2.replace(" ", "");
                        data2 = data2.replace("\r\n", "");
                        data2 = data2.replace("\r", "");
                        data2 = data2.replace("\n", "");
                        data2 = data2.replace("\\u0007", "");
                        data2 = data2.replace("O", "");
                        data2 = data2.replace("E", "");
                        data2 = data2.replace("kg", "");
                        data = data2.replace(".", "");
                        if (Utils.isNumeric(data2)) {
                            int index = data2.indexOf('.'); // Busca el índice del primer punto en la cadena
                            puntoDecimal = data.length() - index;
                            brutoStr = removeLeadingZeros(new BigDecimal(data2));
                            Bruto = Float.parseFloat(data2);
                            if (taraDigital == 0) {
                                Neto = Bruto - Tara;
                                if (index == -1) {
                                    netoStr = String.valueOf(Neto).replace(".0", "");
                                }
                            } else {
                                Neto = Bruto - taraDigital;
                                if (index == -1) {
                                    netoStr = String.valueOf(Neto).replace(".0", "");
                                }
                            }
                            if (index != -1 && puntoDecimal > 0) {
                                String formato = "0.";

                                StringBuilder capacidadBuilder = new StringBuilder(formato);
                                for (int i = 0; i < puntoDecimal; i++) {
                                    capacidadBuilder.append("0");
                                }
                                formato = capacidadBuilder.toString();
                                DecimalFormat df = new DecimalFormat(formato);
                                netoStr = df.format(Neto);
                                taraDigitalStr = df.format(taraDigital);
                            }
                        }
                    }
                }
            }


        };
        try{
            readers = new PuertosSerie.SerialPortReader(serialPort.getInputStream(), receiver);
            readers.startReading();
        }catch (Exception e){
            Utils.Mensaje(e.getMessage(), R.layout.item_customtoasterror,mainActivity);
        }

    }

    @Override
    public void escribir(String msj, int numBza) {
        serialPort.write(msj);
    }

    public void setTara(float tara) {
        Tara = tara;
        taraStr = String.valueOf(tara);
       // serialPort.write("T\u00013\u00010");
    }

    public void setTaraDigital(float tara) {
        taraDigital = tara;
        taraDigitalStr = String.valueOf(tara);
    }

    public float getPesoBandaCero() {
        SharedPreferences preferences = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numero) + "_" + "pbandacero", 5.0F));
    }

    public String Cero() {
        serialPort.write("KZERO\r\n");
        Tara = 0;
        setTaraDigital(0);
        return "KZERO\r\n";
    }

    public String Tara() {
        if (serialPort != null) {
            serialPort.write("KTARE\r");
        }
        return "KTARE\r\n";
    }

    private String Param(String str) {
        Integer decimal = Integer.parseInt(str, 2);
        String hexadecimal = Integer.toHexString(decimal).toUpperCase(); // Convertir decimal a hexadecimal
        while (hexadecimal.length() < 2) {
            hexadecimal = "0" + hexadecimal;
        }
        return hexadecimal;
    }

    public String EnviarParametros(String param1, String param2, String sp_prostr, String sp_offstr, String sp_acustr) { //NUEVO
        String Parametros = "\u0005P";
        Parametros += Param(param1); // Param1 Pf
        Parametros += Param(param2); // Param2 pd
        Parametros += sp_offstr;//"0";//#005P8200F000050000#013 1382000027100172E0001C3F810900F000050000 off
        Parametros += sp_prostr;//classcalibradora.sp_pro.getSelectedItem().toString();//"0";//#005P82000F00050000#013 1382000027100172E0001C3F8109000F00050000 pro
        Parametros += sp_acustr;//"0";//#005P820000F0050000#013 13820000// 27100172E0001C3F81090000F0050000 acu
        Parametros += "0";//#005P8200000F050000#013 1382000027100172E0001C3F810900000F050000 bf1
        Parametros += "0";//#005P82000000F50000#013 1382000027100172E0001C3F8109000000F50000 dat
        Parametros += "5";//#005P820000000F0000#013 1382000027100172E0001C3F81090000000F0000 bps
        Parametros += "0";//#005P8200000005F000#013 1382000027100172E0001C3F810900000005F000 ano
        Parametros += "0";//#005P82000000050F00#013 1382000027100172E0001C3F8109000000050F00 uni
        Parametros += "0";//#005P820000000500F0#013 1382000027100172E0001C3F81090000000500F0 reg
        Parametros += "0";//#005P8200000005000F#013 1382000027100172E0001C3F810900000005000F bot
        Parametros += "\r";
        System.out.println("ITW230II:" + Parametros.toString());

        return Parametros;
    }

    public void Pedirparam() { // NUEVO
        serialPort.write("\u0005O\r");
    }

    public void Mensaje(String texto, int Color) {
        final Toast[] toast = {null};
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LayoutInflater inflater = mainActivity.getLayoutInflater();
                toast[0] = new Toast(mainActivity.getApplicationContext());
                toast[0].setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast[0].setDuration(Toast.LENGTH_LONG);
            }
        });
    }

    public void Guardar_cal() {
        serialPort.write("\u0005S\r");
    }

    public String reset() {
        return "\u0005T\r";
    }

    public String ReAjusteCero() {
        return "\u0005M\r";
    }

    public String Peso_conocido(String pesoconocido, String PuntoDecimal) {
        String pesoConocido = "";
        if (pesoconocido.length() > 0) {
            pesoConocido = pesoconocido.replace(",", "");
            pesoConocido = pesoConocido.replace(".", "");
            int cerocount = 0;
            int enter = pesoConocido.length();
            int end = 0;
            if (pesoconocido.contains(".")) {
                enter = pesoconocido.indexOf('.');
                end = pesoconocido.length() - (enter);
            }
            if (end != 0) {
                if (end > Integer.parseInt(PuntoDecimal)) {
                    pesoConocido = String.format("%." + PuntoDecimal + "f", Double.parseDouble(pesoconocido));
                    pesoConocido = pesoConocido.replace(".", "");
                }
                if (enter + Integer.parseInt(PuntoDecimal) > 5 && end != 0) {
                    return null;
                }
                if (end < Integer.parseInt(PuntoDecimal) && end != 0) {
                    StringBuilder capacidadBuilder = new StringBuilder(pesoConocido);
                    for (int i = 0; i < Integer.parseInt(PuntoDecimal) - end; i++) {
                        capacidadBuilder.append("0");
                    }
                    pesoConocido = capacidadBuilder.toString();
                }
                if (pesoConocido.length() - cerocount < 5 && enter < (5 - Integer.parseInt(PuntoDecimal))) {
                    StringBuilder capacidadBuilder1 = new StringBuilder(pesoConocido);
                    while (capacidadBuilder1.length() - cerocount < 5) {
                        capacidadBuilder1.insert(0, "0");
                    }
                    pesoConocido = capacidadBuilder1.toString();
                }
                if (pesoConocido.length() > 5) {
                    StringBuilder capacidadBuilder1 = new StringBuilder(pesoConocido);
                    while (capacidadBuilder1.length() > 5) {
                        char lastChar = capacidadBuilder1.charAt(capacidadBuilder1.length() - 1);
                        if (lastChar == '0' && (capacidadBuilder1.length() - 1) - enter > Integer.parseInt(PuntoDecimal)) {
                            capacidadBuilder1 = new StringBuilder(capacidadBuilder1.substring(0, capacidadBuilder1.length() - 1));
                        } else {
                            return null;
                        }
                    }
                    pesoConocido = capacidadBuilder1.toString();
                }
            } else {
                if (pesoconocido.length() + Integer.parseInt(PuntoDecimal) > 5) { // PROBABLE PROBLEM
                    return null;
                }
                StringBuilder capacidadBuilder = new StringBuilder(pesoconocido);
                for (int i = 0; i < Integer.parseInt(PuntoDecimal); i++) {
                    capacidadBuilder.append("0");
                }
                pesoConocido = capacidadBuilder.toString();
                if (pesoconocido.length() < 5) {
                    StringBuilder capacidadBuilder1 = new StringBuilder(pesoConocido);
                    while (capacidadBuilder1.length() < 5) {
                        capacidadBuilder1.insert(0, "0");
                    }
                    pesoConocido = capacidadBuilder1.toString();
                }
            }
            String pesocon = String.format("%." + PuntoDecimal + "f", Double.parseDouble(pesoconocido));

        } else {
            return null;
        }
        return "\u0005L" + pesoConocido + "\r";
    }

    public String Cero_cal() {
        return "\u0005U\r";
    }

    public String Recero_cal() {
        return "\u0005Z\r";
    }

    public String CapacidadMax_DivMin_PDecimal(String capacidad, String DivMin, String PuntoDecimal) {
        if (capacidad.length() + Integer.parseInt(PuntoDecimal) > 5) {
            return null;
        }
        StringBuilder capacidadBuilder = new StringBuilder(capacidad);
        for (int i = 0; i < Integer.parseInt(PuntoDecimal); i++) {
            capacidadBuilder.append("0");
        }
        capacidad = capacidadBuilder.toString();
        if (capacidad.length() < 5) {
            StringBuilder capacidadBuilder1 = new StringBuilder(capacidad);
            while (capacidadBuilder1.length() != 5) {
                capacidadBuilder1.insert(0, "0");
            }
            capacidad = capacidadBuilder1.toString();
        }
        return "\u0005D" + capacidad + "0" + DivMin + "" + PuntoDecimal + "\r";
    }

    public static String Salir_cal() {
        return "\u0005E \r";
    }

    private ArrayList<Character> initlist2() {
        ArrayList<Character> lis = new ArrayList<>();
        lis.add('a');
        lis.add('b');
        lis.add('c');
        lis.add('d');
        lis.add('e');
        lis.add('f');
        lis.add('g');
        lis.add('h');
        lis.add('i');
        lis.add('j');
        lis.add('k');
        lis.add('p');
        return lis;
    }

    private ArrayList<Character> initlist() {
        ArrayList<Character> list = new ArrayList<>();
        list.add('C');
        list.add('S');
        list.add('P');
        list.add('D');
        list.add('U');
        list.add('L');
        list.add('Z');
        list.add('M');
        list.add('R');
        list.add('A');
        list.add('I');
        list.add('O');
        return list;
    }

    public ArrayList<String> Errores(String lectura) {
        ArrayList<String> listErr = new ArrayList<>();
        if (lectura != null) {
            ArrayList<Character> list = new ArrayList<>();
            ArrayList<Character> list2 = new ArrayList<>();
            list = initlist();
            list2 = initlist2();
            int lastindex = 0;
            ArrayList<String> listlect = new ArrayList<>();
            Boolean bol = false;
            for (int i = 0; i < list.size(); i++) {
                if (lectura.indexOf(list2.get(i)) != -1) {
                    bol = true;
                }
            }
            if (bol) {
                for (int i = 0; i < list.size(); i++) {
                    if (lectura.indexOf(list.get(i)) != -1) {
                        try {
                            listlect.add(lectura.substring(lastindex, lectura.indexOf(list.get(i)) + 3));
                            lastindex = (lectura.indexOf(list.get(i)) + 3);
                        } catch (Exception e) {
                        }
                    }
                }
                for (int i = 0; i < listlect.size(); i++) {
                    if (listlect.get(i).charAt(0) == 6 && listlect.get(i).charAt(2) != 32) {
                        System.out.println("ITW230II  paso weon" + listlect.get(i).charAt(0) + " " + listlect.get(i).charAt(1) + " " + listlect.get(i).charAt(2)
                        );
                        String Error = "";
                        switch (listlect.get(i).charAt(1)) {

                            case 'C':
                                Error = "C_CAL-";
                                break;
                            case 'S':
                                Error = "S_SAVE-";
                                break;
                            case 'P':
                                Error = "P_PARAM-";
                                break;
                            case 'D':
                                Error = "D_CAPMAX-";
                                break;
                            case 'U':
                                Error = "U_CERO-";
                                break;
                            case 'L':
                                Error = "L_CARGA";
                                break;
                            case 'Z':
                                Error = "Z_FIN";
                                break;
                            case 'M':
                                Error = "M_RECERO";
                                break;
                            case 'R':
                                Error = "R_RELOJ";
                                break;
                            case 'A':
                                Error = "A_DAC";
                                break;
                            case 'I':
                                Error = "I_I.D_";
                                break;
                            case 'O':
                                Error = "O_OPTIONS";
                                break;
                            default:
                                Error = "X_UNKNOWN";
                                break;
                        }

                        switch (listlect.get(i).charAt(2)) {
                            case 'a':
                                Error = Error + "ERR AJUSTE";
                                break;
                            case 'b':
                                Error = Error + "BAD LEN COMMNAD";
                                break;
                            case 'c':
                                Error = Error + "ERR CERO";
                                break;
                            case 'd':
                                Error = Error + "ERR PARTES";
                                break;
                            case 'e':
                                Error = Error + "ERR ESCRITURA EEPROM";
                                break;
                            case 'f':
                                Error = Error + "BAD ASCII_CHARACTER";
                                break;
                            case 'g':
                                Error = Error + "NOT CAP.MAX.";
                                break;
                            case 'h':
                                Error = Error + "NOT CAP.MAX./INICIAL";
                                break;
                            case 'i':
                                Error = Error + "NOT CAP.MAX./INICIAL/PES.PAT./SPAN_FINAL";
                                break;
                            case 'j':
                                Error = Error + "NOT END CALIB";
                                break;
                            case 'k':
                                Error = Error + "NOT DEVICE HABILITADO";
                                break;
                            case 'l':
                                Error = Error + "ERR LECTURA EEPROM";
                                break;
                            case 'p':
                                Error = Error + "ERR PESO PATRON";
                                break;
                            default:
                                Error = Error + "ERR UNKNOWN";
                                break;
                        }
                        switch (listlect.get(i).charAt(2)) {
                            case 'a':
                                Error = Error + ": Peso patrón colocado mayor al máximo permitido";
                                break;
                            case 'c':
                                Error = Error + ": Valor de señal, durante la toma del cero; es menor al mínimo permitido";
                                break;
                            case 'd':
                                Error = Error + ": (capacidad máxima/división mínima) es mayor a 5000 divisiones de display. Esta limitación es sólo para el modo rápido";
                                break;
                            case 'e':
                                Error = Error + ": Error interno. comunicarse con soporte";
                                break;
                            case 'f':
                                Error = Error + "";
                                break;
                            case 'g':
                                Error = Error + "";
                                break;
                            case 'h':
                                Error = Error + "";
                                break;
                            case 'i':
                                Error = Error + "";
                                break;
                            case 'j':
                                Error = Error + ": No termino la calibracion";
                                break;
                            case 'k':
                                Error = Error + ": Dispositivo no habilitado";
                                break;
                            case 'l':
                                Error = Error + "";
                                break;
                            case 'p':
                                Error = Error + ": Peso patrón colocado, es menor o igual al valor de la toma del cero";
                                break;
                            default:
                                Error = Error + "";
                                break;
                        }
                        listErr.add(Error);
                    }
                }
            }
        }
        if (listErr.size() >= 1) {
            return listErr;
        } else {
            return null;
        }
    }

    public void setPesoUnitario(float peso) {
        pesoUnitario = peso;
        SharedPreferences preferencias = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor = preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numero) + "_" + "punitario", 0.5F);
        ObjEditor.apply();
    }

    public float getPesoUnitario() {
        SharedPreferences preferences = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numero) + "_" + "punitario", 0.5F));
    }

    public void setUnidad(String Unidad) {
        SharedPreferences preferencias = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor = preferencias.edit();
        ObjEditor.putString(String.valueOf(numero) + "_" + "unidad", Unidad);
        ObjEditor.apply();
    }

    public String getUnidad() {
        SharedPreferences preferences = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        return (preferences.getString(String.valueOf(numero) + "_" + "unidad", "kg"));
    }



    public void set_DivisionMinima(int divmin) {
        SharedPreferences Preferencias = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor = Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numero) + "_" + "div", divmin);
        ObjEditor.apply();
    }

    public void set_PuntoDecimal(int puntoDecimal) {
        SharedPreferences Preferencias = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor = Preferencias.edit();
        ObjEditor.putInt(String.valueOf(numero) + "_" + "pdecimal", puntoDecimal);
        ObjEditor.apply();
    }

    public void set_UltimaCalibracion(String ucalibracion) {
        SharedPreferences Preferencias = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor = Preferencias.edit();
        ObjEditor.putString(String.valueOf(numero) + "_" + "ucalibracion", ucalibracion);
        ObjEditor.apply();
    }

    public String get_UltimaCalibracion() {
        SharedPreferences Preferencias = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numero) + "_" + "ucalibracion", "");
    }

    public void set_CapacidadMax(String capacidad) {
        SharedPreferences Preferencias = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor = Preferencias.edit();
        ObjEditor.putString(String.valueOf(numero) + "_" + "capacidad", capacidad);
        ObjEditor.apply();
    }

    public void set_PesoConocido(String pesoConocido) {
        SharedPreferences Preferencias = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor = Preferencias.edit();
        ObjEditor.putString(String.valueOf(numero) + "_" + "pconocido", pesoConocido);
        ObjEditor.apply();
    }

    public int get_PuntoDecimal() {
        SharedPreferences Preferencias = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        int lea = Preferencias.getInt(String.valueOf(numero) + "_" + "pdecimal", 1);
        return lea;
    }

    public String get_CapacidadMax() {
        SharedPreferences Preferencias = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numero) + "_" + "capacidad", "100");
    }

    public String get_PesoConocido() {
        SharedPreferences Preferencias = mainActivity.getSharedPreferences("ITW230II", Context.MODE_PRIVATE);
        return Preferencias.getString(String.valueOf(numero) + "_" + "pconocido", "100");
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

    Runnable GET_PESO_cal_bza = new Runnable() {
        String[] array;
        int contador = 0;
        String filtro = "\r\n";

        @Override
        public void run() {
            try {
                String data = serialPort.writendwaitStr("R\u00013"+"\u00010",200);
                array = data.split(filtro);
                if (array.length > 0) {
                    String  data2 = array[0];
                    data2 = data2.replace(" ", "");
                    data2 = data2.replace("\r\n", "");
                    data2 = data2.replace("\r", "");
                    data2 = data2.replace("\n", "");
                    data2 = data2.replace("\\u0007", "");
                    data2 = data2.replace("O", "");
                    data2 = data2.replace("E", "");
                    data2 = data2.replace("kg", "");
                    data = data2.replace(".", "");
                    if (Utils.isNumeric(data2)) {
                        int index = data2.indexOf('.'); // Busca el índice del primer punto en la cadena
                        puntoDecimal = data.length() - index;
                        brutoStr = removeLeadingZeros(new BigDecimal(data2));
                        Bruto = Float.parseFloat(data2);
                        if (taraDigital == 0) {
                            Neto = Bruto - Tara;
                            if (index == -1) {
                                netoStr = String.valueOf(Neto).replace(".0", "");
                            }
                        } else {
                            Neto = Bruto - taraDigital;
                            if (index == -1) {
                                netoStr = String.valueOf(Neto).replace(".0", "");
                            }
                        }
                        if (index != -1 && puntoDecimal > 0) {
                            String formato = "0.";

                            StringBuilder capacidadBuilder = new StringBuilder(formato);
                            for (int i = 0; i < puntoDecimal; i++) {
                                capacidadBuilder.append("0");
                            }
                            formato = capacidadBuilder.toString();
                            DecimalFormat df = new DecimalFormat(formato);
                            netoStr = df.format(Neto);
                            taraDigitalStr = df.format(taraDigital);
                        }
                        if (Neto > pico) {
                            pico = Neto;
                            picoStr = netoStr;
                        }
                        if (Bruto < pesoBandaCero) {
                            bandaCero = true;
                        } else {
                            if (inicioBandaPeso) {
                                bandaCero = false;
                            }
                        }
                        acumulador++;
                        }
                }
                } catch (Exception ex) {
                System.out.println("ERROR EN  ITW230II "+ex.getMessage());
            }
        };
    };
    @Override
    public void setID(int numID, int numBza) {
        numeroid = numID;
    }

    @Override
    public Integer getID(int numBza) {
        return numeroid;
    }

    @Override
    public Float getNeto(int numBza) {
        return Neto;
    }

    @Override
    public String getNetoStr(int numBza) {
        return netoStr;
    }

    @Override
    public Float getBruto(int numBza) {
        return Bruto;
    }

    @Override
    public String getBrutoStr(int numBza) {
        return brutoStr;
    }

    @Override
    public Float getTara(int numBza) {
        return taraDigital;
    }

    @Override
    public String getTaraStr(int numBza) {
        return taraDigitalStr;
    }

    @Override
    public void setTara(int numBza) {
        if (serialPort != null) {
            serialPort.write("KTARE\r");
        }
    }

    @Override
    public void setCero(int numBza) {
        if (serialPort != null) {
            serialPort.write("Z\u00013\u00010");
        }
        taraDigital = 0;
        taraDigitalStr = "0";
    }

    @Override
    public void setTaraDigital(int numBza, float TaraDigital) {
        taraDigital = TaraDigital;
        taraDigitalStr = String.valueOf(TaraDigital);
    }

    @Override
    public String getTaraDigital(int numBza) {
        return taraDigitalStr;
    }

    @Override
    public void setBandaCero(int numBza, Boolean bandaCeroi) {
        bandaCero = bandaCeroi;
    }

    @Override
    public Boolean getBandaCero(int numBza) {
        return bandaCero;
    }

    @Override
    public Float getBandaCeroValue(int numBza) {
        SharedPreferences preferences = mainActivity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getFloat(String.valueOf(numBza) + "_" + "pbandacero", 5.0F));
    }

    @Override
    public void setBandaCeroValue(int numBza, float bandaCeroValue) {
        pesoBandaCero = bandaCeroValue;
        SharedPreferences preferencias = mainActivity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor = preferencias.edit();
        ObjEditor.putFloat(String.valueOf(numBza) + "_" + "pbandacero", bandaCeroValue);
        ObjEditor.apply();
    }

    @Override
    public Boolean getEstable(int numBza) {
        return Establebool;
    }

    public String format(String numero) {
        String formato = "0.";
        try {
            StringBuilder capacidadBuilder = new StringBuilder(formato);
            for (int i = 0; i < puntoDecimal; i++) {
                capacidadBuilder.append("0");
            }
            formato = capacidadBuilder.toString();
            DecimalFormat df = new DecimalFormat(formato);
            String str = df.format(Double.parseDouble(numero));
            return str;
        } catch (NumberFormatException e) {
            System.err.println("Error: El número no es válido.");
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    public String format(int numero, String peso) {
        String formato = "0.";
        try {
            StringBuilder capacidadBuilder = new StringBuilder(formato);
            for (int i = 0; i < puntoDecimal; i++) {
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

    @Override
    public String getUnidad(int numBza) {
        SharedPreferences preferences = mainActivity.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE);
        return (preferences.getString(String.valueOf(numBza) + "_" + "unidad", "kg"));
    }

    @Override
    public void stop(int numBza) {
//        serialPort = null;
        try {
            readers.stopReading();
            estado = M_VERIFICANDO_MODO;
            //mHandler.removeCallbacks(GET_PESO_cal_bza);

            handlerThread.quit();
        } catch (Exception e) {

        }
    }

    @Override
    public void start(int numBza) {
        estado = M_MODO_BALANZA;
    }

    @Override
    public Boolean calibracionHabilitada(int numBza) {
        return false;
    }

    @Override
    public void openCalibracion(int numero) {

    }

    @Override
    public Boolean getSobrecarga(int numBza) {
        return SobrecargaBool;
    }


}
