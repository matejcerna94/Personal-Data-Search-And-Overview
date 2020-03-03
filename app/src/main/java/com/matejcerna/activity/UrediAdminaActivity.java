package com.matejcerna.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.app.AlertDialog;
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
import static com.matejcerna.activity.PrikazAdminaActivity.admini;

public class UrediAdminaActivity extends AppCompatActivity {

    @BindView(R.id.image_view_uredi_admina)
    ImageView imageViewUrediAdmina;
    @BindView(R.id.uredi_korisnicko_ime_txt)
    EditText urediKorisnickoIme;
    @BindView(R.id.uredi_lozinku_txt)
    EditText urediLozinku;
    /*EditText urediKorisnickoIme;
        EditText urediLozinku;
        Button urediAdmina;
        Button odaberiSliku;
        ImageView imageViewUrediAdmina;*/
    private boolean success = false;

    byte[] slika_admina;
    byte[] slika_iz_baze;
    byte[] slika_nova;

    private int GALLERY = 1;
    private int CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/slike_iz_aplikacije";
    Uri imageUri;
    ContentValues cv;
    private String lokacija_slike = "";


    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uredi_admina);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        /*urediKorisnickoIme=findViewById(R.id.uredi_korisnicko_ime_txt);
        urediLozinku=findViewById(R.id.uredi_lozinku_txt);
        odaberiSliku = findViewById(R.id.odaberi_uredi_sliku_btn);
        urediAdmina = findViewById(R.id.uredi_admina_btn);
        imageViewUrediAdmina = findViewById(R.id.image_view_uredi_admina);*/

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
       /* urediKorisnickoIme.setText(intent.getStringExtra("korisnicko_ime"));
        urediLozinku.setText(intent.getStringExtra("lozinka"));
        slika = intent.getByteArrayExtra("slika");
        Bitmap slika_admina= BitmapFactory.decodeByteArray(slika,0,slika.length);
        imageViewUrediAdmina.setImageBitmap(slika_admina);*/

        dohvatiDetaljeAdmina(id);


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

                            urediKorisnickoIme.setText(korisnicko_ime);
                            urediLozinku.setText(lozinka);
                            final Bitmap slika_admina = BitmapFactory.decodeByteArray(slika_iz_baze, 0, slika_iz_baze.length);
                            imageViewUrediAdmina.setImageBitmap(slika_admina);

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
                    Toast.makeText(UrediAdminaActivity.this, "Neuspješno!", Toast.LENGTH_SHORT).show();
                }
                lokacija_slike = saveImage(bitmap);
                imageViewUrediAdmina.setImageBitmap(bitmap);
                Toast.makeText(UrediAdminaActivity.this, "Slika spremljena u mapu " + IMAGE_DIRECTORY, Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = null;
            try {
                thumbnail = kreirajIspravnuSliku(this, imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(UrediAdminaActivity.this, "Neuspješno!", Toast.LENGTH_SHORT).show();
            }
            lokacija_slike = saveImage(thumbnail);
            imageViewUrediAdmina.setImageBitmap(thumbnail);
            Toast.makeText(UrediAdminaActivity.this, "Slika spremljena u mapu " + IMAGE_DIRECTORY, Toast.LENGTH_SHORT).show();
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
                    takePhotoFromCamera();
                }
                if (which == 1) {
                    //odabrana je opcija GALERIJA
                    choosePhotoFromGallary();
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

    public void choosePhotoFromGallary() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, GALLERY);
    }

    public void urediAdmina() {
        //String msg = "";
        String korisnicko_ime = urediKorisnickoIme.getText().toString();
        String lozinka = urediLozinku.getText().toString();


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
                Toast.makeText(UrediAdminaActivity.this, "Problemi prilikom konekcije!", Toast.LENGTH_SHORT).show();
            } else {
                if (korisnicko_ime.equals("") || lozinka.equals("")) {
                    Toast.makeText(UrediAdminaActivity.this, "Morate ispuniti sva polja!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(UrediAdminaActivity.this, "Podaci su ažurirani!", Toast.LENGTH_SHORT).show();
                    }


                }


            }
            connection.close();
        } catch (Exception e) {
            //msg = "Probllemi s konekcijom";
            Toast.makeText(UrediAdminaActivity.this, "Problemi s konekcijom!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        // poruka.setText(msg);
    }


    @OnClick(R.id.odaberi_uredi_sliku_btn)
    public void onOdaberiUrediSlikuBtnClicked() {
        prikaziDialog();
    }

    @OnClick(R.id.uredi_admina_btn)
    public void onUrediAdminaBtnClicked() {
        provjeriUnos(urediKorisnickoIme);
        provjeriUnos(urediLozinku);
        urediAdmina();
    }
}
