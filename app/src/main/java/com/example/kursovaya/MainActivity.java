package com.example.kursovaya;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.kursovaya.data.LeaderboardRepo;
import com.example.kursovaya.databinding.ActivityMainBinding;
import com.example.kursovaya.ui.HomeFragment;
import com.example.kursovaya.ui.ProfileFragment;
import com.example.kursovaya.ui.ResultsFragment;
import com.example.kursovaya.viewmodel.TaskViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TaskViewModel vm;

    private FirebaseAuth auth;
    private final LeaderboardRepo leaderboardRepo = new LeaderboardRepo();

    private String cachedNick = null;
    private String cachedPlace = null;

    private final FirebaseAuth.AuthStateListener authListener = firebaseAuth -> {
        if (firebaseAuth.getCurrentUser() != null) {
            fetchProfileAndWireLeaderboard();
        } else {
            cachedNick = null;
            cachedPlace = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        vm = new ViewModelProvider(this).get(TaskViewModel.class);

        // Firebase
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(authListener);

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

        if (auth.getCurrentUser() != null) {
            fetchProfileAndWireLeaderboard();
        }
    }

    private void show(androidx.fragment.app.Fragment f) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, f)
                .commit();
    }

    private void fetchProfileAndWireLeaderboard() {
        String uid = auth.getCurrentUser() == null ? null : auth.getCurrentUser().getUid();
        if (uid == null) return;

        FirebaseDatabase.getInstance().getReference("users").child(uid).get()
                .addOnSuccessListener(snap -> {
                    cachedNick = String.valueOf(snap.child("nick").getValue());
                    cachedPlace = String.valueOf(snap.child("place").getValue());
                    if ("null".equals(cachedNick)) cachedNick = "";
                    if ("null".equals(cachedPlace)) cachedPlace = "";

                    vm.getTotal().observe(this, totalVal -> {
                        Integer t = totalVal == null ? 0 : totalVal;
                        vm.getDoneCount().observe(this, doneVal -> {
                            Integer d = doneVal == null ? 0 : doneVal;
                            if (cachedNick != null && cachedPlace != null) {
                                leaderboardRepo.updateUserStats(t, d, cachedNick, cachedPlace);
                            }
                        });
                    });
                })
                .addOnFailureListener(err -> {
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (auth != null && authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
