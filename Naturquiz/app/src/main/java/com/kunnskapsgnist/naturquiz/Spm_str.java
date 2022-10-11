package com.kunnskapsgnist.naturquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.kunnskapsgnist.naturquiz.data.AppKontroll;
import com.kunnskapsgnist.naturquiz.data.Artsbank;
import com.kunnskapsgnist.naturquiz.data.Mediabank;
import com.kunnskapsgnist.naturquiz.databinding.SpmStrBinding;
import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Farge;
import com.kunnskapsgnist.naturquiz.informasjon.Innstillinger;
import com.kunnskapsgnist.naturquiz.informasjon.Lagret;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;
import com.kunnskapsgnist.naturquiz.informasjon.Poeng;
import com.kunnskapsgnist.naturquiz.informasjon.Randomisere;
import com.kunnskapsgnist.naturquiz.modell.Art;
import com.kunnskapsgnist.naturquiz.modell.Media;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Spm_str extends AppCompatActivity {
    private SpmStrBinding binding;
    private static final String TAG = "Naturquiz";

    TextView view_tittel, view_tekst, view_poeng, view_antall,
            view_art_nr1, view_art_nr2, view_art_nr3, view_art_nr4,
            view_cc_eier, view_cc_lisens, view_hjelp_tekst;
    CardView view_kort_nr1, view_kort_nr2, view_kort_nr3, view_kort_nr4;
    NetworkImageView view_bilde_nr1, view_bilde_nr2, view_bilde_nr3, view_bilde_nr4, view_bilde_stor;
    ImageView view_hjelp_finger, view_internett, view_cc_stor, view_lukk_stor, view_cc_lenke, view_cc_lukk;
    Button view_svar;
    ConstraintLayout view_bakgrunn;
    LinearLayout view_cc_lag;

    CardView[] view_kortene;
    NetworkImageView[] view_bildene;
    TextView[] view_artene;

    float dY,y0 = 0, z0 = 0;                                // Brukt i OnTouch
    private final float[] y0_kort  = new float[4];          // Plassering av kortene
    private boolean start = true;                           // Plassering av items i starten
    private double tid_start;
    private float dh;
    private boolean vis_meny;
    float faktor;

    private final int[] nr_arter = new int[4];              // Valgt arts-nr
    private double[] str_arter = new double[4];       // Valgt arts størrelse (vekt/vingespenn/lengde/etc.)
    private int[] ny_kort = {0,1,2,3};                      // Kortenes rekkefølge etter at de er flyttet
    private final int[] ny_sjekk = new int[4];              // Kortenes rekkefølge ved forrige sjekk
    private final int[] riktig = new int[4];                // Markør på om spillene har vært plassert riktig
    private final String[] bildeurl = new String[4];        // Bildenes url
    private final String[] bildeeier = new String[4];       // Bildenes eier
    private final String[] bildelisens = new String[4];     // Bildenes lisens
    private final String[] bildelisenslenke = new String[4];// Bildenes lenker til lisens
    private final String[] bildelenke = new String[4];      // Bildenes lenke

    private int poengMulig = 3;                             // Maks antall poeng per spørsmål
    List<Art> artListe;                                     // Hele listen

    Poeng poeng;

    Innstillinger innstillinger;                            // Tilgang til innstillinger
    BrukerInfo brukerInfo;
    Farge farge;
    Lagret lagret;                                          // Tilgang til lagrede parametere
    Menybar menybar;                                        // Tilgang til menybar
    ActionBar actionBar;                                    // Actionbar øverst
    Animation anim,animDrag;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(Spm_str.this, R.layout.spm_str);

        view_bakgrunn = binding.strBakgrunn;
        view_tittel = binding.strTittel;
        view_tekst = binding.strTekst;
        view_poeng = binding.strPoeng;
        view_antall = binding.strAntall;
        view_svar = binding.strSvar;
        view_internett = binding.strInternett;

        view_kort_nr1 = binding.strKortNr1;
        view_kort_nr2 = binding.strKortNr2;
        view_kort_nr3 = binding.strKortNr3;
        view_kort_nr4 = binding.strKortNr4;

        view_art_nr1 = binding.strArtNr1;
        view_art_nr2 = binding.strArtNr2;
        view_art_nr3 = binding.strArtNr3;
        view_art_nr4 = binding.strArtNr4;

        view_bilde_nr1 = binding.strBildeNr1;
        view_bilde_nr2 = binding.strBildeNr2;
        view_bilde_nr3 = binding.strBildeNr3;
        view_bilde_nr4 = binding.strBildeNr4;
        view_bilde_stor = binding.strBildeStor;

        view_lukk_stor = binding.strStorLukk;
        view_cc_stor = binding.strStorCc;
        view_cc_lag = binding.strCcLag;
        view_cc_eier = binding.strCcEier;
        view_cc_lisens = binding.strCcLisens;
        view_cc_lenke = binding.strCcLenke;
        view_cc_lukk = binding.strCcLukk;

        view_kortene = new CardView[]{view_kort_nr1, view_kort_nr2, view_kort_nr3, view_kort_nr4};
        view_bildene = new NetworkImageView[]{view_bilde_nr1, view_bilde_nr2, view_bilde_nr3, view_bilde_nr4};
        view_artene = new TextView[]{view_art_nr1, view_art_nr2, view_art_nr3, view_art_nr4};

        view_hjelp_finger = binding.strHjelpFinger;
        view_hjelp_tekst = binding.strHjelpTekst;

        // Initierer
        innstillinger = new Innstillinger(this);
        brukerInfo = new BrukerInfo(this);
        lagret = new Lagret(this);
        lagret.setNrSpm(1);
        lagret.setPoeng(0);
        poeng = new Poeng();
        farge = new Farge(this,innstillinger.getType());

        dh = 0;
        fargelegg();

        // Starttekster
        view_poeng.setText(String.format(getString(R.string.poeng_d), lagret.getPoeng()));
        skrivTekst();
        if (isConnectingToInternet()) {
            new Artsbank(innstillinger.getLevel(), innstillinger.getType(), innstillinger.getGruppe())
                    .getArter(artListe -> {
                        this.artListe = artListe;
                        oppdaterSpm();
                    });

            // Flyttbare items og klikkbare spillbokser
            for (int n = 0; n < 4; n++) {
                int n_kort = n;
                view_kortene[n_kort].setOnTouchListener(this::onTouchEvent);
                view_bildene[n_kort].setOnClickListener(view -> zoom_inn(n_kort));
            }
            view_bilde_stor.setOnClickListener(view -> {
                if (view_cc_lag.getVisibility() == View.GONE)
                    zoom_ut();
                view_cc_lag.setVisibility(View.GONE);
            });
            view_lukk_stor.setOnClickListener(view -> zoom_ut());

        } else Toast.makeText(this,R.string.mangler_internett,Toast.LENGTH_LONG).show();

        // Actionbar
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbake
        menybar = new Menybar("Spm_str");
        vis_meny = true;
        invalidateOptionsMenu();
    }

    private void fargelegg() {
        // Farge på menylinjen
        Objects.requireNonNull(getSupportActionBar())
                .setBackgroundDrawable(new ColorDrawable(farge.getFarge()));

        view_bakgrunn.setBackground(farge.getGradient());
        view_kort_nr1.setBackground(farge.getBakgrunn());
        view_kort_nr2.setBackground(farge.getBakgrunn());
        view_kort_nr3.setBackground(farge.getBakgrunn());
        view_kort_nr4.setBackground(farge.getBakgrunn());
        view_tittel.setTextColor(farge.getFarge());
        view_tekst.setTextColor(farge.getFarge());
        view_poeng.setTextColor(farge.getFarge());
        view_antall.setTextColor(farge.getFarge());
        view_svar.setBackgroundColor(farge.getFarge());
        view_bilde_stor.setBackground(farge.getGradient());
        view_hjelp_tekst.setTextColor(farge.getFarge());

        view_lukk_stor.setImageDrawable(farge.symbolFarge(R.drawable.symbol_lukk));
        view_cc_stor.setImageDrawable(farge.symbolFarge(R.drawable.symbol_cc));
    }

    // Zoom inn
    private void zoom_inn(int n_kort) {
        if (artListe != null) {
            view_bilde_stor.setImageUrl(bildeurl[n_kort], AppKontroll.getInstance().getImageLoader());
            view_bilde_stor.setVisibility(View.VISIBLE);
            view_lukk_stor.setVisibility(View.VISIBLE);
            view_cc_stor.setVisibility(View.VISIBLE);

            view_cc_eier.setText(bildeeier[n_kort]);
            lagLinkLisens(view_cc_lisens, bildelisens[n_kort], bildelisenslenke[n_kort]);
            lagLink(view_cc_lenke, bildelenke[n_kort]);

            view_cc_stor.setOnClickListener(view -> {
                if (view_cc_lag.getVisibility() == View.VISIBLE)
                    view_cc_lag.setVisibility(View.GONE);
                else
                    view_cc_lag.setVisibility(View.VISIBLE);
            });
            view_cc_lukk.setOnClickListener(view -> view_cc_lag.setVisibility(View.GONE));

            vis_meny = false;
            invalidateOptionsMenu();
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(false); // tilbakeknapp fjernes
        }
    }

    // Zoom ut
    private void zoom_ut() {
        view_bilde_stor.setVisibility(View.INVISIBLE);
        view_lukk_stor.setVisibility(View.INVISIBLE);
        view_cc_stor.setVisibility(View.INVISIBLE);
        view_cc_lag.setVisibility(View.GONE);

        vis_meny = true;
        invalidateOptionsMenu();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbakeknapp legges til igjen
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

    // Sjekker om appen er koblet til internett (det kan tenkes den er koblet til et nett som ikke er koblet til internett)
    private boolean isConnectingToInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null){
            view_tekst.setText(R.string.mangler_internett);
            view_kort_nr1.setVisibility(View.INVISIBLE);
            view_kort_nr2.setVisibility(View.INVISIBLE);
            view_kort_nr3.setVisibility(View.INVISIBLE);
            view_kort_nr4.setVisibility(View.INVISIBLE);
            view_internett.setVisibility(View.VISIBLE);
            view_svar.setVisibility(View.INVISIBLE);
        } else {
            skrivTekst();
            view_kort_nr1.setVisibility(View.VISIBLE);
            view_kort_nr2.setVisibility(View.VISIBLE);
            view_kort_nr3.setVisibility(View.VISIBLE);
            view_kort_nr4.setVisibility(View.VISIBLE);
            view_internett.setVisibility(View.GONE);
            view_svar.setVisibility(View.VISIBLE);
        }
        return ni != null;
    }

    private void sjekkSvar() {
        view_cc_lag.setVisibility(View.GONE);
        if (artListe != null) {
            view_svar.setOnClickListener(null);

            for (int n = 0; n < 4; n++) {
                double lengde = artListe.get(nr_arter[n]).getStr(innstillinger.getKategori());
                if (lengde == str_arter[ny_kort[n]]) {
                    riktig[n] = 1;
                } else {
                    riktig[n] = -1;
                }
                ny_sjekk[n] = ny_kort[n];
            }

            RotateAnimation animasjonFeil = new RotateAnimation(
                    -5, 5,
                    Animation.RELATIVE_TO_SELF, (float) 0.5,
                    Animation.RELATIVE_TO_SELF, (float) 0.5);
            animasjonFeil.setDuration(200);
            animasjonFeil.setRepeatCount(2);
            animasjonFeil.setRepeatMode(Animation.REVERSE);

            animasjonFeil.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view_svar.setOnClickListener(v -> sjekkSvar());
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });

            int sum = 0;
            for (int n = 0; n < 4; n++) {
                if (riktig[n] == 1) {
                    view_artene[n].setTextColor(getColor(R.color.gronn_klar));
                } else {
                    view_artene[n].setTextColor(getColor(R.color.rod));
                    view_kortene[n].setAnimation(animasjonFeil);
                }
                sum += (riktig[n] + 1) / 2;
            }

            view_tekst.setTypeface(null, Typeface.BOLD);
            if (sum == 4) {
                view_tekst.setTextColor(getColor(R.color.gronn_klar));
                view_tekst.setText(R.string.riktig);
                riktig();
            } else {
                poengMulig -= 1;
                if (poengMulig < 0) {
                    poengMulig = 0;
                }

                if (sum > 0) {
                    view_tekst.setText(String.format(getString(R.string.noen_rett_prov_igjen), sum));
                } else {
                    view_tekst.setText(getString(R.string.ingen_rett_prov_igjen));
                }
                view_tekst.setTextColor(getColor(R.color.rod));
                view_tekst.setAnimation(animasjonFeil);
                view_tekst.startAnimation(animasjonFeil);
            }
        }
    }

    private void riktig() {
        double tid = 0.5*(System.currentTimeMillis() / 1000.0 - tid_start) - 1.0;
        lagret.setNrSpm(lagret.getNrSpm() + 1);
        int poeng_runden = poengMulig * poeng.faktor(tid, innstillinger.getAntallSpmTotalt());
        lagret.setPoeng(lagret.getPoeng() + poeng_runden);
        view_poeng.setText(MessageFormat.format("Poeng: + {0}", poeng_runden));
        view_tekst.setText(R.string.riktig);
        poengMulig = 0; // unngå å trykke flere ganger før neste spm

        AlphaAnimation animasjonRiktig = new AlphaAnimation(1.0f,0.5f);
        animasjonRiktig.setDuration(400); // milliseconds
        animasjonRiktig.setRepeatCount(2);
        animasjonRiktig.setRepeatMode(Animation.REVERSE);

        view_poeng.setAnimation(animasjonRiktig);
        view_tekst.setAnimation(animasjonRiktig);
        view_poeng.startAnimation(animasjonRiktig);
        view_tekst.startAnimation(animasjonRiktig);

        animasjonRiktig.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view_poeng.setTextColor(getColor(R.color.gronn_klar));
                view_tekst.setTextColor(getColor(R.color.gronn_klar));
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view_poeng.setText(MessageFormat.format("Poeng: {0}", lagret.getPoeng()));
                view_poeng.setTextColor(farge.getFarge());
                if(lagret.getNrSpm() <= innstillinger.getAntallSpmTotalt()) {
                    oppdaterSpm();
                } else {
                    lengdeFerdig();
                }
                poengMulig = 3;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        if (vis_meny) {
            getMenuInflater().inflate(R.menu.meny, menu);
            getMenuInflater().inflate(R.menu.hjelp, menu);
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

    private void oppdaterSpm(){
        if (isConnectingToInternet() & artListe != null) {
            int n;
            int antall = innstillinger.getAntallSpmTotalt();
            view_antall.setText(String.format(getString(R.string.spm_av_total), lagret.getNrSpm(), antall));

            // Sprer kortene fint utover når layouten er klar
            if (start) {
                if (view_tekst.getHeight() == 0)
                    new Handler().postDelayed(this::startPosisjoner, 100);
                else startPosisjoner();
            }

            // Rydder
            view_tekst.setTextColor(farge.getFarge());
            skrivTekst();
            view_tekst.setTypeface(null, Typeface.NORMAL);
            for (n = 0; n < 4; n++) {
                riktig[n] = 0;
                ny_sjekk[n] = -1;
                ny_kort[n] = n;
                view_artene[n].setTextColor(getColor(R.color.hvit));
                view_kortene[n].setY(y0_kort[n]);
            }

            // Finner fire arter
            n = 0;
            str_arter = new double[]{0,0,0,0};
            while (n < 4) {
                int nr = (int) (Math.random() * (artListe.size() - 1));
                double str = artListe.get(nr).getStr(innstillinger.getKategori());
                Log.d(TAG, "oppdaterSpm: " + n + " " + artListe.get(nr).getArt() + " " + str);
                int ok = 1;
                if (str == 0) ok = 0;
                for (int m = 0; m < 4; m++) {
                    if ((str_arter[m] > (1-faktor)*str) & (str_arter[m] < (1+faktor)*str)) {
                        ok = 0;
                        break;
                    }
                }
                if (ok == 1) {
                    nr_arter[n] = nr;
                    str_arter[n] = str;
                    n += 1;
                }
            }

            Randomisere.sortDouble(str_arter);

            for (n = 0; n < 4; n++) {
                // Bilde
                int n_kort = n;
                new Mediabank(innstillinger.getType(), artListe.get(nr_arter[n]).getFamilie())
                        .getMedier(media -> visBilde(media, n_kort,nr_arter[n_kort]));
            }

            // Starter tiden
            tid_start = System.currentTimeMillis() / 1000.0;
            view_svar.setOnClickListener(v -> sjekkSvar());
        }
    }

    private void skrivTekst() {
        Log.d(TAG, "skrivTekst: " + innstillinger.getType());
        faktor = 0.1f;
        if (innstillinger.getType().equals("Fugl") & innstillinger.getKategori().equals("vingespenn")) {
            view_tittel.setText(R.string.vingespenn);
            view_tekst.setText(R.string.plasser_fuglene_fra_kortest_til_lengst);
        } else if (innstillinger.getType().equals("Fugl") & innstillinger.getKategori().equals("vekt")) {
            view_tittel.setText(R.string.vekt);
            view_tekst.setText(R.string.plasser_fuglene_fra_lettest_til_tyngst);
        } else if (innstillinger.getGruppe().equals("Sommerfugler") & innstillinger.getKategori().equals("vingespenn")) {
            view_tittel.setText(R.string.vingespenn);
            view_tekst.setText(R.string.plasser_sommerfuglene_fra_kortest_til_lengst);
            faktor = 0.05f;
        } else if (innstillinger.getType().equals("Dyr") & innstillinger.getKategori().equals("lengde")) {
            view_tittel.setText(R.string.lengde);
            view_tekst.setText(R.string.plasser_dyrene_fra_minst_til_lengst);
        } else if (innstillinger.getType().equals("Dyr") & innstillinger.getKategori().equals("vekt")) {
            view_tittel.setText(R.string.vekt);
            view_tekst.setText(R.string.plasser_dyrene_fra_lettest_til_tyngst);
        }
    }

    private void visBilde(ArrayList<Media> bildeListe, int n_kort, int nr) {
        if (bildeListe == null){
            view_bildene[n_kort].setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.fugl));
        } else {
            for (int n = 0; n < bildeListe.size(); n++) {
                if (bildeListe.get(n).getGbif() == artListe.get(nr).getId()) {
                    bildeurl[n_kort] = bildeListe.get(n).getMediaurl();
                    bildeeier[n_kort] = bildeListe.get(n).getEier();
                    bildelisens[n_kort] = bildeListe.get(n).getLisens();
                    bildelisenslenke[n_kort] = bildeListe.get(n).getLisensLenke();
                    bildelenke[n_kort] = bildeListe.get(n).getLisensLenke();
                    view_bildene[n_kort].setImageUrl(bildeurl[n_kort], AppKontroll.getInstance().getImageLoader());
                    break;
                }
            }
            view_artene[n_kort].setText(artListe.get(nr).getArt());
        }
    }

    // Sprer kortene fint utover
    private void startPosisjoner() {
        view_tekst.setHeight(view_tekst.getHeight()); // Fryser høyden til teksten
        float y0 = view_tekst.getY() + view_tekst.getHeight();
        float y1 = view_svar.getY();
        float dy = (y1 - y0 - 4 * view_kortene[0].getHeight()) / 5;
        for (int n = 0; n < 4; n++) {
            y0_kort[n] = y0 + dy + n * (dy + view_kortene[0].getHeight());
        }
        z0 = view_kortene[0].getElevation();
        start = false;
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(View view, MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                view_svar.setEnabled(false);
                y0 = view.getY();
                dY = view.getY() - event.getRawY();

                view.setElevation(z0 + 1);
                break;

            case MotionEvent.ACTION_MOVE:
                view.setY(event.getRawY() + dY);

                // Flytter inn på skjermen igjen
                if (view.getY() < 0){
                    view.setY(0);
                }
                if (view.getY() + view.getHeight()
                        > view_svar.getY() + view_svar.getHeight()){
                    view.setY(view_svar.getY() + view_svar.getHeight() - view.getHeight());
                }

                break;

            case MotionEvent.ACTION_UP:
                view.setElevation(z0);
                float[] y_pos = new float[4];
                for (int n = 0; n < 4; n++) {
                    y_pos[n] = view_kortene[n].getY();  // - henter posisjonene
                }
                ny_kort = Randomisere.sortFloat(y_pos); // - sorterer posisjonene fra minst til størst

                for (int n = 0; n < 4; n++) {           // - animasjon av alle kort på plass
                    view_kortene[n].animate().y(y0_kort[ny_kort[n]]).
                            setDuration(300).start();
                }

                // Fargelegger dersom vi har noe informasjon fra forrige sjekk
                for (int n = 0; n < 4; n++) {
                    if (ny_kort[n] == ny_sjekk[n]) {
                        if (riktig[n] == -1) {
                            view_artene[n].setTextColor(getResources().getColor(R.color.rod,null));
                        } else if (riktig[n] == 1) {
                            view_artene[n].setTextColor(getColor(R.color.gronn_klar));
                        }
                    } else {
                        view_artene[n].setTextColor(getColor(R.color.hvit));
                    }
                }
                view_svar.setEnabled(true);
                break;
            default:
                return false;
        }
        return true;
    }

    private void lengdeFerdig() {
        startActivity(new Intent(this,Resultat.class));
        finish();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (lagret.getPoeng() > innstillinger.getAntallSpmTotalt()) {
            lagret.setPoeng(0);
            lagret.setNrSpm(1);
            view_poeng.setText(String.format(getString(R.string.poeng_d), lagret.getPoeng()));
            view_antall.setText(String.format(getString(R.string.spm_av_total), lagret.getNrSpm(), innstillinger.getAntallSpmTotalt()));
            oppdaterSpm();
        }
    }

    // --------------- Hjelpende hånd ------------------------
    private void hjelp() {
        int antall = 3;
        final int[] nr = {0};

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
                    nr[0] += 1;
                    startHjelp(nr[0]);
                } else {
                    view_hjelp_tekst.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        animDrag = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1);
        animDrag.setFillAfter(false); // Needed to keep the result of the animation
        animDrag.setDuration(1500);
        animDrag.setRepeatMode(Animation.REVERSE);
        animDrag.setRepeatCount(1);
        animDrag.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (nr[0] < antall) {
                    nr[0] += 1;
                    startHjelp(nr[0]);
                } else {
                    view_hjelp_tekst.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        startHjelp(nr[0]);

    }

    // Pekende finger
    private void startHjelp(int h_nr) {
        if (dh == 0) { dh = view_hjelp_tekst.getY() - view_hjelp_finger.getY(); }
        if (h_nr == 0) {
            // Drag
            view_kortene[0].setCardElevation(z0+1);
            view_hjelp_tekst.setText(R.string.hold_og_dra_for_a_bytte_plass);
            view_kortene[0].startAnimation(animDrag);
            view_hjelp_finger.startAnimation(animDrag);
            view_hjelp_tekst.startAnimation(animDrag);
        } else if (h_nr == 1){
            view_kortene[0].setCardElevation(z0);
            // Forstørr
            view_hjelp_finger.setX(view_kort_nr2.getX() + 0.2f*view_hjelp_finger.getWidth());
            view_hjelp_finger.setY(view_kort_nr2.getY());
            view_hjelp_tekst.setText(R.string.klikk_forstorre);
            view_hjelp_finger.startAnimation(anim);
        } else if (h_nr == 2){
            zoom_inn(1);
            // Forminsk
            view_hjelp_tekst.setText(R.string.klikk_forminske);
            view_hjelp_finger.startAnimation(anim);
        } else if (h_nr == 3){
            zoom_ut();
            // Submit
            view_hjelp_finger.setX(view_svar.getX());
            view_hjelp_finger.setY(view_svar.getY());
            view_hjelp_tekst.setText(R.string.avgi_svar);
            view_hjelp_finger.startAnimation(anim);
        }
        view_hjelp_tekst.setX(view_hjelp_finger.getX() - view_hjelp_finger.getWidth()*0.2f);
        view_hjelp_tekst.setY(view_hjelp_finger.getY() + dh);
    }
}