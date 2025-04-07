package com.service.Interfaz;

import java.util.ArrayList;

public interface Expansion {
    /**
     * Lee una entrada específica según el número de entrada y el id.
     *
     * @param numEntrada El número de la entrada a leer.
     * @param id El identificador del dispositivo o componente.
     * @return El valor de la entrada leída.
     */
    Integer LeerEntrada(int numEntrada,int id);
    /**
     * Configura una salida con un valor dado.
     *
     * @param valor El valor que se quiere asignar a la salida.
     * @param numSalida El número de la salida a configurar.
     * @param id El identificador del dispositivo o componente.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    Boolean SetearSalida(Boolean valor,int numSalida,int id);
    /**
     * Detiene la operación de la expansión.
     */
    void Stop();
    /**
     * Obtiene el número de salidas disponibles.
     *
     * @return El número de salidas.
     */
    Integer getSalidas();
    /**
     * Obtiene el número de entradas disponibles.
     *
     * @return El número de entradas.
     */
    Integer getEntradas();
    /**
     * Obtiene los estados actuales de las expansiones.
     *
     * @return Una lista de bits que representan los estados de las expansiones .
     */
    ArrayList<Integer> getEstados();

}
