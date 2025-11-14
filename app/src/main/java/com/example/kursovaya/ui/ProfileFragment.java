package com.example.kursovaya.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.kursovaya.databinding.FragmentProfileBinding;
import com.example.kursovaya.viewmodel.TaskViewModel;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding b;
    private TaskViewModel vm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        b = FragmentProfileBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        vm = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
        PieChart chart = b.pieChart;
        chart.getDescription().setEnabled(false);
        chart.setUsePercentValues(true);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setTransparentCircleAlpha(0);
        chart.setEntryLabelTextSize(14f);

        vm.done.observe(getViewLifecycleOwner(), done -> updateChart(chart, done.size(), vm));
    }

    private void updateChart(PieChart chart, int doneCount, TaskViewModel vm) {
        vm.total.observe(getViewLifecycleOwner(), total -> {
            int done = doneCount;
            int notDone = Math.max(0, total - done);

            ArrayList<PieEntry> entries = new ArrayList<>();
            entries.add(new PieEntry(done, "Выполнено"));
            entries.add(new PieEntry(notDone, "Не выполнено"));

            PieDataSet set = new PieDataSet(entries, "");
            set.setColors(Color.parseColor("#4CAF50"), Color.parseColor("#F44336"));
            set.setValueTextColor(Color.BLACK);
            set.setValueTextSize(14f);

            chart.setData(new PieData(set));
            chart.invalidate();
        });
    }
}
