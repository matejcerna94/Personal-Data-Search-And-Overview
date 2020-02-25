package com.matejcerna;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Calendar;

import static com.matejcerna.DodajOsobuActivity.kreirajIspravnuSliku;
import static com.matejcerna.DodajOsobuActivity.provjeriUnos2;
import static com.matejcerna.PretraziOsobuZaUredivanje.osobe;


public class UrediOsobu2Activity extends AppCompatActivity {

    EditText urediImePretrazeneOsobe;
    EditText urediPrezimePretrazeneOsobe;
    EditText urediAdresuPretrazeneOsobe;
    EditText urediOibPretrazeneOsobe;
    EditText urediGradPretrazeneOsobe;
    EditText urediMjestoRodenjaPretrazene;
    TextView urediZupanijuPretrazeneOsobe;
    TextView urediDatumRodenjaPretrazeneOsobe;
    Button urediPretrazenuOsobu;
    Button odaberiDatumRodenja;
    Button odaberiZupaniju;
    Button odaberiSliku;
    RadioButton urediRadioButtonMuskoPretrazeneOsobe;
    RadioButton urediRadioButtonZenskoPretrazeneOsobe;
    RadioGroup radioGroup;
    ImageView imageViewUrediSlikuPretrazeneOsobe;
    String spol = "";
    private boolean success = false;
    DatePickerDialog.OnDateSetListener datum_rodenja;
    SpinnerDialog spinnerDialog;
    ArrayList<Zupanija> listaZupanija = new ArrayList<>();


    byte[] slika_iz_baze;
    byte[] slika_nova;
    byte[] slika_osobe;
    private String lokacija_slike = "";
    Uri imageUri;
    ContentValues cv;

