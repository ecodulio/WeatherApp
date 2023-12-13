package com.ecodulio.weatherapp.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AlertDialog;

import com.ecodulio.weatherapp.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class Global {
    public static AlertDialog dialog;

    public static boolean isEmpty(Context context, TextInputEditText[] mTextInputEditTexts) {
        for (TextInputEditText text : mTextInputEditTexts) {
            if (text.getText().toString().isEmpty()) {
                setTextError(context, text, "This field is required.");
                text.requestFocus();
                return false;
            } else {
                clearTextError(text);
            }
        }
        return true;
    }

    public static String getTextInput(TextInputEditText textInputEditText) {
        return textInputEditText.getText() == null ? "" : textInputEditText.getText().toString();
    }

    public static void setTextError(Context context, View view, String error) {
        Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
        ((TextInputLayout) view.getParent().getParent()).setError(error);
        ((TextInputLayout) view.getParent().getParent()).startAnimation(shake);
    }

    public static void clearTextError(View view) {
        if (((TextInputLayout) view.getParent().getParent()).isErrorEnabled()) {
            ((TextInputLayout) view.getParent().getParent()).setErrorEnabled(false);
        }
    }

    public static boolean isValidPassword(final String password) {
        return Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[.~!@#$%^&*()_+=])(?!.*\\s).{6,}$").matcher(password).matches();
    }

    public static boolean isValidEmail(final String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isGPSEnabled(Context context) {
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        return startActivityIfNeeded(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
    }

    public static String hashString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(input.getBytes("UTF-8"));
            return Base64.encodeToString(hashedBytes, Base64.DEFAULT);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getUserData(Context context, String key) {
        if (hasUserData(context)) {
            try {
                JSONObject jsonObject = new JSONObject(context.getSharedPreferences("userData", Context.MODE_PRIVATE).getString("data", ""));
                return jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static Map<String, List<String>> convertHashMap(HashMap<String, String> hashMap) {
        Map<String, List<String>> map = new HashMap<>();
        for (Map.Entry<String, String> entry : hashMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            List<String> values = map.computeIfAbsent(key, k -> new ArrayList<>());
            values.add(value);
        }
        return map;
    }

    public static String convertUnixTime(long unixTime) {
        Date date = new Date(unixTime * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        return sdf.format(date);
    }

    public static void dismissAlertDialog() {
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();
    }

    public static void showAlertDialog(Context context, String title, String message, String negative, String positive, DialogInterface.OnClickListener onClick) {
        dismissAlertDialog();
        dialog = new MaterialAlertDialogBuilder(context, R.style.MaterialAlertDialogStyle).create();
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setButton(Dialog.BUTTON_POSITIVE, positive, onClick);
        if (!negative.isEmpty()) {
            dialog.setButton(Dialog.BUTTON_NEGATIVE, negative, onClick);
        }
        dialog.show();
    }

    public static void getRequest(Context context, String endpoint, Map<String, List<String>> queries, FutureCallback<Response<String>> callback) {
        Ion.with(context).load("GET", endpoint)
                .setTimeout(15000)
                .addQueries(queries)
                .asString()
                .withResponse()
                .setCallback(callback);
    }

    public static JSONObject getAccount(Context context, String email) {
        try {
            return new JSONObject(context.getSharedPreferences("accounts", Context.MODE_PRIVATE).getString(email, ""));
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static JSONArray getWeatherHistory(Context context, String email) {
        try {
            return new JSONArray(context.getSharedPreferences("history", Context.MODE_PRIVATE).getString(email, ""));
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    public static void setUserData(Context context, String data) {
        context.getSharedPreferences("userData", Context.MODE_PRIVATE).edit().putString("data", data).apply();
    }

    public static void addWeather(Context context, String email, String data) {
        context.getSharedPreferences("history", Context.MODE_PRIVATE).edit().putString(email, data).apply();
    }

    public static void addAccount(Context context, String email, String data) {
        context.getSharedPreferences("accounts", Context.MODE_PRIVATE).edit().putString(email, data).apply();
    }

    public static boolean hasAccount(Context context, String email) {
        return context.getSharedPreferences("accounts", Context.MODE_PRIVATE).contains(email);
    }

    public static boolean hasHistory(Context context, String email) {
        return context.getSharedPreferences("history", Context.MODE_PRIVATE).contains(email);
    }

    public static boolean hasUserData(Context context) {
        return context.getSharedPreferences("userData", Context.MODE_PRIVATE).contains("data");
    }

    public static void clearUserData(Context context) {
        context.getSharedPreferences("userData", Context.MODE_PRIVATE).edit().remove("data").apply();
    }
}
