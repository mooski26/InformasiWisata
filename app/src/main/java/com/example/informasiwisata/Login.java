package com.example.informasiwisata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.example.informasiwisata.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    TextInputEditText etLogNama, etLogPassword;
    Button btnLogRegister, btnLogLupaPassword, btnLogLogin;
    String LogNama,LogPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth=FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu Sebentar yaa...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,daftar_user.class));
            }
        });
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

        binding.keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                moveTaskToBack(true);
            }
        });

    }

    private String email="",password="";
    private void validateData() {
        email=binding.email.getText().toString().trim();
        password=binding.password.getText().toString().trim();

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this,"Email tidak sesuai...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Masukkan Password...", Toast.LENGTH_SHORT).show();
        }else{
            loginUser();
        }

    }

    private void loginUser() {
        progressDialog.setMessage("Proses Masuk ....");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        progressDialog.setMessage("Memeriksa User....");
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("login");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        String tipeuser= ""+snapshot.child("tipeuser").getValue();

                        if (tipeuser.equals("user")){
                            startActivity(new Intent(Login.this,menu_user.class));
                            finish();
                        }else if (tipeuser.equals("admin")){
                            startActivity(new Intent(Login.this,MenuUtama.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

}