package com.example.informasiwisata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.informasiwisata.databinding.ActivityProfileUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profile_user extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private ActivityProfileUserBinding binding;
    private static final String TAG = "PROFILE_TAG";
    private ImageButton kembali;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        binding = ActivityProfileUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        kembali = (ImageButton) findViewById(R.id.btnback);
        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(profile_user.this, menu_user.class);
                finish();
                startActivity(intent);
            }
        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        loadUserInfo();

        binding.btnEditProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profile_user.this,EditProfileUser.class));
            }
        });
        binding.btOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(profile_user.this, Login.class));
            }
        });

    }

    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Loading User Info..."+authProfile.getUid());

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("login");
        referenceProfile.child(authProfile.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String namaProfil = ""+snapshot.child("username").getValue();
                        String emailProfil = ""+snapshot.child("email").getValue();

                        binding.showWelcome.setText("Halo, "+namaProfil);
                        binding.showUsername.setText(namaProfil);
                        binding.showEmail.setText(emailProfil);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}