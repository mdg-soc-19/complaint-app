package com.example.android.complaintapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button manager = (Button) findViewById(R.id.manager_text_view);
        Button student = (Button) findViewById(R.id.student_text_view);

        manager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent managerIntent = new Intent(MainActivity.this,MainActivity1.class);

                startActivity(managerIntent);
            }
        });

        student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent studentIntent = new Intent(MainActivity.this,MainActivity2.class);
                startActivity(studentIntent);
            }
        });


    }
}
