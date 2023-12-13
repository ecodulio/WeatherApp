package com.ecodulio.weatherapp.fragment;

import static com.ecodulio.weatherapp.utils.Global.getUserData;
import static com.ecodulio.weatherapp.utils.Global.getWeatherHistory;
import static com.ecodulio.weatherapp.utils.Global.hasHistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecodulio.weatherapp.activity.Main;
import com.ecodulio.weatherapp.adapter.WeatherHistoryAdapter;
import com.ecodulio.weatherapp.databinding.FragmentWeatherHistoryBinding;
import com.ecodulio.weatherapp.model.WeatherObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WeatherHistory extends Fragment {

    private Main context;
    private FragmentWeatherHistoryBinding binding;
    private RecyclerView listWeatherHistory;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWeatherHistoryBinding.inflate(inflater, container, false);

        setVariables();
        setAdapter();

        return binding.getRoot();
    }

    private void setVariables() {
        context = (Main) getActivity();
        listWeatherHistory = binding.listWeatherHistory;
    }

    private void setAdapter() {
        String email = getUserData(context, "email");
        if (hasHistory(context, email)) {
            try {
                ArrayList<WeatherObject> weatherObjects = new ArrayList<>();
                JSONArray jsonArray = getWeatherHistory(context, email);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    WeatherObject obj = new WeatherObject();
                    obj.setLocation(jsonObject.getString("location"));
                    obj.setTemperature(jsonObject.getString("temperature"));
                    obj.setDescription(jsonObject.getString("description"));
                    obj.setSunrise(jsonObject.getString("sunrise"));
                    obj.setSunset(jsonObject.getString("sunset"));
                    obj.setIcon(jsonObject.getString("icon"));
                    weatherObjects.add(obj);
                }

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

                WeatherHistoryAdapter weatherHistoryAdapter = new WeatherHistoryAdapter(weatherObjects);

                listWeatherHistory.setLayoutManager(linearLayoutManager);
                listWeatherHistory.setAdapter(weatherHistoryAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            binding.layoutNoData.setVisibility(View.VISIBLE);
        }
    }
}