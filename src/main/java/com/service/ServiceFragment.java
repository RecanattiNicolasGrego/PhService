package com.service;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.service.Utils.getIPAddress;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
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
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.service.Devices.Balanzas.Clases.BalanzaBase;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import com.service.Devices.Balanzas.Clases.GenericDiscovery;
import com.service.Devices.Impresora.PrinterDiscovery;
import com.service.Comunicacion.PingTask;
import com.service.estructuras.ZebraStruct;
import com.service.estructuras.classDevice;
import com.service.Recyclers.MyRecyclerViewAdapter;
import com.service.Devices.Impresora.ImprimirEstandar;
import com.service.Recyclers.RecyclerDeviceLimpio;
import com.service.Recyclers.RecyclerSearcher;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ServiceFragment extends Fragment {

    Button bt_home, bt_1, bt_2, bt_3, bt_4, bt_5, bt_6/*,btAdd, btback*/;
    LinearLayout linearcalib, linearadd,linearpuertos;
    private ButtonProvider buttonProvider;
    BluetoothAdapter bluetoothAdapter;


    ArrayList<String> listmac = new ArrayList<>();
    Spinner  sp_tipopuerto;
    private  int Permisos=0;
    BalanzaService service;
    FragmentActivity actividad;
    TabLayout tablayout;
    AlertDialog dialog;
    BalanzaService.Balanzas Balanzas;
    RecyclerView recyclerView;
    static Fragment serviceFrgmnt;
    Boolean isReceiverRegistered = false;
    public ArrayList<ZebraStruct> ListaScanner= new ArrayList<>();
    Boolean BoolChangeBalanza=false,stoped = false, programador = false;
    List<String> ListElementsArrayList = new ArrayList<>();
 // jajajajaja
    //jijijiji no lo soñe
    public RecyclerSearcher adapterimpresora;
    RecyclerView recycler;
    MyRecyclerViewAdapter adapter;
    AppCompatActivity activity;

    int menu = 0;

    public PrinterDiscovery multizebra = null;
    public GenericDiscovery GenericDiscovery = null;
    static ArrayList<classDevice> lista,listaglob,listaperdevice;
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
        service = BalanzaService.getInstance();
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
        tablayout = view.findViewById(R.id.tablayout);
        recyclerView = view.findViewById(R.id.recyclerview);
        sp_tipopuerto = view.findViewById(R.id.sp_tipopuerto);
        recycler = view.findViewById(R.id.listview);
        listaperdevice = PreferencesDevicesManager.get_listIndexPorTipo(tablayout.getSelectedTabPosition()-1,activity);
        lista = PreferencesDevicesManager.get_listPorSalida(sp_tipopuerto.getSelectedItem().toString(), tablayout.getSelectedTabPosition()-1,activity);
            tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(@NonNull TabLayout.Tab tab) {
                    int position = tab.getPosition();
                  //  listaperdevice = PreferencesDevicesManager.get_listIndexPorTipo(position-1,activity);
                    ArrayList<classDevice> ls= PreferencesDevicesManager.get_list(activity);

                    menu = position-1;
                    Boolean salidaDisponible = false;
                    boolean[] arr = new boolean[10];
//   boolean[] arr = PreferencesDevicesManager.get_numeroSalidasBZA(activity);
                   // Arrays.fill(arr, true);
                    Integer[] arrint = new Integer[7];
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
                            for (classDevice x :
                                    ls
                            ) {
                                if (x.getSalida() != null  && x.getTipo().equals(PreferencesDevicesManager.obtenerTipoPorIndice(tablayout.getSelectedTabPosition()-1)) ) {
                                    if (x.getSalida().equals("PuertoSerie 1")) {
                                        arr[0] = false;
                                        arrint[0]++;
                                    }

                                    if (x.getSalida().equals("PuertoSerie 2")) {
                                        arr[1] = false;
                                        arrint[1]++;

                                    }

                                    if (x.getSalida().equals("PuertoSerie 3")) {
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

                                        if (x.getSalida().equals("PuertoSerie 1")) {
                                            arr[0] = true;
                                            arrint[0]--;
                                        }

                                        if (x.getSalida().equals("PuertoSerie 2")) {
                                            arr[1] = true;
                                            arrint[1]--;
                                        }

                                        if (x.getSalida().equals("PuertoSerie 3")) {
                                            arr[2] = true;
                                            arrint[2]--;
                                        }
                                    }


                            }

                            salidaDisponible = ModificacionImpresoras(arr, arrint);
                            break;
                        }
                        case 3:
                        case 2:{
                            linearpuertos.setVisibility(VISIBLE);
                            for (classDevice x : ls) {
                                if (x.getSalida() != null && x.getTipo().equals(PreferencesDevicesManager.obtenerTipoPorIndice(tablayout.getSelectedTabPosition() - 1))) {
                                    if (x.getSalida().equals("PuertoSerie 1")) {
                                        arr[0] = false;
                                        arrint[0]++;
                                    }

                                    if (x.getSalida().equals("PuertoSerie 2")) {
                                        arr[1] = false;
                                        arrint[1]++;
                                    }

                                    if (x.getSalida().equals("PuertoSerie 3")) {
                                        arr[2] = false;
                                        arrint[2]++;
                                    }
                                } else if (x.getSalida() != null) {
                                    if (x.getSalida().equals("PuertoSerie 1")) {
                                        arr[0] = true;
                                        arrint[0]--;
                                    }

                                    if (x.getSalida().equals("PuertoSerie 2")) {
                                        arr[1] = true;
                                        arrint[1]--;
                                    }

                                    if (x.getSalida().equals("PuertoSerie 3")) {
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
                            salidaDisponible = ModificacionImpresoras(arr, arrint);
                            break;
                        } case 4:  case 5: {
                            linearpuertos.setVisibility(VISIBLE);
                            for (classDevice x : ls) {

                                if (x.getSalida() != null && x.getTipo().equals(PreferencesDevicesManager.obtenerTipoPorIndice(tablayout.getSelectedTabPosition()-1))) {
                                    if (x.getSalida().equals("PuertoSerie 1")) {
                                        arr[0] = false;
                                        arrint[0]++;
                                    }

                                    if (x.getSalida().equals("PuertoSerie 2")) {
                                        arr[1] = false;
                                        arrint[1]++;
                                    }

                                    if (x.getSalida().equals("PuertoSerie 3")) {
                                        arr[2] = false;
                                        arrint[2]++;
                                    }

                                }else if(x.getSalida()!=null) {
                                    if (x.getSalida().equals("PuertoSerie 1")) {
                                        arr[0] = true;
                                        arrint[0]--;
                                    }

                                    if (x.getSalida().equals("PuertoSerie 2")) {
                                        arr[1] = true;
                                        arrint[1]--;
                                    }

                                    if (x.getSalida().equals("PuertoSerie 3")) {
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
                            salidaDisponible = ModificacionImpresoras(arr, arrint);
                            break;
                        }
                    }
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
            TabLayout.Tab tab = tablayout.getTabAt(2); // Índice basado en 0
            if (tab != null) {
                tab.select();
                View tabView = ((ViewGroup) tablayout.getChildAt(0)).getChildAt(0);
                //if (tabView != null) {
               //     tabView.setVisibility(GONE);}
                tabView = ((ViewGroup) tablayout.getChildAt(0)).getChildAt(1);
                if (tabView != null) {
                    tabView.setVisibility(GONE);
                }  tabView = ((ViewGroup) tablayout.getChildAt(0)).getChildAt(3);
                if (tabView != null) {
                    tabView.setVisibility(GONE);
                }//  tabView = ((ViewGroup) tablayout.getChildAt(0)).getChildAt(4);
                //if (tabView != null) {
                //    tabView.setVisibility(GONE);
                //} tabView = ((ViewGroup) tablayout.getChildAt(0)).getChildAt(5);
                //if (tabView != null) {
                //    tabView.setVisibility(GONE);
                //}
            }
        }else if (Permisos<=2){
          bt_1.setVisibility(GONE);
      }
        LCalibracioninit();
        CargarRecyclerBzas();
        try {
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            ArrayList<Integer> list = PreferencesDevicesManager.getBalanzas(activity);
            Boolean tienecalibracion=false;
            for (int i = 0; i < list.size(); i++) {
                tienecalibracion =BalanzaService.ModelosClasesBzas.values()[list.get(i)].getTieneCal();
                if(tienecalibracion){
                    ListElementsArrayList.add("Calibracion Balanza " + String.valueOf(i + 1));
                }else{
                    ListElementsArrayList.add("Balanza " + String.valueOf(i + 1)+" no tiene calibracion");

                }
            }
            adapter = new MyRecyclerViewAdapter(getContext(), ListElementsArrayList, activity);
            adapter.setClickListener((view1, position) -> {

                if(BalanzaService.ModelosClasesBzas.values()[list.get(position)].getTieneCal()) {

                Balanzas.setEstado(position+1, BalanzaBase.M_MODO_CALIBRACION);
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
        sp_tipopuerto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String value = String.valueOf(sp_tipopuerto.getItemAtPosition(i));
                CargarDatosRecycler(value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void CargarDatosRecycler(String Salida) {
        int tipodevice= tablayout.getSelectedTabPosition()-1;
        if(tipodevice!=-1) {
            try {
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                lista.clear();
                lista = PreferencesDevicesManager.get_listPorSalida(Salida, tipodevice,activity);
                listaglob = PreferencesDevicesManager.get_listIndex(activity);
                listaperdevice = PreferencesDevicesManager.get_listIndexPorTipo(tablayout.getSelectedTabPosition() - 1,activity);
            } catch (Exception e) {
            } finally {
                addapterItem(Salida);
            }
        }else{
            lista.clear();

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            lista = (ArrayList<classDevice>) PreferencesDevicesManager.organizarDispositivos(PreferencesDevicesManager.get_list(activity));//lista = ordenarLista(PreferencesDevicesManager.get_list(activity)); //ordenarLista(PreferencesDevicesManager.get_list(activity));
            listaglob = PreferencesDevicesManager.get_listIndex(activity);
            listaperdevice = PreferencesDevicesManager.get_listIndexPorTipo(tablayout.getSelectedTabPosition() - 1,activity);
        }
        try {
            RecyclerDeviceLimpio.ItemClickListener itemClickListener = new RecyclerDeviceLimpio.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position, classDevice Device, String Salida, int TipoDevice) {
                    showdialog(Device, Salida);
                }
            };
            if (lista.size() >= 1) {
                RecyclerDeviceLimpio adapter = new RecyclerDeviceLimpio(getContext(), lista, activity, itemClickListener, sp_tipopuerto.getSelectedItem().toString(), tablayout.getSelectedTabPosition()-1);
                recyclerView.setAdapter(adapter);
            }
        } catch (Exception e) {
        }
    }

    public void LCalibracioninit() {
        if(Permisos>=3){//if((programador)){
            TabLayout.Tab tab = tablayout.getTabAt(0); // Índice basado en 0
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
                listaperdevice = PreferencesDevicesManager.get_listIndexPorTipo(tablayout.getSelectedTabPosition()-1,activity);
                boolean[] arr = new boolean[10];
//boolean[] arr = PreferencesDevicesManager.get_numeroSalidasBZA(activity );
                Integer[] arrint= new Integer[7];
                for (int i = 0; i < arrint.length; i++) {
                    arrint[i] = 0; // Inicializa todos los elementos a 0
                }

                for (classDevice x :
                        listaperdevice
                ) {

                    if (x.getSalida() != null) {
                        if (x.getSalida().equals("PuertoSerie 1")) {
                            arr[0] = false;
                            arrint[0]++;
                        }

                        if (x.getSalida().equals("PuertoSerie 2")) {
                            arr[1] = false;
                            arrint[1]++;
                        }

                        if (x.getSalida().equals("PuertoSerie 3")) {
                            arr[2] = false;
                            arrint[2]++;
                        }
                        if (x.getSalida().equals("Red")) {
                            arr[3] = false;
                            arrint[3]++;
                        }
                        if (x.getSalida().equals("Bluetooth")) {
                            arr[4] = false;
                            arrint[4]++;
                        }
                        if (x.getSalida().equals("USB")) {
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
                    CargarDatosRecycler("Red");
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
                    ComService.getInstance().fragmentChangeListener.openFragmentPrincipal();
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
                        Boolean   tienecalibracion =BalanzaService.ModelosClasesBzas.values()[list.get(i)].getTieneCal();
                        if(tienecalibracion){
                            ListElementsArrayList.add("Calibracion Balanza " + String.valueOf(i + 1));
                        }else{
                        ListElementsArrayList.add("Balanza " + String.valueOf(i + 1)+" no tiene calibracion");
                    }}
                    adapter = new MyRecyclerViewAdapter(getContext(), ListElementsArrayList, activity);
                    adapter.setClickListener((view1, position) -> {

                        if(BalanzaService.ModelosClasesBzas.values()[list.get(position)].getTieneCal()) {
                            Balanzas.setEstado(position + 1, BalanzaBase.M_MODO_CALIBRACION);
                            Balanzas.openCalibracion(position + 1);
                        }
                    });
                    recycler.setAdapter(adapter);

                } catch (Exception e) {
                }}
            ListElementsArrayList.clear();
            ArrayList<Integer> list = PreferencesDevicesManager.getBalanzas(activity);
            for (int i = 0; i < list.size(); i++) {
                Boolean  tienecalibracion =BalanzaService.ModelosClasesBzas.values()[list.get(i)].getTieneCal();
                if(tienecalibracion){
                    ListElementsArrayList.add("Calibracion Balanza " + String.valueOf(i + 1));
                }else{
                    ListElementsArrayList.add("Balanza " + String.valueOf(i + 1)+" no tiene calibracion");
                }
            }
            adapter = new MyRecyclerViewAdapter(getContext(), ListElementsArrayList, activity);
            adapter.setClickListener((view1, position) -> {
                if(BalanzaService.ModelosClasesBzas.values()[list.get(position)].getTieneCal()) {
                    Balanzas.setEstado(position + 1, BalanzaBase.M_MODO_CALIBRACION);
                    Balanzas.openCalibracion(position + 1);
                }
            });
            recycler.setAdapter(adapter);
        });
    }
    private int indexofservicedevice(ArrayList<classDevice> arr, classDevice b) {
        int response = 0;
        int count = 0;
        if (b.getTipo() != PreferencesDevicesManager.valordef) {
            for (classDevice a : arr) {
                if (a.getND() == b.getND()) {
                    response = count;
                }
                count++;
            }
        } else {
            response = 0;
        }
        return response;
    }

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
   private void connectToImpresoraBTLE(String theBtMacAddress, int pos, String pin) {
       try {
           bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
           BluetoothDevice device = bluetoothAdapter.getRemoteDevice(theBtMacAddress);

           BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
               @Override
               public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                   if (newState == BluetoothProfile.STATE_CONNECTED) {
                       gatt.discoverServices();
                   } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                       // Desconectado, cerrar gatt
                     //  System.out.println("disconnected");
                       gatt.close();
                   }
               }

               @Override
               public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                   if (status == BluetoothGatt.GATT_SUCCESS) {
                       if (gatt.getDevice().getBondState() != BluetoothDevice.BOND_BONDED) {
                           gatt.getDevice().createBond();
                       }

                      // GUARDAR LOS DATOS DE LA IMPRESORA ; CHIQUITIN
                   }
               }

               @Override
               public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                   // Manejo de lectura de características si es necesario
               }
           };

           BluetoothGatt gatt = device.connectGatt(getContext(), false, gattCallback);
       } catch (Exception e) {
       }
   }

   @SuppressLint("MissingPermission")
   private void setpair(String address, String pin, Dialog dialog) throws IOException {
       boolean bandbtle = false;
       boolean ispaired = false;

       if (bluetoothAdapter == null) {
           bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       }
       Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
       for (BluetoothDevice device : pairedDevices) {
           // Aquí puedes comparar el address del dispositivo con el que deseas verificar
           if (device.getAddress().equals(address)) {
               // El dispositivo está vinculado
               ispaired =true;
           }
       }
       BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);// Reemplaza con la dirección MAC
       Connection connection = new BluetoothConnection(address);
        if(!ispaired) {
            try {
                connection.open();
                if (!pin.isEmpty()) {
                    device.setPin(pin.getBytes());
                }
                device.createBond();

                // Enviar datos a la impresora aquí
            } catch (ConnectionException e) {
                bandbtle = true;
            } finally {
                try {
                    if (connection.isConnected()) {
                    }
                    connection.close();

                    bluetoothAdapter.cancelDiscovery();

                    if (bandbtle) {
                        try {
                            connectToImpresoraBTLE(address, 10, pin);
                        } catch (Exception e) {
                        }
                    }
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                } catch (ConnectionException e) {
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
        ListaScanner.clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(actividad);
        View view = actividad.getLayoutInflater().inflate(R.layout.dialogolistaimpresoras, null);
        EditText Search= view.findViewById(R.id.Search);
        RecyclerView ListaBalanzas = view.findViewById(R.id.lista);
        AppCompatButton btBack = view.findViewById(R.id.bt_back);
        progressBar = view.findViewById(R.id.progressBar);

        ImageView btSearch= view.findViewById(R.id.btSearch);
        ListaBalanzas.setLayoutManager(new LinearLayoutManager(requireContext()));
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
                if(adapterimpresora.getTipo(position).equals("WFO")){
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
    }

    private void buscarImpresoras(TextView tvbaud, int type) {
        ListaScanner.clear();
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
        TextView tvBaud = mView.findViewById(R.id.tv_Baud1);
        TextView ip = mView.findViewById(R.id.tv_Baud);
        Button buscador =mView.findViewById(R.id.buscadorbt);
        buscador.setVisibility(GONE);
        LinearLayout Lip= mView.findViewById(R.id.linearLayout4);
        TextView tvStop = mView.findViewById(R.id.tv_Stopbit);
        TextView tvData = mView.findViewById(R.id.tv_Databit);
        TextView tvparity = mView.findViewById(R.id.tv_Parity);
        LinearLayout Lid = mView.findViewById(R.id.linearLayout3);
        Lid.setVisibility(GONE);
        TextView nombreDialog = mView.findViewById(R.id.numMOD);
        LinearLayout Lrs232 = mView.findViewById(R.id.Lrs232);
        LinearLayout Lelse= mView.findViewById(R.id.Lelse);
        Spinner sp_Modelo = mView.findViewById(R.id.sp_port);

        ArrayAdapter<String> adapter11 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayout.getSelectedTabPosition()-1)));
        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_Modelo.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_Modelo.setAdapter(adapter11);
        sp_Modelo.setSelection(PreferencesDevicesManager.obtenerIndiceModeloPorTipo((tablayout.getSelectedTabPosition()-1),Device.getModelo()));
        if(sp_tipopuerto.getSelectedItem().toString().contains("Puerto Serie")){
            Lrs232.setVisibility(VISIBLE);
        }else{
            if(sp_tipopuerto.getSelectedItem().toString().contains("Red")){
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
                if(sp_tipopuerto.getSelectedItem().toString().contains("Red")) {
                        if (!PreferencesDevicesManager.obtenerAliasDeModelos(BalanzaService.ModelosClasesDispositivos.values()).get(sp_Modelo.getSelectedItemPosition()).contains("Master")) {
                            Lip.setClickable(false);
                            ip.setClickable(false);
                            ip.setText(getIPAddress(true));
                        } else {
                            Lip.setClickable(true);
                            ip.setClickable(true);
                            ip.setText("");
                        }
            }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        nombreDialog.setText(("Nº Dispositivo " + (Device.getNDL())));
        if(Device.getDireccion()!=null) {
            tvBaud.setText(Device.getDireccion().size() > 0 ? String.valueOf(Device.getDireccion().get(0)) : PreferencesDevicesManager.DefConfig.get(0));
            tvStop.setText(Device.getDireccion().size() > 1 ? String.valueOf(Device.getDireccion().get(1)) : PreferencesDevicesManager.DefConfig.get(1));
            tvData.setText(Device.getDireccion().size() > 2 ? String.valueOf(Device.getDireccion().get(2)) : PreferencesDevicesManager.DefConfig.get(2));
            tvparity.setText(Device.getDireccion().size() > 3 ? String.valueOf(Device.getDireccion().get(3)) : PreferencesDevicesManager.DefConfig.get(3));
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
        tvBaud.setOnClickListener(View -> {
            Utils.dialogTextNumber(tvBaud, "", "Ingrese Baud",actividad);
        });
        tvparity.setOnClickListener(View -> {

            Utils.dialogTextNumber(tvparity, "", "Ingrese Parity",actividad);
        });
        tvStop.setOnClickListener(View -> {

            Utils.dialogTextNumber(tvStop, "", "Ingrese stop bit",actividad);
        });
        tvData.setOnClickListener(View -> {

            Utils.dialogTextNumber(tvData, "", "Ingrese data bit",actividad);
        });
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
            if(sp_Modelo.getSelectedItemPosition()==0 && PreferencesDevicesManager.salidaMap.get(sp_tipopuerto.getSelectedItem().toString()).equals("Red")){
                Utils.Mensaje("Funcion no disponible en RED",R.layout.item_customtoasterror,activity);
            }else{
                if((!tvBaud.getText().toString().equals("") && !tvparity.getText().toString().equals("") && !tvData.getText().toString().equals("") && !tvStop.getText().toString().equals("")) && sp_tipopuerto.getSelectedItem().toString().contains("Puerto Serie")||sp_tipopuerto.getSelectedItem().toString().contains("Puerto Serie") && sp_Modelo.getSelectedItemPosition()!=0|| sp_tipopuerto.getSelectedItem().toString().contains("Red") && !ip.getText().toString().equals("")) {
                    try {
                        classDevice newDevice = new classDevice();
                        int position = tablayout.getSelectedTabPosition() - 1;
                        newDevice.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(position));
                        String value2 = sp_tipopuerto.getSelectedItem().toString();
                        ArrayList<String> listaux = new ArrayList<>();
                        String value ="";
                        if(sp_Modelo.getVisibility()==View.VISIBLE ) {
                            value= sp_Modelo.getSelectedItem().toString();
                        }else{
                            value = (PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayout.getSelectedTabPosition()-1)).get(0));
                        }
                        newDevice.setModelo(value);
                        newDevice.setSalida(PreferencesDevicesManager.salidaMap.get(value2));
                        if(PreferencesDevicesManager.salidaMap.get(value2).contains("PuertoSerie")){
                            listaux.add(tvBaud.getText().toString());
                            listaux.add(tvData.getText().toString());
                            listaux.add(tvStop.getText().toString());
                            listaux.add(tvparity.getText().toString());
                        }else{
                                listaux.add(ip.getText().toString());
                        }
                        newDevice.setDireccion(listaux);

                        newDevice.setSeteo(true);
                        newDevice.setID(1);
                        newDevice.setND(Device.getND());
                        PreferencesDevicesManager.addDevice(newDevice,activity);
                        BoolChangeBalanza=true;
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                                recyclerView.getAdapter().notifyDataSetChanged();}});
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
        TextView tvBaud = mView.findViewById(R.id.tv_Baud1);

            TextView tvStop = mView.findViewById(R.id.tv_Stopbit);
            TextView tvData = mView.findViewById(R.id.tv_Databit);
            TextView tvparity = mView.findViewById(R.id.tv_Parity);
        TextView nombreDialog = mView.findViewById(R.id.numMOD);
        nombreDialog.setText(("Nº Escaner " + (Device.getNDL())));
        LinearLayout Lelse= mView.findViewById(R.id.Lelse);
        LinearLayout Lrs232 = mView.findViewById(R.id.Lrs232);
        Lelse.setVisibility(GONE);
        Lrs232.setVisibility(VISIBLE);
        if(Device.getDireccion()!=null) {
            tvBaud.setText(Device.getDireccion().size() > 0 ? String.valueOf(Device.getDireccion().get(0)) : PreferencesDevicesManager.DefConfig.get(0));
            tvStop.setText(Device.getDireccion().size() > 1 ? String.valueOf(Device.getDireccion().get(1)) : PreferencesDevicesManager.DefConfig.get(1));
            tvData.setText(Device.getDireccion().size() > 2 ? String.valueOf(Device.getDireccion().get(2)) : PreferencesDevicesManager.DefConfig.get(2));
            tvparity.setText(Device.getDireccion().size() > 3 ? String.valueOf(Device.getDireccion().get(3)) : PreferencesDevicesManager.DefConfig.get(3));
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
        tvBaud.setOnClickListener(View -> {
            Utils.dialogTextNumber(tvBaud, "", "Ingrese Baud",actividad);
        });
        tvparity.setOnClickListener(View -> {

            Utils.dialogTextNumber(tvparity, "", "Ingrese Parity",actividad);
        });
        tvStop.setOnClickListener(View -> {

            Utils.dialogTextNumber(tvStop, "", "Ingrese stop bit",actividad);
        });
        tvData.setOnClickListener(View -> {

            Utils.dialogTextNumber(tvData, "", "Ingrese data bit",actividad);
        });

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
            if((tvBaud.getVisibility() == GONE||!tvBaud.getText().toString().equals("")) && (tvparity.getVisibility() == GONE||!tvparity.getText().toString().equals("")) && (tvData.getVisibility() == GONE||!tvData.getText().toString().equals("")) && (tvStop.getVisibility() == GONE||!tvStop.getText().toString().equals(""))) {
                try {
                    classDevice x = new classDevice();

                    int position = tablayout.getSelectedTabPosition() - 1;
                    x.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(position));
                    String value2 = sp_tipopuerto.getSelectedItem().toString();

                    ArrayList<String> listaux = new ArrayList<>();
                    x.setModelo(PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(position)).get(0)); // Expansion
                    x.setSalida(PreferencesDevicesManager.salidaMap.get(value2));
                    /*switch (value2) {
                        case "Puerto Serie 1": {
                            x.setSalida("PuertoSerie 1");
                            listaux.add(tvBaud.getText().toString());
                            listaux.add(tvStop.getText().toString());
                            listaux.add(tvData.getText().toString());
                            listaux.add(tvparity.getText().toString());
                            break;
                        }
                        case "Puerto Serie 2": {
                            x.setSalida("PuertoSerie 2");
                            listaux.add(tvBaud.getText().toString());
                            listaux.add(tvStop.getText().toString());
                            listaux.add(tvData.getText().toString());
                            listaux.add(tvparity.getText().toString());
                            break;
                        }
                        case "Puerto Serie 3": {
                            x.setSalida("PuertoSerie 3");

                            break;
                        }
                    }*/
                    listaux.add(tvBaud.getText().toString());
                    listaux.add(tvData.getText().toString());
                    listaux.add(tvStop.getText().toString());
                    listaux.add(tvparity.getText().toString());

                    x.setDireccion(listaux);
                    x.setSeteo(true);
                    x.setID(1);
                    x.setND(Device.getND());
                    PreferencesDevicesManager.addDevice(x,activity);
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });
                    dialog.cancel();
                } catch (Exception e) {
                    Utils.Mensaje("Error, deben ser todos los valores numeros enteros", R.layout.item_customtoasterror, activity);
                    dialog.cancel();
                }
            }else{
                Utils.Mensaje("Todavia hay valores vacios", R.layout.item_customtoasterror, activity);
            }});
    }

    private void dialogoExpansion(classDevice Device) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(actividad);
        View mView = null;
        mView = actividad.getLayoutInflater().inflate(R.layout.dialogo_devices, null);
        LinearLayout LtvSlave = mView.findViewById(R.id.linearLayout3);
        LtvSlave.setVisibility(GONE); // POR AHORA LPMLPMLPMLPM
        Spinner sp_Modelo = mView.findViewById(R.id.sp_port);
        TextView nombreDialog = mView.findViewById(R.id.numMOD);

        nombreDialog.setText(("Nº Expansion " + (Device.getNDL())));
        ArrayAdapter<String> adapter11 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayout.getSelectedTabPosition()-1)));
        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_Modelo.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_Modelo.setAdapter(adapter11);
        sp_Modelo.setSelection(PreferencesDevicesManager.obtenerIndiceModeloPorTipo((tablayout.getSelectedTabPosition()-1),Device.getModelo()));
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
                int position = tablayout.getSelectedTabPosition() - 1;
                x.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(position));
                String value2 = sp_tipopuerto.getSelectedItem().toString();
                ArrayList<String> listaux = new ArrayList<>();
                x.setSalida(PreferencesDevicesManager.salidaMap.get(value2));
               /* switch (value2) {
                    case "Puerto Serie 1": {
                        x.setSalida("PuertoSerie 1");
                        break;
                    }
                    case "Puerto Serie 2": {
                        x.setSalida("PuertoSerie 2");
                        break;
                    }
                    case "Puerto Serie 3": {
                        x.setSalida("PuertoSerie 3");
                        break;
                    }
                }*/
                String value ="";
                 value= sp_Modelo.getSelectedItem().toString();
                   listaux = BalanzaService.ModelosClasesExpansiones.valueOf(value).getConfiguraciones();
                x.setModelo(value);
                x.setDireccion(listaux);
                x.setSeteo(true);
                x.setID(1);
                x.setND(Device.getND());
                PreferencesDevicesManager.addDevice(x,activity);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                        recyclerView.getAdapter().notifyDataSetChanged();
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
        if(sp_tipopuerto.getSelectedItem().toString().contains("Puerto Serie")){
                macIp.setVisibility(GONE);
            LPin.setVisibility(GONE);
            }else if(sp_tipopuerto.getSelectedItem().toString().contains("Bluetooth")||sp_tipopuerto.getSelectedItem().toString().contains("Red")){
               LMacIp.setVisibility(VISIBLE);
               Lsp_Modelo.setVisibility(GONE);
               macIp.setVisibility(VISIBLE);
               searchbt.setVisibility(VISIBLE);
               if(sp_tipopuerto.getSelectedItem().toString().contains("Red")){
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

               }else if (sp_tipopuerto.getSelectedItem().toString().contains("Bluetooth")){
                   nombreMacIP.setText("MAC");
                   Pin.setVisibility(VISIBLE);
                   LPin.setVisibility(VISIBLE);
                   nombrePin.setText("Pin");
                   searchbt.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
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
           }else if (sp_tipopuerto.getSelectedItem().toString().contains("USB")){
               LMacIp.setVisibility(GONE);
               LPin.setVisibility(GONE);
               Lsp_Modelo.setVisibility(GONE);
               macIp.setVisibility(GONE);
           }
        ArrayAdapter<String> adapter11 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayout.getSelectedTabPosition()-1)));
        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_Modelo.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_Modelo.setAdapter(adapter11);
        sp_Modelo.setSelection(PreferencesDevicesManager.obtenerIndiceModeloPorTipo(tablayout.getSelectedTabPosition()-1,Device.getModelo()));
        if(indexofservicedevice(lista,Device)>=1){
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

        }else if(sp_tipopuerto.getSelectedItem().toString().contains("Red") || sp_tipopuerto.getSelectedItem().toString().contains("Bluetooth")) {
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
                    int position = tablayout.getSelectedTabPosition() - 1;
                    x.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(position));
                    String value2 = sp_tipopuerto.getSelectedItem().toString();
                    ArrayList<String> listaux = new ArrayList<>();
                    // ZEBRA
                    x.setSalida(PreferencesDevicesManager.salidaMap.get(value2));

                    switch (value2) {
                        case "Puerto Serie 1":
                        case "Puerto Serie 3":
                        case "Puerto Serie 2": {
                            listaux.add(ImprimirEstandar.BaudZebradef);
                            listaux.add(ImprimirEstandar.StopBZebradef);
                            listaux.add(ImprimirEstandar.DataBZebradef);
                            listaux.add(ImprimirEstandar.ParityZebradef);
                            break;
                        }
                        case "Red": {
                            listaux.add(Address);
                            break;
                        }
                        case "Bluetooth": {
                            listaux.add(Address);
                            String finalAddress = Address;

                                    try {
                                        setpair(finalAddress, Pin.getText().toString(),dialog); // probar
                                    } catch (IOException e) {

                                     Utils.Mensaje("fallo el emparejamiento",R.layout.item_customtoasterror,activity);
                                            }
                                    break;
                        }

                    }
                    if(sp_Modelo.getVisibility()==View.VISIBLE && Lsp_Modelo.getVisibility()==View.VISIBLE) {
                        x.setModelo(sp_Modelo.getSelectedItem().toString());
                    }else{
                        x.setModelo(PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayout.getSelectedTabPosition()-1)).get(0));
                    }
                    x.setDireccion(listaux);
                    x.setSeteo(true);
                    x.setID(1);
                    x.setND(Device.getND());
                    PreferencesDevicesManager.addDevice(x,activity);
                } catch (Exception e) {
                    Utils.Mensaje("Error, deben ser todos los valores numeros enteros", R.layout.item_customtoasterror, activity);
                    dialog.cancel();
                }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                            recyclerView.getAdapter().notifyDataSetChanged();
                        }
                    });
                    if( sp_tipopuerto.getSelectedItem().toString().contains("Bluetooth")){
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
            LinearLayout LtvSlaveID = mView.findViewById(R.id.linearLayout3);
            TextView SlaveID = mView.findViewById(R.id.tv_Slave);
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

        LtvSlaveID.setVisibility(GONE);// POR AHORA NO SERA SETEABLE HASTA HACER LOS PROTOCOLOS CON ID LUEGO SOLO SERA PARA LOS DISPOSITIVOS SIN ID
        ArrayAdapter<String> adapter11 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, PreferencesDevicesManager.obtenerModelosDeTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayout.getSelectedTabPosition()-1)));
        adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_Modelo.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_Modelo.setAdapter(adapter11);
        sp_Modelo.setSelection(PreferencesDevicesManager.obtenerIndiceModeloPorTipo((tablayout.getSelectedTabPosition()-1),Device.getModelo()));
        if(lista.size()>2){
            sp_Modelo.setSelection(PreferencesDevicesManager.obtenerIndiceModeloPorTipo((tablayout.getSelectedTabPosition()),Device.getModelo()));
            sp_Modelo.setEnabled(false); // POR AHORA PROBAR
            sp_Modelo.setClickable(false);
        }else{
            if(lista.size()!=1 &&!Device.getSeteo()){
                sp_Modelo.setSelection(PreferencesDevicesManager.obtenerIndiceModeloPorTipo((tablayout.getSelectedTabPosition()),Device.getModelo()));
                sp_Modelo.setEnabled(false);
                sp_Modelo.setClickable(false);
            }
        }
        if(Device.getID()!=-1){
            SlaveID.setText(String.valueOf(Device.getID()));
        }
        SlaveID.setOnClickListener(View ->{
            Utils.dialogTextNumber(SlaveID,"","Ingrese ID",actividad);
        });

        LPin.setVisibility(GONE);// POR AHORA NO SERA SETEABLE HASTA HACER LOS PROTOCOLOS CON ID LUEGO SOLO SERA PARA LOS DISPOSITIVOS SIN ID
        if(sp_tipopuerto.getSelectedItem().toString().contains("Puerto Serie")){
            macIp.setVisibility(GONE);
        }else if(sp_tipopuerto.getSelectedItem().toString().contains("Bluetooth")||sp_tipopuerto.getSelectedItem().toString().contains("Red")){
            LMacIp.setVisibility(VISIBLE);
            Lsp_Modelo.setVisibility(GONE);

            macIp.setVisibility(VISIBLE);
            searchbt.setVisibility(VISIBLE);

            if(sp_tipopuerto.getSelectedItem().toString().contains("Red")){
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
            }else if (sp_tipopuerto.getSelectedItem().toString().contains("Bluetooth")){
                nombreMacIP.setText("MAC");
                Pin.setVisibility(VISIBLE);
                LPin.setVisibility(VISIBLE);
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
        remove.setOnClickListener(view ->{
            if(listaperdevice.size()>1) {
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
            if(LtvSlaveID.getVisibility()==GONE||!SlaveID.getText().toString().equals("")||macIp.getVisibility()==VISIBLE &&!macIp.getText().toString().isEmpty()) {
                int Slave = 0;
                try {
                    classDevice x = new classDevice();

                    if (SlaveID != null && SlaveID.getVisibility() == VISIBLE) {
                        Slave = Integer.parseInt(SlaveID.getText().toString());
                    }
                    ArrayList<String> listaux = new ArrayList<>();

                    int position = tablayout.getSelectedTabPosition() - 1;
                    if(sp_Modelo.getVisibility()==View.VISIBLE) {
                        String value = sp_Modelo.getSelectedItem().toString();
                        listaux = BalanzaService.ModelosClasesBzas.valueOf(value).getConfiguraciones();
                        x.setModelo(sp_Modelo.getSelectedItem().toString());
                    }else{
                        if(sp_tipopuerto.getSelectedItem().toString().contains("Red")){
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
                    x.setID(1);
                    x.setSeteo(true);
                    x.setND(Device.getND());
                    PreferencesDevicesManager.addDevice(x,activity);
                    BoolChangeBalanza = true;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                            recyclerView.getAdapter().notifyDataSetChanged();

                        }
                    });
                    dialog.cancel();
                } catch (Exception e) {
                    Utils.Mensaje("Error, deben ser todos los valores numeros enteros", R.layout.item_customtoasterror, activity);
                    dialog.cancel();
                }
            }else{
                Utils.Mensaje("Todavia hay valores vacios", R.layout.item_customtoasterror, activity);
            }
        });
    }
    private void addapterItem(String Salida){
        if(!Salida.equals("NO HAY SALIDAS")  && tablayout.getSelectedTabPosition()!=0 && Permisos>=2){//programador) {
        try {
               classDevice Adapteritem = new classDevice();
                Adapteritem.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayout.getSelectedTabPosition()-1));
                Adapteritem.setModelo(PreferencesDevicesManager.valordef);
                Adapteritem.setSalida(Salida);
                Adapteritem.setID(-1);
                Adapteritem.setNDL(listaperdevice.get(listaperdevice.size()-1).getNDL()+1);
                Adapteritem.setSeteo(false);
                Adapteritem.setND(listaglob.get(listaglob.size() - 1).getND() + 1);
                ArrayList<String> diraux = new ArrayList<String>();
                Adapteritem.setDireccion(diraux);
                lista.add(Adapteritem);
            } catch (Exception e) {
           // System.out.println("ERROR EN ADDAPTER WN");
                classDevice Adapteritem  = new classDevice();
                Adapteritem.setTipo(PreferencesDevicesManager.obtenerTipoPorIndice(tablayout.getSelectedTabPosition()-1));
                Adapteritem.setNDL(0);
                Adapteritem.setModelo(PreferencesDevicesManager.valordef);
                Adapteritem.setSalida(Salida);
                Adapteritem.setID(-1);
                Adapteritem.setSeteo(false);
                Adapteritem.setND(0);
                ArrayList<String> diraux= new ArrayList<String>();
                Adapteritem.setDireccion(diraux);
                lista.add(Adapteritem);
            }
        }
    }

    private void showdialog(classDevice Device, String Salida){
        Device.setID(0);//POR AHORA VA A SER 0 POR DEFAULT HASTA HACER LO DEL I
        if(PreferencesDevicesManager.salidaMap.containsKey(Salida)) {
            switch (tablayout.getSelectedTabPosition() - 1) {
                case 0: {
                    dialogBalanza(Device);
                    break;
                }
                case 1: {
                    dialogImpresora(Device);
                    break;
                }
                case 2: {
                    dialogoExpansion(Device);
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


        int numsalidasdesbloqueadas =0;
        if(!arr[0]){
            lista.add("Puerto Serie 1");//arrint[0].toString()));
            numsalidasdesbloqueadas++;

        }
        if(!arr[1]){
            lista.add("Puerto Serie 2");//arrint[1].toString()));
            numsalidasdesbloqueadas++;
        }
        if(!arr[2]){
            lista.add("Puerto Serie 3");//arrint[2].toString()));
            numsalidasdesbloqueadas++;
        }
        if (!arr[3]) {
            numsalidasdesbloqueadas++;
            lista.add("Red");//arrint[3].toString()));
        }
        if (!arr[4]){

            numsalidasdesbloqueadas++;
            lista.add(("Bluetooth"));//arrint[4].toString()));
        }
        if (!arr[5]){
            numsalidasdesbloqueadas++;
            lista.add(("USB"));//arrint[5].toString()));
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
    private boolean ModificacionDispositivos(boolean[] arr,Integer[] arrint){
        ArrayList<String> lista = new ArrayList<String>();
        int numsalidasdesbloqueadas=0;
        if(!arr[0]){
            lista.add((PreferencesDevicesManager.obtenerClavePorValor(PreferencesDevicesManager.salidaMap,"PuertoSerie 1")));//+arrint[0].toString()));
            numsalidasdesbloqueadas++;
        }
        if(!arr[1]) {
            lista.add((PreferencesDevicesManager.obtenerClavePorValor(PreferencesDevicesManager.salidaMap,"PuertoSerie 2")));//+arrint[1].toString()));
            numsalidasdesbloqueadas++;
        }
        if(!arr[2]) {
            lista.add((PreferencesDevicesManager.obtenerClavePorValor(PreferencesDevicesManager.salidaMap,"PuertoSerie 3")));//+arrint[2].toString()));
            numsalidasdesbloqueadas++;
        }
        if(!arr[3]) {
            lista.add((PreferencesDevicesManager.obtenerClavePorValor(PreferencesDevicesManager.salidaMap,"Red")));//+arrint[2].toString()));
            numsalidasdesbloqueadas++;
        }
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

    private void CargarRecyclerBzas(){
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
                ComService.getInstance().fragmentChangeListener.openFragmentPrincipal();
            });

        }
    }
    @Override
    public void onDestroyView() {
        stoped=true;
        super.onDestroyView();
    }


}


