package com.matejcerna.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.matejcerna.activity.DodajOsobuActivity;
import com.matejcerna.activity.ObrisiOsobuActivity;
import com.matejcerna.activity.PretraziOsobuZaUredivanjeActivity;
import com.matejcerna.activity.PrijavaActivity;
import com.matejcerna.activity.PrikazOsobaActivity;
import com.matejcerna.R;
import com.matejcerna.activity.TraziOsobuActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class OsobeFragment extends Fragment {
    /* LinearLayout linearLayoutDodajOsobu;
     LinearLayout linearLayoutUrediOsobu;
     LinearLayout linearLayoutObrisiOsobu;
     LinearLayout linearLayoutPrikaziSveOsobe;
     LinearLayout linearLayoutTraziOsobe;
     LinearLayout linearLayoutOdjava;

     ImageView pozadina, clover;
     LinearLayout textHome, meni;*/
    Animation animacijaOdozdo;
    @BindView(R.id.clover)
    ImageView clover;
    @BindView(R.id.text_view_prijavljeni_admin)
    TextView textViewPrijavljeniAdmin;
    @BindView(R.id.texthome)
    LinearLayout textHome;
    @BindView(R.id.linear_layout_dodaj_osobu)
    LinearLayout linearLayoutDodajOsobu;
    @BindView(R.id.linear_layout_uredi_osobu)
    LinearLayout linearLayoutUrediOsobu;
    @BindView(R.id.linear_layout_obrisi_osobu)
    LinearLayout linearLayoutObrisiOsobu;
    @BindView(R.id.linear_layout_trazi_osobu)
    LinearLayout linearLayoutTraziOsobu;
    @BindView(R.id.linear_layout_prikazi_osobe)
    LinearLayout linearLayoutPrikaziOsobe;
    @BindView(R.id.linear_layout_odjavi_se)
    LinearLayout linearLayoutOdjaviSe;
    @BindView(R.id.meni)
    LinearLayout meni;
    @BindView(R.id.pozadina)
    ImageView pozadina;
    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_osobe, container, false);
        ButterKnife.bind(this, v);

       /* linearLayoutDodajOsobu = v.findViewById(R.id.linear_layout_dodaj_osobu);
        linearLayoutUrediOsobu = v.findViewById(R.id.linear_layout_uredi_osobu);
        linearLayoutObrisiOsobu = v.findViewById(R.id.linear_layout_obrisi_osobu);
        linearLayoutTraziOsobe = v.findViewById(R.id.linear_layout_trazi_osobu);
        linearLayoutPrikaziSveOsobe = v.findViewById(R.id.linear_layout_prikazi_osobe);
        linearLayoutOdjava = v.findViewById(R.id.linear_layout_odjavi_se);*/


        animacijaOdozdo = AnimationUtils.loadAnimation(getActivity(), R.anim.animacija_odozdo);

      /*  pozadina = v.findViewById(R.id.pozadina);
        clover = v.findViewById(R.id.clover);
        textHome = v.findViewById(R.id.texthome);
        meni = v.findViewById(R.id.meni);*/

        pozadina.animate().translationY(-1450).setDuration(800).setStartDelay(300);
        clover.animate().alpha(0).setDuration(800).setStartDelay(600);

        textHome.startAnimation(animacijaOdozdo);
        meni.startAnimation(animacijaOdozdo);


        unbinder = ButterKnife.bind(this, v);
        return v;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.linear_layout_dodaj_osobu)
    public void onLinearLayoutDodajOsobuClicked() {
        Intent i = new Intent(getActivity(), DodajOsobuActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.linear_layout_uredi_osobu)
    public void onLinearLayoutUrediOsobuClicked() {
        Intent i = new Intent(getActivity(), PretraziOsobuZaUredivanjeActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.linear_layout_obrisi_osobu)
    public void onLinearLayoutObrisiOsobuClicked() {
        Intent i = new Intent(getActivity(), ObrisiOsobuActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.linear_layout_trazi_osobu)
    public void onLinearLayoutTraziOsobuClicked() {
        Intent i = new Intent(getActivity(), TraziOsobuActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.linear_layout_prikazi_osobe)
    public void onLinearLayoutPrikaziOsobeClicked() {
        Intent i = new Intent(getActivity(), PrikazOsobaActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.linear_layout_odjavi_se)
    public void onLinearLayoutOdjaviSeClicked() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("Jeste li sigurni da se želite odjaviti?").setCancelable(false)
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getActivity(), PrijavaActivity.class);
                        startActivity(i);
                        getActivity().finish();
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
