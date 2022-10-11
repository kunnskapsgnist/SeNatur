package com.kunnskapsgnist.naturquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.kunnskapsgnist.naturquiz.data.AppKontroll;
import com.kunnskapsgnist.naturquiz.data.Artsbank;
import com.kunnskapsgnist.naturquiz.databinding.SpmFotsporBinding;
import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Farge;
import com.kunnskapsgnist.naturquiz.informasjon.Innstillinger;
import com.kunnskapsgnist.naturquiz.informasjon.Lagret;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;
import com.kunnskapsgnist.naturquiz.informasjon.Poeng;
import com.kunnskapsgnist.naturquiz.informasjon.Randomisere;
import com.kunnskapsgnist.naturquiz.modell.Art;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Spm_fotspor extends AppCompatActivity implements View.OnTouchListener {
    private static final String TAG = "Naturquiz";
    private SpmFotsporBinding binding;

    TextView view_tittel, view_tekst, view_poeng, view_antall, view_hjelp_tekst,
            view_navn1, view_navn2, view_navn3, view_navn4;
    NetworkImageView view_stort_bilde;
    NetworkImageView view_art1, view_art2, view_art3, view_art4;
    NetworkImageView view_spor1, view_spor2, view_spor3, view_spor4;
    ImageView view_hjelp_finger, view_internett;
    Button view_svar;

    NetworkImageView[] view_sporene;
    NetworkImageView[] view_artene;
    TextView[] view_navnene;

    List<Art> art_liste;                                    // Hele artslisten
    private int poengMulig = 3;                             // Maks antall poeng per spørsmål
    double tid_start;
    float dX,dY,x0 = 0, y0 = 0,z0 = 0, dh = 0;              // Brukt i OnTouch
    private final float[] x0_part = new float[4];           // Plassering av ting
    private float y0_part = 0;                              // Plassering av ting
    private boolean start = true;
    private int dy_art;
    private boolean vis_meny;

    private final int[] nr_art = new int[4];                 // Artenes nr
    private final int[] nr_spor = new int[4];                // Sporenes nr

    Poeng poeng;

    Innstillinger innstillinger;                            // Tilgang til innstillinger
    BrukerInfo brukerInfo;
    Lagret lagret;                                          // Tilgang til lagrede parametere
    Menybar menybar;                                        // Tilgang til menybar
    ActionBar actionBar;                                    // Actionbar øverst

    Animation anim,animDrag;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.spm_fotspor);

        view_tittel = binding.fotsporTittel;
        view_tekst = binding.fotsporTekst;
        view_stort_bilde = binding.fotsporStortBilde;
        view_svar = binding.fotsporSvar;
        view_poeng = binding.fotsporPoeng;
        view_antall = binding.fotsporAntall;
        view_internett = binding.fotsporInternett;

        view_art1 = binding.fotsporArtNr1;
        view_art2 = binding.fotsporArtNr2;
        view_art3 = binding.fotsporArtNr3;
        view_art4 = binding.fotsporArtNr4;

        view_spor1 = binding.fotsporSporNr1;
        view_spor2 = binding.fotsporSporNr2;
        view_spor3 = binding.fotsporSporNr3;
        view_spor4 = binding.fotsporSporNr4;

        view_navn1 = binding.fotsporNavnNr1;
        view_navn2 = binding.fotsporNavnNr2;
        view_navn3 = binding.fotsporNavnNr3;
        view_navn4 = binding.fotsporNavnNr4;

        view_artene = new NetworkImageView[]{view_art1, view_art2, view_art3, view_art4};
        view_sporene = new NetworkImageView[]{view_spor1, view_spor2, view_spor3, view_spor4};
        view_navnene = new TextView[]{view_navn1, view_navn2, view_navn3, view_navn4};

        view_hjelp_finger = binding.fotsporHjelpFinger;
        view_hjelp_tekst = binding.fotsporHjelpTekst;

        // Initierer
        innstillinger = new Innstillinger(this);
        brukerInfo = new BrukerInfo(this);
        lagret = new Lagret(this);
        lagret.setNrSpm(1);
        lagret.setPoeng(0);
        poeng = new Poeng();

        fargelegg();

        // Starttekster
        view_tekst.setText(R.string.dra_fotspor_til_riktig_dyr);
        view_poeng.setText(String.format(getString(R.string.poeng_d), lagret.getPoeng()));
        if (isConnectingToInternet()) {
            new Artsbank(innstillinger.getLevel(),innstillinger.getType(),innstillinger.getGruppe())
                    .getArter(art_liste -> {
                        this.art_liste = art_liste;
                        oppdaterSpm();
                    });

            // Flyttbare ting og klikkbare spillbokser
            for (int n = 0; n < 4; n++){
                int finalN = n;
                view_sporene[n].setOnTouchListener(this);
                view_artene[n].setOnClickListener(v ->
                        zoom_inn(nr_art[finalN],"dyr"));
            }

            view_stort_bilde.setOnClickListener(v -> zoom_ut());
            view_svar.setOnClickListener(v -> sjekkSvar());
        } else Toast.makeText(this,R.string.mangler_internett,Toast.LENGTH_LONG).show();

        // Actionbar
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbake
        vis_meny = true;
        menybar = new Menybar("Which_parts");
    }

    private void fargelegg() {
        Farge farge = new Farge(this,innstillinger.getType());
        Objects.requireNonNull(getSupportActionBar())
                .setBackgroundDrawable(new ColorDrawable(farge.getFarge()));
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        if (vis_meny) {
            getMenuInflater().inflate(R.menu.meny, menu);
            getMenuInflater().inflate(R.menu.hjelp, menu);
            MenuCompat.setGroupDividerEnabled(menu, true);
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
            view_tekst.setText(R.string.mangler_internett);
            for (int n = 0; n < 4; n++){
                view_artene[n].setVisibility(View.INVISIBLE);
                view_sporene[n].setVisibility(View.INVISIBLE);
            }
            view_internett.setVisibility(View.VISIBLE);
            view_svar.setVisibility(View.INVISIBLE);
        } else {
            for (int n = 0; n < 4; n++){
                view_artene[n].setVisibility(View.VISIBLE);
                view_sporene[n].setVisibility(View.VISIBLE);
            }
            view_internett.setVisibility(View.GONE);
            view_svar.setVisibility(View.VISIBLE);
        }
        return ni != null;
    }


    private void sjekkSvar() {
        if (art_liste != null) {

            int m, n;
            int[] part_pos = new int[2];
            int[] spill_pos = new int[2];
            int[] feil = {1, 1, 1, 1};

            view_svar.setOnClickListener(null);
            view_tekst.setTypeface(null, Typeface.BOLD);
            for (n = 0; n < 4; n++) {
                for (m = 0; m < 4; m++) {
                    if (nr_spor[n] == nr_art[m]) {
                        break;
                    }
                }

                view_sporene[n].getLocationOnScreen(part_pos);
                view_artene[m].getLocationOnScreen(spill_pos);

                if (part_pos[0] + view_sporene[n].getWidth() / 2 > spill_pos[0] &
                        part_pos[0] + view_sporene[n].getWidth() / 2 < spill_pos[0] + view_artene[m].getWidth() &
                        part_pos[1] + view_sporene[n].getHeight() / 2 > spill_pos[1] &
                        part_pos[1] + view_sporene[n].getHeight() / 2 < spill_pos[1] + view_artene[m].getHeight()) {
                    feil[n] = 0;
                }
            }

            // Teller hvor mange som er feil
            int sum = Arrays.stream(feil).sum();
            if (sum == 0) {
                riktig();
            } else {
                feil(sum, feil);
            }
        }
    }

    private void feil(int sum, int[] feil) {
        poengMulig -= 1;
        if (poengMulig < 0) {
            poengMulig = 0;
        }

        if (sum < 4) {
            view_tekst.setText(String.format(getString(R.string.noen_rett_prov_igjen), 4 - sum));
        } else {
            view_tekst.setText(getString(R.string.ingen_rett_prov_igjen));
        }

        for(int n = 0; n < 4; n++){
            TranslateAnimation flyttTilbake = new TranslateAnimation(
                    0, feil[n] * (x0_part[n] - view_sporene[n].getX()),
                    0, feil[n] * (y0_part - view_sporene[n].getY()));
            flyttTilbake.setDuration(300);

            int finalN = n;
            flyttTilbake.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    if(feil[finalN] == 1) {
                        view_sporene[finalN].setX(x0_part[finalN]);
                        view_sporene[finalN].setY(y0_part);
                        view_svar.setOnClickListener(v -> sjekkSvar());
                    }
                }
                @Override
                public void onAnimationRepeat(Animation animation) {}

            });

            view_sporene[n].setAnimation(flyttTilbake);
            view_sporene[n].startAnimation(flyttTilbake);
        }

        RotateAnimation animasjonFeil = new RotateAnimation(
                -5,5,
                Animation.RELATIVE_TO_SELF,(float) 0.5,
                Animation.RELATIVE_TO_SELF,(float) 0.5);
        animasjonFeil.setDuration(100);
        animasjonFeil.setRepeatCount(2);
        animasjonFeil.setRepeatMode(Animation.REVERSE);

        view_tekst.setAnimation(animasjonFeil);
        view_tekst.startAnimation(animasjonFeil);

        animasjonFeil.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view_tekst.setTextColor(ContextCompat.getColor(Spm_fotspor.this, R.color.rod));
            }
            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void riktig(){
        double tid = 0.5*(System.currentTimeMillis() / 1000.0 - tid_start) - 1.0;
        lagret.setNrSpm(lagret.getNrSpm()+1);
        int poeng_runden = poengMulig * poeng.faktor(tid,innstillinger.getAntallSpmTotalt());
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
                view_poeng.setTextColor(getColor(R.color.brun_mork));
                if(lagret.getNrSpm() <= innstillinger.getAntallSpmTotalt()) {
                    oppdaterSpm();
                } else {
                    fotsporFerdig();
                }
                poengMulig = 3;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    private void oppdaterSpm() {
        if (isConnectingToInternet() & art_liste != null) {
            view_tekst.setTextColor(getColor(R.color.brun_mork));
            view_tekst.setText(R.string.dra_fotspor_til_riktig_dyr);
            view_antall.setText(String.format(getString(R.string.spm_av_total), lagret.getNrSpm(), innstillinger.getAntallSpmTotalt()));
            view_tekst.setTypeface(null, Typeface.NORMAL);

            int n, nr;
            // Sprer ting pent utover når layouten er klar
            if (start) {
                if (view_tekst.getHeight() == 0)
                    new Handler().postDelayed(this::startPosisjoner, 100);
                else startPosisjoner();
            } else {
                for (n = 0; n < 4; n++) {
                    view_artene[n].setMaxHeight(dy_art);
                    view_sporene[n].setX(x0_part[n]);
                    view_sporene[n].setY(y0_part);
                    view_artene[n].setMaxHeight(50);
                }
            }

            // Visker ut forrige kombinasjon
            for (n = 0; n < 4; n++) {
                nr_art[n] = -1;
                nr_spor[n] = -1;
            }

            // Plukkere ut fire spor og blander artene
            n = 0;
            while (n < 4) {
                nr = (int) (Math.random() * art_liste.size());
                int ok = 1;
                for (int m = 0; m < 4; m++) {
                    if (nr_art[m] == nr) { // Arten er valgt tidligere
                        ok = 0;
                        break;
                    }
                }
                if (ok == 1) {
                    nr_art[n] = nr;
                    nr_spor[n] = nr;
                    n += 1;
                }
            }
            Randomisere.randomInt(nr_art);

            // Henter fotsporene
            String bildeurl;
            String itemurl;
            for (n = 0; n < 4; n++) {
                itemurl = art_liste.get(nr_spor[n]).getFotspor_url();
                bildeurl = art_liste.get(nr_art[n]).getBilde_url();
                view_sporene[n].setImageUrl(itemurl, AppKontroll.getInstance().getImageLoader());
                view_artene[n].setImageUrl(bildeurl, AppKontroll.getInstance().getImageLoader());
                view_artene[n].setContentDescription(art_liste.get(nr_art[n]).getArt());
                view_navnene[n].setText(art_liste.get(nr_art[n]).getArt());
            }

            // Starter tiden
            tid_start = System.currentTimeMillis() / 1000.0;
            view_svar.setOnClickListener(v -> sjekkSvar());
        }
    }

    // Sprer tingene fint utover
    private void startPosisjoner() {
        y0_part = view_sporene[0].getY();
        x0_part[0] = view_sporene[0].getX();
        x0_part[3] = view_sporene[3].getX();
        x0_part[1] = x0_part[0] + (x0_part[3] - x0_part[0]) / 3;
        x0_part[2] = x0_part[3] - (x0_part[3] - x0_part[0]) / 3;
        z0 = view_sporene[0].getElevation();
        view_tekst.setHeight(view_tekst.getHeight());
        dy_art = (int) (0.5 * (view_tekst.getY() - view_artene[0].getY()));
        start = false;

        for (int n = 0; n < 4; n++) {
            view_artene[n].setMaxHeight(dy_art);
            view_sporene[n].setX(x0_part[n]);
            view_sporene[n].setY(y0_part);
            view_artene[n].setMaxHeight(50);
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x0 = view.getX();
                y0 = view.getY();
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                view.setElevation(z0+1);
                break;

            case MotionEvent.ACTION_MOVE:
                view.setX(event.getRawX() + dX);
                view.setY(event.getRawY() + dY);

                // Flytter inn på skjermen igjen
                if (view.getX() < 0){
                    view.setX(0);
                }
                if (view.getX() + view.getWidth() > view_tittel.getX() + view_tittel.getWidth() + 40){
                    view.setX(view_tittel.getX() + view_tittel.getWidth() + 40 - view.getWidth());
                }
                if (view.getY() < 0){
                    view.setY(0);
                }
                if (view.getY() + view.getHeight() > view_svar.getY()){
                    view.setY(view_svar.getY() - view.getHeight());
                }

                break;

            case MotionEvent.ACTION_UP:
                view.setElevation(z0);
                if((x0-view.getX())*(x0-view.getX()) + (y0-view.getY())*(y0-view.getY()) < 2){
                    int nr_art_touched = 0;
                    for (int n = 0; n < 4; n++){
                        if (view_sporene[n].equals(view)) {
                            nr_art_touched = nr_spor[n];
                            break;
                        }
                    }
                    zoom_inn(nr_art_touched, "spor");
                }
                break;
            default:
                return false;
        }
        return true;
    }

    // Zoom inn
    private void zoom_inn(int nr_spill, String hva) {
        if (art_liste != null) {
            String bildeurl;
            if (hva.equals("dyr")) {
                bildeurl = art_liste.get(nr_spill).getBilde_url();
            } else {
                bildeurl = art_liste.get(nr_spill).getFotsporInfo_url();
            }
            view_stort_bilde.setImageUrl(bildeurl, AppKontroll.getInstance().getImageLoader());
            view_stort_bilde.setVisibility(View.VISIBLE);
            view_svar.setVisibility(View.INVISIBLE);

            vis_meny = false;
            invalidateOptionsMenu();
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(false); // tilbakeknapp fjernes
        }
    }

    // Zoom ut
    private void zoom_ut() {
        view_stort_bilde.setVisibility(View.INVISIBLE);
        view_svar.setVisibility(View.VISIBLE);

        vis_meny = true;
        invalidateOptionsMenu();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbakeknapp legges til igjen
    }

    private void fotsporFerdig() {
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
        int antall = 6;
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
            // Zoom inn på art
            view_hjelp_tekst.setText(R.string.klikk_forstorre);
            view_hjelp_finger.setRotation(30);
            view_hjelp_finger.setX(view_sporene[1].getX() - view_sporene[1].getWidth()*0.2f);
            view_hjelp_finger.setY(view_sporene[1].getY() + view_sporene[1].getHeight()*0.2f);
            view_hjelp_finger.startAnimation(anim);
        } else if (h_nr == 1){
            zoom_inn(nr_spor[1], "spor");
            // Zoom ut
            view_hjelp_tekst.setText(R.string.klikk_forminske);
            view_hjelp_finger.setX(view_stort_bilde.getX() + view_stort_bilde.getWidth()*0.3f);
            view_hjelp_finger.setY(view_stort_bilde.getY() + view_stort_bilde.getHeight()*0.3f);
            view_hjelp_finger.startAnimation(anim);
        } else if (h_nr == 2){
            zoom_ut();
            // Zoom inn på art
            view_hjelp_tekst.setText(R.string.klikk_forstorre);
            view_hjelp_finger.setX(view_stort_bilde.getX() + view_stort_bilde.getWidth()*0.2f);
            view_hjelp_finger.setY(view_svar.getY() - view_stort_bilde.getHeight()*0.2f);
            view_hjelp_finger.startAnimation(anim);
        } else if (h_nr == 3){
            zoom_inn(nr_art[2],"dyr");
            // Zoom ut
            view_hjelp_tekst.setText(R.string.klikk_forminske);
            view_hjelp_finger.setX(view_stort_bilde.getX() + view_stort_bilde.getWidth()*0.3f);
            view_hjelp_finger.setY(view_stort_bilde.getY() + view_stort_bilde.getHeight()*0.3f);
            view_hjelp_finger.startAnimation(anim);
        } else if (h_nr == 4){
            zoom_ut();
            // Drag
            view_hjelp_tekst.setText(R.string.dra_fotspor_til_riktig_dyr);
            view_hjelp_finger.setX(view_sporene[1].getX() - view_sporene[1].getWidth()*0.2f);
            view_hjelp_finger.setY(view_sporene[1].getY() + view_sporene[1].getHeight()*0.2f);
            view_hjelp_finger.startAnimation(animDrag);
            view_sporene[1].startAnimation(animDrag);
            view_hjelp_tekst.startAnimation(animDrag);
        } else if (h_nr == 5){
            // Submit
            view_hjelp_tekst.setVisibility(View.GONE);
            view_hjelp_finger.setRotation(30);
            view_hjelp_finger.setX(view_svar.getX());
            view_hjelp_finger.setY(view_svar.getY());
            view_hjelp_finger.startAnimation(anim);
        }
        view_hjelp_tekst.setX(view_hjelp_finger.getX() - view_hjelp_finger.getWidth()*0.2f);
        view_hjelp_tekst.setY(view_hjelp_finger.getY() + dh);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (brukerInfo.getBrukerID().equals(""))
            if (!(innstillinger.getLevel().equals("Demo") & (innstillinger.getAntallSpmTotalt() == 3)))
                finish();
    }

}