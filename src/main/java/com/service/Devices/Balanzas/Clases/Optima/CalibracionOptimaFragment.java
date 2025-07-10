package com.service.Devices.Balanzas.Clases.Optima;
import static com.service.utilsPackage.Utils.Mensaje;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;


import com.service.BalanzaService;
import com.service.Devices.Balanzas.Clases.BalanzaBase;
import com.service.utilsPackage.ComService;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import com.service.Comunicacion.PuertosSerie.PuertosSerie;
import com.service.utilsPackage.PreferencesDevicesManager;
import com.service.R;
import com.service.utilsPackage.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class CalibracionOptimaFragment extends Fragment {

    Button bt_iniciarCalibracion,btReajusteCero,bt_reset,btCalibracion,bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6,Guardar; //btSeteo,
    static AppCompatActivity activity;
    static OPTIMA_I BZA;
    private ButtonProvider buttonProvider;
    PuertosSerie.SerialPortReader reader=null;
    ProgressBar loadingPanel;
    ImageView animbutton,imgseteo,imgCal;
    LinearLayout togglediv,Lseteo,Lcalibracion;
    AlertDialog dialog, dialog1,dialog2, dialog3,dialog4, dialog5;
    Spinner sp_divisionMinima,sp_puntoDecimal,sp_unidad,sp_acu,sp_pro,sp_off/*,sp_bot,sp_reg,sp_uni,sp_ano,sp_dat,sp_bf1,sp_bps*/;
    TextView titulo,tvCarga,tv_pesoConocido, tv_capacidad,tv_ultimaCalibracion;
    RadioGroup toggle1,toggle2,toggle3,toggle4,toggle5,toggle6,toggle7,toggle8,toggle13;
    RadioButton OFF1,OFF2,OFF3,OFF4,OFF5,OFF6,OFF7,OFF8,OFF13, ON1,ON2,ON3,ON4,ON5,ON6,ON7,ON8,ON13;
    ConstraintLayout table_parametrosPrincipales;
    View viewMang=null;
    BalanzaService Service;

    public float Bruto=0,Tara=0,Neto=0/*,pico=0,taraDigital=0,pesoUnitario=0.5F,pesoBandaCero=0F*/;
    public Boolean lasttanque = true,BooleanRESET=false,btSeteobool=true,bt_homebool=true,bt_resetbool=true,btCalibracionbool=true,enviarparambool=true,bt_iniciarCalibracionbool=true,btReajusteCerobool=true/*,bandaCero =true,inicioBandaPeso=false*/;
    public String estable="",read,estado=BalanzaBase.M_VERIFICANDO_MODO/*,ultimaCalibracion="",brutoStr="0",netoStr="0",taraStr="0",taraDigitalStr="0",picoStr="0"*/;
    public int /*puntoDecimal=1,acumulador=0,*/indiceCalibracion=1,numero=1;
    Boolean stoped=false;
    private boolean isCollapsed = false;
    private int initialWidth = 258;
   // private OPTIMA_I bza;
//    private OnFragmentChangeListener fragmentChangeListener;

    public static CalibracionOptimaFragment newInstance(OPTIMA_I instance, BalanzaService service) {
        CalibracionOptimaFragment fragment = new CalibracionOptimaFragment();
        Bundle args = new Bundle();
        args.putSerializable("instance", instance);
        args.putSerializable("instanceService", service);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //mainActivity=(MainActivity)activity;
         viewMang = inflater.inflate(R.layout.standar_calibracion_v2,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        estado= BalanzaBase.M_MODO_CALIBRACION;
        if (getArguments() != null) {
            BZA = (OPTIMA_I)getArguments().getSerializable("instance");
            Service =  BalanzaService.getInstance();//(BalanzaService) getArguments().getSerializable("instanceService");
            activity = ComService.getInstance().activity;//activity = (AppCompatActivity)BZA.activity;//BZA.activity;
            BZA.abrircalib();

        }
        return viewMang;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        configuracionBotones();
        initializeViews(view);
        setClickListeners();


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
        sp_acu.setAdapter(adapter11);
        ArrayAdapter<String> adapter13= new ArrayAdapter<>(activity.getApplicationContext(),R.layout.item_spinner,Lista0a9);
        adapter13.setDropDownViewResource(R.layout.item_spinner);
        sp_off.setAdapter(adapter13);
        toggle4.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton bton= view.findViewById(R.id.btON4);
            RadioButton btoff = view.findViewById(R.id.btOFF4);
            if(toggle4.getCheckedRadioButtonId()==R.id.btON4){
                btoff.setText("NO");
                bton.setText("SI");
                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
            } else {
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                btoff.setText("NO");
                bton.setText("SI");
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
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
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
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                    }
        });
        ArrayAdapter<String> adapter12= new ArrayAdapter<>(activity.getApplicationContext(),R.layout.item_spinner,Lista0aF);
        adapter12.setDropDownViewResource(R.layout.item_spinner);
        sp_pro.setAdapter(adapter12);
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
            bt_home.setOnClickListener(view -> {
                if(bt_homebool){
                    bt_homebool=false;
                    new Thread() {
                        @Override
                        public void run() {
                            try {

                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
                                        View mView =activity.getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);
                                         titulo=mView.findViewById(R.id.textViewt);
                                         loadingPanel=mView.findViewById(R.id.loadingPanel);
                                         tvCarga=mView.findViewById(R.id.tvCarga);
                                         Guardar =  mView.findViewById(R.id.buttons);
                                        Button Cancelar =  mView.findViewById(R.id.buttonc);
                                        Cancelar.setVisibility(View.INVISIBLE);
                                        Guardar.setVisibility(View.INVISIBLE);
                                        titulo.setText("espere un momento...");
                                        mBuilder.setView(mView);
                                        dialog2 = mBuilder.create();
                                        dialog2.show();
                                    }
                                });
                                Thread.sleep(2000);
                                BZA.Salir_cal();
                                Thread.sleep(14000);
                                //if(!stoped){
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            estado= BalanzaBase.M_MODO_BALANZA;
                                            //BZA.setPesoUnitario(PreferencesDevicesManager.getPesoUnitario(BZA.Nombre,BZA.numBza, activity));//BZA.setPesoUnitario( BZA.getPesoUnitario());
                                            BZA.Estado =  BalanzaBase.M_MODO_BALANZA;
                                            try {
                                                estado= BalanzaBase.M_MODO_BALANZA;
                                              //  BZA.setPesoUnitario(PreferencesDevicesManager.getPesoUnitario(BZA.Nombre,BZA.numBza, activity));// BZA.setPesoUnitario( BZA.getPesoUnitario());
                                                BZA.Estado =  BalanzaBase.M_MODO_BALANZA;
                                                reader.stopReading();
                                                if(BZA.readers!=null) {
                                                    BZA.readers.startReading();
                                                }
                                                reader=null;
                                                bt_homebool=true;
                                                dialog.cancel();
                                                ComService.getInstance().openServiceFragment();
                                            }catch (Exception e){
                                             //   System.out.println("OLA?"+e.getMessage());
                                            }
                                        }
                                    });
                              //  }
                            } catch (InterruptedException e) {
                               // System.out.println("OLA?"+e.getMessage());
                            }
                        }
                    }.start();
                }
            });
        }
    }

    public void procesarMensaje(String Mensaje) {

        if(Mensaje.contains("\u0006E")){
            Thread s = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(12000);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    reader.stopReading();
                                    reader=null;
                                    if(BZA.readers!=null) {
                                        BZA.readers.startReading();
                                    }
                                    estado= BalanzaBase.M_MODO_BALANZA;
                                    // BZA.setPesoUnitario(PreferencesDevicesManager.getPesoUnitario(BZA.Nombre,BZA.numBza, activity)); //BZA.setPesoUnitario( BZA.getPesoUnitario());
                                    BZA.Estado =  BalanzaBase.M_MODO_BALANZA;
                                    ComService.getInstance().openServiceFragment();
                                    bt_homebool=true;
                                    dialog2.cancel();
                                }catch(Exception e){

                                }

                            }
                        });
                    } catch (InterruptedException e) {
                    }
                }
            });
            s.start();
          }
        if(Mensaje.contains("\u0006D")){
//            BZA.Guardar_cal(); // ANTES GUARDABA
        }
        if(Mensaje.contains("\u0006T")){
            bt_resetbool=true;
            BZA.Pedirparam();
            BooleanRESET=true;
        }
        if(Mensaje.contains("\u0006P")){
            BZA.Guardar_cal();
            enviarparambool=true;
            if( BooleanRESET){
                bt_resetbool = true;
                BooleanRESET=false;
                dialog1.cancel();
            }else{
                dialog3.cancel();
            }
        }
        if(Mensaje.contains("\u0006M ")){
            activity.runOnUiThread(() -> {
                BZA.Guardar_cal();
                btReajusteCerobool=true;
                dialog4.cancel();
            });
        }
        if (Mensaje.contains("\u0006U")) {

            activity.runOnUiThread(() -> {
                TextView textView = titulo;
                String textoCompleto = "Coloque el peso conocido ("+tv_pesoConocido.getText()+sp_unidad.getSelectedItem().toString()+") y luego presione \"SIGUIENTE\"";
                SpannableStringBuilder builder = new SpannableStringBuilder(textoCompleto);
                RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan(1.3f); // Aumenta el tamaño 1.5 veces
                builder.setSpan(sizeSpan1, 25,("Coloque el peso conocido ("+tv_pesoConocido.getText()+sp_unidad.getSelectedItem().toString()+")").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Aplica al texto desde el índice 10 al 15
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_ultimaCalibracion.setText(Utils.DevuelveFecha()+" "+ Utils.DevuelveHora());
                        PreferencesDevicesManager.setUltimaCalibracion(BZA.Nombre,BZA.numBza,Utils.DevuelveFecha()+" "+ Utils.DevuelveHora(), activity);// BZA.set_UltimaCalibracion(getFecha()+" "+ getHora());
                    }
                });
                textView.setText(builder);
                 if(loadingPanel!=null){
                    loadingPanel.setVisibility(View.INVISIBLE);
                    tvCarga.setVisibility(View.INVISIBLE);
                }
            });
        };
        if(Mensaje.contains("\u0006L ")){
            activity.runOnUiThread(() -> {
                if(!lasttanque) {
                    try{
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                titulo.setText("Coloque el recero y luego presione \"SIGUIENTE\"");
                                loadingPanel.setVisibility(View.INVISIBLE);
                                tvCarga.setVisibility(View.INVISIBLE);
                                tv_ultimaCalibracion.setText(Utils.DevuelveFecha()+" "+Utils.DevuelveHora());
                                PreferencesDevicesManager.setUltimaCalibracion(BZA.Nombre,BZA.numBza,Utils.DevuelveFecha()+" "+ Utils.DevuelveHora(), activity);//BZA.set_UltimaCalibracion(Utils.DevuelveFecha()+" "+Utils.DevuelveHora());
                            }
                        });
                    }catch (Exception e){
                    }
                }else{
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_ultimaCalibracion.setText(Utils.DevuelveFecha()+" "+ Utils.DevuelveHora());
                            PreferencesDevicesManager.setUltimaCalibracion(BZA.Nombre,BZA.numBza,Utils.DevuelveFecha()+" "+ Utils.DevuelveHora(), activity);//BZA.set_UltimaCalibracion(Utils.DevuelveFecha()+" "+Utils.DevuelveHora());
                            //BZA.set_UltimaCalibracion(getFecha()+" "+ getHora());
                        }
                    });
                    if(dialog1!=null){
                        dialog1.setCancelable(true);
                        dialog1.cancel();
                    }
                    try {
                        bt_iniciarCalibracionbool=true;
                        indiceCalibracion = 1;
                        Thread.sleep(1000);
//                        BZA.Guardar_cal();ANTES GUARDABA
                    }catch (Exception e) {
                    }
                }
            });
        }
        if(Mensaje.contains("\u0006Z ")){
            if(lasttanque || indiceCalibracion==4){
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_ultimaCalibracion.setText(Utils.DevuelveFecha()+" "+Utils.DevuelveHora());
                        PreferencesDevicesManager.setUltimaCalibracion(BZA.Nombre,BZA.numBza,Utils.DevuelveFecha()+" "+ Utils.DevuelveHora(), activity);//BZA.set_UltimaCalibracion(Utils.DevuelveFecha()+" "+Utils.DevuelveHora());
                    }
                });
                if(!stoped ){
                    try {
                        Thread.sleep(1000);
                        activity.runOnUiThread(() -> {
                            BZA.Guardar_cal();
                            bt_iniciarCalibracionbool=true;
                            indiceCalibracion = 1;
                            if(dialog1!=null){
                                dialog1.setCancelable(true);
                                dialog1.cancel();
                            }
                        });
                    }catch (Exception e){
                    }
                }
            }else{
                try{
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            titulo.setText("Coloque el recero y luego presione \"SIGUIENTE\"");
                            loadingPanel.setVisibility(View.INVISIBLE);
                            tvCarga.setVisibility(View.INVISIBLE);
                            tv_ultimaCalibracion.setText(Utils.DevuelveFecha()+" "+Utils.DevuelveHora());
                            PreferencesDevicesManager.setUltimaCalibracion(BZA.Nombre,BZA.numBza,Utils.DevuelveFecha()+" "+ Utils.DevuelveHora(), activity);//BZA.set_UltimaCalibracion(Utils.DevuelveFecha()+" "+Utils.DevuelveHora());
                        }
                    });
                }catch (Exception e){

                }
            }
        }
        if (Mensaje.contains("\u0006O ")) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String strbin = "";
                            int promvar = 0;
                            int offvar = 0;
                            int acuvar = 0;
                            int pdvar = 0;
                            int divmvar = 0;
                            String pd = "0";
                            String divm = "0";
                            String promvars = "0";
                            String offvars = "0";
                            String acuvars = "0";
                            String binario = "";
                            String hex = Mensaje.substring(Mensaje.indexOf("\u0006O ") + 5, Mensaje.indexOf("\u0006O ") + 7);
                            promvars = Mensaje.substring(Mensaje.indexOf("\u0006O ") + 38, Mensaje.indexOf("\u0006O ") + 39);
                            offvars = Mensaje.substring(Mensaje.indexOf("\u0006O ") + 37, Mensaje.indexOf("\u0006O ") + 38);
