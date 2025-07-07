package com.service.Interfaz;

import com.service.Devices.Balanzas.Clases.BalanzaBase;


public interface Balanza {

    /** Devuelve la balanza especificada
     * @param numID El ID a devolver.
     */
    Balanza getBalanza(int numID);

    /**
     * Obtiene el valor neto de la balanza especificada.
     *
     * @param numBza El número de la balanza.
     * @return El valor neto.
     */
    Float getNeto(int numBza);
    /**
     * Obtiene el valor neto de la balanza como un String.
     *
     * @param numBza El número de la balanza.
     * @return El valor neto como String.
     */
    String getNetoStr(int numBza);
    /**
     * Obtiene el valor bruto de la balanza especificada.
     *
     * @param numBza El número de la balanza.
     * @return El valor bruto.
     */
    Float getBruto(int numBza);
    /**
     * Obtiene el valor bruto de la balanza como un String.
     *
     * @param numBza El número de la balanza.
     * @return El valor bruto como String.
     */
    String getBrutoStr(int numBza);
    /**
     * Obtiene el valor tara de la balanza especificada.
     *
     * @param numBza El número de la balanza.
     * @return El valor tara.
     */
    Float getTara(int numBza);
    /**
     * Obtiene el valor tara de la balanza como un String.
     *
     * @param numBza El número de la balanza.
     * @return El valor tara como String.
     */
    String getTaraStr(int numBza);
    /**
     * Establece el valor tara para la balanza especificada.
     *
     * @param numBza El número de la balanza.
     */
    void setTara(int numBza);
    /**
     * Establece el valor cero para la balanza especificada.
     *
     * @param numBza El número de la balanza.
     */
    void setCero(int numBza);
    /**
     * Establece el valor tara digital para la balanza especificada.
     *
     * @param numBza El número de la balanza.
     * @param TaraDigital El valor tara digital.
     */
    void setTaraDigital(int numBza, float TaraDigital);
    /**
     * Obtiene el valor tara digital de la balanza especificada.
     *
     * @param numBza El número de la balanza.
     * @return El valor tara digital.
     */
    String getTaraDigital(int numBza);
    /**
     * Verifica si la balanza especificada está estable.
     *
     * @param numBza El número de la balanza.
     * @return true si está estable, false de lo contrario.
     */
    Boolean getEstable(int numBza);
    /**
     * Formatea el número según el PuntoDecimal de la balanza especificada.
     *
     * @param numero El número a formatear.
     * @param peso La unidad de peso.
     * @return El número formateado.
     */
    String format(int numero, String peso);
    /**
     * Obtiene la unidad de medida de la balanza especificada.
     *
     * @param numBza El número de la balanza.
     * @return La unidad de medida.
     */
    String getUnidad(int numBza);


  /**
     * Verifica si hay sobrecarga en la balanza especificada.
     *
     * @param numBza El número de la balanza.
     * @return true si hay sobrecarga, false de lo contrario.
     */
    Boolean getSobrecarga(int numBza);


        interface  ITW380 extends Balanza{
            String getCaudalInstantaneo();
            String getAcumuladoParcial();
            String getResultadoPID();
            String getProducto();
            String getAcumuladoTotal();
            String getAlarmaCaudalMinima();
            String getAlarmaCaudalMaxima();
            String getAlarmaAcumulado();
            String getTurno1Acumulado();
            String getTurno2Acumulado();
            String getTurno3Acumulado();
            String getTurno4Acumulado();
            String getRegistroTurno();
            String getRPM();
            String getVelocidad();
            String getPulsos();
            String getTiempoCinta();
            String getPIDSetpoint();
            String getSetpoint();
            String getSetpoint1Acumulado();
            String getSetpoint2Acumulado();
            String getSetpoint3Acumulado();
            String getSetpoint4Acumulado();
            String getSetpoint5Acumulado();
            String getDiaMes();
            String getAño();

            String getHora();
            String getTurno1();
            String getTurno2();
            String getTurno3();
            String getTurno4();
            String getEncoder();
            String getVelocidadSinEncoder();
            boolean setAcumula(Boolean Acumula);

            boolean setParadaArranque(Boolean ParadaArranque);
            boolean setResetAcumTotal();
            boolean setResetAcumParcial();

