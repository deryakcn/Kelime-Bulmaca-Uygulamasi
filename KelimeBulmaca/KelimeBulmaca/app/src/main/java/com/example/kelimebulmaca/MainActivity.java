package com.example.kelimebulmaca;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {




    private WindowManager.LayoutParams params;
    private SharedPreferences preferences;


    private Dialog settingsDialog;
    private  Button ayarlar_btn_isim_degistir;
    private ImageView ayarlar_kapat;
    //Change Name Dialog
    private Dialog changeNameDialog;
    private ImageView isim_kapat;
    private EditText isim_gir;
    private Button isim_degistir_btn;
    private String getChangeName;
    private ImageView butonAyarlar;
    private TextView txt_kullanici_adi;
    private int izinVerme = 0, izinVerildi = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txt_kullanici_adi = (TextView) findViewById(R.id.nickname);

        butonAyarlar = findViewById(R.id.butonAyarlar);
        preferences = this.getSharedPreferences("com.example.kelimebulmaca", MODE_PRIVATE);//veri kaydetme veri çekme






        butonAyarlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ayarlariGoster();
            }
        });


    }

    private void ayarlariGoster() {
        settingsDialog = new Dialog(this);
        params = new WindowManager.LayoutParams();
        params.copyFrom(settingsDialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        settingsDialog.setCancelable(false);
        settingsDialog.setContentView(R.layout.activity_setting);


        ayarlar_kapat = (ImageView) settingsDialog.findViewById(R.id.kapat);
        ayarlar_btn_isim_degistir = (Button) settingsDialog.findViewById(R.id.txt_isim_degistir);



        ayarlar_kapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsDialog.dismiss();// diyalogu kapatır
            }
        });
        ayarlar_btn_isim_degistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isimDegistirDiyalog();
                settingsDialog.dismiss();
            }
        });


        settingsDialog.getWindow().setAttributes(params);

        settingsDialog.show();
    }




    private void isimDegistirDiyalog() {
        changeNameDialog = new Dialog(this);
        params = new WindowManager.LayoutParams();
        params.copyFrom(changeNameDialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        changeNameDialog.setCancelable(false);
        changeNameDialog.setContentView(R.layout.activity_isim_degistirme_ekrani);

        isim_kapat = (ImageView) changeNameDialog.findViewById(R.id.isim_kapat);
        isim_gir = (EditText) changeNameDialog.findViewById(R.id.ayarlar_isim_giriniz);
        isim_degistir_btn = (Button) changeNameDialog.findViewById(R.id.btn_isim_degistir);


       isim_kapat.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               changeNameDialog.dismiss();
           }
       });

        isim_gir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNameDialog.dismiss();
            }
        });

        isim_degistir_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getChangeName = isim_gir.getText().toString();

                if (!TextUtils.isEmpty(getChangeName)) {
                    if (!(getChangeName.matches(txt_kullanici_adi.getText().toString())))
                        ismiGuncelle(getChangeName, txt_kullanici_adi.getText().toString());
                    else
                        Toast.makeText(getApplicationContext(), "Zaten Bu İsimi Kullanıyorsunuz.", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), "İsim Değeri Boş Olamaz.", Toast.LENGTH_SHORT).show();
            }
        });

        changeNameDialog.getWindow().setAttributes(params);
        changeNameDialog.show();

    }

    private void ismiGuncelle(String getChangeName, String txtViewUsername) {
        txt_kullanici_adi.setText(getChangeName);

    }



    public void mainclick(View v) {
        switch (v.getId()) {
            case R.id.button_hemenoyna:
                Intent playIntent = new Intent(this, oyun_ekrani.class);
                finish();
                startActivity(playIntent);

                break;

            case R.id.button_cikis:
                uygulamadanCik();
                break;
        }


    }




    @Override
    public void onBackPressed() {
        //AlertDialog Açılmalı
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Kelime Bulmaca");
        alert.setIcon(R.mipmap.icon);
        alert.setMessage("Uygulamadan Çıkmak İstediğinize Emin Misiniz?");
        alert.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                uygulamadanCik();
            }
        });

        alert.show();
    }

    private void uygulamadanCik() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }


}





