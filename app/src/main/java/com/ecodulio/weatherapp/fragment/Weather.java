package com.ecodulio.weatherapp.fragment;

import static com.ecodulio.weatherapp.utils.Constants.API_KEY;
import static com.ecodulio.weatherapp.utils.Constants.GET_CURRENT_WEATHER;
import static com.ecodulio.weatherapp.utils.Constants.ICON_BASE_URL;
import static com.ecodulio.weatherapp.utils.Global.addWeather;
import static com.ecodulio.weatherapp.utils.Global.capitalize;
import static com.ecodulio.weatherapp.utils.Global.clearUserData;
import static com.ecodulio.weatherapp.utils.Global.convertHashMap;
import static com.ecodulio.weatherapp.utils.Global.convertUnixTime;
import static com.ecodulio.weatherapp.utils.Global.getRequest;
import static com.ecodulio.weatherapp.utils.Global.getUserData;
import static com.ecodulio.weatherapp.utils.Global.getWeatherHistory;
import static com.ecodulio.weatherapp.utils.Global.isGPSEnabled;
import static com.ecodulio.weatherapp.utils.Global.showAlertDialog;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ecodulio.weatherapp.R;
import com.ecodulio.weatherapp.activity.Login;
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
    private TextView textName;
    private TextView textTemp;
    private TextView textLocation;
    private TextView textWeather;
    private TextView textSunrise;
    private TextView textSunset;
    private TextView textNoWeather;
    private TextView textNoSunrise;
    private TextView textNoSunset;
    private ImageView imageWeather;
    private LinearLayout layoutProgressWeather;
    private LinearLayout layoutProgressSunrise;
    private LinearLayout layoutProgressSunset;
    private SwipeRefreshLayout refreshLayout;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private ActivityResultLauncher<String> permissionResultLauncher;
    private ActivityResultLauncher<Intent> locationResultLauncher;
    private Location mLocation;
    private String latitude = "";
    private String longitude = "";
    private JSONObject resultObject;
    private boolean locationPermissionGranted;

    public Weather() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWeatherBinding.inflate(inflater, container, false);

        setVariables();
        setListeners();
        setWeather();

        return binding.getRoot();
    }

    private void setVariables() {
        context = (Main) getActivity();
        textName = binding.textName;
        textTemp = binding.textTemp;
        textLocation = binding.textLocation;
        textWeather = binding.textWeather;
        textNoWeather = binding.textNoWeather;
        textNoSunrise = binding.textNoSunrise;
        textNoSunset = binding.textNoSunset;
        imageWeather = binding.imageWeather;
        textSunrise = binding.textSunrise;
        textSunset = binding.textSunset;
        layoutProgressWeather = binding.layoutProgressWeather;
        layoutProgressSunrise = binding.layoutProgressSunrise;
        layoutProgressSunset = binding.layoutProgressSunset;
        refreshLayout = binding.refreshLayout;
    }

    private void setListeners() {
        refreshLayout.setOnRefreshListener(this::checkGPS);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(context, R.color.md_theme_light_primary));
        binding.btnBack.setOnClickListener(v -> showAlertDialog(context, "Logout", "Do you want to logout?", "CANCEL", "LOGOUT", (dialogInterface, i) -> {
            if (i == dialogInterface.BUTTON_POSITIVE) {
                clearUserData(context);
                startActivity(new Intent(context, Login.class));
                context.finish();
            }
        }));
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
    }

    private void setWeather() {
        if (resultObject == null) {
            checkGPS();
        } else {
            try {
                setData(resultObject, false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkGPS() {
        if (isGPSEnabled(context)) {
            getLocationPermission();
        } else {
            showAlertDialog(context, "Location Request", "Please turn on your location service to get the current weather", "CANCEL", "OK", (dialogInterface, i) -> {
                if (i == dialogInterface.BUTTON_POSITIVE) {
                    locationResultLauncher.launch(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                } else {
                    refreshLayout.setRefreshing(false);
                    showNoData();
                }
            });
        }
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
                        getCurrentWeather();
                    } else {
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                                new LocationCallback() {
                                    @Override
                                    public void onLocationResult(@NonNull LocationResult locationResult) {
                                        mLocation = locationResult.getLastLocation();
                                        if (mLocation != null) {
                                            longitude = String.valueOf(mLocation.getLongitude());
                                            latitude = String.valueOf(mLocation.getLatitude());
                                            getCurrentWeather();
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
        hashMap.put("appid", API_KEY);

        getRequest(context, GET_CURRENT_WEATHER, convertHashMap(hashMap), (e, result) -> {
            refreshLayout.setRefreshing(false);
            if (e == null) {
                try {
                    resultObject = new JSONObject(result.getResult());
                    if (result.getHeaders().code() == 200) {
                        setData(resultObject, true);
                    } else if (result.getHeaders().code() == 401) {
                        showAlertDialog(context, "Unauthorized", resultObject.getString("message"), "", "OK", (dialogInterface, i) -> showNoData());
                    } else {
                        showNoData();
                    }
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } else {
                e.printStackTrace();
            }
        });
    }

    private void setData(JSONObject jsonObject, boolean newRequest) throws JSONException {
        textName.setText(String.format("Hello, %s %s!", getUserData(context, "firstName"), getUserData(context, "lastName")));

        JSONObject weatherObject = jsonObject.getJSONArray("weather").getJSONObject(0);
        JSONObject sysObject = jsonObject.getJSONObject("sys");

        String location = String.format("%s, %s", jsonObject.getString("name"), sysObject.isNull("country") ? "N/A" : sysObject.getString("country"));
        String temperature = String.format("%s°", jsonObject.getJSONObject("main").getString("temp"));
        String icon = String.format("%s%s@4x.png", ICON_BASE_URL, weatherObject.getString("icon"));
        String description = capitalize(weatherObject.getString("description"));
        String sunrise = convertUnixTime(sysObject.getLong("sunrise"));
        String sunset = convertUnixTime(sysObject.getLong("sunset"));
        Picasso.get().load(icon).into(imageWeather);

        textLocation.setText(location);
        textTemp.setText(temperature);
        textWeather.setText(description);
        textSunrise.setText(sunrise);
        textSunset.setText(sunset);

        if (newRequest) {
            JSONArray weatherHistoryArray = getWeatherHistory(context, getUserData(context, "email"));
            JSONObject weatherHistoryObject = new JSONObject();
            weatherHistoryObject.put("location", location);
            weatherHistoryObject.put("temperature", temperature);
            weatherHistoryObject.put("icon", icon);
            weatherHistoryObject.put("description", description);
            weatherHistoryObject.put("sunrise", sunrise);
            weatherHistoryObject.put("sunset", sunset);
            weatherHistoryArray.put(weatherHistoryObject);
            addWeather(context, getUserData(context, "email"), weatherHistoryArray.toString());
        }

        layoutProgressWeather.setVisibility(View.GONE);
        layoutProgressSunrise.setVisibility(View.GONE);
        layoutProgressSunset.setVisibility(View.GONE);
    }

    private void showNoData() {
        layoutProgressWeather.getChildAt(1).setVisibility(View.GONE);
        layoutProgressSunrise.getChildAt(1).setVisibility(View.GONE);
        layoutProgressSunset.getChildAt(1).setVisibility(View.GONE);

        textNoWeather.setVisibility(View.VISIBLE);
        textNoSunrise.setVisibility(View.VISIBLE);
        textNoSunset.setVisibility(View.VISIBLE);
    }
}