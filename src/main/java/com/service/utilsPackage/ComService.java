package com.service.utilsPackage;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.service.BalanzaService;
import com.service.Interfaz.OnFragmentChangeListener;
import com.service.ServiceFragment;

public class ComService {
    public static final String ServiceVersion = "1.050";
    public AppCompatActivity activity;
    public OnFragmentChangeListener fragmentChangeListener;
    private static ComService instance;

    private ServiceFragment Servicefragment= new ServiceFragment();
    private
    ComService(AppCompatActivity activity, OnFragmentChangeListener OnFragmentChangeListener){
        this.activity=activity;
        this.fragmentChangeListener=OnFragmentChangeListener;
    }
    public static ComService init(AppCompatActivity activity, OnFragmentChangeListener OnFragmentChangeListener) {
        if (instance == null) {
            instance = new ComService(activity, OnFragmentChangeListener);
        }
        return instance;
    }
    public static ComService getInstance(){
        return instance;
    }
    public void openServiceFragment(){
        ServiceFragment fragment = Servicefragment.newInstance(BalanzaService.getInstance());
        Bundle args = new Bundle();
        args.putSerializable("instanceService", BalanzaService.getInstance());
        fragmentChangeListener.openFragmentService( fragment,args);
    }
}
