package com.example.kursovaya.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kursovaya.R;
import com.example.kursovaya.data.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.VH> {

    private final List<Task> data = new ArrayList<>();
    private final SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
    private String mode = "done";

    public void submit(List<Task> items, String mode){
        this.mode = mode;
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int position) {
        Task t = data.get(position);
        h.title.setText(t.title);
        h.due.setText("Срок: " + fmt.format(t.dueAtMillis));
        if ("done".equals(mode)) {
            h.status.setText("Выполнено");
            h.status.setTextColor(0xFFBDBDBD);
        } else {
            h.status.setText("Рецидив");
            h.status.setTextColor(0xFFE53935);
        }
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder{
        TextView title, due, status;
        VH(@NonNull View v){
            super(v);
            title = v.findViewById(R.id.title);
            due = v.findViewById(R.id.due);
            status = v.findViewById(R.id.status);
        }
    }
}
