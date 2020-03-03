package com.matejcerna.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.matejcerna.R;
import com.matejcerna.database.Baza;
import com.matejcerna.model.Admin;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.matejcerna.activity.DodajOsobuActivity.kreirajIspravnuSliku;
import static com.matejcerna.activity.DodajOsobuActivity.provjeriUnos;
import static com.matejcerna.activity.TraziAdminaActivity.admini;

public class UrediAdmina3Activity extends AppCompatActivity {

    /*EditText urediKorisnickoImePretrazenogAdmina;
    EditText urediLozinkuPretrazenogAdmina;
    Button urediPretrazenogAdmina;
    Button odaberiSliku;
    ImageView imageViewUrediSlikuPretrazenogAdmina;*/
    byte[] slika_iz_baze;
    byte[] slika_admina;
    byte[] slika_nova;
    @BindView(R.id.image_view_uredi_sliku_trazenog_admina)
    ImageView imageViewUrediSlikuPretrazenogAdmina;
    @BindView(R.id.uredi_korisnicko_ime_trazenog_admina_txt)
    EditText urediKorisnickoImePretrazenogAdmina;
    @BindView(R.id.uredi_lozinku_trazenog_admina_txt)
    EditText urediLozinkuPretrazenogAdmina;

    private boolean success = false;
    private int GALLERY = 1;
    private int CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/slike_iz_aplikacije";

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    String cameraPermission[];
    String storagePermission[];
    Uri imageUri;
    ContentValues cv;
    private String lokacija_slike = "";


