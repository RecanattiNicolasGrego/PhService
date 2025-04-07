package com.service.Devices.Balanzas.Clases.Minima;

import static com.service.Utils.Mensaje;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.service.BalanzaService;
import com.service.Devices.Balanzas.Clases.BalanzaBase;
import com.service.ComService;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.PreferencesDevicesManager;
import com.service.R;
import com.service.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class CalibracionMinimaFragment extends Fragment {

    AppCompatActivity activity;
    private ButtonProvider buttonProvider;
    PuertosSerie.SerialPortReader reader=null;
    Handler mHandler= new Handler();
    Toast toast=null;
   // public static final String M_VERIFICANDO_MODO="VERIFICANDO_MODO",M_MODO_CALIBRACION="MODO_CALIBRACION",M_ERROR_COMUNICACION="M_ERROR_COMUNICACION"
    public String estado= BalanzaBase.M_VERIFICANDO_MODO,picoStr="0",estable="";//,ultimaCalibracion="",brutoStr="0",netoStr="0",taraStr="0", taraDigitalStr="0";
    public float bruto =0, tara =0, neto =0;//taraDigital=0,pico=0,pesoUnitario=0.5F,pesoBandaCero=0F,
    public Boolean stoped=false,isCollapsed = false,lastTanque = true,boolBtSeteo =true,boolBtHome =true,boolBtReset =true,boolBtCalibracion =true,boolEnviarParam =true,boolBtIniciarCalibracion =true,boolBtReajusteCero =true;/*,bandaCero =true,inicioBandaPeso=false*/;
    public int initialWidth = 258,indiceCalibracion=1;//,puntoDecimal=1,acumulador=0;
    MINIMA_I BZA;
    ImageView ImButton, ImSeteo, ImCal;
    LinearLayout lnToggleDiv, LnSeteo, LCalibracion;
    Button btIniciarCalibracion, btReAjusteCero, btReset,btCalibracion,btSeteo, btGuardar,bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    AlertDialog dialog, dialog1;
    TextView tvPesoConocido, tvCapacidad, tvUltimaCalibracion,tvCarga, tvTitulo;
    ProgressBar loadingPanel;
    Spinner spDivisionMinima, spPuntoDecimal,spUnidad,spAcu,spPro,spOff;//spBot,spReg,spUni,spAno,spDat,spBf1,spBps,
    RadioGroup toggle1,toggle2,toggle3,toggle4,toggle5,toggle6,toggle7,toggle8,toggle13;
    RadioButton OFF1,OFF2,OFF3,OFF4,OFF5,OFF6,OFF7,OFF8,OFF13, ON1,ON2,ON3,ON4,ON5,ON6,ON7,ON8,ON13;
    ConstraintLayout tableParametrosPrincipales;
    BalanzaService Service;

//    private OnFragmentChangeListener fragmentChangeListener;

    public static CalibracionMinimaFragment newInstance(MINIMA_I instance, BalanzaService bza) {
        CalibracionMinimaFragment fragment = new CalibracionMinimaFragment();
        Bundle args = new Bundle();
        args.putSerializable("instance", instance);
        args.putSerializable("instanceService", bza);
        fragment.setArguments(args);
        return fragment;
    }
//    public void setFragmentChangeListener(OnFragmentChangeListener listener) {
//        this.fragmentChangeListener = listener;
//    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_calibracion_v2,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        if (getArguments() != null) {
            BZA = (MINIMA_I) getArguments().getSerializable("instance");
            Service =  BalanzaService.getInstance();//(BalanzaService) getArguments().getSerializable("instanceService");
            activity = ComService.getInstance().activity;//BZA.activity;//BZA.activity;
           BZA.abrirCalib();
        }
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        initializeViews(view);
        configuracionBotones();
        setClickListeners();
        initSetters(view);

    }
    private void initSetters(View view){
        String[] Lista0aF = new String[16];
        String[] Lista0a9 = new String[10];
        for (int i = 0; i < 16; i++) {
            Lista0aF[i] = Integer.toHexString(i).toUpperCase();
            if(i<=9){
                Lista0a9[i]=Integer.toHexString(i).toUpperCase();
            }
        }
        ArrayAdapter<String> adapter11 = new ArrayAdapter<>(activity.getApplicationContext(),R.layout.item_spinner,Lista0aF);
        adapter11.setDropDownViewResource(R.layout.item_spinner);
        spAcu.setAdapter(adapter11);
        ArrayAdapter<String> adapter13= new ArrayAdapter<>(activity.getApplicationContext(),R.layout.item_spinner,Lista0a9);
        adapter13.setDropDownViewResource(R.layout.item_spinner);
        spOff.setAdapter(adapter13);
        toggle4.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton bton= view.findViewById(R.id.btON4);
            RadioButton btoff = view.findViewById(R.id.btOFF4);
            if(toggle4.getCheckedRadioButtonId()==R.id.btON4){
                btoff.setText("NO");
                bton.setText("SI");
                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
            } else {
                btoff.setText("NO");
                bton.setText("SI");

                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));

            }
        });
        toggle2.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton bton= view.findViewById(R.id.btON2);
            RadioButton btoff = view.findViewById(R.id.btOFF2);
            if(toggle2.getCheckedRadioButtonId()==R.id.btON2){

                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                btoff.setText("NO");
                bton.setText("SI");
            } else {
                btoff.setText("NO");
                bton.setText("SI");
                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
            }

        });
        toggle13.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton bton= view.findViewById(R.id.btON13);
            RadioButton btoff = view.findViewById(R.id.btOFF13);
            if (toggle13.getCheckedRadioButtonId() == R.id.btON13) {
                btoff.setText("NO");
                bton.setText("SI");

                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
            } else {
                btoff.setText("NO");
                bton.setText("SI");

                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
            }
        });
        toggle8.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton bton= view.findViewById(R.id.btON8);
            RadioButton btoff = view.findViewById(R.id.btOFF8);
            if (toggle8.getCheckedRadioButtonId() == R.id.btON8) {
                btoff.setText("NO");
                bton.setText("SI");

                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
            } else {
                btoff.setText("NO");
                bton.setText("SI");

                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
            }
        });

        ArrayAdapter<String> adapter12= new ArrayAdapter<>(activity.getApplicationContext(),R.layout.item_spinner,Lista0aF);
        adapter12.setDropDownViewResource(R.layout.item_spinner);
        spPro.setAdapter(adapter12);
         }

    private String leertoggles(RadioGroup toggle,Integer id){ //NUEVO
        String param1 ="";
        if(toggle.getCheckedRadioButtonId()==id){
            return "1";

        }else{

            return "0";

        }
    }
    private void collapseLinearLayout(final ConstraintLayout linearLayout) {
        ValueAnimator animator = ValueAnimator.ofInt(initialWidth, 0);
        animator.setDuration(300); // Duración de la animación en milisegundos
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
                layoutParams.width = (int) valueAnimator.getAnimatedValue();

                linearLayout.setLayoutParams(layoutParams);
                isCollapsed=true;

            }
        });
        animator.start();
    }

    private void expandLinearLayout(final ConstraintLayout linearLayout) {
        ValueAnimator animator = ValueAnimator.ofInt(0, initialWidth);
        animator.setDuration(300); // Duración de la animación en milisegundos
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
                layoutParams.width = (int) valueAnimator.getAnimatedValue();
                linearLayout.setLayoutParams(layoutParams);
                isCollapsed=false;
            }
        });
        animator.start();
    }

    private void configuracionBotones() {
        if (buttonProvider != null) {

            bt_home = buttonProvider.getButtonHome();
            bt_1 = buttonProvider.getButton1();
            bt_2 = buttonProvider.getButton2();
            bt_3 = buttonProvider.getButton3();
            bt_4 = buttonProvider.getButton4();
            bt_5 = buttonProvider.getButton5();
            bt_6 = buttonProvider.getButton6();
            LinearLayout ln_Menu = buttonProvider.getMenu();
            ln_Menu.setClickable(false);
            bt_1.setVisibility(View.INVISIBLE);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);
        }
        //--

    }
    public void procesarMensaje(String Mensaje) {
        if(Mensaje.contains("\u0006D")){

        }
        if(Mensaje.contains("\u0006T")){
            boolBtReset =true;

        }
        if(Mensaje.contains("\u0006P")){
          BZA.Guardar_cal();
        }
        if(Mensaje.contains("\u0006M ")){
            guardar(8, null, null);
            activity.runOnUiThread(() -> {
                boolBtReajusteCero =true;
                dialog.cancel();
            });
        }
        if (Mensaje.contains("\u0006U")) {

            activity.runOnUiThread(() -> {
                TextView textView = tvTitulo;
                String textoCompleto = "Coloque el peso conocido ("+ tvPesoConocido.getText()+ spUnidad.getSelectedItem().toString()+") y luego presione \"SIGUIENTE\"";
                SpannableStringBuilder builder = new SpannableStringBuilder(textoCompleto);
                RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan(1.3f); // Aumenta el tamaño 1.5 veces
                builder.setSpan(sizeSpan1, 25,("Coloque el peso conocido ("+ tvPesoConocido.getText()+ spUnidad.getSelectedItem().toString()+")").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Aplica al texto desde el índice 10 al 15

                textView.setText(builder);
                if(loadingPanel!=null){

                    loadingPanel.setVisibility(View.INVISIBLE);
                    tvCarga.setVisibility(View.INVISIBLE);

                }
            });
        };
        if(Mensaje.contains("\u0006L ")){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvUltimaCalibracion.setText(Utils.DevuelveFecha()+" "+ Utils.DevuelveHora());
                    PreferencesDevicesManager.setUltimaCalibracion(BZA.Nombre,BZA.numBza,Utils.DevuelveFecha()+" "+ Utils.DevuelveHora(), activity);//BZA.set_UltimaCalibracion(Utils.DevuelveFecha()+" "+ Utils.DevuelveHora());
                }
            });

            activity.runOnUiThread(() -> {
                if (dialog1 != null) {
                    dialog1.setCancelable(true);
                    dialog1.cancel();
                }
                try {
                    boolBtIniciarCalibracion = true;
                    indiceCalibracion=1;
                    Thread.sleep(1000);
                    guardar(8, dialog, btGuardar);
                } catch (Exception e) {

                }

            });
        }


        if (Mensaje.contains("\u0006O ")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String strbin = "";
                    int promvar = 0;
                    int offvar = 0;
                    int acuvar = 0;
                    int pdvar = 0;
                    int divmvar = 0;
                    String pd="0";
                    String divm="0";
                    String promvars = "0";
                    String offvars = "0";
                    String acuvars = "0";
                    String binario = "";
                    String hex = Mensaje.substring(Mensaje.indexOf("\u0006O") + 5, Mensaje.indexOf("\u0006O") + 7);
                    promvars = Mensaje.substring(Mensaje.indexOf("\u0006O") + 34, Mensaje.indexOf("\u0006O") + 35);
                    offvars = Mensaje.substring(Mensaje.indexOf("\u0006O") + 33, Mensaje.indexOf("\u0006O") + 34);
                    acuvars = Mensaje.substring(Mensaje.indexOf("\u0006O") + 35, Mensaje.indexOf("\u0006O") + 36);
                    pd= Mensaje.substring(Mensaje.indexOf("\u0006O")+27,Mensaje.indexOf("\u0006O")+28);
                    divm= Mensaje.substring(Mensaje.indexOf("\u0006O")+28,Mensaje.indexOf("\u0006O")+29);
                    int decimal = Integer.parseInt(hex, 16); // Convertir hexadecimal a decimal
                    binario = Integer.toBinaryString(decimal); // Convertir decimal a binario
                    while (binario.length() < 8) {
                        binario = "0" + binario;
                    }
                    strbin = binario + " " + promvars + offvars + acuvars + pd + divm;
                    promvar = Integer.parseInt(strbin.substring(9, 10), 16);
                    offvar = Integer.parseInt(strbin.substring(10, 11), 16);
//                    acuvar = Integer.parseInt(strbin.substring(11, 12), 16);
                    pdvar = Integer.parseInt(strbin.substring(12, 13), 16);
                    divmvar = Integer.parseInt(strbin.substring(13, 14), 16);
                    strbin = strbin.substring(0, 8);

                    char[] charstr = strbin.toCharArray();
                    if (charstr[0] == '0') {
                        toggle1.check(OFF1.getId());
                        OFF1.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                        ON1.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                    }else{
                        toggle1.check(ON1.getId());
                        OFF1.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                        ON1.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));

                    }
                    toggle13.check(OFF13.getId());
                    OFF13.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                    ON13.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                    if (charstr[1] == '0') {
                        toggle2.check(OFF2.getId());
                        OFF2.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                        ON2.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                    }else{
                        toggle2.check(ON2.getId());

                        OFF2.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                        ON2.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                    }

                    if (charstr[2] == '0') {
                        toggle3.check(OFF3.getId());

                        OFF3.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                        ON3.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                    }else{
                        toggle3.check(ON3.getId());

                        OFF3.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                        ON3.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                    }
                    lastTanque = charstr[2] == '1';
                    if (charstr[3] == '0') {
                        toggle4.check(OFF4.getId());

                        OFF4.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                        ON4.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                    }else{
                        toggle4.check(ON4.getId());

                        OFF4.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                        ON4.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                    }

                    if (charstr[7] == '0') {
                        toggle8.check(OFF8.getId());

                        OFF8.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                        ON8.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                    }else{
                        toggle8.check(ON8.getId());

                        OFF8.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                        ON8.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                    }
                    spPro.setSelection(promvar);
                    spOff.setSelection(offvar);
