package com.example.informasiwisata;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.informasiwisata.databinding.AktivityEditAlamBinding;
import com.example.informasiwisata.model.Alam;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
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

public class EditAlam extends AppCompatActivity {
    private AktivityEditAlamBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog progressDialog;
    private static final String TAG = "ADD_WISATA_TAG";
    private static final int GALERY_PICK = 1000;
    private Uri imageWisataUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = AktivityEditAlamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        /*Alam emp_edit = (Alam)getIntent().getSerializableExtra("EDIT");
        if (emp_edit != null){
            btsave.setText("UPDATE");
            editgambar.setImageResource(emp_edit.getGambar());
            editjudul.setText(emp_edit.getJudul());
            editdeskripsi.setText(emp_edit.getDeskripsi());

        } else {
            btsave.setText("SUBMIT");
        }*/

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tunggu Sebentar yaa...");
        progressDialog.setCanceledOnTouchOutside(false);

        binding.btcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditAlam.this, MenuUtama.class);
                finish();
                startActivity(intent);
            }
        });

//        progressDialog = new ProgressDialog(EditAlam.this);
//        progressDialog.setTitle("Loading");
//        progressDialog.setMessage("Menyimpan...");

//        binding.btsave.setOnClickListener(v -> {
//            if (binding.judul.getText().length()>0 && binding.deskripsi.getText().length()>0){
//                upload( binding.judul.getText().toString(), binding.deskripsi.getText().toString());
//                Intent intent = new Intent(EditAlam.this, MenuUtama.class);
//                startActivity(intent);
//            }else {
//                Toast.makeText(getApplicationContext(), "Silahkan mengisi semua data!",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });

        Intent intent = getIntent();
        if (intent != null){
            binding.judul.setText(intent.getStringExtra("judul"));
            binding.deskripsi.setText(intent.getStringExtra("deskripsi"));
            Glide.with(getApplicationContext()).load(intent.getStringExtra("editgamber")).into(binding.imageview);
        }


        binding.btsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validasiData();

            }
        });

        binding.imageview.setOnClickListener(v -> {
            selectImage();
        });

    }

    private void selectImage() {
        Log.d(TAG, "fotoPickIntent: memilih foto..");
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/wisata/*");
        binding.imageview.setImageResource(R.drawable.ic_baseline_image);
        startActivityForResult(Intent.createChooser(intent,"Silahkan Pilih Foto"),GALERY_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == GALERY_PICK){
                Log.d(TAG, "onActivityResult: memilih Foto");
                imageWisataUri = data.getData();
                binding.imageview.setImageURI(imageWisataUri);
                Log.d(TAG, "onActivityResult: URI: "+imageWisataUri);
            }
        }else {
            Log.d(TAG, "onActivityResult: membatalkan memilih foto");
            Toast.makeText(this, "Membatalkan memilih foto", Toast.LENGTH_SHORT).show();
        }

    }

    private String judulWisata="",isiWisata="";
    private void validasiData() {
//        gambarWisata = binding.imageview.getDrawable().toString().trim();
        judulWisata = binding.judul.getText().toString().trim();
        isiWisata = binding.deskripsi.getText().toString().trim();

        if(TextUtils.isEmpty(judulWisata)){
            Toast.makeText(this, "Input Judul Wisata...", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(isiWisata)){
            Toast.makeText(this, "Input Deskripsi Wisata...", Toast.LENGTH_SHORT).show();
        }else{
            uploadWisata();
            cleardata();

        }

    }

    private void cleardata() {
        binding.imageview.setImageResource(R.drawable.ic_baseline_image);
        binding.judul.setText("");
        binding.deskripsi.setText("");
    }

//    private void selectImage(){
//        final CharSequence[] items = {"Take Photo", "Choose form library", "Cancel"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(EditAlam.this);
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
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == 20 && resultCode == RESULT_OK && data != null){
//            final Uri path= data.getData();
//            Thread thread = new Thread(() -> {
//                try {
//                    InputStream inputStream = getContentResolver().openInputStream(path);
//                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                    binding.imageview.post(() -> {
//                        binding.imageview.setImageBitmap(bitmap);
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
//                binding.imageview.post(() -> {
//                    binding.imageview.setImageBitmap(bitmap);
//                });
//            });
//            thread.start();
//        }
//    }

    private void uploadWisata(){
        Log.d(TAG, "uploadWIsata: Mengupload Wisata ke database");

        progressDialog.setMessage("Mengupload Wisata....");
        progressDialog.show();

        long timestamp = System.currentTimeMillis();

        String filePathName = "ImageWisata/"+timestamp;

        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filePathName);
        storageReference.putFile(imageWisataUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: Foto diupload ke penyimpanan");
                        Log.d(TAG, "onSuccess: mendapatkan foto url");

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String gambarWIsata = ""+uriTask.getResult();

                        saveData(gambarWIsata,timestamp);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
//        Log.d(TAG, "uploadWisata: Mengupload ke Database...");
//
//        progressDialog.setMessage("mengupload Wisata....");
//        progressDialog.show();
//
//        long timestamp = System.currentTimeMillis();
//
//        binding.imageview.setDrawingCacheEnabled(true);
//        binding.imageview.buildDrawingCache();
//        Bitmap bitmap = ((BitmapDrawable) binding.imageview.getDrawable()).getBitmap();
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

    private void saveData(String gambarWisata, long timestamp) {
        Log.d(TAG, "uplpadData: Upload ke firebase...");
        progressDialog.setMessage("Mengupload Data....");
        String uid = firebaseAuth.getUid();

        Map<String, Object> alam = new HashMap<>();
        alam.put("uid",""+uid);
        alam.put("id",""+timestamp);
        alam.put("judul", judulWisata);
        alam.put("deskripsi", isiWisata);
        alam.put("editgambar", gambarWisata);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Wisata");
        ref.child(""+timestamp)
                .setValue(alam)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onSuccess: Sukses diupload ke db");
                        Toast.makeText(EditAlam.this, "Berhasil diupload..", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditAlam.this,MenuUtama.class));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: Failed Upload ke db "+e.getMessage());
                        Toast.makeText(EditAlam.this, "Gagal mengupload ke db"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

//        progressDialog.show();
//        db.collection("alams")
//                .add(alam)
//                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                    @Override
//                    public void onSuccess(DocumentReference documentReference) {
//                        Toast.makeText(getApplicationContext(),"Berhasil",Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                        finish();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(),
//                                Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                    }
//                });
    }
}
