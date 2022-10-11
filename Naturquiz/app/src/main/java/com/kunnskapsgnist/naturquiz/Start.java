package com.kunnskapsgnist.naturquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerView;
import com.kunnskapsgnist.naturquiz.databinding.StartBinding;
import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Farge;
import com.kunnskapsgnist.naturquiz.informasjon.Innstillinger;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;

import java.util.Objects;

public class Start extends AppCompatActivity {
    private static final String TAG = "Naturquiz";

    private StartBinding binding;
    private Button view_logg_inn, view_oppgrader;
    private ImageView view_spm1, view_spm2, view_spm3, view_spm4,
            view_fugl, view_plante, view_sopp, view_insekt, view_dyr;
    private TextView view_tittel, view_hjelp_tekst;
    private ImageView view_hjelp_finger;
    private BannerView view_reklame;
    private ConstraintLayout view_bakgrunn;

    float dh;
    boolean oppgradering_mulig;
    String[] gruppe = new String[4];
    String[] kategori = new String[4];

    Menybar menybar;
    Innstillinger innstillinger;
    BrukerInfo brukerInfo;
    Farge farge;
    Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start);

        binding = DataBindingUtil.setContentView(this, R.layout.start);

        view_tittel = binding.startTittel;
        view_spm1 = binding.startSpm1;
        view_spm2 = binding.startSpm2;
        view_spm3 = binding.startSpm3;
        view_spm4 = binding.startSpm4;
        view_bakgrunn = binding.startBakgrunn;
        view_fugl = binding.startFugl;
        view_plante = binding.startPlante;
        view_sopp = binding.startSopp;
        view_insekt = binding.startInsekt;
        view_dyr = binding.startDyr;
        view_logg_inn = binding.startLoggInn;
        view_oppgrader = binding.startOppgrader;
        view_hjelp_tekst = binding.startHjelpTekst;
        view_hjelp_finger = binding.startHjelpFinger;
        view_reklame = binding.startReklame;

        innstillinger = new Innstillinger(this);
        brukerInfo = new BrukerInfo(this);

        if (innstillinger.getType() == null) innstillinger.setType("Fugl");
        if (brukerInfo.getOppgradering() & !brukerInfo.getBrukerID().equals("")) view_reklame.setVisibility(View.GONE);
        else reklame();

        dh = 0;
        startOppsett();
        visKnappene();
        aktiverKnappene();
        fargelegg();

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setElevation(0);
        menybar = new Menybar("Start");
    }

    private void aktiverKnappene() {
        view_spm1.setOnClickListener(v -> startAktivitet(0));
        view_spm2.setOnClickListener(v -> startAktivitet(1));
        view_spm3.setOnClickListener(v -> startAktivitet(2));
        view_spm4.setOnClickListener(v -> startAktivitet(3));

        view_fugl.setOnClickListener(view -> {
            innstillinger.setType("Fugl");
            fargelegg();
            visKnappene();
        });

        view_plante.setOnClickListener(view -> {
            innstillinger.setType("Plante");
            fargelegg();
            visKnappene();
        });

        view_sopp.setOnClickListener(view -> {
            innstillinger.setType("Sopp");
            fargelegg();
            visKnappene();
        });

        view_insekt.setOnClickListener(view -> {
            innstillinger.setType("Insekt");
            fargelegg();
            visKnappene();
        });

        view_dyr.setOnClickListener(view -> {
            innstillinger.setType("Dyr");
            fargelegg();
            visKnappene();
        });

    }

    private void startAktivitet(int n) {
        Intent intent;
        innstillinger.setKategori(kategori[n]);
        innstillinger.setGruppe(gruppe[n]);
        if (!brukerInfo.getBrukerID().equals("")) {
            intent = new Intent(this, Valg.class);
            innstillinger.setLevel("Ekspert");
            innstillinger.setAntallSpmTotalt(6);
        } else {
            if (kategori[n].equals("bilder"))
                intent = new Intent(this, Spm_bilder.class);
            else if (kategori[n].equals("fotspor"))
                intent = new Intent(this, Spm_fotspor.class);
            else
                intent = new Intent(this, Spm_str.class);
            innstillinger.setLevel("Demo");
            innstillinger.setAntallSpmTotalt(3);
        }
        startActivity(intent);
    }

    // Setter startOppsett basert på hvilke rettigheter brukeren har
    private void startOppsett() {

        // Ikke logget inn
        if (brukerInfo.getBrukerID().equals("")) {
            view_logg_inn.setVisibility(View.VISIBLE);
            view_logg_inn.setOnClickListener(v -> startActivity(
                    new Intent(Start.this, Meny_logg_inn.class)));
        } else {
            view_logg_inn.setVisibility(View.GONE);
        }

        oppgradering_mulig = false;
        view_oppgrader.setVisibility(View.GONE);


        // Oppgraderinger mulig
//        view_oppgrader.setOnClickListener(v -> startActivity(
//                new Intent(Start.this, Menu_oppgrader.class)));


    }

    private void fargelegg() {
        farge = new Farge(this,innstillinger.getType());

        // Farge på menylinjen
        Objects.requireNonNull(getSupportActionBar())
                .setBackgroundDrawable(new ColorDrawable(farge.getFarge()));

        // Fikser fargene på symbolene nederst i tilelle de er rotet til
        Farge bla = new Farge(this,"Fugl");
        view_fugl.setImageDrawable(bla.symbolFarge(R.drawable.fugl));
        Farge gronn = new Farge(this,"Plante");
        view_plante.setImageDrawable(gronn.symbolFarge(R.drawable.blomst));
        Farge rod = new Farge(this,"Insekt");
        view_insekt.setImageDrawable(rod.symbolFarge(R.drawable.insekt_sommerfugl));
        Farge brun = new Farge(this,"Dyr");
        view_dyr.setImageDrawable(brun.symbolFarge(R.drawable.dyr_ekorn));
        Farge lilla = new Farge(this,"Sopp");
        view_sopp.setImageDrawable(lilla.symbolFarge(R.drawable.sopp));

        // Bakgrunn og spørsmålsbokser
        view_tittel.setTextColor(farge.getFarge());
        view_bakgrunn.setBackground(farge.getGradient());
        view_logg_inn.setTextColor(farge.getFarge());
        view_oppgrader.setTextColor(farge.getFarge());
        view_hjelp_tekst.setTextColor(farge.getFarge());

        view_spm3.setRotation(0);
        switch (innstillinger.getType()){
            case "Fugl":
                view_spm1.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_fugl));
                view_spm2.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.lyd));
                view_spm3.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.fugl_vingespenn));
                view_spm4.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.vekt_bla));
                view_spm3.setRotation(90);
                break;
            case "Plante":
                view_spm1.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_blomst));
                view_spm2.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_tre));
                view_spm3.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_bregne));
                break;
            case "Sopp":
                view_spm1.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_sopp));
                break;
            case "Insekt":
                view_spm1.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_sommerfugl));
                view_spm2.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.sommerfugl_vingespenn));
                view_spm3.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_edderkopp));
                view_spm4.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_insekt));
                break;
            case "Dyr":
                view_spm1.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.bilde_dyr));
                view_spm2.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.dyr_fotspor));
                view_spm3.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.dyr_lengde));
                view_spm4.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.vekt_brun));
                break;
        }
    }

    private void visKnappene() {
        // De nederste type-knappene
        view_fugl.setVisibility(View.VISIBLE);
        view_plante.setVisibility(View.VISIBLE);
        view_sopp.setVisibility(View.VISIBLE);
        view_insekt.setVisibility(View.VISIBLE);
        view_dyr.setVisibility(View.VISIBLE);

        // Tekst på spill-knappene
        view_spm1.setVisibility(View.VISIBLE);
        view_spm2.setVisibility(View.VISIBLE);
        view_spm3.setVisibility(View.VISIBLE);
        view_spm4.setVisibility(View.VISIBLE);
        if (innstillinger.getType().equals("Fugl")) {
            gruppe = new String[]{"Fugler", "Fugler", "Fugler", "Fugler"};
            kategori = new String[]{"bilder","lyd","vingespenn","vekt"};
            view_spm1.setContentDescription("Bilder");
            view_spm2.setContentDescription("Lyd");
            view_spm3.setContentDescription("Vingspenn");
            view_spm4.setContentDescription("Vekt");
            view_spm2.setVisibility(View.GONE);
        } else if (innstillinger.getType().equals("Plante")) {
            gruppe = new String[]{"Blomster", "Trær", "Bregner", ""};
            kategori = new String[]{"bilder","bilder","bilder",""};
            view_spm1.setContentDescription("Bilder av blomster");
            view_spm2.setContentDescription("Bilder av trær");
            view_spm3.setContentDescription("Bilder av bregner");
            view_spm4.setVisibility(View.GONE);
        } else if (innstillinger.getType().equals("Sopp")) {
            gruppe = new String[]{"Sopp", "", "", ""};
            kategori = new String[]{"bilder","","",""};
            view_spm1.setContentDescription("Bilder av sopp");
            view_spm2.setVisibility(View.GONE);
            view_spm3.setVisibility(View.GONE);
            view_spm4.setVisibility(View.GONE);
        } else if (innstillinger.getType().equals("Insekt")) {
            gruppe = new String[]{"Sommerfugler", "Sommerfugler","Edderkopper", "Insekter"};
            kategori = new String[]{"bilder","vingespenn","bilder","bilder"};
            view_spm1.setContentDescription("Bilder av sommerfugler");
            view_spm2.setContentDescription("Vingespenn");
            view_spm3.setContentDescription("Bilder av edderkopper");
            view_spm4.setContentDescription("Bilder av insekt");
        } else if (innstillinger.getType().equals("Dyr")) {
            gruppe = new String[]{"Dyr", "Fotspor", "Dyr", "Dyr"};
            kategori = new String[]{"bilder","fotspor","lengde","vekt"};
            view_spm1.setContentDescription("Bilder av dyr");
            view_spm2.setContentDescription("Fotspor");
            view_spm3.setContentDescription("Lengde");
            view_spm4.setContentDescription("Vekt");
        }
    }

    // Reklame
    private void reklame() {
        Appodeal.setBannerViewId(R.id.start_reklame);
        if (Appodeal.isInitialized(Appodeal.BANNER))
            Appodeal.show(this, Appodeal.BANNER_VIEW);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        startOppsett();
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

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.hjelp, menu);
        getMenuInflater().inflate(R.menu.meny, menu);
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

    // --------------- Hjelpende hånd ------------------------
    private void hjelp() {
        int antall = 2;
        final int[] nr = {0};
        view_hjelp_finger.setElevation(view_spm1.getElevation() + 10);
        if (dh == 0) {
            dh = view_hjelp_tekst.getY() - view_hjelp_finger.getY();
        }

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
                    if (nr[0] == 2 & !brukerInfo.getBrukerID().equals("")) nr[0] += 1;
                    if (nr[0] == 3 & !oppgradering_mulig) nr[0] += 1;
                    startHjelp(nr[0]);
                } else {
                    view_hjelp_tekst.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startHjelp(nr[0]);
    }

    private void startHjelp(int h_nr) {
        if (h_nr == 0) {
            // Velg en artsgruppe
            view_hjelp_finger.setRotation(-150);
            view_hjelp_finger.setX(view_plante.getX());
            view_hjelp_finger.setY(binding.startTyper.getY() - view_hjelp_finger.getHeight());
            view_hjelp_tekst.setText(R.string.velg_en_artsgruppe);
            view_hjelp_tekst.setY(view_hjelp_finger.getY() + 0.5f*view_hjelp_finger.getHeight() - dh);
        } else if (h_nr == 1) {
            // Velg en kategori
            view_hjelp_finger.setRotation(30);
            view_hjelp_finger.setX(view_spm1.getX());
            view_hjelp_finger.setY(view_spm1.getY());
            view_hjelp_tekst.setText(R.string.velg_en_kategori);
            view_hjelp_tekst.setY(view_hjelp_finger.getY() + dh);
        } else if (h_nr == 2) {
            // Logg in
            view_hjelp_finger.setRotation(30);
            view_hjelp_finger.setX(view_logg_inn.getX() + view_logg_inn.getWidth() * 0.3f);
            view_hjelp_finger.setY(view_logg_inn.getY());
            view_hjelp_tekst.setText(R.string.logg_inn);
            view_hjelp_tekst.setY(view_hjelp_finger.getY() + dh);
        } else if (h_nr == 3) {
            // Oppgrader
            view_hjelp_finger.setRotation(30);
            view_hjelp_finger.setX(view_oppgrader.getX() + view_oppgrader.getWidth() * 0.3f);
            view_hjelp_finger.setY(view_oppgrader.getY());
            view_hjelp_tekst.setText(R.string.oppgrader);
            view_hjelp_tekst.setY(view_hjelp_finger.getY() + dh);
        }
        view_hjelp_tekst.setX(view_hjelp_finger.getX() - view_hjelp_tekst.getWidth() * 0.2f);
        view_hjelp_finger.startAnimation(anim);
    }
}