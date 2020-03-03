package com.matejcerna.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.matejcerna.R;
import com.matejcerna.database.Baza;
import com.matejcerna.model.Osoba;

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

import static com.matejcerna.activity.PrikazOsobaActivity.osobe;


public class DetaljiOsobeActivity extends AppCompatActivity {

    /*TextView ispisImena;
    TextView ispisPrezimena;
    TextView ispisAdrese;
    TextView ispisSpola;
    TextView ispisOiba;
    TextView ispisDatumaRodenja;
    TextView ispisGrada;
    TextView ispisMjestaRodenja;
    TextView ispisZupanije;
    ImageView ispisSlikeOsobe;
    Button prebaciNaUrediOsobu;
    Button obrisiOsobu;*/
    int id;
    byte[] slika_iz_baze;
    String msg;
    @BindView(R.id.ispis_slike_osobe)
    ImageView ispisSlikeOsobe;
    @BindView(R.id.ispis_imena_txt)
    TextView ispisImena;
    @BindView(R.id.ispis_prezimena_txt)
    TextView ispisPrezimena;
    @BindView(R.id.ispis_adrese_txt)
    TextView ispisAdrese;
    @BindView(R.id.ispis_spola_txt)
    TextView ispisSpola;
    @BindView(R.id.ispis_oiba_txt)
    TextView ispisOiba;
    @BindView(R.id.ispis_mjesta_rodenja_txt)
    TextView ispisMjestaRodenja;
    @BindView(R.id.ispis_datuma_rodenja_txt)
    TextView ispisDatumaRodenja;
    @BindView(R.id.ispis_grada_txt)
    TextView ispisGrada;
    @BindView(R.id.ispis_zupanije_txt)
    TextView ispisZupanije;
    private boolean success = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalji_osobe);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        int position = getIntent().getExtras().getInt("key");

        /*ispisImena = findViewById(R.id.ispis_imena_txt);
        ispisPrezimena = findViewById(R.id.ispis_prezimena_txt);
        ispisAdrese = findViewById(R.id.ispis_adrese_txt);
        ispisSpola = findViewById(R.id.ispis_spola_txt);
        ispisOiba = findViewById(R.id.ispis_oiba_txt);
        ispisGrada = findViewById(R.id.ispis_grada_txt);
        ispisMjestaRodenja = findViewById(R.id.ispis_mjesta_rodenja_txt);
        ispisDatumaRodenja = findViewById(R.id.ispis_datuma_rodenja_txt);
        ispisZupanije = findViewById(R.id.ispis_zupanije_txt);
        ispisSlikeOsobe = findViewById(R.id.ispis_slike_osobe);
        prebaciNaUrediOsobu = findViewById(R.id.uredi_osobu_btn);
        obrisiOsobu = findViewById(R.id.obrisi_osobu_btn);*/


        id = osobe.get(position).getId();


    }

    @Override
    protected void onStart() {
        super.onStart();
        dohvatiDetaljeOsobe(id);
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
                Toast.makeText(DetaljiOsobeActivity.this, "Osoba obrisana!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetaljiOsobeActivity.this, PrikazOsobaActivity.class);
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

                            ispisImena.setText(ime);
                            ispisPrezimena.setText(prezime);
                            ispisSpola.setText(spol);
                            ispisAdrese.setText(adresa);
                            ispisOiba.setText(oib);
                            ispisDatumaRodenja.setText(datum_rodenja);
                            ispisGrada.setText(grad);
                            ispisMjestaRodenja.setText(mjesto_rodenja);
                            ispisZupanije.setText(dohvatiNazivZupanije(zupanija_id));
                            final Bitmap slika_osobe = BitmapFactory.decodeByteArray(slika_iz_baze, 0, slika_iz_baze.length);
                            ispisSlikeOsobe.setImageBitmap(slika_osobe);

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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DetaljiOsobeActivity.this, PrikazOsobaActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.uredi_osobu_btn)
    public void onUrediOsobuBtnClicked() {
        Intent intent = new Intent(DetaljiOsobeActivity.this, UrediOsobuActivity.class);
        intent.putExtra("id", id);
        startActivity(intent);
    }

    @OnClick(R.id.obrisi_osobu_btn)
    public void onObrisiOsobuBtnClicked() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetaljiOsobeActivity.this);
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
