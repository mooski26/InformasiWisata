package com.example.informasiwisata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.informasiwisata.adapter.AlamAdapter;
import com.example.informasiwisata.adapter.RecyclerViewinterface;
import com.example.informasiwisata.adapter.UserAdapter;
import com.example.informasiwisata.databinding.ActivityMenuUserBinding;
import com.example.informasiwisata.databinding.AktivityWisataAlamBinding;
import com.example.informasiwisata.model.Alam;
import com.example.informasiwisata.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class menu_user extends AppCompatActivity {
    private ActivityMenuUserBinding binding;
    private RecyclerView recyclerView;
    private String useriId;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Alam> list = new ArrayList<>();
    private UserAdapter userAdapter;
    private ProgressDialog progressDialog;
    private DatabaseReference reference;
    private TextView userwelcome;
    private String username;
    private FirebaseAuth authProfile;
    private static final String TAG ="USER_LIST_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = findViewById(R.id.list_user);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firebaseAuth = FirebaseAuth.getInstance();

        loadListWisata();

        userwelcome = (TextView) findViewById(R.id.tv_profile);
        userwelcome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(menu_user.this, profile_user.class);
                finish();
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        useriId = intent.getStringExtra("id");

        progressDialog = new ProgressDialog(menu_user.this);
        progressDialog.setTitle("Loading...");
        progressDialog.setMessage("Tunggu Sebentar Yaa ...");

//        userAdapter = new UserAdapter(getApplicationContext(), list, this);
//
//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(),
//                LinearLayoutManager.VERTICAL,false);
//        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(getApplicationContext(),
//                DividerItemDecoration.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(decoration);
//        recyclerView.setAdapter(userAdapter);
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
//                                User user = new User(document.getString("judul"), document.getString("deskripsi"), document.getString("editgambar"));
//                                list.add(user);
//                            }
//                            userAdapter.notifyDataSetChanged();
//                        }else {
//                            Toast.makeText(getApplicationContext(),"Data gagal diambil!",
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                        progressDialog.dismiss();
//                    }
//                });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            /*Toast.makeText(menu_user.this, "Something went wrong !, User details are not available at the moment", Toast.LENGTH_LONG).show();*/

        } else {
            showUserProfile(firebaseUser);
        }
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
                        userAdapter = new UserAdapter(menu_user.this, list);
                        binding.listUser.setAdapter(userAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Log.d(TAG, "onCancelled: "+error.getMessage());
                    }
                });
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();

        reference = FirebaseDatabase.getInstance().getReference("login");
        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String namaProfil = ""+snapshot.child("username").getValue();

                binding.tvProfile.setText("Halo, "+namaProfil);
                binding.tvProfile.setText(namaProfil);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    //    @Override
//    public void onItemClick(int position) {
//        Intent intent = new Intent(menu_user.this, hasil_user.class);
//        intent.putExtra("editgambar", list.get(position).getGambars());
//        intent.putExtra("judul", list.get(position).getJuduls());
//        intent.putExtra("deskripsi", list.get(position).getDeskripsis());
//
//        startActivity(intent);
//    }
//
//    @Override
//    public void onItemLongClick(int position) {
//
//    }

}