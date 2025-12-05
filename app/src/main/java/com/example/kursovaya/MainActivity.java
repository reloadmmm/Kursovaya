package com.example.kursovaya;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.example.kursovaya.notif.NotificationUtil;
import com.example.kursovaya.App;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    private String cachedNick = null;
    private String cachedPlace = null;

    private final LeaderboardRepo leaderboardRepo = new LeaderboardRepo();

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

        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1002
                );
            }
        }

        vm = new ViewModelProvider(this).get(TaskViewModel.class);

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

        binding.bottomNav.post(() -> {
            View homeTab = binding.bottomNav.findViewById(R.id.nav_home);
            if (homeTab != null) {
                homeTab.setOnLongClickListener(v -> {
                    androidx.fragment.app.Fragment f =
                            getSupportFragmentManager().findFragmentById(R.id.container);
                    if (f instanceof HomeFragment) {
                        ((HomeFragment) f).toggleQuickPanel();
                    }
                    return true;
                });
            }
        });

        if (auth.getCurrentUser() != null) {
            fetchProfileAndWireLeaderboard();
        }
    }


    private void show(androidx.fragment.app.Fragment f) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, f)
                .commit();
    }

    private void fetchProfileAndWireLeaderboard() {
        if (auth.getCurrentUser() == null) return;

        String uid = auth.getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("users")
                .child(uid)
                .get()
                .addOnSuccessListener(snap -> {

                    cachedNick = String.valueOf(snap.child("nick").getValue());
                    cachedPlace = String.valueOf(snap.child("place").getValue());

                    if ("null".equals(cachedNick)) cachedNick = "";
                    if ("null".equals(cachedPlace)) cachedPlace = "";

                    vm.total.observe(this, totalVal -> {
                        int t = totalVal == null ? 0 : totalVal;

                        vm.doneCount.observe(this, doneVal -> {
                            int d = doneVal == null ? 0 : doneVal;

                            if (cachedNick != null && cachedPlace != null &&
                                    !cachedNick.isEmpty() && !cachedPlace.isEmpty()) {

                                leaderboardRepo.updateUserStats(t, d, cachedNick, cachedPlace);
                            }
                        });
                    });
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (auth != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}
