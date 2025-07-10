package com.service.Devices.Balanzas.Clases.Optima;

import android.os.Bundle;
import android.os.HandlerThread;

import androidx.appcompat.app.AppCompatActivity;

import com.service.Devices.Balanzas.Clases.BalanzaBase;
import com.service.Comunicacion.GestorPuertoSerie;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.Interfaz.Balanza;
import com.service.utilsPackage.PreferencesDevicesManager;
import com.service.utilsPackage.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class OPTIMA_I extends BalanzaBase implements Balanza.Optima_Image ,Serializable {
    PuertosSerie.PuertosSerieListener receiver = null;

    public PuertosSerie serialPort = null;
    private Boolean isRunning = false;
    public static final int nBalanzas = 1;
    public PuertosSerie.SerialPortReader readers = null;
    public static Boolean /*Tieneid=false,*/TieneCal = false;
    public static String Nombre = "OPTIMA";
    public static String StopBdef = "1";
    public static String Bauddef = "9600";
    public static String DataBdef = "8";
    public static String Paritydef = "0";

    public static Boolean TienePorDemanda = true;
    public static int timeout = 300;
    Boolean imgbool = false, inicioBandaPeso = false, estadoNeto = false, estadoPesoNeg = false, estadoBajoCero = false, estadoBzaEnCero = false, estadoBajaBat = false, estadoCentroCero = false;
    public int acumulador = 0;
    String strid = "";

    public OPTIMA_I(String puerto, int id, AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener, int idaux) {
        super(puerto, id, activity, fragmentChangeListener, nBalanzas);
        try {
            System.out.print("INIT OPTIM" + id);
            this.serialPort = GestorPuertoSerie.getInstance().initPuertoSerie(puerto, Integer.parseInt(Bauddef), Integer.parseInt(DataBdef), Integer.parseInt(StopBdef), Integer.parseInt(Paritydef), 0, 0);
            Thread.sleep(300);

        } catch (InterruptedException e) {

        } finally {
            if (id > 0) {
                strid = String.valueOf((char) id);
            } else {
                strid = "";
            }
            this.numBza = (this.serialPort.get_Puerto() * 100) + id; // si no tiene id seria :  10,20,3x  ; SI PUERTO 1 y 2 TIENEN ID ->(puerto.get_Puerto()*100)+numero;  Y CONTROLAR DE ALGUNA FORMA QUE ID NO TENGA 3 CIFRAS
        }
    }

    /* @Override
     public Balanza getBalanza(int numBza) {
         return this;
     }*/
    private void initbucle() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Aquí va tu lógica que se repite (el "bucle")
                synchronized (this) {
                    try {
                        BucleporDemanda.run();  // O lo que necesites hacer
                        // Reprogramar el bucle cada X milisegundos
                    } catch (Exception e) {
                        latch.countDown();
                    }

                }
                mHandler.postDelayed(this, 1); // 1000 ms = 1 segundo
            }
        }, 1);

    }

    Runnable BucleporDemanda = new Runnable() {
        public void run() {
            String data2="";
            Integer auxPuntoDecimal = null;
            String auxBrutoStr = null;
            Float auxBruto =0f;
            char[] ArrByt = null;
            String auxNetoStr="";
            String auxTaraDigitalStr="";
            Float auxNeto=0f;
            Boolean bandestado=false;
            Boolean bandbruto=false;
            Boolean bandtara=false;
            try {
                int value = (int) strid.charAt(0);
                System.out.println("ESPERANDO " + value);
                Semaforo.acquire();
                latch = resetlatch();
                System.out.println("Running " + value);
                // byte[] subArray = serialPort.writendwaitArrbit("\u0002" + strid + "XF\r", 10);
                // ArrByt= Arrays.copyOfRange(subArray, 3, ArrByt.length-2);  // Extrae datos[1] y datos[2]
                byte[] z = serialPort.writendwaitArrbit("\u0002" + strid + "XF\r", 10);
                int c = z[2];
                ArrByt = String.format("%8s", Integer.toBinaryString(c & 0xFF)).replace(' ', '0').toCharArray();
                data2 = serialPort.writendwaitStr("\u0002"+strid+"XG\r",20);
                if(data2!=null && data2.contains(strid)) {
                    try {
                        Unidad = new String(data2.substring(data2.indexOf("\u0003") -4,data2.indexOf("\u0003") -1).trim().replaceAll("\\d+([.,]\\d+)?\\n","").trim().replace("\n",""));
                    } catch (Exception e) {
                    }
                    String[] array = data2.split("\r\n");
                    data2 = limpiardata(array[0]);
                    String data = data2.replace(".", "");
                    if (Utils.isNumeric(data)) {
                        int index = data2.indexOf('.');
                        auxBrutoStr = Utils.removeLeadingZeros(new BigDecimal(data2));
                        auxBruto = Float.parseFloat(data2);
                        bandbruto=true;
                        auxPuntoDecimal = data.length() - index;
                        if (TaraDigital == 0) {
                            auxNeto = auxBruto - Tara;
                            auxNetoStr = String.valueOf(auxNeto);
                            if (index == -1) {
                                auxNetoStr = auxNetoStr.replace(".0", "");
                            }
                        } else {
                            auxNeto = auxBruto - TaraDigital;
                            auxNetoStr = String.valueOf(auxNeto);
                            if (index == -1) {
                                auxNetoStr = auxNetoStr.replace(".0", "");
                            }
                        }
                        if (index != -1 && auxPuntoDecimal > 0) {
                            String formato = "0.";
                            StringBuilder capacidadBuilder = new StringBuilder(formato);
                            for (int i = 0; i < auxPuntoDecimal; i++) {
                                capacidadBuilder.append("0");
                            }
                            formato = capacidadBuilder.toString();
                            DecimalFormat df = new DecimalFormat(formato);
                            auxNetoStr = df.format(auxNeto);
                            auxTaraDigitalStr = df.format(TaraDigital);
                        }

                    }

                    //}
               /* data2 =  serialPort.writendwaitStr("\u0002"+strid+"XT\r",20);
                if(data2!=null && data2.contains(strid)) {
                    int index = data2.indexOf('.');
                    String[] array = data2.split("\r\n");
                    data2 = limpiardata(array[0]);
                    String data = data2.replace(".", "");*/
                    // if (Utils.isNumeric(data)) {

                    //}

                }
                if(!Objects.equals(auxBrutoStr, "") && !auxTaraDigitalStr.isEmpty() && !Objects.equals(auxNetoStr, "")) {
                    TaraDigitalStr = auxTaraDigitalStr;
                    Neto = auxNeto;
                    NetoStr = auxNetoStr;
                    Bruto = auxBruto;
                    BrutoStr = auxBrutoStr;
                    PuntoDecimal = auxPuntoDecimal;
                }
                    int x = 0;
                    if(ArrByt.length>1) {
                        for (char bit : ArrByt) {
                            if (bit =='1') {
                                switch (x) {
                                    case 6:
                                        SobrecargaBool = true;
                                        break;
                                    case 0:
                                        EstableBool = true;
                                        break;
                                }
                            } else {
                                switch (x) {
                                    case 6:
                                        SobrecargaBool = false;
                                        break;
                                    case 0:
                                        EstableBool = false;
                                        break;
                                }
                            }
                            x++;
                        }
                    }
                    Thread.sleep(50);
                    latch.countDown();
            } catch (InterruptedException e) {
                System.out.println("error optima"+e.getMessage());
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {

                }
                latch.countDown();
            }
        }
    };

    private String limpiardata(String data){
        String data2 = data;
        if(data!=null&&data.length()>2) {
            data2 = data2.replace(strid, "");
            data2 = data2.replace(String.valueOf(strid), "");
            data2 = data2.replace("\u0002", "");
            data2 = data2.replace("\u0003", "");
            data2 = data2.replace(" ", "");
            data2 = data2.replace("\r\n", "");
            data2 = data2.replace("\r", "");
            data2 = data2.replace("\n", "");
            data2 = data2.replace("\u0007", "");
            try {
                data2 = data2.trim().substring(0, data2.length() - 2);
            } catch (Exception e) {
                System.out.println("ERRER CLEARDATA" + e.getMessage());
            }
        }
        //  data2 = data2.replace("O", "");
        return data2;
    }


    private void receiverinit() {
        String filtro = "\r\n";
        receiver = new PuertosSerie.PuertosSerieListener() {
            @Override
            public void onMsjPort(String data) {
                System.out.println("OPTIMA DATA: " + data);
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
                            data2 = limpiardata(arrPeso[0]);
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
                            } else if (data2.contains("S")) { // && !data2.matches("kg") lo saque por que en minima no funciona, pero por ahi es parte de optima
                                EstableBool = false;
                                SobrecargaBool = true;
                            } else {
                                if (true) { //!data2.matches("kg") lo saque por que en minima no funciona, pero por ahi es parte de optima
                                    EstableBool = false;
                                    SobrecargaBool = false;
                                }
                            }
                            data2 = limpiardata(arrPeso[0]);
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
                        } else if (data2.contains("S")) { // && !data2.matches("kg") lo saque por que en minima no funciona, pero por ahi es parte de optima
                            EstableBool = false;
                            SobrecargaBool = true;
                        } else {
                            if (!data2.contains("kg")) { //!data2.matches("kg") lo saque por que en minima no funciona, pero por ahi es parte de optima
                                EstableBool = false;
                                SobrecargaBool = false;
                            }
                        }

                        String auxdat= data2;
                        try {
                            Unidad = auxdat.substring(data2.length() -2).trim().replaceAll("\\d+([.,]\\d+)?\\n","");
                        } catch (Exception e) {

                        }

                        data2 = limpiardata(data2);
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
                                Neto = Bruto;
                                NetoStr = df.format(Neto);
                                TaraDigitalStr = df.format(TaraDigital);
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

    private HandlerThread handlerThread;

    @Override
    public void init(int numBza) {
        mHandler.post(() -> {
            Estado = M_VERIFICANDO_MODO;
            isRunning = true;

            pesoUnitario = PreferencesDevicesManager.getPesoUnitario(Nombre, numBza, activity);
            pesoBandaCero = PreferencesDevicesManager.getPesoBandaCero(Nombre, numBza, activity);
            PuntoDecimal = PreferencesDevicesManager.getPuntoDecimal(Nombre, numBza, activity);
            ultimaCalibracion = PreferencesDevicesManager.getUltimaCalibracion(Nombre, numBza, activity);

            if (!band485) {
                receiverinit();
            }
            if (serialPort != null) {
                mHandler.post(Bucle);

                //iniciarBucle();
            }
        });

    }

    private void iniciarBucle() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Bucle.run();  // Tu lógica en segundo plano
                // Si quieres que se repita periódicamente:
                mHandler.postDelayed(this, 1); // cada 1 segundo
            }
        });
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
            String prefix = !Objects.equals(strid, "") ? "\u0002" + strid : "\u0006";
            String read = null;
            if (!isRunning) return;
            if (!Objects.equals(strid, "")) filtro = "\u0001";
            if (Objects.equals(Estado, M_VERIFICANDO_MODO)) {
                if (contador == 0) {
                    read = serialPort.writendwaitStr("\u0006C\r",0);
                    System.out.println("OPTIMA:BUSCANDO CALIBRACION");
                    if (Objects.equals(strid, "")) {
                    } else {
                        read = serialPort.writendwaitStr(prefix + "XF\r",10);
                    }
                } else {
                    if (Objects.equals(strid, "")) {
                        read = serialPort.writendwaitStr("\u0005"+"C\r",0);
                    } else {
                        read = serialPort.writendwaitStr(prefix + "XF\r",10);
                    }
                }
                contador++;
            }
            if (read != null) {
                String filtro = "\r\n";
                if (!Objects.equals(strid, "")) filtro = "\u0001";
                if (read.contains("\u0006C \r")) {
                    //entro a calibracion
                    // System.out.println("OPTIMA:CALIBRACION");
                    Estado = M_MODO_CALIBRACION;
                    isRunning = false;
                    try {
                        Thread.sleep(500);
                        openCalibracion(numBza);
                    } catch (InterruptedException e) {
                    }
                }

                if (read.toLowerCase().contains(filtro.toLowerCase())) {
                    Estado = M_MODO_BALANZA;
                    isRunning = false;
                    if (band485) {
                        initbucle();
                        read = limpiardata(read);
                        //String original = new String(read.toString().getBytes(), StandardCharsets.UTF_8);  // o ISO_8859_1, depende del origen
                        Integer[] ArrByt = Utils.charToBitArray(read);
                        int x = 0;
                        for (Integer bit : ArrByt) {
                            if (bit == 1) {
                                switch (x) {
                                    case 1:
                                        SobrecargaBool = true;
                                    case 7:
                                        EstableBool = true;
                                }
                            } else {
                                switch (x) {
                                    case 1:
                                        SobrecargaBool = false;
                                    case 7:
                                        EstableBool = false;
                                }
                            }
                            x++;
                        }
                    } else {
                        readers.startReading();
                        if (read.toLowerCase().contains("E".toLowerCase())) {
                            EstableBool = true;
                            SobrecargaBool = false;
                        } else if (read.toLowerCase().contains("S".toLowerCase())) {

                            EstableBool = false;
                            SobrecargaBool = true;
                        } else {
                            EstableBool = false;
                            SobrecargaBool = false;
                        }
                        array = read.split(filtro);
                    }
                }

            }
            ;
            contador++;

            if(Objects.equals(Estado, M_VERIFICANDO_MODO) && contador<=10) {
                mHandler.postDelayed(Bucle, 500);
            }
        }
    };

    @Override
    public void setTara(int numBza) {
        setTaraDigital(numBza, Bruto);
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
        try {
            readers.stopReading();
        } catch (Exception e) {

        }
        readers = null;
        Estado = M_VERIFICANDO_MODO;
        mHandler.removeCallbacks(BucleporDemanda);
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

