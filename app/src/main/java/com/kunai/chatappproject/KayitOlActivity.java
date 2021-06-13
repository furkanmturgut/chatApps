package com.kunai.chatappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class KayitOlActivity extends AppCompatActivity {
    TextInputEditText editTextMail,editTextSifre,editTextIsim;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Boolean mailKontrol;
    TextView textHesabimVar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kayit_ol);

        init();
        textHesabimVar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(KayitOlActivity.this,GirisYapActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        editTextIsim = findViewById(R.id.editTextIsım);
        editTextMail = findViewById(R.id.editTextMail);
        editTextSifre = findViewById(R.id.editTextSifre);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getInstance().getReference();
        textHesabimVar = findViewById(R.id.textHesabimVar);

    }

    public void buttınClickMethod(final View view){
        //Auth ile kayit
        final String mail = editTextMail.getText().toString();
        final String pass = editTextSifre.getText().toString();
        final String name = editTextIsim.getText().toString();

        mailKontrol = mail.contains("@");
        mailKontrol = mail.contains(".com");


        if (mail.isEmpty() && pass.isEmpty() && name.isEmpty()){
            Snackbar.make(view,"Tüm alanları doldurunuz",BaseTransientBottomBar.LENGTH_LONG).show();
        }else if(pass.length() < 6){
            Snackbar.make(view,"En az altı(6) karakter şifre belirleyiniz",BaseTransientBottomBar.LENGTH_LONG).show();
        }else if(mailKontrol != true) {
            Snackbar.make(view, "Geçerli bir mail adresi giriniz", BaseTransientBottomBar.LENGTH_LONG).show();
        }
        else{
            firebaseAuth.createUserWithEmailAndPassword(mail,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(KayitOlActivity.this, "Yönlendiriliyorsunuz!", Toast.LENGTH_SHORT).show();
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("nickName",name);
                    hashMap.put("mailAdres",mail);
                    hashMap.put("sifre",pass);
                    hashMap.put("kID",firebaseAuth.getUid());




                    databaseReference.child("Kullanici Bilgiler").child(name).setValue(hashMap);
                    databaseReference.child("Kullanici Isimler").child(firebaseAuth.getUid()).setValue(hashMap);

                    Intent intent = new Intent(KayitOlActivity.this,KullaniciBilgiActivity.class);
                    intent.putExtra("kAdi",name);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(view,"Hatalı işlem !", BaseTransientBottomBar.LENGTH_LONG).show();
                }
            });
        }


    }


}