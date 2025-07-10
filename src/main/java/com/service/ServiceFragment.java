package com.service;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.service.utilsPackage.Utils.getIPAddress;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.service.Devices.Balanzas.Clases.BalanzaBase;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import com.service.Comunicacion.GenericDiscovery;
import com.service.Comunicacion.PrinterDiscovery;
import com.service.Comunicacion.PingTask;
import com.service.Interfaz.Balanza;
import com.service.estructuras.ZebraStruct;
import com.service.estructuras.classDevice;
import com.service.Recyclers.MyRecyclerViewAdapter;
import com.service.Devices.Impresora.ImprimirEstandar;
import com.service.Recyclers.RecyclerDeviceLimpio;
import com.service.Recyclers.RecyclerSearcher;
import com.service.utilsPackage.ComService;
import com.service.utilsPackage.PreferencesDevicesManager;
import com.service.utilsPackage.Utils;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ServiceFragment extends Fragment {
    //--------------------

    ArrayList<String> listaKeySalidaMap = PreferencesDevicesManager.listaKeySalidaMap;

    ArrayList<String> listaKeyDeviceMap = PreferencesDevicesManager.listaKeyDeviceMap;
    BluetoothAdapter    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    Boolean isReceiverRegistered = false;
    public ArrayList<ZebraStruct> ListaScanner= new ArrayList<>();
    public RecyclerSearcher adapterimpresora;
    public PrinterDiscovery multizebra = null;
    public GenericDiscovery GenericDiscovery = null;

    //-------------------------

    Button bt_home, bt_1, bt_2, bt_3, bt_4, bt_5, bt_6/*,btAdd, btback*/;
    LinearLayout linearcalib, linearadd,linearpuertos;
    private ButtonProvider buttonProvider;



    ArrayList<String> listmac = new ArrayList<>();
    Spinner  sp_tipopuerto;
    private  int Permisos=0;
    PHService service;
    FragmentActivity actividad;
    TabLayout tablayoutTipoDevices;
    AlertDialog dialog;
    PHService.Balanzas Balanzas;

    RecyclerView recyclerView;
    LinearLayout recyclerviewContainer;
    static Fragment serviceFrgmnt;
    Boolean usuarioSelecciono=false;

    Boolean BoolChangeBalanza=false,stoped = false, programador = false;
    List<String> ListElementsArrayList = new ArrayList<>();
 // jajajajaja
    //jijijiji no lo soñe

    RecyclerView recycler;
    MyRecyclerViewAdapter adapter;

    AppCompatActivity activity;

    int menu = 0;


    static ArrayList<classDevice> listaPorSalida, listaglobal, listaporTipo;
    public static ServiceFragment newInstance(Serializable instance) {
        ServiceFragment fragment = new ServiceFragment();
        Bundle args = new Bundle();
        args.putSerializable("instanceService", instance);
        fragment.setArguments(args);
        serviceFrgmnt = fragment;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_service, container, false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        service = PHService.Instancia();
        Balanza aux = PHService.Instancia().Balanzas;
        Balanzas  = (PHService.Balanzas)aux;
        Permisos=ComService.getInstance().fragmentChangeListener.getUsuarioLvl(); //0 = sin loguear /1 = operador / 2=supervisor / 3=administrador // 4 = programador
       // System.out.println(Permisos);
        this.actividad=getActivity();
        activity=(AppCompatActivity)ComService.getInstance().activity;
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configuracionBotones();
        linearpuertos= view.findViewById(R.id.ln17);
        linearcalib = view.findViewById(R.id.linearCalib);
        linearadd = view.findViewById(R.id.linearañadir);
        tablayoutTipoDevices = view.findViewById(R.id.tablayout);
        tablayoutTipoDevices.addTab(tablayoutTipoDevices.newTab().setText("V.Previa"));
        for(String Devicestr:listaKeyDeviceMap){
            if(!Objects.equals(Devicestr, listaKeyDeviceMap.get(listaKeyDeviceMap.size()-1))) {
                tablayoutTipoDevices.addTab(tablayoutTipoDevices.newTab().setText(PreferencesDevicesManager.pluralizar(Devicestr)));
            }
        }
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerviewContainer = view.findViewById(R.id.recyclerviewContainer);
        sp_tipopuerto = view.findViewById(R.id.sp_tipopuerto);
        recycler = view.findViewById(R.id.listview);
        listaporTipo = PreferencesDevicesManager.get_listIndexPorTipo(tablayoutTipoDevices.getSelectedTabPosition()-1,activity);
        listaPorSalida = PreferencesDevicesManager.get_listPorSalida(sp_tipopuerto.getSelectedItem().toString(), tablayoutTipoDevices.getSelectedTabPosition()-1,activity);
            tablayoutTipoDevices.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(@NonNull TabLayout.Tab tab) {
                    int position = tab.getPosition();
                  //  listaperdevice = PreferencesDevicesManager.get_listIndexPorTipo(position-1,activity);
                    ArrayList<classDevice> ls= PreferencesDevicesManager.get_list(activity);

                    menu = position-1;
                    Boolean salidaDisponible = false;
                    boolean[] arr = new boolean[listaKeySalidaMap.size()];
//   boolean[] arr = PreferencesDevicesManager.get_numeroSalidasBZA(activity);
                   // Arrays.fill(arr, true);
                    Integer[] arrint = new Integer[listaKeySalidaMap.size()];
                    for (int i = 0; i < arrint.length; i++) {
                        arrint[i] = 0;
                    }
                  //  System.out.println("position-1 "+(position-1));
                    switch (position-1) {
                        case -1:{
                            linearpuertos.setVisibility(GONE);
                            salidaDisponible=true;
                            break;
                        }
                        case 0:
                        case 1: {
                            linearpuertos.setVisibility(VISIBLE);
                            for (classDevice x : ls) {
                                if (x.getSalida() != null  && x.getTipo().equals(PreferencesDevicesManager.obtenerTipoPorIndice(tablayoutTipoDevices.getSelectedTabPosition()-1)) ) {
                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(0)))) {
                                        arr[0] = false;
                                        arrint[0]++;
                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(1)))) {
                                        arr[1] = false;
                                        arrint[1]++;

                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(2)))) {
                                        arr[2] = false;
                                        arrint[2]++;
                                    }
                                        arr[3] = false;
                                        arrint[3]++;
                                        arr[4] = false;
                                        arrint[4]++;
                                        arr[4] = false;
                                        arrint[4]++;
                                        if((position-1)==1){
                                            arr[5]=false;
                                            arrint[5]++;
                                        }else{
                                            arr[5]=true;
                                        }

                                }else if(x.getSalida()!=null) {

                                        if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(0)))) {
                                            arr[0] = true;
                                            arrint[0]--;
                                        }

                                        if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(1)))) {
                                            arr[1] = true;
                                            arrint[1]--;
                                        }

                                        if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(2)))) {
                                            arr[2] = true;
                                            arrint[2]--;
                                        }
                                    }


                            }
                            break;
                        }
                        case 3:{
                            linearpuertos.setVisibility(VISIBLE);
                            for (classDevice x : ls) {
                                if (x.getSalida() != null && x.getTipo().equals(PreferencesDevicesManager.obtenerTipoPorIndice(tablayoutTipoDevices.getSelectedTabPosition() - 1))) {
                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(0)))) {
                                        arr[0] = false;
                                        arrint[0]++;
                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(1)))) {
                                        arr[1] = false;
                                        arrint[1]++;
                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(2)))) {
                                        arr[2] = false;
                                        arrint[2]++;
                                    }
                                } else if (x.getSalida() != null) {
                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(0)))) {
                                        arr[0] = true;
                                        arrint[0]--;
                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(1)))) {
                                        arr[1] = true;
                                        arrint[1]--;
                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(2)))) {
                                        arr[2] = true;
                                        arrint[2]--;
                                    }
                                }
                                arr[3] = true;
                                arrint[3]--;
                                arr[4] = true;
                                arrint[4]--;
                            }
                            break;
                        }
                        case 2:{
                            linearpuertos.setVisibility(VISIBLE);
                            for (classDevice x : ls) {
                                if (x.getSalida() != null && x.getTipo().equals(PreferencesDevicesManager.obtenerTipoPorIndice(tablayoutTipoDevices.getSelectedTabPosition() - 1))) {
                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(0)))) {
                                        arr[0] = false;
                                        arrint[0]++;
                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(1)))) {
                                        arr[1] = false;
                                        arrint[1]++;
                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(2)))) {
                                        arr[2] = false;
                                        arrint[2]++;
                                    }
                                } else if (x.getSalida() != null) {
                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(0)))) {
                                        arr[0] = true;
                                        arrint[0]--;
                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(1)))) {
                                        arr[1] = true;
                                        arrint[1]--;
                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(2)))) {
                                        arr[2] = true;
                                        arrint[2]--;
                                    }
                                }
                                arr[3] = true;
                                arrint[3]--;
                                arr[4] = true;
                                arrint[4]--;
                                arr[5] = true;
                                arrint[5]--;
                            }
                            break;
                        } case 4:  case 5: {
                            linearpuertos.setVisibility(VISIBLE);
                            for (classDevice x : ls) {
                                if (x.getSalida() != null && x.getTipo().equals(PreferencesDevicesManager.obtenerTipoPorIndice(tablayoutTipoDevices.getSelectedTabPosition()-1))) {
                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(0)))) {
                                        arr[0] = false;
                                        arrint[0]++;
                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(1)))) {
                                        arr[1] = false;
                                        arrint[1]++;
                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(2)))) {
                                        arr[2] = false;
                                        arrint[2]++;
                                    }

                                }else if(x.getSalida()!=null) {
                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(0)))) {
                                        arr[0] = true;
                                        arrint[0]--;
                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(1)))) {
                                        arr[1] = true;
                                        arrint[1]--;
                                    }

                                    if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(2)))) {
                                        arr[2] = true;
                                        arrint[2]--;
                                    }

                                }
                                arr[4] = true;
                                arrint[4]--;
                                arr[5] = true;
                                arrint[5]--;
                                if(position-1 == 4) {
                                    arr[3] = false;
                                    arrint[3]++;
                                }
                            }

                            break;
                        }
                    }
                    salidaDisponible = ModificacionImpresoras(arr, arrint);
                    if (salidaDisponible) {
                        sp_tipopuerto.setSelection(0);
                        CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                    } else {
                        ArrayList<String> lista = new ArrayList<>();
                        lista.add("NO HAY SALIDAS");
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lista);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sp_tipopuerto.setPopupBackgroundResource(R.drawable.campollenarclickeable);

                        sp_tipopuerto.setAdapter(adapter);
                        Utils.Mensaje("Todas las salidas posibles ya utilizadas", R.layout.item_customtoasterror, activity);
                    }
                }

                @Override
                public void onTabUnselected(@NonNull TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(@NonNull TabLayout.Tab tab) {
                }
            });

      if((Permisos==3)){//  if(!programador){
            TabLayout.Tab tab = tablayoutTipoDevices.getTabAt(2); // Índice basado en 0
            if (tab != null) {
                tab.select();
                View tabView = ((ViewGroup) tablayoutTipoDevices.getChildAt(0)).getChildAt(0);
                tabView = ((ViewGroup) tablayoutTipoDevices.getChildAt(0)).getChildAt(1);
                if (tabView != null) {
                    tabView.setVisibility(GONE);
                }  tabView = ((ViewGroup) tablayoutTipoDevices.getChildAt(0)).getChildAt(3);
                if (tabView != null) {
                    tabView.setVisibility(GONE);
                }
            }
        }else if (Permisos<=2){
          bt_1.setVisibility(GONE);
      }
        LCalibracioninit();
        CargarRecycle();
        try {
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            ArrayList<Integer> list = PreferencesDevicesManager.getBalanzas(activity);
            Boolean tienecalibracion=false;
            for (int i = 0; i < list.size(); i++) {
                tienecalibracion = PHService.ModelosClasesBzas.values()[list.get(i)].getTieneCal();
                if(tienecalibracion){
                    ListElementsArrayList.add("Calibracion Balanza " + String.valueOf(i + 1));
                }else{
                    ListElementsArrayList.add("Balanza " + String.valueOf(i + 1)+" no tiene calibracion");

                }
            }
            adapter = new MyRecyclerViewAdapter(getContext(), ListElementsArrayList, activity);
            adapter.setClickListener((view1, position) -> {

                if(PHService.ModelosClasesBzas.values()[list.get(position)].getTieneCal()) {

                    System.out.println("ESTADO SETEADO? "+BalanzaBase.M_MODO_CALIBRACION);
                    Balanzas.openCalibracion(position + 1);
                };
            });
            recycler.setAdapter(adapter);


        } catch (Exception e) {

        }

        String[] Balanzas_arrtipo = getResources().getStringArray(R.array.tipoSalida);
        ArrayAdapter<String> adapter7 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, Balanzas_arrtipo);
        adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_tipopuerto.setAdapter(adapter7);
        adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_tipopuerto.setPopupBackgroundResource(R.drawable.campollenarclickeable);

        try {
            sp_tipopuerto.setSelection(PreferencesDevicesManager.getBalanzas(activity).get(0));
        } catch (Exception e) {
        }
        sp_tipopuerto.setOnTouchListener((v, event)     -> {
            usuarioSelecciono = true;
            v.performClick();
            return false;
        });
        sp_tipopuerto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (usuarioSelecciono || i>0) {
                    String value = String.valueOf(sp_tipopuerto.getItemAtPosition(i));
                    CargarDatosRecycler(value);
                    usuarioSelecciono = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void CargarDatosRecycler(String Salida) {
        int tipodevice= tablayoutTipoDevices.getSelectedTabPosition()-1;
        if(tipodevice!=-1) {
            try {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                listaPorSalida.clear();
                listaPorSalida = PreferencesDevicesManager.get_listPorSalida(Salida, tipodevice,activity);
                listaglobal = PreferencesDevicesManager.get_listIndex(activity);
                listaporTipo = PreferencesDevicesManager.get_listIndexPorTipo(tablayoutTipoDevices.getSelectedTabPosition() - 1,activity);
            } catch (Exception e) {
            } finally {
                addapterItem(Salida);
            }
        }else{
            listaPorSalida.clear();

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            listaPorSalida = (ArrayList<classDevice>) PreferencesDevicesManager.organizarDispositivos(PreferencesDevicesManager.get_list(activity));//lista = ordenarLista(PreferencesDevicesManager.get_list(activity)); //ordenarLista(PreferencesDevicesManager.get_list(activity));
            listaglobal = PreferencesDevicesManager.get_listIndex(activity);
            listaporTipo = PreferencesDevicesManager.get_listIndexPorTipo(tablayoutTipoDevices.getSelectedTabPosition() - 1,activity);
        }
        try {
            RecyclerDeviceLimpio.ItemClickListener itemClickListener = new RecyclerDeviceLimpio.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position, classDevice Device, String Salida, int TipoDevice) {
                    showdialog(Device, Salida);
                }
            };
            if (listaPorSalida.size() >= 1) {
                RecyclerDeviceLimpio adapter = new RecyclerDeviceLimpio(getContext(), listaPorSalida, activity, itemClickListener, sp_tipopuerto.getSelectedItem().toString(), tablayoutTipoDevices.getSelectedTabPosition()-1);
                recyclerView.setAdapter(adapter);
                recyclerView.post(() -> {
                    try {
                        setPaddingRecycler();
                    } catch (Exception e) {
                    }
                });
            }
        } catch (Exception e) {
        }
    }
    void setPaddingRecycler() {
          /*  int totalHeight = recyclerView.computeVerticalScrollRange();
            int visibleHeight = recyclerView.computeVerticalScrollExtent();
            boolean canScrollVertical = totalHeight > visibleHeight;
        if(canScrollVertical) {
            ((RecyclerDeviceLimpio)recyclerView.getAdapter()).setPaddingchild(27);
        }else{
            ((RecyclerDeviceLimpio)recyclerView.getAdapter()).setPaddingchild(10);
        }*/
        }

    public void LCalibracioninit() {
        if(Permisos>=3){//if((programador)){
            TabLayout.Tab tab = tablayoutTipoDevices.getTabAt(0); // Índice basado en 0
            if (tab != null) {
                tab.select();
            }
        }
        bt_1.setBackgroundResource(R.drawable.boton_editar_i);
        bt_1.setOnClickListener(View -> {
            if (linearadd.getVisibility() == GONE) {
                linearadd.setVisibility(VISIBLE);
                linearcalib.setVisibility(GONE);
                sp_tipopuerto.setSelection(0);
                Lagregarinit();
                listaporTipo = PreferencesDevicesManager.get_listIndexPorTipo(tablayoutTipoDevices.getSelectedTabPosition()-1,activity);
                boolean[] arr = new boolean[listaKeySalidaMap.size()];
                Integer[] arrint= new Integer[listaKeySalidaMap.size()];
                for (int i = 0; i < arrint.length; i++) {
                    arrint[i] = 0;
                }

                for (classDevice x :
                        listaporTipo
                ) {

                    if (x.getSalida() != null) {
                        if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(0)))) {
                            arr[0] = false;
                            arrint[0]++;
                        }

                        if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(1)))) {
                            arr[1] = false;
                            arrint[1]++;
                        }

                        if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(2)))) {
                            arr[2] = false;
                            arrint[2]++;
                        }
                        if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(3)))) {
                            arr[3] = false;
                            arrint[3]++;
                        }
                        if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(4)))) {
                            arr[4] = false;
                            arrint[4]++;
                        }
                        if (x.getSalida().equals(PreferencesDevicesManager.salidaMap.get(listaKeySalidaMap.get(5)))) {
                            arr[5] = false;
                            arrint[5]++;
                        }
                    }

                }
                if (Permisos >= 3) {// if(programador){
                    linearpuertos.setVisibility(GONE);
                    sp_tipopuerto.setSelection(0);
                    CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                }else{
                    ModificacionImpresoras(arr,arrint);
                    CargarDatosRecycler(listaKeySalidaMap.get(3));
                }
            }

        });
    }

    public void Lagregarinit() {
        bt_1.setBackgroundResource(R.drawable.boton_atras_i);
        if(Permisos==4) {
            bt_2.setVisibility(VISIBLE);
            bt_2.setBackgroundResource(R.drawable.btn_reset);
            bt_2.setOnClickListener(View ->{
                Utils.dialogoDosOpciones(actividad,"¿esta seguro que quiere resetear Dispositivos de Service?","Si",() ->{
                    Utils.clearCache(activity.getApplicationContext());
                    BoolChangeBalanza=true;
                    service.init(true);
                    ComService.getInstance().fragmentChangeListener.AbrirFragmentPrincipal();
                    },"No",() ->{});

            });
        }
        bt_1.setOnClickListener(View -> {
            if (linearadd.getVisibility() == VISIBLE) {
                linearadd.setVisibility(GONE);
                linearcalib.setVisibility(VISIBLE);
                bt_2.setVisibility(View.INVISIBLE);
                bt_1.setVisibility(VISIBLE);
                LCalibracioninit();
                try {
                    recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    ArrayList<Integer> list = PreferencesDevicesManager.getBalanzas(activity);
                    for (int i = 0; i < list.size(); i++) {
                        Boolean   tienecalibracion = PHService.ModelosClasesBzas.values()[list.get(i)].getTieneCal();
                        if(tienecalibracion){
                            ListElementsArrayList.add("Calibracion Balanza " + String.valueOf(i + 1));
                        }else{
                        ListElementsArrayList.add("Balanza " + String.valueOf(i + 1)+" no tiene calibracion");
                    }}
                    adapter = new MyRecyclerViewAdapter(getContext(), ListElementsArrayList, activity);
                    adapter.setClickListener((view1, position) -> {

                        if(PHService.ModelosClasesBzas.values()[list.get(position)].getTieneCal()) {
                            Balanzas.setEstado(position + 1, BalanzaBase.M_MODO_CALIBRACION);
                            Balanzas.openCalibracion(position + 1);
                        }
                    });
                    recycler.setAdapter(adapter);

                } catch (Exception e) {
                }}
            ListElementsArrayList.clear();
            ArrayList<Integer> list = PreferencesDevicesManager.getBalanzas(activity);
            /*
            Process: com.jws.jwsapi, PID: 22355
                                                                                                    java.lang.ArrayIndexOutOfBoundsException: length=11; index=-1
                                                                                                    	at com.service.utilsPackage.PreferencesDevicesManager.getBalanzas(PreferencesDevicesManager.java:179)
                                                                                                    	at com.service.ServiceFragment.lambda$Lagregarinit$9$com-service-ServiceFragment(ServiceFragment.java:652)
                                                                                                    	at com.service.ServiceFragment$$ExternalSyntheticLambda15.onClick(Unknown Source:2)
                                                                                                    	at android.view.View.performClick(View.java:6597)
                                                                                                    	at android.view.View.performClickInternal(View.java:6574)
                                                                                                    	at android.view.View.access$3100(View.java:778)
                                                                                                    	at android.view.View$PerformClick.run(View.java:25885)
                                                                                                    	at android.os.Handler.handleCallback(Handler.java:873)
                                                                                                    	at android.os.Handler.dispatchMessage(Handler.java:99)
                                                                                                    	at android.os.Looper.loop(Looper.java:193)
                                                                                                    	at android.app.ActivityThread.main(ActivityThread.java:6718)
                                                                                                    	at java.lang.reflect.Method.invoke(Native Method)
                                                                                                    	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
                                                                                                    	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)
            * */
            for (int i = 0; i < list.size(); i++) {
                Boolean  tienecalibracion = PHService.ModelosClasesBzas.values()[list.get(i)].getTieneCal();
                if(tienecalibracion){
                    ListElementsArrayList.add("Calibracion Balanza " + String.valueOf(i + 1));
                }else{
                    ListElementsArrayList.add("Balanza " + String.valueOf(i + 1)+" no tiene calibracion");
                }
            }
            adapter = new MyRecyclerViewAdapter(getContext(), ListElementsArrayList, activity);
            adapter.setClickListener((view1, position) -> {
                if(PHService.ModelosClasesBzas.values()[list.get(position)].getTieneCal()) {
                    System.out.println("ESTADO SETEADO? "+BalanzaBase.M_MODO_CALIBRACION+ " POSICION? "+position+1+ "");

                    Balanzas.setEstado(position + 1, BalanzaBase.M_MODO_CALIBRACION);
                    Balanzas.openCalibracion(position + 1);
                }
            });
            recycler.setAdapter(adapter);
        });
    }


    private classDevice newAdapteritem(int nID) {
        classDevice Adapteritem = new classDevice();
        Adapteritem.setTipo(PreferencesDevicesManager.valordef);
        Adapteritem.setModelo(PreferencesDevicesManager.valordef);
        Adapteritem.setSalida(PreferencesDevicesManager.valordef);//"-1");
        Adapteritem.setID(-1);
        Adapteritem.setND(nID);
        Adapteritem.setSeteo(false);
        ArrayList<String> diraux = new ArrayList<String>();
        diraux.add("");
        diraux.add("");
        diraux.add("");
        diraux.add("");
        diraux.add("");
        diraux.add("");
        Adapteritem.setDireccion(diraux);
        return Adapteritem;
    }
    private void dialogDispositivo(classDevice Device) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(actividad);
        View mView = null;
        mView = actividad.getLayoutInflater().inflate(R.layout.dialogo_devices, null);
        Spinner spBaud = mView.findViewById(R.id.sp_Baud);
        TextView ip = mView.findViewById(R.id.tv_Baud);
        TextView nombreid = mView.findViewById(R.id.textView1);
        nombreid.setText("id");
        Button buscador =mView.findViewById(R.id.buscadorbt);
        TextView ID = mView.findViewById(R.id.tv_Slave);
        ID.setOnClickListener(View ->{
            Utils.dialogTextNumber(ID,"","Ingrese Id",actividad);
        });
        buscador.setVisibility(GONE);
        LinearLayout Lip= mView.findViewById(R.id.linearLayout4);
        Spinner spStopbit = mView.findViewById(R.id.sp_Stopbit);
        Spinner spDatabit = mView.findViewById(R.id.sp_Databit);
        Spinner spParity = mView.findViewById(R.id.sp_Parity);
        LinearLayout Lid = mView.findViewById(R.id.linearLayout3);
        ID.setVisibility(VISIBLE);
       // Lid.setVisibility(GONE);
        TextView nombreDialog = mView.findViewById(R.id.numMOD);
        LinearLayout Lrs232 = mView.findViewById(R.id.Lrs232);
        LinearLayout Lelse= mView.findViewById(R.id.Lelse);
        Spinner sp_Modelo = mView.findViewById(R.id.sp_port);

        ArrayAdapter<String> adapter11 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayoutTipoDevices.getSelectedTabPosition()-1)));
        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_Modelo.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_Modelo.setAdapter(adapter11);
        if(PreferencesDevicesManager.EsPuertoSerie(sp_tipopuerto.getSelectedItem().toString())){
            Lrs232.setVisibility(VISIBLE);
            if(Device.getID()>0){
                ID.setText(String.valueOf(Device.getID()));
            }
        }else{
            if(sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(3))){
                Lip.setVisibility(VISIBLE);
            }else{
                Lip.setVisibility(GONE);
            }
            Lrs232.setVisibility(GONE);
        }
        final int[] lastpos = {1};
        sp_Modelo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_Modelo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!sp_Modelo.getSelectedItem().equals(PHService.ModelosClasesDispositivos.ASCII.name())){
                    ID.setVisibility(VISIBLE);
                    Lid.setVisibility(VISIBLE);
                }else{
                    Lid.setVisibility(GONE);
                }

                if(sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(3))) {
                        if (!sp_Modelo.getSelectedItem().equals(PHService.ModelosClasesDispositivos.Master.name())) {
                            Lip.setClickable(false);
                            ip.setClickable(false);
                            ip.setText(getIPAddress(true));
                        } else {
                            Lip.setClickable(true);
                            ip.setClickable(true);
                            if(!Device.getDireccion().isEmpty()) {
                                if(!Device.getDireccion().get(0).isEmpty()) {
                                    ip.setText( Device.getDireccion().get(0));;;;;;;;;;
                                }else{
                                    ip.setText("");
                                }
                            }else{
                                ip.setText("");
                            }
                        }
            }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        try {
            if(Device.getID()>0) {
                ID.setText(String.valueOf(Device.getID()));
            }
        } catch (Exception e) {

        }
        try {
            ip.setText(Device.getDireccion().get(0));
        } catch (Exception e) {

        }
        nombreDialog.setText(("Nº Dispositivo " + (Device.getNDL())));
        if(Device.getDireccion()!=null) {
            String[] lbaud = requireContext().getResources().getStringArray(R.array.baudrate);
            spBaud.setSelection(Arrays.asList(lbaud).indexOf(Device.getDireccion().size() > 0 ? String.valueOf(Device.getDireccion().get(0)) : PreferencesDevicesManager.DefConfig.get(0)));
            String[] lDatabit = requireContext().getResources().getStringArray(R.array.DataBit);
            spDatabit.setSelection(Arrays.asList(lDatabit).indexOf(Device.getDireccion().size() > 1 ? String.valueOf(Device.getDireccion().get(1)) : PreferencesDevicesManager.DefConfig.get(1)));
            String[] lStopbit = requireContext().getResources().getStringArray(R.array.Stopbit);
            spStopbit.setSelection(Arrays.asList(lStopbit).indexOf(Device.getDireccion().size() > 2 ? String.valueOf(Device.getDireccion().get(2)) : PreferencesDevicesManager.DefConfig.get(2)));
            String[] lparity = requireContext().getResources().getStringArray(R.array.Stopbit);
            spParity.setSelection(Arrays.asList(lparity).indexOf(Device.getDireccion().size() > 3 ? String.valueOf(Device.getDireccion().get(3)) : PreferencesDevicesManager.DefConfig.get(3)));
        }
        Button Guardar = mView.findViewById(R.id.Guardar);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //Utils.configureDialogSize(dialog,requireContext());
        Button remove = mView.findViewById(R.id.Remove);
        if (!Device.getSeteo()) {
            remove.setVisibility(GONE);
        }
        ip.setOnClickListener(View -> {
            Utils.dialogIp(ip, "", "Ingrese ip",actividad);
        });

        if(PreferencesDevicesManager.indexofclasesdevices(listaPorSalida,Device)>=1 || listaPorSalida.size()>2){
            sp_Modelo.setSelection(PreferencesDevicesManager.obtenerIndiceModeloPorTipo(tablayoutTipoDevices.getSelectedTabPosition()-1,listaPorSalida.get(0).getModelo()));
            sp_Modelo.setEnabled(false); // POR AHORA PROBAR
            sp_Modelo.setClickable(false);
        }else{
            sp_Modelo.setSelection(PreferencesDevicesManager.obtenerIndiceModeloPorTipo((tablayoutTipoDevices.getSelectedTabPosition()-1),Device.getModelo()));
        }
        remove.setOnClickListener(view -> {
            try {
                classDevice Adapteritem = newAdapteritem(Device.getND());
                PreferencesDevicesManager.addDevice(Adapteritem,activity);
                CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                dialog.cancel();
            } catch (Exception e) {
                Utils.Mensaje("Error en eliminacion", R.layout.item_customtoasterror, activity);
            }
        });
        Guardar.setOnClickListener(View -> {
            if(sp_Modelo.getSelectedItemPosition()==0 && PreferencesDevicesManager.salidaMap.get(sp_tipopuerto.getSelectedItem().toString()).equals(listaKeySalidaMap.get(3))){
                Utils.Mensaje("Funcion no disponible en RED",R.layout.item_customtoasterror,activity);
            }else{
                if( PreferencesDevicesManager.EsPuertoSerie(sp_tipopuerto.getSelectedItem().toString())||PreferencesDevicesManager.EsPuertoSerie(sp_tipopuerto.getSelectedItem().toString()) && sp_Modelo.getSelectedItemPosition()!=0|| sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(3)) && !ip.getText().toString().equals("")) {
                    try {
                        classDevice newDevice = new classDevice();
                        int position = tablayoutTipoDevices.getSelectedTabPosition() - 1;
                        newDevice.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(position));
                        String value2 = sp_tipopuerto.getSelectedItem().toString();
                        ArrayList<String> listaux = new ArrayList<>();
                        String value ="";
                        if ( !ID.getText().toString().isEmpty() ) { //PreferencesDevicesManager.EsPuertoSerie(sp_tipopuerto.getSelectedItem().toString()) &&
                            int Slave = Integer.parseInt(ID.getText().toString());
                            newDevice.setID(Slave);
                        }else{
                            if(!sp_Modelo.getSelectedItem().equals(PHService.ModelosClasesDispositivos.ASCII.name())){
                                newDevice.setID(1);
                            }else{
                                newDevice.setID(0);
                            }}
                        if(sp_Modelo.getVisibility()==View.VISIBLE ) {
                            value= sp_Modelo.getSelectedItem().toString();
                        }else{
                            value = (PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayoutTipoDevices.getSelectedTabPosition()-1)).get(0));
                        }
                        newDevice.setModelo(value);
                        newDevice.setSalida(PreferencesDevicesManager.salidaMap.get(value2));
                        if(PreferencesDevicesManager.EsPuertoSerie(PreferencesDevicesManager.salidaMap.get(value2))){
                            listaux.add(spBaud.getSelectedItem().toString());
                            listaux.add(spDatabit.getSelectedItem().toString());
                            listaux.add(spStopbit.getSelectedItem().toString());
                            listaux.add(String.valueOf(spParity.getSelectedItemPosition()));
                        }else{
                                listaux.add(ip.getText().toString());
                        }
                        newDevice.setDireccion(listaux);

                        newDevice.setSeteo(true);
                        newDevice.setND(Device.getND());
                        if(newDevice.getID()>=1||(!newDevice.getModelo().equals(PHService.ModelosClasesDispositivos.Slave.name()) &&  listaPorSalida.size()<=2)) {
                            if(!PreferencesDevicesManager.EstaEnUsoElID(Device.getNDL(),newDevice.getID(), tablayoutTipoDevices.getSelectedTabPosition()-1,sp_tipopuerto.getSelectedItem().toString(),activity)){
                                PreferencesDevicesManager.addDevice(newDevice, activity);
                            }else{
                                Utils.Mensaje("El Id ya esta en uso",R.layout.item_customtoasterror,activity);
                            }
                        }else{
                            Utils.Mensaje("Debe tener un Id",R.layout.item_customtoasterror,activity);
                        }
                        BoolChangeBalanza=true;
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());

                            }});
                        dialog.cancel();
                    } catch (Exception e) {
                        Utils.Mensaje("Error, deben ser todos los valores numeros enteros", R.layout.item_customtoasterror, activity);
                        dialog.cancel();
                    }
                }else{
                    Utils.Mensaje("Todavia hay valores vacios", R.layout.item_customtoasterror, activity);
                }
            }
        });
    }
    private void dialogEscaner(classDevice Device) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(actividad);
        View mView = null;
        mView = actividad.getLayoutInflater().inflate(R.layout.dialogo_devices, null);
        Spinner spBaud = mView.findViewById(R.id.sp_Baud);
        TextView ID = mView.findViewById(R.id.tv_Slave);
        ID.setVisibility(GONE);
        Spinner spStopbit = mView.findViewById(R.id.sp_Stopbit);
        Spinner spDatabit = mView.findViewById(R.id.sp_Databit);
        Spinner spParity = mView.findViewById(R.id.sp_Parity);
        TextView nombreDialog = mView.findViewById(R.id.numMOD);
        nombreDialog.setText(("Nº Escaner " + (Device.getNDL())));
        LinearLayout Lelse= mView.findViewById(R.id.Lelse);
        LinearLayout Lrs232 = mView.findViewById(R.id.Lrs232);
        Lelse.setVisibility(GONE);
        Lrs232.setVisibility(VISIBLE);
        if(Device.getDireccion()!=null) {
            String[] lbaud = requireContext().getResources().getStringArray(R.array.baudrate);
            spBaud.setSelection(Arrays.asList(lbaud).indexOf(Device.getDireccion().size() > 0 ? String.valueOf(Device.getDireccion().get(0)) : PreferencesDevicesManager.DefConfig.get(0)));
            String[] lDatabit = requireContext().getResources().getStringArray(R.array.DataBit);
            spDatabit.setSelection(Arrays.asList(lDatabit).indexOf(Device.getDireccion().size() > 1 ? String.valueOf(Device.getDireccion().get(1)) : PreferencesDevicesManager.DefConfig.get(1)));
            String[] lStopbit = requireContext().getResources().getStringArray(R.array.Stopbit);
            spStopbit.setSelection(Arrays.asList(lStopbit).indexOf(Device.getDireccion().size() > 2 ? String.valueOf(Device.getDireccion().get(2)) : PreferencesDevicesManager.DefConfig.get(2)));
            String[] lparity = requireContext().getResources().getStringArray(R.array.Stopbit);
            spParity.setSelection(Arrays.asList(lparity).indexOf(Device.getDireccion().size() > 3 ? String.valueOf(Device.getDireccion().get(3)) : PreferencesDevicesManager.DefConfig.get(3)));
        }
        Button Guardar = mView.findViewById(R.id.Guardar);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //Utils.configureDialogSize(dialog,requireContext());

        Button remove = mView.findViewById(R.id.Remove);
        if (!Device.getSeteo()) {
            remove.setVisibility(GONE);
        }
        if(sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(5))) {
            Lrs232.setVisibility(GONE);
        }

        remove.setOnClickListener(view -> {
            try {
                classDevice Adapteritem = newAdapteritem(Device.getND());
                PreferencesDevicesManager.addDevice(Adapteritem,activity);
                CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                dialog.cancel();
            } catch (Exception e) {
                Utils.Mensaje("Error en eliminacion", R.layout.item_customtoasterror, activity);
            }
        });
        Guardar.setOnClickListener(View -> {
                try {
                    classDevice x = new classDevice();
                    x.setID(0);
                    int position = tablayoutTipoDevices.getSelectedTabPosition() - 1;
                    x.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(position));
                    String value2 = sp_tipopuerto.getSelectedItem().toString();

                    ArrayList<String> listaux = new ArrayList<>();
                    x.setModelo(PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(position)).get(0)); // Expansion
                    x.setSalida(PreferencesDevicesManager.salidaMap.get(value2));
                    listaux.add(spBaud.getSelectedItem().toString());
                    listaux.add(spDatabit.getSelectedItem().toString());
                    listaux.add(spStopbit.getSelectedItem().toString());
                    listaux.add(String.valueOf(spParity.getSelectedItemPosition()));
                    x.setDireccion(listaux);
                    x.setSeteo(true);
                    x.setND(Device.getND());
                    PreferencesDevicesManager.addDevice(x,activity);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());

                        }
                    });
                    dialog.cancel();
                } catch (Exception e) {
                    Utils.Mensaje("Error, deben ser todos los valores numeros enteros", R.layout.item_customtoasterror, activity);
                    dialog.cancel();
                }
        });
    }

    private void dialogExpansion(classDevice Device) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(actividad);
        View mView = null;
        mView = actividad.getLayoutInflater().inflate(R.layout.dialogo_devices, null);
        LinearLayout LtvSlave = mView.findViewById(R.id.linearLayout3);
        TextView ID = mView.findViewById(R.id.tv_Slave);
        ID.setOnClickListener(View ->{
            Utils.dialogTextNumber(ID,"","Ingrese Id",actividad);
        });
        Spinner sp_Modelo = mView.findViewById(R.id.sp_port);
        TextView nombreDialog = mView.findViewById(R.id.numMOD);
        TextView nombreid = mView.findViewById(R.id.textView1);
        nombreid.setText("Id");
        if(Device.getID()>0){
            ID.setText(String.valueOf(Device.getID()));
        }
        nombreDialog.setText(("Nº Expansion " + (Device.getNDL())));
        ArrayAdapter<String> adapter11 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayoutTipoDevices.getSelectedTabPosition()-1)));
        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_Modelo.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_Modelo.setAdapter(adapter11);
        sp_Modelo.setSelection(PreferencesDevicesManager.obtenerIndiceModeloPorTipo((tablayoutTipoDevices.getSelectedTabPosition()-1),Device.getModelo()));
        Button Guardar = mView.findViewById(R.id.Guardar);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //Utils.configureDialogSize(dialog,requireContext());

        Button remove = mView.findViewById(R.id.Remove);
        if (!Device.getSeteo()) {
            remove.setVisibility(GONE);
        }

       // if(PreferencesDevicesManager.indexofclasesdevices(listaPorSalida,Device)>=1){

         //   sp_Modelo.setEnabled(false); // POR AHORA PROBAR
           // sp_Modelo.setClickable(false);
        //}

        remove.setOnClickListener(view -> {
            try {
                classDevice Adapteritem = newAdapteritem(Device.getND());
                PreferencesDevicesManager.addDevice(Adapteritem,activity);
                CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                dialog.cancel();
            } catch (Exception e) {
                Utils.Mensaje("Error en eliminacion", R.layout.item_customtoasterror
                        , activity);
            }
        });
        Guardar.setOnClickListener(View -> {
            try {
                classDevice x = new classDevice();
                int position = tablayoutTipoDevices.getSelectedTabPosition() - 1;
                x.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(position));
                String value2 = sp_tipopuerto.getSelectedItem().toString();
                ArrayList<String> listaux = new ArrayList<>();
                x.setSalida(PreferencesDevicesManager.salidaMap.get(value2));
                if (PreferencesDevicesManager.EsPuertoSerie(sp_tipopuerto.getSelectedItem().toString()) && !ID.getText().toString().isEmpty()) {
                    int Slave = Integer.parseInt(ID.getText().toString());
                    x.setID(Slave);
                }else{
                    x.setID(1);
                }
                String value ="";
                 value= sp_Modelo.getSelectedItem().toString();
                   listaux = PHService.ModelosClasesExpansiones.valueOf(value).getConfiguraciones();
                x.setModelo(value);
                x.setDireccion(listaux);
                x.setSeteo(true);
                x.setND(Device.getND());
                if(x.getID()>=1) {
                    if(!PreferencesDevicesManager.EstaEnUsoElID(Device.getNDL(),x.getID(), tablayoutTipoDevices.getSelectedTabPosition()-1,sp_tipopuerto.getSelectedItem().toString(),activity)){
                        PreferencesDevicesManager.addDevice(x, activity);
                    }else{
                        Utils.Mensaje("El id ya esta en uso",R.layout.item_customtoasterror,activity);
                    }
                }else {
                    Utils.Mensaje("Debe tener un id", R.layout.item_customtoasterror, activity);
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());

                    }
                });
                dialog.cancel();
            } catch (Exception e) {
                System.out.println("ERROR"+e.getMessage());
                Utils.Mensaje("Error, deben ser todos los valores numeros enteros", R.layout.item_customtoasterror, activity);
                dialog.cancel();
            }
        });
    }
    private void ActivarBluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Utils.Mensaje("Contacte a servicio tecnico, error en activar bluetooth",R.layout.item_customtoasterror,activity);
                return;
            }
            bluetoothAdapter.enable(); // Requiere permiso BLUETOOTH_ADMIN
        }
    }
    @SuppressLint("MissingPermission")
    private void dialogImpresora(classDevice Device){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(actividad);
        View mView = null;
        mView = actividad.getLayoutInflater().inflate(R.layout.dialogo_devices, null);
        Spinner sp_Modelo = mView.findViewById(R.id.sp_port);
        TextView macIp = mView.findViewById(R.id.tv_Baud);
        TextView nombreMacIP = mView.findViewById(R.id.textView2);
        LinearLayout LPin = mView.findViewById(R.id.linearLayout3);
        TextView Pin = mView.findViewById(R.id.tv_Slave);
        TextView nombrePin = mView.findViewById(R.id.textView1);
        TextView nombreDialog = mView.findViewById(R.id.numMOD);
        Button ping = mView.findViewById(R.id.ping);
            LinearLayout LMacIp = mView.findViewById(R.id.linearLayout4);
            LinearLayout Lsp_Modelo = mView.findViewById(R.id.linearLayout2);
        Button searchbt = mView.findViewById(R.id.buscadorbt);
        nombreDialog.setText(("Nº Impresora " + (Device.getNDL())));
        if(PreferencesDevicesManager.EsPuertoSerie(sp_tipopuerto.getSelectedItem().toString())){
                macIp.setVisibility(GONE);
            //LPin.setVisibility(GONE);
            if(Device.getID()>0){
                Pin.setText(String.valueOf(Device.getID()));
            }
            LPin.setVisibility(GONE);
            }else if(sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(4))||sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(3))){
               LMacIp.setVisibility(VISIBLE);
               Lsp_Modelo.setVisibility(GONE);
               macIp.setVisibility(VISIBLE);
               searchbt.setVisibility(VISIBLE);
               if(sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(3))){
                   nombreMacIP.setText("Red");
                   LPin.setVisibility(GONE);
                   macIp.setOnClickListener(View ->{
                       Utils.dialogIp(macIp,"","Ingrese IP",actividad);
                   });
                   searchbt.setOnClickListener(new View.OnClickListener(){
                       @Override
                       public void onClick(View view) {
                           buscarImpresoras(macIp,1);//TCPIP
                       }
                   });

               }else if (sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(4))){
                   nombreMacIP.setText("MAC");
                   Pin.setVisibility(VISIBLE);
                   LPin.setVisibility(GONE);
                   nombrePin.setText("Pin");
                   searchbt.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           ActivarBluetooth();
                           buscarImpresoras(macIp,2); //BT
                       }
                   });
                   macIp.setOnClickListener(View ->{
                       Utils.dialogText(macIp,"","Ingrese MAC",actividad); // BUSQUEDA ADAPTER BLUETOOTH
                   });
                   Pin.setOnClickListener(View ->{
                       Utils.dialogText(Pin,"","Ingrese Pin",actividad); // BUSQUEDA ADAPTER BLUETOOTH
                   });
               }
           }else if (sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(5))){
               LMacIp.setVisibility(GONE);
               LPin.setVisibility(GONE);
               Lsp_Modelo.setVisibility(GONE);
               macIp.setVisibility(GONE);
           }
        ArrayAdapter<String> adapter11 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayoutTipoDevices.getSelectedTabPosition()-1)));
        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_Modelo.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_Modelo.setAdapter(adapter11);
        sp_Modelo.setSelection(PreferencesDevicesManager.obtenerIndiceModeloPorTipo(tablayoutTipoDevices.getSelectedTabPosition()-1,Device.getModelo()));
        if(PreferencesDevicesManager.indexofclasesdevices(listaPorSalida,Device)>=1 || listaPorSalida.size()>2){
            sp_Modelo.setEnabled(false); // POR AHORA PROBAR
            sp_Modelo.setClickable(false);
        }
        if(Device.getDireccion() != null && !Device.getDireccion().isEmpty()) {
            int lenghtdireccion = Device.getDireccion().size();
            if (lenghtdireccion > 0 && macIp != null) {
                macIp.setText(String.valueOf(Device.getDireccion().get(0)));
            }
        }
        Button Guardar = mView.findViewById(R.id.Guardar);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        //Utils.configureDialogSize(dialog,requireContext());

        Button remove = mView.findViewById(R.id.Remove);
        if(!Device.getSeteo() || !(Permisos>=3)){//!programador){
            remove.setVisibility(GONE);

        }else if(sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(3)) || sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(4))) {
            ping.setVisibility(VISIBLE);
        }
        ping.setOnClickListener(view ->{
            Ping(macIp.getText().toString());
                });
        remove.setOnClickListener(view ->{
                try {
                    classDevice Adapteritem = newAdapteritem(Device.getND());
                    PreferencesDevicesManager.addDevice(Adapteritem,activity);
                    CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                    dialog.cancel();
                } catch (Exception e) {
                    Utils.Mensaje("Error en eliminacion", R.layout.item_customtoasterror, activity);
                }
        });
        Guardar.setOnClickListener(View ->{
            if((macIp.getVisibility() == GONE||!macIp.getText().toString().equals(""))) {
                String Address = "0";
                try {
                    classDevice x = new classDevice();
                    if (macIp != null && macIp.getVisibility() == VISIBLE) {
                        Address = macIp.getText().toString();
                    }
                    int position = tablayoutTipoDevices.getSelectedTabPosition() - 1;
                    x.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(position));
                    String value2 = sp_tipopuerto.getSelectedItem().toString();
                    ArrayList<String> listaux = new ArrayList<>();
                    // ZEBRA
                    x.setSalida(PreferencesDevicesManager.salidaMap.get(value2));

                    switch (listaKeySalidaMap.indexOf(value2)) {
                        case 0:
                        case 1:
                        case 2: {
                            listaux.add(ImprimirEstandar.BaudZebradef);
                            listaux.add(ImprimirEstandar.StopBZebradef);
                            listaux.add(ImprimirEstandar.DataBZebradef);
                            listaux.add(ImprimirEstandar.ParityZebradef);
                            break;
                        }
                        case 3: {
                            listaux.add(Address);
                            break;
                        }
                        case 4: {
                            listaux.add(Address);
                            String finalAddress = Address;
                                        setpair(activity,finalAddress, Pin.getText().toString(),dialog); // probar
                                    break;
                        }
                    }
                    if(sp_Modelo.getVisibility()==View.VISIBLE && Lsp_Modelo.getVisibility()==View.VISIBLE) {
                        x.setModelo(sp_Modelo.getSelectedItem().toString());
                    }else{
                        x.setModelo(PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayoutTipoDevices.getSelectedTabPosition()-1)).get(0));
                    }
                    x.setDireccion(listaux);
                    x.setSeteo(true);
                    x.setID(0);
                    x.setND(Device.getND());
                    PreferencesDevicesManager.addDevice(x,activity);
                } catch (Exception e) {
                    System.out.println("MIA "+ e.getMessage());
                    Utils.Mensaje("Error, deben ser todos los valores numeros enteros "+e.getMessage(), R.layout.item_customtoasterror, activity);
                    dialog.cancel();
                }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                            
                        }
                    });
                    if( sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(4))){
                        try {
                            bluetoothAdapter.cancelDiscovery();
                            bluetoothAdapter=null;
                            ListaScanner.clear();
                            listmac.clear();
                        } catch (Exception e) {

                        }
                    }


            }else{
                    Utils.Mensaje("Todavia hay valores vacios",R.layout.item_customtoasterror,activity);
            }

            dialog.cancel();
        });
        dialog.show();
    }
    private void dialogBalanza(classDevice Device){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(actividad);
        View mView = actividad.getLayoutInflater().inflate(R.layout.dialogo_devices, null);
            Spinner sp_Modelo = mView.findViewById(R.id.sp_port);
            TextView nombreDialog = mView.findViewById(R.id.numMOD);
        TextView macIp = mView.findViewById(R.id.tv_Baud);
        TextView nombreMacIP = mView.findViewById(R.id.textView2);
        LinearLayout LPin = mView.findViewById(R.id.linearLayout3);
        TextView Pin = mView.findViewById(R.id.tv_Slave);
        TextView nombrePin = mView.findViewById(R.id.textView1);
        Button ping = mView.findViewById(R.id.ping);
        LinearLayout LMacIp = mView.findViewById(R.id.linearLayout4);
        LinearLayout Lsp_Modelo = mView.findViewById(R.id.linearLayout2);
        Button searchbt = mView.findViewById(R.id.buscadorbt);
        searchbt.setText("Buscar Balanza");
        nombreDialog.setText("Nº Balanza: "+(Device.getNDL()));
        final boolean[] aceptaid = {PHService.ModelosClasesBzas.values()[sp_Modelo.getSelectedItemPosition()].getTienePorDemanda()};


        List<String> listaauxiliar =PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayoutTipoDevices.getSelectedTabPosition()-1));
        ArrayList<String> listaSP = new ArrayList<>() ;
        for (String z: listaauxiliar) {
            listaSP.add(z.replace("_"," "));
        }
        //LtvSlaveID.setVisibility(GONE);// POR AHORA NO SERA SETEABLE HASTA HACER LOS PROTOCOLOS CON ID LUEGO SOLO SERA PARA LOS DISPOSITIVOS SIN ID
        ArrayAdapter<String> adapter11 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, listaSP);
        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_Modelo.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_Modelo.setAdapter(adapter11);
        if((PreferencesDevicesManager.indexofclasesdevices(listaPorSalida,Device)>=1 && PreferencesDevicesManager.EsPuertoSerie(sp_tipopuerto.getSelectedItem().toString()))|| listaPorSalida.size()>2){
            sp_Modelo.setSelection(PreferencesDevicesManager.obtenerIndiceModeloPorTipo(tablayoutTipoDevices.getSelectedTabPosition()-1,listaPorSalida.get(0).getModelo().replace("_"," ")));
            sp_Modelo.setEnabled(false); // POR AHORA PROBAR
            sp_Modelo.setClickable(false);
        }else{
            sp_Modelo.setSelection(PreferencesDevicesManager.obtenerIndiceModeloPorTipo((tablayoutTipoDevices.getSelectedTabPosition()-1),Device.getModelo().replace("_"," ")));
        }
        Pin.setOnClickListener(View ->{
            Utils.dialogTextNumber(Pin,"","Ingrese Id",actividad);
        });

       // LPin.setVisibility(GONE);// POR AHORA NO SERA SETEABLE HASTA HACER LOS PROTOCOLOS CON ID LUEGO SOLO SERA PARA LOS DISPOSITIVOS SIN ID
        if(PreferencesDevicesManager.EsPuertoSerie(sp_tipopuerto.getSelectedItem().toString())){
            macIp.setVisibility(GONE);
            if(Device.getID()>0){
                Pin.setText(String.valueOf(Device.getID()));
            }
        }else if(sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(4))||sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(3))){
            LMacIp.setVisibility(VISIBLE);
            Lsp_Modelo.setVisibility(GONE);
            LPin.setVisibility(GONE);
            macIp.setVisibility(VISIBLE);
            searchbt.setVisibility(VISIBLE);

            if(sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(3))){
                nombreMacIP.setText("Red");
                LPin.setVisibility(GONE);
                macIp.setOnClickListener(View ->{
                    Utils.dialogIp(macIp,"","Ingrese IP",actividad);
                });
                searchbt.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        buscarBalanzas(macIp,1);//TCPIP
                    }
                });
            }else if (sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(4))){
                nombreMacIP.setText("MAC");
                Pin.setVisibility(VISIBLE);
                LPin.setVisibility(GONE);
                nombrePin.setText("Pin");
                Pin.setText("");
                searchbt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        buscarBalanzas(macIp,2); //BT
                    }
                });
                macIp.setOnClickListener(View ->{
                    Utils.dialogText(macIp,"","Ingrese MAC",actividad); // BUSQUEDA ADAPTER BLUETOOTH
                });
                Pin.setOnClickListener(View ->{
                    Utils.dialogText(Pin,"","Ingrese Pin",actividad); // BUSQUEDA ADAPTER BLUETOOTH
                });
            }
        }
        Button Guardar = mView.findViewById(R.id.Guardar);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        Button remove = mView.findViewById(R.id.Remove);
        if(!Device.getSeteo()){
            remove.setVisibility(GONE);
        }
        sp_Modelo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    aceptaid[0] = PHService.ModelosClasesBzas.values()[position].getTienePorDemanda();
                    if(!aceptaid[0]){
                        LPin.setVisibility(GONE);
                    }else{
                        LPin.setVisibility(VISIBLE);
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        remove.setOnClickListener(view ->{
            if(listaporTipo.size()>1) {
                try {
                    classDevice Adapteritem = newAdapteritem(Device.getND());
                    PreferencesDevicesManager.addDevice(Adapteritem,activity); //
                    CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                    BoolChangeBalanza=true;
                    dialog.cancel();
                } catch (Exception e) {
                    Utils.Mensaje("Error en eliminacion", R.layout.item_customtoasterror, activity);
                }
            }else{
                Utils.Mensaje("Debe tener almenos 1 balanza configurada",R.layout.item_customtoasterror,activity);
            }
        });
        Guardar.setOnClickListener(View ->{
            if((macIp.getVisibility()==VISIBLE && !macIp.getText().toString().isEmpty())|| macIp.getVisibility()==GONE) {
                int Slave = 0;
                try {
                    classDevice x = new classDevice();
                    if (PreferencesDevicesManager.EsPuertoSerie(sp_tipopuerto.getSelectedItem().toString())&& !Pin.getText().toString().isEmpty()) {
                        Slave = Integer.parseInt(Pin.getText().toString());
                        x.setID(Slave);
                    }else{
                        x.setID(0);
                    }
                    ArrayList<String> listaux = new ArrayList<>();
                    int position = tablayoutTipoDevices.getSelectedTabPosition() - 1;
                    if(sp_Modelo.getVisibility()==View.VISIBLE) {
                        String value = sp_Modelo.getSelectedItem().toString().replace(" ","_");
                        listaux = PHService.ModelosClasesBzas.valueOf(value).getConfiguraciones();
                        System.out.println("DIOS POR QUE NO FUNCIONO"+listaux.get(0));
                        x.setModelo(value);
                    }else{
                        if(sp_tipopuerto.getSelectedItem().toString().contains(listaKeySalidaMap.get(3))){
                            x.setModelo("WF");//PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(position)).get(0));

                        }else{
                            x.setModelo("BT");//PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(position)).get(0));

                        }
                        listaux.add(macIp.getText().toString());
                    }
                    x.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(position));
                    String value2 = sp_tipopuerto.getSelectedItem().toString();
                    x.setSalida(PreferencesDevicesManager.salidaMap.get(value2));
                    x.setDireccion(listaux);
                    x.setSeteo(true);
                    x.setND(Device.getND());
                    if(aceptaid[0]) {
                        if ((listaPorSalida.size() <= 2 && PreferencesDevicesManager.indexofclasesdevices(listaPorSalida, x) == 0 || x.getID() >= 1)) {
                            if (x.getID() == 0 || !PreferencesDevicesManager.EstaEnUsoElID(Device.getNDL(), x.getID(), tablayoutTipoDevices.getSelectedTabPosition() - 1, sp_tipopuerto.getSelectedItem().toString(), activity)) {
                                PreferencesDevicesManager.addDevice(x, activity);
                            } else {
                                Utils.Mensaje("El Id ya esta en uso", R.layout.item_customtoasterror, activity);
                            }
                        } else {
                            Utils.Mensaje("Para Setearlo en 0 debe borrar las anteriores Balanzas", R.layout.item_customtoasterror, activity);
                        }
                    }else{
                        if(x.getID()==0) {
                            PreferencesDevicesManager.addDevice(x, activity);
                        }else{
                            Utils.Mensaje("El modelo no acepta id", R.layout.item_customtoasterror, activity);
                        }
                    }
                    BoolChangeBalanza = true;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                            
                        }
                    });
                    dialog.cancel();
                } catch (Exception e) {
                    System.out.println("OLA?"+e);
                    Utils.Mensaje("Error, deben ser todos los valores numeros enteros", R.layout.item_customtoasterror, activity);
                    dialog.cancel();
                }
            }else{
                Utils.Mensaje("Todavia hay valores vacios", R.layout.item_customtoasterror, activity);
            }
        });
    }
    private void addapterItem(String Salida){
        if(!Salida.equals("NO HAY SALIDAS")  && tablayoutTipoDevices.getSelectedTabPosition()!=0 && Permisos>=2){//programador) {
        try {
               classDevice Adapteritem = new classDevice();
                Adapteritem.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayoutTipoDevices.getSelectedTabPosition()-1));
                Adapteritem.setModelo(PreferencesDevicesManager.valordef);
                Adapteritem.setSalida(Salida);
                Adapteritem.setID(-1);
                Adapteritem.setNDL(listaporTipo.get(listaporTipo.size()-1).getNDL()+1);
                Adapteritem.setSeteo(false);
                Adapteritem.setND(listaglobal.get(listaglobal.size() - 1).getND() + 1);
                ArrayList<String> diraux = new ArrayList<String>();
                Adapteritem.setDireccion(diraux);
                listaPorSalida.add(Adapteritem);
            } catch (Exception e) {
           // System.out.println("ERROR EN ADDAPTER WN");
                classDevice Adapteritem  = new classDevice();
                Adapteritem.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayoutTipoDevices.getSelectedTabPosition()-1));
                Adapteritem.setNDL(0);
                Adapteritem.setModelo(PreferencesDevicesManager.valordef);
                Adapteritem.setSalida(Salida);
                Adapteritem.setID(-1);
                Adapteritem.setSeteo(false);
                Adapteritem.setND(0);
                ArrayList<String> diraux= new ArrayList<String>();
                Adapteritem.setDireccion(diraux);
                listaPorSalida.add(Adapteritem);
            }
        }
    }

    private void showdialog(classDevice Device, String Salida){
        //Device.setID(0);//POR AHORA VA A SER 0 POR DEFAULT HASTA HACER LO DEL I
        if(PreferencesDevicesManager.salidaMap.containsKey(Salida)) {
            switch (tablayoutTipoDevices.getSelectedTabPosition() - 1) {
                case 0: {
                    dialogBalanza(Device);
                    break;
                }
                case 1: {
                    dialogImpresora(Device);
                    break;
                }
                case 2: {
                    dialogExpansion(Device);
                    break;
                }
                case 3: {
                    dialogEscaner(Device);
                    break;
                }
                case 4: {
                    dialogDispositivo(Device);
                    break;
                }

            }
        }
    }
    private boolean ModificacionImpresoras(boolean[] arr,Integer[] arrint){
        ArrayList<String> lista = new ArrayList<String>();


        ArrayList<String> listasalida = PreferencesDevicesManager.obtenerListaPorMap(PreferencesDevicesManager.salidaMap);
        int numsalidasdesbloqueadas =0;
        for (int i = 0; i < arr.length; i++) {
            if (!arr[i]) {
                lista.add(listasalida.get(i));//arrint[i].toString()));
                numsalidasdesbloqueadas++;
            }
        }
       // System.out.println("dios "+ !arr[5]);
        if(numsalidasdesbloqueadas!=0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_item,lista);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp_tipopuerto.setPopupBackgroundResource(R.drawable.campollenarclickeable);

            sp_tipopuerto.setAdapter(adapter);
            return true;
        }else{
            return false;
        }
    }

    private void CargarRecycle(){
        linearcalib.setVisibility(VISIBLE);
        linearadd.setVisibility(GONE);

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
            buttonProvider.getTitle().setText("SERVICE");
            bt_1.setVisibility(VISIBLE);

            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);
            bt_home.setOnClickListener(view -> {

                if(BoolChangeBalanza){
                    System.out.print("ENTRO?");
                    BoolChangeBalanza=false;
                    service.init(true);

                }
                ComService.getInstance().fragmentChangeListener.AbrirFragmentPrincipal();
            });

        }
    }
    @Override
    public void onDestroyView() {
        stoped=true;
        super.onDestroyView();
    }

    //----------------------------------------------------------

    //    private BroadcastReceiver mPairingRequestReceiver = new BroadcastReceiver() {
