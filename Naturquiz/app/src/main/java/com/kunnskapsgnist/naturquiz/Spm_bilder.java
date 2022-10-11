package com.kunnskapsgnist.naturquiz;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.android.volley.toolbox.NetworkImageView;
import com.kunnskapsgnist.naturquiz.data.AppKontroll;
import com.kunnskapsgnist.naturquiz.data.Artsbank;
import com.kunnskapsgnist.naturquiz.data.Mediabank;
import com.kunnskapsgnist.naturquiz.databinding.SpmBilderBinding;
import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Farge;
import com.kunnskapsgnist.naturquiz.informasjon.Innstillinger;
import com.kunnskapsgnist.naturquiz.informasjon.Lagret;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;
import com.kunnskapsgnist.naturquiz.informasjon.Poeng;
import com.kunnskapsgnist.naturquiz.modell.Art;
import com.kunnskapsgnist.naturquiz.modell.Media;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class Spm_bilder extends AppCompatActivity {
    private static final String TAG = "Naturquiz";
    private SpmBilderBinding binding;      // Bånd til layout

    TextView view_tittel, view_alternativ1, view_alternativ2, view_alternativ3, view_alternativ4,
            view_poeng, view_antall, view_hjelp_tekst, view_cc_eier, view_cc_lisens, view_bilde_mangler;
    NetworkImageView view_bilde, view_bilde_stor;
    ImageView view_type, view_hjelp_finger, view_internett, view_cc, view_cc_stor, view_cc_lenke, view_cc_lukk;
    LinearLayout view_cc_lag;
    ConstraintLayout view_bakgrunn;

    List<Art> artListe;// Hele listen
    private String rettSvar;      // Riktig designer
    private String bildeurl;
    private int poengMulig = 3;      // Maks antall poeng per spørsmål
    private int nr_valgt;
    private List<Integer> nr_valgte; // Numre som er valgt tidligere
    private double tid_start;
    private float dh;
    private boolean vis_meny;

    Poeng poeng;
    Farge farge;

    Innstillinger innstillinger;    // Tilgang til innstillinger
    BrukerInfo brukerInfo;
    Lagret lagret;                  // Tilgang til lagrede parametere
    Menybar menybar;                // Tilgang til menybar
    ActionBar actionBar;
    Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.spm_bilder);

        view_bakgrunn = binding.bildeBakgrunn;
        view_tittel = binding.bildeTittel;
        view_bilde = binding.bildeBilde;
        view_bilde_stor = binding.bildeStor;
        view_alternativ1 = binding.bildeAlternativNr1;
        view_alternativ2 = binding.bildeAlternativNr2;
        view_alternativ3 = binding.bildeAlternativNr3;
        view_alternativ4 = binding.bildeAlternativNr4;

        view_internett = binding.bildeInternett;
        view_poeng = binding.bildePoeng;
        view_antall = binding.bildeAntall;
        view_type = binding.bildeType;

        view_cc = binding.bildeCc;
        view_cc_stor = binding.bildeStorCc;
        view_cc_lag = binding.bildeCcLag;
        view_cc_eier = binding.bildeCcEier;
        view_cc_lisens = binding.bildeCcLisens;
        view_cc_lenke = binding.bildeCcLenke;
        view_cc_lukk = binding.bildeCcLukk;

        view_hjelp_tekst = binding.bildeHjelpTekst;
        view_hjelp_finger = binding.bildeHjelpFinger;

        view_bilde_mangler = binding.bildeMangler;

        // Initierer
        innstillinger = new Innstillinger(this);
        brukerInfo = new BrukerInfo(this);
        lagret = new Lagret(this);
        lagret.setNrSpm(1);
        lagret.setPoeng(0);
        poeng = new Poeng();
        farge = new Farge(this,innstillinger.getType());

        dh = 0;
        nr_valgte = new ArrayList<>();

        // Starttekster
        view_poeng.setText(String.format(getString(R.string.poeng_d), lagret.getPoeng()));
        fargelegg();

        if (isConnectingToInternet()) {
            new Artsbank(innstillinger.getLevel(), innstillinger.getType(), innstillinger.getGruppe())
                    .getArter(artListe -> {
                        this.artListe = artListe;
                        oppdaterSpm();
                    });

            // Klargjør knappene
            view_alternativ1.setOnClickListener(v -> sjekkSvar(view_alternativ1));
            view_alternativ2.setOnClickListener(v -> sjekkSvar(view_alternativ2));
            view_alternativ3.setOnClickListener(v -> sjekkSvar(view_alternativ3));
            view_alternativ4.setOnClickListener(v -> sjekkSvar(view_alternativ4));

            // Gjør bildene klikkbare
            view_bilde.setOnClickListener(v -> zoom_inn());
            view_bilde_stor.setOnClickListener(view -> {
                if (view_cc_lag.getVisibility() == View.GONE)
                    zoom_ut();
                view_cc_lag.setVisibility(View.GONE);
           });
        } else Toast.makeText(this,R.string.mangler_internett,Toast.LENGTH_LONG).show();

        // Actionbar
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbake
        menybar = new Menybar("Spm_bilder");
        vis_meny = true;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu meny) {
        if (vis_meny) {
            getMenuInflater().inflate(R.menu.meny, meny);
            getMenuInflater().inflate(R.menu.hjelp, meny);
            return true;
        } else return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_hjelp & isConnectingToInternet()) {
            hjelp();
            return true;
        } else {
            return menybar.sjekkMenybar(this, item.getItemId());
        }
    }

    // Sjekker om appen er koblet til internett (det kan tenkes den er koblet til et nett som ikke er koblet til internett)
    private boolean isConnectingToInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null){
            view_alternativ1.setVisibility(View.INVISIBLE);
            view_alternativ2.setVisibility(View.INVISIBLE);
            view_alternativ3.setVisibility(View.INVISIBLE);
            view_alternativ4.setVisibility(View.INVISIBLE);
            view_bilde.setVisibility(View.INVISIBLE);
            view_internett.setVisibility(View.VISIBLE);
        } else {
            view_alternativ1.setVisibility(View.VISIBLE);
            view_alternativ2.setVisibility(View.VISIBLE);
            view_alternativ3.setVisibility(View.VISIBLE);
            view_alternativ2.setVisibility(View.VISIBLE);
            view_bilde.setVisibility(View.VISIBLE);
            view_internett.setVisibility(View.GONE);
        }
        return ni != null;
    }

    private void sjekkSvar(TextView valgt) {
        view_cc_lag.setVisibility(View.GONE);
        if (artListe != null) {
            view_alternativ1.setOnClickListener(null);
            view_alternativ2.setOnClickListener(null);
            view_alternativ3.setOnClickListener(null);
            view_alternativ4.setOnClickListener(null);
            if (rettSvar.contentEquals(valgt.getText())) {
                riktig(valgt);
            } else {
                feil(valgt);
            }
        }
    }

    private void feil(TextView valgt) {
        poengMulig -= 1;
        if (poengMulig < 0) poengMulig = 0;

        RotateAnimation animasjonFeil = new RotateAnimation(
                -10,10,
                Animation.RELATIVE_TO_SELF,(float) 0.5,
                Animation.RELATIVE_TO_SELF,(float) 0.5);
        animasjonFeil.setDuration(200);
        animasjonFeil.setRepeatCount(2);
        animasjonFeil.setRepeatMode(Animation.REVERSE);

        valgt.setAnimation(animasjonFeil);
        animasjonFeil.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                valgt.setTextColor(ContextCompat.getColor(Spm_bilder.this, R.color.rod));
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view_alternativ1.setOnClickListener(v -> sjekkSvar(view_alternativ1));
                view_alternativ2.setOnClickListener(v -> sjekkSvar(view_alternativ2));
                view_alternativ3.setOnClickListener(v -> sjekkSvar(view_alternativ3));
                view_alternativ4.setOnClickListener(v -> sjekkSvar(view_alternativ4));
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        valgt.startAnimation(animasjonFeil);

    }

    private void riktig(TextView view_valgt) {
        double tid = System.currentTimeMillis() / 1000.0 - tid_start - 1.0;
        lagret.setNrSpm(lagret.getNrSpm() + 1);
        int poeng_runden = poengMulig * poeng.faktor(tid, innstillinger.getAntallSpmTotalt());
        lagret.setPoeng(lagret.getPoeng() + poeng_runden);
        view_poeng.setText(MessageFormat.format("Poeng: + {0}", poeng_runden));
        poengMulig = 0; // unngå å trykke flere ganger før neste spm

        AlphaAnimation animasjonRiktig = new AlphaAnimation(1.0f,0.5f);
        animasjonRiktig.setDuration(400); // milliseconds
        animasjonRiktig.setRepeatCount(2);
        animasjonRiktig.setRepeatMode(Animation.REVERSE);

        view_valgt.setAnimation(animasjonRiktig);

        animasjonRiktig.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view_valgt.setTextColor(getColor(R.color.gronn_klar));
                view_poeng.setTextColor(getColor(R.color.gronn_klar));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view_alternativ1.setTextColor(getColor(R.color.hvit));
                view_alternativ2.setTextColor(getColor(R.color.hvit));
                view_alternativ3.setTextColor(getColor(R.color.hvit));
                view_alternativ4.setTextColor(getColor(R.color.hvit));
//                view_poeng.setText(String.format(getString(string.empty), lagret.getPoeng()));
                view_poeng.setText(MessageFormat.format("Points: {0}", lagret.getPoeng()));
                view_poeng.setTextColor(farge.getFarge());
                if(lagret.getNrSpm() <= innstillinger.getAntallSpmTotalt()) {
                    oppdaterSpm();
                } else {
                    ferdig();
                }
                poengMulig = 3;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        view_valgt.startAnimation(animasjonRiktig);
    }

    private void oppdaterSpm() {
        if (isConnectingToInternet() & artListe != null){
            LinkedList<String> artene = new LinkedList<>();

            view_antall.setText(String.format(getString(R.string.spm_av_total), lagret.getNrSpm(), innstillinger.getAntallSpmTotalt()));

            // Riktig spill og designer
            nr_valgt = (int) (Math.random() * artListe.size());
            while (artListe.get(nr_valgt).getArt().equals("") | nr_valgte.contains(nr_valgt))
                nr_valgt = (int) (Math.random() * artListe.size());

            nr_valgte.add(nr_valgt);
            rettSvar = artListe .get(nr_valgt).getArt();

            // Bilde
            new Mediabank(innstillinger.getType(), artListe.get(nr_valgt).getFamilie())
                    .getMedier(this::visBilde);

            // Legger til alternativer
            int nr;
            artene.add(rettSvar);
            while (artene.size() < 4) {
                nr = (int) (Math.random() * artListe.size());
                if (!artene.contains(artListe.get(nr).getArt())) {
                    if (artListe.get(nr).getArt().length() > 2) {
                        artene.add(artListe.get(nr).getArt());
                    }
                }
            }

            // Shuffle alternatives
            Collections.shuffle(artene);

            // Insert alternatives
            view_alternativ1.setText(artene.get(0));
            view_alternativ2.setText(artene.get(1));
            view_alternativ3.setText(artene.get(2));
            view_alternativ4.setText(artene.get(3));

            // Starter tiden
            tid_start = System.currentTimeMillis() / 1000.0;
            view_alternativ1.setOnClickListener(v -> sjekkSvar(view_alternativ1));
            view_alternativ2.setOnClickListener(v -> sjekkSvar(view_alternativ2));
            view_alternativ3.setOnClickListener(v -> sjekkSvar(view_alternativ3));
            view_alternativ4.setOnClickListener(v -> sjekkSvar(view_alternativ4));

        }
    }

    private void visBilde(ArrayList<Media> bildeListe) {
        if (bildeListe == null){
            view_bilde_mangler.setText(MessageFormat.format("{0}\nmangler bilder", artListe.get(nr_valgt).getFamilie()));
            view_bilde_mangler.setVisibility(View.VISIBLE);
        } else {
            view_bilde_mangler.setVisibility(View.GONE);
            ArrayList<Integer> bilde_id = new ArrayList<>();
            for (int n = 0; n < bildeListe.size(); n++) {
                if ((bildeListe.get(n).getGbif() == artListe.get(nr_valgt).getId())
                        & (!bildeListe.get(n).getMediefil().contains("95")) // Sportegn bilder
                        & (!bildeListe.get(n).getMediefil().contains("96"))
                        & (!bildeListe.get(n).getMediefil().contains("97"))
                        & (!bildeListe.get(n).getMediefil().contains("98")) // Sportegn tegnet
                        & (!bildeListe.get(n).getMediefil().contains("99")) // Sportegn tegnet med info
                ) {
                    bilde_id.add(n);
                }
            }

            int bilde_nr = bilde_id.get((int) (Math.random() * bilde_id.size()));
            bildeurl = bildeListe.get(bilde_nr).getMediaurl();
            view_bilde.setImageUrl(bildeurl, AppKontroll.getInstance().getImageLoader());
            view_cc_eier.setText(bildeListe.get(bilde_nr).getEier());
            lagLinkLisens(view_cc_lisens, bildeListe.get(bilde_nr).getLisens(), bildeListe.get(bilde_nr).getLisensLenke());
            lagLink(view_cc_lenke, bildeListe.get(bilde_nr).getLenke());

            view_cc.setOnClickListener(view -> view_cc_lag.setVisibility(View.VISIBLE));
            view_cc_stor.setOnClickListener(view -> view_cc_lag.setVisibility(View.VISIBLE));
            view_cc_lukk.setOnClickListener(view -> view_cc_lag.setVisibility(View.GONE));
            view_bakgrunn.setOnClickListener(view -> view_cc_lag.setVisibility(View.GONE));
        }
    }

    // Lenker til originalbilde og mer informasjon
    private void lagLink(ImageView view, String lenke) {
        if (!lenke.equals("")) {
            view.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(lenke))));
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    // Lenker til lisens
    private void lagLinkLisens(TextView view, String lisens, String lenke) {
        view.setText(lisens);
        if (lenke.equals("Offentlig eiendom") | lenke.equals("Tillatelse gitt")){
            view.getPaint().setUnderlineText(false);
            view.setOnClickListener(null);
            view.setVisibility(View.VISIBLE);
        } else if (!lenke.equals("")) {
            view.getPaint().setUnderlineText(true);
            view.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(lenke))));
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }


    private void fargelegg() {
        // Farge på menylinjen
        Objects.requireNonNull(getSupportActionBar())
                .setBackgroundDrawable(new ColorDrawable(farge.getFarge()));

        switch (innstillinger.getGruppe()){
            case "Fugler":
                view_tittel.setText(R.string.hvilken_fugl);
                view_type.setImageDrawable(farge.symbolFarge(R.drawable.fugl));
                break;
            case "Blomster":
                view_tittel.setText(R.string.hvilken_blomst);
                view_type.setImageDrawable(farge.symbolFarge(R.drawable.blomst));
                break;
            case "Trær":
                view_tittel.setText(R.string.hvilket_tre);
                view_type.setImageDrawable(farge.symbolFarge(R.drawable.tre));
                break;
            case "Bregner":
                view_tittel.setText(R.string.hvilken_bregne);
                view_type.setImageDrawable(farge.symbolFarge(R.drawable.bregne));
                break;
            case "Sopp":
                view_tittel.setText(R.string.hvilken_sopp);
                view_type.setImageDrawable(farge.symbolFarge(R.drawable.sopp));
                break;
            case "Sommerfugler":
                view_tittel.setText(R.string.hvilken_sommerfugl);
                view_type.setImageDrawable(farge.symbolFarge(R.drawable.insekt_sommerfugl));
                break;
            case "Edderkopper":
                view_tittel.setText(R.string.hvilken_edderkopp);
                view_type.setImageDrawable(farge.symbolFarge(R.drawable.edderkopp));
                break;
            case "Insekter":
                view_tittel.setText(R.string.hvilket_insekt);
                view_type.setImageDrawable(farge.symbolFarge(R.drawable.insekt));
                break;
            case "Dyr":
                view_tittel.setText(R.string.hvilket_dyr);
                view_type.setImageDrawable(farge.symbolFarge(R.drawable.dyr_ekorn));
                break;
        }

        view_bakgrunn.setBackground(farge.getGradient());
        view_alternativ1.setBackground(farge.getBakgrunn());
        view_alternativ2.setBackground(farge.getBakgrunn());
        view_alternativ3.setBackground(farge.getBakgrunn());
        view_alternativ4.setBackground(farge.getBakgrunn());
        view_tittel.setTextColor(farge.getFarge());
        view_poeng.setTextColor(farge.getFarge());
        view_antall.setTextColor(farge.getFarge());
        view_alternativ1.setPadding(0,30,0,30);
        view_alternativ2.setPadding(0,30,0,30);
        view_alternativ3.setPadding(0,30,0,30);
        view_alternativ4.setPadding(0,30,0,30);
        view_bilde_stor.setBackground(farge.getGradient());
        view_hjelp_tekst.setTextColor(farge.getFarge());

        view_cc.setImageDrawable(farge.symbolLysFarge(R.drawable.symbol_cc));
        view_cc_stor.setImageDrawable(farge.symbolFarge(R.drawable.symbol_cc));

    }

    // Zoom inn
    private void zoom_inn() {
        view_cc_lag.setVisibility(View.GONE);
        if (artListe != null) {
            view_bilde_stor.setImageUrl(bildeurl, AppKontroll.getInstance().getImageLoader());
            view_bilde_stor.setVisibility(View.VISIBLE);
            view_cc_stor.setVisibility(View.VISIBLE);
            vis_meny = false;
            invalidateOptionsMenu();
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(false); // tilbakeknapp fjernes
        }
    }

    // Zoom ut
    private void zoom_ut() {
        view_bilde_stor.setVisibility(View.INVISIBLE);
        view_cc_stor.setVisibility(View.INVISIBLE);
        view_cc_lag.setVisibility(View.GONE);
        vis_meny = true;
        invalidateOptionsMenu();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbakeknapp legges til igjen
    }

    private void ferdig() {
        startActivity(new Intent(Spm_bilder.this,Resultat.class));
        finish();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (lagret.getNrSpm() > innstillinger.getAntallSpmTotalt()) {
            lagret.setPoeng(0);
            lagret.setNrSpm(1);
            view_poeng.setText(String.format(getString(R.string.poeng_d), lagret.getPoeng()));
            view_antall.setText(String.format(getString(R.string.spm_av_total), lagret.getNrSpm(), innstillinger.getAntallSpmTotalt()));
            oppdaterSpm();
        }
    }

    private void hjelp() {
        int antall = 3;
        final int[] nr = {0};
        if (dh == 0) { dh = view_hjelp_tekst.getY() - view_hjelp_finger.getY(); }

        // Lager en animasjon av en finger som trykker
        anim = new ScaleAnimation(
                1.1f, 1f, // Start and end values for the X axis scaling
                1.1f, 1f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 1f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0f); // Pivot point of Y scaling
        anim.setFillAfter(false); // Needed to keep the result of the animation
        anim.setDuration(1500);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(1);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view_hjelp_tekst.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (nr[0] < antall) {
                    startHjelp(nr[0]);
                    nr[0] += 1;
                } else {
                    view_hjelp_tekst.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        startHjelp(nr[0]);
        nr[0] += 1;

    }

    private void startHjelp(int h_nr) {
        if (h_nr == 0) {
            // Zoom inn
            view_hjelp_finger.setRotation(30);
            view_hjelp_finger.setX(view_bilde.getX() + view_bilde.getWidth()*0.2f);
            view_hjelp_finger.setY(view_bilde.getY() + view_bilde.getHeight()*0.2f);
            view_hjelp_tekst.setText(R.string.klikk_forstorre);
            view_hjelp_tekst.setX(view_hjelp_finger.getX() - view_hjelp_tekst.getWidth()*0.2f);
            view_hjelp_tekst.setY(view_hjelp_finger.getY() + dh);
            view_hjelp_finger.startAnimation(anim);
        } else if (h_nr == 1){
            // Zoom ut
            zoom_inn();
            view_hjelp_tekst.setText(R.string.klikk_forminske);
            view_hjelp_finger.startAnimation(anim);
        } else if (h_nr == 2){
            zoom_ut();
            // Gjett art
            view_hjelp_finger.setRotation(30);
            view_hjelp_finger.setX(view_alternativ2.getX() + view_alternativ2.getWidth()*0.2f);
            view_hjelp_finger.setY(view_alternativ2.getY() + view_alternativ2.getHeight()*0.2f);
            view_hjelp_tekst.setX(view_hjelp_finger.getX() - view_hjelp_tekst.getWidth()*0.2f);
            view_hjelp_tekst.setY(view_hjelp_finger.getY() + dh);
            view_hjelp_tekst.setText(R.string.gjett_art);
            view_hjelp_finger.startAnimation(anim);
        }
    }
    //setContentView(R.layout.bilder);

}

