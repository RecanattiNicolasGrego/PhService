package com.service.Interfaz;


public interface Dispositivo {

    public interface DeviceFachade extends Dispositivo {

        /**
         * Obtiene la instancia del gestor de dispositivos.
         * @return la instancia de DeviceManager.
         */
        dispositivoBase getDispositivo(int nDispositivo);
    }
}
