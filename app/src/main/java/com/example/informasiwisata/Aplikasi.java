package com.example.informasiwisata;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Calendar;
import java.util.Locale;

public class Aplikasi extends Application {
    private static final String TAG_DOWNLOAD = "DOWNLOAD_TAG";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public  static final String formatTimestamp1(long timestamp) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(timestamp);
        String date = DateFormat.format("yyyy-MM-dd 'Pukul' HH:mm", cal).toString();

        return date;
    }

    public static void deleteWisataDialog(Context context, String judulId, String deskripsiId, String gambarId) {
        String TAG = "DELETE_WISATA_TAG";


        Log.d(TAG, "deleteWisata: Menghapus...");
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Please Wait..");
        progressDialog.setMessage("Menghapus"+judulId+"....");
        progressDialog.show();

        Log.d(TAG, "deleteWisata: Hapus dari Penyimpanan..");
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(gambarId);
        storageReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Menghapus dari penyimpanan...");

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Wisata");
                        reference.child(judulId)
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "onSuccess: menghapus dari database");
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Wisata telah terhapus....", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: gagal menghapus dari database"+e.getMessage());
                                        progressDialog.dismiss();
                                        Toast.makeText(context, "Gagal menghapus dari database"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Gagal Menhapus dari penyimpanan.."+e.getMessage());
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
