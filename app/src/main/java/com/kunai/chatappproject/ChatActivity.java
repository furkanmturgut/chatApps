package com.kunai.chatappproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {
    EditText editTextMesaj;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    ImageView imageBack,imageSend;
    TextView textUser;
    RecyclerView recyclerViewChat;
    List<MesajModel> list;
    List<String> listResim;
    RecyclerChat chatAdapter;
    Dialog dialog;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        list = new ArrayList<>();
        listResim = new ArrayList<>();
        recyclerViewChat = findViewById(R.id.recylerViewChat);
        editTextMesaj = findViewById(R.id.editTextMesaj);
        textUser = findViewById(R.id.textUser);
        imageBack = findViewById(R.id.imageBack);
        imageSend = findViewById(R.id.imageSend);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getInstance().getReference();
        dialog = new Dialog(this);

        Bundle extras = getIntent().getExtras();
        //ID dB'den aldı
        String myID = "";
        myID = extras.getString("myId");


        OSDeviceState deviceState = OneSignal.getDeviceState();
        final String userId = deviceState != null ? deviceState.getUserId() : null;
        user = firebaseAuth.getCurrentUser();


        UUID uuid = UUID.randomUUID();
        final String uuids = uuid.toString();

        DatabaseReference newReference = firebaseDatabase.getReference("PlayerIDs");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> playerIDsFromServer = new ArrayList<>();

                for (DataSnapshot ds: dataSnapshot.getChildren()) {

                    HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                    String currentPlayerID = hashMap.get("playerID");
                    System.out.println("current: "+currentPlayerID);
                    playerIDsFromServer.add(currentPlayerID);
                }

                if (!playerIDsFromServer.contains(userId)) {
                    databaseReference.child("PlayerIDs").child(uuids).child("playerID").setValue(userId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        textUser.setText(myID);
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this,AnasayfaActivity.class);
                startActivity(intent);
            }
        });


        //Mesaj yollama
        final String finalMyID = myID;
        imageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String atilanMesaj = editTextMesaj.getText().toString();
                final Map<String,String> mesajMap = new HashMap<>();
                if (atilanMesaj.isEmpty()){
                    Toast.makeText(ChatActivity.this, "Mesaj yollamak istersen değer yaz!", Toast.LENGTH_SHORT).show();
                }else {
                    mesajMap.put("mesaj",atilanMesaj);
                    mesajMap.put("alici", finalMyID);
                    databaseReference.child("Kullanici Isimler").child(firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            final String userNames = dataSnapshot.child("nickName").getValue(String.class);
                            final String key = databaseReference.child("Mesajlar").child(userNames).child(finalMyID).push().getKey();

                            DatabaseReference newReferences = firebaseDatabase.getReference("PlayerIDs");
                            newReferences.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();


                                        String playerID = hashMap.get("playerID");
                                        System.out.println("playerID server:" + playerID);


                                        try {
                                            OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+userNames+"'}, 'include_player_ids': ['" + playerID + "']}"), null);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                            editTextMesaj.setText("");
                            databaseReference.child("Mesajlar").child(userNames).child(finalMyID).child(key).setValue(mesajMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            databaseReference.child("Mesajlar").child(finalMyID).child(userNames).child(key).setValue(mesajMap);
                                        }
                                    });

                        }
                    });

                }
            }
            });




        recyclerViewChat = findViewById(R.id.recylerViewChat);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ChatActivity.this,1);
        recyclerViewChat.setLayoutManager(layoutManager);

        databaseReference.child("Kullanici Isimler").child(firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("nickName").getValue(String.class);
                chatAdapter = new RecyclerChat(list,ChatActivity.this,ChatActivity.this, userName);
                recyclerViewChat.setAdapter(chatAdapter);

            }
        });

        loadMesajs();
    }




    public void loadMesajs() {
        Bundle extras = getIntent().getExtras();
        //ID dB'den aldı
        String myID = "";
        myID = extras.getString("myId");

        final String finalMyID = myID;
        databaseReference.child("Kullanici Isimler").child(firebaseAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("nickName").getValue(String.class);
                databaseReference.child("Mesajlar").child(userName).child(finalMyID).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        MesajModel mesajModel = snapshot.getValue(MesajModel.class);
                        list.add(mesajModel);
                        chatAdapter.notifyDataSetChanged();
                        recyclerViewChat.scrollToPosition(list.size() - 1);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

        });

    }

}