package com.example.kursovaya.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kursovaya.R;
import com.example.kursovaya.databinding.FragmentProfileBinding;
import com.example.kursovaya.viewmodel.TaskViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding b;
    private TaskViewModel vm;
    private FirebaseAuth auth;

    private SharedPreferences prefs;
    private ImageView ivThemeToggle;

    private String nick = "Гость";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentProfileBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        vm = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE);

        ivThemeToggle = b.ivThemeToggle;
        boolean darkOn = prefs.getBoolean("dark_theme", false);
        updateThemeIcon(darkOn);
        applyTheme(darkOn);

        ivThemeToggle.setOnClickListener(v -> {
            boolean nowDark = !prefs.getBoolean("dark_theme", false);
            prefs.edit().putBoolean("dark_theme", nowDark).apply();
            applyTheme(nowDark);
            updateThemeIcon(nowDark);
        });

        PieChart chart = b.pieChart;
        chart.getDescription().setEnabled(false);
        chart.setUsePercentValues(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(60f);
        chart.setTransparentCircleRadius(65f);
        chart.setCenterTextSize(16f);
        chart.setCenterTextColor(Color.DKGRAY);
        chart.getLegend().setEnabled(false);

        TextView tvName = b.tvUserName;
        TextView tvLevel = b.tvLevel;
        TextView tvXp = b.tvXp;
        Button btnAuth = b.btnAuth;
        Button btnRate = b.btnRate;
        TextView tvFooter = b.tvFooter;

        if (auth.getCurrentUser() == null) {
            tvName.setText("Гость");
            btnAuth.setText("Регистрация / Вход");
            btnRate.setVisibility(View.GONE);
            btnAuth.setOnClickListener(v1 ->
                    startActivity(new Intent(getContext(), AuthActivity.class)));
        } else {
            String uid = auth.getCurrentUser().getUid();
            FirebaseDatabase.getInstance().getReference("users").child(uid)
                    .get().addOnSuccessListener(snap -> {
                        nick = String.valueOf(snap.child("nick").getValue());
                        if (nick == null || nick.equals("null") || nick.isEmpty()) {
                            nick = "Без имени";
                        }
                        tvName.setText(nick);
                    });

            btnAuth.setText("Выйти");
            btnAuth.setOnClickListener(v12 -> {
                auth.signOut();
                tvName.setText("Гость");
                btnRate.setVisibility(View.GONE);
            });

            btnRate.setVisibility(View.VISIBLE);
            btnRate.setOnClickListener(v13 ->
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new LeaderboardFragment())
                            .addToBackStack(null)
                            .commit());
        }

        vm.getDoneCount().observe(getViewLifecycleOwner(), doneCount -> {
            vm.getTotal().observe(getViewLifecycleOwner(), totalCount -> {
                int done = doneCount == null ? 0 : doneCount;
                int total = totalCount == null ? 0 : totalCount;

                int xp = done * 30;
                int level = xp / 100 + 1;

                tvLevel.setText("Уровень " + level);
                tvXp.setText("XP: " + xp);

                updateChart(chart, done, total);
            });
        });

        Button btnDiary = b.btnDiary;

        if (auth.getCurrentUser() == null) {
            btnDiary.setVisibility(View.GONE);
        } else {
            btnDiary.setVisibility(View.VISIBLE);
            btnDiary.setOnClickListener(v ->
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new DiaryFragment())
                            .addToBackStack(null)
                            .commit());
        }


        tvFooter.setText("разработал студент группы ...");
    }

    private void updateChart(PieChart chart, int done, int total) {
        int notDone = Math.max(0, total - done);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(done, "Выполнено"));
        entries.add(new PieEntry(notDone, "Не выполнено"));

        PieDataSet set = new PieDataSet(entries, "");
        set.setColors(Color.parseColor("#4CAF50"), Color.parseColor("#F44336"));
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(13f);

        PieData data = new PieData(set);
        chart.setData(data);

        double percent = total == 0 ? 0 : (done * 100.0 / total);
        chart.setCenterText(String.format(Locale.getDefault(), "%.0f%%", percent));

        chart.invalidate();
    }

    private void applyTheme(boolean dark) {
        if (dark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void updateThemeIcon(boolean dark) {
        if (ivThemeToggle != null) {
            ivThemeToggle.setImageResource(dark ? R.drawable.ic_moon : R.drawable.ic_sun);
        }
    }
}
