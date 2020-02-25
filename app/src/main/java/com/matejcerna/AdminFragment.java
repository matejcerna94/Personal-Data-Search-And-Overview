package com.matejcerna;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class AdminFragment extends Fragment {

   /* LinearLayout linearLayoutDodajAdmina;
    LinearLayout linearLayoutUrediAdmina;
    LinearLayout linearLayoutObrisiAdmina;
    LinearLayout linearLayoutTraziAdmina;
    LinearLayout linearLayoutPrikaziAdmine;
    LinearLayout linearLayoutOdjava;*/


    Animation animacijaOdozdo;
    @BindView(R.id.clover)
    ImageView clover;
    @BindView(R.id.texthome)
    LinearLayout textHome;
    @BindView(R.id.linear_layout_dodaj_admina)
    LinearLayout linearLayoutDodajAdmina;
    @BindView(R.id.linear_layout_uredi_admina)
    LinearLayout linearLayoutUrediAdmina;
    @BindView(R.id.linear_layout_obrisi_admina)
    LinearLayout linearLayoutObrisiAdmina;
    @BindView(R.id.linear_layout_trazi_admina)
    LinearLayout linearLayoutTraziAdmina;
    @BindView(R.id.linear_layout_prikazi_admine)
    LinearLayout linearLayoutPrikaziAdmine;
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
        View v = inflater.inflate(R.layout.fragment_admin, container, false);
        ButterKnife.bind(this, v);

        /*linearLayoutDodajAdmina = v.findViewById(R.id.linear_layout_dodaj_admina);
        linearLayoutUrediAdmina = v.findViewById(R.id.linear_layout_uredi_admina);
        linearLayoutObrisiAdmina = v.findViewById(R.id.linear_layout_obrisi_admina);
        linearLayoutPrikaziAdmine = v.findViewById(R.id.linear_layout_prikazi_admine);
        linearLayoutTraziAdmina = v.findViewById(R.id.linear_layout_trazi_admina);
        linearLayoutOdjava = v.findViewById(R.id.linear_layout_odjavi_se);*/


        animacijaOdozdo = AnimationUtils.loadAnimation(getActivity(), R.anim.animacija_odozdo);

       /* pozadina = v.findViewById(R.id.pozadina);
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

    @OnClick(R.id.linear_layout_dodaj_admina)
    public void onLinearLayoutDodajAdminaClicked() {
        Intent i = new Intent(getActivity(), DodajAdminaActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.linear_layout_uredi_admina)
    public void onLinearLayoutUrediAdminaClicked() {
        Intent i = new Intent(getActivity(), UrediAdminaActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.linear_layout_obrisi_admina)
    public void onLinearLayoutObrisiAdminaClicked() {
        Intent i = new Intent(getActivity(), ObrisiAdminaActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.linear_layout_trazi_admina)
    public void onLinearLayoutTraziAdminaClicked() {
        Intent i = new Intent(getActivity(), TraziAdminaActivity.class);
        startActivity(i);
    }

    @OnClick(R.id.linear_layout_prikazi_admine)
    public void onLinearLayoutPrikaziAdmineClicked() {
        Intent i = new Intent(getActivity(), PrikazAdminaActivity.class);
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
