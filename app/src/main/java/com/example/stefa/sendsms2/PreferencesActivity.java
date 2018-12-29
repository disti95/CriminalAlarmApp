package com.example.stefa.sendsms2;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceGroup;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PreferencesActivity extends AppCompatPreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final ArrayList<String> contactLists = new ArrayList<String>(){{
        this.add(PreferencesString.CONTACT1);
        this.add(PreferencesString.CONTACT2);
        this.add(PreferencesString.CONTACT3);
        this.add(PreferencesString.CONTACT4);
        this.add(PreferencesString.CONTACT5);
    }};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        loadContactsToContactList();
        initSummary(getPreferenceScreen());
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void loadContactsToContactList() {
        HashMap<String, String> contactMap = new HashMap<>();
        Cursor contacts = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        if(contacts != null && contacts.getCount() != 0) {
            while (contacts.moveToNext()) {
                String name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if(name != null && phoneNumber != null) {
                    contactMap.put(name, phoneNumber);
                }
            }
            contacts.close();
        }
        HashMap contactsSharedPreferences = getContacts();
        for (String contactsListKey: contactLists) {
            ArrayList<String> nameList = new ArrayList<>();
            ArrayList<String> numberList = new ArrayList<>();
            ListPreference listPreference = (ListPreference) getPreferenceScreen().findPreference(contactsListKey);
            for(Map.Entry entry: contactMap.entrySet()){
                if(!contactsSharedPreferences.containsValue(entry.getValue()) || (contactsSharedPreferences.containsValue(entry.getValue()) && contactsSharedPreferences.get(contactsListKey).equals(entry.getValue()))){
                    nameList.add(entry.getKey().toString());
                    numberList.add(entry.getValue().toString());
                }
            }
            nameList.add("unset");
            numberList.add("");
            listPreference.setEntries(nameList.toArray(new String[0]));
            listPreference.setEntryValues(numberList.toArray(new String[0]));
        }

    }

    private HashMap getContacts() {
        HashMap<String, String> contactsSharedPreferences = new HashMap<>();
        for (String contactsListKey: contactLists) {
            contactsSharedPreferences.put(contactsListKey, getPreferenceScreen().getSharedPreferences().getString(contactsListKey,""));
        }
        return contactsSharedPreferences;
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
        loadContactsToContactList();
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