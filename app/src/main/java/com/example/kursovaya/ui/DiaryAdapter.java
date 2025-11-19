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

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.Holder> {

    public interface Listener { void onLongClick(DiaryEntry e); }

    private final Listener listener;
    private final List<DiaryEntry> data = new ArrayList<>();
    private final SimpleDateFormat fmt =
            new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public DiaryAdapter(Listener l){ this.listener = l; }

    public void submit(List<DiaryEntry> list){
        data.clear();
        if (list != null) data.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDiaryBinding b = ItemDiaryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new Holder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        DiaryEntry e = data.get(position);
        h.b.tvDate.setText(fmt.format(e.createdAtMillis));
        h.b.tvText.setText(e.text);
        h.itemView.setOnLongClickListener(v -> {
            listener.onLongClick(e);
            return true;
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class Holder extends RecyclerView.ViewHolder {
        ItemDiaryBinding b;
        Holder(ItemDiaryBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
