package com.ecodulio.weatherapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.ecodulio.weatherapp.databinding.FragmentWeatherHistoryBinding;

public class WeatherHistory extends Fragment {

    private FragmentWeatherHistoryBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWeatherHistoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}