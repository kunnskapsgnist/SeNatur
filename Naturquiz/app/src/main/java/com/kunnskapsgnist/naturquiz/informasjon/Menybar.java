package com.kunnskapsgnist.naturquiz.informasjon;

import android.app.Activity;
import android.content.Intent;

import com.kunnskapsgnist.naturquiz.Meny_artene;
import com.kunnskapsgnist.naturquiz.Meny_historie;
import com.kunnskapsgnist.naturquiz.Meny_om;
import com.kunnskapsgnist.naturquiz.Meny_oppgradering;
import com.kunnskapsgnist.naturquiz.Meny_prestasjoner;
import com.kunnskapsgnist.naturquiz.Meny_profil;
import com.kunnskapsgnist.naturquiz.R;
import com.kunnskapsgnist.naturquiz.Start;

public class Menybar {
    Lagret lagret;
    BrukerInfo brukerInfo;

    String meny_aktivitet;

    public Menybar(String aktivitet) {
        this.meny_aktivitet = aktivitet;
    }

    public boolean sjekkMenybar(Activity context, int id) {
        lagret = new Lagret(context);
        brukerInfo = new BrukerInfo(context);
        if (id == android.R.id.home) {
            context.finish();
            return true;
        } else if (id == R.id.meny_start & !meny_aktivitet.equals("Start")) {
            Intent i=new Intent(context, Start.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
            return true;
        } else if (id == R.id.meny_profil) {
            context.startActivity(new Intent(context, Meny_profil.class));
            return true;
        } else if (id == R.id.meny_historie) {
            context.startActivity(new Intent(context, Meny_historie.class));
            return true;
        } else if (id == R.id.meny_prestasjoner) {
            context.startActivity(new Intent(context, Meny_prestasjoner.class));
            return true;
        } else if (id == R.id.meny_arter) {
            context.startActivity(new Intent(context, Meny_artene.class));
            return true;
        } else if (id == R.id.meny_oppgrader) {
            context.startActivity(new Intent(context, Meny_oppgradering.class));
            return true;
        } else if (id == R.id.meny_om) {
            context.startActivity(new Intent(context, Meny_om.class));
            return true;
        } else if (id == R.id.meny_lukk) {
            context.finishAffinity();
            return true;
        } else {
            return false;
        }

    }
}
