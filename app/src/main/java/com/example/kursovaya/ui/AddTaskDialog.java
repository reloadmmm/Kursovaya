package com.example.kursovaya.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kursovaya.R;
import com.example.kursovaya.data.Task;
import com.example.kursovaya.databinding.DialogAddTaskBinding;
import com.example.kursovaya.viewmodel.TaskViewModel;

import java.util.Calendar;

public class AddTaskDialog extends DialogFragment {

    private DialogAddTaskBinding b;
    private long selectedMillis = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.RoundedDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = DialogAddTaskBinding.inflate(inflater, container, false);

        b.swReward.setOnCheckedChangeListener((btn, checked) -> toggle(b.tilReward, checked));
        b.swNotify.setOnCheckedChangeListener((btn, checked) -> toggle(b.tilInterval, checked));

        toggle(b.tilReward, b.swReward.isChecked());
        toggle(b.tilInterval, b.swNotify.isChecked());

        b.btnDue.setOnClickListener(v -> pickDateTime());

        b.btnSave.setOnClickListener(v -> {
            TaskViewModel vm = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);

            String title = b.etTitle.getText().toString().trim();
            boolean rewardOn = b.swReward.isChecked();
            boolean notifyOn = b.swNotify.isChecked();

            int interval = 0;
            if (notifyOn) {
                String raw = b.etInterval.getText() != null
                        ? b.etInterval.getText().toString().trim()
                        : "";

                if (raw.isEmpty()) {
                    interval = 1;
                } else {
                    try {
                        interval = Integer.parseInt(raw);
                    } catch (Exception e) {
                        interval = 1;
                    }
                }
                if (interval < 1) interval = 1;
            }

            if (title.isEmpty() || selectedMillis <= 0) {
                Toast.makeText(getContext(), "Заполни название и срок", Toast.LENGTH_SHORT).show();
                return;
            }

            Task t = new Task(
                    title,
                    selectedMillis,
                    rewardOn,
                    rewardOn ? String.valueOf(b.etReward.getText()) : null,
                    notifyOn,
                    interval,
                    "pending",
                    System.currentTimeMillis()
            );

            vm.add(t);
            dismiss();

            if (getActivity() instanceof QuickAddActivity) {
                getActivity().finish();
            }
        });

        return b.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int w = (int) (requireContext()
                    .getResources()
                    .getDisplayMetrics()
                    .widthPixels * 0.92f);
            getDialog().getWindow().setLayout(w, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void pickDateTime() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(getContext(), (dp, y, m, d) -> {
            c.set(y, m, d);
            new TimePickerDialog(getContext(), (tp, h, min) -> {
                c.set(Calendar.HOUR_OF_DAY, h);
                c.set(Calendar.MINUTE, min);
                c.set(Calendar.SECOND, 0);
                selectedMillis = c.getTimeInMillis();
                String text = "Срок: " + d + "." + (m + 1) + "." + y +
                        " " + h + ":" + String.format("%02d", min);
                b.btnDue.setText(text);
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void toggle(View v, boolean show) {
        if (show) {
            v.setVisibility(View.VISIBLE);
            v.setAlpha(0f);
            v.animate().alpha(1f).setDuration(150).start();
        } else {
            v.setVisibility(View.GONE);
        }
    }
}
