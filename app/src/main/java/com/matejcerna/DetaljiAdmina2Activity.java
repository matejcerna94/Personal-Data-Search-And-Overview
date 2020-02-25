package com.matejcerna;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

import static com.matejcerna.TraziAdminaActivity.admini;

public class DetaljiAdmina2Activity extends AppCompatActivity {

    TextView ispisKorisnickogImenaPretrazenogAdmina;
    TextView ispisLozinkePretrazenogAdmina;
    ImageView ispisSlikePretrazenogAdmina;
    Button prebaciNaUrediAdmina;
    Button obrisiAdmina;
    byte [] slika_iz_baze;
    int id;
    String msg;
    private boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalji_admina2);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        int position = getIntent().getExtras().getInt("key");

        ispisKorisnickogImenaPretrazenogAdmina = findViewById(R.id.ispis_korisnickog_imena_pretrazenog_admina_txt);
        ispisLozinkePretrazenogAdmina = findViewById(R.id.ispis_lozinke_pretrazenog_admina_txt);
        ispisSlikePretrazenogAdmina = findViewById(R.id.ispis_slike_pretrazenog_admina);
        prebaciNaUrediAdmina = findViewById(R.id.uredi_pretrazenog_admina_btn);
        obrisiAdmina = findViewById(R.id.obrisi_pretrazenog_admina_btn);








        id = admini.get(position).getId();
        prebaciNaUrediAdmina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetaljiAdmina2Activity.this, UrediAdmina3Activity.class);
                intent.putExtra("id", id);

                startActivity(intent);
            }
        });

        obrisiAdmina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetaljiAdmina2Activity.this);
                alertDialog.setMessage("Jeste li sigurni da želite obrisati osobu?").setCancelable(false)
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                obrisiAdmina();
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
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        dohvatiDetaljeAdmina(id);
    }

    public void obrisiAdmina() {
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
                Toast.makeText(DetaljiAdmina2Activity.this, "Administrator obrisan!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetaljiAdmina2Activity.this, PrikazAdminaActivity.class);
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
                                    korisnicko_ime=resultSet.getString("korisnicko_ime"),
                                    lozinka=resultSet.getString("lozinka"),
                                    slika_iz_baze=resultSet.getBytes("slika")));

                            ispisKorisnickogImenaPretrazenogAdmina.setText(korisnicko_ime);
                            ispisLozinkePretrazenogAdmina.setText(lozinka);
                            final Bitmap slika_admina=BitmapFactory.decodeByteArray(slika_iz_baze,0,slika_iz_baze.length);
                            ispisSlikePretrazenogAdmina.setImageBitmap(slika_admina);

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
        Intent intent = new Intent(DetaljiAdmina2Activity.this, TraziAdminaActivity.class);
        startActivity(intent);
        finish();
    }
}
