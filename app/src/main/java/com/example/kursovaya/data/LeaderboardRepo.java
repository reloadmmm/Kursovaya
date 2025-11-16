package com.example.kursovaya.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.*;

public class LeaderboardRepo {
    private final FirebaseDatabase db = FirebaseDatabase.getInstance();

    public void updateUserStats(int total, int done, String nick, String place){
        if (FirebaseAuth.getInstance().getCurrentUser()==null) return;
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        double rate = total==0 ? 0.0 : (done*100.0/total);

        Map<String,Object> stats = new HashMap<>();
        stats.put("total", total); stats.put("done", done); stats.put("rate", rate);
        db.getReference("users").child(uid).child("stats").setValue(stats);

        Calendar c = Calendar.getInstance();
        String monthly  = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(c.getTime());
        String yearly   = new SimpleDateFormat("yyyy", Locale.getDefault()).format(c.getTime());
        String halfyear = yearly + (c.get(Calendar.MONTH)<6 ? "-H1" : "-H2");

        Map<String,Object> lb = new HashMap<>();
        lb.put("nick", nick); lb.put("done", done); lb.put("rate", rate); lb.put("place", place);

        db.getReference("leaderboard").child("monthly").child(monthly).child(place).child(uid).setValue(lb);
        db.getReference("leaderboard").child("halfyear").child(halfyear).child(place).child(uid).setValue(lb);
        db.getReference("leaderboard").child("yearly").child(yearly).child(place).child(uid).setValue(lb);
    }
}
