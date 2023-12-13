package com.ecodulio.weatherapp.fragment;

import static com.ecodulio.weatherapp.utils.Constants.APP_ID;
import static com.ecodulio.weatherapp.utils.Constants.GET_CURRENT_WEATHER;
import static com.ecodulio.weatherapp.utils.Constants.GET_REVERSE_GEOCODING;
import static com.ecodulio.weatherapp.utils.Constants.ICON_BASE_URL;
import static com.ecodulio.weatherapp.utils.Global.capitalize;
import static com.ecodulio.weatherapp.utils.Global.convertHashMap;
import static com.ecodulio.weatherapp.utils.Global.convertUnixTime;
import static com.ecodulio.weatherapp.utils.Global.getRequest;
import static com.ecodulio.weatherapp.utils.Global.isGPSEnabled;
import static com.ecodulio.weatherapp.utils.Global.logResponse;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ecodulio.weatherapp.activity.Main;
import com.ecodulio.weatherapp.databinding.FragmentWeatherBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Weather extends Fragment {

    private Main context;

    private FragmentWeatherBinding binding;
    private TextView textTemp;
    private TextView textLocation;
    private TextView textWeather;
    private TextView textSunrise;
    private TextView textSunset;
    private ImageView imageWeather;
    private LinearLayout layoutProgressWeather;
    private LinearLayout layoutProgressSunrise;
    private LinearLayout layoutProgressSunset;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private ActivityResultLauncher<String> permissionResultLauncher;
    private ActivityResultLauncher<Intent> locationResultLauncher;
    private Location mLocation;
    private String latitude = "";
    private String longitude = "";
    private boolean locationPermissionGranted;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWeatherBinding.inflate(inflater, container, false);
        initializeVariables();

        permissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            locationPermissionGranted = result;
            requestLocation();
        });

        locationResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (isGPSEnabled(context)) {
                getLocationPermission();
            } else {
                Toast.makeText(context, "Location must be enabled.", Toast.LENGTH_SHORT).show();
            }
        });

        if (isGPSEnabled(context)) {
            getLocationPermission();
        } else {
            locationResultLauncher.launch(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        return binding.getRoot();
    }

    private void initializeVariables() {
        context = (Main) getActivity();
        textTemp = binding.textTemp;
        textLocation = binding.textLocation;
        textWeather = binding.textWeather;
        imageWeather = binding.imageWeather;
        textSunrise = binding.textSunrise;
        textSunset = binding.textSunset;
        layoutProgressWeather = binding.layoutProgressWeather;
        layoutProgressSunrise = binding.layoutProgressSunrise;
        layoutProgressSunset = binding.layoutProgressSunset;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            requestLocation();
        } else {
            permissionResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void requestLocation() {
        try {
            if (locationPermissionGranted) {
                final LocationRequest locationRequest = new LocationRequest()
                        .setNumUpdates(1)
                        .setFastestInterval(0)
                        .setSmallestDisplacement(0)
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    mLocation = location;
                    if (location != null) {
                        longitude = String.valueOf(mLocation.getLongitude());
                        latitude = String.valueOf(mLocation.getLatitude());
                        getReverseGeocoding();
                        getCurrentWeather();
                        Log.d("Coordinates", longitude + "-" + latitude);
                    } else {
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                                new LocationCallback() {
                                    @Override
                                    public void onLocationResult(@NonNull LocationResult locationResult) {
                                        mLocation = locationResult.getLastLocation();
                                        if (mLocation != null) {
                                            longitude = String.valueOf(mLocation.getLongitude());
                                            latitude = String.valueOf(mLocation.getLatitude());
                                            getReverseGeocoding();
                                            getCurrentWeather();
                                            Log.d("Coordinates", longitude + "-" + latitude);
                                        } else {
                                            Toast.makeText(context, "Failed to retrieve location.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },
                                Looper.getMainLooper());
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("error-requestLocation", e.getMessage(), e);
        }
    }

    private void getCurrentWeather() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("lat", latitude);
        hashMap.put("lon", longitude);
        hashMap.put("units", "metric");
        hashMap.put("appid", APP_ID);

        getRequest(context, GET_CURRENT_WEATHER, convertHashMap(hashMap), (e, result) -> {
            if (e == null) {
                logResponse(result);
                try {
                    JSONObject jsonObject = new JSONObject(result.getResult());
                    JSONObject weatherObject = jsonObject.getJSONArray("weather").getJSONObject(0);
                    JSONObject sysObject = jsonObject.getJSONObject("sys");
                    textTemp.setText(String.format("%s°", jsonObject.getJSONObject("main").getString("temp")));
                    Picasso.get().load(String.format("%s%s@4x.png", ICON_BASE_URL, weatherObject.getString("icon")))
                            .into(imageWeather);
                    textWeather.setText(capitalize(weatherObject.getString("description")));
                    textSunrise.setText(convertUnixTime(sysObject.getLong("sunrise")));
                    textSunset.setText(convertUnixTime(sysObject.getLong("sunset")));

                    layoutProgressWeather.setVisibility(View.GONE);
                    layoutProgressSunrise.setVisibility(View.GONE);
                    layoutProgressSunset.setVisibility(View.GONE);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } else {
                e.printStackTrace();
            }
        });
    }

    private void getReverseGeocoding() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("lat", latitude);
        hashMap.put("lon", longitude);
        hashMap.put("limit", "1");
        hashMap.put("appid", APP_ID);

        getRequest(context, GET_REVERSE_GEOCODING, convertHashMap(hashMap), (e, result) -> {
            if (e == null) {
                logResponse(result);
                try {
                    JSONObject jsonObject = new JSONArray(result.getResult()).getJSONObject(0);
                    textLocation.setText(String.format("%s, %s", jsonObject.getString("name"), jsonObject.getString("country")));
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } else {
                e.printStackTrace();
            }
        });
    }
}