//                            acuvars = Mensaje.substring(Mensaje.indexOf("\u0006O ") + 39, Mensaje.indexOf("\u0006O ") + 40);
                            pd = Mensaje.substring(Mensaje.indexOf("\u0006O ") + 27, Mensaje.indexOf("\u0006O ") + 28);
                            divm = Mensaje.substring(Mensaje.indexOf("\u0006O ") + 28, Mensaje.indexOf("\u0006O ") + 29);

                            int decimal = Integer.parseInt(hex, 16); // Convertir hexadecimal a decimal
                            binario = Integer.toBinaryString(decimal); // Convertir decimal a binario
                            while (binario.length() < 8) {
                                binario = "0" + binario;
                            }
                            strbin = binario + " " + promvars + offvars + acuvars + pd + divm;
                            promvar = Integer.parseInt(strbin.substring(9, 10), 16);
                            offvar = Integer.parseInt(strbin.substring(10, 11), 16);
                            acuvar = Integer.parseInt(strbin.substring(11, 12), 16);
                            pdvar = Integer.parseInt(strbin.substring(12, 13), 16);
                            divmvar = Integer.parseInt(strbin.substring(13, 14), 16);
                            strbin = strbin.substring(0, 8);
                            char[] charstr = strbin.toCharArray();

                            if (charstr[0] == '0') {
                                ON1.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                                OFF1.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                                toggle1.check(OFF1.getId());
                            } else {
                                toggle1.check(ON1.getId());

                                OFF1.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                                ON1.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                            }
                            if (charstr[1] == '0') {
                                //System.out.println(toggle2 + "" + OFF2 + "" + R.id.btOFF2);
                                toggle2.check(OFF2.getId());
                                ON2.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                                OFF2.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));

                            } else {
                                toggle2.check(ON2.getId());
                                OFF2.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                                ON2.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));

                            }
                            toggle13.check(OFF13.getId());
                        OFF13.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                        ON13.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));


                        // inittoggle(toggle2,ON2,OFF2);
                            if (charstr[2] == '0') {
                                toggle3.check(OFF3.getId());
                                ON3.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                                OFF3.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                            } else {
                                toggle3.check(ON3.getId());
                                OFF3.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                                ON3.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                            }
                            lasttanque = charstr[2] == '1';

                            //inittoggle(toggle3,ON3,OFF3);
                            if (charstr[3] == '0') {
                                toggle4.check(OFF4.getId());
                                ON4.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                                OFF4.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));

                            } else {

                                OFF4.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                                ON4.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                                toggle4.check(ON4.getId());
                            }
                            if (charstr[7] == '0') {
                                toggle8.check(OFF8.getId());

                                ON8.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                                OFF8.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                            } else {
                                toggle8.check(ON8.getId());

                                OFF8.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                                ON8.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));
                            }
                            //inittoggle(toggle8,ON8,OFF8);


                            //  System.out.println("OPTIMA" + promvar);
                            sp_pro.setSelection(promvar);
                            // System.out.println("OPTIMA" + offvar);
                            sp_off.setSelection(offvar);
                            //System.out.println("OPTIMA" + acuvar);
