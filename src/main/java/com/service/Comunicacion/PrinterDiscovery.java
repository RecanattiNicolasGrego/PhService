package com.service.Comunicacion;

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
import android.util.Log;
import android.widget.ProgressBar;

import com.service.ComService;
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

public class PrinterDiscovery extends AsyncTask<String, Void, List<ZebraStruct>> {
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
    private List<ZebraStruct> reachableHosts = new ArrayList<>();

    private IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

    public PrinterDiscovery(ServiceFragment context, ProgressBar progressBar, int type) {
        this.FService = context;
        this.progressBar = progressBar;
        this.type = type;
    }

    @Override
    protected List<ZebraStruct> doInBackground(String... params) {
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
            DiscoveryHandler discoveryHandler = new MyDiscoveryHandlerBTLE(ComService.getInstance().activity);
            BluetoothLeDiscoverer.findPrinters(ComService.getInstance().activity, discoveryHandler);
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
                ComService.getInstance().activity.registerReceiver(bluetoothReceiver, filter);
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
            if (interruptDiscovery) return;

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null && device.getBluetoothClass().getDeviceClass() == 1664 && device.getAddress() != null) {
                    Boolean boolinList = false;
                    try {
                        boolinList = (!FService.adapterimpresora.getlistMac().contains(device.getAddress())||FService.adapterimpresora.getlistMac().isEmpty());
                    } catch (Exception e) {
                    }
                    if (boolinList) {
                            System.out.println(device.getName());
                            FService.adapterimpresora.add(device.getAddress(), device.getName(), btc, btc);
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

    class MyDiscoveryHandlerBTLE implements DiscoveryHandler {
        private Context context;

        public MyDiscoveryHandlerBTLE(Context context) {
            this.context = context;
        }

        @SuppressLint("MissingPermission")
        @Override
        public void foundPrinter(DiscoveredPrinter printer) {
            System.out.println("ola?");
            try {
                if (interruptDiscovery) return;

                Boolean boolinList = false;
                try {
                    boolinList = (!FService.adapterimpresora.getlistMac().contains(printer.address)||FService.adapterimpresora.getlistMac().isEmpty());
                } catch (Exception e) {
                }
                if (printer != null && boolinList) {
                    System.out.println("ola2?");
                    String friendName = "";
                    BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(printer.address);

                    if (bluetoothDevice != null && bluetoothDevice.getBluetoothClass() != null  && bluetoothDevice.getName() != null && !bluetoothDevice.getName().isEmpty()) {
                        System.out.println("ola3?");
                        try {
                                friendName = bluetoothDevice.getName();
                                System.out.println(friendName);
                        } catch (Exception e) {
                            System.out.println("aaaaaaaaaaa");
                            e.printStackTrace();
                        }
                        System.out.println("ola4?");
                        String finalFriendName = friendName;
                        new Handler(Looper.getMainLooper()).post(() -> {
                            FService.adapterimpresora.add(printer.address, finalFriendName, btle, btle);
                        FService.progressBar.setVisibility(GONE);
                        });
                    }
                }
            }
           catch (Exception e) {
                    e.printStackTrace();
                    Log.e("PrinterDiscovery", "Error en foundPrinter: " + e.getMessage());
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
                    if (interruptDiscovery) return;

                    try {
                        NetworkDiscoverer.findPrinters(new DiscoveryHandler() {
                            @Override
                            public void foundPrinter(DiscoveredPrinter printer) {
                                System.out.println("A");
                                Boolean boolinList = false;
                                try {
                                    boolinList = (!FService.adapterimpresora.getlistMac().contains(printer.address)||FService.adapterimpresora.getlistMac().isEmpty());
                                    System.out.println("A2"+ printer.address);
                                } catch (Exception e) {
                                }

                                if (printer != null && boolinList) {
                                    try {
                                        new Handler(Looper.getMainLooper()).post(() -> {
                                        FService.progressBar.setVisibility(GONE);
                                        FService.adapterimpresora.add(printer.address, "WF", "WF", "WF");
                                        });
                                    }catch (Exception e){

                                    }
                                  //  reachableHosts.add(new ZebraStruct(printer.address, "WF", "WF", "WF"));

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
