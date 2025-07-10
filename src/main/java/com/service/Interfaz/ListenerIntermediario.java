package com.service.Interfaz;

import java.util.ArrayList;

public interface ListenerIntermediario {
        interface Intermediario extends fachade {
            void ListenerIntermediario(int i,int NumeroExpansion, ArrayList<Integer> dato);
        }

        void setListener(Intermediario listener,int NumeroExpansion);
}
