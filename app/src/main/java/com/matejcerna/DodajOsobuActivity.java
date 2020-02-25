package com.matejcerna;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
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
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;


public class DodajOsobuActivity extends AppCompatActivity {


    EditText unesiIme;
    EditText unesiPrezime;
    EditText unesiAdresu;
    EditText unesiOib;
    EditText unesiMjestoRodenja;
    EditText unesiGrad;
    Button dodajOsobu;
    Button odaberiSliku;
    ImageView imageViewSlikaOsobe;
    RadioGroup radioGroup;
    RadioButton radioButtonMusko;
    RadioButton radioButtonZensko;
    TextView textViewZupanija;
    String spol = "";
    byte[] slika_osobe;
    Button odaberiDatumRodenja;
    DatePickerDialog.OnDateSetListener datum_rodenja;
    TextView textViewDatumRodenja;
    private boolean success = false;
    ContentValues cv;
    Uri imageUri;

    ArrayList<Zupanija> listaZupanija = new ArrayList<>();
    SpinnerDialog spinnerDialog;
    ArrayAdapter<Zupanija> adapter;
    Button odaberiZupaniju;

    private static final int GALLERY = 1;
    private static final int CAMERA = 2;
    private static final String IMAGE_DIRECTORY = "/slike_iz_aplikacije";
    private String lokacija_slike = "";


    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;
    private static final int IMAGE_PICK_GALLERY_CODE = 1000;
    private static final int IMAGE_PICK_CAMERA_CODE = 1001;
    String cameraPermission[];
    String storagePermission[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_osobu);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioButtonMusko = (RadioButton) findViewById(R.id.radio_button_musko);
        radioButtonZensko = (RadioButton) findViewById(R.id.radio_button_zensko);

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        unesiIme = findViewById(R.id.unesi_ime_txt);
        unesiPrezime = findViewById(R.id.unesi_prezime_txt);
        unesiAdresu = findViewById(R.id.unesi_adresu_txt);
        unesiOib = findViewById(R.id.unesi_oib_txt);
        dodajOsobu = findViewById(R.id.dodaj_osobu_btn);
        odaberiSliku = findViewById(R.id.odaberi_sliku_btn);
        odaberiDatumRodenja = findViewById(R.id.odaberi_datum_rodenja_btn);
        textViewDatumRodenja = findViewById(R.id.text_view_datum_rodenja);
        imageViewSlikaOsobe = findViewById(R.id.image_view_slika_osobe);
        textViewZupanija = findViewById(R.id.text_view_zupanija);
        odaberiZupaniju = findViewById(R.id.odaberi_zupaniju_btn);
        unesiMjestoRodenja = findViewById(R.id.unesi_mjesto_rodenja_txt);
        unesiGrad = findViewById(R.id.unesi_grad_txt);




        IzlistajZupanije izlistajZupanije = new IzlistajZupanije();
        izlistajZupanije.execute();

        spinnerDialog = new SpinnerDialog(DodajOsobuActivity.this, listaZupanija, "Odaberite zupaniju");
        spinnerDialog.bindOnSpinerListener(new OnSpinnerItemClick() {
            @Override
            public void onClick(String zupanija, int position) {
                textViewZupanija.setText(zupanija);
            }
        });