    int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uredi_admina3);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);

       /* urediKorisnickoImePretrazenogAdmina = findViewById(R.id.uredi_korisnicko_ime_trazenog_admina_txt);
        urediLozinkuPretrazenogAdmina = findViewById(R.id.uredi_lozinku_trazenog_admina_txt);
        urediPretrazenogAdmina = findViewById(R.id.uredi_trazenog_admina_btn);
        odaberiSliku = findViewById(R.id.odaberi_sliku_trazenog_admina_btn);
        imageViewUrediSlikuPretrazenogAdmina = findViewById(R.id.image_view_uredi_sliku_trazenog_admina);*/

       /* urediKorisnickoImePretrazenogAdmina.setText(admini.get(position).getKorisniko_ime());
        urediLozinkuPretrazenogAdmina.setText(admini.get(position).getLozinka());
        slika = admini.get(position).getSlika();
        Bitmap slika_admina= BitmapFactory.decodeByteArray(slika,0,slika.length);
        imageViewUrediSlikuPretrazenogAdmina.setImageBitmap(slika_admina);*/

        dohvatiDetaljeAdmina(id);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                    Toast.makeText(UrediAdmina3Activity.this, "Neuspješno!", Toast.LENGTH_SHORT).show();
                }
                lokacija_slike = saveImage(bitmap);
                imageViewUrediSlikuPretrazenogAdmina.setImageBitmap(bitmap);
                Toast.makeText(UrediAdmina3Activity.this, "Slika spremljena u mapu " + IMAGE_DIRECTORY, Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = null;
            try {
                thumbnail = kreirajIspravnuSliku(this, imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(UrediAdmina3Activity.this, "Neuspješno!", Toast.LENGTH_SHORT).show();
            }
            lokacija_slike = saveImage(thumbnail);
            imageViewUrediSlikuPretrazenogAdmina.setImageBitmap(thumbnail);
            Toast.makeText(UrediAdmina3Activity.this, "Slika spremljena u mapu " + IMAGE_DIRECTORY, Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
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
            slika_nova = bytes.toByteArray();
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
                    if (!checkCameraPermission()) {
                        //nije odobreno koristenje kamere, zatrazi dopustenje
                        requestCameraPermission();
                    } else {
                        //dopustena je upotreba kamere, moze se uslikati slika
                        takePhotoFromCamera();
                    }
                }
                if (which == 1) {
                    //odabrana je opcija GALERIJA
                    if (!checkStoragePermission()) {
                        //nije odobreno koristenje galerije, zatrazi dopustenje
                        requestStoragePermission();
                    } else {
                        //dopustena je upotreba galerije, moze se odabrati slika
                        choosePhotoFromGallary();
                    }
                }

            }
        });
        dialog.create().show(); //prikazivanje dialoga, hocemo li odabrati kameru ili galeriju
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
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, GALLERY);
    }


    public void dohvatiDetaljeAdmina(int id_admina) {
        String korisnicko_ime;
        String lozinka;
        slika_iz_baze = null;

        String msg = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(Baza.getDbUrl(), Baza.getUSER(), Baza.getPASS());

            if (connection == null) {
                success = false;
            } else {
                String upit = "SELECT * FROM admin WHERE id = '" + id_admina + "'";
                PreparedStatement preparedStatement = connection.prepareStatement(upit);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next() == false) {
                    msg = "Nema podataka u bazi!";
                    success = false;
                } else {
                    do {
                        try {
                            admini.add(new Admin(
                                    resultSet.getInt("id"),
                                    korisnicko_ime = resultSet.getString("korisnicko_ime"),
                                    lozinka = resultSet.getString("lozinka"),
                                    slika_iz_baze = resultSet.getBytes("slika")));

                            urediKorisnickoImePretrazenogAdmina.setText(korisnicko_ime);
                            urediLozinkuPretrazenogAdmina.setText(lozinka);
                            final Bitmap slika_admina = BitmapFactory.decodeByteArray(slika_iz_baze, 0, slika_iz_baze.length);
                            imageViewUrediSlikuPretrazenogAdmina.setImageBitmap(slika_admina);

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    } while (resultSet.next());
                    msg = "Ima podataka u bazi!";
                    success = true;


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Writer writer = new StringWriter();
            e.printStackTrace(new PrintWriter(writer));
            msg = writer.toString();
            success = false;

        }

    }

    public void urediAdmina() {
        //String msg = "";
        String korisnicko_ime = urediKorisnickoImePretrazenogAdmina.getText().toString();
        String lozinka = urediLozinkuPretrazenogAdmina.getText().toString();


        if (slika_nova == null) {
            slika_admina = slika_iz_baze;
        } else {
            slika_admina = slika_nova;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(Baza.getDbUrl(), Baza.getUSER(), Baza.getPASS());
            if (connection == null) {
                //msg = "Problemi prilikom konekcije";
                Toast.makeText(UrediAdmina3Activity.this, "Problemi prilikom konekcije!", Toast.LENGTH_SHORT).show();
            } else {
                if (korisnicko_ime.equals("") || lozinka.equals("")) {
                    Toast.makeText(UrediAdmina3Activity.this, "Morate ispuniti sva polja!", Toast.LENGTH_SHORT).show();
                } else {
                    if (slika_admina == null) {
                        Toast.makeText(this, "Morate odabrati sliku!", Toast.LENGTH_SHORT).show();
                    } else {
                        String upit = "UPDATE admin SET korisnicko_ime= ?, lozinka= ?, slika= ?  WHERE id= ?";
                        PreparedStatement preparedStatement = connection.prepareStatement(upit);

                        preparedStatement.setString(1, korisnicko_ime);
                        preparedStatement.setString(2, lozinka);
                        preparedStatement.setBytes(3, slika_admina);
                        preparedStatement.setInt(4, id);

                        preparedStatement.executeUpdate();
                        // msg = "Osoba dodana";
                        Toast.makeText(UrediAdmina3Activity.this, "Podaci su ažurirani!", Toast.LENGTH_SHORT).show();
                    }


                }


            }
            connection.close();
        } catch (Exception e) {
            //msg = "Probllemi s konekcijom";
            Toast.makeText(UrediAdmina3Activity.this, "Problemi s konekcijom!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        // poruka.setText(msg);
    }


    @OnClick(R.id.odaberi_sliku_trazenog_admina_btn)
    public void onOdaberiSlikuTrazenogAdminaBtnClicked() {
        prikaziDialog();
    }

    @OnClick(R.id.uredi_trazenog_admina_btn)
    public void onUrediTrazenogAdminaBtnClicked() {
        provjeriUnos(urediKorisnickoImePretrazenogAdmina);
        provjeriUnos(urediLozinkuPretrazenogAdmina);
        urediAdmina();
    }
}
