package com.kunnskapsgnist.naturquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerView;
import com.kunnskapsgnist.naturquiz.data.Filbank;
import com.kunnskapsgnist.naturquiz.databinding.ValgBinding;
import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Farge;
import com.kunnskapsgnist.naturquiz.informasjon.Innstillinger;
import com.kunnskapsgnist.naturquiz.informasjon.Lagret;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;
import com.kunnskapsgnist.naturquiz.modell.Fil;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

public class Valg extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener  {
    private static final String TAG = "Naturquiz";
    ValgBinding binding;

    //    Button view_start;
    ConstraintLayout view_bakgrunn;
    ImageView view_illustrasjon;
    Spinner view_nedtrekk;
    ImageView view_hjelp_finger;
    TextView view_velg_level, view_tekst, view_hjelp_levels, view_hjelp_tekst, view_start;
    ProgressBar view_progress_bar;
    LinearLayout view_ll_level;
    private BannerView view_reklame;

    float dh;

    Farge farge;
    Menybar menybar;
    Innstillinger innstillinger;
    Lagret lagret;
    BrukerInfo brukerInfo;

    Animation anim;
    private Fil filInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.valg);

        view_bakgrunn = binding.valgBakgrunn;
        view_illustrasjon = binding.valgIllustrasjon;
        view_velg_level = binding.valgVelgLevel;
        view_start = binding.valgStart;
        view_nedtrekk = binding.valgLevelNedtrekk;
        view_tekst = binding.valgTekst;
        view_progress_bar = binding.valgProgressBar;
        view_ll_level = binding.valgLlLevel;
        view_hjelp_finger = binding.valgHjelp;
        view_hjelp_levels = binding.valgHjelpLevel;
        view_hjelp_tekst = binding.valgHjelpTekst;
        view_reklame = binding.valgReklame;

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        innstillinger = new Innstillinger(this);
        menybar = new Menybar("Valg");
        brukerInfo = new BrukerInfo(this);

        Log.d(TAG, "onCreate: " + innstillinger.getType() + " " + innstillinger.getKategori() + " " + innstillinger.getGruppe());
        lagret = new Lagret(this);
        dh = 0;

        // Rullemeny for level
        if (isConnectingToInternet()) {
            new Filbank().getFiler(filListe -> {
                for (int n = 0; n < filListe.size(); n++) {
                    if (filListe.get(n).getGruppe().equals(innstillinger.getGruppe()))
                        this.filInfo = filListe.get(n);
                }
                ArrayList<String> listLevel = filInfo.hentLevel();
                ArrayAdapter<String> adapterLevel  = new ArrayAdapter<>(this,R.layout.min_nedtrekksmeny,listLevel);
                adapterLevel.setDropDownViewResource(R.layout.min_nedtrekksfelt);
                view_nedtrekk.setAdapter(adapterLevel);
                view_nedtrekk.setOnItemSelectedListener(this);
                view_nedtrekk.setSelection(adapterLevel.getPosition(
                        brukerInfo.getLevel(innstillinger.getGruppe(), innstillinger.getKategori(), innstillinger.getLevel())));
            });
        }

        fargelegg();

        // Reklame
        if (brukerInfo.getOppgradering() & !brukerInfo.getBrukerID().equals("")) view_reklame.setVisibility(View.GONE);
        else reklame();

        // Actionbar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbake

    }

    // Sjekker om appen er koblet til internett (det kan tenkes den er koblet til et nett som ikke er koblet til internett)
    private boolean isConnectingToInternet(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    private void fargelegg() {
        farge = new Farge(this,innstillinger.getType());

        // Farge på menylinjen
        Objects.requireNonNull(getSupportActionBar())
                .setBackgroundDrawable(new ColorDrawable(farge.getFarge()));

        // Bakgrunn og spørsmålsbokser
        view_velg_level.setTextColor(farge.getFarge());
        view_bakgrunn.setBackground(farge.getGradient());
        view_tekst.setTextColor(farge.getFarge());
        view_progress_bar.setProgressDrawable(farge.getProgress());
        view_nedtrekk.setBackground(farge.getNedtrekk());
        view_hjelp_tekst.setTextColor(farge.getFarge());
        view_hjelp_levels.setBackground(farge.symbolFarge(R.drawable.bakgrunn_midt));

        view_illustrasjon.setRotation(0);
        Log.d(TAG, "fargelegg: " + innstillinger.getGruppe());
        switch (innstillinger.getGruppe()){
            case "Fugler":
                switch (innstillinger.getKategori()){
                    case "bilder":
                        view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_fugl));
                        break;
                    case "lyd":
                        view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.lyd));
                        break;
                    case "vingespenn":
                        view_illustrasjon.setRotation(90);
                        view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.fugl_vingespenn));
                        break;
                    case "vekt":
                        view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.vekt_bla));
                        break;
                }
                break;
            case "Blomster":
                view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_blomst));
                break;
            case "Trær":
                view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_tre));
                break;
            case "Bregner":
                view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_bregne));
                break;
            case "Sommerfugler":
                switch (innstillinger.getKategori()) {
                    case "bilder":
                        view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_sommerfugl));
                        break;
                    case "vingespenn":
                        view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.sommerfugl_vingespenn));
                        break;
                }
                break;
            case "Edderkopper":
                view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_edderkopp));
                break;
            case "Insekter":
                view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_insekt));
                break;
            case "Sopp":
                view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_sopp));
                break;
            case "Dyr":
                switch (innstillinger.getKategori()){
                    case "bilder":
                        view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_dyr));
                        break;
                    case "lengde":
                        view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.dyr_lengde));
                        break;
                    case "vekt":
                        view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.vekt_brun));
                        break;
                }
                break;
            case "Fotspor":
                view_illustrasjon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.dyr_fotspor));
                break;
        }
    }

    private void startNyttSpill() {
        Intent intent;
        String kategori = innstillinger.getKategori();
        switch (kategori) {
            case "bilder":
                innstillinger.setAntallSpmTotalt(10);
                intent = new Intent(Valg.this, Spm_bilder.class);
                break;
            case "vekt":
            case "vingespenn":
            case "lengde":
                innstillinger.setAntallSpmTotalt(6);
                intent = new Intent(Valg.this, Spm_str.class);
                break;
            case "fotspor":
                innstillinger.setAntallSpmTotalt(6);
                intent = new Intent(Valg.this, Spm_fotspor.class);
                break;
            default:
                innstillinger.setAntallSpmTotalt(10);
                intent = new Intent(Valg.this, Spm_bilder.class);
        }
        if (innstillinger.getLevel().equals("Demo")){
            innstillinger.setAntallSpmTotalt(3);
        }
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.meny, menu);
        getMenuInflater().inflate(R.menu.hjelp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_hjelp) {
            hjelp();
            return true;
        } else {
            return menybar.sjekkMenybar(this, item.getItemId());
        }
    }

    // Reklame
    private void reklame() {
        Appodeal.setBannerViewId(R.id.valg_reklame);
        if (!brukerInfo.getOppgradering() | brukerInfo.getBrukerID().equals(""))
            Appodeal.show(this, Appodeal.BANNER_VIEW);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!brukerInfo.getOppgradering() | brukerInfo.getBrukerID().equals(""))
            Appodeal.show(this, Appodeal.BANNER_VIEW);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!brukerInfo.getOppgradering() | brukerInfo.getBrukerID().equals(""))
            Appodeal.show(this, Appodeal.BANNER_VIEW);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!brukerInfo.getOppgradering() | brukerInfo.getBrukerID().equals(""))
            Appodeal.show(this, Appodeal.BANNER_VIEW);
    }

    // Sjekker hva spilleren velger
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String valg = parent.getItemAtPosition(position).toString();
        innstillinger.setLevel(valg);

        // TODO: Må ha oppdaterte verdier i lagret for å kunne vise dem. Må disse flyttes inn i if for å ikke vises fortidlig?
        int poeng = brukerInfo.getNesteLevelPoeng(innstillinger.getGruppe(), innstillinger.getKategori(), innstillinger.getLevel());
        if (poeng == 100) {
            view_tekst.setVisibility(View.VISIBLE);
            view_tekst.setText(String.format(getString(R.string.din_rekord_pa),
                    innstillinger.getLevel().toLowerCase(),
                    lagret.getRekord(innstillinger.getType(), innstillinger.getGruppe(), innstillinger.getKategori(), innstillinger.getLevel())));
            view_progress_bar.setProgress(100);
            view_start.setText(R.string.start);
            view_start.setOnClickListener(v -> startNyttSpill());
            view_illustrasjon.setOnClickListener(v -> startNyttSpill());
            view_start.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else if (poeng < 0) {
            view_tekst.setText(MessageFormat.format("({0} {1})", getString(R.string.unlock), parent.getItemAtPosition(position - 1)));
            view_progress_bar.setProgress(0);
            view_start.setText(R.string.last_level);
            view_start.setOnClickListener(null);
            view_illustrasjon.setOnClickListener(null);
            view_start.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this,R.drawable.symbol_last), null, AppCompatResources.getDrawable(this,R.drawable.symbol_last), null);
        } else {
            view_tekst.setText(MessageFormat.format("({0} {1})", getString(R.string.unlock), parent.getItemAtPosition(position - 1)));
            view_progress_bar.setProgress(brukerInfo.getNesteLevelPoeng(innstillinger.getGruppe(), innstillinger.getKategori(), innstillinger.getLevel()));
            view_start.setText(R.string.last_level);
            view_start.setOnClickListener(null);
            view_illustrasjon.setOnClickListener(null);
            view_start.setCompoundDrawablesWithIntrinsicBounds(AppCompatResources.getDrawable(this,R.drawable.symbol_last), null, AppCompatResources.getDrawable(this,R.drawable.symbol_last), null);
        }
    }

    // Ingenting valgt, ingenting skjer
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    // --------------- Hjelpende hånd ------------------------
    private void hjelp() {
        view_hjelp_levels.setText(filInfo.antallArter());
//        if (innstillinger.getType().equals("Dyr"))
//           view_hjelp_levels.setText(R.string.level_forklart_dyr);
//        else
//            view_hjelp_levels.setText(R.string.level_forklart);

        int antall = 1;
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
                    view_hjelp_levels.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        startHjelp(nr[0]);
    }

    private void startHjelp(int h_nr) {
        if (dh == 0) { dh = view_hjelp_tekst.getY() - view_hjelp_finger.getY(); }
        if (h_nr == 0) {
            // Level
            view_hjelp_finger.setRotation(30);
            view_hjelp_finger.setX(view_ll_level.getX() + view_ll_level.getWidth()*0.6f);
            view_hjelp_finger.setY(view_ll_level.getY());
            view_hjelp_tekst.setText(R.string.velg_niva);
        } else if (h_nr == 1){
            // Start
            view_hjelp_finger.setX(view_start.getX() + view_start.getWidth()*0.1f);
            view_hjelp_finger.setY(view_start.getY() + view_start.getHeight()*0.2f);
            view_hjelp_tekst.setText(R.string.start);
        }
        view_hjelp_tekst.setX(view_hjelp_finger.getX() - view_hjelp_finger.getWidth()*0.2f);
        view_hjelp_tekst.setY(view_hjelp_finger.getY() + dh);
        view_hjelp_finger.startAnimation(anim);
    }
}