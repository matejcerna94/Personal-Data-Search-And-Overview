package com.matejcerna;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
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

public class PretraziOsobuZaUredivanje extends AppCompatActivity {

    static ArrayList<Osoba> osobe;
    private Adapter adapter;
    private GridView gridView;
    private boolean success = false;
    EditText traziOsobuZaUredivanjeTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretrazi_osobu_za_uredivanje);

        gridView = findViewById(R.id.gridView);
        osobe = new ArrayList<Osoba>();
        traziOsobuZaUredivanjeTxt = findViewById(R.id.trazi_osobu_za_uredivanje_txt);



        traziOsobuZaUredivanjeTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                IzlistajOsobe orderData = new IzlistajOsobe();
                orderData.execute("");

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PretraziOsobuZaUredivanje.this, UrediOsobu2Activity.class);
                intent.putExtra("key", position);
                startActivity(intent);
            }
        });


    }

    private class IzlistajOsobe extends AsyncTask<String, String, String> {
        String msg = "";


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
                    osobe = new ArrayList<>();
                    String pojam = traziOsobuZaUredivanjeTxt.getText().toString();

                    if (pojam.equals("")) {
                        osobe = new ArrayList<>();
                    } else {
                        String upit = "SELECT * FROM osobe WHERE ime LIKE '%" + pojam + "%' OR prezime LIKE '%"
                                + pojam + "%' OR spol LIKE '%" + pojam + "%' OR adresa LIKE '%"
                                + pojam + "%' OR oib LIKE '%" + pojam + "%' OR datum_rodenja LIKE '%"
                                + pojam + "%' OR grad LIKE '%" + pojam + "%' OR mjesto_rodenja LIKE '%" + pojam + "%'";
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
                                            resultSet.getString("ime"),
                                            resultSet.getString("prezime"),
                                            resultSet.getString("spol"),
                                            resultSet.getString("adresa"),
                                            resultSet.getString("oib"),
                                            resultSet.getString("datum_rodenja"),
                                            resultSet.getString("grad"),
                                            resultSet.getString("mjesto_rodenja"),
                                            resultSet.getBytes("slika"),
                                            resultSet.getInt("zupanija_id")));
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
//            progress.dismiss();
            Toast.makeText(PretraziOsobuZaUredivanje.this, msg + "", Toast.LENGTH_SHORT).show();
            if (success = false) {

            } else {
                try {
                    adapter = new PretraziOsobuZaUredivanje.Adapter(osobe, PretraziOsobuZaUredivanje.this);
                    gridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    gridView.setAdapter(adapter);
                } catch (Exception ex) {

                }
            }
        }
    }

    public class Adapter extends BaseAdapter {

        public class ViewHolder {
            TextView textIme;
            TextView textPrezime;
            TextView textGrad;
            ImageView slikaOsobe;
            byte [] slika;
        }

        public List<Osoba> listaOsoba;

        public Context context;
        ArrayList<Osoba> arrayList;

        private Adapter(List<Osoba> apps, Context context) {
            this.listaOsoba = apps;
            this.context = context;
            arrayList = new ArrayList<Osoba>();
            arrayList.addAll(listaOsoba);
        }

        @Override
        public int getCount() {
            return listaOsoba.size();
        }

        @Override
        public Object getItem(int position) {
            return this.listaOsoba.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            PretraziOsobuZaUredivanje.Adapter.ViewHolder viewHolder = null;

            if (rowView == null) {
                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.list_content, parent, false);
                viewHolder = new PretraziOsobuZaUredivanje.Adapter.ViewHolder();
                viewHolder.textIme = (TextView) rowView.findViewById(R.id.text_view_ime);
                viewHolder.textPrezime = (TextView) rowView.findViewById(R.id.text_view_prezime);
                viewHolder.textGrad = (TextView) rowView.findViewById(R.id.text_view_grad);
                viewHolder.slikaOsobe = (ImageView) rowView.findViewById(R.id.image_view_slika);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (PretraziOsobuZaUredivanje.Adapter.ViewHolder) convertView.getTag();
            }
            viewHolder.textIme.setText(listaOsoba.get(position).getIme() + "");
            viewHolder.textPrezime.setText(listaOsoba.get(position).getPrezime() + "");
            viewHolder.textGrad.setText(listaOsoba.get(position).getGrad() + "");
            viewHolder.slika = listaOsoba.get(position).getSlika();
            Bitmap slika_osobe= BitmapFactory.decodeByteArray(viewHolder.slika,0,viewHolder.slika.length);
            viewHolder.slikaOsobe.setImageBitmap(slika_osobe);

            return rowView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PretraziOsobuZaUredivanje.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
