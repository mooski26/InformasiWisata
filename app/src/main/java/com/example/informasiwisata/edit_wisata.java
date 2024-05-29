package com.example.informasiwisata;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.informasiwisata.databinding.ActivityEditWisataBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class edit_wisata extends AppCompatActivity {
    private ActivityEditWisataBinding binding;
    private FirebaseAuth firebaseAuth;
    private  String wisataId;
    private ProgressDialog progressDialog;
    private ArrayList<String> wisataArrayList;
    private static final int GALERY_PICK = 1000;
    private Uri imageWisataUri = null;
    private static final int Gallery_code = 1;
    private static final String TAG = "WISATA_EDIT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditWisataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wisataId = getIntent().getStringExtra("id");

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu sebentar Ya ...");
        progressDialog.setCanceledOnTouchOutside(false);

        loadWisataInfo();

        binding.btcancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(edit_wisata.this, WisataAlam.class);
                finish();
                startActivity(intent);
            }
        });
        binding.btsaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasiData();

            }
        });
        binding.imageEdit.setOnClickListener(v -> {
            selectImage();
        });
    }

    private String judulWisata="",isiWisata="";
    private void validasiData() {
        judulWisata = binding.judulEdit.getText().toString().trim();
        isiWisata = binding.deskripsiEdit.getText().toString().trim();

        if(TextUtils.isEmpty(judulWisata)){
            Toast.makeText(this, "Input Judul Wisata...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(isiWisata)){
            Toast.makeText(this, "Input Deskripsi Wisata...", Toast.LENGTH_SHORT).show();
        }else{
            if (imageWisataUri==null){
                updateWisata("");
            }else {
                uploadImageWisata();
            }

        }

    }

    private void uploadImageWisata() {
        Log.d(TAG, "uploadImageWisata: Upload Foto Wisata.....");
        progressDialog.setMessage("Mengupdate Foto Wisata...");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        String filePathAndName = "ImageWisata/"+timestamp;
        StorageReference ref = FirebaseStorage.getInstance().getReference(filePathAndName);
        ref.putFile(imageWisataUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: Profil foto suksess diupload");
                        Log.d(TAG, "onSuccess: mendapatkan url dari upload foto");
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String imageUrl = ""+uriTask.getResult();
                        Log.d(TAG, "onSuccess: upload Image Url");
                        updateWisata(imageUrl);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: gagal mengupload gambar "+e.getMessage());
                        Toast.makeText(edit_wisata.this, "gagal mengupload gambar"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
//    private void cleardata() {
//        binding.imageEdit.setImageResource(R.drawable.ic_baseline_image);
//        binding.judulEdit.setText("");
//        binding.deskripsiEdit.setText("");
//    }

    private void updateWisata(String url) {
        Log.d(TAG, "updateWisata: Memulai mengupdate....");

        progressDialog.setMessage("update info...");
        progressDialog.show();

        HashMap<String, Object> alam = new HashMap<>();
        alam.put("judul", ""+judulWisata);
        alam.put("deskripsi", ""+isiWisata);

        if (imageWisataUri !=null){
            alam.put("url",""+url);
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wisata");
        ref.child(wisataId)
                .updateChildren(alam)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: Wisata terupdate....");
                        progressDialog.dismiss();
                        Toast.makeText(edit_wisata.this, "Wisata terupdate...", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: gagal mengupdate "+e.getMessage());
                        progressDialog.dismiss();
                        Toast.makeText(edit_wisata.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }

    private void loadWisataInfo() {
        Log.d(TAG, "loadWisataInfo: Memuat info Wisata.....");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wisata");
        ref.child(wisataId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String judulWisata = ""+snapshot.child("judul").getValue();
                        String isiWisata = ""+snapshot.child("deskripsi").getValue();
                        String gambarWisata = ""+snapshot.child("editgambar").getValue();

                        binding.judulEdit.setText(judulWisata);
                        binding.deskripsiEdit.setText(isiWisata);
                        Glide.with(edit_wisata.this)
                                .load(gambarWisata)
                                .placeholder(R.drawable.ic_baseline_image)
                                .into(binding.imageEdit);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void selectImage(){
        Log.d(TAG, "fotoPickIntent: memilih foto..");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/wisata/*");
        startActivityForResult(Intent.createChooser(intent,"Silahkan pilih Foto"),GALERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == GALERY_PICK){
                Log.d(TAG, "onActivityResult: memilih foto Wisata");
                imageWisataUri = data.getData();
                binding.imageEdit.setImageURI(imageWisataUri);
                Log.d(TAG, "onActivityResult: URI: "+imageWisataUri);
            }
        }else {
            Log.d(TAG, "onActivityResult: membatalkan memilih foto");
            Toast.makeText(this, "Membatalkan memilih foto", Toast.LENGTH_SHORT).show();
        }

    }
//        final CharSequence[] items = {"Take Photo", "Choose form library", "Cancel"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(edit_wisata.this);
//        builder.setTitle(R.string.app_name);
//        builder.setIcon(R.mipmap.ic_launcher);
//        builder.setItems(items, (dialog, item) -> {
//            if(items[item].equals("Take Photo")){
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intent, 10);
//            } else if(items[item].equals("Choose from library")){
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(Intent.createChooser(intent,"Select Image"), 20);
//            } else if(items[item].equals("Cancel")){
//                dialog.dismiss();
//            }
//        });
//        builder.show();
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 20 && resultCode == RESULT_OK && data != null){
//            final Uri path= data.getData();
//            Thread thread = new Thread(() -> {
//                try {
//                    InputStream inputStream = getContentResolver().openInputStream(path);
//                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                    binding.imageEdit.post(() -> {
//                        binding.imageEdit.setImageBitmap(bitmap);
//                    });
//                } catch (IOException e){
//                    e.printStackTrace();
//                }
//            });
//            thread.start();
//        }
//
//        if(requestCode == 10 && resultCode == RESULT_OK) {
//            final Bundle extras = data.getExtras();
//            Thread thread = new Thread(() -> {
//                Bitmap bitmap = (Bitmap) extras.get("data");
//                binding.imageEdit.post(() -> {
//                    binding.imageEdit.setImageBitmap(bitmap);
//                });
//            });
//            thread.start();
//        }
//    }

//    private void upload(String judul, String deskripsi){
//        Log.d(TAG, "uploadWisata: Mengupload ke Database...");
//
//        progressDialog.setMessage("mengupload Wisata....");
//        progressDialog.show();
//
//        long timestamp = System.currentTimeMillis();
//
//        binding.imageEdit.setDrawingCacheEnabled(true);
//        binding.imageEdit.buildDrawingCache();
//        Bitmap bitmap = ((BitmapDrawable) binding.imageEdit.getDrawable()).getBitmap();
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] data = baos.toByteArray();
//
//        //UPLOAD
//        FirebaseStorage storage = FirebaseStorage.getInstance();
//        StorageReference reference = storage.getReference("images_wisata").child("IMG"+new Date().getTime()+".jpeg");
//        UploadTask uploadTask = reference.putBytes(data);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
//
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                if(taskSnapshot.getMetadata()!=null){
//                    if(taskSnapshot.getMetadata().getReference()!=null){
//                        taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Uri> task) {
//                                if (task.getResult()!=null){
//                                    saveData(timestamp);
//                                } else{
//                                    progressDialog.dismiss();
//                                    Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                    } else{
//                        progressDialog.dismiss();
//                        Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
//                    }
//                } else{
//                    progressDialog.dismiss();
//                    Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

//    private void saveData(long timestamp) {
//        Log.d(TAG, "uplpadData: Upload to firebase...");
//        progressDialog.setMessage("Mengupload Data....");
//        String uid = firebaseAuth.getUid();
//
//        Map<String, Object> alam = new HashMap<>();
//        alam.put("uid",""+uid);
//        alam.put("id",""+timestamp);
//        alam.put("judul", judulWisata);
//        alam.put("deskripsi", isiWisata);
//        alam.put("editgambar", gambarWisata);
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wisata");
//        ref.child(""+timestamp)
//                .setValue(alam)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void unused) {
//                        progressDialog.dismiss();
//                        Log.d(TAG, "onSuccess: Sukses diupload ke db");
//                        Toast.makeText(edit_wisata.this, "Berhasil diupload..", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(edit_wisata.this,MenuUtama.class));
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        progressDialog.dismiss();
//                        Log.d(TAG, "onFailure: Failed Upload ke db "+e.getMessage());
//                        Toast.makeText(edit_wisata.this, "Gagal mengupload ke db"+e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

}