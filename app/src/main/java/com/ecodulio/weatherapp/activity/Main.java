package com.ecodulio.weatherapp.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.ecodulio.weatherapp.R;
import com.ecodulio.weatherapp.databinding.ActivityMainBinding;

public class Main extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavHostFragment mNavHostFragment;
    private NavController mMainController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mNavHostFragment = binding.navHostFragment.getFragment();
        mMainController = mNavHostFragment.getNavController();
        mMainController.setGraph(R.navigation.main_navigation);
        NavigationUI.setupWithNavController(binding.bottomNavView, mMainController);
    }
}