    private int GALLERY = 1;
    private int CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/slike_iz_aplikacije";

    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    String cameraPermission[];
    String storagePermission[];


    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uredi_osobu2);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        int position = getIntent().getExtras().getInt("key");

        urediImePretrazeneOsobe=findViewById(R.id.uredi_ime_pretrazene_osobe_txt);
        urediPrezimePretrazeneOsobe=findViewById(R.id.uredi_prezime_pretrazene_osobe_txt);
        urediAdresuPretrazeneOsobe=findViewById(R.id.uredi_adresu_pretrazene_osobe_txt);
        urediOibPretrazeneOsobe=findViewById(R.id.uredi_oib_pretrazene_osobe_txt);
        urediDatumRodenjaPretrazeneOsobe=findViewById(R.id.uredi_datum_rodenja_pretrazene_osobe_txt);
        urediMjestoRodenjaPretrazene=findViewById(R.id.uredi_mjesto_rodenja_pretrazene_osobe_txt);
        urediZupanijuPretrazeneOsobe=findViewById(R.id.uredi_zupaniju_pretrazene_osobe_txt);
        urediGradPretrazeneOsobe=findViewById(R.id.uredi_grad_pretrazene_osobe_txt);
        odaberiDatumRodenja = findViewById(R.id.odaberi_uredi_datum_rodenja_btn);
        odaberiZupaniju = findViewById(R.id.odaberi_uredi_zupaniju_pretrazene_osobe_btn);
        odaberiSliku = findViewById(R.id.odaberi_sliku_btn);
        urediRadioButtonMuskoPretrazeneOsobe=findViewById(R.id.uredi_radio_button_musko_pretrazene_osobe);
        urediRadioButtonZenskoPretrazeneOsobe=findViewById(R.id.uredi_radio_button_zensko_pretrazene_osobe);
        urediPretrazenuOsobu=findViewById(R.id.uredi_pretrazenu_osobu_btn);
        radioGroup=findViewById(R.id.radio_group_uredivanje_osobe);
        imageViewUrediSlikuPretrazeneOsobe = findViewById(R.id.image_view_uredi_sliku_pretrazene_osobe);




        id=osobe.get(position).getId();
        dohvatiDetaljeOsobe(id);
        /*urediImePretrazeneOsobe.setText(osobe.get(position).getIme());
        urediPrezimePretrazeneOsobe.setText(osobe.get(position).getPrezime());
        urediAdresuPretrazeneOsobe.setText(osobe.get(position).getAdresa());
        urediOibPretrazeneOsobe.setText(osobe.get(position).getOib());
        urediGradPretrazeneOsobe.setText(osobe.get(position).getGrad());
        urediMjestoRodenjaPretrazene.setText(osobe.get(position).getMjesto_rodenja());
        urediDatumRodenjaPretrazeneOsobe.setText(osobe.get(position).getDatum_rodenja());
        urediZupanijuPretrazeneOsobe.setText(dohvatiNazivZupanije(osobe.get(position).getZupanija_id()));
        slika = osobe.get(position).getSlika();
        Bitmap slika_osobe= BitmapFactory.decodeByteArray(slika,0,slika.length);
        imageViewUrediSlikuPretrazeneOsobe.setImageBitmap(slika_osobe);*/

        odaberiSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prikaziDialog();
            }
        });

        odaberiDatumRodenja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int godina = calendar.get(Calendar.YEAR);
                int mjesec = calendar.get(Calendar.MONTH);
                int dan = calendar.get(Calendar.DAY_OF_WEEK);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        UrediOsobu2Activity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        datum_rodenja,
                        godina, mjesec, dan);

                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        datum_rodenja = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int godina, int mjesec, int dan) {
                mjesec = mjesec + 1;

                if (dan < 10 && mjesec > 9) {
                    String datum_rodenja = "0" + dan + "." + mjesec + "." + godina;
                    urediDatumRodenjaPretrazeneOsobe.setText(datum_rodenja);
                }
                if (dan > 9 && mjesec < 10) {
                    String datum_rodenja = dan + ".0" + mjesec + "." + godina;
                    urediDatumRodenjaPretrazeneOsobe.setText(datum_rodenja);
                }
                if (dan > 9 && mjesec > 9) {
                    String datum_rodenja = dan + "." + mjesec + "." + godina;
                    urediDatumRodenjaPretrazeneOsobe.setText(datum_rodenja);
                }
                if (dan < 10 && mjesec < 10) {
                    String datum_rodenja = "0" + dan + ".0" + mjesec + "." + godina;
                    urediDatumRodenjaPretrazeneOsobe.setText(datum_rodenja);

                }
            }
        };

        IzlistajZupanije izlistajZupanije = new IzlistajZupanije();
        izlistajZupanije.execute();

        spinnerDialog = new SpinnerDialog(UrediOsobu2Activity.this, listaZupanija, "Odaberite zupaniju");
        spinnerDialog.bindOnSpinerListener(new OnSpinnerItemClick() {
            @Override
            public void onClick(String zupanija, int position) {
                urediZupanijuPretrazeneOsobe.setText(zupanija);
            }
        });

        odaberiZupaniju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });



      /*   osobe.get(position).getSpol();

        if(osobe.get(position).getSpol().equals("Muško")){
            urediRadioButtonMuskoPretrazeneOsobe.setChecked(true);
        }else{
            urediRadioButtonZenskoPretrazeneOsobe.setChecked(true);
        }*/

        urediPretrazenuOsobu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provjeriUnos(urediImePretrazeneOsobe);
                provjeriUnos(urediPrezimePretrazeneOsobe);
                provjeriUnos(urediAdresuPretrazeneOsobe);
                provjeriUnos(urediOibPretrazeneOsobe);
                provjeriUnos(urediGradPretrazeneOsobe);
                provjeriUnos(urediMjestoRodenjaPretrazene);
                provjeriUnos2(urediZupanijuPretrazeneOsobe);
                provjeriUnos2(urediDatumRodenjaPretrazeneOsobe);
                urediOsobu();
            }
        });
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
                    Toast.makeText(UrediOsobu2Activity.this, "Neuspješno!", Toast.LENGTH_SHORT).show();
                }
                lokacija_slike = saveImage(bitmap);
                imageViewUrediSlikuPretrazeneOsobe.setImageBitmap(bitmap);
                Toast.makeText(UrediOsobu2Activity.this, "Slika spremljena u mapu " + IMAGE_DIRECTORY, Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = null;
            try {
                thumbnail = kreirajIspravnuSliku(this, imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(UrediOsobu2Activity.this, "Neuspješno!", Toast.LENGTH_SHORT).show();
            }
            lokacija_slike = saveImage(thumbnail);
            imageViewUrediSlikuPretrazeneOsobe.setImageBitmap(thumbnail);
            Toast.makeText(UrediOsobu2Activity.this, "Slika spremljena u mapu " + IMAGE_DIRECTORY, Toast.LENGTH_SHORT).show();
        }
    }

    public void dohvatiDetaljeOsobe(int id_osobe) {
        String ime;
        String prezime;
        String spol;
        String adresa;
        String oib;
        String datum_rodenja;
        String grad;
        String mjesto_rodenja;
        slika_iz_baze = null;
        int zupanija_id;
        String msg = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(Baza.getDbUrl(), Baza.getUSER(), Baza.getPASS());

            if (connection == null) {
                success = false;
            } else {
                String upit = "SELECT * FROM osobe WHERE id = '" + id_osobe + "'";
                PreparedStatement preparedStatement = connection.prepareStatement(upit);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next() == false) {
                    msg = "Nema podataka u bazi!";
                    success = false;
                } else {
                    do {
                        try {
                            osobe.add(new Osoba(
                                    resultSet.getInt("id"),
                                    ime = resultSet.getString("ime"),
                                    prezime = resultSet.getString("prezime"),
                                    spol = resultSet.getString("spol"),
                                    adresa = resultSet.getString("adresa"),
                                    oib = resultSet.getString("oib"),
                                    datum_rodenja = resultSet.getString("datum_rodenja"),
                                    grad = resultSet.getString("grad"),
                                    mjesto_rodenja = resultSet.getString("mjesto_rodenja"),
                                    slika_iz_baze = resultSet.getBytes("slika"),
                                    zupanija_id = resultSet.getInt("zupanija_id")));

                            urediImePretrazeneOsobe.setText(ime);
                            urediPrezimePretrazeneOsobe.setText(prezime);
                            if (spol.equals("Muško")) {
                                urediRadioButtonMuskoPretrazeneOsobe.setChecked(true);
                            } else {
                                urediRadioButtonZenskoPretrazeneOsobe.setChecked(true);
                            }
                            urediAdresuPretrazeneOsobe.setText(adresa);
                            urediOibPretrazeneOsobe.setText(oib);
                            urediDatumRodenjaPretrazeneOsobe.setText(datum_rodenja);
                            urediGradPretrazeneOsobe.setText(grad);
                            urediMjestoRodenjaPretrazene.setText(mjesto_rodenja);
                            urediZupanijuPretrazeneOsobe.setText(dohvatiNazivZupanije(zupanija_id));
                            final Bitmap slika_osobe = BitmapFactory.decodeByteArray(slika_iz_baze, 0, slika_iz_baze.length);
                            imageViewUrediSlikuPretrazeneOsobe.setImageBitmap(slika_osobe);

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
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, GALLERY);
    }

    private boolean provjeriUnos(EditText text) {
        String provjeri = text.getText().toString();

        if (provjeri.isEmpty()) {
            text.setError("Ovo polje ne može biti prazno!");
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    private void urediOsobu() {
        //String msg = "";
        String ime = urediImePretrazeneOsobe.getText().toString();
        String prezime = urediPrezimePretrazeneOsobe.getText().toString();
        String adresa = urediAdresuPretrazeneOsobe.getText().toString();
        String oib = urediOibPretrazeneOsobe.getText().toString();
        String mjesto_rodenja = urediMjestoRodenjaPretrazene.getText().toString();
        String datum_rodenja = urediDatumRodenjaPretrazeneOsobe.getText().toString();
        String grad = urediGradPretrazeneOsobe.getText().toString();
        int id_zupanije = dohvatiIdZupanije(urediZupanijuPretrazeneOsobe.getText().toString());
        provjeriSpol();


        if (slika_nova == null) {
            slika_osobe = slika_iz_baze;
        } else {
            slika_osobe = slika_nova;
        }

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(Baza.getDbUrl(), Baza.getUSER(), Baza.getPASS());
            if (connection == null) {
                //msg = "Problemi prilikom konekcije";
                Toast.makeText(UrediOsobu2Activity.this, "Problemi prilikom konekcije!", Toast.LENGTH_SHORT).show();
            } else {
                if (ime.equals("") || prezime.equals("") || adresa.equals("") || oib.equals("") || grad.equals("") || mjesto_rodenja.equals("")) {
                    Toast.makeText(UrediOsobu2Activity.this, "Morate ispuniti sva polja!", Toast.LENGTH_SHORT).show();
                } else {
                    if (radioGroup.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(this, "Morate odabrati spol!", Toast.LENGTH_SHORT).show();
                    } else {
                        if(datum_rodenja.equals("")){
                            Toast.makeText(this, "Morate odabrati datum rođenja!", Toast.LENGTH_SHORT).show();
                        }else{
                            if(id_zupanije == 0){
                                Toast.makeText(this, "Morate odabrati županiju!", Toast.LENGTH_SHORT).show();
                            }else{

                                String upit = "UPDATE osobe SET ime= ?, prezime= ?, spol= ?, adresa= ?, oib= ?, datum_rodenja= ?, grad= ?, mjesto_rodenja= ?, slika= ?, zupanija_id= ?  WHERE id= ?";
                                PreparedStatement preparedStatement = connection.prepareStatement(upit);


                                preparedStatement.setString(1, ime);
                                preparedStatement.setString(2, prezime);
                                preparedStatement.setString(3, spol);
                                preparedStatement.setString(4, adresa);
                                preparedStatement.setString(5, oib);
                                preparedStatement.setString(6, datum_rodenja);
                                preparedStatement.setString(7, grad);
                                preparedStatement.setString(8, mjesto_rodenja);
                                preparedStatement.setBytes(9, slika_osobe);
                                preparedStatement.setInt(10, id_zupanije);
                                preparedStatement.setInt(11, id);
                                preparedStatement.executeUpdate();
                                Toast.makeText(UrediOsobu2Activity.this, "Podaci su ažurirani!", Toast.LENGTH_SHORT).show();


                            }
                        }

                    }
                }


            }
            connection.close();
        } catch (Exception e) {
            Toast.makeText(UrediOsobu2Activity.this, "Problemi s konekcijom!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

    private void provjeriSpol() {
        if (radioGroup.getCheckedRadioButtonId() == urediRadioButtonMuskoPretrazeneOsobe.getId()) {
            spol = "Muško";
        } else if (radioGroup.getCheckedRadioButtonId() == urediRadioButtonZenskoPretrazeneOsobe.getId()) {
            spol = "Žensko";
        }
    }

    public String dohvatiNazivZupanije(int id_zupanije) {
        String naziv_zupanije = "";
        String msg = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(Baza.getDbUrl(), Baza.getUSER(), Baza.getPASS());

            if (connection == null) {
                success = false;
            } else {
                String upit = "SELECT ime_zupanije FROM zupanija WHERE id = '" + id_zupanije + "'";
                PreparedStatement preparedStatement = connection.prepareStatement(upit);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next() == false) {
                    msg = "Nema podataka u bazi!";
                    success = false;
                } else {
                    do {
                        try {
                            naziv_zupanije = resultSet.getString("ime_zupanije");
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

        return naziv_zupanije;
    }

    private class IzlistajZupanije extends AsyncTask<String, String, String> {
        ArrayList<Zupanija> zupanije;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            zupanije = new ArrayList<>();

        }

        @Override
        protected String doInBackground(String... strings) {
            String msg = "";
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(Baza.getDbUrl(), Baza.getUSER(), Baza.getPASS());

                if (connection == null) {
                    success = false;
                } else {
                    String upit = "SELECT * FROM zupanija";
                    PreparedStatement preparedStatement = connection.prepareStatement(upit);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next() == false) {
                        msg = "Nema podataka u bazi!";
                        success = false;
                    } else {
                        do {
                            try {
                                zupanije.add(new Zupanija(
                                        resultSet.getInt("id"),
                                        resultSet.getString("ime_zupanije")));
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
            return msg;
        }

        @Override
        protected void onPostExecute(String result) {
            listaZupanija.addAll(zupanije);
//            adapter.notifyDataSetChanged();
        }
    }

    public int dohvatiIdZupanije(String naziv_zupanije) {
        int id_zupanije = 0;
        String msg = "";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(Baza.getDbUrl(), Baza.getUSER(), Baza.getPASS());

            if (connection == null) {
                success = false;
            } else {
                String upit = "SELECT id FROM zupanija WHERE ime_zupanije = '" + naziv_zupanije + "'";
                PreparedStatement preparedStatement = connection.prepareStatement(upit);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next() == false) {
                    msg = "Nema podataka u bazi!";
                    success = false;
                } else {
                    do {
                        try {
                            id_zupanije = resultSet.getInt("id");
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

        return id_zupanije;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UrediOsobu2Activity.this, PretraziOsobuZaUredivanje.class);
        startActivity(intent);
    }
}
