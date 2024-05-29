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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.informasiwisata.databinding.ActivityDaftarUserBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;

public class daftar_user extends AppCompatActivity {
    private ActivityDaftarUserBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDaftarUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu Sebentar yaa...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.btcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(daftar_user.this, Login.class);
                startActivity(intent);
            }
        });

        binding.btdaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }

    private String username="",email="",password="";

    private void validateData() {
        username=binding.username.getText().toString().trim();
        email=binding.email.getText().toString().trim();
        password=binding.password.getText().toString().trim();
        String cPassword =binding.confirmPass.getText().toString().trim();

        if (TextUtils.isEmpty(username)){
            Toast.makeText(this,"Masukkan Nama...", Toast.LENGTH_SHORT).show();
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Email tidak sesuai...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Masukkan Password...", Toast.LENGTH_SHORT).show();
        }else  if (TextUtils.isEmpty(cPassword)){
            Toast.makeText(this,"Masukkan Konfirmasi Password...", Toast.LENGTH_SHORT).show();
        }else if (!password.equals(cPassword)){
            Toast.makeText(this,"Password tidak sama...", Toast.LENGTH_SHORT).show();
        }else{
            createUserAccount();
        }

    }

    private void createUserAccount() {
        progressDialog.setMessage("Sedang Membuat Akun .....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        updateUserInfo();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(daftar_user.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Menyimpan Data User.....");

        long timestamp = System.currentTimeMillis();

        String uid = firebaseAuth.getUid();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("uid",uid);
        hashMap.put("email",email);
        hashMap.put("username",username);
        hashMap.put("password", password);
        hashMap.put("tipeuser","user");
        hashMap.put("timestamp",timestamp);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("login");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(daftar_user.this,"Akun telah Dibuat...",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(daftar_user.this,Login.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(daftar_user.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}