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

import static com.matejcerna.PrikazAdminaActivity.admini;

public class DetaljiAdminaActivity extends AppCompatActivity {

    /*TextView ispisKorisnickogImena;
    TextView ispisLozinke;
    ImageView ispisSlikeAdmina;
    Button prebaciNaUrediAdmina;
    Button obrisiAdmina;*/
    int id;
    String msg;
    @BindView(R.id.ispis_slike_admina)
    ImageView ispisSlikeAdmina;
    @BindView(R.id.ispis_korisnickog_imena_txt)
    TextView ispisKorisnickogImena;
    @BindView(R.id.ispis_lozinke_txt)
    TextView ispisLozinke;
    private boolean success = false;
    byte[] slika_iz_baze;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalji_admina);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        int position = getIntent().getExtras().getInt("key");

       /* ispisKorisnickogImena = findViewById(R.id.ispis_korisnickog_imena_txt);
        ispisLozinke = findViewById(R.id.ispis_lozinke_txt);
        ispisSlikeAdmina = findViewById(R.id.ispis_slike_admina);
        prebaciNaUrediAdmina = findViewById(R.id.uredi_admina_btn);
        obrisiAdmina = findViewById(R.id.obrisi_admina_btn);*/

        id = admini.get(position).getId();

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
                String upit = "DELETE FROM admin WHERE id= ?";
                PreparedStatement preparedStatement = connection.prepareStatement(upit);
                preparedStatement.setInt(1, id);
                preparedStatement.executeUpdate();
                Toast.makeText(DetaljiAdminaActivity.this, "Administrator obrisan!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DetaljiAdminaActivity.this, PrikazAdminaActivity.class);
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
                                    korisnicko_ime = resultSet.getString("korisnicko_ime"),
                                    lozinka = resultSet.getString("lozinka"),
                                    slika_iz_baze = resultSet.getBytes("slika")));

                            ispisKorisnickogImena.setText(korisnicko_ime);
                            ispisLozinke.setText(lozinka);
                            final Bitmap slika_admina = BitmapFactory.decodeByteArray(slika_iz_baze, 0, slika_iz_baze.length);
                            ispisSlikeAdmina.setImageBitmap(slika_admina);

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
        Intent intent = new Intent(DetaljiAdminaActivity.this, PrikazAdminaActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.uredi_admina_btn)
    public void onUrediAdminaBtnClicked() {
        Intent intent = new Intent(DetaljiAdminaActivity.this, UrediAdminaActivity.class);
        intent.putExtra("id", id);

        startActivity(intent);
    }

    @OnClick(R.id.obrisi_admina_btn)
    public void onObrisiAdminaBtnClicked() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DetaljiAdminaActivity.this);
        alertDialog.setMessage("Jeste li sigurni da želite obrisati administratora?").setCancelable(false)
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
}
