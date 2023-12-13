package com.ecodulio.weatherapp.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.ecodulio.weatherapp.R;
import com.ecodulio.weatherapp.databinding.ActivityMainBinding;

public class Main extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        NavHostFragment mNavHostFragment = binding.navHostFragment.getFragment();
        NavController mMainController = mNavHostFragment.getNavController();
        mMainController.setGraph(R.navigation.main_navigation);
        NavigationUI.setupWithNavController(binding.bottomNavView, mMainController);

        setContentView(binding.getRoot());
    }
}