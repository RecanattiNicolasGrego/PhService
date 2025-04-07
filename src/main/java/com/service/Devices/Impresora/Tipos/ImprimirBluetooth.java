package com.service.Devices.Impresora.Tipos;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.service.R;
import com.service.Utils;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ImprimirBluetooth {

    private final Context context;
    private AppCompatActivity mainActivity;
    private String mac = "";
    BluetoothSocket bluetoothSocket;

    public ImprimirBluetooth(Context context, AppCompatActivity activity, String mac) {
        this.context = context;
        this.mainActivity = activity;
        this.mac = mac;
    }

    public void Imprimir(String etiqueta) {
        Runnable myrun = new Runnable() {
            @Override
            public void run() {

        Connection connection = null;
                try {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(mac);
                    if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                     connection = new BluetoothConnection(mac);
                    connection.open();
                    connection.write(etiqueta.getBytes());
                    Thread.sleep(1000);

                } catch ( InterruptedException | ConnectionException e) {
                    e.printStackTrace();
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // Utils.Mensaje("Error impresora bluetooth, verifique que la misma se encuentra encendida", R.layout.item_customtoasterror, mainActivity);
                        }
                    });
                    // Maneja errores de conexión aquí
                } finally {
                    if(connection!=null &&connection.isConnected()) {
                        try {
                            connection.close();
                        } catch (ConnectionException e) {

                        }
                    }
                }


            }
        };
        Thread x = new Thread(myrun);
        x.start();
    }


}
