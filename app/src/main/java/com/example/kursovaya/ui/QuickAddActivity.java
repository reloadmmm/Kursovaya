package com.example.kursovaya.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class QuickAddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AddTaskDialog dialog = new AddTaskDialog();
        dialog.setCancelable(true);
        dialog.show(getSupportFragmentManager(), "quick_add");
    }
}
