package com.example.stefa.sendsms2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by stefa on 28.10.2018.
 */
public class ConfigParameters {

    ArrayList<String> Contacts = new ArrayList<String>();
    public String Message;
    public int SendingInterval;
    public boolean ActivateLight;
    public boolean ActivateAudio;
    public int AutoOFF;
    public boolean LockScreen;


    public ConfigParameters(Activity a){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(a);
        String number1=sharedPreferences.getString("contact1", "Default");
        if(!number1.isEmpty()){
            Contacts.add(number1);
        }
        String number2=sharedPreferences.getString("contact2", "Default");
        if(!number2.isEmpty()){
            Contacts.add(number2);
        }
        String number3=sharedPreferences.getString("contact3", "Default");
        if(!number3.isEmpty()){
            Contacts.add(number3);
        }
        String number4=sharedPreferences.getString("contact4", "Default");
        if(!number4.isEmpty()){
            Contacts.add(number4);
        }
        String number5=sharedPreferences.getString("contact5", "Default");
        if(!number5.isEmpty()){
            Contacts.add(number5);
        }

        String message=sharedPreferences.getString("message", "Default");
        if(!message.isEmpty()){
            Message = message;
        }


        int interval=Integer.parseInt(sharedPreferences.getString("interval", null));
        SendingInterval=interval;

        boolean activateLight=sharedPreferences.getBoolean("light", false);
        ActivateLight=activateLight;

        boolean activateAudio=sharedPreferences.getBoolean("alarm", false);
        ActivateAudio=activateAudio;

        int autoOFF=Integer.parseInt(sharedPreferences.getString("auto_off", null));
        AutoOFF=autoOFF;

        boolean lockScreen=sharedPreferences.getBoolean("lockScreen", false);
        LockScreen=lockScreen;


    }


}
