package com.kunai.chatappproject;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfilFragment extends Fragment {
    TextView textContact,backProfil;
    Button buttonEdit;
    CircleImageView circlePhoto;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    Dialog dialog;
    SharedPreferences sharedPreferences;
    StorageReference storageReference;
    Uri imageData;
    Bitmap profilResimBitmap;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profil, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Init
       firebaseAuth = FirebaseAuth.getInstance();
       firebaseDatabase = FirebaseDatabase.getInstance();
       databaseReference = firebaseDatabase.getInstance().getReference();
       firebaseStorage = FirebaseStorage.getInstance();
       storageReference = firebaseStorage.getInstance().getReference();
       init(view);
       dialog = new Dialog(getContext());
       buttonEdit.setVisibility(View.INVISIBLE);
       backProfil.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getActivity(),AnasayfaActivity.class);
               startActivity(intent);
           }
       });

       circlePhoto.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               buttonEdit.setVisibility(View.VISIBLE);
               buttonEdit.setText("Kaydet");

               if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                   ActivityCompat.requestPermissions(getActivity(),new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1);
               }else{
                   Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   startActivityForResult(intentToGallery,2);
               }
           }
       });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String imageName = firebaseAuth.getUid() + ".jpeg";

                if (imageData==null){
                    Toast.makeText(getActivity(), "Resim seçilmedi", Toast.LENGTH_SHORT).show();
                    buttonEdit.setVisibility(View.INVISIBLE);
                }else {
                    storageReference.child(imageName).putFile(imageData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            StorageReference newReference = FirebaseStorage.getInstance().getReference(imageName);
                            newReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    final HashMap<String,Object> detaylar = new HashMap<>();
                                    detaylar.put("resim",downloadUrl);
                                    databaseReference.child("Kullanici Isimler").child(firebaseAuth.getUid()).updateChildren(detaylar);

                                    buttonEdit.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });
                }


            }
        });

        textContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.popuptasarim);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

                Button buttonPopupEdit = dialog.findViewById(R.id.buttonPopupEdit);

                buttonPopupEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final EditText hakkindaEdit = dialog.findViewById(R.id.hakkindaEdit);
                        String gelenHak = hakkindaEdit.getText().toString();

                        if (gelenHak == null){
                            Snackbar.make(view,"Alan boş bırakılamaz", BaseTransientBottomBar.LENGTH_LONG).show();
                        }else if(gelenHak.length()<=3){
                            Snackbar.make(view,"En az üç(3) harf giriniz!",BaseTransientBottomBar.LENGTH_LONG).show();
                        }else{
                            final HashMap<String,Object> detaylar = new HashMap<>();
                            detaylar.put("hakkimda",gelenHak);

                            databaseReference.child("Kullanici Isimler").child(firebaseAuth.getUid()).updateChildren(detaylar);
                            Toast.makeText(getActivity(), "Tamamlandı", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });


    }

    public void init(View view){

        textContact = view.findViewById(R.id.textContact);
        backProfil = view.findViewById(R.id.backProfil);
        buttonEdit = view.findViewById(R.id.buttonEdit);
        circlePhoto = view.findViewById(R.id.circlePhoto);



        databaseReference.child("Kullanici Isimler").child(firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String userContact = dataSnapshot.child("hakkimda").getValue(String.class);
                String userPhoto = dataSnapshot.child("resim").getValue(String.class);
                Picasso.get().load(userPhoto).into(circlePhoto);

                textContact.setText(userContact);

                sharedPreferences = getContext().getSharedPreferences("Kullanici Bilgi", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("hakk",userContact);
                editor.putString("photo",userPhoto);
                editor.commit();

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode ==1){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode ==2 && resultCode == RESULT_OK && data != null){

            imageData = data.getData();

            try {
                if (Build.VERSION.SDK_INT >= 28){
                    ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(),imageData);
                    profilResimBitmap = ImageDecoder.decodeBitmap(source);
                    circlePhoto.setImageBitmap(profilResimBitmap);
                }

                profilResimBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),imageData);
                circlePhoto.setImageBitmap(profilResimBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}