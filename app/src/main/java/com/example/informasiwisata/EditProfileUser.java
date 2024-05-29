package com.example.informasiwisata;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.informasiwisata.databinding.ActivityEditProfileUserBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditProfileUser extends AppCompatActivity {

    private ActivityEditProfileUserBinding binding;
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "PROFIL_EDIT";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu Sebentar Ya...");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();
        loadUserInfo();

        binding.btcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.btEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateData();
            }
        });

    }
    private String nama ="" ,email ="";
    private void ValidateData() {
        nama = binding.Editusername.getText().toString().trim();
        email = binding.Editemail.getText().toString().trim();

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
                        Toast.makeText(EditProfileUser.this, "Profil terupdate...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: gagal mengubah data di database "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileUser.this, "gagal mengubah data di database "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

                        binding.Editusername.setText(namaProfil);
                        binding.Editemail.setText(emailProfil);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}