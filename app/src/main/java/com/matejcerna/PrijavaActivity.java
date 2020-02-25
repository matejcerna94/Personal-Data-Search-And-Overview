package com.matejcerna;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PrijavaActivity extends AppCompatActivity {

    static ArrayList<Admin> admini;

    EditText unesiKorisnickoIme;
    EditText unesiLozinku;
    Button prijaviSe;
    Button info;
    private boolean success = false;
    String msg;
    private int brojac = 5;
    Animation animacijaOdozdo;
    LinearLayout linearLayoutPolja;
    ImageView slikaLogin;
    TextView textViewPrijava;
    RelativeLayout relativeLayoutAnimacija;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prijava);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        animacijaOdozdo = AnimationUtils.loadAnimation(this, R.anim.animacija_odozdo);


        unesiKorisnickoIme = findViewById(R.id.korisnicko_ime_txt);
        unesiLozinku = findViewById(R.id.lozinka_txt);
        prijaviSe = findViewById(R.id.prijavi_se_btn);
        info = findViewById(R.id.info_btn);
        linearLayoutPolja = findViewById(R.id.linear_layout_polja);
        slikaLogin = findViewById(R.id.slika_login);
        textViewPrijava = findViewById(R.id.text_view_prijava);
        relativeLayoutAnimacija = findViewById(R.id.relative_layout_animacija);


      /*  linearLayoutPolja.startAnimation(animacijaOdozdo);
        prijaviSe.startAnimation(animacijaOdozdo);
        slikaLogin.startAnimation(animacijaOdozdo);
        textViewPrijava.startAnimation(animacijaOdozdo);*/
      relativeLayoutAnimacija.startAnimation(animacijaOdozdo);

        unesiKorisnickoIme.setFocusable(false);
        unesiLozinku.setFocusable(false);

        unesiKorisnickoIme.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                unesiKorisnickoIme.setFocusableInTouchMode(true);

                return false;
            }
        });

        unesiLozinku.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                unesiLozinku.setFocusableInTouchMode(true);
                return false;
            }
        });


        prijaviSe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provjeriUnos(unesiKorisnickoIme);
                provjeriUnos(unesiLozinku);
                prijava();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrijavaActivity.this, OAplikacijiActivity.class);
                startActivity(intent);
            }
        });


    }

    private void prijava() {
        String korisnicko_ime = unesiKorisnickoIme.getText().toString();
        String lozinka = unesiLozinku.getText().toString();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(Baza.getDbUrl(), Baza.getUSER(), Baza.getPASS());

            if (connection == null) {
                success = false;
            } else {

                if (korisnicko_ime.equals("") || lozinka.equals("")) {
                    Toast.makeText(this, "Morate unijeti korisničko ime i lozinku!", Toast.LENGTH_LONG).show();
                } else {
                    String upit = "SELECT korisnicko_ime, lozinka FROM admin WHERE korisnicko_ime='" + korisnicko_ime + "' AND lozinka= '" + lozinka + "' ";
                    PreparedStatement preparedStatement = connection.prepareStatement(upit);
                    ResultSet resultSet = preparedStatement.executeQuery();

                    if (resultSet.next() == false) {
                        brojac--;
                        if (brojac == 1) {
                            msg = "Korisnički podaci nisu ispravni! Imate još " + brojac + " pokušaj!";
                        } else {
                            msg = "Korisnički podaci nisu ispravni! Imate još " + brojac + " pokušaja!";
                        }


                        if (brojac == 0) {
                            msg = "Iskoristili ste sve pokušaje. Kontaktirajte administratora za daljnje upute!";
                            prijaviSe.setEnabled(false);
                        }
                        success = false;
                        Toast.makeText(this, msg + "", Toast.LENGTH_LONG).show();
                    } else {
                        do {
                            try {
                                admini.add(new Admin(
                                        resultSet.getInt("id"),
                                        resultSet.getString("korisnicko_ime"),
                                        resultSet.getString("lozinka"),
                                        resultSet.getBytes("slika")));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }

                        } while (resultSet.next());
                        msg = "Ima podataka u bazi!";
                        success = true;
                        Toast.makeText(PrijavaActivity.this, "Uspješna prijava!", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);

                    }
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

    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Jeste li sigurni da želite izaći iz aplikacije?").setCancelable(false)
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        System.exit(0);
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
