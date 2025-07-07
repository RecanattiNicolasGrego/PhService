package com.service.Devices.Expansiones.Clases;

import androidx.appcompat.app.AppCompatActivity;

import com.service.Comunicacion.PuertosSerie.PuertosSerie;

public class EntradasC extends ExpansionBase{
    public static int     Salidas=0;
    public static int   Entradas=12;
    public static String Bauddef="115200";
    public static String StopBdef="1";
    public static String DataBdef="8";
    public static String Paritydef="0";
    public EntradasC(PuertosSerie Puerto, String id, AppCompatActivity activity) {
        super(Puerto, id, activity);

    }

}
