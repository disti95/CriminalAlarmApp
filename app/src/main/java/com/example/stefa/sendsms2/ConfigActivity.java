package com.example.stefa.sendsms2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by stefa on 13.03.2018.
 */
public class ConfigActivity extends Activity {

    Button toMainBtn;
    EditText contact1;
    EditText contact2;
    EditText contact3;
    EditText contact4;
    EditText contact5;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config);

        toMainBtn = (Button) findViewById(R.id.toMainBtn);
        toMainBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showConfig();
            }
        });



        SharedPreferences sharedPreferences= this.getSharedPreferences("gameSetting", Context.MODE_PRIVATE);

        contact1 = (EditText) findViewById(R.id.Contact1);
        contact2 = (EditText) findViewById(R.id.Contact2);
        contact3 = (EditText) findViewById(R.id.Contact3);
        contact4 = (EditText) findViewById(R.id.Contact4);
        contact5 = (EditText) findViewById(R.id.Contact5);

        if(sharedPreferences!= null) {
            String number1=sharedPreferences.getString("contact1", "Default");
            if(number1 != "Default"){
                contact1.setText(number1);
            }
            String number2=sharedPreferences.getString("contact2", "Default");
            if(number2 != "Default"){
                contact2.setText(number2);
            }
            String number3=sharedPreferences.getString("contact3", "Default");
            if(number3 != "Default"){
                contact3.setText(number3);
            }
            String number4=sharedPreferences.getString("contact4", "Default");
            if(number4 != "Default"){
                contact4.setText(number4);
            }
            String number5=sharedPreferences.getString("contact5", "Default");
            if(number5 != "Default"){
                contact5.setText(number5);
            }


        }

    }

    protected void showConfig() {

        SharedPreferences sharedPreferences= this.getSharedPreferences("gameSetting", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        contact1 = (EditText) findViewById(R.id.Contact1);
        contact2 = (EditText) findViewById(R.id.Contact2);
        contact3 = (EditText) findViewById(R.id.Contact3);
        contact4 = (EditText) findViewById(R.id.Contact4);
        contact5 = (EditText) findViewById(R.id.Contact5);

        editor.putString("contact1",contact1.getText().toString());
        editor.putString("contact2",contact2.getText().toString());
        editor.putString("contact3",contact3.getText().toString());
        editor.putString("contact4",contact4.getText().toString());
        editor.putString("contact5",contact5.getText().toString());

        editor.commit();
        startActivity(new Intent(this, MainActivity.class));
    }
}
