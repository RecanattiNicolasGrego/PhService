package com.service.Devices.Balanzas.Clases;

import android.os.Bundle;
import android.os.HandlerThread;

import androidx.appcompat.app.AppCompatActivity;

import com.service.Comunicacion.GestorPuertoSerie;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.Devices.Balanzas.Clases.Optima.CalibracionOptimaFragment;
import com.service.Interfaz.Balanza;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.PreferencesDevicesManager;
import com.service.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class NuevaBalanza extends BalanzaBase implements Balanza , Serializable {
    PuertosSerie.PuertosSerieListener receiver = null;
    public PuertosSerie serialPort = null;
    public static final int nBalanzas = 1;
    public static Boolean TieneCal = false;
    public static String Nombre = "OPTIMA";
    public static String StopBdef = "1";
    public static String Bauddef = "9600";
    public static String DataBdef = "8";
    public static String Paritydef = "0";
    public static Boolean TienePorDemanda = true;
    public static int timeout = 300;

    public NuevaBalanza(String puerto, int id, AppCompatActivity activity, OnFragmentChangeListener fragmentChangeListener, int idaux) {
        super(puerto, id, activity, fragmentChangeListener, nBalanzas);

    }


    @Override
    public void init(int numBza) {


    };
}