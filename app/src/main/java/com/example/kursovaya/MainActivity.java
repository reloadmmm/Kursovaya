package com.example.kursovaya;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.example.kursovaya.ui.*;
import com.example.kursovaya.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                show(new HomeFragment());
                return true;
            } else if (id == R.id.nav_results) {
                show(new ResultsFragment());
                return true;
            } else if (id == R.id.nav_profile) {
                show(new ProfileFragment());
                return true;
            }
            return false;
        });
        binding.bottomNav.setSelectedItemId(R.id.nav_home);
    }

    private void show(androidx.fragment.app.Fragment f){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, f).commit();
    }
}
