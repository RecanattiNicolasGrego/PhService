package com.service.Devices.Impresora;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import com.service.ServiceFragment;
import com.service.estructuras.ZebraStruct;
import com.zebra.sdk.btleComm.BluetoothLeDiscoverer;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveryException;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;
import com.zebra.sdk.printer.discovery.NetworkDiscoverer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PrinterDiscovery extends AsyncTask<String, Void, List<String>> {
    Handler mainHandler = new Handler(Looper.getMainLooper());
    private ArrayList<String> listMac = new ArrayList<>();
    private ServiceFragment FService;
    private int type;
    private ExecutorService discoveryWFJob= Executors.newSingleThreadExecutor();
    private ProgressBar progressBar;
    public static final  String btle=String.valueOf(BluetoothDevice.DEVICE_TYPE_LE);
    public static final  String btc=String.valueOf(BluetoothDevice.DEVICE_TYPE_CLASSIC);
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private CountDownLatch latchBTLE = new CountDownLatch(1);
    private CountDownLatch latchWF = new CountDownLatch(1);
    private CountDownLatch latchBTC = new CountDownLatch(1);
    private boolean interruptDiscovery = false;
    private long discoveryTimeout = 7000; // Tiempo máximo de búsqueda en milisegundos
    private List<String> reachableHosts = new ArrayList<>();

    private IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

    public PrinterDiscovery(ServiceFragment context, ProgressBar progressBar, int type) {
        this.FService = context;
        this.progressBar = progressBar;
        this.type = type;
    }

    @Override
    protected List<String> doInBackground(String... params) {
        listMac.clear();
        try {
                ((ServiceFragment) FService).ListaScanner.clear();
        }catch (Exception e){
        } catch (Throwable e) {
        }
  //  while(!interruptDiscovery) {
        if (type == 1) {
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
  //  }
        return reachableHosts;
    }

    public void BTLESEARCHSTART() {
        try {
            latchBTLE = new CountDownLatch(1);
            DiscoveryHandler discoveryHandler = new MyDiscoveryHandlerBTLE(FService.requireContext());
            BluetoothLeDiscoverer.findPrinters(FService.requireContext(), discoveryHandler);
            latchBTLE.await(5000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    public void BTCSEARCHSTART() {
        try {
            latchBTC = new CountDownLatch(1);
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bluetoothAdapter != null && !bluetoothAdapter.isDiscovering()) {
            try {
                bluetoothAdapter.startDiscovery();
                FService.getContext().registerReceiver(bluetoothReceiver, filter);
            } catch (Exception e) {
                e.printStackTrace();
            }

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                        FService.multizebra.stopDiscovery();
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

        listMac.clear();
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

                if (device != null && device.getBluetoothClass().getDeviceClass() == 1664 && device.getAddress() != null) {
                    if (!listMac.contains(device.getAddress())) {
                        listMac.add(device.getAddress());
                        reachableHosts.add(device.getAddress());
                        //FService.ListaScanner.add(new ZebraStruct(device.getAddress(), device.getName(),"WF","WF"));
                        FService.adapterimpresora.add(device.getAddress(), device.getName(),btc,btc);
                        FService.progressBar.setVisibility(GONE);
                    }
                }
            }
        }
    };
    @SuppressLint("MissingPermission")
    public void stopDiscovery() {
        if (!interruptDiscovery) {
            try {
                FService.requireContext().unregisterReceiver(bluetoothReceiver);
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

    class MyDiscoveryHandlerBTLE implements DiscoveryHandler {
        private Context context;

        public MyDiscoveryHandlerBTLE(Context context) {
            this.context = context;
        }

        @SuppressLint("MissingPermission")
        @Override
        public void foundPrinter(DiscoveredPrinter printer) {
            if (printer != null && !listMac.contains(printer.address)) {
                listMac.add(printer.address);
                String friendName="";
                BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(printer.address);
                if (bluetoothDevice.getName() != null && bluetoothDevice.getBluetoothClass().getDeviceClass()== 1664 && !bluetoothDevice.getName().isEmpty()) {
                    try {
                        FService.multizebra.listMac.add(printer.address);
                        String ms = printer.address;
                       // System.out.println(ms);

                        try {
                            bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(printer.address);
                            friendName = bluetoothDevice.getName();
                        //    System.out.println(friendName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                FService.adapterimpresora.add(printer.address, friendName,btle,btle);
                FService.progressBar.setVisibility(GONE);
            }
        }

        @Override
        public void discoveryFinished() {
            latchBTLE.countDown();
        }

        @Override
        public void discoveryError(String message) {
            // Handle error
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
