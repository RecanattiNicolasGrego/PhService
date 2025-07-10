package com.service.utilsPackage;

import android.view.View;

import com.service.R;

public class buttonclass {
    private String labelText;
    private View view;

    private int i = 0;
    public buttonclass(View view, String labelText) {
        this.view = view;
        this.labelText = labelText;
    }
    protected void init(int i){
        this.i=i;
    }
    public void setOnClickListener(View.OnClickListener onClickListener) {
        if (view != null) {
            view.setOnClickListener(onClickListener);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Utils.Mensaje(PreferencesDevicesManager.getmensajeHelper(i,labelText), R.layout.item_customtoasterror,ComService.getInstance().activity);
                    return false;
                }
            });
        }
    }
    public String getLabelText() {
        return labelText;
    }

    public View getView() {
        return view;
    }
}
