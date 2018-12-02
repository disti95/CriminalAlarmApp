package com.example.stefa.sendsms2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
        String number1=sharedPreferences.getString(PreferencesString.CONTACT1, "Default");
        if(!number1.isEmpty()){
            Contacts.add(number1);
        }
        String number2=sharedPreferences.getString(PreferencesString.CONTACT2, "Default");
        if(!number2.isEmpty()){
            Contacts.add(number2);
        }
        String number3=sharedPreferences.getString(PreferencesString.CONTACT3, "Default");
        if(!number3.isEmpty()){
            Contacts.add(number3);
        }
        String number4=sharedPreferences.getString(PreferencesString.CONTACT4, "Default");
        if(!number4.isEmpty()){
            Contacts.add(number4);
        }
        String number5=sharedPreferences.getString(PreferencesString.CONTACT5, "Default");
        if(!number5.isEmpty()){
            Contacts.add(number5);
        }

        String message=sharedPreferences.getString(PreferencesString.MESSAGE, "Default");
        if(!message.isEmpty()){
            Message = message;
        }


        int interval=Integer.parseInt(sharedPreferences.getString(PreferencesString.INTERVAL, null));
        SendingInterval=interval;

        boolean activateLight=sharedPreferences.getBoolean(PreferencesString.LIGHT, false);
        ActivateLight=activateLight;

        boolean activateAudio=sharedPreferences.getBoolean(PreferencesString.ALARM, false);
        ActivateAudio=activateAudio;

        int autoOFF=Integer.parseInt(sharedPreferences.getString(PreferencesString.AUTO_OFF, null));
        AutoOFF=autoOFF;

        boolean lockScreen=sharedPreferences.getBoolean(PreferencesString.LOCKSCREEN, false);
        LockScreen=lockScreen;

    }

}
