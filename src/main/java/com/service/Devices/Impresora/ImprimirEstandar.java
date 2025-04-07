package com.service.Devices.Impresora;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;


import com.service.Devices.Impresora.Tipos.ImprimirBluetooth;
import com.service.Devices.Impresora.Tipos.ImprimirRS232;
import com.service.Devices.Impresora.Tipos.ImprimirRed;
import com.service.Devices.Impresora.Tipos.ImprimirUSB;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.estructuras.classDevice;
import com.service.R;
import com.service.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ImprimirEstandar {
    private final Context context;
    private AppCompatActivity activity;
    private  String etiqueta;
    private  int num;
    PuertosSerie serialPort;
    public static final  String BaudZebradef="9600";
    public static final String StopBZebradef="1";
    public static final String DataBZebradef="8";
    public static final String ParityZebradef="0";
    public Boolean tieneid=true;
    int modo=0;
    classDevice impresora;
    private List<String> imprimiblesPredefinidas = new ArrayList<>();
    public ImprimirEstandar(Context context, AppCompatActivity activity, String etiqueta, Integer num, PuertosSerie port, int modo, classDevice impresora) {
        this.context = context;
        this.activity = activity;
        this.etiqueta = etiqueta;
        this.num = num;
        this.serialPort = port;
        this.modo = modo;
        this.impresora = impresora;

    }

    //bindService connection
    public void imprimirRS232(){
        if(serialPort!=null){

            //  System.out.println("p1");
            ImprimirRS232 imprimirRS232= new ImprimirRS232(serialPort);imprimirRS232.Imprimir(etiqueta);
        }else {
        //    Utils.Mensaje("Error para imprimir por puerto serie A", R.layout.item_customtoasterror, activity);
        }
    }
    public void EnviarEtiqueta(){

      //  System.out.println("etiquetamagic"+etiqueta+impresora.getSalida());
//        int modo=consultaModo(num);
        switch (modo){
            case 1:
            case 2:
            case 3: {
                imprimirRS232();
            }
            case 4: { // USB

                System.out.println("USB");
                ImprimirUSB imprimirUSB= new ImprimirUSB(activity);
                imprimirUSB.print(etiqueta, activity,false,null);
                break;
            }
            case 5:{ // RED
                //verificamos si el dato guardado es una ip
                String patronIP = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                        + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                        + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                        + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
                Pattern pattern = Pattern.compile(patronIP);
                String ip = impresora.getDireccion().get(0);
                System.out.println("IP?!? "+ip);
                Matcher matcher = pattern.matcher(ip); // consultaIP(num)
                if (matcher.matches()) {
                    ImprimirRed imprimirRed= new ImprimirRed();
                    imprimirRed.Imprimir(impresora.getDireccion().get(0),etiqueta);
                }else {
                   // Utils.Mensaje("Error para imprimir, IP no valida", R.layout.item_customtoasterror, activity);
                }
                break;
            }
            case 6:{ //BT
                System.out.println("BT");
                ImprimirBluetooth imprimirBluetooth= new ImprimirBluetooth(context, activity,impresora.getDireccion().get(0));
                imprimirBluetooth.Imprimir(etiqueta);
                break;
            }
            default:{
             //   Utils.Mensaje("OPCION DESHABILITADA O NO CONFIGURADA",R.layout.item_customtoasterror, activity);
            }
        }


    }

}
