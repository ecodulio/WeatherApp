package com.ecodulio.weatherapp.activity;

import static com.ecodulio.weatherapp.utils.Global.*;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.ecodulio.weatherapp.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateAccount extends AppCompatActivity {

    private TextInputEditText textInputFirstName;
    private TextInputEditText textInputLastName;
    private TextInputEditText textInputEmail;
    private TextInputEditText textInputPassword;
    private TextInputEditText textInputConfirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        textInputFirstName = findViewById(R.id.text_input_first_name);
        textInputLastName = findViewById(R.id.text_input_last_name);
        textInputEmail = findViewById(R.id.text_input_email);
        textInputPassword = findViewById(R.id.text_input_password);
        textInputConfirmPassword = findViewById(R.id.text_input_confirm_password);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_submit).setOnClickListener(v -> {
            if (isEmpty(this, new TextInputEditText[]{textInputFirstName, textInputLastName, textInputEmail, textInputPassword, textInputConfirmPassword})) {
                if (isValidEmail(getTextInput(textInputEmail))) {
                    if (isValidPassword(getTextInput(textInputPassword))) {
                        if (getTextInput(textInputConfirmPassword).equals(getTextInput(textInputPassword))) {
                            if (!hasAccount(this, getTextInput(textInputEmail))) {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("firstName", getTextInput(textInputFirstName));
                                    jsonObject.put("lastName", getTextInput(textInputLastName));
                                    jsonObject.put("email", getTextInput(textInputEmail));
                                    jsonObject.put("password", hashString(getTextInput(textInputPassword)));

                                    addAccount(this, getTextInput(textInputEmail), jsonObject.toString());
                                    Toast.makeText(this, "Your account has been created.", Toast.LENGTH_SHORT).show();
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                setTextError(this, textInputEmail, "This email is already registered.");
                            }
                        } else {
                            setTextError(this, textInputConfirmPassword, "Password does not match.");
                        }
                    } else {
                        setTextError(this, textInputPassword, "Weak password.\nPlease include at least 1 digit, 1 lowercase letter, 1 uppercase letter, 1 special character, and a minimum length of 6 characters");
                    }
                } else {
                    setTextError(this, textInputEmail, "Invalid email.");
                }
            }
        });
    }
}