package com.service.Devices.Balanzas.Clases.ITW410;


import static com.service.utilsPackage.Utils.Mensaje;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.AsyncTask;
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

import com.service.PHService;
import com.service.utilsPackage.ComService;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import com.service.Comunicacion.GestorRecursos;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.R;
import com.service.utilsPackage.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class calibracionitw410Fragment extends Fragment {
    static AppCompatActivity mainActivity;
    static ITW4102Bzas BZA;
    private ButtonProvider buttonProvider;
    int subnombre=0;
    TextView titulo;
    ProgressBar loadingPanel;
    TextView tvCarga;
    Button Guardar;
   ArrayList<String> listfiltro1;
   ArrayList<String> DivisionMin_arr;
    PHService Service;
    ExecutorService thread = Executors.newFixedThreadPool(2);

    public static final String M_ERROR_COMUNICACION="M_ERROR_COMUNICACION",M_MODO_CALIBRACION="MODO_CALIBRACION",M_MODO_BALANZA="MODO_BALANZA",M_VERIFICANDO_MODO="VERIFICANDO_MODO";
    public float pesoUnitario=0.5F,pesoBandaCero=0F,taraDigital=0,Bruto=0,Tara=0,Neto=0,pico=0;
    public String read,estado=M_MODO_CALIBRACION,estable="",ultimaCalibracion="",brutoStr="0",netoStr="0",taraStr="0",taraDigitalStr="0",picoStr="0";
    public Boolean isCollapsed = false,stoped=false,inicioBandaPeso=false,lasttanque = true,bandaCero =true,btSeteobool=true,bt_homebool=true,bt_resetbool=true,btCalibracionbool=true,enviarparambool=true,bt_iniciarCalibracionbool=true,btReajusteCerobool=true;
    public int initialWidth = 258,puntoDecimal=1,acumulador=0, numero=1,indiceCalibracion=1;
    ImageView animbutton,imgCal;
    LinearLayout togglediv,Lcalibracion;
    Button bt_iniciarCalibracion,btReajusteCero,btCalibracion,bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    AlertDialog dialog, dialog1;
    Spinner sp_bps,sp_off,sp_pro,sp_acu,sp_bf1,sp_dat,sp_ano,sp_uni,sp_reg,sp_bot,sp_unidad,sp_divisionMinima,sp_puntoDecimal,tv_filtros1,tv_filtros2,tv_filtros3;
    TextView tv_pesoConocido,tv_ultimaCalibracion,tv_capacidad;
    RadioGroup toggle1,toggle2,toggle3,toggle4,toggle5,toggle6,toggle7,toggle8;
    RadioButton OFF1,OFF2,OFF3,OFF4,OFF5,OFF6,OFF7,OFF8, ON1,ON2,ON3,ON4,ON5,ON6,ON7,ON8;
    ConstraintLayout table_parametrosPrincipales;
    View viewMang=null;
    private OnFragmentChangeListener fragmentChangeListener;

    public static calibracionitw410Fragment newInstance(ITW4102Bzas instance, PHService service) {
        calibracionitw410Fragment fragment = new calibracionitw410Fragment();
        Bundle args = new Bundle();
        args.putSerializable("instance", instance);
        args.putSerializable("instanceService", service);

        fragment.setArguments(args);
        return fragment;
    }
    public void setFragmentChangeListener(OnFragmentChangeListener listener) {
        this.fragmentChangeListener = listener;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewMang = inflater.inflate(R.layout.standarcal_v410,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        estado=M_MODO_CALIBRACION;
        if (getArguments() != null) {
            BZA = (ITW4102Bzas) getArguments().getSerializable("instance");
            BZA.Estado=M_MODO_CALIBRACION;
            Service = (PHService) getArguments().getSerializable("instanceService");
            mainActivity = ComService.getInstance().activity;
        }
        return viewMang;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        configuracionBotones();
       // BalanzaService.getInstance().Balanzas.shutdown(); DEBO USAR GESTOR DE RECURSOS
        GestorRecursos.getinstance().shutdown(BZA.serialport.get_Puerto());
        listfiltro1 = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.Filtros410)));
        DivisionMin_arr= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.DivisionMinima2)));
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
                    thread.execute(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                estado= BZA.M_MODO_BALANZA;
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
                                enviarpddiv();
                                BZA.Guardar_cal();
                                if(!stoped){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            estado= BZA.M_MODO_BALANZA;
                                            BZA.setPesoUnitario( BZA.getPesoUnitario());
                                            BZA.Estado =  BZA.M_MODO_BALANZA;
                                            BZA.salir_cal();
                                            //ComService.getInstance().Balanzas.unshutdown(); USAR BRRRRRRRRRR
                                            GestorRecursos.getinstance().unshutdown(BZA.serialport.get_Puerto());

                                            bt_homebool=true;
                                            dialog.cancel();
                                            try {
                                                dialog1.cancel();
                                            } catch (Exception e) {

                                            }
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
    private String DevuelveHora(){
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
    private String DevuelveFecha(){
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
    private void initializeViews(View view) {
        bt_iniciarCalibracion = view.findViewById(R.id.btIniciarCalibracion);
        btReajusteCero = view.findViewById(R.id.btReajusteCero);
        table_parametrosPrincipales = view.findViewById(R.id.TableParametrosprincipales);
        sp_divisionMinima = view.findViewById(R.id.spDivisionMinima);
        sp_puntoDecimal = view.findViewById(R.id.spPuntoDecimal);
        sp_puntoDecimal = view.findViewById(R.id.spPuntoDecimal);
        tv_capacidad = view.findViewById(R.id.tvCapacidad);
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
        BZA.write(BZA.set_cal_Cero_inicial("100"));
        try{
            tv_capacidad.setText(BZA.get_CapacidadMax());
        }catch (Exception e){

        }

        try {
            tv_filtros1.setSelection(listfiltro1.indexOf(BZA.get_filtro1()));
        }catch (Exception e){

        }
        try {
            tv_filtros2.setSelection(listfiltro1.indexOf(BZA.get_filtro2()));
        }catch (Exception e){

        }
        try {
            tv_filtros3.setSelection(listfiltro1.indexOf(BZA.get_filtro3()));
        }catch (Exception e){

        }
        tv_capacidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog(tv_capacidad,"Capacidad","Capacidad");
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
        ArrayAdapter<String> adapter6 = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, listfiltro1);
        adapter6.setDropDownViewResource(R.layout.item_spinner);
        tv_filtros1.setAdapter(adapter6);
        ArrayAdapter<String> adapter7 = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, listfiltro1);
        adapter7.setDropDownViewResource(R.layout.item_spinner);
        tv_filtros2.setAdapter(adapter7);
        ArrayAdapter<String> adapter8 = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, listfiltro1);
        adapter8.setDropDownViewResource(R.layout.item_spinner);
        tv_filtros3.setAdapter(adapter8);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, DivisionMin_arr);
        adapter2.setDropDownViewResource(R.layout.item_spinner);
        sp_divisionMinima.setAdapter(adapter2);

        String[] unidad_arr = getResources().getStringArray(R.array.Unidad);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, unidad_arr);
        adapter3.setDropDownViewResource(R.layout.item_spinner);
        sp_unidad.setAdapter(adapter3);

        ArrayList<String> puntoDecimal_arr= new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.PuntoDecimal2)));
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, puntoDecimal_arr);
        adapter4.setDropDownViewResource(R.layout.item_spinner);
        sp_puntoDecimal.setAdapter(adapter4);
        sp_divisionMinima.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_puntoDecimal.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_puntoDecimal.setSelection(BZA.get_PuntoDecimal());
        sp_puntoDecimal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BZA.set_PuntoDecimal(Integer.parseInt(puntoDecimal_arr.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        sp_unidad.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        tv_ultimaCalibracion=view.findViewById(R.id.tvUltimaCalibracion);
        tv_pesoConocido.setText( BZA.get_PesoConocido());
        tv_ultimaCalibracion.setText( BZA.get_UltimaCalibracion());
        if(Objects.equals( BZA.getUnidad(), "ton")){
            sp_unidad.setSelection(2);
        }else if(Objects.equals( BZA.getUnidad(), "gr")){
            sp_unidad.setSelection(0);
        }else{
            sp_unidad.setSelection(1);
        }
        sp_unidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    BZA.setUnidad("gr");
                }
                if(i==1){
                    BZA.setUnidad("kg");
                }
                if(i==2){
                    BZA.setUnidad("ton");
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
                        mainActivity.runOnUiThread(new Runnable() {
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
                }else{
                    Mensaje("Revisa la capacidad, division minima y  el punto decimal",R.layout.item_customtoasterror, ComService.getInstance().activity);
                    bt_iniciarCalibracionbool=true;
                }
            }
        });
        final ArrayList<String>[] listdat = new ArrayList[1];
        thread.execute(new Runnable() {
            @Override
            public void run() {
                puntoDecimal= BZA.get_PuntoDecimal();
                try {
                    tv_pesoConocido.setText(BZA.get_PesoConocido());
                    tv_filtros1.setSelection(listfiltro1.indexOf(BZA.get_filtro1()));
                    tv_filtros2.setSelection(listfiltro1.indexOf(BZA.get_filtro2()));
                    tv_filtros3.setSelection(listfiltro1.indexOf(BZA.get_filtro3()));
                    sp_divisionMinima.setSelection(DivisionMin_arr.indexOf(String.valueOf(BZA.get_DivisionMinima())));
                }catch(Exception e){
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.Mensaje("Error al leer los datos",R.layout.item_customtoasterror,mainActivity);
                        }});
                }
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tv_pesoConocido.setOnClickListener(view112 ->
                                mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialog(tv_pesoConocido, "Peso Conocido", "Peso Conocido");
                                    }
                                })
                        );
                    }
                });
            }
        });



    }
    private void enviarpddiv() throws InterruptedException {

        ArrayList<String> Listavals = new ArrayList<>();
        Listavals.add(sp_divisionMinima.getSelectedItem().toString());
        Listavals.add(tv_pesoConocido.getText().toString().replace(".",""));
        Listavals.add(tv_capacidad.getText().toString().replace(".",""));
        Listavals.add(String.valueOf(tv_filtros1.getSelectedItem()));
        Listavals.add(String.valueOf(tv_filtros2.getSelectedItem()));
        Listavals.add(String.valueOf(tv_filtros3.getSelectedItem()));
        BZA.enviarParametros(Listavals);
    }
    @SuppressLint("StaticFieldLeak")
    private void inicioCalibracion(String CapDivPDecimal) {
        indiceCalibracion=1;
        final Boolean[] bt_guardarbool = {true};

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
        Guardar.setOnClickListener(view -> {
            try {
                if (bt_guardarbool[0] && !titulo.getText().toString().toLowerCase(Locale.ROOT).contains("espere un momento") && !titulo.getText().toString().toLowerCase(Locale.ROOT).contains("cargando") && loadingPanel.getVisibility() != View.VISIBLE) {
                    bt_guardarbool[0] = false;
                    switch (indiceCalibracion) {
                        case 1:
                            ejecutarCalibracionCero(Guardar, titulo, loadingPanel, tvCarga, dialog1);
                            break;
                        case 2:
                            ejecutarCalibracionPesoConocido(Guardar, titulo, loadingPanel, tvCarga, dialog1);
                            break;
                        case 3:
                            ejecutarCalibracionRecero(Guardar, titulo, loadingPanel, tvCarga, dialog1);
                            break;

                        default:
                            break;
                    }
                    indiceCalibracion++;
                    bt_guardarbool[0] = true;
                }
            }catch (Exception e){

            }
        });
        Cancelar.setOnClickListener(view -> dialog1.cancel());
        dialog1.show();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    BZA.set_DivisionMinima(Integer.parseInt(sp_divisionMinima.getSelectedItem().toString()) );
                    BZA.set_PuntoDecimal(Integer.parseInt(sp_puntoDecimal.getSelectedItem().toString()) );
                    enviarpddiv();
                } catch (InterruptedException e) {
                    Mensaje("ocurrio un error al mandar los parametros, vuelva a intentarlo",R.layout.item_customtoasterror,mainActivity);
                    dialog1.cancel();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                titulo.setText("Verifique que la balanza esté en cero...");
                loadingPanel.setVisibility(View.INVISIBLE);
                tvCarga.setVisibility(View.INVISIBLE);
                Guardar.setClickable(true);
            }
        }.execute();

    }
    private void ejecutarCalibracionRecero(Button Guardar, TextView titulo, ProgressBar loadingPanel, TextView tvCarga,AlertDialog dialog) {
        System.out.println("ITW 410 recero");
        indiceCalibracion = 1;
        BZA.setRecerocal();

        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_ultimaCalibracion.setText(DevuelveFecha() + " " + DevuelveHora());
                BZA.set_UltimaCalibracion(DevuelveFecha() + " " + DevuelveHora());
            }
        });
        if (!stoped) {
            try {
                getActivity().runOnUiThread(() -> {
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
        String msj = tv_pesoConocido.getText().toString();//,tv_capacidad.getText().toString()
        if(msj!=null){
            BZA.setSpancal();
            titulo.setText("");
            loadingPanel.setVisibility(View.VISIBLE);
            tvCarga.setVisibility(View.VISIBLE);
            BZA.set_PesoConocido(tv_pesoConocido.getText().toString());
            getActivity().runOnUiThread(() -> {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            titulo.setText("Coloque el recero y luego presione \"SIGUIENTE\"");
                            loadingPanel.setVisibility(View.INVISIBLE);
                            tvCarga.setVisibility(View.INVISIBLE);
                            tv_ultimaCalibracion.setText(DevuelveFecha() + " " + DevuelveHora());
                            BZA.set_UltimaCalibracion(DevuelveFecha() + " " + DevuelveHora());
                        }
                    });
                } catch (Exception e) {
                }
            });
        }else{
            dialog1.cancel();
            bt_iniciarCalibracionbool=true;
            Mensaje("Error, peso conocido fuera de rango de acuerdo a Capacidad/punto decimal elegida", R.layout.item_customtoasterror, ComService.getInstance().activity);
        }
    }
    private void ejecutarCalibracionCero(Button Guardar,TextView titulo,ProgressBar loadingPanel,TextView tvCarga,AlertDialog dialog) {
        System.out.println("ITW 410 cero");
        BZA.setCerocal();
        getActivity().runOnUiThread(() -> {
            TextView textView = titulo;
            String textoCompleto = "Coloque el peso conocido (" + tv_pesoConocido.getText() + sp_unidad.getSelectedItem().toString() + ") y luego presione \"SIGUIENTE\"";
            SpannableStringBuilder builder = new SpannableStringBuilder(textoCompleto);
            RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan(1.3f); // Aumenta el tamaño 1.5 veces
            builder.setSpan(sizeSpan1, 25, ("Coloque el peso conocido (" + tv_pesoConocido.getText() + sp_unidad.getSelectedItem().toString() + ")").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Aplica al texto desde el índice 10 al 15
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_ultimaCalibracion.setText(DevuelveFecha() + " " + DevuelveHora());
                    BZA.set_UltimaCalibracion(DevuelveFecha() + " " + DevuelveHora());
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
            if(Texto.equals("Peso Conocido")|| Texto.toLowerCase().contains("filtro")|| Texto.toLowerCase().contains("Capacidad") ){ // Peso conocido con coma
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
                    if(userInput.getText().toString()!=null){ // ,tv_capacidad.getText().toString()
                        textView.setText(userInput.getText().toString());
                    }else{
                        Mensaje("Error, peso conocido fuera de rango de acuerdo a Capacidad/punto decimal elegida", R.layout.item_customtoasterror, ComService.getInstance().activity);
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


