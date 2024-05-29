package com.example.informasiwisata;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.informasiwisata.adapter.AlamAdapter;
import com.example.informasiwisata.adapter.RecyclerViewinterface;
import com.example.informasiwisata.databinding.AktivityWisataAlamBinding;
import com.example.informasiwisata.model.Alam;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class WisataAlam extends AppCompatActivity {
    private AktivityWisataAlamBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Alam> list = new ArrayList<>();
    private AlamAdapter alamAdapter;
    private ProgressDialog progressDialog;
    private TextView txtoption;
    private String judulId;
    private RecyclerView recyclerView;
    private static final String TAG ="WISATA_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = AktivityWisataAlamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.rv_alam);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();
        
        loadListWisata();

//        Intent intent = getIntent();
//        judulId = intent.getStringExtra("judul");

        binding.btkembali.setOnClickListener(v -> {
            startActivity(new Intent(WisataAlam.this, MenuUtama.class));
        });
//
//        txtoption = findViewById(R.id.option);
//
        progressDialog = new ProgressDialog(WisataAlam.this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Tunggu Sebentar Yaa...");
//
//        alamAdapter = new AlamAdapter(getApplicationContext(), list, this);
//
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
//                LinearLayoutManager.VERTICAL,false);
//        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(),
//                DividerItemDecoration.VERTICAL);
//        binding.rvAlam.setLayoutManager(layoutManager);
//        binding.rvAlam.addItemDecoration(decoration);
//        binding.rvAlam.setAdapter(alamAdapter);
//
//        progressDialog.show();
//        db.collection("alams")
//                .orderBy("judul")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @SuppressLint("NotifyDataSetChanged")
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        list.clear();
//                        if (task.isSuccessful()){
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Alam alam = new Alam(document.getString("judul"), document.getString("deskripsi"), document.getString("editgambar"));
//                                list.add(alam);
//                            }
//                            alamAdapter.notifyDataSetChanged();
//                        }else {
//                            Toast.makeText(getApplicationContext(),"Data gagal diambil!",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                       progressDialog.dismiss();
//                    }
//                });
//
//    }
//
//    @Override
//    public void onItemClick(int position) {
//        Intent intent = new Intent(WisataAlam.this, Hasil.class);
//        intent.putExtra("editgambar", list.get(position).getGambar());
//        intent.putExtra("judul", list.get(position).getJudul());
//        intent.putExtra("deskripsi", list.get(position).getDeskripsi());
//
//        startActivity(intent);
//    }
//
//    @Override
//    public void onItemLongClick(int position) {
//        list.remove(position);
//        alamAdapter.notifyItemRemoved(position);
//    }
    }

    private void loadListWisata() {
        list = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wisata");
        ref.orderByChild("judul")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            Log.d(TAG, "onDataChange: masalah disini");
                            Alam model = dataSnapshot.getValue(Alam.class);
                            Log.d(TAG, "onDataChange: masalah LEWAT 1");
                            list.add(model);
                            Log.d(TAG, "onDataChange: masalah LEWAT 2");

                        }
                        Log.d(TAG, "onDataChange: masalah LEWAT 3");
                        alamAdapter = new AlamAdapter(WisataAlam.this,list);
                        binding.rvAlam.setAdapter(alamAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Log.d(TAG, "onCancelled: "+error.getMessage());
                    }
                });
    }
}