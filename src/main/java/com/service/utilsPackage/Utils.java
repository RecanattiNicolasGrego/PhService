package com.service.utilsPackage;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.service.BalanzaService;
import com.service.Comunicacion.Modbus.Req.BasicProcessImageSlave;
import com.service.Interfaz.dispositivoBase;
import com.service.R;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.apache.http.conn.util.InetAddressUtils;


public  class Utils {
    private static Toast toast;
    public static String DevuelveHora(){
        String Fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        String hora24=String.valueOf(hour24hrs);
        String minutos=String.valueOf(minutes);
        String segundos=String.valueOf(seconds);
        String Hora= hora24 +":"+minutos+":"+segundos;
        return Hora;
    }
    public static String removeLeadingZeros(BigDecimal number) {
        String formatted = number.toPlainString();
        if (formatted.contains(".")) {
            // Elimina los ceros innecesarios al principio antes del punto decimal
            formatted = formatted.replaceFirst("^0+(?!\\.)", "");
            // Si todos los dígitos antes del punto decimal eran ceros, agrega uno
            if (formatted.matches("^\\..*")) {
                formatted = "0" + formatted;
            }
        }
        return formatted;
    }
    static public void dialogoDosOpciones(FragmentActivity activity, String titulo,String textoPositivo, Runnable accionPositiva, String textoNegativo, Runnable accionNegativa) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.dialogo_dosopciones_i, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        userInput.setVisibility(View.GONE);
        final LinearLayout delete_text = mView.findViewById(R.id.lndelete_text);
        delete_text.setVisibility(View.GONE);

        TextView title = mView.findViewById(R.id.textViewt);
        title.setText(titulo);
        Button Guardar = mView.findViewById(R.id.buttons);
        Button Cancelar = mView.findViewById(R.id.buttonc);
        Guardar.setText(textoPositivo);
        Cancelar.setText(textoNegativo);


