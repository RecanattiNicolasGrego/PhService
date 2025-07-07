package com.service.Devices.Dispositivos.Clases;

import com.service.Interfaz.dispositivoBase;
import com.service.estructuras.classDevice;

public class DispositivoBase implements dispositivoBase {
    String strpuerto;
    int ndevice;
    int Slaveid=0;
    classDevice Device;
    public static Boolean puede485 = false;
    public DispositivoBase(String strpuerto, classDevice Device,int ndevice) {
    this.strpuerto = strpuerto;
    this.Device = Device;
    this.ndevice = ndevice;
    this.Slaveid = Device.getID();
    }

    @Override
    public void stop() {

    }
}
