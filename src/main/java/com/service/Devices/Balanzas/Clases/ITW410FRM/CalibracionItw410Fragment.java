package com.service.Devices.Balanzas.Clases.ITW410FRM;

import static com.service.Utils.Mensaje;

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
import android.widget.Spinner;
import android.widget.TextView;

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
import com.service.PreferencesDevicesManager;
import com.service.R;
import com.service.Utils;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalibracionItw410Fragment extends Fragment {
    static AppCompatActivity activity;
    static ITW410_FORM BZA;
    private ButtonProvider buttonProvider;
    ExecutorService thread = Executors.newFixedThreadPool(2);

    //    int subnombre=0;
    TextView titulo;
    ProgressBar loadingPanel;
    TextView tvCarga;
    Button Guardar;
    BalanzaService Service;
    //    public float pesoUnitario=0.5F,pesoBandaCero=0F,taraDigital=0,Bruto=0,Tara=0,Neto=0,pico=0;
    public String read,estado= BalanzaBase.M_MODO_CALIBRACION,estable=""/*,ultimaCalibracion="",brutoStr="0",netoStr="0",taraStr="0",taraDigitalStr="0",picoStr="0"*/;
    public Boolean isCollapsed = false,stoped=false,inicioBandaPeso=false,lasttanque = true,bandaCero =true,btSeteobool=true,bt_homebool=true,bt_resetbool=true,btCalibracionbool=true,enviarparambool=true,bt_iniciarCalibracionbool=true,btReajusteCerobool=true;
    public int initialWidth = 258,puntoDecimal=1,/*acumulador=0,*/ numero=1,indiceCalibracion=1;
    ImageView animbutton,imgCal;
    LinearLayout togglediv,Lcalibracion;
    Button bt_iniciarCalibracion,btReajusteCero,btCalibracion,bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    AlertDialog dialog, dialog1;
    Spinner /*sp_bps,sp_off,sp_pro,sp_acu,sp_bf1,sp_dat,sp_ano,sp_uni,sp_reg,sp_bot,*/sp_unidad,sp_divisionMinima,sp_puntoDecimal;
    TextView tv_pesoConocido,tv_ultimaCalibracion,tv_filtros1,tv_filtros2,tv_filtros3;
//    RadioGroup toggle1,toggle2,toggle3,toggle4,toggle5,toggle6,toggle7,toggle8;
//    RadioButton OFF1,OFF2,OFF3,OFF4,OFF5,OFF6,OFF7,OFF8, ON1,ON2,ON3,ON4,ON5,ON6,ON7,ON8;
    ConstraintLayout table_parametrosPrincipales;
    View viewMang=null;
//    private OnFragmentChangeListener fragmentChangeListener;

    public static CalibracionItw410Fragment newInstance(ITW410_FORM instance, BalanzaService service) {
        CalibracionItw410Fragment fragment = new CalibracionItw410Fragment();
        Bundle args = new Bundle();
        args.putSerializable("instance", instance);
        args.putSerializable("instanceService", service);
        fragment.setArguments(args);
        return fragment;
    }
//    public void setFragmentChangeListener(OnFragmentChangeListener listener) {
//        this.fragmentChangeListener = listener;
//    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         viewMang = inflater.inflate(R.layout.standar_calibracion_v2_410,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        estado=BalanzaBase.M_MODO_CALIBRACION;
        if (getArguments() != null) {
            BZA = (ITW410_FORM) getArguments().getSerializable("instance");
            BZA.Estado =BalanzaBase.M_MODO_CALIBRACION;
            Service = BalanzaService.getInstance();// (BalanzaService) getArguments().getSerializable("instanceService");
                activity = ComService.getInstance().activity;
        }
        return viewMang;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        configuracionBotones();
        initializeViews(view);
        setClickListeners();


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

            bt_home.setOnClickListener(view1 -> {
                if(bt_homebool){
                    bt_homebool=false;
                    thread.execute(new Runnable() {
                        @Override
                        public void run() {
                    try {
                                estado= BalanzaBase.M_MODO_BALANZA;
                                 BZA.Guardar_cal();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                                        View mView = getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);

                                         titulo=mView.findViewById(R.id.textViewt);
                                         loadingPanel=mView.findViewById(R.id.loadingPanel);
                                         tvCarga=mView.findViewById(R.id.tvCarga);
                                         Guardar =  mView.findViewById(R.id.buttons);
                                        Button Cancelar =  mView.findViewById(R.id.buttonc);
                                        Cancelar.setVisibility(View.INVISIBLE);
                                        Guardar.setVisibility(View.INVISIBLE);
                                        titulo.setText("espere un momento...");
                                        mBuilder.setView(mView);
                                        dialog = mBuilder.create();
                                        dialog.show();
                                    }
                                });
                                Thread.sleep(2000) ;
                                if(!stoped){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            estado= BalanzaBase.M_MODO_BALANZA;
                                           // BZA.setPesoUnitario( BZA.getPesoUnitario());
                                            BZA.Estado =  BalanzaBase.M_MODO_BALANZA;
                                            BZA.salir_cal();
                                            bt_homebool=true;
                                            dialog.cancel();
                                            ComService.getInstance().openServiceFragment();

                                        }
                                    });
                                }


                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
            });

        }
    });
        }
    }
   private void initializeViews(View view) {
        bt_iniciarCalibracion = view.findViewById(R.id.btIniciarCalibracion);
        btReajusteCero = view.findViewById(R.id.btReajusteCero);
        table_parametrosPrincipales = view.findViewById(R.id.TableParametrosprincipales);
        sp_divisionMinima = view.findViewById(R.id.spDivisionMinima);
        sp_puntoDecimal = view.findViewById(R.id.spPuntoDecimal);
        sp_puntoDecimal = view.findViewById(R.id.spPuntoDecimal);
        sp_unidad = view.findViewById(R.id.spUnidad);
        tv_pesoConocido = view.findViewById(R.id.tvPesoconocido);
        tv_filtros1 = view.findViewById(R.id.tv_filtro1X);
        tv_filtros2 = view.findViewById(R.id.tv_filtro2X);
        tv_filtros3 = view.findViewById(R.id.tv_filtro3X);
        //-- NUEVO
         animbutton = view.findViewById(R.id.animbutton);
         togglediv = view.findViewById(R.id.togglediv);
         imgCal = view.findViewById(R.id.imgCal);
         Lcalibracion = view.findViewById(R.id.Lcalibracion);
         btCalibracion = view.findViewById(R.id.btCalibracion);
        collapseLinearLayout(table_parametrosPrincipales);
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
       
        String[] DivisionMin_arr = requireContext().getResources().getStringArray(R.array.DivisionMinima);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, DivisionMin_arr);
        adapter2.setDropDownViewResource(R.layout.item_spinner);
        sp_divisionMinima.setAdapter(adapter2);

        String[] unidad_arr = requireContext().getResources().getStringArray(R.array.Unidad);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, unidad_arr);
        adapter3.setDropDownViewResource(R.layout.item_spinner);
        sp_unidad.setAdapter(adapter3);

        String[] puntoDecimal_arr = requireContext().getResources().getStringArray(R.array.PositionDecimal);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, puntoDecimal_arr);
        adapter4.setDropDownViewResource(R.layout.item_spinner);
        sp_puntoDecimal.setAdapter(adapter4);
        sp_divisionMinima.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_puntoDecimal.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_puntoDecimal.setSelection(PreferencesDevicesManager.getPuntoDecimal(BZA.Nombre,BZA.numBza,activity));
        sp_puntoDecimal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PreferencesDevicesManager.setPuntoDecimal(BZA.Nombre,BZA.numBza,position,activity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sp_unidad.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        tv_ultimaCalibracion=view.findViewById(R.id.tvUltimaCalibracion);
        tv_pesoConocido.setText( BZA.get_PesoConocido());
         tv_ultimaCalibracion.setText( PreferencesDevicesManager.getUltimaCalibracion(BZA.Nombre,BZA.numBza,activity));
        if(Objects.equals( PreferencesDevicesManager.getUnidad(BZA.Nombre,BZA.numBza,activity), "ton")){
            sp_unidad.setSelection(2);
        }else if(Objects.equals( PreferencesDevicesManager.getUnidad(BZA.Nombre,BZA.numBza,activity), "gr")){
            sp_unidad.setSelection(0);
        }else{
            sp_unidad.setSelection(1);
        }
        sp_unidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    PreferencesDevicesManager.setUnidad(BZA.Nombre,BZA.numBza,"gr", activity);
                }
                if(i==1){
                    PreferencesDevicesManager.setUnidad(BZA.Nombre,BZA.numBza,"kg", activity);
                }
                if(i==2){
                    PreferencesDevicesManager.setUnidad(BZA.Nombre,BZA.numBza,"ton", activity);
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

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialogo_transferenciaarchivo, null);
                TextView textView = mView.findViewById(R.id.textView);
                textView.setText("Re ajustando...");
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
                thread.execute(new Runnable() {
                    @Override
                    public void run() {
                        BZA.Recero_cal();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                btReajusteCerobool = true;
                                dialog.cancel();

                            }
                        });
                    }
                });
            }
        });
        bt_iniciarCalibracion.setOnClickListener(view12 -> {
            if(bt_iniciarCalibracionbool) {
                bt_iniciarCalibracionbool = false;
                String CapDivPDecimal =  "";
                if(CapDivPDecimal!=null){
                    inicioCalibracion(CapDivPDecimal);
                    PreferencesDevicesManager.setPuntoDecimal(BZA.Nombre,BZA.numBza,sp_puntoDecimal.getSelectedItemPosition(),activity);
                }else{
                    Mensaje("Revisa la capacidad, division minima y  el punto decimal",R.layout.item_customtoasterror, activity);
                    bt_iniciarCalibracionbool=true;
                }
            }
            });
        final ArrayList<String>[] listdat = new ArrayList[1];
        thread.execute(new Runnable() {
            @Override
            public void run() {
                puntoDecimal= PreferencesDevicesManager.getPuntoDecimal(BZA.Nombre,BZA.numBza,activity);
                ArrayList<String> listdata=   BZA.Pedirparam();
                try {
                tv_pesoConocido.setText(BZA.format(numero,String.valueOf(BZA.formatpuntodec(Integer.parseInt(listdata.get(0))))));
                tv_filtros1.setText(listdata.get(2));
                tv_filtros2.setText(listdata.get(3));
                tv_filtros3.setText(listdata.get(4));
                listdat[0] =listdata;
                switch (listdata.get(1)){
                    case "1":{
                        sp_divisionMinima.setSelection(0);
                        break;
                    }
                    case "2":{
                        sp_divisionMinima.setSelection(1);
                        break;
                    }
                    case "5":{
                        sp_divisionMinima.setSelection(2);
                        break;
                    }
                    default:{
                        //none
                    }
                } }catch(Exception e){
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.Mensaje("Error al leer los datos",R.layout.item_customtoasterror, activity);
                        }});
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tv_pesoConocido.setOnClickListener(view112 ->
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialog(tv_pesoConocido, listdat[0] .get(0), "Peso Conocido");
                                    }
                                })
                        );


                       
                        tv_filtros1.setOnClickListener(view112 -> activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Dialog(tv_filtros1, listdat[0] .get(2), "Filtro 1");
                            }
                        }));
                        tv_filtros2.setOnClickListener(view112 -> activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Dialog(tv_filtros2, listdat[0] .get(3), "Filtro 2");
                            }
                        }));

                      
                        tv_filtros3.setOnClickListener(view112 ->
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialog(tv_filtros3, listdat[0].get(4), "Filtro 3");
                                    }
                                })
                        );
                    }

                });
            }
            });



    }

    private void inicioCalibracion(String CapDivPDecimal) {
        indiceCalibracion=1;
        final Boolean[] bt_guardarbool = {true};
        if(CapDivPDecimal!="ERRCONTROL")
        {
            PreferencesDevicesManager.setDivisionMinima(BZA.Nombre,BZA.numBza,sp_divisionMinima.getSelectedItemPosition(),activity);
            PreferencesDevicesManager.setPuntoDecimal(BZA.Nombre,BZA.numBza,sp_puntoDecimal.getSelectedItemPosition(),activity);
        }
         AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);
        titulo=mView.findViewById(R.id.textViewt);
         loadingPanel=mView.findViewById(R.id.loadingPanel);
         tvCarga=mView.findViewById(R.id.tvCarga);
         Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        Cancelar.setVisibility(View.INVISIBLE);
        titulo.setText("");
        mBuilder.setView(mView);
        dialog1 = mBuilder.create();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        Runnable myRunnable = () -> {
            try {
                ArrayList<Integer> Listavals = new ArrayList<>();
                Listavals.add(Integer.parseInt(sp_divisionMinima.getSelectedItem().toString()));
                Listavals.add(Integer.parseInt(BZA.format(numero,tv_pesoConocido.getText().toString()).replace(".","")));
                Listavals.add(Integer.parseInt(tv_filtros1.getText().toString()));
                Listavals.add(Integer.parseInt(tv_filtros2.getText().toString()));
                Listavals.add(Integer.parseInt(tv_filtros3.getText().toString()));
                BZA.enviarParametros(Listavals); // //Division minim,Pesoconocido,filtro1,filtro2,filtro3 5vals

                activity.runOnUiThread(() -> {
               titulo.setText("Verifique que la balanza este en cero, luego presione \"SIGUIENTE\"");
                        loadingPanel.setVisibility(View.INVISIBLE);
                        tvCarga.setVisibility(View.INVISIBLE);
                        Guardar.setClickable(true);
                    });
            } catch (Exception e) {
                e.printStackTrace();
                Utils.Mensaje("Peso conocido invalido, Vuelva a intentarlo",R.layout.item_customtoasterror, activity);
            }
        };
        Thread myThread = new Thread(myRunnable);
        myThread.start();
        
        Guardar.setOnClickListener(view -> {
            if(bt_guardarbool[0] && !titulo.getText().toString().toLowerCase(Locale.ROOT).contains("espere un momento") && !titulo.getText().toString().toLowerCase(Locale.ROOT).contains("cargando") && loadingPanel.getVisibility()!=View.VISIBLE ){
                bt_guardarbool[0] =false;
                switch (indiceCalibracion) {
                    case 1:
                        ejecutarCalibracionCero(Guardar,titulo,loadingPanel,tvCarga,dialog1);
                        break;
                    case 2:
                        ejecutarCalibracionPesoConocido(Guardar,titulo,loadingPanel,tvCarga, dialog1);
                        break;
                    case 3:
                            ejecutarCalibracionRecero(Guardar, titulo, loadingPanel, tvCarga, dialog1);
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
       // System.out.println("ITW 410 recero");
            indiceCalibracion = 1;
        BZA.setRecerocal();

        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_ultimaCalibracion.setText(Utils.DevuelveFecha() + " " + Utils.DevuelveHora());
                    PreferencesDevicesManager.setUltimaCalibracion(BZA.Nombre,BZA.numBza,Utils.DevuelveFecha() + " " + Utils.DevuelveHora(),activity);
                }
            });
            if (!stoped) {
                try {
                    activity.runOnUiThread(() -> {
                        BZA.Guardar_cal();
                        bt_iniciarCalibracionbool = true;

                        if (dialog1 != null) {
                            dialog1.setCancelable(true);
                            dialog1.cancel();
                        }
                    });
                } catch (Exception e) {
                }
            }
    }

    private void ejecutarCalibracionPesoConocido(Button Guardar,TextView titulo,ProgressBar loadingPanel,TextView tvCarga,AlertDialog dialog) {
        String msj = BZA.format(numero,tv_pesoConocido.getText().toString());//,tv_capacidad.getText().toString()
        if(msj!=null){
            BZA.setSpancal();
            titulo.setText("");
            loadingPanel.setVisibility(View.VISIBLE);
            tvCarga.setVisibility(View.VISIBLE);
            PreferencesDevicesManager.setPesoConocido(BZA.Nombre,BZA.numBza,tv_pesoConocido.getText().toString(), activity);
            activity.runOnUiThread(() -> {
                try {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            titulo.setText("Coloque el recero y luego presione \"SIGUIENTE\"");
                            loadingPanel.setVisibility(View.INVISIBLE);
                            tvCarga.setVisibility(View.INVISIBLE);
                            tv_ultimaCalibracion.setText(Utils.DevuelveFecha() + " " + Utils.DevuelveHora());
                            PreferencesDevicesManager.setUltimaCalibracion(BZA.Nombre,BZA.numBza,Utils.DevuelveFecha() + " " + Utils.DevuelveHora(),activity);
                        }
                    });
                } catch (Exception e) {
                }
            });
        }else{
            dialog1.cancel();
            bt_iniciarCalibracionbool=true;
            Mensaje("Error, peso conocido fuera de rango de acuerdo a Capacidad/punto decimal elegida", R.layout.item_customtoasterror, activity);
        }
    }
    private void ejecutarCalibracionCero(Button Guardar,TextView titulo,ProgressBar loadingPanel,TextView tvCarga,AlertDialog dialog) {
     //   System.out.println("ITW 410 cero");
        BZA.setCerocal();
        activity.runOnUiThread(() -> {
            TextView textView = titulo;
            String textoCompleto = "Coloque el peso conocido (" + tv_pesoConocido.getText() + sp_unidad.getSelectedItem().toString() + ") y luego presione \"SIGUIENTE\"";
            SpannableStringBuilder builder = new SpannableStringBuilder(textoCompleto);
            RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan(1.3f); // Aumenta el tamaño 1.5 veces
            builder.setSpan(sizeSpan1, 25, ("Coloque el peso conocido (" + tv_pesoConocido.getText() + sp_unidad.getSelectedItem().toString() + ")").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Aplica al texto desde el índice 10 al 15
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_ultimaCalibracion.setText(Utils.DevuelveFecha() + " " + Utils.DevuelveHora());
                    PreferencesDevicesManager.setUltimaCalibracion(BZA.Nombre,BZA.numBza,Utils.DevuelveFecha() + " " + Utils.DevuelveHora(),activity);
                }
            });
            textView.setText(builder);
            if (loadingPanel != null) {

                loadingPanel.setVisibility(View.INVISIBLE);
                tvCarga.setVisibility(View.INVISIBLE);

            }
            //   Guardar.setClickable(true);
        });


    }

    public void Dialog(TextView textView,String string,String Texto){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopciones_i, null);
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
            if(Texto.equals("Peso Conocido")|| Texto.toLowerCase().contains("filtro") ){ // Peso conocido con coma
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
                    if(BZA.format(numero,userInput.getText().toString())!=null){ // ,tv_capacidad.getText().toString()
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
        super.onDestroyView();
    }



}


