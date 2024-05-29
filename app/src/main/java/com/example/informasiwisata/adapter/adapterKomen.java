package com.example.informasiwisata.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.informasiwisata.Aplikasi;
import com.example.informasiwisata.R;
import com.example.informasiwisata.databinding.RowCommentBinding;
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

public class adapterKomen extends RecyclerView.Adapter<adapterKomen.HolderKomen> {
    private Context context;
    private ArrayList<komen> commentArrayList;
    private RowCommentBinding binding;
    private FirebaseAuth firebaseAuth;

    public adapterKomen(Context context, ArrayList<komen> commentArrayList) {
        this.context = context;
        this.commentArrayList = commentArrayList;
        firebaseAuth =FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public HolderKomen onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowCommentBinding.inflate(LayoutInflater.from(context),parent,false);
        return new HolderKomen(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderKomen holder, int position) {
        komen komenUser=commentArrayList.get(position);
        String id = komenUser.getId();
        String hadistId = komenUser.getId();
        String comment = komenUser.getComment();
        String uid = komenUser.getUid();
        String timestamp = komenUser.getTimestamp();

        String date = Aplikasi.formatTimestamp1(Long.parseLong(timestamp));

        holder.dateTv.setText(date);
        holder.commentTv.setText(comment);

        loadUserKomen(komenUser, holder);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (firebaseAuth.getCurrentUser()!=null && uid.equals(firebaseAuth.getUid())){
//                    deleteComment(komenUser,holder);
//                }
//            }
//        });

    }

    private void loadUserKomen(komen komenUser, HolderKomen holder) {
        String uid = komenUser.getUid();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("login");
        reference.child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String nama = ""+ snapshot.child("username").getValue();
//                        String profilImage = ""+snapshot.child("imageprofil").getValue();

                        holder.namaTv.setText(nama);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

//    private void deleteComment(komen komenUser, HolderKomen holder) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Delete Comment")
//                .setMessage("apakah kamu ingin menghapus pesan ini?")
//                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Wisata");
//                        reference.child(komenUser.getWisataId())
//                                .child("comments")
//                                .child(komenUser.getUid())
//                                .removeValue()
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void unused) {
//                                        Toast.makeText(context, "Delete.....", Toast.LENGTH_SHORT).show();
//                                    }
//                                })
//                                .addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//                                        Toast.makeText(context, "gagal menghapus komentar", Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                    }
//                })
//                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//
//                    }
//                })
//                .show();
//    }

    @Override
    public int getItemCount() {
        return commentArrayList.size();
    }

    public class HolderKomen extends RecyclerView.ViewHolder {
        TextView namaTv, dateTv, commentTv;

        public HolderKomen(@NonNull View itemView) {
            super(itemView);

            namaTv = binding.namaTv;
            dateTv = binding.dateTv;
            commentTv = binding.commentTv;
        }
    }
}
