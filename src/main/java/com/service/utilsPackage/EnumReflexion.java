package com.service.utilsPackage;

 public class EnumReflexion {
    public  enum Balanzas{
        puede485("TienePorDemanda"),
        nBalanzas("nBalanzas"),
        timeout("timeout"),
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
    public enum Expansiones {
        Salidas("Salidas"),
        Entradas("Entradas");

        public final String nombre;

        Expansiones(String nombre) {
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
