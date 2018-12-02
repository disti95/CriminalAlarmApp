package com.example.stefa.sendsms2;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements LocationListener {

    ImageButton sendSMSBtn;
    ImageButton configBtn;
    EditText toPhoneNumberET;
    EditText smsMessageET;
    Double currentLattitude, currentLongitude;
    String GlobalToPhoneNumber;
    LocationManager locationManager;
    long lastDown;
    long lastDuration;
    MediaPlayer mP;

    public static boolean stopThread = false;
    public static boolean stopService = false;
    public static boolean IsActive = false;
    //public static int TIME_DELAY = 20000;
    public boolean flashLightStatus = false;
    private static final int CAMERA_REQUEST = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendSMSBtn = (ImageButton) findViewById(R.id.sendSMSBtn);

        /*sendSMSBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMS();
            }
        });*/


        /////////////////////////////////
        //TODO: Remove this before merge
        Button testbutton = (Button) findViewById(R.id.button);
        testbutton.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                EditText textField = (EditText) findViewById(R.id.editText);

                try {

//                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
//                    while (phones.moveToNext())
//                    {
//                        String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                        textField.setTextColor(Color.RED);
//                        textField.setText(name + " " + phoneNumber);
//                    }
//                    phones.close();



                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    String ena = sharedPreferences.getString(PreferencesString.CONTACT1, "");
                    textField.setTextColor(Color.RED);
                    textField.setText(ena);
                }
                catch (Exception e) {
                    textField.setTextColor(Color.RED);
                    textField.setText(e.getMessage());
                }
                return true;
            }
        });
        ///////////////////////////////


        sendSMSBtn.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                touch(v, event);
                return true;
            }
        });


        configBtn = (ImageButton) findViewById(R.id.configBtn);
        configBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showConfig();
            }
        });

        int PERMISSION_REQUEST_CODE = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to READ_CONTACTS - requesting it");
                String[] permissions = {Manifest.permission.READ_CONTACTS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }


            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to ACCESS_FINE_LOCATION - requesting it");
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }

            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to ACCESS_COARSE_LOCATION - requesting it");
                String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }

            if (checkSelfPermission(Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to INTERNET - requesting it");
                String[] permissions = {Manifest.permission.INTERNET};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }

            if (checkSelfPermission(Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_DENIED){
                final boolean hasCameraFlash = getPackageManager().
                        hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

                Log.d("permission", "permission denied to INTERNET - requesting it");
                String[] permissions = {Manifest.permission.CAMERA};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
            try{
                //mP= new MediaPlayer();
                mP = MediaPlayer.create(MainActivity.this,R.raw.siren_noise);
                mP.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mP.start();
                    }

                });
                int a=1;
            }catch (Exception err){
                int a=1;
            }






