package com.matejcerna;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import static com.matejcerna.DodajOsobuActivity.provjeriUnos;
import static com.matejcerna.DodajOsobuActivity.provjeriUnos2;
import static com.matejcerna.PrikazOsobaActivity.osobe;


public class UrediOsobuActivity extends AppCompatActivity {

    EditText urediIme;
    EditText urediPrezime;
    EditText urediAdresu;
    EditText urediOib;
    EditText urediGrad;
    EditText urediMjestoRodenja;
    TextView urediZupaniju;
    TextView urediDatumRodenja;
    Button odaberiZupaniju;
    Button urediOsobu;
    Button odaberiSliku;
    Button odaberiDatumRodenja;
    RadioButton urediRadioButtonMusko;
    RadioButton urediRadioButtonZensko;
    RadioGroup radioGroup;
    ImageView imageViewUrediOsobu;
    String spol = "";
    DatePickerDialog.OnDateSetListener datum_rodenja;
    private boolean success = false;
    ArrayList<Zupanija> listaZupanija = new ArrayList<>();
    SpinnerDialog spinnerDialog;
    byte[] slika_nova;
    byte[] slika_osobe;
    byte[] slika_iz_baze;
    ContentValues cv;

    private int GALLERY = 1;
    private int CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/slike_iz_aplikacije";
    private String lokacija_slike = "";
    Uri imageUri;


