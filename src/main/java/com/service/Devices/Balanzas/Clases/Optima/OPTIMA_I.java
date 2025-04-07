package com.service.Devices.Balanzas.Clases.Optima;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.service.Devices.Balanzas.Clases.BalanzaBase;
import com.service.Comunicacion.GestorPuertoSerie;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.Interfaz.Balanza;
import com.service.PreferencesDevicesManager;
import com.service.R;
import com.service.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class OPTIMA_I extends BalanzaBase implements Balanza.Optima_Image ,Serializable {
    PuertosSerie.PuertosSerieListener receiver = null;
    public static final String Nombre = "OPTIMA";
    public PuertosSerie serialPort = null;
    private Boolean isRunning = false;
    public static Integer nBalanzas=1;
    public static final String StopBdef = "1", Bauddef = "9600", DataBdef = "8", Paritydef = "0";
    public PuertosSerie.SerialPortReader readers = null;
    public static Boolean /*Tieneid=false,*/TieneCal = false;
    Boolean  imgbool = false,  inicioBandaPeso = false, estadoNeto = false, estadoPesoNeg = false, estadoBajoCero = false, estadoBzaEnCero = false, estadoBajaBat = false, estadoCentroCero = false;
    public int  acumulador = 0;
    public OPTIMA_I(String puerto, int numero, AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener, int idaux) {

        super(puerto, numero, activity, fragmentChangeListener,idaux);
        try {
            System.out.print("INIT OPTIM");
            this.serialPort = GestorPuertoSerie.getInstance().initPuertoSerie(puerto, Integer.parseInt(Bauddef), Integer.parseInt(DataBdef), Integer.parseInt(StopBdef), Integer.parseInt(Paritydef), 0, 0);
            Thread.sleep(300);
        } catch (InterruptedException e) {

        } finally {
            this.numBza = (this.serialPort.get_Puerto() * 10) + numero; // si no tiene id seria :  10,20,3x  ; SI PUERTO 1 y 2 TIENEN ID ->(puerto.get_Puerto()*100)+numero;  Y CONTROLAR DE ALGUNA FORMA QUE ID NO TENGA 3 CIFRAS
        }
    }
   /* @Override
    public Balanza getBalanza(int numBza) {
        return this;
    }*/
    private void receiverinit() {
        String filtro = "\r\n";
        receiver = new PuertosSerie.PuertosSerieListener() {
            @Override
            public void onMsjPort(String data) {
            //    System.out.println("OPTIMA DATA: " + data);
                String[] array = new ArrayList<>().toArray(new String[0]);
                if (Objects.equals(Estado, M_MODO_BALANZA)) {
                    String data2 = "";

                    if (imgbool) {
                        int count = 0;
                        char[] arr = data.toCharArray();
                        for (int i = 0; i < data.length(); i++) {
                            if (arr[i] == '0' || arr[i] == '1' || arr[i] == '2' || arr[i] == '3' || arr[i] == '4' || arr[i] == '5' || arr[i] == '6' || arr[i] == '7' || arr[i] == '8' || arr[i] == '9') {
                                count++;
                            }
                        }
                        if (count <= 3) {
                            String str = data.replace(",", "");
                            str.trim();
                            byte[] bytesISO = str.getBytes(StandardCharsets.ISO_8859_1);
                            int valorSinSigno = bytesISO[0] & 0xFF; // Esto convierte el byte a un entero de 0 a 255
                            String binario = String.format("%8s", Integer.toBinaryString(valorSinSigno)).replace(' ', '0');
                            char[] arrbin = binario.toCharArray();
                            estadoCentroCero = (arrbin[7] == '1');
                            SobrecargaBool = (arrbin[6] == '1');
                            estadoNeto = (arrbin[5] == '1');
                            estadoPesoNeg = (arrbin[4] == '1');
                            estadoBajoCero = (arrbin[3] == '1');
                            estadoBzaEnCero = (arrbin[2] == '1');
                            estadoBzaEnCero = (arrbin[2] == '1');
                            estadoBajaBat = (arrbin[1] == '1');
                            EstableBool = (arrbin[0] == '1');
                        } else {
                            String[] arrPeso = data.split(",");
                            data2 = arrPeso[0];
                            data2 = data2.replace(" ", "");
                            data2 = data2.replace("\r\n", "");
                            data2 = data2.replace("\r", "");
                            data2 = data2.replace("\n", "");
                            data2 = data2.replace("\\u0007", "");
                            data2 = data2.replace("O", "");
                            data2 = data2.replace("E", "");
                            data2 = data2.replace("S", "");
                            data2 = data2.replace("kg", "");
                            data = data2.replace(".", "");
                            if (Utils.isNumeric(data)) {
                                int index = data2.indexOf('.'); // Busca el índice del primer punto en la cadena
                                PuntoDecimal = data.length() - index;
                                // uso bigdecimal porque si restaba me daba numeros raros detras de la coma
                                BrutoStr = Utils.removeLeadingZeros(new BigDecimal(data2));
                                Bruto = Float.parseFloat(data2);
                            }

                            data2 = arrPeso[1];
                            if (data2.contains("E")) { // && !data2.matches("kg") lo saque por que en minima no funciona, pero por ahi es parte de optima
                                EstableBool = true;
                                SobrecargaBool = false;
                            } else if (data2.contains("S") ) { // && !data2.matches("kg") lo saque por que en minima no funciona, pero por ahi es parte de optima
                                EstableBool = false;
                                SobrecargaBool = true;
                            } else {
                                if (true) { //!data2.matches("kg") lo saque por que en minima no funciona, pero por ahi es parte de optima
                                    EstableBool = false;
                                    SobrecargaBool = false;
                                }
                            }
                            data2 = data2.replace(" ", "");
                            data2 = data2.replace("\r\n", "");
                            data2 = data2.replace("\r", "");
                            data2 = data2.replace("\n", "");
                            data2 = data2.replace("\\u0007", "");
                            data2 = data2.replace("O", "");
                            data2 = data2.replace("S", "");
                            data2 = data2.replace("E", "");
                            data2 = data2.replace("kg", "");
                            data = data2.replace(".", "");
                            if (Utils.isNumeric(data)) { // data2 lo saque por que en minima no funciona, pero por ahi es parte de optima

                                int index = data2.indexOf('.'); // Busca el índice del primer punto en la cadena
                                PuntoDecimal = data2.length() - index;
                                NetoStr = Utils.removeLeadingZeros(new BigDecimal(data2));
                                Neto = Float.parseFloat(data2);
                            }
                            Tara = Bruto - Neto;
                            String formato = "0.";
                            StringBuilder capacidadBuilder = new StringBuilder(formato);
                            for (int i = 0; i < PuntoDecimal; i++) {
                                capacidadBuilder.append("0");
                            }
                            formato = capacidadBuilder.toString();
                            DecimalFormat df = new DecimalFormat(formato);
                            TaraStr = df.format(Tara);
                        }
                        if (Neto > pico) {
                            pico = Neto;
                            picoStr = NetoStr;
                        }
                        if (Bruto < pesoBandaCero) {
                            BandaCero = true;
                        } else {
                            if (inicioBandaPeso) {
                                BandaCero = false;
                            }
                        }
                        acumulador++;
                    } else {
                       // if (!data.contains(filtro.toLowerCase())) { lo saque por que en minima no funciona, pero por ahi es parte de optima
                            data2 = data;

                        array = data.split(filtro);
                     //   if (array.length > 0) {  lo saque por que en minima no funciona, pero por ahi es parte de optima
                            data2 = array[0];
                        if (data2.contains("E")) { // && !data2.matches("kg") lo saque por que en minima no funciona, pero por ahi es parte de optima
                            EstableBool = true;
                            SobrecargaBool = false;
                        } else if (data2.contains("S") ) { // && !data2.matches("kg") lo saque por que en minima no funciona, pero por ahi es parte de optima
                            EstableBool = false;
                            SobrecargaBool = true;
                        } else {
                            if (!data2.contains("kg")) { //!data2.matches("kg") lo saque por que en minima no funciona, pero por ahi es parte de optima
                                EstableBool = false;
                                SobrecargaBool = false;
                            }
                        }
                            data2 = data2.replace(" ", "");
                            data2 = data2.replace("\r\n", "");
                            data2 = data2.replace("\r", "");
                            data2 = data2.replace("\n", "");
                            data2 = data2.replace("\\u0007", "");
                            data2 = data2.replace("O", "");
                            data2 = data2.replace("S", "");
                            data2 = data2.replace("E", "");
                            data2 = data2.replace("kg", "");
                            data = data2.replace(".", "");
                            if (Utils.isNumeric(data)) { // data2 lo saque por que en minima no funciona, pero por ahi es parte de optima
                                int index = data2.indexOf('.'); // Busca el índice del primer punto en la cadena
                                PuntoDecimal = data.length() - index;
                                BrutoStr = Utils.removeLeadingZeros(new BigDecimal(data2));
                                Bruto = Float.parseFloat(data2);
                                if (TaraDigital == 0) {
                                    Neto = Bruto - Tara;
                                    if (index == -1) {
                                        NetoStr = String.valueOf(Neto).replace(".0", "");
                                    }
                                } else {
                                    Neto = Bruto - TaraDigital;
                                    if (index == -1) {
                                        NetoStr = String.valueOf(Neto).replace(".0", "");
                                    }
                                }
                                if (index != -1 && PuntoDecimal > 0) {
                                    String formato = "0.";

                                    StringBuilder capacidadBuilder = new StringBuilder(formato);
                                    for (int i = 0; i < PuntoDecimal; i++) {
                                        capacidadBuilder.append("0");
                                    }
                                    formato = capacidadBuilder.toString();
                                    DecimalFormat df = new DecimalFormat(formato);
                                    NetoStr = df.format(Neto);
                                    TaraDigitalStr = df.format(TaraDigital);
                                }
                                if (Neto > pico) {
                                    pico = Neto;
                                    picoStr = NetoStr;
                                }
                                if (Bruto < pesoBandaCero) {
                                    BandaCero = true;
                                } else {
                                    if (inicioBandaPeso) {
                                        BandaCero = false;
                                    }
                                }
                                acumulador++;
                            }
                      //  }
                    }
                }
            }
        };
        try {
            readers = new PuertosSerie.SerialPortReader(serialPort.getInputStream(), receiver);
        } catch (Exception e) {
           // Utils.Mensaje(e.getMessage(), R.layout.item_customtoasterror, activity);
        }
    }
    @Override
    public void init(int numBza) {
     //   System.out.println("OPTIMA init");
        Estado = M_VERIFICANDO_MODO;
        isRunning = true;
        pesoUnitario = PreferencesDevicesManager.getPesoUnitario(Nombre, this.numBza, activity);//getPesoUnitario();
        pesoBandaCero = PreferencesDevicesManager.getPesoBandaCero(Nombre, this.numBza, activity);
        PuntoDecimal = PreferencesDevicesManager.getPuntoDecimal(Nombre, this.numBza, activity);
        ultimaCalibracion = PreferencesDevicesManager.getUltimaCalibracion(Nombre, this.numBza, activity);
        receiverinit();

        if (this.serialPort != null) {
            Bucle.run();
        }
    }
    @Override
    public void escribir(String msj, int numBza) {
        serialPort.write(msj);
    }
    //    public String Cero(){
//        serialPort.write("KZERO\r\n");
//        setTaraDigital(0);
//        return "KZERO\r\n";
//    }
    private String Param(String str) {
        Integer decimal = Integer.parseInt(str, 2);
        String hexadecimal = Integer.toHexString(decimal).toUpperCase(); // Convertir decimal a hexadecimal
        while (hexadecimal.length() < 2) {
            hexadecimal = "0" + hexadecimal;
        }
        return hexadecimal;
    }

    protected void EnviarParametros(String param1, String param2, String sp_prostr, String sp_offstr, String sp_acustr) { //NUEVO
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

        serialPort.write(Parametros);
    }

    protected void Pedirparam() { // NUEVO
        serialPort.write("\u0005O\r");
    }
    protected void Guardar_cal() {
        serialPort.write("\u0005S\r");
    }

    public void reset() {
        serialPort.write("\u0005T\r");
    }
    public void ReAjusteCero() {
        serialPort.write("\u0005M\r");
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
    public void Cero_cal() {
        serialPort.write("\u0005U\r");
    }
    public void Recero_cal() {
        serialPort.write("\u0005Z\r");
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

    public void abrircalib() {
        serialPort.write("\u0006C \r");
    }

    public void Salir_cal() {
        serialPort.write("\u0005E \r");
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

   

    Runnable Bucle = new Runnable() {
        String[] array;
        int contador = 0;
        String filtro = "\r\n";

        @Override
        public void run() {
            try {
                if (!isRunning) return;
                if (Objects.equals(Estado, M_VERIFICANDO_MODO) && isRunning) {
                    if (contador == 0) {
                       // System.out.println("OPTIMA:BUSCANDO CALIBRACION");
                        abrircalib();
                        mHandler.postDelayed(Bucle, 500);
                    } else {
                      //  System.out.println("OPTIMA:BUSCANDO CALIBRACION");
                        serialPort.write("\u0005C \r");
                        mHandler.postDelayed(Bucle, 500);
                    }
                    //contador++;
                }
                if (serialPort.HabilitadoLectura() && Objects.equals(Estado, M_VERIFICANDO_MODO)) {
                    String read = serialPort.read_2();
                    String filtro = "\r\n";
                    if (read != null) {
                        if (read.contains("\u0006C \r")) {
                            //entro a calibracion
                           // System.out.println("OPTIMA:CALIBRACION");
                            Estado = M_MODO_CALIBRACION;
                            isRunning=false;
                            try {
                                Thread.sleep(500);
                                openCalibracion(numBza);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    }
                    if (read.contains(filtro)) {
                        Estado = M_MODO_BALANZA;
                        isRunning=false;
                        readers.startReading();
                        if (read.contains("E")) {
                            EstableBool = true;
                            SobrecargaBool = false;
                        } else if (read.contains("S")) {

                            EstableBool = false;
                            SobrecargaBool = true;
                        } else {
                            EstableBool = false;
                            SobrecargaBool = false;
                        }
                        array = read.split(filtro);
                    }
                }
            } catch (IOException ex) {
            }
        }

        ;
    };

    @Override
    public void setTara(int numBza) {
        setTaraDigital(numBza,Bruto);
        /*if (serialPort != null) {
            serialPort.write("KTARE\r");
        }*/
    }

    @Override
    public Float getTara(int numBza) {
        return TaraDigital;
    }

    @Override
    public String getTaraStr(int numBza) {
        return TaraDigitalStr;
    }

    @Override
    public void setCero(int numBza) {
        if (serialPort != null) {
            serialPort.write("KZERO\r\n");
        }
        setTaraDigital(numBza, 0);
        Tara = 0;
    }


    @Override
    public void stop(int numBza) {
        isRunning = false;
        try {
            serialPort.close();
        } catch (IOException e) {

        }
        serialPort = null;
        readers.stopReading();
        readers = null;
        Estado = M_VERIFICANDO_MODO;
        mHandler.removeCallbacks(Bucle);
    }


    @Override
    public void openCalibracion(int numero) {
        try {
            readers.stopReading();
            abrircalib();
            Thread.sleep(700);

        } catch (InterruptedException e) {

        } finally {
            CalibracionOptimaFragment fragment = CalibracionOptimaFragment.newInstance(this, Service);
            Bundle args = new Bundle();
            args.putSerializable("instance", this);
            args.putSerializable("instanceService", Service);
            fragmentChangeListener.openFragmentService(fragment, args);
            Estado = M_MODO_CALIBRACION;
        }
    }

    @Override
    public Boolean calibracionHabilitada(int numBza) {
        return true;
    }
    @Override
    public Boolean getEstadoCentroCero(int numBza) {
        return estadoCentroCero;
    }



    @Override
    public Boolean getEstadoNeto(int numBza) {
        return estadoNeto;
    }

    @Override
    public Boolean getEstadoPesoNeg(int numBza) {
        return estadoPesoNeg;
    }

    @Override
    public Boolean getEstadoBajoCero(int numBza) {
        return estadoBajoCero;
    }

    @Override
    public Boolean getEstadoBzaEnCero(int numBza) {
        return estadoBzaEnCero;
    }

    @Override
    public Boolean getEstadoBajaBateria(int numBza) {
        return estadoBajaBat;
    }



    //    public String format(String numero) {
//        String formato = "0.";
//        try {
//            StringBuilder capacidadBuilder = new StringBuilder(formato);
//            for (int i = 0; i <puntoDecimal; i++) {
//                capacidadBuilder.append("0");
//            }
//            formato = capacidadBuilder.toString();
//            DecimalFormat df = new DecimalFormat(formato);
//            String str = df.format(Double.parseDouble(numero));
//            return str;
//        } catch (NumberFormatException e) {
//            System.err.println("Error: El número no es válido.");
//            e.printStackTrace();
//            return "0";
//        }
//    }
//    @Override

    //    ------------------------------------------------------------------------------------



}
