package com.example.kursovaya.ui;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.kursovaya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class LeaderboardFragment extends Fragment {
    private RecyclerView list; private Spinner spPeriod;
    private final List<Map<String,Object>> data = new ArrayList<>();
    private Adapter adapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        spPeriod = v.findViewById(R.id.spPeriod);
        list = v.findViewById(R.id.leaderList);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Adapter(); list.setAdapter(adapter);

        spPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) { load(); }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        return v;
    }

    private void load(){
        if (FirebaseAuth.getInstance().getCurrentUser()==null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("users").child(uid).get().addOnSuccessListener(snap -> {
            String place = String.valueOf(snap.child("place").getValue());
            String periodKey; String periodNode;
            Calendar c = Calendar.getInstance();
            int i = spPeriod.getSelectedItemPosition(); // 0: месяц, 1: полгода, 2: год
            if (i==0){ periodNode="monthly"; periodKey=new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(c.getTime()); }
            else if (i==1){ String y = new SimpleDateFormat("yyyy", Locale.getDefault()).format(c.getTime()); periodNode="halfyear"; periodKey=y+(c.get(Calendar.MONTH)<6?"-H1":"-H2"); }
            else { periodNode="yearly"; periodKey=new SimpleDateFormat("yyyy", Locale.getDefault()).format(c.getTime()); }

            DatabaseReference ref = FirebaseDatabase.getInstance()
                    .getReference("leaderboard").child(periodNode).child(periodKey).child(place);

            ref.orderByChild("done").limitToLast(50).get().addOnSuccessListener(s -> {
                data.clear();
                for (DataSnapshot ch : s.getChildren()){
                    Map<String,Object> m = (Map<String, Object>) ch.getValue();
                    if (m!=null) data.add(m);
                }
                Collections.reverse(data);
                adapter.notifyDataSetChanged();
            });
        });
    }

    class Adapter extends RecyclerView.Adapter<VH>{
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leader, parent, false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(@NonNull VH h, int pos) {
            Map<String,Object> m = data.get(pos);
            h.nick.setText(String.valueOf(m.get("nick")));
            h.place.setText(String.valueOf(m.get("place")));
            h.stats.setText("Выполнено: "+safeInt(m.get("done"),0)+" • "+String.format(Locale.getDefault(),"%.0f%%", safeDbl(m.get("rate"),0)));
        }
        @Override public int getItemCount(){ return data.size(); }
    }
    static class VH extends RecyclerView.ViewHolder{
        TextView nick, place, stats;
        VH(@NonNull View v){ super(v);
            nick = v.findViewById(R.id.tvNick);
            place= v.findViewById(R.id.tvPlace);
            stats= v.findViewById(R.id.tvStats);
        }
    }

    private int safeInt(Object o, int d){ try { return Integer.parseInt(String.valueOf(o)); } catch(Exception e){ return d; } }
    private double safeDbl(Object o, double d){ try { return Double.parseDouble(String.valueOf(o)); } catch(Exception e){ return d; } }
}
