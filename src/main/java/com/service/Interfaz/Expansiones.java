package com.service.Interfaz;

import com.service.Devices.Expansiones.Clases.ExpansionManager;

public interface Expansiones extends ExpansionGestor {
    /**
     * Obtiene la instancia del gestor de expansiones.
     * @return la instancia de ExpansionManager.
     */
    public ExpansionManager getInstance();

}
