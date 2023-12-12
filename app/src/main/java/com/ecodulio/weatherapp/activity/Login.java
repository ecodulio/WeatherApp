package com.ecodulio.weatherapp.activity;

import static com.ecodulio.weatherapp.utils.Global.*;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ecodulio.weatherapp.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private TextInputEditText textInputEmail;
    private TextInputEditText textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (hasUserData(this)) {
            startActivity(new Intent(this, Main.class));
            finish();
        }

        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);

        findViewById(R.id.btn_login).setOnClickListener(v -> {
            if (isEmpty(this, new TextInputEditText[]{textInputEmail, textInputPassword})) {
                if (hasAccount(this, getTextInput(textInputEmail))) {
                    JSONObject jsonObject = getAccount(this, getTextInput(textInputEmail));
                    try {
                        if (hashString(getTextInput(textInputPassword)).equals(jsonObject.getString("password"))) {
                            setUserData(this, jsonObject.toString());
                            startActivity(new Intent(this, Main.class));
                            finish();
                        } else {
                            Toast.makeText(this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Invalid credentials.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_create_account).setOnClickListener(v -> {
            startActivity(new Intent(this, CreateAccount.class));
        });
    }
}