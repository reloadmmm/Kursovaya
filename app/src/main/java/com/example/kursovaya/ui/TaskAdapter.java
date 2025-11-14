package com.example.kursovaya.ui;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kursovaya.R;
import com.example.kursovaya.data.Task;

import java.text.SimpleDateFormat;
import java.util.*;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.VH> {
    public interface OnAction { void run(Task t); }
    private final List<Task> data = new ArrayList<>();
    private final OnAction onDone, onRelapse;
    private final SimpleDateFormat fmt = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());

    public TaskAdapter(OnAction onDone, OnAction onRelapse){
        this.onDone = onDone; this.onRelapse = onRelapse;
    }

    public void submit(List<Task> items){
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos){
        Task t = data.get(pos);
        h.title.setText(t.title);
        h.due.setText("Срок: " + fmt.format(new Date(t.dueAtMillis)));

        long now = System.currentTimeMillis();
        long total = Math.max(t.dueAtMillis - t.createdAt, 1);
        int progress = (int)Math.min(100, Math.max(0, 100 - ((t.dueAtMillis - now) * 100 / total)));
        h.progress.setProgress(progress);

        h.btnDone.setOnClickListener(v -> onDone.run(t));
        h.btnRelapse.setOnClickListener(v -> onRelapse.run(t));
    }

    @Override public int getItemCount(){ return data.size(); }

    static class VH extends RecyclerView.ViewHolder{
        TextView title, due; ProgressBar progress; Button btnDone, btnRelapse;
        VH(View v){
            super(v);
            title = v.findViewById(R.id.title);
            due = v.findViewById(R.id.due);
            progress = v.findViewById(R.id.progress);
            btnDone = v.findViewById(R.id.btnDone);
            btnRelapse = v.findViewById(R.id.btnRelapse);
        }
    }
}
