package com.example.informasiwisata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.informasiwisata.adapter.adapterKomen;
import com.example.informasiwisata.databinding.DialogcommentBinding;
import com.example.informasiwisata.databinding.ActivityHasilUserBinding;
import com.example.informasiwisata.databinding.DialogcommentBinding;
import com.example.informasiwisata.databinding.HasilAlamBinding;
import com.example.informasiwisata.model.komen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class hasil_user extends AppCompatActivity {
    private ActivityHasilUserBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private ArrayList<komen> commentArrayList;
    private adapterKomen adapterCommentHadist;
    private static final String TAG = "HASIL_TAG";

    String Id, userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHasilUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btcancelUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        String juduls = getIntent().getStringExtra("judul");
//        String deskripsis = getIntent().getStringExtra("deksripsi");
//        int gambars = getIntent().getIntExtra("editgambar", 0);
//
//        binding.judulUser.setText(juduls);
//        binding.descUser.setText(deskripsis);
//        binding.imgUser.setImageResource(gambars);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu Sebentar Ya...");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        Id = intent.getStringExtra("id");
        userId = intent.getStringExtra("id");

        binding.addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() == null){
                    Toast.makeText(hasil_user.this, "Kamu tidak login....", Toast.LENGTH_SHORT).show();

                }
                else{
                    addCommentDialog();

                }
            }
        });

        loadHasil();
        loadCommentUser();
    }

    private void loadCommentUser() {
        commentArrayList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wisata");
        ref.child(Id).child("comments")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        commentArrayList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()){
                            komen model = ds.getValue(komen.class);
                            commentArrayList.add(model);
                        }
                        adapterCommentHadist = new adapterKomen(hasil_user.this,commentArrayList);
                        binding.commentRv.setAdapter(adapterCommentHadist);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private String comment="";
    private void addCommentDialog() {
        DialogcommentBinding commentbinding = DialogcommentBinding.inflate(LayoutInflater.from(this));

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.CostumDialog);
        builder.setView(commentbinding.getRoot());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        commentbinding.btbackcoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        commentbinding.btComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment = commentbinding.commentEt.getText().toString().trim();

                if (TextUtils.isEmpty(comment)){
                    Toast.makeText(hasil_user.this, "Masukkan Komentar anda...", Toast.LENGTH_SHORT).show();
                }
                else {
                    alertDialog.dismiss();
                    addComment();
                }
            }
        });

    }

    private void addComment() {
        progressDialog.setMessage("Menambah Komentar...");
        progressDialog.show();

        String timestamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id",""+Id);
        hashMap.put("timestamp", ""+timestamp);
        hashMap.put("comment", ""+comment);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wisata");
        ref.child(Id).child("comments").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(hasil_user.this, "Komentar dimasukkan....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(hasil_user.this, "Gagal menambahkan komentar "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadHasil() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wisata");
        ref.child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String judul = ""+snapshot.child("judul").getValue();
                        String deskripsi = ""+snapshot.child("deskripsi").getValue();
                        String gambar = ""+snapshot.child("editgambar").getValue();

                        binding.judulUser.setText(judul);
                        binding.descUser.setText(deskripsi);
                        Glide.with(hasil_user.this)
                                .load(gambar)
                                .placeholder(R.drawable.ic_baseline_image)
                                .into(binding.imgUser);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}