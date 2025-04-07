package com.service.Interfaz;

import com.service.Devices.Expansiones.Clases.ExpansionManager;

public interface Expansiones {
    /**
     * Obtiene la instancia del gestor de expansiones.
     * @return la instancia de ExpansionManager.
     */
    public ExpansionManager getInstance();
    /**
     * Inicializa las expansiones, configurando el listener y configurando los puertos serie.
     * @param Listener el listener para las actualizaciones de expansiones.
     */
    public  void init(ExpansionManager.ExpansionesMessageListener Listener);
}
