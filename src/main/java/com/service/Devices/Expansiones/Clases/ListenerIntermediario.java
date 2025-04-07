package com.service.Devices.Expansiones.Clases;

import java.util.ArrayList;

public interface ListenerIntermediario {
        interface Intermediario {
            void ListenerIntermediario(int i,int NumeroExpansion, ArrayList<Integer> dato);
        }

        void setListener(Intermediario listener,int NumeroExpansion);
}
