package com.service.Comunicacion;

public class ButtonProviderSingleton {
    private static ButtonProviderSingleton instance;
    private ButtonProvider buttonProvider;

    private ButtonProviderSingleton() {
        // Constructor privado para evitar instanciación directa
    }

    public static ButtonProviderSingleton getInstance() {
        if (instance == null) {
            instance = new ButtonProviderSingleton();
        }
        return instance;
    }

    public ButtonProvider getButtonProvider() {
        return buttonProvider;
    }

    public void setButtonProvider(ButtonProvider buttonProvider) {
        this.buttonProvider = buttonProvider;
    }
}