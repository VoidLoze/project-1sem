package com.example.projet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity2 extends AppCompatActivity {
    private Button avokado;
    private Button apple;
    private Button limon;
    private Button banana;
    private Button pear;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        avokado = findViewById(R.id.avokado);
        apple = findViewById(R.id.apple);
        limon = findViewById(R.id.limon);
        banana = findViewById(R.id.banan);


        avokado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MainActivity2.this, avokado.class);
                startActivities(new Intent[]{intent});
            }
        });

        apple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MainActivity2.this, apple.class);
                startActivities(new Intent[]{intent});
            }
        });

        limon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MainActivity2.this, Limon.class);
                startActivities(new Intent[]{intent});
            }
        });

        banana.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MainActivity2.this, Banana.class);
                startActivities(new Intent[]{intent});
            }
        });

    }
}