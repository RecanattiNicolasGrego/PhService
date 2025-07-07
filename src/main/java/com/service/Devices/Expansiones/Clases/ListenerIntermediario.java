package com.service.Devices.Expansiones.Clases;

import com.service.Interfaz.Expansion;
import com.service.Interfaz.ExpansionGestor;
import com.service.Interfaz.fachade;

import java.util.ArrayList;

public interface ListenerIntermediario {
        interface Intermediario extends fachade {
            void ListenerIntermediario(int i,int NumeroExpansion, ArrayList<Integer> dato);
        }

        void setListener(Intermediario listener,int NumeroExpansion);
}
