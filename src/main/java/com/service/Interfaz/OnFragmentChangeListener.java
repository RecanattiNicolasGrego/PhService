package com.service.Interfaz;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public interface OnFragmentChangeListener {
    void AbrirServiceFragment(Fragment fragment, Bundle arg);
    void AbrirFragmentPrincipal();
    int getUsuarioLvl();
}