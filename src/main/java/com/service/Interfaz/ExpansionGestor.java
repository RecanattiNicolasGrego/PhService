package com.service.Interfaz;

import com.service.Devices.Expansiones.Clases.ExpansionManager;

public interface ExpansionGestor extends fachade{

    /**
     * Inicializa las expansiones, configurando el listener y configurando los puertos serie.
     * @param Listener el listener para las actualizaciones de expansiones.
     */
    public  void Iniciar(ExpansionManager.ExpansionesListener Listener);
}
