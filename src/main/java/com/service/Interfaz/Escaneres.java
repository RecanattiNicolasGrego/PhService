package com.service.Interfaz;

import com.service.Devices.Expansiones.Clases.EscannerManager;

public interface Escaneres {
    /**
     * Obtiene la instancia del gestor de escáneres.
     * @return la instancia de EscannerManager.
     */
    public EscannerManager getInstance();
    /**
     * Inicializa los escáneres, configurando el listener y configurando los puertos serie.
     * @param Listener el listener para las actualizaciones de escáneres.
     */
    public  void init(EscannerManager.ScannerMessageListener Listener);
}
