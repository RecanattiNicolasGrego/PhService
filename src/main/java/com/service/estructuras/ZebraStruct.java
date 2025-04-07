package com.service.estructuras;

public class ZebraStruct {
    private String direccion;
    private String name;
    private String tipoBT;
    private  String clase;
    public ZebraStruct(String direccion, String tipo, String tipoBT,String clase) {
        this.direccion = direccion;
        this.name = tipo;
        this.tipoBT = tipoBT;
        this.clase=clase;
    }

    public String getDireccionx() {
        return direccion;
    }

    public String getname() {
        return name;
    }

    public String getTipo() {
        return tipoBT;
    }
    public String getClase(){return clase;}
}
