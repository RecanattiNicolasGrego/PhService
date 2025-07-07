package com.service;

import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class buttonMaster {
    static buttonMaster instance;
    private Map<Integer, buttonclass> buttonMap = new HashMap<>();
    public static buttonMaster getInstance() {
        if (instance == null) {
            instance = new buttonMaster();

        }
        return instance;
    }

    public void addButton(buttonclass buttonclass){
        buttonclass.init(buttonMap.size());
        buttonMap.put(buttonMap.size(),buttonclass);
    }
}
