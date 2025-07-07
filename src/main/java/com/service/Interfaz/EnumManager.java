package com.service.Interfaz;

 public class EnumManager{
    public  enum Balanzas{
        puede485("TienePorDemanda"),
        nBalanzas("nBalanzas"),
        TieneCal("TieneCal");


        public final String nombre;

        Balanzas(String nombre) {
            this.nombre = nombre;
        }
    }
    public enum Dispositivos {
        puede485("puede485");

        public final String nombre;

        Dispositivos(String nombre) {
            this.nombre = nombre;
        }
    }
    public enum Configuracion_Puerto {
        BAUD("Bauddef"),
        DATA("DataBdef"),
        STOP("StopBdef"),
        PARITY("Paritydef");

        public final String nombre;

        Configuracion_Puerto(String nombre) {
            this.nombre = nombre;
        }
    }
}
