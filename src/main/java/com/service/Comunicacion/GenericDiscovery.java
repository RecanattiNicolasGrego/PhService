package com.service.Comunicacion;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import com.service.ComService;
import com.service.ServiceFragment;
import com.service.estructuras.ZebraStruct;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveryException;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;
import com.zebra.sdk.printer.discovery.NetworkDiscoverer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GenericDiscovery extends AsyncTask<String, Void, List<String>> {
    Handler mainHandler = new Handler(Looper.getMainLooper());
    private ArrayList<String> listMac = new ArrayList<>();
    private ServiceFragment FService;
    private int type;
    WifiManager wifiManager;
    public static final  String btle=String.valueOf(BluetoothDevice.DEVICE_TYPE_LE);
    public static final  String btc=String.valueOf(BluetoothDevice.DEVICE_TYPE_CLASSIC);
    private ExecutorService discoveryWFJob= Executors.newSingleThreadExecutor();
    private ProgressBar progressBar;
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private CountDownLatch latchBTLE = new CountDownLatch(1);
    private CountDownLatch latchWF = new CountDownLatch(1);
    private CountDownLatch latchBTC = new CountDownLatch(1);
    private boolean interruptDiscovery = false;
    private long discoveryTimeout = 7000; // Tiempo máximo de búsqueda en milisegundos
    private List<String> reachableHosts = new ArrayList<>();
    private IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
    public GenericDiscovery(ServiceFragment context, ProgressBar progressBar, int type) {
        this.FService = context;
        this.progressBar = progressBar;
        this.type = type;
        wifiManager = (WifiManager) ComService.getInstance().activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    @Override
    protected List<String> doInBackground(String... params) {
        try {
            ((ServiceFragment) FService).ListaScanner.clear();
        }catch (Exception e){
        } catch (Throwable e) {
        }
       // while(!interruptDiscovery) {
            if (type == 1) {
                try {
                    WFStartSearch();
                } catch (Exception e) {

                }
                try {
                    WFSEARCHSTART();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

                try {
                    BTLESEARCHSTART();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    BTCSEARCHSTART();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
       // }
        return reachableHosts;
    }
    private final BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (interruptDiscovery) return;

            boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
            if (success) {
                @SuppressLint("MissingPermission") List<android.net.wifi.ScanResult> results = wifiManager.getScanResults();
                for (android.net.wifi.ScanResult result : results) {
                    Boolean boolinList = false;
                    try {
                        boolinList = (!FService.adapterimpresora.getlistMac().contains(result.SSID)||FService.adapterimpresora.getlistMac().isEmpty());
                    } catch (Exception e) {
                    }
                    if (!result.SSID.isEmpty() && boolinList) {
                        FService.adapterimpresora.add(result.SSID, result.SSID, "WFO", "WFO");

                    }
                }
            }
            context.unregisterReceiver(this);
        }
    };
    public void WFStartSearch(){
         if (wifiManager.isWifiEnabled()) {
            IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
             ComService.getInstance().activity.registerReceiver(scanReceiver, filter);
            wifiManager.startScan();
        } else {
           // Utils.Mensaje("Active Wifi", R.layout.item_customtoasterror,(AppCompatActivity) FService.requireActivity());
        }
    }
    @SuppressLint("MissingPermission")
    public void BTLESEARCHSTART() {
        try {
            latchBTLE = new CountDownLatch(1);
            ScanCallback scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, android.bluetooth.le.ScanResult result) {
                    if (interruptDiscovery) return;

                    BluetoothDevice device = result.getDevice();
                    Boolean boolinList = false;
                    try {
                        boolinList = (!FService.adapterimpresora.getlistMac().contains(device.getAddress())||FService.adapterimpresora.getlistMac().isEmpty());
                    } catch (Exception e) {
                    }
                    if (device != null && device.getAddress() != null && device.getName()!=null && device.getBluetoothClass().getDeviceClass()!=1664  && boolinList &&!Objects.equals(device.getName(), "")) {
                        FService.adapterimpresora.add(device.getAddress(), device.getName(), btle, btle);
                        FService.progressBar.setVisibility(GONE);
                    }
                }
                @Override
                public void onScanFailed(int errorCode) {
                    // Maneja el error si el escaneo falla
                }
            };


            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
                bluetoothLeScanner.startScan(scanCallback);
            }
            latchBTLE.await(5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void BTCSEARCHSTART() {
        if (interruptDiscovery) return;

        try {
            latchBTC = new CountDownLatch(1);
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bluetoothAdapter != null && !bluetoothAdapter.isDiscovering()) {
            try {
                bluetoothAdapter.startDiscovery();
                ComService.getInstance().activity.registerReceiver(bluetoothReceiver, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    FService.GenericDiscovery.stopDiscovery();
                    latchBTC.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, discoveryTimeout);
        }

        try {
            latchBTC.await(5000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void interruptDiscovery() {
        interruptDiscovery = true;
        try {
            latchBTLE.countDown();
            latchWF.countDown();
            latchBTC.countDown();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (FService instanceof ServiceFragment) {
                ((ServiceFragment) FService).ListaScanner.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (device != null  && device.getBluetoothClass().getDeviceClass()!=1664  && device.getAddress() != null) {
                    Boolean boolinList = false;
                    try {
                        boolinList = (!FService.adapterimpresora.getlistMac().contains(device.getAddress())||FService.adapterimpresora.getlistMac().isEmpty());
                    } catch (Exception e) {
                    }
                    if (boolinList) {
                        FService.adapterimpresora.add(device.getAddress(), device.getName(),btc,btc);
                        FService.progressBar.setVisibility(GONE);
                    }
                }
            }
        }
    };
    @SuppressLint("MissingPermission")
    private void stopDiscovery() {
        if (!interruptDiscovery) {
            try {
                ComService.getInstance().activity.unregisterReceiver(bluetoothReceiver);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            try {
                if (discoveryWFJob != null) {
                    discoveryWFJob.shutdown();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //DISCOVERED FINISHED?
            try{
                latchBTLE.countDown();
            }catch (Exception e){

            }
            try {
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void WFSEARCHSTART() {
        latchWF = new CountDownLatch(1);
        if (discoveryWFJob != null && !discoveryWFJob.isShutdown()) {
            discoveryWFJob.shutdown();
            discoveryWFJob = Executors.newSingleThreadExecutor();
        }
        try {

            discoveryWFJob.submit(new Runnable() {
                @Override
                public void run() {
                    if (interruptDiscovery) return;


                    try {
                        NetworkDiscoverer.findPrinters(new DiscoveryHandler() {
                            @Override
                            public void foundPrinter(DiscoveredPrinter printer) {
                                Boolean boolinList = true;
                                try {
                                    boolinList = FService.adapterimpresora.getlist().contains(new ZebraStruct(printer.address, "WF","WF","WF"));
                                } catch (Exception e) {
                                    boolinList = true;
                                }
                                if (printer != null && !boolinList) {
                                    try {
                                        mainHandler.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                FService.progressBar.setVisibility(GONE);
                                                FService.adapterimpresora.add(printer.address, "WF", "WF", "WF");
                                            }});
                                    } catch (Exception e) {
                                    }
                                    reachableHosts.add(printer.address);

                                }
                            }

                            @Override
                            public void discoveryFinished() {
                                latchWF.countDown();
                            }

                            @Override
                            public void discoveryError(String message) {
                                // Manejo de error
                            }
                        });
                    } catch (DiscoveryException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            latchWF.await(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
