package com.example.stefa.sendsms2;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.provider.ContactsContract;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        initSummary(getPreferenceScreen());
        loadContactsToContactList();
    }

    public void loadContactsToContactList() {
        ListPreference listPreference = (ListPreference) getPreferenceScreen().findPreference("t2");
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> numberList = new ArrayList<>();
        Cursor contacts = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        if(contacts != null && contacts.getCount() != 0) {
            while (contacts.moveToNext()) {
                String name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if(name != null && phoneNumber != null) {
                         nameList.add(name);
                         numberList.add(phoneNumber);
                }
            }
            contacts.close();
        }
        listPreference.setEntries(nameList.toArray(new String[0]));
        listPreference.setEntryValues(numberList.toArray(new String[0]));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref instanceof EditTextPreference) {
            EditTextPreference editTextPreference = (EditTextPreference) pref;
            pref.setSummary(editTextPreference.getText());
        }
        if (pref instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) pref;
            pref.setSummary(listPreference.getEntry());
        }
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        }
//        if (p instanceof MultiSelectListPreference) {
//            EditTextPreference editTextPref = (EditTextPreference) p;
//            p.setSummary(editTextPref.getText());
//        }
    }

}