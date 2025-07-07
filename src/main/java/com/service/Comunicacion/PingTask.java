package com.service.Comunicacion;

import android.os.AsyncTask;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PingTask extends AsyncTask<Void, Void, Boolean> {

    private String ipAddress;
    private PingCallback callback;

    public interface PingCallback {
        void onPingResult(Boolean result);
    }

    public PingTask(String ipAddress, PingCallback callback) {
        this.ipAddress = ipAddress;
        this.callback = callback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Socket socket = new Socket();
            InetSocketAddress socketAddress = new InetSocketAddress(ipAddress, 80); // O el puerto que desees
            socket.connect(socketAddress, 3000); // Tiempo de espera de 2 segundos
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        callback.onPingResult(result);
    }
}