        mBuilder.setView(mView);
        Dialog dialog1 = mBuilder.create();
        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accionPositiva.run();
                dialog1.cancel();
            }
        });
        Cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accionNegativa.run();
                dialog1.cancel();
            }
        });
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.show();
        configureDialogSize(dialog1, dialog1.getContext());
        //showKeyboard(userInput, activity);
    }
    public static void dialogTestingModbus(TextView textView, String string, String Texto, FragmentActivity activity) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.testingmodbus, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        final LinearLayout delete_text = mView.findViewById(R.id.lndelete_text);
        final Spinner spinner = mView.findViewById(R.id.spinner);
        ArrayList<String> items = new ArrayList<>();

        items.addAll(PreferencesDevicesManager.obtenerAliasDeModelos(dispositivoBase.Modbus.ClasesModbus.values()));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity.getApplicationContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        userInput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        TextView titulo = mView.findViewById(R.id.textViewt);
        delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInput.setText("");
            }
        });
        titulo.setText(Texto);


        if (!textView.getText().toString().equals("")) {
            userInput.setText(textView.getText().toString());
            userInput.setSelection(userInput.getText().length());
        }
        Button Guardar = mView.findViewById(R.id.buttons);
        Button Cancelar = mView.findViewById(R.id.buttonc);
        mBuilder.setView(mView);
        Dialog dialog1 = mBuilder.create();
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog1.show();
        configureDialogSize(dialog1, dialog1.getContext());
        showKeyboard(userInput, activity);
        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dispositivoBase x =BalanzaService.getInstance().Dispositivos.getDispositivo(1);
                if(BalanzaService.ModelosClasesDispositivos.Master.compararInstancia(1)) {
                    dispositivoBase.Modbus.Master mSlave = (dispositivoBase.Modbus.Master) x;
                    boolean[] lista = new boolean[33];
                    for (int j = 1; j < 31; j++) lista[j]=true;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mSlave.init();
                            mSlave.WriteMultiplesCoils(1,lista);

                        }
                    }).start();
                }

                    /* x =BalanzaService.getInstance().Dispositivos.getDispositivo(2);
                    if(BalanzaService.ModelosClasesDispositivos.Slave.compararInstancia(2)) {
                        dispositivoBase.Modbus.Slave mSlave = (dispositivoBase.Modbus.Slave) x;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<Integer> x=  new ArrayList<Integer>();
                                for (int i = 1; i < 30 ; i++) {
                                    x.add(i);
                                }
                                BasicProcessImageSlave z = new BasicProcessImageSlave.BPIBuilder(mSlave.getImageBasic())
                                        .InitDefaultCoils(x)
                                        .InitDefaultHoldingRegisters(x)
                                        .build();
                                mSlave.init(new dispositivoBase.Modbus.Slave.DeviceMessageListenerM_Slave() {
                                    @Override
                                    public void CoilChange(int Num, int nRegistro, boolean oldVal, boolean newVal) {
                                        System.out.println("COIL CHANGE "+Num+" "+nRegistro+" "+oldVal+" "+newVal);
                                    }

                                    @Override
                                    public void RegisterChange(int Num, int nRegistro, Short oldVal, Short newVal) {
                                        System.out.println("registerChange "+Num+" "+nRegistro+" "+oldVal+" "+newVal);
                                    }
                                },z);
                            }
                        }).start();
                    }*/
                    //textView.setText(userInput.getText().toString());
                    dialog1.cancel();
            }
        });
        Cancelar.setOnClickListener(view -> dialog1.cancel());

    }


    public static String DevuelveFecha(){
        String Fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        String hora24=String.valueOf(hour24hrs);
        String minutos=String.valueOf(minutes);
        String segundos=String.valueOf(seconds);
        String Hora= hora24 +":"+minutos+":"+segundos;
        return Fecha;
    }
    public static boolean isNumeric(String strNum) {
        try {
            if (strNum == null) {
                return false;
            }
            try {
                double d = Double.parseDouble(strNum);
            } catch (NumberFormatException nfe) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public static int[] numberToBitArray2(int number) {
        int[] bitArray = new int[16];
        for (int i = 0; i < 16; i++) {
            bitArray[i] = (number >> i) & 1;
        }
        return bitArray;
    }
    public static String bitListToString(ArrayList<Integer> Estados) {
       // System.out.println("QUE PASO ACA!?!?!?"+Estados.size());
        if (Estados.size() % 16 != 0) {
            throw new IllegalArgumentException("El ArrayList debe tener una longitud múltiplo de 16");
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < Estados.size(); i += 16) {
            int value = 0;

            // Convertimos los 16 bits a un valor hexadecimal
            for (int j = 0; j < 16; j++) {
                value |= (Estados.get(i + j) << j);
            }

            // Convertimos el valor a un par de caracteres hexadecimales
            sb.append(String.format("%02X", value));
        }
        return sb.toString();
    }
    public static Integer[] charToBitArray(String c) {
        try {
            Integer[] bitArray = new Integer[8];
            Character x = Character.valueOf(c.charAt(0));
            int value = (int) x; // convierte el char a su valor ASCII/Unicode

            for (int i = 7; i >= 0; i--) {
                bitArray[i] = (value >> (7 - i)) & 1;  // Shift de 7-i para colocar los bits de izquierda a derecha
            }


            return bitArray;
        } catch (Exception e) {
            System.out.println("YA NO ENTIENDO NA");
            Integer[] aux = new Integer[8];
            for (int i = 7; i >= 0; i--) {
                if(i==1){
                    aux[i] = 1;
                }else{
                    aux[i] = 0;
                }
                  // Shift de 7-i para colocar los bits de izquierda a derecha
            }
            return aux;
        }
    }


    public static Integer[] hexsize2ToBitArray(String str) {
        if (str.length() % 2 != 0) {
        //    System.out.println("AAAAAAAAAAAAAAAA");
        }

        Integer[] bitArray = new Integer[(str.length() / 2) * 16];
        for (int i = 0; i < str.length(); i += 2) {
            int value = Integer.parseInt(str.substring(i, i + 2), 16);
            for (int j = 15; j >= 0; j--) {
                bitArray[(i / 2) * 16 + j] = (value >> j) & 1;
            }
        }
        return bitArray;
    }


    /*  public static Integer[] stringToBitArray(String str) {
        // Asegurar que la longitud sea par
        if (str.length() % 2 != 0) {
            throw new IllegalArgumentException("La cadena debe tener una cantidad par de caracteres");
        }

        Integer[] bitArray = new Integer[(str.length() / 2) * 12]; // 12 bits por cada 2 caracteres

        for (int i = 0; i < str.length(); i += 2) {
            // Tomamos 2 caracteres y los convertimos a un valor hexadecimal
            int value = Integer.parseInt(str.substring(i, i + 2), 16);

            // Convertimos el valor a 12 bits
            for (int j = 0; j < 12; j++) {
                bitArray[(i / 2) * 12 + j] = (value >> (11 - j)) & 1;
            }
        }

        return bitArray;
    }
*/
    public static int toggleBit(int number, int bitIndex, boolean toggleIfSet) {
        if (((number & (1 << bitIndex)) == 0) == toggleIfSet) {
            return number ^ (1 << bitIndex);
        } else {
            return number;
        }
    }
    public static void configureDialogSize(Dialog dialog, Context context) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

            // Establecer el ancho y la altura al tamaño total de la pantalla
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.MATCH_PARENT;

            // Ajustar la posición para que esté centrado
            params.x = 0; // Posición horizontal
            params.y = 0; // Posición vertical
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Fondo transparente
            window.setGravity(Gravity.CENTER);
            window.setAttributes(params);

        }
    }

    public static void deleteCache(Context context) {
        try {
            context.getCacheDir().deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void clearCache(Context context){
                    SharedPreferences preferences = context.getSharedPreferences("devicesService", Context.MODE_PRIVATE);
                    preferences.edit().clear().apply();
                    // Elimina la caché de la aplicación
                    deleteCache(context);
    }

    public static boolean isLong(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            long l = Long.parseLong(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Convert byte array to hex string
     * @param bytes toConvert
     * @return hexValue
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for(int idx=0; idx < bytes.length; idx++) {
            int intVal = bytes[idx] & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Get utf8 byte array.
     * @param str which to be converted
     * @return  array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try { return str.getBytes(StandardCharsets.UTF_8); } catch (Exception ex) { return null; }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     * @param filename which to be converted to string
     * @return String value of File
     * @throws IOException if error occurs
     */
    public static String loadFileAsString(String filename) throws IOException {
        final int BUFLEN=1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8=false;
            int read,count=0;
            while((read=is.read(bytes)) != -1) {
                if (count==0 && bytes[0]==(byte)0xEF && bytes[1]==(byte)0xBB && bytes[2]==(byte)0xBF ) {
                    isUTF8=true;
                    baos.write(bytes, 3, read-3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count+=read;
            }
            return isUTF8 ? baos.toString(String.valueOf(StandardCharsets.UTF_8)) : baos.toString();
        } finally {
            try{ is.close(); } catch(Exception ignored){}
        }
    }

    public static String jwsGetEthWifiAddress() {
        try {
            return loadFileAsString("/sys/class/net/wlan0/address").toUpperCase().substring(0, 17);
        } catch (IOException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) buf.append(String.format("%02X:",aMac));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";

    }
    private static final String IPV4_PATTERN =
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    private static boolean isValidIPv4(String ip) {
        Pattern pattern = Pattern.compile(IPV4_PATTERN);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
    public static void dialogText(TextView textView, String string, String Texto, FragmentActivity activity) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.dialogo_dosopciones_i, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        final LinearLayout delete_text = mView.findViewById(R.id.lndelete_text);
        userInput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        TextView titulo = mView.findViewById(R.id.textViewt);
        delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInput.setText("");
            }
        });
        titulo.setText(Texto);

        if (!textView.getText().toString().equals("")) {
            userInput.setText(textView.getText().toString());

            userInput.setSelection(userInput.getText().length());
        }
        Button Guardar = mView.findViewById(R.id.buttons);
        Button Cancelar = mView.findViewById(R.id.buttonc);
        mBuilder.setView(mView);
        Dialog dialog1 = mBuilder.create();
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog1.show();
        configureDialogSize(dialog1, dialog1.getContext());
        showKeyboard(userInput, activity);
        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(userInput.getText().toString());
                dialog1.cancel();
            }
        });
        Cancelar.setOnClickListener(view -> dialog1.cancel());

    }

    public static void dialogTextNumber(TextView textView, String string, String Texto, FragmentActivity activity) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
        View mView = activity.getLayoutInflater().inflate(R.layout.dialogo_dosopciones_i, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        final LinearLayout delete_text = mView.findViewById(R.id.lndelete_text);
        userInput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        TextView titulo = mView.findViewById(R.id.textViewt);
        delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInput.setText("");
            }
        });
        userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
        userInput.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(10) });

        titulo.setText(Texto);
        if (!textView.getText().toString().equals("")) {
            userInput.setText(textView.getText().toString());

            userInput.setSelection(userInput.getText().length());
        }
        Button Guardar = mView.findViewById(R.id.buttons);
        Button Cancelar = mView.findViewById(R.id.buttonc);
        mBuilder.setView(mView);
        Dialog dialog1 = mBuilder.create();
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog1.show();
        configureDialogSize(dialog1, dialog1.getContext());
        showKeyboard(userInput,activity);
        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(userInput.getText().toString());
                dialog1.cancel();
            }
        });
        Cancelar.setOnClickListener(view -> dialog1.cancel());

    }
    public static void showKeyboard(EditText editText, FragmentActivity activity) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void dialogIp(TextView textView, String string, String Texto, FragmentActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        View view = View.inflate(activity.getApplicationContext(), R.layout.dialogo_dosopciones_i, null);
        final EditText userInput = view.findViewById(R.id.etDatos);
        final LinearLayout delete_text = view.findViewById(R.id.lndelete_text);
        TextView titulo = view.findViewById(R.id.textViewt);
        Button btnCancel = view.findViewById(R.id.buttonc);
        Button Guardar = view.findViewById(R.id.buttons);
        userInput.setText(textView.getText().toString());

        userInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        userInput.setKeyListener(DigitsKeyListener.getInstance(".0123456789"));

// Permite múltiples puntos y números
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        btnCancel.setOnClickListener(v -> dialog.cancel());
        delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInput.setText("");
            }
        });
        dialog.show();
        configureDialogSize(dialog, dialog.getContext());
        showKeyboard(userInput,activity);
        titulo.setText(Texto);
        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidIPv4(userInput.getText().toString())) {
                    if(!Utils.getIPAddress(true).equals(userInput.getText().toString())) {
                        textView.setText(userInput.getText().toString());
                        dialog.cancel();
                    }else{
                        Mensaje("No puede ser la ip actual",R.layout.item_customtoasterror,(AppCompatActivity) activity);
                    }
                } else {
                    Mensaje("No tiene formato IP",R.layout.item_customtoasterror,(AppCompatActivity) activity);
                }
            }
        });

    }


    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }

    public static void Mensaje(String texto, int Color, AppCompatActivity appCompatActivity) {
        appCompatActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float scale = appCompatActivity.getResources().getDisplayMetrics().scaledDensity;
                float size = 30 * (scale * 2);
                String txtanterior = "";

                if (toast != null && toast.getView().isShown()) {
                    txtanterior = ((TextView) toast.getView().findViewById(R.id.text)).getText().toString() + "\n" + "\n";
                    //System.out.println("WOLOLO"+toast.getView().isShown());
                    toast.cancel();
                    toast = null;
                    size = size - (10 * (scale * 2));
                }
                LayoutInflater inflater = appCompatActivity.getLayoutInflater();
                View layout = inflater.inflate(Color, appCompatActivity.findViewById(R.id.toast_layout_root));
                TextView text = layout.findViewById(R.id.text);
                text.setText(txtanterior + texto);
                text.setTextSize(size);

                toast = new Toast(appCompatActivity.getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

            }

        });
    }
    public  static void ocultarTeclado(EditText editText,AppCompatActivity actividad) {
        if (editText != null && actividad != null) {
            InputMethodManager imm = (InputMethodManager) actividad.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }
    }

    /*public static String getHora(){
        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        String hora24=String.valueOf(hour24hrs);
        String minutos=String.valueOf(minutes);
        String segundos=String.valueOf(seconds);
        return hora24 +":"+minutos+":"+segundos;
    }

    public static String getFecha(){
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }*/
    public static  String format(int PuntoDecimal, String peso) {
        String formato = "0.";
        try {
            StringBuilder capacidadBuilder = new StringBuilder(formato);
            for (int i = 0; i < PuntoDecimal; i++) {
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
    public static String pointDecimalFormat(String numero, int decimales) {
        try {
            Double.parseDouble(numero);
        } catch (NumberFormatException e) {
            return "0000";
        }

        String formato = "0";
        if (decimales > 0) {
            formato += ".";
            for (int i = 0; i < decimales; i++) {
                formato += "0";
            }
        }

        DecimalFormat decimalFormat = new DecimalFormat(formato);
        return decimalFormat.format(Double.parseDouble(numero));
    }
    public static int indexOf(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
    public static void EsHiloSecundario() {
        if (Looper.getMainLooper().isCurrentThread()) {
            throw new RuntimeException("Esta función no debe ejecutarse en el hilo principal");
        }
        // Código pesado aquí
    }

}