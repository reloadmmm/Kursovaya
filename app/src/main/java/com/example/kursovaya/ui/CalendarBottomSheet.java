package com.example.kursovaya.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.kursovaya.R;
import com.example.kursovaya.data.Task;
import com.example.kursovaya.viewmodel.TaskViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarBottomSheet extends BottomSheetDialogFragment {

    private TaskViewModel vm;
    private TaskAdapter adapter;
    private long selectedDayStart;
    private long selectedDayEnd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottomsheet_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentActivity act = requireActivity();
        vm = new ViewModelProvider(act).get(TaskViewModel.class);

        CalendarView calendarView = view.findViewById(R.id.calendarView);
        RecyclerView rv = view.findViewById(R.id.rvCalTasks);
        MaterialButton btnAdd = view.findViewById(R.id.btnAddFromCalendar);

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TaskAdapter(
                t -> vm.markDone(t),
                t -> vm.markRelapse(t)
        );
        rv.setAdapter(adapter);

        setDayRange(System.currentTimeMillis());
        observeDay();

        calendarView.setOnDateChangeListener((cv, year, month, dayOfMonth) -> {
            Calendar c = Calendar.getInstance();
            c.set(year, month, dayOfMonth, 0, 0, 0);
            long millis = c.getTimeInMillis();
            setDayRange(millis);
            observeDay();
        });

        btnAdd.setOnClickListener(v -> {
            new AddTaskDialog().show(requireActivity().getSupportFragmentManager(), "add_task");
        });
    }

    private void setDayRange(long anyMillisInDay) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(anyMillisInDay);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        selectedDayStart = c.getTimeInMillis();
        c.add(Calendar.DAY_OF_MONTH, 1);
        selectedDayEnd = c.getTimeInMillis() - 1;
    }

    private void observeDay() {
        vm.tasksForDay(selectedDayStart, selectedDayEnd)
                .observe(getViewLifecycleOwner(), tasks -> {
                    if (tasks == null) return;
                    adapter.submit(tasks);
                });
    }
}
