package com.kunnskapsgnist.naturquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.appodeal.ads.BannerView;
import com.kunnskapsgnist.naturquiz.data.AppKontroll;
import com.kunnskapsgnist.naturquiz.data.Artsbank;
import com.kunnskapsgnist.naturquiz.databinding.MenyArteneBinding;
import com.kunnskapsgnist.naturquiz.informasjon.Innstillinger;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;
import com.kunnskapsgnist.naturquiz.modell.Art;
import com.kunnskapsgnist.naturquiz.modell.ArtsListeAdapter;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class Meny_artene extends AppCompatActivity
        implements ArtsListeAdapter.OnArtKlikkListener {
    private static final String TAG = "Naturquiz";
    private MenyArteneBinding binding;      // Bånd til layout

    TextView view_tittel, view_level, view_antall, view_hjelp_tekst;
    ImageView view_level_forrige, view_level_neste, view_hjelp_finger, view_internett;
    RecyclerView view_liste;
    NetworkImageView view_stort_bilde;
    BannerView view_reklame;

    String type, gruppe, level;
    List<Art> artsListe;// Hele listen
    int nl;
    String[] levelListe;
    boolean vis_meny, reverser = false;
    private float dh;
    ArtsListeAdapter artsListeAdapter;

    Innstillinger innstillinger;

    Menybar menybar;                // Tilgang til menybar
    ActionBar actionBar;
    Animation anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.meny_artene);

        view_tittel = binding.arteneTittel;
        view_antall = binding.arteneAntall;
        view_level = binding.arteneLevel;
        view_level_forrige = binding.arteneLevelForrige;
        view_level_neste = binding.arteneLevelNeste;
        view_liste = binding.arteneListe;
        view_stort_bilde = binding.arteneStortBilde;
        view_internett = binding.arteneInternett;

        view_hjelp_finger = binding.arteneHjelpFinger;
        view_hjelp_tekst = binding.arteneHjelpTekst;
        view_reklame = binding.arteneReklame;

        innstillinger = new Innstillinger(this);
        level = innstillinger.getLevel();
        type = innstillinger.getType();
        gruppe = innstillinger.getGruppe();
        hentArtene(level,type,gruppe);

        view_level.setText(String.format("%s %s", getString(R.string.level), level));
        view_liste.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        view_liste.setLayoutManager(linearLayoutManager);

        nl = 0;
        levelListe = getResources().getStringArray(R.array.levelListe);
        view_level_neste.setOnClickListener(v -> {
            nl = (nl + 1) % levelListe.length;
            hentArtene(levelListe[nl],type,gruppe);
            view_level.setText(String.format("%s %s", getString(R.string.level), levelListe[nl]));
        });
        view_level.setOnClickListener(v -> {
            nl = (nl + 1) % levelListe.length;
            hentArtene(levelListe[nl],type,gruppe);
            view_level.setText(String.format("%s %s", getString(R.string.level), levelListe[nl]));
        });
        view_level_forrige.setOnClickListener(v -> {
            nl = (levelListe.length + nl - 1) % levelListe.length;
            hentArtene(levelListe[nl],type,gruppe);
            view_level.setText(String.format("%s %s", getString(R.string.level), levelListe[nl]));
        });

        view_stort_bilde.setOnClickListener(v -> zoom_ut());

        // Actionbar
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbake
        menybar = new Menybar("Game list");
        vis_meny = true; // Endres når man zoomer inn
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

    // Zoom inn
    private void zoom_inn(int nr_art) {
        if (artsListe != null) {
            String bildeurl = artsListe.get(nr_art).getBilde_url();
            Log.d(TAG, "zoom_inn: " + bildeurl);
            view_stort_bilde.setImageUrl(bildeurl, AppKontroll.getInstance().getImageLoader());
            view_stort_bilde.setVisibility(View.VISIBLE);
            vis_meny = false;
            invalidateOptionsMenu();
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(false); // tilbakeknapp fjernes
        }
    }

    // Zoom ut
    private void zoom_ut() {
        view_stort_bilde.setVisibility(View.INVISIBLE);
        vis_meny = true;
        invalidateOptionsMenu();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbakeknapp legges til igjen
    }

    private void hentArtene(String level,String type, String gruppe) {
        view_tittel.setText(gruppe);
        if (isConnectingToInternet()) {
            new Artsbank(level,type,gruppe)
                    .getArter(artsListe -> {
                        this.artsListe = artsListe;
                        oppdaterListe();
                    });
        }
    }

    private void oppdaterListe() {
        if (artsListe != null) {

            view_antall.setText(String.format(Locale.getDefault(), "(%d %s)", artsListe.size(), getString(R.string.arter).toLowerCase(Locale.ROOT)));

            artsListe.sort(Comparator.comparing(Art::getArt));

            artsListeAdapter = new ArtsListeAdapter(Meny_artene.this, artsListe, Meny_artene.this);
            view_liste.setAdapter(artsListeAdapter);
        }
    }

    // Sjekker om appen er koblet til internett (det kan tenkes den er koblet til et nett som ikke er koblet til internett)
    private boolean isConnectingToInternet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            view_liste.setVisibility(View.INVISIBLE);
            view_internett.setVisibility(View.VISIBLE);
        } else {
            view_liste.setVisibility(View.VISIBLE);
            view_internett.setVisibility(View.GONE);
        }
        return ni != null;
    }

    private void hjelp() {
        int antall = 8;
        final int[] nr = {0};
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
                    startHjelp(nr[0]);
                    nr[0] += 1;
                } else {
                    zoom_ut();
                    view_hjelp_tekst.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        startHjelp(nr[0]);
        nr[0] += 1;

    }

    private void startHjelp(int h_nr) {
        if (h_nr == 0) {
            // Mer info
            view_hjelp_finger.setRotation(30);
            view_hjelp_finger.setX(view_liste.getX() + view_liste.getWidth() * 0.4f);
            view_hjelp_finger.setY(view_liste.getY());
            view_hjelp_tekst.setText(R.string.klikk);
            view_hjelp_tekst.setX(view_hjelp_finger.getX() - view_hjelp_tekst.getWidth() * 0.2f);
            view_hjelp_tekst.setY(view_hjelp_finger.getY() + dh);
            view_hjelp_finger.startAnimation(anim);
        } else if (h_nr == 6) {
            // Zoom inn
            view_hjelp_finger.setRotation(30);
            view_hjelp_finger.setX(view_liste.getX() + view_liste.getWidth() * 0.05f);
            view_hjelp_finger.setY(view_liste.getY());
            view_hjelp_tekst.setText(R.string.klikk_forstorre);
            view_hjelp_tekst.setX(view_hjelp_finger.getX() - view_hjelp_tekst.getWidth() * 0.2f);
            view_hjelp_tekst.setY(view_hjelp_finger.getY() + dh);
            view_hjelp_finger.startAnimation(anim);
        } else if (h_nr == 7) {
            // Zoom ut
            zoom_inn(0);
            view_hjelp_tekst.setText(R.string.klikk_forminske);
            view_hjelp_finger.startAnimation(anim);
        }
    }

    @Override
    public void onArtKlikk(int position, String klikket) {
        Log.d(TAG, "onArtKlikk: noe er klikket på");
        Art art = artsListe.get(position);
        if (klikket.equals("bilde")) {
            Log.d(TAG, "onArtKlikk: " + position);
            zoom_inn(position);
        } else if (klikket.equals("info")) {
            art.setVisInfo(!art.getVisInfo());
            artsListeAdapter.notifyItemChanged(position);
        }
    }
}