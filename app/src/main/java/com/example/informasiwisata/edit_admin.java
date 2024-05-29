package com.example.informasiwisata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.informasiwisata.databinding.ActivityEditAdminBinding;
import com.example.informasiwisata.databinding.ActivityEditProfileUserBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class edit_admin extends AppCompatActivity {
    private ActivityEditAdminBinding binding;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "PROFIL_EDIT";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu Sebentar Yaa ...");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();

        binding.btback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });

    }
    private String nama ="" ,email ="";
    private void ValidateData() {
        nama = binding.usernameAdmin.getText().toString().trim();
        email = binding.emailAdmin.getText().toString().trim();

        if (TextUtils.isEmpty(nama)){
            Toast.makeText(this, "Masukkan Nama.....", Toast.LENGTH_SHORT).show();
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Masukkan Email....", Toast.LENGTH_SHORT).show();
        }else {

            updateProfil();
            loadUserInfo();
        }
    }

    private void updateProfil(){
        Log.d(TAG, "updateProfil: Mengupdate user profil");
        progressDialog.setMessage("update user profil....");
        progressDialog.show();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("username",""+nama);
        hashMap.put("email",email);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("login");
        databaseReference.child(firebaseAuth.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Profil terupdate....");
                        progressDialog.dismiss();
                        Toast.makeText(edit_admin.this, "Profil terupdate...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: gagal mengubah data di database "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(edit_admin.this, "gagal mengubah data di database "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: Loading User Info..."+firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("login");
        ref.child(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String namaProfil = ""+snapshot.child("username").getValue();
                        String emailProfil = ""+snapshot.child("email").getValue();

                        binding.usernameAdmin.setText(namaProfil);
                        binding.emailAdmin.setText(emailProfil);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}