    int id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uredi_osobu);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //int position = getIntent().getExtras().getInt("key");

        urediIme = findViewById(R.id.uredi_ime_txt);
        urediPrezime = findViewById(R.id.uredi_prezime_txt);
        urediAdresu = findViewById(R.id.uredi_adresu_txt);
        urediOib = findViewById(R.id.uredi_oib_txt);
        urediGrad = findViewById(R.id.uredi_grad_txt);
        urediZupaniju = findViewById(R.id.uredi_zupaniju_txt);
        odaberiZupaniju = findViewById(R.id.odaberi_uredi_zupaniju_btn);
        urediMjestoRodenja = findViewById(R.id.uredi_mjesto_rodenja_txt);
        odaberiSliku = findViewById(R.id.odaberi_uredi_sliku_btn);
        odaberiDatumRodenja = findViewById(R.id.odaberi_uredi_datum_rodenja_btn);
        urediDatumRodenja = findViewById(R.id.uredi_datum_rodenja_txt);
        urediRadioButtonMusko = findViewById(R.id.uredi_radio_button_musko);
        urediRadioButtonZensko = findViewById(R.id.uredi_radio_button_zensko);
        urediOsobu = findViewById(R.id.uredi_osobu_btn);
        radioGroup = findViewById(R.id.radio_group_uredi_osobu);
        imageViewUrediOsobu = findViewById(R.id.image_view_uredi_osobu);



        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);



        /*Bundle extras = getIntent().getExtras();

        if(extras.containsKey("id_iz_detalji_osobe")){      //provjerava se sadrzi li extras kljuc pod nazivom "id", odnosno iz aktivnosti PrikazOsobaActivity
            id = extras.getInt("id_iz_detalji_osobe");
        }

        else if(extras.containsKey("id_iz_detalji_osobe2")){
            id= extras.getInt("id"); // ovo je iz aktivnosti PretraziOsobuZaUredivanjeActivity
        }*/

        dohvatiDetaljeOsobe(id);





       /* urediIme.setText(intent.getStringExtra("ime"));
        urediPrezime.setText(intent.getStringExtra("prezime"));
        urediAdresu.setText(intent.getStringExtra("adresa"));
        urediOib.setText(intent.getStringExtra("oib"));
        urediDatumRodenja.setText(intent.getStringExtra("datum_rodenja"));
        urediMjestoRodenja.setText(intent.getStringExtra("mjesto_rodenja"));
        urediZupaniju.setText(intent.getStringExtra("zupanija"));
        urediGrad.setText(intent.getStringExtra("grad"));

        slika = intent.getByteArrayExtra("slika");
        Bitmap slika_osobe= BitmapFactory.decodeByteArray(slika,0,slika.length);
        imageViewUrediOsobu.setImageBitmap(slika_osobe);

        String spol = intent.getStringExtra(("spol"));

        if(spol.equals("Muško")){
            urediRadioButtonMusko.setChecked(true);
        }else{
            urediRadioButtonZensko.setChecked(true);
        }*/

        odaberiDatumRodenja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int godina = calendar.get(Calendar.YEAR);
                int mjesec = calendar.get(Calendar.MONTH);
                int dan = calendar.get(Calendar.DAY_OF_WEEK);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        UrediOsobuActivity.this,
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
                    urediDatumRodenja.setText(datum_rodenja);
                }
                if (dan > 9 && mjesec < 10) {
                    String datum = dan + ".0" + mjesec + "." + godina;
                    urediDatumRodenja.setText(datum);
                }
                if (dan > 9 && mjesec > 9) {
                    String datum = dan + "." + mjesec + "." + godina;
                    urediDatumRodenja.setText(datum);
                }
                if (dan < 10 && mjesec < 10) {
                    String datum = "0" + dan + ".0" + mjesec + "." + godina;
                    urediDatumRodenja.setText(datum);

                }
            }
        };



        IzlistajZupanije izlistajZupanije = new IzlistajZupanije();
        izlistajZupanije.execute();

        spinnerDialog = new SpinnerDialog(UrediOsobuActivity.this, listaZupanija, "Odaberite zupaniju");
        spinnerDialog.bindOnSpinerListener(new OnSpinnerItemClick() {
            @Override
            public void onClick(String zupanija, int position) {
                urediZupaniju.setText(zupanija);
            }
        });

        odaberiZupaniju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });

        odaberiSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prikaziDialog();
            }
        });


        urediOsobu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provjeriUnos(urediIme);
                provjeriUnos(urediPrezime);
                provjeriUnos(urediAdresu);
                provjeriUnos(urediOib);
                provjeriUnos(urediGrad);
                provjeriUnos(urediMjestoRodenja);
                provjeriUnos2(urediZupaniju);
                provjeriUnos2(urediDatumRodenja);
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
                    Toast.makeText(UrediOsobuActivity.this, "Neuspješno!", Toast.LENGTH_SHORT).show();
                }
                lokacija_slike = saveImage(bitmap);
                imageViewUrediOsobu.setImageBitmap(bitmap);
                Toast.makeText(UrediOsobuActivity.this, "Slika spremljena u mapu " + IMAGE_DIRECTORY, Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = null;
            try {
                thumbnail = kreirajIspravnuSliku(this, imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(UrediOsobuActivity.this, "Neuspješno!", Toast.LENGTH_SHORT).show();
            }
            lokacija_slike = saveImage(thumbnail);
            imageViewUrediOsobu.setImageBitmap(thumbnail);
            Toast.makeText(UrediOsobuActivity.this, "Slika spremljena u mapu " + IMAGE_DIRECTORY, Toast.LENGTH_SHORT).show();
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

                            urediIme.setText(ime);
                            urediPrezime.setText(prezime);
                            if (spol.equals("Muško")) {
                                urediRadioButtonMusko.setChecked(true);
                            } else {
                                urediRadioButtonZensko.setChecked(true);
                            }
                            urediAdresu.setText(adresa);
                            urediOib.setText(oib);
                            urediDatumRodenja.setText(datum_rodenja);
                            urediGrad.setText(grad);
                            urediMjestoRodenja.setText(mjesto_rodenja);
                            urediZupaniju.setText(dohvatiNazivZupanije(zupanija_id));
                            final Bitmap slika_osobe = BitmapFactory.decodeByteArray(slika_iz_baze, 0, slika_iz_baze.length);
                            imageViewUrediOsobu.setImageBitmap(slika_osobe);

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
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, GALLERY);
    }

   /* private boolean provjeriUnos(EditText text) {
        String provjeri = text.getText().toString();

        if (provjeri.isEmpty()) {
            text.setError("Ovo polje ne može biti prazno!");
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }*/

    private void urediOsobu() {
        //String msg = "";
        String ime = urediIme.getText().toString();
        String prezime = urediPrezime.getText().toString();
        String adresa = urediAdresu.getText().toString();
        String oib = urediOib.getText().toString();
        String mjesto_rodenja = urediMjestoRodenja.getText().toString();
        String datum_rodenja = urediDatumRodenja.getText().toString();
        String grad = urediGrad.getText().toString();
        int id_zupanije = dohvatiIdZupanije(urediZupaniju.getText().toString());
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
                Toast.makeText(UrediOsobuActivity.this, "Problemi prilikom konekcije!", Toast.LENGTH_SHORT).show();
            } else {
                if (ime.equals("") || prezime.equals("") || adresa.equals("") || oib.equals("") || grad.equals("") || mjesto_rodenja.equals("")) {
                    Toast.makeText(UrediOsobuActivity.this, "Morate ispuniti sva polja!", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(UrediOsobuActivity.this, "Podaci su ažurirani!", Toast.LENGTH_SHORT).show();


                            }
                        }

                    }
                }


            }
            connection.close();
        } catch (Exception e) {
            Toast.makeText(UrediOsobuActivity.this, "Problemi s konekcijom!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }


    }

  /* public void urediOsobu() {
        //String msg = "";
        String ime = urediIme.getText().toString();
        String prezime = urediPrezime.getText().toString();
        String adresa = urediAdresu.getText().toString();
        String oib = urediOib.getText().toString();
        String mjesto_rodenja = urediMjestoRodenja.getText().toString();
        String datum_rodenja = urediDatumRodenja.getText().toString();
        String grad = urediGrad.getText().toString();
        int id_zupanije = dohvatiIdZupanije(urediZupaniju.getText().toString());
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
                Toast.makeText(UrediOsobuActivity.this, "Problemi prilikom konekcije!", Toast.LENGTH_SHORT).show();
            } else {
                if (ime.equals("") || prezime.equals("") || adresa.equals("") || oib.equals("") || grad.equals("") || mjesto_rodenja.equals("")) {
                    Toast.makeText(UrediOsobuActivity.this, "Morate ispuniti sva polja!", Toast.LENGTH_SHORT).show();
                } else {
                    if (radioGroup.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(this, "Morate odabrati spol!", Toast.LENGTH_SHORT).show();
                    } else {
                        if (datum_rodenja.equals("")) {
                            Toast.makeText(this, "Morate odabrati datum rođenja!", Toast.LENGTH_SHORT).show();
                        } else {
                            if (id_zupanije == 0) {
                                Toast.makeText(this, "Morate odabrati županiju!", Toast.LENGTH_SHORT).show();
                            } else {
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
                                // msg = "Osoba dodana";
                                Toast.makeText(UrediOsobuActivity.this, "Podaci su ažurirani!", Toast.LENGTH_SHORT).show();


                            }


                        }
                        connection.close();
                    } catch(Exception e){
                        //msg = "Probllemi s konekcijom";
                        Toast.makeText(UrediOsobuActivity.this, "Problemi s konekcijom!", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    // poruka.setText(msg);
                }
            }
        }
    }*/

    private void provjeriSpol() {
        if (radioGroup.getCheckedRadioButtonId() == urediRadioButtonMusko.getId()) {
            spol = "Muško";
        } else if (radioGroup.getCheckedRadioButtonId() == urediRadioButtonZensko.getId()) {
            spol = "Žensko";
        }
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

}