//                    spAcu.setSelection(acuvar);
                    if(pdvar == 0){
                        spPuntoDecimal.setSelection(0);
                    }
                    if(pdvar==4){
                        spPuntoDecimal.setSelection(1);
                    }
                    if(pdvar==8){
                        spPro.setSelection(promvar);
                        spPuntoDecimal.setSelection(2);
                    }
                    if(pdvar==12){
                        spOff.setSelection(offvar);
                        spPuntoDecimal.setSelection(3);
                    }
                    if(divmvar==1){
                        spDivisionMinima.setSelection(0);
                    }
                    if(divmvar==2){

                        spDivisionMinima.setSelection(1);
                    }
                    if(divmvar==5){
                        spDivisionMinima.setSelection(2);
                    }
                }

            });
        }
        ArrayList<String> Listerr = new ArrayList<>();
        Listerr =BZA.Errores(Mensaje);
        if (Listerr != null) {
            for (int i = 0; i < Listerr.size(); i++) {
                indiceCalibracion = 1;
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog1 != null) {
                            dialog1.setCancelable(true);
                            dialog1.cancel();
                            dialog1 = null;


                        }
                        if (dialog != null) {
                            dialog.setCancelable(true);
                            dialog.cancel();
                            dialog = null;
                        }
                        boolBtIniciarCalibracion = true;
                        boolBtReset = true;
                        boolBtReajusteCero = true;

                    }
                });


                int finalI = i;
                ArrayList<String> finalListerr = Listerr;
                Fmensaje(finalListerr.get(finalI).toString(),R.layout.item_customtoasterror);
            }}
    }
    private void Fmensaje(String texto, int Color){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                float scale = activity.getApplicationContext().getResources().getDisplayMetrics().scaledDensity;
                float size=30*(scale*2);
                String txtanterior="";

                if (toast!=null && toast.getView().isShown()) {
                    txtanterior = ((TextView)toast.getView().findViewById(R.id.text)).getText().toString()+"\n"+"\n";
                    toast.cancel();
                    toast=null;
                    size=size-(10*(scale*2));
                }
                LayoutInflater inflater =activity.getLayoutInflater();
                View layout = inflater.inflate(Color, activity.findViewById(R.id.toast_layout_root));
                TextView text = layout.findViewById(R.id.text);
                text.setText(txtanterior+texto);
                text.setTextSize(size);

                toast = new Toast(activity.getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });

    }

    private void initializeViews(View view) {
        btIniciarCalibracion = view.findViewById(R.id.btIniciarCalibracion);
        btReAjusteCero = view.findViewById(R.id.btReajusteCero);
        btReset = view.findViewById(R.id.btreset);
        tableParametrosPrincipales = view.findViewById(R.id.TableParametrosprincipales);
        spDivisionMinima = view.findViewById(R.id.spDivisionMinima);
        spPuntoDecimal = view.findViewById(R.id.spPuntoDecimal);
        spUnidad = view.findViewById(R.id.spUnidad);
        tvCapacidad = view.findViewById(R.id.tvCapacidad);

        Button btenviarparam = view.findViewById(R.id.btenviarparam);
        tvPesoConocido = view.findViewById(R.id.tvPesoconocido);
        //-- NUEVO
        btSeteo= view.findViewById(R.id.btSeteo);
        toggle1 = view.findViewById(R.id.toggle1);
        OFF1 = view.findViewById(R.id.btOFF1);
        OFF2 = view.findViewById(R.id.btOFF2);
        OFF3 = view.findViewById(R.id.btOFF3);
        OFF4 = view.findViewById(R.id.btOFF4);
        OFF5 = view.findViewById(R.id.btOFF5);
        OFF6 = view.findViewById(R.id.btOFF6);
        OFF7 = view.findViewById(R.id.btOFF7);
        OFF8 = view.findViewById(R.id.btOFF8);
        OFF13= view.findViewById(R.id.btOFF13);

        ON1 = view.findViewById(R.id.btON1);
        ON2 = view.findViewById(R.id.btON2);
        ON3 = view.findViewById(R.id.btON3);
        ON4 = view.findViewById(R.id.btON4);
        ON5 = view.findViewById(R.id.btON5);
        ON6 = view.findViewById(R.id.btON6);
        ON7 = view.findViewById(R.id.btON7);
        ON8 = view.findViewById(R.id.btON8);
        ON13 = view.findViewById(R.id.btON13);

        toggle2 = view.findViewById(R.id.toggle2);
        toggle3 = view.findViewById(R.id.toggle3);
        toggle4 = view.findViewById(R.id.toggle4);
        toggle5 = view.findViewById(R.id.toggle5);
        toggle6 = view.findViewById(R.id.toggle6);
        toggle7 = view.findViewById(R.id.toggle7);
        toggle8 = view.findViewById(R.id.toggle8);
        toggle13 = view.findViewById(R.id.toggle13);
        toggle13.check(OFF13.getId());
        OFF13.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
        ON13.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
        ImButton = view.findViewById(R.id.animbutton);
        lnToggleDiv = view.findViewById(R.id.togglediv);
        LnSeteo = view.findViewById(R.id.Lseteo);
        ImSeteo = view.findViewById(R.id.imgseteo);
        ImCal = view.findViewById(R.id.imgCal);
        LCalibracion = view.findViewById(R.id.Lcalibracion);
        btCalibracion = view.findViewById(R.id.btCalibracion);

        if (BZA.serialPort != null) {
        PuertosSerie.PuertosSerieListener receiver = new PuertosSerie.PuertosSerieListener() {
                @Override
                public void onMsjPort(String data) {
                //    System.out.println("MINIMA MSJ: "+data);
                    procesarMensaje(data);
                }
            };
            reader = new PuertosSerie.SerialPortReader(BZA.serialPort.getInputStream(), receiver);

            reader.startReading();

        } else {
        }

        btSeteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(boolBtSeteo){
                    boolBtSeteo =false;
                    Runnable myRunnable = () -> {
                        try {
                           BZA.Pedirparam();

                            Thread.sleep(500);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ImCal.setVisibility(View.GONE);
                                    LCalibracion.setVisibility(View.GONE);
                                    ImSeteo.setVisibility(View.VISIBLE);
                                    LnSeteo.setVisibility(View.VISIBLE);
                                    collapseLinearLayout(tableParametrosPrincipales);
                                    ImButton.setRotation(0);
                                    boolBtSeteo =true;
                                }
                            });
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    };
                    Thread myThread2 = new Thread(myRunnable);
                    myThread2.start();
                }
            }
        });
        btCalibracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               BZA.Pedirparam();

                if(boolBtCalibracion) {
                    boolBtCalibracion = false;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ImCal.setVisibility(View.VISIBLE);
                            LCalibracion.setVisibility(View.VISIBLE);
                            ImSeteo.setVisibility(View.GONE);
                            LnSeteo.setVisibility(View.GONE);
                            collapseLinearLayout(tableParametrosPrincipales);
                            ImButton.setRotation(0);
                            boolBtCalibracion = true;
                        }
                    });

                }
            }
        });
        btenviarparam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(boolEnviarParam) {
                    boolEnviarParam =false;
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
                    View mView =activity.getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);
                    tvTitulo = mView.findViewById(R.id.textViewt);
                    loadingPanel = mView.findViewById(R.id.loadingPanel);
                    tvCarga = mView.findViewById(R.id.tvCarga);
                    btGuardar = mView.findViewById(R.id.buttons);
                    Button Cancelar = mView.findViewById(R.id.buttonc);
                    Cancelar.setVisibility(View.INVISIBLE);
                    tvTitulo.setText("Espere un momento...");
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();
                    Runnable myRunnable = () -> {

                        try {
                            ejecutarenviodeparametros();
                            Thread.sleep(3000);

                            guardar(8, null, null);
                            Thread.sleep(1000);
                            boolEnviarParam =true;
                            dialog.cancel();
                        } catch (Exception e) {

                        }
                    };
                    Thread myThread2 = new Thread(myRunnable);
                    myThread2.start();
                }

            }



        });


        lnToggleDiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollapsed) {
                    expandLinearLayout(tableParametrosPrincipales);
                    ImButton.setRotation(180);
                } else {
                    collapseLinearLayout(tableParametrosPrincipales);
                    ImButton.setRotation(0);
                }
                // isCollapsed = !isCollapsed;
            }
        });
        ImButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollapsed) {
                    expandLinearLayout(tableParametrosPrincipales);
                    ImButton.setRotation(180);
                } else {
                    collapseLinearLayout(tableParametrosPrincipales);
                    ImButton.setRotation(0);
                }
                // isCollapsed = !isCollapsed;
            }
        });

        toggle1.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton bton= view.findViewById(R.id.btON1);
            RadioButton btoff = view.findViewById(R.id.btOFF1);
            if(toggle1.getCheckedRadioButtonId()==R.id.btON1) {
                btoff.setText("NO");
                bton.setText("SI");
                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
            } else {
                btoff.setText("NO");
                bton.setText("SI");
                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
            }
        });

        toggle3.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton bton= view.findViewById(R.id.btON3);
            RadioButton btoff = view.findViewById(R.id.btOFF3);

            if(toggle3.getCheckedRadioButtonId()==R.id.btON3){
                btoff.setText("NO");
                bton.setText("SI");

                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
            } else {
                btoff.setText("NO");
                bton.setText("SI");
                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));

            }

        });

        spAcu = view.findViewById(R.id.spacu);
        spOff = view.findViewById(R.id.spoff);
        spPro = view.findViewById(R.id.sppro);
         spPro.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        spAcu.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        spOff.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        spDivisionMinima.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        spPuntoDecimal.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        spUnidad.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        tvUltimaCalibracion =view.findViewById(R.id.tvUltimaCalibracion);
        tvPesoConocido.setText(PreferencesDevicesManager.getPesoConocido(BZA.Nombre,BZA.numBza, activity));
        tvCapacidad.setText(PreferencesDevicesManager.getCapacidadMax(BZA.Nombre,BZA.numBza, activity));
        tvUltimaCalibracion.setText(PreferencesDevicesManager.getUltimaCalibracion(BZA.Nombre,BZA.numBza, activity));
        if(Objects.equals(PreferencesDevicesManager.getUnidad(BZA.Nombre,BZA.numBza, activity), "ton")){
            spUnidad.setSelection(2);
        }else if(Objects.equals(PreferencesDevicesManager.getUnidad(BZA.Nombre,BZA.numBza, activity), "gr")){
            spUnidad.setSelection(0);
        }else{
            spUnidad.setSelection(1);
        }
        spUnidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    PreferencesDevicesManager.setUnidad(BZA.Nombre,BZA.numBza,"gr", activity);//BZA.setUnidad("gr");
                }
                if(i==1){
                    PreferencesDevicesManager.setUnidad(BZA.Nombre,BZA.numBza,"kg", activity);//BZA.setUnidad("kg");
                }
                if(i==2){
                    PreferencesDevicesManager.setUnidad(BZA.Nombre,BZA.numBza,"ton", activity);//BZA.setUnidad("ton");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



    }

    private void setClickListeners() {
        bt_home.setOnClickListener(view1 -> {
          //  System.out.println("boolbthome "+ boolBtHome);
            if(boolBtHome){
                boolBtHome =false;
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            guardar(8,null,null);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
                                    View mView =activity.getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);
                                    tvTitulo =mView.findViewById(R.id.textViewt);
                                    loadingPanel=mView.findViewById(R.id.loadingPanel);
                                    tvCarga=mView.findViewById(R.id.tvCarga);
                                    btGuardar =  mView.findViewById(R.id.buttons);
                                    Button Cancelar =  mView.findViewById(R.id.buttonc);
                                    Cancelar.setVisibility(View.INVISIBLE);
                                    btGuardar.setVisibility(View.INVISIBLE);
                                    tvTitulo.setText("espere un momento...");

                                    mBuilder.setView(mView);
                                    dialog = mBuilder.create();
                                    dialog.show();
                                }
                            });
                            Thread.sleep(2000) ; //pa minima
                            BZA.Salir_cal();
                            Thread.sleep(14000);
                           // if(!stoped){
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        estado= BalanzaBase.M_MODO_BALANZA;
                                        // BZA.setPesoUnitario(PreferencesDevicesManager.getPesoUnitario(BZA.Nombre,BZA.numBza, activity));
                                        BZA.Estado = BalanzaBase.M_MODO_BALANZA;
                                        reader.stopReading();
                                        BZA.readers.startReading();
                                        reader=null;
                                        ComService.getInstance().openServiceFragment();
                                        boolBtHome =true;
                                        dialog.cancel();
                                    }
                                });
                           // }


                        } catch (InterruptedException e) {
                          //  System.out.println("OLA?"+e.getMessage());
                        }

                    }
                }.start();

            }
        });

        btReAjusteCero.setOnClickListener(view -> {
            if(boolBtReajusteCero) {
                boolBtReajusteCero = false;
               BZA.ReAjusteCero();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
                View mView =activity.getLayoutInflater().inflate(R.layout.dialogo_transferenciaarchivo, null);
                TextView textView = mView.findViewById(R.id.textView);
                textView.setText("Re ajustando...");
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
            }
        });
        btReset.setOnClickListener(view -> {
            if(boolBtReset) {
                boolBtReset = false;
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);

                View mView =activity.getLayoutInflater().inflate(R.layout.dialogo_dossinet, null);
                TextView textView = mView.findViewById(R.id.textViewt);
                textView.setText("¿Esta seguro de resetear los parametros y la calibracion?");

                btGuardar = mView.findViewById(R.id.buttons);
                Button Cancelar = mView.findViewById(R.id.buttonc);
                btGuardar.setText("Resetear");

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                btGuardar.setOnClickListener(view1 -> {

                    AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(activity);;
                    View mView1 =activity.getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);
                    tvTitulo = mView1.findViewById(R.id.textViewt);
                    loadingPanel = mView1.findViewById(R.id.loadingPanel);
                    tvCarga = mView1.findViewById(R.id.tvCarga);
                    Button Guardar1 = mView1.findViewById(R.id.buttons);
                    Button Cancelar1 = mView1.findViewById(R.id.buttonc);
                    Cancelar1.setVisibility(View.INVISIBLE);
                    tvTitulo.setText("Espere un momento...");
                    Guardar1.setClickable(false);
                    mBuilder1.setView(mView1);
                    dialog.cancel();
                    dialog1 = mBuilder1.create();
                    dialog1.show();
                    BZA.reset();


                    Runnable myRunnable1 = () -> {
                        try {
                            Thread.sleep(1000);
                           BZA.Pedirparam();

                            Thread.sleep(500);
                            String param1 = "";
                            param1 += leertoggles(toggle1, R.id.btON1);
                            param1 += leertoggles(toggle2, R.id.btON2);// "0";
                            param1 += leertoggles(toggle3, R.id.btON3);
                            param1 += leertoggles(toggle4, R.id.btON4); //"0";
                            lastTanque = (leertoggles(toggle3, R.id.btON3).equals("1"));
                            param1 += "0";// leertoggles(toggle5,R.id.btON5);
                            param1 += "1";// leertoggles(toggle6,R.id.btON6); // ESTE NECESITA ESTAR EN 0
                            param1 += "1";// leertoggles(toggle7,R.id.btON7);
                            param1 += leertoggles(toggle8, R.id.btON8); // "0";
                            String param2 ="" ;//"00000000";
                            param2 += 0;
                            param2 += 0;
                            param2 += 0;
                            param2 += 0;
                            param2 +=  leertoggles(toggle13, R.id.btON13);
                            param2 += 0;
                            param2 += 0;
                            param2 += 0;
                            BZA.EnviarParametros(param1, param2, spPro.getSelectedItem().toString(), spOff.getSelectedItem().toString(), spAcu.getSelectedItem().toString());


                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    };
                    Runnable myRunnable = () -> {
                        try {
                            Thread myThread = new Thread(myRunnable1);
                            myThread.start();
                            Thread.sleep(3000);
                            boolBtReset = true;
                            dialog1.cancel();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }


                    };
                    Thread myThread2 = new Thread(myRunnable);
                    myThread2.start();

                });

                Cancelar.setOnClickListener(view1 -> {
                    dialog.cancel();
                    boolBtReset =true;
                });


            }
        });
        btIniciarCalibracion.setOnClickListener(view12 -> {
            if(boolBtIniciarCalibracion) {
                boolBtIniciarCalibracion = false;
                String CapDivPDecimal =BZA.CapacidadMax_DivMin_PDecimal(tvCapacidad.getText().toString(), spDivisionMinima.getSelectedItem().toString(), String.valueOf(spPuntoDecimal.getSelectedItemPosition()));
                try {
                    BZA.serialPort.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if(CapDivPDecimal!=null){
                    inicioCalibracion(CapDivPDecimal);
                }else{
                    Mensaje("Revisa la capacidad, division minima y  el punto decimal",R.layout.item_customtoasterror,activity);
                    boolBtIniciarCalibracion =true;
                }
            }
        });
        tvCapacidad.setOnClickListener(view110 -> Dialog(tvCapacidad, "", "Capacidad"));
        tvPesoConocido.setOnClickListener(view112 -> Dialog(tvPesoConocido, "", "Peso Conocido"));
        try {
            BZA.abrirCalib();
            Thread.sleep(700);
        } catch (InterruptedException e) {

        }finally {
            BZA.Pedirparam();
        }


    }
    private void guardar(int indexcalib,AlertDialog dialog, Button boton){
        BZA.Guardar_cal();

    }
    private void enviarpddiv(String CapDivPDecimal) throws InterruptedException {
        BZA.escribir(CapDivPDecimal,0);
        Thread.sleep(500);
    }
    private void inicioCalibracion(String CapDivPDecimal) {
        final Boolean[] bt_guardarbool = {true};
        if(CapDivPDecimal!="ERRCONTROL")
        {
            BZA.escribir(CapDivPDecimal,BZA.numBza);
           PreferencesDevicesManager.setCapacidadMax(BZA.Nombre,BZA.numBza,tvCapacidad.getText().toString(), activity);//BZA.set_CapacidadMax(tvCapacidad.getText().toString());
           PreferencesDevicesManager.setDivisionMinima(BZA.Nombre,BZA.numBza,spDivisionMinima.getSelectedItemPosition(), activity);//;BZA.set_DivisionMinima(spDivisionMinima.getSelectedItemPosition());
           PreferencesDevicesManager.setPuntoDecimal(BZA.Nombre,BZA.numBza,spPuntoDecimal.getSelectedItemPosition(), activity);//BZA.set_PuntoDecimal(spPuntoDecimal.getSelectedItemPosition());

        }
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);;
        View mView =activity.getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);

        tvTitulo =mView.findViewById(R.id.textViewt);
        loadingPanel=mView.findViewById(R.id.loadingPanel);
        tvCarga=mView.findViewById(R.id.tvCarga);
        btGuardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        Cancelar.setVisibility(View.INVISIBLE);
        tvTitulo.setText("");

        mBuilder.setView(mView);
        dialog1 = mBuilder.create();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();


        Runnable myRunnable = () -> {
            try {

                if (CapDivPDecimal != "ERRCONTROL"){
                    Thread.sleep(1000);
                    enviarpddiv(CapDivPDecimal);
                }
                activity.runOnUiThread(() -> {
                    if(CapDivPDecimal.equals("ERRCONTROL")){

                        switch (indiceCalibracion){
                            case 1:
                                tvTitulo.setText("Verifique que la balanza este en cero, luego presione \"SIGUIENTE\"");
                                loadingPanel.setVisibility(View.INVISIBLE);
                                tvCarga.setVisibility(View.INVISIBLE);
                                btGuardar.setClickable(true);
                                break;
                            case 2:
                                TextView textView = tvTitulo;
                                String textoCompleto = "Coloque el peso conocido ("+ tvPesoConocido.getText()+ spUnidad.getSelectedItem().toString()+") y luego presione \"SIGUIENTE\"";

                                SpannableStringBuilder builder = new SpannableStringBuilder(textoCompleto);

// Aplicar tamaño más grande a una parte del texto
                                RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan(1.3f); // Aumenta el tamaño 1.5 veces
                                builder.setSpan(sizeSpan1, 25,("Coloque el peso conocido ("+ tvPesoConocido.getText()+ spUnidad.getSelectedItem().toString()+")").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Aplica al texto desde el índice 10 al 15
// Puedes aplicar más estilos a otras partes del texto si es necesario

                                textView.setText(builder);
                                loadingPanel.setVisibility(View.INVISIBLE);
                                tvCarga.setVisibility(View.INVISIBLE);
                                btGuardar.setClickable(true);
                                break;
                            case 3:
                                if(!lastTanque){
                                    tvTitulo.setText("Coloque el recero y luego presione \"SIGUIENTE\"");
                                    loadingPanel.setVisibility(View.INVISIBLE);
                                    tvCarga.setVisibility(View.INVISIBLE);
                                    btGuardar.setClickable(true);

                                }else {
                                    indiceCalibracion = 1;
                                    dialog1.setCancelable(true);
                                    dialog1.cancel();
                                }
                                break;
                            default:

                                break;
                        }
                    }else{
                        tvTitulo.setText("Verifique que la balanza este en cero, luego presione \"SIGUIENTE\"");
                        loadingPanel.setVisibility(View.INVISIBLE);
                        tvCarga.setVisibility(View.INVISIBLE);
                        btGuardar.setClickable(true);


                    }
                });




            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();

        btGuardar.setOnClickListener(view -> {
            if(bt_guardarbool[0] && !tvTitulo.getText().toString().toLowerCase(Locale.ROOT)
                    .contains("espere un momento") && !tvTitulo.getText().toString().toLowerCase(Locale.ROOT).contains("cargando") && loadingPanel.getVisibility()!=View.VISIBLE ){
                bt_guardarbool[0] =false;
                switch (indiceCalibracion) {
                    case 1:

                        ejecutarCalibracionCero(btGuardar, tvTitulo,loadingPanel,tvCarga,dialog1);
                        break;
                    case 2:
                        ejecutarCalibracionPesoConocido(btGuardar, tvTitulo,loadingPanel,tvCarga, dialog1);
                        break;
                    case 3:

                        break;

                    default:
                        break;
                }
                indiceCalibracion++;
                bt_guardarbool[0]=true;
            }
        });

        Cancelar.setOnClickListener(view -> dialog1.cancel());
    }
    private void ejecutarCalibracionPesoConocido(Button Guardar,TextView titulo,ProgressBar loadingPanel,TextView tvCarga,AlertDialog dialog) {
        String msj =BZA.Peso_conocido(tvPesoConocido.getText().toString(),String.valueOf(spPuntoDecimal.getSelectedItemPosition())); // ,tv_capacidad.getText().toString()
        if(msj!=null){
            BZA.escribir(msj,0);
        }else{
            dialog1.cancel();
            boolBtIniciarCalibracion =true;
           Mensaje("Error, peso conocido fuera de rango de acuerdo a Capacidad/punto decimal elegida", R.layout.item_customtoasterror,activity);
        }
        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);
       PreferencesDevicesManager.setPesoConocido(BZA.Nombre,BZA.numBza,tvPesoConocido.getText().toString(), activity);
    }

    private void ejecutarenviodeparametros( ) {



        Runnable myRunnable = () -> {
            try {
                String param1="";
                param1 += leertoggles(toggle1,R.id.btON1);
                param1 += leertoggles(toggle2,R.id.btON2);// "0";
                param1 += leertoggles(toggle3,R.id.btON3);
                param1 +=leertoggles(toggle4,R.id.btON4); //"0";
                lastTanque = (leertoggles(toggle3, R.id.btON3).equals("1"));
                param1 +="0";// leertoggles(toggle5,R.id.btON5);
                param1 +=  "1";// leertoggles(toggle6,R.id.btON6); // ESTE NECESITA ESTAR EN 0
                param1 +="1";// leertoggles(toggle7,R.id.btON7);
                param1 += leertoggles(toggle8,R.id.btON8); // "0";
                String param2 ="" ;//"00000000";
                param2 += 0;
                param2 += 0;
                param2 += 0;
                param2 += 0;
                param2 +=  leertoggles(toggle13, R.id.btON13);
                param2 += 0;
                param2 += 0;
                param2 += 0;
                BZA.EnviarParametros(param1,param2, spPro.getSelectedItem().toString(), spOff.getSelectedItem().toString(), spAcu.getSelectedItem().toString());
                Thread.sleep(1000);


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread myThread = new Thread(myRunnable);
        myThread.start();
    }
    private void ejecutarCalibracionCero(Button Guardar,TextView titulo,ProgressBar loadingPanel,TextView tvCarga,AlertDialog dialog) {
        BZA.Cero_cal();
        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);

    }


    public void Dialog(TextView textView,String string,String Texto){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);;
        View mView =activity.getLayoutInflater().inflate(R.layout.dialogo_dosopciones_i, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        final LinearLayout delete_text= mView.findViewById(R.id.lndelete_text);
        delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInput.setText("");
            }
        });
        userInput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        tvTitulo =mView.findViewById(R.id.textViewt);
        tvTitulo.setText(Texto);
        if(string.equals("Ceroinicial")){
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
            userInput.requestFocus();
        }
        else{
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            userInput.requestFocus();
            if(Texto.equals("Peso Conocido")){ // Peso conocido con coma
                userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
                userInput.requestFocus();
            }
        }
        if(!textView.getText().toString().equals("") && !textView.getText().toString().equals("-")){
            userInput.setText(textView.getText().toString());
            userInput.requestFocus();
            userInput.setSelection(userInput.getText().length());
        }

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Texto.equals("Peso Conocido")){
                    if(BZA.Peso_conocido(userInput.getText().toString(),String.valueOf(spPuntoDecimal.getSelectedItemPosition()))!=null){ // ,tv_capacidad.getText().toString()
                         textView.setText(userInput.getText().toString());
                    }else{
                        Mensaje("Error, peso conocido fuera de rango de acuerdo a Capacidad/punto decimal elegida", R.layout.item_customtoasterror,activity);
                    }
                } else{
                    textView.setText(userInput.getText().toString());
                }

                dialog.cancel();
            }
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    @Override
    public void onDestroyView() {
        stoped=true;
        super.onDestroyView();
    }



}


