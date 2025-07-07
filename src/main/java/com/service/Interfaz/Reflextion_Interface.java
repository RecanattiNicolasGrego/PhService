package com.service.Interfaz;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
public class  Reflextion_Interface  {


    public static String getFieldValueStr(Class<?> clase, String fieldName){
        try {
            Field field = clase.getDeclaredField(fieldName);
            field.setAccessible(true);
            if (field.getType().equals(String.class)) {
                return (String) field.get(null);
            } else {
                System.out.println("El campo no es de tipo String");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                Field field = clase.getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
                if (field.getType().equals(String.class)) {
                    return (String) field.get(null);
                } else {
                    System.out.println("El campo no es de tipo String");
                }
            } catch (NoSuchFieldException | IllegalAccessException z) {
                System.out.println("Error al obtener el valor del campo: " + fieldName+" "+z);
            }
        }
        return fieldName;
    }

    public static Boolean getFieldValueBool(Class<?> clase, String fieldName){
        try {
            Field field = clase.getDeclaredField(fieldName);
            field.setAccessible(true);
            if (field.getType().equals(Boolean.class)) {
                return (Boolean) field.get(null);
            } else {
                System.out.println("El campo no es de tipo Boolean");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                Field field = clase.getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
                if (field.getType().equals(Boolean.class)) {
                    return (Boolean) field.get(null);
                } else {
                    System.out.println("El campo no es de tipo Boolean");
                }
            } catch (NoSuchFieldException | IllegalAccessException x) {
                System.out.println("Error al obtener el valor del campo: " + fieldName+" "+x);
            }
        }
        return false;
    }

    public static  int getFieldValueInt(Class<?> clase, String fieldName) {
        try {
            Field field = clase.getDeclaredField(fieldName);
            field.setAccessible(true);
            if (field.getType().equals(int.class)) {
                return (int) field.get(null);
            } else {
                System.out.println("El campo no es de tipo int");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            try {
                Field field = clase.getSuperclass().getDeclaredField(fieldName);
                field.setAccessible(true);
                if (field.getType().equals(int.class)) {
                    return (int) field.get(null);
                } else {
                    System.out.println("El campo no es de tipo int");
                }
            } catch (NoSuchFieldException | IllegalAccessException x) {
                System.out.println("Error al obtener el valor del campo: " + fieldName+" "+ x);
            }
        }
        return 0;
    };
    }
