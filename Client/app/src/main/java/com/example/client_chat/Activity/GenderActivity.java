package com.example.client_chat.Activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.airbnb.lottie.LottieAnimationView;
import com.example.client_chat.R;

public class GenderActivity extends AppCompatActivity {
    LottieAnimationView man, woman;
    AppCompatButton next;
    int gioitinh;
    RadioGroup rdogroup;
    RadioButton rdonam, rdonu;
    LinearLayout line_nam, linear_nu;
    boolean with = false;

    public GenderActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gender);
        Mirror();
        Intent intent = getIntent();
        String a = intent.getStringExtra("name");
        rdogroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.rdinam) {
                gioitinh = 1;
                line_nam.setBackgroundResource(R.drawable.mbackground_black);
                linear_nu.setBackgroundResource(R.drawable.mbackground);
            } else {
                gioitinh = 0;
                linear_nu.setBackgroundResource(R.drawable.mbackground_black);
                line_nam.setBackgroundResource(R.drawable.mbackground);
            }
        });
        next.setOnClickListener(view -> {
            if (rdonam.isChecked()) {
                gioitinh = 1;
                Intent intent1 = new Intent(GenderActivity.this, ChooseActivity.class);
                intent1.putExtra("username", a);
                intent1.putExtra("gender", gioitinh);
                Bundle c = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                startActivity(intent1, c);
                finish();
            } else if (rdonu.isChecked()) {
                gioitinh = 0;
                Intent intent1 = new Intent(GenderActivity.this, ChooseActivity.class);
                intent1.putExtra("username", a);
                intent1.putExtra("gender", gioitinh);
                Bundle c = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
                startActivity(intent1, c);
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "You need to fill in your gender information !!", Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    public void Mirror() {
        line_nam = findViewById(R.id.line_nam);
        linear_nu = findViewById(R.id.linear_nu);
        man = findViewById(R.id.man);
        woman = findViewById(R.id.woman);
        next = findViewById(R.id.next);
        rdonam = findViewById(R.id.rdinam);
        rdonu = findViewById(R.id.rdinu);
        rdogroup = findViewById(R.id.rdogroup);
    }
}