package com.harry.joker.muilte;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void twoMuilte(View v) {
        startActivity(new Intent(this, TwoMuilteActivity.class));
    }

    public void threeMuilte(View v) {
        startActivity(new Intent(this, ThreeMuilteActivity.class));
    }

    public void fourMuilte(View v) {
        startActivity(new Intent(this, FourMuilteActivity.class));
    }
}
