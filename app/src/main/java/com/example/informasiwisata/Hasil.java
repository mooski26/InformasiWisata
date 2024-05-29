package com.example.informasiwisata;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.informasiwisata.adapter.adapterKomen;
import com.example.informasiwisata.databinding.DialogcommentBinding;
import com.example.informasiwisata.databinding.HasilAlamBinding;
import com.example.informasiwisata.model.Alam;
import com.example.informasiwisata.model.komen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Hasil extends AppCompatActivity {
    private HasilAlamBinding binding;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String TAG = "HASIL_TAG";
    private FirebaseAuth firebaseAuth;
    private ArrayList<komen> commentArrayList;
    private adapterKomen adapterCommentHadist;
    private ProgressDialog progressDialog;

    String Id, wisataId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = HasilAlamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("please wait...");
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        Id = intent.getStringExtra("id");
        wisataId = intent.getStringExtra("id");


//        MyApplication.incrementHadistViewCount(hadistId);
        binding.addCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() == null){
                    Toast.makeText(Hasil.this, "Kamu tidak login....", Toast.LENGTH_SHORT).show();

                }
                else{
                    addCommentDialog();

                }
            }
        });

        loadDetail();
        loadCommentUser();
        
        binding.btcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Hasil.this, WisataAlam.class);
                finish();
                startActivity(intent);
            }
        });

//        String judul = getIntent().getStringExtra("judul");
//        String deskripsi = getIntent().getStringExtra("deksripsi");
//        int gambar = getIntent().getIntExtra("editgambar", 0);

//        img_admin = findViewById(R.id.img_admin);
//        judul_admin = findViewById(R.id.judul_admin);
//        desc_admin = findViewById(R.id.desc_admin);

//        binding.judulAdmin.setText(judul);
//        binding.descAdmin.setText(deskripsi);
//        Glide.with(Hasil.this)
//                        .load(gambar)
//                        .placeholder(R.drawable.ic_baseline_image)
//                        .into(binding.imgAdmin);
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
                    Toast.makeText(Hasil.this, "Masukkan Komentar anda...", Toast.LENGTH_SHORT).show();
                }
                else {
                    alertDialog.dismiss();
                    addComment();
                }
            }
        });
    }

    private void addComment() {
        progressDialog.setMessage("Adding Comment...");
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
                        Toast.makeText(Hasil.this, "Komentar dimasukkan....", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Hasil.this, "Gagal menambahkan komentar "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
                        adapterCommentHadist = new adapterKomen(Hasil.this,commentArrayList);
                        binding.commentRv.setAdapter(adapterCommentHadist);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void loadDetail() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wisata");
        ref.child(wisataId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String judul = ""+snapshot.child("judul").getValue();
                        String deskripsi = ""+snapshot.child("deskripsi").getValue();
                        String gambar = ""+snapshot.child("editgambar").getValue();

                        binding.judulAdmin.setText(judul);
                        binding.descAdmin.setText(deskripsi);
                        try {
                            Glide.with(Hasil.this)
                                    .load(gambar)
                                    .placeholder(R.drawable.ic_baseline_image)
                                    .into(binding.imgAdmin);
                        }catch (Exception e){
                            binding.imgAdmin.setImageResource(R.drawable.ic_baseline_image);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

//        final DocumentReference docRef = db.collection("alams").document("judul");
//        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot snapshot,
//                                @Nullable FirebaseFirestoreException e) {
//                judul = ""+snapshot.getString("judul");
//                String deskripsi = ""+snapshot.getString("deskripsi");
//                String gambar = ""+snapshot.getString("editgambar");
//
//                binding.judulAdmin.setText(judul);
//                binding.descAdmin.setText(deskripsi);
//                Glide.with(Hasil.this)
//                        .load(gambar)
//                        .placeholder(R.drawable.ic_baseline_image)
//                        .into(binding.imgAdmin);
//            }
//        });

}