//                            sp_acu.setSelection(acuvar);

                            // System.out.println("PDDIVM"+pdvar+" "+ divmvar );
                            if (pdvar == 12) {
                                sp_puntoDecimal.setSelection(0);
                            }
                            if (pdvar == 0) {
                                sp_puntoDecimal.setSelection(1);
                            }
                            if (pdvar == 4) {

                                sp_puntoDecimal.setSelection(2);
                            }
                            if (pdvar == 8) {
                                sp_puntoDecimal.setSelection(3);
                            }
                            if (divmvar == 1) {
                                sp_divisionMinima.setSelection(0);
                            }
                            if (divmvar == 2) {

                                sp_divisionMinima.setSelection(1);
                            }
                            if (divmvar == 5) {
                                sp_divisionMinima.setSelection(2);
                            }
//                        } catch (Exception e) {
//                            Mensaje("Error al momento de leer parametros",R.layout.item_customtoasterror,mainActivity);
//                        }
                    if(BooleanRESET){
                        String param1 = "";
                        param1 += leertoggles(toggle1, R.id.btON1);
                        param1 += leertoggles(toggle2, R.id.btON2);// "0";
                        param1 += leertoggles(toggle3, R.id.btON3);
                        lasttanque= (leertoggles(toggle3, R.id.btON3).equals("1"));
                        param1 += leertoggles(toggle4, R.id.btON4); //"0";
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
                        BZA.EnviarParametros(param1, param2, sp_pro.getSelectedItem().toString(), sp_off.getSelectedItem().toString(), sp_acu.getSelectedItem().toString());
                    }
                    }

                    });
            } catch (Exception e) {

            }
        }
        ArrayList<String> Listerr = new ArrayList<>();
        Listerr = BZA.Errores(Mensaje);//Errores2(Mensaje);//
        if (Listerr != null) {
            for (int i = 0; i < Listerr.size(); i++) {
                    indiceCalibracion = 1;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null) {

                                dialog.setCancelable(true);
                                dialog.cancel();
                                dialog = null;
                            }
                            if (dialog1 != null) {
                                dialog1.setCancelable(true);
                                dialog1.cancel();
                                dialog1 = null;
                            }
                            if( dialog2!=null){
                                dialog2.cancel();
                            }
                            if( dialog3!=null){
                                dialog3.cancel();
                            }
                            if( dialog4!=null){
                                dialog4.cancel();
                            }
                            if( dialog5!=null){
                                dialog5.cancel();
                            }
                            bt_iniciarCalibracionbool = true;
                            bt_resetbool = true;
                            btReajusteCerobool = true;

                        }
                    });



                int finalI = i;
                ArrayList<String> finalListerr = Listerr;


                Mensaje(finalListerr.get(finalI).toString(),R.layout.item_customtoasterror, activity);
                    //Toast.makeText(mainActivity.getApplicationContext(),finalListerr.get(finalI).toString(),Toast.LENGTH_SHORT).show();

            }}}


    private void initializeViews(View view) {
        bt_iniciarCalibracion = view.findViewById(R.id.btIniciarCalibracion);
        btReajusteCero = view.findViewById(R.id.btReajusteCero);
        bt_reset = view.findViewById(R.id.btreset);
        table_parametrosPrincipales = view.findViewById(R.id.TableParametrosprincipales);
        sp_divisionMinima = view.findViewById(R.id.spDivisionMinima);
        sp_puntoDecimal = view.findViewById(R.id.spPuntoDecimal);
        sp_puntoDecimal = view.findViewById(R.id.spPuntoDecimal);
        sp_unidad = view.findViewById(R.id.spUnidad);
        tv_capacidad = view.findViewById(R.id.tvCapacidad);

        Button btenviarparam = view.findViewById(R.id.btenviarparam);
        tv_pesoConocido = view.findViewById(R.id.tvPesoconocido);
        //-- NUEVO
        Button btSeteo= view.findViewById(R.id.btSeteo);
        toggle1 = view.findViewById(R.id.toggle1);
        OFF1 = view.findViewById(R.id.btOFF1);
        OFF2 = view.findViewById(R.id.btOFF2);
        OFF3 = view.findViewById(R.id.btOFF3);
        OFF4 = view.findViewById(R.id.btOFF4);
        OFF5 = view.findViewById(R.id.btOFF5);
        OFF6 = view.findViewById(R.id.btOFF6);
        OFF7 = view.findViewById(R.id.btOFF7);
        OFF8 = view.findViewById(R.id.btOFF8);
        OFF13 = view.findViewById(R.id.btOFF13);
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
         animbutton = view.findViewById(R.id.animbutton);
         togglediv = view.findViewById(R.id.togglediv);
         Lseteo = view.findViewById(R.id.Lseteo);
         imgseteo = view.findViewById(R.id.imgseteo);
         imgCal = view.findViewById(R.id.imgCal);
         Lcalibracion = view.findViewById(R.id.Lcalibracion);
         btCalibracion = view.findViewById(R.id.btCalibracion);
        if ( BZA.serialPort!= null) {
            PuertosSerie.PuertosSerieListener receiver = new PuertosSerie.PuertosSerieListener() {
                @Override
                public void onMsjPort(String data) {

                   // System.out.println("OPTIMA DataCal:"+data);
                    procesarMensaje(data);
                }
            };
            reader = new PuertosSerie.SerialPortReader( BZA.serialPort.getInputStream(), receiver);

            // Iniciar la lectura
            reader.startReading();


            // Para detener la lectura más tarde, puedes llamar a:
            // reader.stopReading();
        } else {
           // System.out.println("Optima: No se pudo abrir el puerto.");
        }

        btSeteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btSeteobool){
                    btSeteobool=false;
                    Runnable myRunnable = () -> {
                        try {
                            BZA.Pedirparam();
                            Thread.sleep(500);
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imgCal.setVisibility(View.GONE);
                                    Lcalibracion.setVisibility(View.GONE);
                                    imgseteo.setVisibility(View.VISIBLE);
                                    Lseteo.setVisibility(View.VISIBLE);
                                    collapseLinearLayout(table_parametrosPrincipales);
                                    animbutton.setRotation(0);
                                    btSeteobool=true;
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
                if(btCalibracionbool) {
                    btCalibracionbool = false;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imgCal.setVisibility(View.VISIBLE);
                            Lcalibracion.setVisibility(View.VISIBLE);
                            imgseteo.setVisibility(View.GONE);
                            Lseteo.setVisibility(View.GONE);
                            collapseLinearLayout(table_parametrosPrincipales);
                            animbutton.setRotation(0);
                            btCalibracionbool = true;
                        }
                    });

                }
                try {
                    BZA.abrircalib();
                    Thread.sleep(700);
                } catch (InterruptedException e) {

                }finally {
                    BZA.Pedirparam();
                }
            }
        });

        btenviarparam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(enviarparambool) {
                    enviarparambool=false;
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
                    View mView =activity.getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);

                     titulo = mView.findViewById(R.id.textViewt);
                     loadingPanel = mView.findViewById(R.id.loadingPanel);
