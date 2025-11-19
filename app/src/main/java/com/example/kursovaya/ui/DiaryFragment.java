package com.example.kursovaya.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.kursovaya.databinding.FragmentDiaryBinding;
import com.example.kursovaya.R;
import com.example.kursovaya.data.DiaryEntry;
import com.example.kursovaya.data.DiaryRepository;
import com.example.kursovaya.ui.DiaryAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class DiaryFragment extends Fragment {

    private FragmentDiaryBinding b;
    private DiaryAdapter adapter;
    private DiaryRepository repo; // или твой ViewModel – подставь своё

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentDiaryBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repo = new DiaryRepository(requireContext()); // если у тебя через ViewModel – замени

        // список
        b.rvDiary.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DiaryAdapter();
        b.rvDiary.setAdapter(adapter);

        loadEntries();

        // FAB – новая запись
        b.fabDiary.setOnClickListener(v -> showAddDialog());
    }

    private void loadEntries() {
        // тут подставь свою загрузку из БД / ViewModel
        List<DiaryEntry> entries = repo.getAll();
        adapter.submit(entries);
        b.tvDiaryEmpty.setVisibility(entries.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void showAddDialog() {
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_diary_entry, null, false);

        TextInputEditText etText = dialogView.findViewById(R.id.etDiaryText);

        new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setNegativeButton("Отмена", (d, w) -> d.dismiss())
                .setPositiveButton("Сохранить", (d, w) -> {
                    String text = etText.getText() == null ? "" : etText.getText().toString().trim();
                    if (!text.isEmpty()) {
                        repo.add(new DiaryEntry(text, System.currentTimeMillis()));
                        loadEntries();
                    }
                })
                .show();
    }
}
