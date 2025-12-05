package com.example.kursovaya.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kursovaya.data.DiaryEntry;
import com.example.kursovaya.databinding.ItemDiaryBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.VH> {

    private final List<DiaryEntry> data = new ArrayList<>();
    private final SimpleDateFormat fmt =
            new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public void submit(List<DiaryEntry> list) {
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    public DiaryEntry getItem(int pos) {
        if (pos < 0 || pos >= data.size()) return null;
        return data.get(pos);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(
                ItemDiaryBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        DiaryEntry e = data.get(position);

        holder.b.tvText.setText(e.text);

        holder.b.tvDate.setText(fmt.format(e.createdAtMillis));

        if (e.audioPath != null) {
            holder.b.tvAudio.setVisibility(View.VISIBLE);
        } else {
            holder.b.tvAudio.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ItemDiaryBinding b;

        VH(ItemDiaryBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }
    }
}
