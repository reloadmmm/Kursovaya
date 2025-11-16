package com.example.kursovaya.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kursovaya.R;
import com.example.kursovaya.data.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class AuthActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseDatabase db;

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        RadioGroup tabs = findViewById(R.id.tabs);
        View pUp = findViewById(R.id.pSignUp);
        View pIn = findViewById(R.id.pSignIn);
        tabs.setOnCheckedChangeListener((g, id) -> {
            pUp.setVisibility(id==R.id.tabSignUp?View.VISIBLE:View.GONE);
            pIn.setVisibility(id==R.id.tabSignIn?View.VISIBLE:View.GONE);
        });

        EditText etNick  = findViewById(R.id.etNick);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPass  = findViewById(R.id.etPass);
        EditText etAge   = findViewById(R.id.etAge);
        EditText etPlace = findViewById(R.id.etPlace);
        RadioGroup rgType = findViewById(R.id.rgType);

        findViewById(R.id.btnSignUp).setOnClickListener(v -> {
            String nick = etNick.getText().toString().trim();
            String email= etEmail.getText().toString().trim();
            String pass = etPass.getText().toString().trim();
            int age = safeInt(etAge.getText().toString(), 0);
            String type = rgType.getCheckedRadioButtonId()==R.id.rbWork? "work":"study";
            String place = etPlace.getText().toString().trim();
            if (nick.isEmpty() || email.isEmpty() || pass.length()<6) {
                toast("Заполни ник / почту / пароль (мин. 6)"); return;
            }
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(t -> {
                if (t.isSuccessful()) {
                    String uid = auth.getCurrentUser().getUid();
                    UserProfile p = new UserProfile(nick, email, age, type, place);
                    db.getReference("users").child(uid).setValue(p);
                    toast("Готово!"); finish();
                } else toast("Ошибка: "+t.getException().getMessage());
            });
        });


        EditText etEmailIn = findViewById(R.id.etEmailIn);
        EditText etPassIn  = findViewById(R.id.etPassIn);
        findViewById(R.id.btnSignIn).setOnClickListener(v -> {
            String email = etEmailIn.getText().toString().trim();
            String pass  = etPassIn.getText().toString().trim();
            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(t -> {
                if (t.isSuccessful()){ toast("Вошли"); finish(); }
                else toast("Ошибка: "+t.getException().getMessage());
            });
        });
    }

    private void toast(String s){ Toast.makeText(this, s, Toast.LENGTH_SHORT).show(); }
    private int safeInt(String s, int d){ try { return Integer.parseInt(s);} catch(Exception e){ return d; } }
}