//                     tvCarga = mView.findViewById(R.id.tvCarga);
                     Guardar = mView.findViewById(R.id.buttons);
                    Button Cancelar = mView.findViewById(R.id.buttonc);
                    Cancelar.setVisibility(View.GONE);
                    titulo.setText("Espere un momento...");

                    Runnable myRunnable = () -> {
                        //  System.out.println("OPTIMA EERRRR  ");

                        try {
                            ejecutarenviodeparametros();

                        } catch (Exception e) {

                        }
                    };
                    Thread myThread2 = new Thread(myRunnable);
                    myThread2.start();



                    mBuilder.setView(mView);
                    dialog3 = mBuilder.create();
                    dialog3.show();
                }

            }



    });


        togglediv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollapsed) {
                    expandLinearLayout(table_parametrosPrincipales);
                    animbutton.setRotation(180);
                } else {
                    collapseLinearLayout(table_parametrosPrincipales);
                    animbutton.setRotation(0);
                }
            }
        });
        animbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollapsed) {
                    expandLinearLayout(table_parametrosPrincipales);
                    animbutton.setRotation(180);
                } else {
                    collapseLinearLayout(table_parametrosPrincipales);
                    animbutton.setRotation(0);
                }
            }
        });

        sp_acu = view.findViewById(R.id.spacu);
        sp_off = view.findViewById(R.id.spoff);
        sp_pro = view.findViewById(R.id.sppro);
        sp_pro.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_acu.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_off.setPopupBackgroundResource(R.drawable.campollenarclickeable);

        String[] DivisionMin_arr = activity.getApplicationContext().getResources().getStringArray(R.array.DivisionMinima);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(activity.getApplicationContext(), R.layout.item_spinner, DivisionMin_arr);
        adapter2.setDropDownViewResource(R.layout.item_spinner);
        sp_divisionMinima.setAdapter(adapter2);

        String[] unidad_arr = activity.getApplicationContext().getResources().getStringArray(R.array.Unidad);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(activity.getApplicationContext(), R.layout.item_spinner, unidad_arr);
        adapter3.setDropDownViewResource(R.layout.item_spinner);
        sp_unidad.setAdapter(adapter3);

        String[] puntoDecimal_arr = activity.getApplicationContext().getResources().getStringArray(R.array.PositionDecimal);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(activity.getApplicationContext(), R.layout.item_spinner, puntoDecimal_arr);
        adapter4.setDropDownViewResource(R.layout.item_spinner);
        sp_puntoDecimal.setAdapter(adapter4);
        toggle1.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton bton= view.findViewById(R.id.btON1);
            RadioButton btoff = view.findViewById(R.id.btOFF1);
            if(toggle1.getCheckedRadioButtonId()==R.id.btON1) {
               btoff.setText("NO");
               btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
               bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));

                bton.setText("SI");
            } else {
                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));

                btoff.setText("NO");
                bton.setText("SI");
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

                bton.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.negro));
                btoff.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.blanco));

            }
        });
        sp_divisionMinima.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_puntoDecimal.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_unidad.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        tv_ultimaCalibracion=view.findViewById(R.id.tvUltimaCalibracion);

        tv_pesoConocido.setText( PreferencesDevicesManager.getPesoConocido(BZA.Nombre, BZA.numBza, activity));
        tv_capacidad.setText( PreferencesDevicesManager.getCapacidadMax(BZA.Nombre, BZA.numBza, activity));
        tv_ultimaCalibracion.setText(PreferencesDevicesManager.getUltimaCalibracion(BZA.Nombre,BZA.numBza, activity));
        if(Objects.equals( PreferencesDevicesManager.getUnidad(BZA.Nombre,BZA.numBza, activity), "ton")){
            sp_unidad.setSelection(2);
        }else if(Objects.equals( PreferencesDevicesManager.getUnidad(BZA.Nombre,BZA.numBza, activity), "gr")){
            sp_unidad.setSelection(0);
        }else{
            sp_unidad.setSelection(1);
        }
        sp_unidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        btReajusteCero.setOnClickListener(view -> { // SIN PROBAR
            if(btReajusteCerobool) {
                btReajusteCerobool = false;
                BZA.ReAjusteCero();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
                View mView =activity.getLayoutInflater().inflate(R.layout.dialogo_transferenciaarchivo, null);
                TextView textView = mView.findViewById(R.id.textView);
                textView.setText("Re ajustando...");
                mBuilder.setView(mView);
                dialog4 = mBuilder.create();
                dialog4.show();
            }
        });
        bt_reset.setOnClickListener(view -> {
            if(bt_resetbool) {
                bt_resetbool = false;
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);

                View mView =activity.getLayoutInflater().inflate(R.layout.dialogo_dossinet, null);
                TextView textView = mView.findViewById(R.id.textViewt);
                textView.setText("¿Esta seguro de resetear los parametros y la calibracion?");

                 Guardar = mView.findViewById(R.id.buttons);
                Button Cancelar = mView.findViewById(R.id.buttonc);
                Guardar.setText("Resetear");

                mBuilder.setView(mView);
                dialog5 = mBuilder.create();
                dialog5.show();

                Guardar.setOnClickListener(view1 -> {

                    AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(activity);
                    View mView1 =activity.getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);

                     titulo = mView1.findViewById(R.id.textViewt);
                     loadingPanel = mView1.findViewById(R.id.loadingPanel);
                     tvCarga = mView1.findViewById(R.id.tvCarga);
                    Button Guardar1 = mView1.findViewById(R.id.buttons);
                    Button Cancelar1 = mView1.findViewById(R.id.buttonc);
                    Cancelar1.setVisibility(View.INVISIBLE);
                    titulo.setText("Espere un momento...");
                    Guardar1.setClickable(false);
                    mBuilder1.setView(mView1);
                    dialog5.cancel();
                    dialog1 = mBuilder1.create();
                    dialog1.show();


                    BZA.reset();
                });

                Cancelar.setOnClickListener(view1 -> {
                    dialog1.cancel();
                    bt_resetbool=true;
                });


            }
        });
        bt_iniciarCalibracion.setOnClickListener(view12 -> {
            if(bt_iniciarCalibracionbool) {
                bt_iniciarCalibracionbool = false;
                String CapDivPDecimal =  BZA.CapacidadMax_DivMin_PDecimal(tv_capacidad.getText().toString(), sp_divisionMinima.getSelectedItem().toString(), String.valueOf(sp_puntoDecimal.getSelectedItemPosition()));
                try {
                    BZA.serialPort.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(CapDivPDecimal!=null){
                    inicioCalibracion(CapDivPDecimal);
                }else{
                    Mensaje("Revisa la capacidad, division minima y  el punto decimal",R.layout.item_customtoasterror, activity);
                    bt_iniciarCalibracionbool=true;
                }
            }
            });
            tv_capacidad.setOnClickListener(view110 -> Dialog(tv_capacidad, "", "Capacidad"));
            tv_pesoConocido.setOnClickListener(view112 -> Dialog(tv_pesoConocido, "", "Peso Conocido"));
            BZA.Pedirparam();
        }
    private void enviarpddiv(String CapDivPDecimal) throws InterruptedException {
        BZA.escribir(CapDivPDecimal,0);
        Thread.sleep(500);

      //  procesarerror(0,null,null);
    }
    private void inicioCalibracion(String CapDivPDecimal) {
       // System.out.println("initcal _> " + indiceCalibracion);
        final Boolean[] bt_guardarbool = {true};
        if(CapDivPDecimal!="ERRCONTROL")
        {
            BZA.escribir(CapDivPDecimal,BZA.numBza);
            PreferencesDevicesManager.setCapacidadMax(BZA.Nombre,BZA.numBza,tv_capacidad.getText().toString(), activity);//BZA.set_CapacidadMax(tv_capacidad.getText().toString());
            PreferencesDevicesManager.setDivisionMinima(BZA.Nombre,BZA.numBza,sp_divisionMinima.getSelectedItemPosition(), activity);// BZA.set_DivisionMinima(sp_divisionMinima.getSelectedItemPosition());
            PreferencesDevicesManager.setPuntoDecimal(BZA.Nombre,BZA.numBza,sp_puntoDecimal.getSelectedItemPosition(), activity);// //BZA.set_PuntoDecimal(sp_puntoDecimal.getSelectedItemPosition());

        }
         AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
        View mView =activity.getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);

        titulo=mView.findViewById(R.id.textViewt);
         loadingPanel=mView.findViewById(R.id.loadingPanel);
         tvCarga=mView.findViewById(R.id.tvCarga);
         Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        Cancelar.setVisibility(View.INVISIBLE);
        titulo.setText("");
       // Guardar.setClickable(false);

        mBuilder.setView(mView);
        dialog1 = mBuilder.create();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();


        Runnable myRunnable = () -> {
            try {

                if (CapDivPDecimal != "ERRCONTROL" ){//&& indiceCalibracion!=2){
                    Thread.sleep(1000);
                //Thread.sleep(2000);
                enviarpddiv(CapDivPDecimal);
                Thread.sleep(1000);
//                BZA.Guardar_cal();
            }

                activity.runOnUiThread(() -> {
                    if(CapDivPDecimal.equals("ERRCONTROL")){

                      //  System.out.println("ERRCALIB");
                        switch (indiceCalibracion){
                            case 1:
                                titulo.setText("Verifique que la balanza este en cero, luego presione \"SIGUIENTE\"");
                                loadingPanel.setVisibility(View.INVISIBLE);
                                tvCarga.setVisibility(View.INVISIBLE);
                                Guardar.setClickable(true);
                                break;
                            case 2:
                                TextView textView = titulo;
                                String textoCompleto = "Coloque el peso conocido ("+tv_pesoConocido.getText()+sp_unidad.getSelectedItem().toString()+") y luego presione \"SIGUIENTE\"";

                                SpannableStringBuilder builder = new SpannableStringBuilder(textoCompleto);
                                RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan(1.3f); // Aumenta el tamaño 1.5 veces
                                builder.setSpan(sizeSpan1, 25,("Coloque el peso conocido ("+tv_pesoConocido.getText()+sp_unidad.getSelectedItem().toString()+")").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Aplica al texto desde el índice 10 al 15
                                textView.setText(builder);
                              //  titulo.setText("Coloque el peso conocido y luego presione \"SIGUIENTE\"");
                                loadingPanel.setVisibility(View.INVISIBLE);
                                tvCarga.setVisibility(View.INVISIBLE);
                                Guardar.setClickable(true);
                                break;
                            case 3:
                                if(!lasttanque){
                                titulo.setText("Coloque el recero y luego presione \"SIGUIENTE\"");
                                loadingPanel.setVisibility(View.INVISIBLE);
                                tvCarga.setVisibility(View.INVISIBLE);
                                Guardar.setClickable(true);


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
                        titulo.setText("Verifique que la balanza este en cero, luego presione \"SIGUIENTE\"");
                        loadingPanel.setVisibility(View.INVISIBLE);
                        tvCarga.setVisibility(View.INVISIBLE);
                        Guardar.setClickable(true);
                    }
                    });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();

        Guardar.setOnClickListener(view -> {
            if(bt_guardarbool[0] && !titulo.getText().toString().toLowerCase(Locale.ROOT)
                    .contains("espere un momento") && !titulo.getText().toString().toLowerCase(Locale.ROOT).contains("cargando") && loadingPanel.getVisibility()!=View.VISIBLE ){
                bt_guardarbool[0] =false;
                switch (indiceCalibracion) {
                    case 1:

                        ejecutarCalibracionCero(Guardar,titulo,loadingPanel,tvCarga,dialog1);
                        break;
                    case 2:
                        ejecutarCalibracionPesoConocido(Guardar,titulo,loadingPanel,tvCarga, dialog1);
                        break;
                    case 3:
                        if(!lasttanque){
                            ejecutarCalibracionRecero(Guardar, titulo, loadingPanel, tvCarga, dialog1);

                        }else {
                            indiceCalibracion=1;

                            dialog1.setCancelable(true);
                            dialog1.cancel();

                        }
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
        private void ejecutarCalibracionRecero(Button Guardar, TextView titulo, ProgressBar loadingPanel, TextView tvCarga,AlertDialog dialog) {
            BZA.Recero_cal();
        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);
    }
    private void ejecutarCalibracionPesoConocido(Button Guardar,TextView titulo,ProgressBar loadingPanel,TextView tvCarga,AlertDialog dialog) {
        String msj = BZA.Peso_conocido(tv_pesoConocido.getText().toString(),String.valueOf(sp_puntoDecimal.getSelectedItemPosition()));//,tv_capacidad.getText().toString()
        if(msj!=null){
            BZA.escribir(msj,0);
        }else{
            dialog1.cancel();
            bt_iniciarCalibracionbool=true;
            Mensaje("Error, peso conocido fuera de rango de acuerdo a Capacidad/punto decimal elegida", R.layout.item_customtoasterror, activity);
        }
        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);
        PreferencesDevicesManager.setPesoConocido(BZA.Nombre,BZA.numBza,tv_pesoConocido.getText().toString(), activity);//BZA.set_PesoConocido(tv_pesoConocido.getText().toString());
    }
    private void ejecutarenviodeparametros( ) {
        Runnable myRunnable = () -> {
            try {
                  String param1="";
                param1 += leertoggles(toggle1,R.id.btON1);
                param1 += leertoggles(toggle2,R.id.btON2);// "0";
                param1 += leertoggles(toggle3,R.id.btON3);
                param1 +=leertoggles(toggle4,R.id.btON4); //"0";
                lasttanque= (leertoggles(toggle3, R.id.btON3).equals("1"));
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
                BZA.EnviarParametros(param1,param2,sp_pro.getSelectedItem().toString(),sp_off.getSelectedItem().toString(),sp_acu.getSelectedItem().toString());
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        if(!stoped){

        }
        Thread myThread = new Thread(myRunnable);
        myThread.start();
    }
    private void ejecutarCalibracionCero(Button Guardar,TextView titulo,ProgressBar loadingPanel,TextView tvCarga,AlertDialog dialog) {
        BZA.Cero_cal();
       // Guardar.setClickable(false);
        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);
    }

    public void Dialog(TextView textView,String string,String Texto){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
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
         titulo=mView.findViewById(R.id.textViewt);
        titulo.setText(Texto);
        if(string.equals("Ceroinicial")){
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
            userInput.requestFocus();
        }
        else{
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER );
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
        dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Texto.equals("Peso Conocido")){
                    if(BZA.Peso_conocido(userInput.getText().toString(),String.valueOf(sp_puntoDecimal.getSelectedItemPosition()))!=null){ // ,tv_capacidad.getText().toString()
                     textView.setText(userInput.getText().toString());
                   }else{
                        Mensaje("Error, peso conocido fuera de rango de acuerdo a Capacidad/punto decimal elegida", R.layout.item_customtoasterror, activity);
                    }
                } else{
                    textView.setText(userInput.getText().toString());
                  //  System.out.println("SETTEXT WOW2");
                }



                dialog.cancel();
            }
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    @Override
    public void onDestroyView() {
        stoped=true;
        dialog=null;
        dialog1=null;
        dialog2=null;
        dialog3=null;
        dialog4=null;
        dialog5=null;
        super.onDestroyView();
    }



}