//            final boolean hasCameraFlash = getPackageManager().
//                    hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
//            boolean isEnabled = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                    == PackageManager.PERMISSION_GRANTED;


        }



    }

    public void touch(View v, MotionEvent event) {

        Integer a = event.getAction();
        Log.d("myTag", "Beginn");
        Log.d("myTag", (a.toString()));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                lastDown = System.currentTimeMillis() / 1000;
                break;
            case MotionEvent.ACTION_UP:
                lastDuration = System.currentTimeMillis() / 1000 - lastDown;
                sendSMSNew();
                break;
        }


    }

    protected void sendSMSNew() {

        ConfigParameters settings = new ConfigParameters(this);

        if(settings.Message == null){

            CreateMessage("Error", "Please enter a message in the settings.");
            return;
        }

//        if(settings.Contacts[0] == null){
//
//        }
//        final ArrayList currentNumbers = getNumbers();
//        final String message = getMessage();

        if (IsActive) {

            if (lastDuration > 5) {
                stopThread = true;
                IsActive = false;

                configBtn.setClickable(true);
                configBtn.setEnabled(true);
                ImageButton btn = (ImageButton) findViewById(R.id.sendSMSBtn);
                btn.setBackgroundResource(R.drawable.circle_danger);
                //setAlarmButtonColor(Color.RED);
                stopGPS();

                if(settings.ActivateLight){
                    flashLightOff();
                }
                if(settings.ActivateAudio){
                    mP.pause();
                }


            } else {
                return;
            }
        } else {

            if(settings.ActivateLight){
                flashLightOn();
            }
            if(settings.ActivateAudio){
                mP.start();
            }

            //sendSMSBtn.setBackgroundColor(Color.GREEN);
            configBtn.setClickable(false);
            configBtn.setEnabled(false);
            setAlarmButtonColor();
            startGPS();
            createSendingThread(settings);

        }
    }

    private void CreateMessage(String title, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void SoundOn(){


    }

    private void SoundOff(){

    }


    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            flashLightStatus = true;
        } catch (CameraAccessException e) {
            int a=1;
        }
    }

    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            flashLightStatus = false;
        } catch (CameraAccessException e) {
            int a=1;
        }
    }

    protected void setAlarmButtonColor() {
        try {
//            GradientDrawable gradientDrawable = (GradientDrawable) sendSMSBtn.getBackground().mutate();
//            gradientDrawable.setColor(id);
            ImageButton btn = (ImageButton) findViewById(R.id.sendSMSBtn);
            btn.setBackgroundResource(R.drawable.circle_danger_green);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void startGPS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {


            } else {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
        }
    }

    protected void stopGPS() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {


            } else {
                locationManager.removeUpdates(this);
            }
        }
    }

    protected void createSendingThread(final ConfigParameters settings) {
        try {
            Thread thread = new Thread(new Runnable() {
                private ConfigParameters alarmSettings;

                //                private ArrayList Numbers;
//                private String Message;
                {
//                    this.Numbers = currentNumbers;
//                    this.Message = message;
                    this.alarmSettings = settings;
                }

                @Override
                public void run() {

                    try {
                        IsActive = true;
                        Thread.sleep(5000); //wait because of gps
                        while (!stopThread) {

                            for (Object number : alarmSettings.Contacts) {
                                DoSendStandartMessage(number.toString(), alarmSettings.Message);
                                Thread.sleep(1500);
                                DoSendLocation(number.toString());
                                Thread.sleep(1000);

                            }
                            Thread.sleep(alarmSettings.SendingInterval);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }

                }
            }) {

            };

            stopThread = false;
            thread.start();
        } catch (Exception ex) {
            stopGPS();
        }
    }


    public ArrayList<String> getNumbers() {

        ArrayList<String> numbers = new ArrayList<String>();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String number1 = sharedPreferences.getString("contact1", "Default");
        if (!number1.isEmpty()) {
            numbers.add(number1);
        }
        String number2 = sharedPreferences.getString("contact2", "Default");
        if (!number2.isEmpty()) {
            numbers.add(number2);
        }
        String number3 = sharedPreferences.getString("contact3", "Default");
        if (!number3.isEmpty()) {
            numbers.add(number3);
        }
        String number4 = sharedPreferences.getString("contact4", "Default");
        if (!number4.isEmpty()) {
            numbers.add(number4);
        }
        String number5 = sharedPreferences.getString("contact5", "Default");
        if (!number5.isEmpty()) {
            numbers.add(number5);
        }
        return numbers;
    }

    public String getMessage() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String message = sharedPreferences.getString("message", "Default");
        if (!message.isEmpty()) {
            return message;
        }

        return null;
    }

    public void DoSendStandartMessage(String currentNumber, String message) {
        //String toPhoneNumber = toPhoneNumberET.getText().toString();
        //String smsMessage = smsMessageET.getText().toString();
        Date currentTime = Calendar.getInstance().getTime();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String googleMapsHeader = "https://maps.google.com/?q=";

        try {

            if (currentLattitude == null || currentLongitude == null) {

                stopThread = true;
                GradientDrawable gradientDrawable = (GradientDrawable) sendSMSBtn.getBackground().mutate();
                gradientDrawable.setColor(Color.RED);
                Toast.makeText(getApplicationContext(),
                        "No gps values!",
                        Toast.LENGTH_LONG).show();
            } else {

                addresses = geocoder.getFromLocation(currentLattitude, currentLongitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0);
                message = message + " \nZeitpunkt: " + currentTime.toString()
                        + " \nLängengrad: " + currentLongitude.toString()
                        + " \nBreitengrad: " + currentLattitude.toString()
                        + " \nAdresse: " + address;
                //+ " \nLink: " + googleMapsHeader + address.replace(" ", "");

                //}

                // message can have only 160 characters
                SmsManager smsManager = SmsManager.getDefault();
                ArrayList<String> parts = smsManager.divideMessage(message);
                smsManager.sendMultipartTextMessage(currentNumber, null, parts, null, null);

                //SystemClock.sleep(1000);
            }


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Sending SMS failed.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public void DoSendLocation(String currentNumber) {
        // toPhoneNumber = toPhoneNumberET.getText().toString();
        Date currentTime = Calendar.getInstance().getTime();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String googleMapsHeader = "https://maps.google.com/?q=";

        try {

            if (currentLattitude == null || currentLongitude == null) {

                stopThread = true;
                GradientDrawable gradientDrawable = (GradientDrawable) sendSMSBtn.getBackground().mutate();
                gradientDrawable.setColor(Color.RED);
                Toast.makeText(getApplicationContext(),
                        "No gps values!",
                        Toast.LENGTH_LONG).show();
            } else {

                addresses = geocoder.getFromLocation(currentLattitude, currentLongitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0);

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(currentNumber, null, googleMapsHeader + address.replace(" ", ""), null, null);
            /*Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();*/
            }


        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Sending SMS failed.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }

    protected void showConfig() {
        startActivity(new Intent(this, PreferencesActivity.class));
    }

    @Override
    public void onLocationChanged(Location location) {

        currentLattitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude", "disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude", "enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude", "status");
    }


}
