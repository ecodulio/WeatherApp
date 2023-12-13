package com.ecodulio.weatherapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ecodulio.weatherapp.R;
import com.ecodulio.weatherapp.model.WeatherObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WeatherHistoryAdapter extends RecyclerView.Adapter<WeatherHistoryAdapter.WeatherHistoryViewHolder> {

    private final ArrayList<WeatherObject> weatherObjects;

    public WeatherHistoryAdapter(ArrayList<WeatherObject> objects) {
        weatherObjects = objects;
    }

    @NonNull
    @Override
    public WeatherHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_weather_history, parent, false);
        return new WeatherHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherHistoryAdapter.WeatherHistoryViewHolder holder, int position) {
        WeatherObject object = weatherObjects.get(position);
        holder.location.setText(object.getLocation());
        holder.temperature.setText(object.getTemperature());
        holder.description.setText(object.getDescription());
        holder.sunrise.setText(object.getSunrise());
        holder.sunset.setText(object.getSunset());

        Picasso.get().load(object.getIcon()).into(holder.icon);
    }

    @Override
    public int getItemCount() {
        return weatherObjects.size();
    }

    static class WeatherHistoryViewHolder extends RecyclerView.ViewHolder {

        private final TextView location;
        private final TextView temperature;
        private final TextView description;
        private final TextView sunrise;
        private final TextView sunset;
        private final ImageView icon;

        public WeatherHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.text_location);
            temperature = itemView.findViewById(R.id.text_temperature);
            description = itemView.findViewById(R.id.text_description);
            sunrise = itemView.findViewById(R.id.text_sunrise);
            sunset = itemView.findViewById(R.id.text_sunset);
            icon = itemView.findViewById(R.id.image_weather);
        }
    }
}
