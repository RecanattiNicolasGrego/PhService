package com.service.estructuras;

import java.util.ArrayList;

public class classDevice {
        private String Salida;
        private String TipoDevice;
        private int NumID;
        private String Modelo;
        private ArrayList<String> Direccion;
        private int NumeroDevice;
        private Boolean Seteado;
        private int NumeroDeviceList=0;

    public int getND() {
        return NumeroDevice;
    }
    public void setND(int numeroDevice) {NumeroDevice = numeroDevice;}
    public int getNDL() {
        return NumeroDeviceList;
    }
    public void setNDL(int numerodevicelist) {NumeroDeviceList = numerodevicelist;}
    public void setSeteo(Boolean set) {this.Seteado=set;}
    public Boolean getSeteo() {return Seteado;}
    public int getID() {
        return NumID;
    }
    public void setID(int ID) {NumID = ID;}
    public String getSalida() {return Salida;}
    public void setSalida(String salida) {Salida = salida;}
    public String getModelo() {
        return Modelo;
    }
    public void setModelo(String modelo) {Modelo = modelo;}
    public String getTipo() {
        return TipoDevice;
    }
    public void setTipo(String Tipo) {
        TipoDevice = Tipo;
    }
    public void setDireccion(ArrayList<String> direccion) {
        Direccion = direccion;
    }
    public ArrayList<String> getDireccion() {
        return Direccion;
    }
//    public void setDevice(serviceDevice Device){
//        NumID=Device.getID();
//        Salida= Device.getSalida();
//        Modelo = Device.getModelo();
//        TipoDevice=Device.getTipo();
//        Direccion = Device.getDireccion();
//        Seteado= Device.getSeteo();
//        NumeroDevice =Device.getND();
//    }

}
