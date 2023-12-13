package com.ecodulio.weatherapp.activity;

import static com.ecodulio.weatherapp.utils.Global.getAccount;
import static com.ecodulio.weatherapp.utils.Global.getTextInput;
import static com.ecodulio.weatherapp.utils.Global.hasAccount;
import static com.ecodulio.weatherapp.utils.Global.hasUserData;
import static com.ecodulio.weatherapp.utils.Global.hashString;
import static com.ecodulio.weatherapp.utils.Global.isEmpty;
import static com.ecodulio.weatherapp.utils.Global.setUserData;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ecodulio.weatherapp.databinding.ActivityLoginBinding;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private TextInputEditText textInputEmail;
    private TextInputEditText textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (hasUserData(this)) {
            startActivity(new Intent(this, Main.class));
            finish();
        }

        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setVariables();
        setListeners();

        setContentView(binding.getRoot());
    }

    private void setVariables() {
        textInputEmail = binding.textInputEmail;
        textInputPassword = binding.textInputPassword;
    }

    private void setListeners() {
        binding.btnLogin.setOnClickListener(v -> {
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

        binding.btnCreateAccount.setOnClickListener(v -> startActivity(new Intent(this, CreateAccount.class)));
    }
}