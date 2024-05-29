package com.example.informasiwisata;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.informasiwisata.databinding.AktivityMenuUtamaBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MenuUtama extends AppCompatActivity {
    Button button1, button2, button3, button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktivity_menu_utama);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);

        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuUtama.this, EditAlam.class);
                finish();
                startActivity(intent);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuUtama.this, WisataAlam.class);
                finish();
                startActivity(intent);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuUtama.this, ProfilAdmin.class);
                finish();
                startActivity(intent);
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MenuUtama.this, Login.class));

                //membuat method tombol keluar dari aplikasi

            }
        });
    }
}
