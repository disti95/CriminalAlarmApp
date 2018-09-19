package com.example.stefa.sendsms2;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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

    Button sendSMSBtn;
    Button configBtn;
    EditText toPhoneNumberET;
    EditText smsMessageET;
    Double currentLattitude, currentLongitude;
    String GlobalToPhoneNumber;
    EditText contact1;
    EditText contact2;
    EditText contact3;
    EditText contact4;
    EditText contact5;
    LocationManager locationManager;
    long lastDown;
    long lastDuration;

    public static boolean stopThread = false;
    public static boolean stopService = false;
    public static boolean IsActive = false;
    public static int TIME_DELAY = 20000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sendSMSBtn = (Button) findViewById(R.id.sendSMSBtn);


        /*sendSMSBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMS();
            }
        });*/

        sendSMSBtn.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                touch(v, event);
                return true;
            }
        });


        configBtn = (Button) findViewById(R.id.configBtn);
        configBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showConfig();
            }
        });

        int PERMISSION_REQUEST_CODE = 1;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

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
        }




    }

    public void touch(View v, MotionEvent event) {

        Integer a=event.getAction();
        Log.d("myTag", "Beginn");
        Log.d("myTag", (a.toString()));

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:

                lastDown = System.currentTimeMillis()/1000;
                break;
            case MotionEvent.ACTION_UP:
                lastDuration = System.currentTimeMillis()/1000 - lastDown;
                sendSMSNew();
                break;
        }


}

    protected void sendSMSNew(){
        final ArrayList currentNumbers=getNumbers();
        final String message = getMessage();

        if(IsActive){

            if(lastDuration > 5){
                stopThread = true;
                IsActive = false;
                setAlarmButtonColor(Color.RED);
                stopGPS();

            }else{
                return;
            }
        }else{

            setAlarmButtonColor(Color.GREEN);
            startGPS();
            createSendingThread(currentNumbers, message);

        }
    }

    protected void sendSMS() {


    final ArrayList currentNumbers=getNumbers();
    final String message = getMessage();

    if(IsActive){

        stopThread = true;
        IsActive = false;
        GradientDrawable gradientDrawable = (GradientDrawable) sendSMSBtn.getBackground().mutate();
        gradientDrawable.setColor(Color.RED);
        locationManager.removeUpdates(this);
    }else{

        //sendSMSBtn.setBackgroundColor(Color.GREEN);

        GradientDrawable gradientDrawable = (GradientDrawable) sendSMSBtn.getBackground().mutate();
        gradientDrawable.setColor(Color.GREEN);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {
                    int a=1;

            }else{
                locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
        }






        try{
            Thread thread = new Thread(new Runnable() {
                private ArrayList Numbers;
                private String Message;
                {
                    this.Numbers = currentNumbers;
                    this.Message = message;
                }
                @Override
                public void run() {

                    try {
                        IsActive = true;
                        Thread.sleep(2000); //wait because of gps
                        while(!stopThread) {

                            for (Object number : currentNumbers) {
                                DoSendStandartMessage(number.toString(), Message);
                                Thread.sleep(1500);
                                DoSendLocation(number.toString());
                                Thread.sleep(1000);

                            }
                            Thread.sleep(TIME_DELAY);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }

                }
            }) {
               /* @Override
                public void run() {
                    try {
                        IsActive = true;
                        while(!stopThread) {

                            //for (String number : currentNumbers) {
                            DoSendStandartMessage();
                            sleep(2000);
                            DoSendLocation();
                            sleep(TIME_DELAY);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/
            };

            stopThread = false;
            thread.start();
        }catch(Exception ex){
            int a = 1;
            locationManager.removeUpdates(this);
        }


    }







    }

    protected void setAlarmButtonColor(int id){
        GradientDrawable gradientDrawable = (GradientDrawable) sendSMSBtn.getBackground().mutate();
        gradientDrawable.setColor(id);
    }

    protected void startGPS(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {


            }else{
                locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
        }
    }

    protected void stopGPS(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_DENIED) {


            }else{
                locationManager.removeUpdates(this);
            }
        }
    }

    protected void createSendingThread(final ArrayList currentNumbers, final String message){
        try{
            Thread thread = new Thread(new Runnable() {
                private ArrayList Numbers;
                private String Message;
                {
                    this.Numbers = currentNumbers;
                    this.Message = message;
                }
                @Override
                public void run() {

                    try {
                        IsActive = true;
                        Thread.sleep(2000); //wait because of gps
                        while(!stopThread) {

                            for (Object number : currentNumbers) {
                                DoSendStandartMessage(number.toString(), Message);
                                Thread.sleep(1500);
                                DoSendLocation(number.toString());
                                Thread.sleep(1000);

                            }
                            Thread.sleep(TIME_DELAY);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }

                }
            }) {

            };

            stopThread = false;
            thread.start();
        }catch(Exception ex){
            stopGPS();
        }
    }


    public ArrayList<String> getNumbers() {

        ArrayList<String> numbers = new ArrayList<String>();

        contact1 = (EditText) findViewById(R.id.Contact1);
        contact2 = (EditText) findViewById(R.id.Contact2);
        contact3 = (EditText) findViewById(R.id.Contact3);
        contact4 = (EditText) findViewById(R.id.Contact4);
        contact5 = (EditText) findViewById(R.id.Contact5);
        SharedPreferences sharedPreferences= this.getSharedPreferences("gameSetting", Context.MODE_PRIVATE);
        String number1=sharedPreferences.getString("contact1", "Default");
        if(!number1.isEmpty()){
            numbers.add(number1);
        }
        String number2=sharedPreferences.getString("contact2", "Default");
        if(!number2.isEmpty()){
            numbers.add(number2);
        }
        String number3=sharedPreferences.getString("contact3", "Default");
        if(!number3.isEmpty()){
            numbers.add(number3);
        }
        String number4=sharedPreferences.getString("contact4", "Default");
        if(!number4.isEmpty()){
            numbers.add(number4);
        }
        String number5=sharedPreferences.getString("contact5", "Default");
        if(!number5.isEmpty()){
            numbers.add(number5);
        }
        return numbers;
    }

    public String getMessage() {
        SharedPreferences sharedPreferences= this.getSharedPreferences("gameSetting", Context.MODE_PRIVATE);
        String message=sharedPreferences.getString("message", "Default");
        if(!message.isEmpty()){
            return message;
        }

        return null;
    }

    public void DoSendStandartMessage(String currentNumber, String message){
        //String toPhoneNumber = toPhoneNumberET.getText().toString();
        //String smsMessage = smsMessageET.getText().toString();
        Date currentTime = Calendar.getInstance().getTime();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String googleMapsHeader="https://maps.google.com/?q=";

        try {

         if(currentLattitude == null || currentLongitude == null){

             stopThread = true;
             GradientDrawable gradientDrawable = (GradientDrawable) sendSMSBtn.getBackground().mutate();
             gradientDrawable.setColor(Color.RED);
             Toast.makeText(getApplicationContext(),
                     "No gps values!",
                     Toast.LENGTH_LONG).show();
         }else{

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

    public void DoSendLocation(String currentNumber){
        // toPhoneNumber = toPhoneNumberET.getText().toString();
        Date currentTime = Calendar.getInstance().getTime();
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String googleMapsHeader="https://maps.google.com/?q=";

        try{

            if(currentLattitude == null || currentLongitude == null){

                stopThread = true;
                GradientDrawable gradientDrawable = (GradientDrawable) sendSMSBtn.getBackground().mutate();
                gradientDrawable.setColor(Color.RED);
                Toast.makeText(getApplicationContext(),
                        "No gps values!",
                        Toast.LENGTH_LONG).show();
            }else{

                addresses = geocoder.getFromLocation(currentLattitude, currentLongitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0);

                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(currentNumber, null, googleMapsHeader + address.replace(" ", ""), null, null);
            /*Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();*/
            }


        }catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "Sending SMS failed.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }

    protected void showConfig() {
        startActivity(new Intent(this, ConfigActivity.class));
    }

    @Override
    public void onLocationChanged(Location location) {

        currentLattitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }





    /*public class MyService extends Service {

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
        @Override
        public void onCreate() {
            Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();


        }
        @Override
        public void onStart(Intent intent, int startid) {
            Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

            while(stopService)
        }
        @Override
        public void onDestroy() {
            Toast.makeText(this, "Service Stopped", Toast.LENGTH_LONG).show();
            myPlayer.stop();
        }
    }*/

}
