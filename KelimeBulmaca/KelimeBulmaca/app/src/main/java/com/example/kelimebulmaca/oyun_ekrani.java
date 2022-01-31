package com.example.kelimebulmaca;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class oyun_ekrani extends AppCompatActivity {
    private AlertDialog.Builder alert;





    private TextView txt_sorular, txt_cevaplar;
    private EditText editTextTahminDegeri;
    private SQLiteDatabase database;
    private Cursor cursor;
    private ArrayList<String> sorularList;
    private ArrayList<String> sorularKodList;
    private ArrayList<String> kelimelerList;
    private ArrayList<Character> kelimeHarfleri;
    private ImageView tablo;
    private Random rndSoru, rndKelime, rndHarf;
    private int rndSoruNumber, rndKelimeNumber, rndHarfNumber;
    private String rastgeleSoru, rastgeleSoruKodu, rastgeleKelime, kelimeBilgisi, textTahminDegeri;
    private int rastgeleBelirlenecekHarfSayisi;

    private  Dialog statisticTableDialog;
    private ImageView tablo_kapat;
    private LinearLayout butonlar;
    private Button btn_anamenu, btn_oyun_baslat;
    private TextView toplam_soru_gösterge, toplam_kelime_gösterge, yanlis_kelime_gösterge;
    private ProgressBar toplamsoru, toplamkelime, yanliskelime;
    private WindowManager.LayoutParams params;
    private int cozulenKelimeSayisi = 0, cozulenSoruSayisi = 0, yapilanYanlisSayisi = 0, maksimumSoruSayisi, maksimumKelimeSayisi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oyun_ekrani);
        txt_sorular= (TextView) findViewById(R.id.txt_sorular);
        txt_cevaplar = (TextView) findViewById(R.id.txt_cevaplar);
        editTextTahminDegeri = (EditText) findViewById(R.id.txt_tahmin_giriniz);
        tablo= (ImageView)findViewById(R.id.tablo);

        sorularList = new ArrayList<>();
        sorularKodList = new ArrayList<>();
        kelimelerList = new ArrayList<>();
        rndSoru = new Random();
        rndKelime = new Random();
        rndHarf = new Random();



        for (Map.Entry soru : giris_ekrani.sorularHashmap.entrySet()) {
            sorularList.add(String.valueOf(soru.getValue()));
            sorularKodList.add(String.valueOf(soru.getKey()));
        }


        randomSoruGetir();
    }

    @Override
    public void onBackPressed() {
        alert = new AlertDialog.Builder(this);
        alert.setTitle("Kelime Bulmaca");
        alert.setMessage("Geri Dönmek İstediğinize Emin Misiniz?");

        alert.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mainIntent();
            }
        });

        alert.show();
    }

    public void btnIstatistikTablosu(View v) {
        maksimumVerileriHesapla("");
    }



    private void istatistiktablogöster(String oyunDurumu, int maksimumSoruSayisi, int maksimumKelimeSayisi, int cozulenSoruSayisi, int cozulenKelimeSayisi, int yapilanYanlisSayisi) {
        params = new WindowManager.LayoutParams();
        params.copyFrom(statisticTableDialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        statisticTableDialog = new Dialog(this);
        statisticTableDialog.setContentView(R.layout.activity_istatislik_tablosu);

        tablo_kapat = (ImageView) statisticTableDialog.findViewById(R.id.tablo_kapat);
        butonlar = (LinearLayout) statisticTableDialog.findViewById(R.id.butonlar);

        btn_anamenu = (Button) statisticTableDialog.findViewById(R.id.btn_anasayfaya_don);
        btn_oyun_baslat = (Button) statisticTableDialog.findViewById(R.id.bbtn_tekrar_oyna);

        toplam_soru_gösterge = (TextView) statisticTableDialog.findViewById(R.id.soru_sayisi_gösterge);
        toplam_kelime_gösterge = (TextView) statisticTableDialog.findViewById(R.id.kelime_sayisi_gösterge);
        yanlis_kelime_gösterge = (TextView) statisticTableDialog.findViewById(R.id.yanlis_kelime_sayisi_gösterge);

        toplamsoru = (ProgressBar) statisticTableDialog.findViewById(R.id.sorusayisi_bar);
        toplamkelime = (ProgressBar) statisticTableDialog.findViewById(R.id.kelimesayisi_bar);
        yanliskelime = (ProgressBar) statisticTableDialog.findViewById(R.id.yanlis_tahmin_sayisi_bar);


        if (oyunDurumu.matches("oyunBitti")) {
            statisticTableDialog.setCancelable(false);
            butonlar.setVisibility(View.VISIBLE);
            tablo_kapat.setVisibility(View.INVISIBLE);
        }

        toplam_soru_gösterge.setText(cozulenSoruSayisi + " / " + maksimumSoruSayisi);
        toplam_kelime_gösterge.setText(cozulenKelimeSayisi + " / " + maksimumKelimeSayisi);
        yanlis_kelime_gösterge.setText(yapilanYanlisSayisi + " / " + maksimumKelimeSayisi);

        toplamsoru.setProgress(cozulenSoruSayisi);
        toplamkelime.setProgress(cozulenKelimeSayisi);
        yanliskelime.setProgress(yapilanYanlisSayisi);

        tablo_kapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statisticTableDialog.dismiss();
            }
        });

        btn_anamenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Main Menu
                mainIntent();
            }
        });

        btn_oyun_baslat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Play Again
                Intent thisIntent = new Intent(oyun_ekrani.this, oyun_ekrani.class);
                finish();
                startActivity(thisIntent);
            }
        });

        statisticTableDialog.getWindow().setAttributes(params);
        statisticTableDialog.show();
    }

    private void maksimumVerileriHesapla(String oyunDurumu) {
        try {
            cursor = database.rawQuery("SELECT * FROM Kelimeler, Sorular WHERE Kelimeler.kKod = Sorular.sKod", null);//database tanımlama
            maksimumKelimeSayisi = cursor.getCount();

            cursor = database.rawQuery("SELECT * FROM Sorular", null);
            maksimumSoruSayisi = cursor.getCount();

            cursor.close();

            istatistiktablogöster(oyunDurumu, maksimumSoruSayisi, maksimumKelimeSayisi, cozulenSoruSayisi, cozulenKelimeSayisi, yapilanYanlisSayisi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void btnHarfAl(View v) {
        Toast.makeText(getApplicationContext(), "Harf alındı", Toast.LENGTH_LONG).show();
        rastgeleHarfAl();

    }


    public void btnTahminEt(View v) {
        textTahminDegeri = editTextTahminDegeri.getText().toString();

        if (!TextUtils.isEmpty(textTahminDegeri)) {
            if (textTahminDegeri.matches(rastgeleKelime)) {//matches= metinsel ifadeleri eşleştirir
                Toast.makeText(getApplicationContext(), "Tebrikler Doğru Tahminde Bulundunuz.", Toast.LENGTH_SHORT).show();
                editTextTahminDegeri.setText("");
                cozulenKelimeSayisi++;

                if (kelimelerList.size() > 0)
                    randomKelimeGetir();// yeni kelime getiriyoruz
                else if (sorularList.size() > 0) {
                    cozulenSoruSayisi++;
                    randomSoruGetir();//kelimeler bittiğinde yeni soru getiriyoruz
                } else
                    maksimumVerileriHesapla("oyunBitti");
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Yanlış tahminde bulundunuz!!!",Toast.LENGTH_LONG).show();
            }
        }
        else

            Toast.makeText(getApplicationContext(), "Tahmin Değeri Boş Olamaz.", Toast.LENGTH_SHORT).show();


    }



    private void rastgeleHarfAl() {
        if (kelimeHarfleri.size() > 0) {
            rndHarfNumber = rndHarf.nextInt(kelimeHarfleri.size());
            String[] txtHarfler = txt_cevaplar.getText().toString().split(" ");//kelimeler texindeki değerleri aldık
            char[] gelenKelimeHarfler = rastgeleKelime.toCharArray();//gelen harflerin 1den fazla olmasını engeller

            for (int i = 0; i < rastgeleKelime.length(); i++) {

                if (txtHarfler[i].equals("_") && gelenKelimeHarfler[i] == kelimeHarfleri.get(rndHarfNumber))
                    //harflerin alt tireye eşit olması ve random harflerin indexdeki değerle eşit olması lazım
                {
                    txtHarfler[i] = String.valueOf(kelimeHarfleri.get(rndHarfNumber));//harfler eşleştiyse verileri içeriye atar
                    kelimeBilgisi = "";//kelimebilgisi boş hale getirildi

                    for (int j = 0; j < txtHarfler.length; j++) {//
                        if (j < txtHarfler.length - 1)
                            kelimeBilgisi += txtHarfler[j] + " ";
                        else
                            kelimeBilgisi += txtHarfler[j];
                    }

                    break;
                }
            }

            txt_cevaplar.setText(kelimeBilgisi);
            kelimeHarfleri.remove(rndHarfNumber);
        }
    }

    private void mainIntent() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainIntent);

    }

    private void randomSoruGetir() {
        rndSoruNumber = rndSoru.nextInt(sorularKodList.size());
        rastgeleSoru = sorularList.get(rndSoruNumber);
        rastgeleSoruKodu = sorularKodList.get(rndSoruNumber);
        sorularList.remove(rndSoruNumber);
        sorularKodList.remove(rndSoruNumber);

        txt_sorular.setText(rastgeleSoru);

        try {
            database = this.openOrCreateDatabase("KelimeBulmaca", MODE_PRIVATE, null);//database tanımlama
            cursor = database.rawQuery("SELECT * FROM Kelimeler WHERE kKod = ?", new String[]{rastgeleSoruKodu});

            int kelimeIndex = cursor.getColumnIndex("kelime");

            while (cursor.moveToNext())
                kelimelerList.add(cursor.getString(kelimeIndex));//kellime verilerini alıyoruz

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        randomKelimeGetir(); //randomKelimeGetir metodunu çağırır
    }


    private void randomKelimeGetir() {

        kelimeBilgisi = "";//sıfırlama

        rndKelimeNumber = rndKelime.nextInt(kelimelerList.size());// kelimeleri alıyoruz
        rastgeleKelime = kelimelerList.get(rndKelimeNumber);//kelime getirme
        kelimelerList.remove(rndKelimeNumber);//kelime silme(aynı kelimenin birdaha gelmemesi için)

        //alt tire sayısı ayarlanıyor
        for (int i = 0; i < rastgeleKelime.length(); i++) {// ekrana çıkacak kelime kadar alt tire koyulur
            if (i < rastgeleKelime.length() - 1)
                kelimeBilgisi += "_ ";
            else
                kelimeBilgisi += "_";
        }

        txt_cevaplar.setText(kelimeBilgisi);
        //sisteme yazdırıyoruz (lokal kısmına)
        System.out.println("Gelen Kelime = " + rastgeleKelime);
        System.out.println("Gelen Kelime Harf Sayısı = " + rastgeleKelime.length());
        kelimeHarfleri = new ArrayList<>();

        for (char harf : rastgeleKelime.toCharArray())
            kelimeHarfleri.add(harf);

        //kelime uzunluğuna göre ekranda gözükecek harf sayısını belirleme
        if (rastgeleKelime.length() >= 5 && rastgeleKelime.length() <= 7)
            rastgeleBelirlenecekHarfSayisi = 1;
        else if (rastgeleKelime.length() >= 8 && rastgeleKelime.length() <= 10)
            rastgeleBelirlenecekHarfSayisi = 2;
        else if (rastgeleKelime.length() >= 11 && rastgeleKelime.length() <= 14)
            rastgeleBelirlenecekHarfSayisi = 3;
        else if (rastgeleKelime.length() >= 15)
            rastgeleBelirlenecekHarfSayisi = 4;
        else
            rastgeleBelirlenecekHarfSayisi = 0;

        for (int i = 0; i < rastgeleBelirlenecekHarfSayisi; i++)
            rastgeleHarfAl();
    }
}