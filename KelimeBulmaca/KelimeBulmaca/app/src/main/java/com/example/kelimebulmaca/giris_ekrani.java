package com.example.kelimebulmaca;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class giris_ekrani extends AppCompatActivity {

    //Sorular İçin Listeler
    private String[] sorularList = {"Bir markette 5 kapak getirene 1 şişe meyve suyu verilmektedir. 50 kapağı olan bir çocuk toplamda kaç tane meyve suyu alabilir?"
            ,"Zafer'in babasının 5 çocuğu var.4'ünün isimleri sırasıyla Zeze, Zizi, Zözö, Zuzu ise 5.çocugun adı nedir?"
            ,"Sana ait olmasına rağmen, başkalarının senden daha çok kullandığı şey nedir?"
            ,"Akşam bakarsan çoktur, gündüz bakarsan yoktur."
            ,"Benim bir dostum var, kuyruğundan uzun burnu var."
            ,"Sarı bir mendil, yeşil bir denize düşerse ne olur?"
            ,"Kocaman beyaz bir perde. Bilsen neler var içinde. Hikayeler masallar. O perdede oynarlar."
            ,"Kolu var bacağı yok, dikdörtgeni vardır, karesi yok."
            ,"29 harf içeren ama sadece 3 hecede söylenen kelime nedir?"
            ,"Sende var, bende yok. Sabahta var, akşamda yok."
            ,"En hızlı yenilen şey nedir?"
            ,"Zilim var ama kapım yok"
            ,"Yer altında turuncu minare"
            ,"Hem açarım hem kapatırım"};

    private String[] sorularKodList = {"kapaksorusu","isimsorusu","aitsorusu","yildizsorusu","filsorusu","mendilsorusu","sinemasorusu","kapisorusu","alfabesorusu","harfsorusu","maasorusu","telefonsorusu","havucsorusu"
             ,"anahtarsorusu"};

    //Kelimeler İçin Listeler
    private String[] kelimelerList = {"12","Zafer","İsmin","Yıldız","Fil","Islanır","Sinema","Kapı","Alfabe","S harfi","Maaş","Telefon","Havuç","Anahtar"};
    private String[] kelimelerKodList = {"kapaksorusu","isimsorusu","aitsorusu","yildizsorusu","filsorusu","mendilsorusu","sinemasorusu","kapisorusu","alfabesorusu","harfsorusu","maasorusu"
            ,"telefonsorusu","havucsorusu","anahtarsorusu"};



    private ProgressBar Progress;
    private TextView TextView;
    private SQLiteDatabase database;
    private Cursor cursor;
    private float maxProgres = 100f, artacakProgress, progresMiktari = 0;
    static public HashMap<String, String> sorularHashmap;
    private String sqlSorgusu;
    private SQLiteStatement statement;


 static public HashMap<String,String> sorularlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_giris_ekrani);

        Progress = (ProgressBar)findViewById(R.id.yükleniyor);
        TextView = (TextView)findViewById(R.id.txt_durum);
        sorularHashmap = new HashMap<>();




        try {
            //veritabanı oluşturma
            database = this.openOrCreateDatabase("KelimeBulmaca", MODE_PRIVATE, null);


           //tablo oluşturma
            database.execSQL("CREATE TABLE IF NOT EXISTS Sorular (id INTEGER PRIMARY KEY, sKod VARCHAR UNIQUE, soru VARCHAR)");// UNIQUE benzersiz hale getirir.Kodun içine yazılan değer bidaha aynı şekilde yazılmaz.
            database.execSQL("DELETE FROM Sorular");//Uygulama tekrardan başlatıldığında soruları baştan başlatır, soruları siler.
            sqlSorulariEkle();
            database.execSQL("CREATE TABLE IF NOT EXISTS Kelimeler (kKod VARCHAR, kelime VARCHAR, FOREIGN KEY (kKod) REFERENCES Sorular (sKod))");//FOREIGN KEY...:kKod ile sKodu birbirine bağlıyoruz.
            database.execSQL("DELETE FROM Kelimeler");
            sqlKelimeleriEkle();

            cursor = database.rawQuery("SELECT * FROM Sorular", null);
            artacakProgress = maxProgres / cursor.getCount();// içerdeki eleman sayısını getirir

            //veriler alınıyor
            int sKodIndex = cursor.getColumnIndex("sKod");
            int soruIndex = cursor.getColumnIndex("soru");

            TextView.setText("Sorular Yükleniyor...");

            while (cursor.moveToNext()){//ilerlee
                sorularHashmap.put(cursor.getString(sKodIndex), cursor.getString(soruIndex));
                progresMiktari += artacakProgress; //artacak proges miktarının üstüne ekler progress=uyarı penceresi giriş ekranı gibi...
                Progress.setProgress((int)progresMiktari);
            }

            TextView.setText("Sorular Alındı, Uygulama Başlatılıyor...");
            cursor.close();

            new CountDownTimer(1100, 1000)// 1 saniye içerisinde sonraki activitye geçiş yapmasını sağlar
            {
                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    Intent mainIntent = new Intent(giris_ekrani.this, MainActivity.class);
                    startActivity(mainIntent);
                }
            }.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void sqlSorulariEkle(){
        try {
            for (int s = 0; s < sorularList.length; s++){
                sqlSorgusu = "INSERT INTO Sorular (sKod, soru) VALUES (?, ?)";
                statement = database.compileStatement(sqlSorgusu);
                statement.bindString(1, sorularKodList[s]);
                statement.bindString(2, sorularList[s]);
                statement.execute();//veriler eklendi
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sqlKelimeleriEkle(){
        try {
            for (int k = 0; k < kelimelerList.length; k++){
                sqlSorgusu = "INSERT INTO Kelimeler (kKod, kelime) VALUES (?, ?)";
                statement = database.compileStatement(sqlSorgusu);
                statement.bindString(1, kelimelerKodList[k]);
                statement.bindString(2, kelimelerList[k]);
                statement.execute();// Kelimeler veritabanına eklendi
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}