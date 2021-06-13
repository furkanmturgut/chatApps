package com.kunai.chatappproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class GirisYapActivity extends AppCompatActivity {
    TextInputEditText editTextMailGiris,editTextSifreGiris;
    FirebaseAuth firebaseAuth;
    Boolean mailKontrol;
    TextView textHesabimYok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_yap);

        init();
        textHesabimYok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GirisYapActivity.this,KayitOlActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        editTextMailGiris = findViewById(R.id.editTextMailGiris);
        editTextSifreGiris = findViewById(R.id.editTextSifreGiris);
        firebaseAuth = FirebaseAuth.getInstance();
        textHesabimYok = findViewById(R.id.textHesabimYok);
    }

    public  void buttonGirisClick(final View view){
        String mail = editTextMailGiris.getText().toString();
        String pass = editTextSifreGiris.getText().toString();
        mailKontrol = mail.contains("@");
        mailKontrol = mail.contains(".com");

        if (mail.isEmpty() && pass.isEmpty()){
            Snackbar.make(view,"Tüm alanları doldurunuz", BaseTransientBottomBar.LENGTH_LONG).show();
        }else if(pass.length() < 6){
            Snackbar.make(view,"Geçerli bir şifre giriniz",BaseTransientBottomBar.LENGTH_LONG).show();
        }else if (mailKontrol != true){
            Snackbar.make(view,"Geçerli bir mail adresi giriniz",BaseTransientBottomBar.LENGTH_LONG).show();
        }else {
            firebaseAuth.signInWithEmailAndPassword(mail, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    Toast.makeText(GirisYapActivity.this, "Yönlendiriliyorsunuz", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(GirisYapActivity.this,AnasayfaActivity.class);
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