            boolean setAlarmaCaudalMin(Integer AlarmaCaudalMin);
            boolean setAlarmaCaudalMax(Integer AlarmaCaudalMax);
            boolean setAlarmaAcum(Integer AlarmaAcum);
            boolean setPIDSetpoint(Integer PIDSetpoint);
            boolean setSetpoint(Integer Setpoint);
            boolean setDiaMes(Integer DiaMes);
            boolean setAño(Integer Año);
            boolean setHora(Integer Hora);
            boolean setTurno1(Integer Turno1);
            boolean setTurno2(Integer Turno2);
            boolean setTurno3(Integer Turno3);
            boolean setTurno4(Integer Turno4);
            boolean setEncoder(Boolean Encoder);
            boolean setVelocidadSinEncoder(Integer VelocidadSinEncoder);
        }
        interface ITW410 extends Balanza{
            /**
             * Configura el set point de la balanza ITW410.
             *
             * @param numBza El número de la balanza.
             * @param setPoint El valor del set point.
             * @param Salida El número de la salida.
             * @return true si la operación fue exitosa, false de lo contrario.
             *
             * //anotacion inocente: puede que la Salida no sea tan asi pero ia god
             */
        Boolean Itw410FrmSetear(int numBza,String setPoint, int Salida);//void Itw410FrmSetear(int numero,String setPoint, int Salida);
            /**
             * Obtiene el set point de la balanza ITW410.
             *
             * @param numBza El número de la balanza.
             * @return El set point de la balanza.
             */
        String Itw410FrmGetSetPoint(int numBza);
            /**
             * Obtiene el número de salida de la balanza ITW410.
             *
             * @param numBza El número de la balanza.
             * @return El número de salida.
             */
        Integer Itw410FrmGetSalida(int numBza);

            /**
             * Inicia la balanza ITW410.
             *
             * @param numBza El número de la balanza.
             */
        void Itw410FrmStart(int numBza);
            /**
             * Obtiene el estado de la balanza ITW410.
             *
             * @param numBza El número de la balanza.
             * @return El estado de la balanza.
             */
        Integer Itw410FrmGetEstado(int numBza);
            /**
             * Obtiene el último peso registrado por la balanza ITW410.
             *
             * @param numBza El número de la balanza
             * @return El último peso registrado.
             */
        String Itw410FrmGetUltimoPeso(int numBza);
            /**
             * Obtiene el último índice registrado por la balanza ITW410.
             *
             * @param numBza El número de la balanza.
             * @return El último índice registrado.
             */
        Integer Itw410FrmGetUltimoIndice(int numBza);
            /**
             * Pausa la balanza ITW410.
             *
             * @param numBza El número de la balanza.
             */
        void itw410FrmPause(int numBza);
            /**
             * Detiene la balanza ITW410.
             *
             * @param numBza El número de la balanza.
             */
        void itw410FrmStop(int numBza);
            /**
             * Establece el tiempo de estabilización de la balanza ITW410.
             *
             * @param numBza El número de la balanza.
             * @param Tiempo El tiempo de estabilización en segundos.
             */
        void Itw410FrmSetTiempoEstabilizacion(int numBza, int Tiempo);
            /**
             * Obtiene el filtro 1 de la balanza ITW410.
             *
             * @param numBza El número de la balanza.
             * @return El filtro 1 de la balanza.
             */
            String getFiltro1(int numBza);
            /**
             * Obtiene el filtro 2 de la balanza ITW410.
             *
             * @param numBza El número de la balanza.
             * @return El filtro 2 de la balanza.
             */
            String getFiltro2(int numBza);
            /**
             * Obtiene el filtro 3 de la balanza ITW410.
             *
             * @param numBza El número de la balanza.
             * @return El filtro 3 de la balanza.
             */
            String getFiltro3(int numBza);
            /**
             * Obtiene el filtro 4 de la balanza ITW410.
             *
             * @param numBza El número de la balanza.
             * @return El filtro 4 de la balanza.
             */
            String getFiltro4(int numBza);
    }
    interface Optima_Image extends Balanza{
        /**
         * Obtiene el estado del centro cero de la balanza.
         *
         * @param numBza El número de la balanza.
         * @return true si el centro cero está activo, false de lo contrario.
         */
        Boolean getEstadoCentroCero(int numBza);


        /**
         * Obtiene el estado del neto de la balanza.
         *
         * @param numBza El número de la balanza.
         * @return true si el peso neto está registrado correctamente, false de lo contrario.
         */
        Boolean getEstadoNeto(int numBza);

        /**
         * Obtiene el estado de peso negativo de la balanza.
         *
         * @param numBza El número de la balanza.
         * @return true si el peso es negativo, false de lo contrario.
         */
        Boolean getEstadoPesoNeg(int numBza);

        /**
         * Obtiene el estado de bajo cero de la balanza.
         *
         * @param numBza El número de la balanza.
         * @return true si está en bajo cero, false de lo contrario.
         */
        Boolean getEstadoBajoCero(int numBza);

        /**
         * Obtiene el estado de la balanza en cero.
         *
         * @param numBza El número de la balanza.
         * @return true si la balanza está en cero, false de lo contrario.
         */
        Boolean getEstadoBzaEnCero(int numBza);


        /**
         * Obtiene el estado de baja batería de la balanza.
         *
         * @param numBza El número de la balanza.
         * @return true si la balanza tiene baja batería, false de lo contrario.
         */
        Boolean getEstadoBajaBateria(int numBza);

    }

}
