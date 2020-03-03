package com.matejcerna.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.matejcerna.R;
import com.matejcerna.database.Baza;
import com.matejcerna.model.Admin;

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

public class PretraziAdminaZaUredivanjeActivity extends AppCompatActivity {

    static ArrayList<Admin> admini;
    @BindView(R.id.trazi_admina_za_uredivanje_txt)
    EditText traziAdminaZaUredivanjeTxt;
    @BindView(R.id.grid_view_admin)
    GridView gridViewAdmin;
    private Adapter adapter;
    //private GridView gridViewAdmin;
    private boolean success = false;
    //EditText traziAdminaZaUredivanjeTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pretrazi_admina_za_uredivanje);
        ButterKnife.bind(this);

        //gridViewAdmin = findViewById(R.id.grid_view_admin);
        admini = new ArrayList<Admin>();
        //traziAdminaZaUredivanjeTxt = findViewById(R.id.trazi_admina_za_uredivanje_txt);

        traziAdminaZaUredivanjeTxt.addTextChangedListener(new TextWatcher() {
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

        gridViewAdmin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PretraziAdminaZaUredivanjeActivity.this, UrediAdmina2Activity.class);
                intent.putExtra("key", position);
                startActivity(intent);
            }
        });


    }

    private class IzlistajAdmine extends AsyncTask<String, String, String> {
        String msg = "";
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            // progress = ProgressDialog.show(TraziOsobuActivity.this, "Sinkronizacija", "Učitavaju se podaci iz baze, molimo pričekajte!", true);
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
                    String pojam = traziAdminaZaUredivanjeTxt.getText().toString();

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
//            progress.dismiss();
            Toast.makeText(PretraziAdminaZaUredivanjeActivity.this, msg + "", Toast.LENGTH_SHORT).show();
            if (success = false) {

            } else {
                try {
                    adapter = new Adapter(admini, PretraziAdminaZaUredivanjeActivity.this);
                    gridViewAdmin.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    gridViewAdmin.setAdapter(adapter);
                } catch (Exception ex) {

                }
            }
        }
    }

    public class Adapter extends BaseAdapter {

        public class ViewHolder {
            //TextView textKorisnickoIme;
            //ImageView slikaAdmina;
            @BindView(R.id.image_view_slika_admina)
            ImageView slikaAdmina;
            @BindView(R.id.text_view_korisnicko_ime)
            TextView textKorisnickoIme;
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
            viewHolder.textKorisnickoIme.setText(listaAdmina.get(position).getKorisnicko_ime() + "");
            viewHolder.slika = listaAdmina.get(position).getSlika();
            Bitmap slika_admina = BitmapFactory.decodeByteArray(viewHolder.slika, 0, viewHolder.slika.length);
            viewHolder.slikaAdmina.setImageBitmap(slika_admina);

            return rowView;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(PretraziAdminaZaUredivanjeActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
