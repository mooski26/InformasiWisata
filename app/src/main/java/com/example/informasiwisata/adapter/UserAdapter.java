package com.example.informasiwisata.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.informasiwisata.Hasil;
import com.example.informasiwisata.R;
import com.example.informasiwisata.databinding.RowAlamBinding;
import com.example.informasiwisata.databinding.RowUserBinding;
import com.example.informasiwisata.hasil_user;
import com.example.informasiwisata.menu_user;
import com.example.informasiwisata.model.Alam;
import com.example.informasiwisata.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private RowUserBinding binding;
    private Context context;
    private List<Alam> list;
    private UserAdapter.Dialog dialog;
    private ProgressDialog progressDialog;
    private static final String TAG = "USER_ADAPTER_TAG";
//    private final RecyclerViewinterface recyclerViewinterface;

    public interface Dialog{
        void onClick(int pos);
    }

    public void setDialog(UserAdapter.Dialog dialog){
        this.dialog = dialog;
    }

    public UserAdapter(Context context, List<Alam> list) {
        this.context = context;
        this.list = list;
//        this.recyclerViewinterface = recyclerViewinterface;
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Tunggu Sebentar Ya ...");
        progressDialog.setCanceledOnTouchOutside(false);
    }


    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = RowUserBinding.inflate(LayoutInflater.from(context),parent,false);

        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        Alam model = list.get(position);
        String userId = model.getId();

        holder.juduls.setText(list.get(position).getJudul());
        holder.deskripsis.setText(list.get(position).getDeskripsi());
        Glide.with(context).load(list.get(position).getEditgambar()).into(holder.gambars);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, hasil_user.class);
                intent.putExtra("id", userId);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView juduls, deskripsis;
        ImageView gambars;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gambars = binding.imgRowUser;
            juduls = binding.judulRowUser;
            deskripsis = binding.desRowUser;

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
        }
    }

}