//        @SuppressLint("MissingPermission")
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//            if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//
//                int pin = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, 0);
//                    System.out.println("ESTE PIN INDICA CONTRASEÑA: "+pin+" SI ES >0");
//                    if(pin>0) {
//
//                        device.setPin(pinglob.getBytes());
//                        device.setPairingConfirmation(true);
//                    }
//            }
//            }
//        };
    @SuppressLint("MissingPermission")
    private void connectToImpresoraBTLE(Context context, String theBtMacAddress, int pos, String pin) {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(theBtMacAddress);

            BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        gatt.discoverServices();
                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        gatt.close();
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        // GUARDAR LOS DATOS DE LA IMPRESORA ; CHIQUITIN
                    }
                }
            };

            // No cerrar inmediatamente: se cierra en onConnectionStateChange
            device.connectGatt(context, false, gattCallback);

        } catch (Exception e) {
            System.out.println("AY NO QUE PASOA " + e.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void setpair(Context context, String address, String pin, Dialog dialog) throws IOException {
         bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean bandbtle = false;
        boolean ispaired = false;
        bluetoothAdapter.startDiscovery();
        for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
            if (device.getAddress().equals(address)) {
                ispaired = true;
                break;
            }
        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        Connection connection = new BluetoothConnection(address);

        if (!ispaired) {
            try {
                connection.open();
                // Idealmente deberías usar reflexión si querés setear el pin

                boolean bondingStarted = device.createBond();
                if (!bondingStarted) {
                    throw new IOException("Falló el bonding");
                }

            } catch (ConnectionException | IOException e) {
                Utils.Mensaje("ERROR FATTALITY " + e.getMessage(), R.layout.item_customtoasterror, activity);
                bandbtle = true;
            } finally {
                try {
                    if (connection.isConnected()) {
                        connection.close();
                    }
                    bluetoothAdapter.cancelDiscovery();

                    if (bandbtle) {
                        connectToImpresoraBTLE(context, address, 10, pin);
                    }

                } catch (ConnectionException e) {
                    Utils.Mensaje("ERROR FATTALITY " + e.getMessage(), R.layout.item_customtoasterror, activity);
                }

                if (!bandbtle) {
                    dialog.cancel();
                }
            }
        }
    }

    public void configurarRed(){
        //conectarse

        // enviar configuracion ( debo pedirla antes)

        // volver a conectarse al antiguo wifi

        // luego hay que crear bucle para CONSULTA, sino es siempre cpnsulta por ahi tenemos problemas
    }
    public void ocultarTeclado(EditText editText) {
        if (editText != null && actividad != null) {
            InputMethodManager imm = (InputMethodManager) actividad.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            }
        }
    }
    public ProgressBar progressBar;
    private void buscarBalanzas(TextView tvbaud, int type) {
        try {
            ListaScanner.clear();
            try{
                multizebra.interruptDiscovery();
               // multizebra.stopDiscovery();
            }catch (Exception e){

            }
            AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
            View view = actividad.getLayoutInflater().inflate(R.layout.dialogolistaimpresoras, null);
            EditText Search= view.findViewById(R.id.Search);
            RecyclerView ListaBalanzas = view.findViewById(R.id.lista);
            AppCompatButton btBack = view.findViewById(R.id.bt_back);
            progressBar = view.findViewById(R.id.progressBar);

            ImageView btSearch= view.findViewById(R.id.btSearch);
            ListaBalanzas.setLayoutManager(new LinearLayoutManager(requireContext()));
          /*
             Process: com.jws.jwsapi, PID: 30091
                                                                                                    java.lang.IllegalStateException: Fragment ServiceFragment{933e7de} (18f1122e-563b-4f02-bd6b-6f4f72076508) not attached to a context.
                                                                                                    	at androidx.fragment.app.Fragment.requireContext(Fragment.java:967)
                                                                                                    	at com.service.ServiceFragment.buscarBalanzas(ServiceFragment.java:1772)
                                                                                                    	at com.service.ServiceFragment.access$500(ServiceFragment.java:76)
                                                                                                    	at com.service.ServiceFragment$12.onClick(ServiceFragment.java:1373)
                                                                                                    	at android.view.View.performClick(View.java:6597)
                                                                                                    	at android.view.View.performClickInternal(View.java:6574)
                                                                                                    	at android.view.View.access$3100(View.java:778)
                                                                                                    	at android.view.View$PerformClick.run(View.java:25885)
                                                                                                    	at android.os.Handler.handleCallback(Handler.java:873)
                                                                                                    	at android.os.Handler.dispatchMessage(Handler.java:99)
                                                                                                    	at android.os.Looper.loop(Looper.java:193)
                                                                                                    	at android.app.ActivityThread.main(ActivityThread.java:6718)
                                                                                                    	at java.lang.reflect.Method.invoke(Native Method)
                                                                                                    	at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:493)
                                                                                                    	at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:858)

            * */
            adapterimpresora = new RecyclerSearcher(requireContext(), ListaScanner);

            LinearLayout lsearch = view.findViewById(R.id.lsearch);
            //lsearch.setVisibility(GONE);
            Search.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });

            ListaBalanzas.setAdapter(adapterimpresora);
            btSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ocultarTeclado(Search);
                }
            });
            GenericDiscovery = new GenericDiscovery(this,progressBar,type);
            GenericDiscovery.execute();

            builder.setView(view);
            AlertDialog dialog2 = builder.create();

            adapterimpresora.setClickListener((v, position) -> {
                String mac = "";
                try {
                    if(adapterimpresora.getname(position).equals("WFO")){
                        configurarRed();
                    }else {
                        mac = adapterimpresora.getAddress(position);
                    }
                } catch (Exception e) {
                    mac = "";
                }

                if (!mac.isEmpty()) {
                    isReceiverRegistered = true;
                    tvbaud.setText(mac);
                    //dialogPin(mac);
                } else {
                    Utils.Mensaje("Reinténtelo, ocurrió un error", R.layout.item_customtoasterror,activity);
                }
                listmac.clear();
                ListaScanner.clear();
                adapterimpresora.removeall();
                GenericDiscovery.interruptDiscovery();
                dialog2.cancel();
            });
            Search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<ZebraStruct> listaFiltrada = new ArrayList<>();
                    if (s.toString().isEmpty()) {
                        adapterimpresora.filterList((ListaScanner));
                    } else {
                        for (ZebraStruct imp : adapterimpresora.getlist()) {
                            if(imp.getname()!=null) {
                                if (!imp.getname().isEmpty() && imp.getname().toLowerCase().contains(s.toString().toLowerCase())) {
                                    listaFiltrada.add(imp);
                                }
                            }
                        }
                        adapterimpresora.filterList(listaFiltrada);
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            btBack.setAlpha(0.4f);
            btBack.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                btBack.setAlpha(1f);
                btBack.setEnabled(true);
            }, 9000);

            btBack.setOnClickListener(v -> {
                GenericDiscovery.interruptDiscovery();
                dialog2.cancel();
            });

            dialog2.show();
            Utils.configureDialogSize(dialog2,getContext());
        } catch (Exception e) {

        }
    }

    private void buscarImpresoras(TextView tvbaud, int type) {
        try {
            ListaScanner.clear();
            try{
                GenericDiscovery.interruptDiscovery();
              //  GenericDiscovery.stopDiscovery();
            }catch (Exception e){

            }
            AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
            View view = actividad.getLayoutInflater().inflate(R.layout.dialogolistaimpresoras, null);
            EditText Search= view.findViewById(R.id.Search);
            RecyclerView listImpresora = view.findViewById(R.id.lista);
            AppCompatButton btBack = view.findViewById(R.id.bt_back);
            progressBar = view.findViewById(R.id.progressBar);

            ImageView btSearch= view.findViewById(R.id.btSearch);
            listImpresora.setLayoutManager(new LinearLayoutManager(requireContext()));
            adapterimpresora = new RecyclerSearcher(requireContext(), ListaScanner);

            LinearLayout lsearch = view.findViewById(R.id.lsearch);
            //lsearch.setVisibility(GONE);
            Search.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return true;
                }
            });
            listImpresora.setAdapter(adapterimpresora);
            btSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ocultarTeclado(Search);
                }
            });
            multizebra = new PrinterDiscovery(this,progressBar,type);
            multizebra.execute();

            builder.setView(view);
            AlertDialog dialog2 = builder.create();

            adapterimpresora.setClickListener((v, position) -> {
                String mac = "";
                try {
                    mac = adapterimpresora.getAddress(position);
                } catch (Exception e) {
                    mac = "";
                }

                if (!mac.isEmpty()) {
                    isReceiverRegistered = true;
                    tvbaud.setText(mac);
                    //dialogPin(mac);
                } else {
                    Utils.Mensaje("Reinténtelo, ocurrió un error", R.layout.item_customtoasterror,activity);
                }
                listmac.clear();
                ListaScanner.clear();
                adapterimpresora.removeall();
                multizebra.interruptDiscovery();
                dialog2.cancel();
            });
            Search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<ZebraStruct> listaFiltrada = new ArrayList<>();
                    if (s.toString().isEmpty()) {
                        adapterimpresora.filterList((ListaScanner));
                    } else {
                        for (ZebraStruct imp : adapterimpresora.getlist()) {
                            if(imp.getname()!=null) {
                                if (!imp.getname().isEmpty() && imp.getname().toLowerCase().contains(s.toString().toLowerCase())) {
                                    listaFiltrada.add(imp);
                                }
                            }
                        }
                        adapterimpresora.filterList(listaFiltrada);
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            btBack.setAlpha(0.4f);
            btBack.setEnabled(false);

            Handler handler = new Handler();
            handler.postDelayed(() -> {
                btBack.setAlpha(1f);
                btBack.setEnabled(true);
            }, 9000);

            btBack.setOnClickListener(v -> {
                multizebra.interruptDiscovery();
                dialog2.cancel();
            });

            dialog2.show();
            Utils.configureDialogSize(dialog2,getContext());
        } catch (Exception e) {
        }
    }
    public boolean isBluetoothAddressValid(String address) {
        String pattern = "^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$";
        return address.matches(pattern);
    }

    @SuppressLint("MissingPermission")
    public void Ping(String ip) {
        new Thread(new Runnable() {








            @Override
            public void run() {


                if (!isBluetoothAddressValid(ip)) {
                    PingTask pingTask = new PingTask(ip, new PingTask.PingCallback() {
                        @Override
                        public void onPingResult(Boolean result) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (result) {
                                        Utils.Mensaje("Equipo en Red", R.layout.item_customtoastok,activity);
                                    } else {
                                        Utils.Mensaje("Ip inaccesible", R.layout.item_customtoasterror,activity);
                                    }

                                }
                            });
                        }
                    });
                    pingTask.execute();
                }else{
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(ip); // Reemplaza con la MAC
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (device != null) {


                                if (pingBluetooth(device)) {
                                    Utils.Mensaje("Equipo Bluetooth en Red", R.layout.item_customtoastok, activity);
                                } else {
                                    Utils.Mensaje("Mac inaccesible", R.layout.item_customtoasterror, activity);
                                }

                            } else {
                                Utils.Mensaje("El ping para Bluetooth no está habilitado", R.layout.item_customtoasterror, activity);
                            }
                        }});
                }
            }
        }).start();
    }
    @SuppressLint("MissingPermission")
    public    Boolean pingBluetooth(BluetoothDevice device) {
        BluetoothSocket socket = null;
        BluetoothGatt gatt = null;
        Handler handler = new Handler(Looper.getMainLooper());
        final boolean[] connectionResult = {false};
        try {
            UUID uuid = device.getUuids()[0].getUuid();
            socket = device.createRfcommSocketToServiceRecord(uuid);
            socket.connect();
            if (socket.isConnected()) {
                socket.close();
                connectionResult[0] = true;
            } else {
                socket.close();
                connectionResult[0] = false;
            }
        } catch (Exception e) {
            try {
                gatt = device.connectGatt(getContext(), false, new BluetoothGattCallback() {
                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        super.onConnectionStateChange(gatt, status, newState);
                        if (newState == BluetoothProfile.STATE_CONNECTED) {
                            gatt.disconnect();
                            gatt.close();
                            connectionResult[0] = true;
                        } else {
                            connectionResult[0]=false;
                            gatt.disconnect();
                            gatt.close();
                        }
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    }
                });

                Thread.sleep(5000);
                gatt.close();
            } catch (Exception ex) {
            } finally {
                // Limpiar cualquier recurso utilizado
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ignored) {
                    }
                }
                if (gatt != null) {
                    gatt.disconnect();
                    gatt.close();
                }
                handler.removeCallbacksAndMessages(null); // Eliminar cualquier Runnable pendiente
            }
        }
        return  connectionResult[0];
    }
//------------------------------------------

}


