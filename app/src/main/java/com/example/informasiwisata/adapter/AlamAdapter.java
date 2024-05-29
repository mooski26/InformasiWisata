package com.example.informasiwisata.adapter;

import android.app.AlertDialog;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.informasiwisata.Aplikasi;
import com.example.informasiwisata.EditAlam;
import com.example.informasiwisata.Hasil;
import com.example.informasiwisata.R;
import com.example.informasiwisata.WisataAlam;
import com.example.informasiwisata.databinding.RowAlamBinding;
import com.example.informasiwisata.edit_wisata;
import com.example.informasiwisata.model.Alam;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AlamAdapter extends RecyclerView.Adapter<AlamAdapter.MyViewHolder> {
    private Context context;
    private List<Alam> list;
    private Dialog dialog;
//    private final RecyclerViewinterface recyclerViewinterface;
    private ProgressDialog progressDialog;
    private RowAlamBinding binding;
    private static final String TAG = "ALAM_ADAPTER_TAG";

    public interface Dialog{
        void onClick(int pos);
    }

    public void setDialog(AlamAdapter.Dialog dialog)   {
        this.dialog = dialog;
    }

    public AlamAdapter(Context context, List<Alam> list) {
        this.context = context;
        this.list = list;
//        this.recyclerViewinterface = recyclerViewinterface;
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Tunggu Sebentar Yaa ...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowAlamBinding.inflate(LayoutInflater.from(context),parent,false);

        return new MyViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Alam model = list.get(position);
        String wisataId = model.getId();
//        String judulId = model.getJudul();
//        String deskripsiId = model.getDeskripsi();

//        long timestamp = model.getTimestamp();

        holder.txt_judul.setText(list.get(position).getJudul());
        holder.txt_deskripsi.setText(list.get(position).getDeskripsi());
        Glide.with(context).load(list.get(position).getEditgambar()).into(holder.gambar);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,Hasil.class);
                intent.putExtra("id", wisataId);
                context.startActivity(intent);
            }
        });

        holder.editWisata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(context,edit_wisata.class);
                intent.putExtra("id",wisataId);
                context.startActivity(intent);
//                editWisata(model,holder);
            }
        });
        holder.deleteWisata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete")
                        .setMessage("Apakah anda ingin menghapus Wisata ini?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(context, "Menghapus...", Toast.LENGTH_SHORT).show();
                                deleteWisata(model,holder);
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });
    }

    private void deleteWisata(Alam model, MyViewHolder holder) {
        String wisataId = model.getId();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wisata");
        ref.child(wisataId)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Berhasil Terhapus ...", Toast.LENGTH_SHORT).show();
                        Intent intent  = new Intent(context,WisataAlam.class);
                        context.startActivity(intent);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    private void editWisata(Alam model, MyViewHolder holder) {
//        String wisataId = model.getJudul();
//        String deskripsiId = model.getDeskripsi();
//        String gambarId = model.getEditgambar();
//
//        String[] option = {"Edit","Delete"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Pilih Pengaturan")
//                .setItems(option, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if(which==0){
//                            Intent intent = new Intent(context, edit_wisata.class);
//                            intent.putExtra("id", wisataId);
//                            context.startActivity(intent);
//                        }
//                        else  if (which==1){
//                            Aplikasi.deleteWisataDialog(
//                                    context,
//                                    ""+wisataId,
//                                    ""+deskripsiId,
//                                    ""+gambarId
//                            );
//                            //deleteHadistDialog(model,holder);
//                        }
//
//                    }
//                }).show();
//    }

    @Override
    public int getItemCount() {

        return list.size();

//        void setFilter(List<Alam> setFilter){
//            list =  new List<();
//            list.addAll(setFilter);
//            notifyDataSetChanged();
//        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView gambar, deleteWisata, editWisata;
        TextView txt_judul, txt_deskripsi;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            gambar= binding.imgRowAdmin;
            txt_judul = binding.judulRowAdmin;
            txt_deskripsi = binding.desRowAdmin;
            editWisata = binding.editWisata;
            deleteWisata = binding.deleteWisata;

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (recyclerViewinterface != null){
//                        int pos = getAdapterPosition();
//
//                        if (pos != RecyclerView.NO_POSITION){
//                            recyclerViewinterface.onItemClick(pos);
//                        }
//
//                    }
//                }
//            });
//
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
//                    if (recyclerViewinterface != null){
//                        int pos = getAdapterPosition();
//
//                        if (pos != RecyclerView.NO_POSITION){
//                            recyclerViewinterface.onItemLongClick(pos);
//                        }
//
//                    }
//                    return true;
//                }
//            });
        }

    }
}
