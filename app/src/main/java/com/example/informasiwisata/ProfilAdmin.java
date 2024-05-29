package com.example.informasiwisata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.informasiwisata.databinding.ActivityProfilAdminBinding;
import com.example.informasiwisata.databinding.ActivityProfileUserBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilAdmin extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private ActivityProfilAdminBinding binding;
    private static final String TAG = "PROFILE_TAG";
    private ImageButton kembali;
    private Button editAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);
        binding = ActivityProfilAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        editAdmin = (Button) findViewById(R.id.btnEditProfil);
        editAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilAdmin.this, edit_admin.class);
                finish();
                startActivity(intent);
            }
        });

        kembali = (ImageButton) findViewById(R.id.btn_backProfil);
        kembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfilAdmin.this, MenuUtama.class);
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
                startActivity(new Intent(ProfilAdmin.this, edit_admin.class));
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

                        binding.NamaAdmin.setText("Halo, "+namaProfil);
                        binding.EmailAdmin.setText(emailProfil);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}