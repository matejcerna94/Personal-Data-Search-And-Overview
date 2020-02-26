package com.matejcerna;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.matejcerna.TraziOsobuActivity.osobe;

public class DetaljiOsobe2Activity extends AppCompatActivity {

    /* TextView ispisImenaPretrazeneOsobe;
     TextView ispisPrezimenaPretrazeneOsobe;
     TextView ispisAdresePretrazeneOsobe;
     TextView ispisSpolaPretrazeneOsobe;
     TextView ispisOibaPretrazeneOsobe;
     ImageView ispisSlikePretrazeneOsobe;
     TextView ispisDatumaRodenjaPretrazeneOsobe;
     TextView ispisGradaPretrazeneOsobe;
     TextView ispisMjestaRodenjaPretrazeneOsobe;
     TextView ispisZupanijePretrazeneOsobe;
     Button prebaciNaUrediOsobu;
     Button obrisiOsobu;*/
    int id;
    String msg;
    @BindView(R.id.ispis_slike_pretrazene_osobe)
    ImageView ispisSlikePretrazeneOsobe;
    @BindView(R.id.ispis_imena_pretrazene_osobe_txt)
    TextView ispisImenaPretrazeneOsobe;
    @BindView(R.id.ispis_prezimena_pretrazene_osobe_txt)
    TextView ispisPrezimenaPretrazeneOsobe;
    @BindView(R.id.ispis_adrese_pretrazene_osobe_txt)
    TextView ispisAdresePretrazeneOsobe;
    @BindView(R.id.ispis_spola_pretrazene_osobe_txt)
    TextView ispisSpolaPretrazeneOsobe;
    @BindView(R.id.ispis_oiba_pretrazene_osobe_txt)
    TextView ispisOibaPretrazeneOsobe;
    @BindView(R.id.ispis_mjesta_rodenja_pretrazene_osobe_txt)
    TextView ispisMjestaRodenjaPretrazeneOsobe;
    @BindView(R.id.ispis_datuma_rodenja_pretrazene_osobe_txt)
    TextView ispisDatumaRodenjaPretrazeneOsobe;
    @BindView(R.id.ispis_grada_pretrazene_osobe_txt)
    TextView ispisGradaPretrazeneOsobe;
    @BindView(R.id.ispis_zupanije_pretrazene_osobe_txt)
    TextView ispisZupanijePretrazeneOsobe;
    private boolean success = false;

    byte[] slika_iz_baze;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalji_osobe2);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        int position = getIntent().getExtras().getInt("key");

       /* ispisImenaPretrazeneOsobe = findViewById(R.id.ispis_imena_pretrazene_osobe_txt);
        ispisPrezimenaPretrazeneOsobe = findViewById(R.id.ispis_prezimena_pretrazene_osobe_txt);
        ispisAdresePretrazeneOsobe = findViewById(R.id.ispis_adrese_pretrazene_osobe_txt);
        ispisSpolaPretrazeneOsobe = findViewById(R.id.ispis_spola_pretrazene_osobe_txt);
        ispisOibaPretrazeneOsobe = findViewById(R.id.ispis_oiba_pretrazene_osobe_txt);
        ispisDatumaRodenjaPretrazeneOsobe = findViewById(R.id.ispis_datuma_rodenja_pretrazene_osobe_txt);
        ispisGradaPretrazeneOsobe = findViewById(R.id.ispis_grada_pretrazene_osobe_txt);
        ispisMjestaRodenjaPretrazeneOsobe = findViewById(R.id.ispis_mjesta_rodenja_pretrazene_osobe_txt);
        ispisZupanijePretrazeneOsobe = findViewById(R.id.ispis_zupanije_pretrazene_osobe_txt);
        ispisSlikePretrazeneOsobe = findViewById(R.id.ispis_slike_pretrazene_osobe);
        prebaciNaUrediOsobu = findViewById(R.id.uredi_pretrazenu_osobu_btn);
        obrisiOsobu = findViewById(R.id.obrisi_pretrazenu_osobu_btn);*/


        id = osobe.get(position).getId();

    }

    @Override
    protected void onStart() {
        super.onStart();
        dohvatiDetaljeOsobe(id);
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

                            ispisImenaPretrazeneOsobe.setText(ime);
                            ispisPrezimenaPretrazeneOsobe.setText(prezime);
                            ispisSpolaPretrazeneOsobe.setText(spol);
                            ispisAdresePretrazeneOsobe.setText(adresa);
                            ispisOibaPretrazeneOsobe.setText(oib);
                            ispisDatumaRodenjaPretrazeneOsobe.setText(datum_rodenja);
                            ispisGradaPretrazeneOsobe.setText(grad);
                            ispisMjestaRodenjaPretrazeneOsobe.setText(mjesto_rodenja);
                            ispisZupanijePretrazeneOsobe.setText(dohvatiNazivZupanije(zupanija_id));
                            final Bitmap slika_osobe = BitmapFactory.decodeByteArray(slika_iz_baze, 0, slika_iz_baze.length);
                            ispisSlikePretrazeneOsobe.setImageBitmap(slika_osobe);

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

    public void obrisiOsobu() {
        try {
            msg = "";
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(Baza.getDbUrl(), Baza.getUSER(), Baza.getPASS());

            if (connection == null) {
                success = false;
            } else {
                String upit = "DELETE FROM osobe WHERE id='" + id + "'";
                PreparedStatement preparedStatement = connection.prepareStatement(upit);
                preparedStatement.executeUpdate();
                Toast.makeText(DetaljiOsobe2Activity.this, "Osoba obrisana!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetaljiOsobe2Activity.this, PrikazOsobaActivity.class);
                startActivity(intent);

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DetaljiOsobe2Activity.this, TraziOsobuActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.uredi_pretrazenu_osobu_btn)
    public void onUrediPretrazenuOsobuBtnClicked() {
        Intent intent = new Intent(DetaljiOsobe2Activity.this, UrediOsobu3Activity.class);
        intent.putExtra("id", id);

        startActivity(intent);
    }

    @OnClick(R.id.obrisi_pretrazenu_osobu_btn)
    public void onObrisiPretrazenuOsobuBtnClicked() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetaljiOsobe2Activity.this);
        alertDialog.setMessage("Jeste li sigurni da želite obrisati osobu?").setCancelable(false)
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        obrisiOsobu();
                    }
                })
                .setNegativeButton("Ne", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialog.create();
        alert.setTitle("Upozorenje!");
        alert.show();
    }
}
