package com.ecodulio.weatherapp.activity;

import static com.ecodulio.weatherapp.utils.Global.addAccount;
import static com.ecodulio.weatherapp.utils.Global.getTextInput;
import static com.ecodulio.weatherapp.utils.Global.hasAccount;
import static com.ecodulio.weatherapp.utils.Global.hashString;
import static com.ecodulio.weatherapp.utils.Global.isEmpty;
import static com.ecodulio.weatherapp.utils.Global.isValidEmail;
import static com.ecodulio.weatherapp.utils.Global.isValidPassword;
import static com.ecodulio.weatherapp.utils.Global.setTextError;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ecodulio.weatherapp.databinding.ActivityCreateAccountBinding;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateAccount extends AppCompatActivity {

    private ActivityCreateAccountBinding binding;
    private TextInputEditText textInputFirstName;
    private TextInputEditText textInputLastName;
    private TextInputEditText textInputEmail;
    private TextInputEditText textInputPassword;
    private TextInputEditText textInputConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());

        setVariables();
        setListeners();

        setContentView(binding.getRoot());
    }

    private void setVariables() {
        textInputFirstName = binding.textInputFirstName;
        textInputLastName = binding.textInputLastName;
        textInputEmail = binding.textInputEmail;
        textInputPassword = binding.textInputPassword;
        textInputConfirmPassword = binding.textInputConfirmPassword;
    }

    private void setListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnSubmit.setOnClickListener(v -> {
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