        odaberiZupaniju.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
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
                        DodajOsobuActivity.this,
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
                    textViewDatumRodenja.setText(datum_rodenja);
                }
                if (dan > 9 && mjesec < 10) {
                    String datum = dan + ".0" + mjesec + "." + godina;
                    textViewDatumRodenja.setText(datum);
                }
                if (dan > 9 && mjesec > 9) {
                    String datum = dan + "." + mjesec + "." + godina;
                    textViewDatumRodenja.setText(datum);
                }
                if (dan < 10 && mjesec < 10) {
                    String datum = "0" + dan + ".0" + mjesec + "." + godina;
                    textViewDatumRodenja.setText(datum);

                }
            }
        };

        odaberiSliku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prikaziDialog();
            }
        });


        dodajOsobu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provjeriUnos(unesiIme);
                provjeriUnos(unesiPrezime);
                provjeriUnos(unesiAdresu);
                provjeriUnos(unesiOib);
                provjeriUnos(unesiGrad);
                provjeriUnos(unesiMjestoRodenja);
                provjeriUnos2(textViewZupanija);
                provjeriUnos2(textViewDatumRodenja);
                dodajOsobu();


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
                    Toast.makeText(DodajOsobuActivity.this, "Neuspješno!", Toast.LENGTH_SHORT).show();
                }
                lokacija_slike = saveImage(bitmap);
                imageViewSlikaOsobe.setImageBitmap(bitmap);
                Toast.makeText(DodajOsobuActivity.this, "Slika spremljena u mapu " + IMAGE_DIRECTORY, Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == CAMERA) {

            Bitmap thumbnail = null;
            try {
                thumbnail = kreirajIspravnuSliku(this, imageUri);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(DodajOsobuActivity.this, "Neuspješno!", Toast.LENGTH_SHORT).show();
            }
            lokacija_slike = saveImage(thumbnail);
            imageViewSlikaOsobe.setImageBitmap(thumbnail);
            Toast.makeText(DodajOsobuActivity.this, "Slika spremljena u mapu " + IMAGE_DIRECTORY, Toast.LENGTH_SHORT).show();
        }


    }

    public static boolean provjeriUnos(EditText text) {
        String provjeri = text.getText().toString();

        if (provjeri.isEmpty()) {
            text.setError("Ovo polje ne može biti prazno!");
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    public static boolean provjeriUnos2(TextView text) {
        String provjeri = text.getText().toString();

        if (provjeri.isEmpty()) {
            text.setError("Ovo polje ne može biti prazno!");
            return false;
        } else {
            text.setError(null);
            return true;
        }
    }

    public static Bitmap kreirajIspravnuSliku(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = izracunajDimenzijeSlike(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = zarotirajSlikuAkoTreba(context, img, selectedImage);
        return img;
    }

    private static int izracunajDimenzijeSlike(BitmapFactory.Options options,
                                               int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap zarotirajSlikuAkoTreba(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return zarotirajSliku(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return zarotirajSliku(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return zarotirajSliku(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap zarotirajSliku(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }


    private void dodajOsobu() {
        //String msg = "";
        String ime = unesiIme.getText().toString();
        String prezime = unesiPrezime.getText().toString();
        String adresa = unesiAdresu.getText().toString();
        String oib = unesiOib.getText().toString();
        String datum_rodenja = textViewDatumRodenja.getText().toString();
        String mjesto_rodenja = unesiMjestoRodenja.getText().toString();
        String grad = unesiGrad.getText().toString();
        int id_zupanije = dohvatiIdZupanije(textViewZupanija.getText().toString());
        provjeriSpol();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(Baza.getDbUrl(), Baza.getUSER(), Baza.getPASS());
            if (connection == null) {
                //msg = "Problemi prilikom konekcije";
                Toast.makeText(DodajOsobuActivity.this, "Problemi prilikom konekcije!", Toast.LENGTH_SHORT).show();
            } else {
                if (ime.equals("") || prezime.equals("") || adresa.equals("") || oib.equals("") || grad.equals("") || mjesto_rodenja.equals("")) {
                    Toast.makeText(DodajOsobuActivity.this, "Morate ispuniti sva polja!", Toast.LENGTH_SHORT).show();
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
                                if (slika_osobe == null) {
                                    Toast.makeText(this, "Morate odabrati sliku!", Toast.LENGTH_SHORT).show();
                                } else {
                                    String upit = "INSERT INTO osobe(id, ime, prezime, spol, adresa, oib, datum_rodenja, grad, mjesto_rodenja, slika, zupanija_id) VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                                    preparedStatement.executeUpdate();
                                    Toast.makeText(DodajOsobuActivity.this, "Osoba dodana!", Toast.LENGTH_SHORT).show();

                                    unesiIme.setText("");
                                    unesiPrezime.setText("");
                                    unesiAdresu.setText("");
                                    unesiOib.setText("");
                                    unesiMjestoRodenja.setText("");
                                    unesiGrad.setText("");
                                    textViewDatumRodenja.setText("");
                                    textViewZupanija.setText("");
                                    radioButtonMusko.setChecked(false);
                                    radioButtonZensko.setChecked(false);
                                    radioGroup.clearCheck();
                                    imageViewSlikaOsobe.setImageBitmap(null);
                                    slika_osobe = null;
                                }
                            }
                        }

                    }
                }


            }
            connection.close();
        } catch (Exception e) {
            Toast.makeText(DodajOsobuActivity.this, "Problemi s konekcijom!", Toast.LENGTH_SHORT).show();
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


    private void provjeriSpol() {
        if (radioGroup.getCheckedRadioButtonId() == radioButtonMusko.getId()) {
            spol = "Muško";
        } else if (radioGroup.getCheckedRadioButtonId() == radioButtonZensko.getId()) {
            spol = "Žensko";
        }
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
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File file = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            slika_osobe = bytes.toByteArray();
            MediaScannerConnection.scanFile(this,
                    new String[]{file.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            // lokacija_slike = file.getAbsolutePath();
            // Log.d("TAG--------------", lokacija_slike);


            return file.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
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
