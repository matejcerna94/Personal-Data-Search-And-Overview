package com.matejcerna;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Calendar;

import static com.matejcerna.DodajOsobuActivity.kreirajIspravnuSliku;
import static com.matejcerna.DodajOsobuActivity.provjeriUnos;

public class DodajAdminaActivity extends AppCompatActivity {

    EditText unesiKorisnickoIme;
    EditText unesiLozinku;
    Button dodajAdmina;
    Button odaberiSliku;
    ImageView imageViewSlikaAdmina;
    byte[] slika_admina;
    private int GALLERY = 1;
    private int CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/slike_iz_aplikacije";
    private String lokacija_slike = "";
    Uri imageUri;
    ContentValues cv;

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    String cameraPermission[];
    String storagePermission[];




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_admina);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        unesiKorisnickoIme = findViewById(R.id.unesi_korisnicko_ime_txt);
        unesiLozinku = findViewById(R.id.unesi_lozinku_txt);
        imageViewSlikaAdmina = findViewById(R.id.image_view_slika_admina);
        dodajAdmina = findViewById(R.id.dodaj_admina_btn);
        odaberiSliku = findViewById(R.id.odaberi_sliku_admina_btn);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        odaberiSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prikaziDialog();
            }
        });

        dodajAdmina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provjeriUnos(unesiKorisnickoIme);
                provjeriUnos(unesiLozinku);
                dodajAdmina();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            Bitmap bitmap = null;
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = kreirajIspravnuSliku(this, contentURI);


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(DodajAdminaActivity.this, "Neuspješno!", Toast.LENGTH_SHORT).show();
                }
                lokacija_slike = saveImage(bitmap);
                imageViewSlikaAdmina.setImageBitmap(bitmap);
                Toast.makeText(DodajAdminaActivity.this, "Slika spremljena u mapu " + IMAGE_DIRECTORY, Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = null;
            try {
                thumbnail = kreirajIspravnuSliku(this, imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(DodajAdminaActivity.this, "Neuspješno!", Toast.LENGTH_SHORT).show();
            }
            lokacija_slike = saveImage(thumbnail);
            imageViewSlikaAdmina.setImageBitmap(thumbnail);
            Toast.makeText(DodajAdminaActivity.this, "Slika spremljena u mapu " + IMAGE_DIRECTORY, Toast.LENGTH_SHORT).show();
        }


    }


    private void dodajAdmina() {
        String korisnicko_ime = unesiKorisnickoIme.getText().toString();
        String lozinka = unesiLozinku.getText().toString();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(Baza.getDbUrl(), Baza.getUSER(), Baza.getPASS());
            if (connection == null) {
                Toast.makeText(DodajAdminaActivity.this, "Problemi prilikom konekcije!", Toast.LENGTH_SHORT).show();
            } else {
                if (korisnicko_ime.equals("") || lozinka.equals("")) {
                    Toast.makeText(DodajAdminaActivity.this, "Morate ispuniti sva polja!", Toast.LENGTH_SHORT).show();
                } else {
                    if(slika_admina == null){
                        Toast.makeText(this, "Morate odabrati sliku!", Toast.LENGTH_SHORT).show();
                    }else{
                        String upit = "INSERT INTO admin(id, korisnicko_ime, lozinka, slika) VALUES (null, ?, ?, ?)";
                        PreparedStatement preparedStatement = connection.prepareStatement(upit);

                        preparedStatement.setString(1, korisnicko_ime);
                        preparedStatement.setString(2, lozinka);
                        preparedStatement.setBytes(3, slika_admina);
                        preparedStatement.executeUpdate();
                        Toast.makeText(DodajAdminaActivity.this, "Admin dodan!", Toast.LENGTH_SHORT).show();

                        unesiKorisnickoIme.setText("");
                        unesiLozinku.setText("");
                        slika_admina = null;
                        imageViewSlikaAdmina.setImageBitmap(null);
                    }
                }


            }
            connection.close();
        } catch (Exception e) {
            Toast.makeText(DodajAdminaActivity.this, "Problemi s konekcijom!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    private void prikaziDialog() {
        //koje ce se stavke prikazati u dialogu
        String[] stavke = {" Kamera", " Galerija"};
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        //postavljanje naslova
        dialog.setTitle("Odaberite sliku");
        dialog.setItems(stavke, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    //odabrana je opcija KAMERA
                    if(!checkCameraPermission()){
                        //nije odobreno koristenje kamere, zatrazi dopustenje
                        requestCameraPermission();
                    }else{
                        //dopustena je upotreba kamere, moze se uslikati slika
                        takePhotoFromCamera();
                    }
                }
                if (which == 1) {
                    //odabrana je opcija GALERIJA
                    if(!checkStoragePermission()){
                        //nije odobreno koristenje galerije, zatrazi dopustenje
                        requestStoragePermission();
                    }else{
                        //dopustena je upotreba galerije, moze se odabrati slika
                        choosePhotoFromGallary();
                    }
                }

            }
        });
        dialog.create().show(); //prikazivanje dialoga, hocemo li odabrati kameru ili galeriju
    }


    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_REQUEST_CODE);
    }

    private boolean checkStoragePermission() {
        boolean rezultat = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return rezultat;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {
        //provjeravamo je li dopustena upotreba kamere i vracamo rezultat
        // da bi dobili dobru kvalitetu slike, pozeljno je da ju spremimo  vanjski spremnik prije umetanja u imageview i zbog toga nam treba dopustenje
        boolean rezultat = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean rezultat1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return rezultat && rezultat1;
    }

    public void choosePhotoFromGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, GALLERY);
    }

    private void takePhotoFromCamera() {
        cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "My Picture");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imageUri = getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA);
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            slika_admina = bytes.toByteArray();
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());


            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }


}
