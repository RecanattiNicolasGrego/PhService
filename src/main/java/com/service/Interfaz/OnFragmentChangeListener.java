package com.service.Interfaz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public interface OnFragmentChangeListener {
    void openFragmentService(Fragment fragment, Bundle arg);
    void openFragmentPrincipal();
    int getUsuarioLvl();
}