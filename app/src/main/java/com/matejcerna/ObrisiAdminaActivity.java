package com.matejcerna;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemLongClick;

public class ObrisiAdminaActivity extends AppCompatActivity {

    static ArrayList<Admin> admini;
    @BindView(R.id.trazi_admina_za_brisanje_txt)
    EditText traziAdminaZaBrisanje;
    @BindView(R.id.grid_view_admin_za_brisanje)
    GridView gridViewAdmin;
    private Adapter adapter;
    //private GridView gridViewAdmin;
    private boolean success = false;
    //EditText traziAdminaZaBrisanje;
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_obrisi_admina);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        //gridViewAdmin = findViewById(R.id.grid_view_admin_za_brisanje);
        admini = new ArrayList<Admin>();
        // traziAdminaZaBrisanjeTxt = findViewById(R.id.trazi_admina_za_brisanje_txt);


        traziAdminaZaBrisanje.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                IzlistajAdmine izlistajAdmine = new IzlistajAdmine();
                izlistajAdmine.execute("");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnItemLongClick(R.id.grid_view_admin_za_brisanje)
    public boolean onViewClicked(AdapterView<?> parent, View view, int position, long id) {
        final int pos = position;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ObrisiAdminaActivity.this);
        alertDialog.setMessage("Jeste li sigurni da želite obrisati administratora?").setCancelable(false)
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            long id = admini.get(pos).getId();
                            msg = "";
                            Class.forName("com.mysql.jdbc.Driver");
                            Connection connection = DriverManager.getConnection(Baza.getDbUrl(), Baza.getUSER(), Baza.getPASS());

                            if (connection == null) {
                                success = false;
                            } else {
                                String upit = "DELETE FROM admin WHERE id= ?";
                                PreparedStatement preparedStatement = connection.prepareStatement(upit);
                                preparedStatement.setLong(1, id);
                                preparedStatement.executeUpdate();
                                Toast.makeText(ObrisiAdminaActivity.this, "Administrator obrisan!", Toast.LENGTH_SHORT).show();
                                IzlistajAdmine izlistajAdmine = new IzlistajAdmine();
                                izlistajAdmine.execute("");


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Writer writer = new StringWriter();
                            e.printStackTrace(new PrintWriter(writer));
                            msg = writer.toString();
                            success = false;
                        }

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

        return false;

    }

    private class IzlistajAdmine extends AsyncTask<String, String, String> {
        String msg = "";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(Baza.getDbUrl(), Baza.getUSER(), Baza.getPASS());

                if (connection == null) {
                    success = false;
                } else {
                    admini = new ArrayList<>();
                    String pojam = traziAdminaZaBrisanje.getText().toString();

                    if (pojam.equals("")) {
                        admini = new ArrayList<>();
                    } else {
                        String upit = "SELECT * FROM admin WHERE korisnicko_ime LIKE '%" + pojam + "%'";
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
                                            resultSet.getString("korisnicko_ime"),
                                            resultSet.getString("lozinka"),
                                            resultSet.getBytes("slika")));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            } while (resultSet.next());
                            msg = "Ima podataka u bazi!";
                            success = true;

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
            return msg;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(ObrisiAdminaActivity.this, msg + "", Toast.LENGTH_SHORT).show();
            if (success = false) {

            } else {
                try {
                    adapter = new Adapter(admini, ObrisiAdminaActivity.this);
                    gridViewAdmin.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    gridViewAdmin.setAdapter(adapter);
                } catch (Exception ex) {

                }
            }
        }
    }

    public class Adapter extends BaseAdapter {




        public class ViewHolder {
            @BindView(R.id.image_view_slika_admina)
            ImageView slikaAdmina;
            @BindView(R.id.text_view_korisnicko_ime)
            TextView textKorisnickoIme;
            //TextView textKorisnickoIme;
            //ImageView slikaAdmina;
            byte[] slika;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }

        public List<Admin> listaAdmina;

        public Context context;
        ArrayList<Admin> arrayList;

        private Adapter(List<Admin> apps, Context context) {
            this.listaAdmina = apps;
            this.context = context;
            arrayList = new ArrayList<Admin>();
            arrayList.addAll(listaAdmina);
        }

        @Override
        public int getCount() {
            return listaAdmina.size();
        }

        @Override
        public Object getItem(int position) {
            return this.listaAdmina.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            ViewHolder viewHolder = null;

            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.list_content_admin, parent, false);
                viewHolder = new ViewHolder(rowView);
                //viewHolder.textKorisnickoIme = (TextView) rowView.findViewById(R.id.text_view_korisnicko_ime);
                // viewHolder.slikaAdmina = (ImageView) rowView.findViewById(R.id.image_view_slika_admina);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textKorisnickoIme.setText(listaAdmina.get(position).getKorisniko_ime() + "");
            viewHolder.slika = listaAdmina.get(position).getSlika();
            Bitmap slika_admina = BitmapFactory.decodeByteArray(viewHolder.slika, 0, viewHolder.slika.length);
            viewHolder.slikaAdmina.setImageBitmap(slika_admina);

            return rowView;
        }
    }
}
