package com.service.Comunicacion;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public interface ButtonProvider {
    Button getButtonHome();
    Button getButton1();
    Button getButton2();
    Button getButton3();
    Button getButton4();
    Button getButton5();
    Button getButton6();
    TextView getTitle();
    LinearLayout getMenu();

   // Button getButton7();
   // Button getButtonArriba();
}
