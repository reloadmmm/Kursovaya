package com.example.kursovaya.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kursovaya.databinding.FragmentHomeBinding;
import com.example.kursovaya.viewmodel.TaskViewModel;

import java.util.Calendar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding b;
    private TaskViewModel vm;
    private TaskAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentHomeBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

        adapter = new TaskAdapter(
                t -> vm.markDone(t),
                t -> vm.markRelapse(t)
        );
        b.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        b.recycler.setAdapter(adapter);

        View.OnClickListener addClick = v ->
                new AddTaskDialog().show(getParentFragmentManager(), "add_task");

        b.fabAddBig.setOnClickListener(addClick);
        b.btnQuickAdd.setOnClickListener(addClick);

        b.btnCalendar.setOnClickListener(v -> {
            CalendarBottomSheet sheet = new CalendarBottomSheet();
            sheet.show(requireActivity().getSupportFragmentManager(), "calendar");
        });

        b.btnQuickCalendar.setOnClickListener(v -> openCalendar());

        vm.pending.observe(getViewLifecycleOwner(), tasks -> {
            adapter.submit(tasks);

            boolean empty = tasks == null || tasks.isEmpty();

            b.emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
            b.recycler.setVisibility(empty ? View.GONE : View.VISIBLE);
            b.headerPanel.setVisibility(empty ? View.VISIBLE : View.GONE);

            if (empty) {
                b.quickPanel.setVisibility(View.GONE);
            }

            updateHint(empty, tasks == null ? 0 : tasks.size());
        });
    }

    private void openCalendar() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void updateHint(boolean empty, int count) {
        if (empty) {
            b.tvHint.setVisibility(View.GONE);
            return;
        }

        String text;
        if (count == 1) {
            text = "Сегодня всего одна цель — сделай её идеально.";
        } else if (count <= 3) {
            text = "Хороший темп: " + count + " цели на сегодня 🚀";
        } else {
            text = "Много дел (" + count + "). начни с самой простой.";
        }

        b.tvHint.setText(text);
        b.tvHint.setVisibility(View.VISIBLE);
    }

    public void toggleQuickPanel() {
        if (b.quickPanel.getVisibility() == View.GONE) {

            b.quickPanel.setVisibility(View.VISIBLE);
            b.quickPanel.setAlpha(0f);

            float offset = getResources().getDisplayMetrics().density * 13f;
            b.quickPanel.setTranslationY(-offset);

            b.quickPanel.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(220)
                    .start();

        } else {
            b.quickPanel.animate()
                    .alpha(0f)
                    .translationY(-20f)
                    .setDuration(180)
                    .withEndAction(() -> b.quickPanel.setVisibility(View.GONE))
                    .start();
        }
    }

}
