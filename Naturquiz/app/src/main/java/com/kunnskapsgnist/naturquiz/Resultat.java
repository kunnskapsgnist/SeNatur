package com.kunnskapsgnist.naturquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.ads.AdError;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.FullScreenContentCallback;
//import com.google.android.gms.ads.LoadAdError;
//import com.google.android.gms.ads.interstitial.InterstitialAd;
//import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.InterstitialCallbacks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kunnskapsgnist.naturquiz.data.Filbank;
import com.kunnskapsgnist.naturquiz.databinding.ResultatBinding;
import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Farge;
import com.kunnskapsgnist.naturquiz.informasjon.Innstillinger;
import com.kunnskapsgnist.naturquiz.informasjon.Lagret;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;
import com.kunnskapsgnist.naturquiz.modell.Fil;
import com.kunnskapsgnist.naturquiz.modell.PoengAdapter;
import com.kunnskapsgnist.naturquiz.modell.PoengHistorie;
import com.kunnskapsgnist.naturquiz.modell.Spiller;

import org.checkerframework.checker.units.qual.A;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Resultat extends AppCompatActivity {
    private static final String TAG = "Naturquiz";
    ResultatBinding binding;

    TextView view_tittel, view_tekst, view_ny_rekord, view_level, view_dine_poeng, view_vent_reklame;
    ImageView view_resultat_venstre, view_resultat_hoyre, view_level_forrige, view_level_neste, view_vent_type;
    ListView view_liste;
    Button view_igjen, view_hjem, view_rekorder;
    ConstraintLayout view_vent, view_bakgrunn;

    Menybar menybar;                // Tilgang til menybar
    Innstillinger innstillinger;    // Tilgang til innstillinger
    BrukerInfo brukerInfo;          // Tilgang til brukerinfo
    Lagret lagret;                  // Tilgang til lagrede parametere
    Farge farge;

//    private InterstitialAd reklame_helside;
    String[] levelNavn;
    int nl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultat);

        binding = DataBindingUtil.setContentView(this, R.layout.resultat);
        menybar = new Menybar("Resultat");

        Log.d(TAG, "onCreate: ");

        view_bakgrunn = binding.resultatBakgrunn;
        view_tittel = binding.resultatTittel;
        view_tekst = binding.resultatTekst;
        view_ny_rekord = binding.resultatNyRekord;
        view_dine_poeng = binding.resultatDinePoeng;
        view_level = binding.resultatLevel;
        view_level_forrige = binding.resultatLevelForrige;
        view_level_neste = binding.resultatLevelNeste;
        view_igjen = binding.resultatIgjen;
        view_hjem = binding.resultatHjem;
        view_rekorder = binding.resultatRekorder;
        view_liste = binding.resultatListe;
        view_vent = binding.resultatVent;
        view_vent_type = binding.resultatVentType;
        view_vent_reklame = binding.resultatVentReklametekst;
        view_resultat_venstre = binding.resultatTypeVenstre;
        view_resultat_hoyre = binding.resultatTypeHoyre;

        innstillinger = new Innstillinger(this);
        brukerInfo = new BrukerInfo(this);
        lagret = new Lagret(this);
        farge = new Farge(this,innstillinger.getType());

        fargelegg();

        // Reklame - foreløpig droppet fordi jeg syntes det ble masete og spillerne kan ikke kjøpe en oppgradering
        if (brukerInfo.getOppgradering() & !brukerInfo.getBrukerID().equals("")) view_vent.setVisibility(View.GONE);
        else reklame();
//        view_vent.setVisibility(View.GONE);

        levelNavn = new String[]{innstillinger.getLevel()};
        new Filbank().getFiler(filListe -> {
            Fil filInfo = null;
            for (int n = 0; n < filListe.size(); n++) {
               if (filListe.get(n).getGruppe().equals(innstillinger.getGruppe()))
                    filInfo = filListe.get(n);
            }
            if (filInfo != null) {
                levelNavn = filInfo.hentLevel().toArray(new String[0]);

                // Samler info til highscorelisten:
                int n;
                for (n = 0; n < levelNavn.length; n++) {
                    if (levelNavn[n].equals(innstillinger.getLevel())) {
                        break;
                    }
                }
                nl = n;
                poengListe(innstillinger.getLevel());
                view_level.setText(String.format("%s %s", getString(R.string.level), innstillinger.getLevel()));
                if (levelNavn.length == 1){
                    view_level.setBackground(farge.symbolFarge(R.drawable.bakgrunn));
                    view_level_forrige.setVisibility(View.GONE);
                    view_level_neste.setVisibility(View.GONE);
                }
            }
        });

        // Presenterer resultatet
        view_tekst.setText(String.format(getString(R.string.du_fikk_poeng),lagret.getPoeng()));

        view_igjen.setOnClickListener(v -> {
            if (!brukerInfo.getBrukerID().equals("")) {
                this.startActivity(new Intent(this, Valg.class));
            } else {
                switch (innstillinger.getKategori()){
                    case "bilder":
                    case "lyd":
                        startActivity(new Intent(this, Spm_bilder.class)); break;
                    case "vekt":
                    case "vingespenn":
                    case "lenge":
                        startActivity(new Intent(this, Spm_str.class)); break;
                    case "fotspor":
                        startActivity(new Intent(this, Spm_fotspor.class)); break;
                }
            }
        });

        view_hjem.setOnClickListener(v -> {
            Intent i=new Intent(this, Start.class);
            finish();
//            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(i);
        });

        view_rekorder.setOnClickListener(v -> startActivity(
                new Intent(Resultat.this, Meny_historie.class)));

        // - Lagrer resultatet i SQLiteDatabasen
        SQLiteDatabase myDB = this.openOrCreateDatabase("poenghistorie", MODE_PRIVATE, null);

        // - Create table in database if it does noe exist already
        myDB.execSQL("CREATE TABLE IF NOT EXISTS historie (poeng INT, type TEXT, gruppe TEXT, kategori TEXT, level TEXT, dato TEXT);");
        myDB.execSQL("INSERT INTO historie (poeng,type,gruppe,kategori,level,dato) VALUES (" +
                lagret.getPoeng() + ", \""
                + innstillinger.getType() + "\",\""
                + innstillinger.getGruppe() + "\",\""
                + innstillinger.getKategori() + "\",\""
                + innstillinger.getLevel() + "\",\""
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")) + "\")");

        // - Åpner nytt level dersom innlogget og nok poeng
        if (!brukerInfo.getBrukerID().equals("")) {
            String nytt_level = brukerInfo.setLevelPoeng(lagret.getPoeng(), innstillinger.getGruppe(), innstillinger.getKategori(), innstillinger.getLevel());
            if (!nytt_level.equals("")) {
                Toast.makeText(this, "Du åpnet " + nytt_level, Toast.LENGTH_LONG).show();
                nyttLevel();
            }
        }

        // Ny rekord?
        boolean ny = lagret.lagreRekord(lagret.getPoeng(), innstillinger.getType(), innstillinger.getGruppe(), innstillinger.getKategori(), innstillinger.getLevel());
        if(ny) nyRekord();
        else view_ny_rekord.setVisibility(View.GONE);

        // Vise poeng for flere level
        if (!brukerInfo.getBrukerID().equals("")) {
            view_level_forrige.setVisibility(View.VISIBLE);
            view_level_neste.setVisibility(View.VISIBLE);

            view_level_neste.setOnClickListener(v -> {
                nl = (nl + 1) % levelNavn.length;
                poengListe(levelNavn[nl]);
                view_level.setText(String.format("%s %s", getString(R.string.level), levelNavn[nl]));
            });
            view_level_forrige.setOnClickListener(v -> {
                nl = (levelNavn.length + nl - 1) % levelNavn.length;
                poengListe(levelNavn[nl]);
                view_level.setText(String.format("%s %s", getString(R.string.level), levelNavn[nl]));
            });

        } else {
            view_level_forrige.setVisibility(View.GONE);
            view_level_neste.setVisibility(View.GONE);
        }

        // Actionbar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setElevation(0);
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbake
    }

    private void nyRekord() {
        view_ny_rekord.setVisibility(View.VISIBLE);

        // - Sjekker om brukeren er logget inn
        if (!brukerInfo.getBrukerID().equals("")) {

            FirebaseUser aktivBruker;
            aktivBruker = FirebaseAuth.getInstance().getCurrentUser();
            if (aktivBruker != null) {

                // Sjekker om highscoren på serveren er lavere
                FirebaseFirestore.getInstance()
                        .collection("Spillere")
                        .document(aktivBruker.getUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Spiller spiller = document.toObject(Spiller.class);
                                if (spiller != null) {
                                    int serverpoeng = spiller.getRekorder(innstillinger.getGruppe(), innstillinger.getKategori(), innstillinger.getLevel());
                                    if (serverpoeng < lagret.getPoeng())
                                        // Skriver til serveren
                                        FirebaseFirestore.getInstance()
                                                .collection("Spillere")
                                                .document(aktivBruker.getUid())
                                                .update(lagret.getFireKombinasjon(innstillinger.getGruppe(), innstillinger.getKategori(), innstillinger.getLevel()),
                                                        lagret.getPoeng());
                                    else
                                        Toast.makeText(this,"Du har flere poeng på en annen enhet", Toast.LENGTH_LONG).show();
                                }

                            } else
                                Toast.makeText(Resultat.this, "Noe gikk galt " + task.getException(), Toast.LENGTH_SHORT).show();
                        });
            } else Toast.makeText(this, getString(R.string.logg_inn_for_a_spille_flere_niva), Toast.LENGTH_LONG).show();
        } else Toast.makeText(this, getString(R.string.logg_inn_for_a_spille_flere_niva), Toast.LENGTH_LONG).show();
    }

    private void nyttLevel() {
        // - Sjekker om brukeren er innlogget
        if (!brukerInfo.getBrukerID().equals("")) {

            FirebaseUser aktivBruker;
            aktivBruker = FirebaseAuth.getInstance().getCurrentUser();
            if (aktivBruker != null) {

                // Sjekker om level på serveren er lavere
                String kombinasjon = "level_" + innstillinger.getGruppe() + "_" + innstillinger.getKategori();
                FirebaseFirestore.getInstance()
                        .collection("Spillere")
                        .document(aktivBruker.getUid())
                        .get()
                        .addOnCompleteListener(task -> {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Spiller spiller = document.toObject(Spiller.class);
                                if (spiller != null) {
                                    int serverlevelpoeng = spiller.getLevelPoeng(innstillinger.getGruppe(), innstillinger.getKategori());
                                    if (serverlevelpoeng < brukerInfo.getLevelPoeng(innstillinger.getGruppe(), innstillinger.getKategori()))
                                        // Skriver til serveren
                                        FirebaseFirestore.getInstance()
                                                .collection("Spillere")
                                                .document(aktivBruker.getUid())
                                                .update(kombinasjon, brukerInfo.getLevelPoeng(innstillinger.getGruppe(), innstillinger.getKategori()));
                                }
                            } else
                                Toast.makeText(Resultat.this, "Noe gikk galt " + task.getException(), Toast.LENGTH_SHORT).show();
                        });
            } else Toast.makeText(this, getString(R.string.logg_inn_for_a_lagre_rekorder), Toast.LENGTH_LONG).show();
        } else Toast.makeText(this, getString(R.string.logg_inn_for_a_lagre_rekorder), Toast.LENGTH_LONG).show();

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    // Tidligere toppscores
    private void poengListe(String level) {

        List<PoengHistorie> poengHistorieList = new ArrayList<>();
        PoengAdapter poengAdapter;

        SQLiteDatabase myDB = this.openOrCreateDatabase("poenghistorie", MODE_PRIVATE, null);

        //Create table in database if it doesnt exist already
        myDB.execSQL("CREATE TABLE IF NOT EXISTS historie (poeng INT, type TEXT, gruppe TEXT, kategori TEXT, level TEXT, dato TEXT);");

        //Initialize and create a new adapter with layout named list found in activity_main layout
        poengAdapter = new PoengAdapter(this, poengHistorieList,innstillinger.getType(), 1);
        view_liste.setAdapter(poengAdapter);
//        view_liste.setEnabled(false);
        view_liste.setOnItemClickListener(null);

        Cursor cursor = Objects.requireNonNull(myDB).rawQuery("SELECT * FROM historie " +
                "WHERE type = \"" + innstillinger.getType() +
                "\" AND gruppe = \"" + innstillinger.getGruppe() +
                "\" AND kategori = \"" + innstillinger.getKategori() +
                "\" AND level = \"" + level +
                "\" ORDER BY poeng DESC", null);
        cursor.moveToFirst();

        // Skjuler highscore-tekst dersom brukeren ikke har noen score i den kategorien og level
        if (cursor.isAfterLast()) view_dine_poeng.setText(R.string.ingen_poeng_enda);
        else view_dine_poeng.setText(R.string.dine_poeng);

        //read all rows from the database and add to the Items array
        while (!cursor.isAfterLast()) {
            PoengHistorie poengHistorie = new PoengHistorie();

            poengHistorie.setPoeng(cursor.getString(0));
            poengHistorie.setType(cursor.getString(1));
            poengHistorie.setGruppe(cursor.getString(2));
            poengHistorie.setKategori(cursor.getString(3));
            poengHistorie.setLevel(cursor.getString(4));
            poengHistorie.setDato(cursor.getString(5));

            poengHistorieList.add(poengHistorie);
            cursor.moveToNext();
        }

        //All done, so notify the adapter to populate the list using the Items Array
        poengAdapter.notifyDataSetChanged();

        cursor.close();
        myDB.close();
    }

    private void fargelegg() {
        // Farge på menylinjen
        Objects.requireNonNull(getSupportActionBar())
                .setBackgroundDrawable(new ColorDrawable(farge.getFarge()));

        ConstraintLayout.MarginLayoutParams marger = (ConstraintLayout.MarginLayoutParams) view_vent_type.getLayoutParams();
        float d = getResources().getDisplayMetrics().density;
        switch (innstillinger.getType()){
            case "Fugl":
                view_vent_type.setImageDrawable(farge.symbolFarge(R.drawable.fugl));
                marger.setMargins(0,0,(int) (30*d),0);
                view_resultat_hoyre.setImageDrawable(farge.symbolFarge(R.drawable.fugl));
                view_resultat_venstre.setImageDrawable(farge.symbolFarge(R.drawable.fugl));
                break;
            case "Plante":
                if (innstillinger.getGruppe().equals("Blomster")){
                    view_vent_type.setImageDrawable(farge.symbolFarge(R.drawable.blomst));
                    marger.setMargins(0,(int) (63*d),0,0);
                    view_resultat_hoyre.setImageDrawable(farge.symbolFarge(R.drawable.blomst));
                    view_resultat_venstre.setImageDrawable(farge.symbolFarge(R.drawable.blomst));
                } else if (innstillinger.getGruppe().equals("Trær")){
                    view_vent_type.setImageDrawable(farge.symbolFarge(R.drawable.tre));
                    marger.setMargins(0,(int) (63*d),0,0);
                    view_resultat_hoyre.setImageDrawable(farge.symbolFarge(R.drawable.tre));
                    view_resultat_venstre.setImageDrawable(farge.symbolFarge(R.drawable.tre));
                } else if (innstillinger.getGruppe().equals("Bregner")){
                    view_vent_type.setImageDrawable(farge.symbolFarge(R.drawable.bregne));
                    marger.setMargins(0,0,0,0);
                    view_resultat_hoyre.setImageDrawable(farge.symbolFarge(R.drawable.bregne));
                    view_resultat_venstre.setImageDrawable(farge.symbolFarge(R.drawable.bregne));
                }
                break;
            case "Insekt":
                if (innstillinger.getGruppe().equals("Sommerfugler")) {
                    view_vent_type.setImageDrawable(farge.symbolFarge(R.drawable.insekt_sommerfugl));
                    marger.setMargins(0, 0, 0, 0);
                    view_resultat_hoyre.setImageDrawable(farge.symbolFarge(R.drawable.insekt_sommerfugl));
                    view_resultat_venstre.setImageDrawable(farge.symbolFarge(R.drawable.insekt_sommerfugl));
                } else if (innstillinger.getGruppe().equals("Edderkopper")) {
                    view_vent_type.setImageDrawable(farge.symbolFarge(R.drawable.edderkopp));
                    marger.setMargins(0, 0, 0, 0);
                    view_resultat_hoyre.setImageDrawable(farge.symbolFarge(R.drawable.edderkopp));
                    view_resultat_venstre.setImageDrawable(farge.symbolFarge(R.drawable.edderkopp));
                } else if (innstillinger.getGruppe().equals("Insekter")) {
                    view_vent_type.setImageDrawable(farge.symbolFarge(R.drawable.insekt));
                    marger.setMargins(0, 0, 0, 0);
                    view_resultat_hoyre.setImageDrawable(farge.symbolFarge(R.drawable.insekt));
                    view_resultat_venstre.setImageDrawable(farge.symbolFarge(R.drawable.insekt));
                }
                break;
            case "Sopp":
                if (innstillinger.getGruppe().equals("Sopp")) {
                    view_vent_type.setImageDrawable(farge.symbolFarge(R.drawable.sopp));
                    marger.setMargins(0, 0, 0, 0);
                    view_resultat_hoyre.setImageDrawable(farge.symbolFarge(R.drawable.sopp));
                    view_resultat_venstre.setImageDrawable(farge.symbolFarge(R.drawable.sopp));
                    break;
                }
            case "Dyr":
                if (innstillinger.getGruppe().equals("Fotspor")) {
                    view_vent_type.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.fotspor));
                    marger.setMargins(0,0,0,0);
                    view_resultat_hoyre.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.fotspor));
                    view_resultat_venstre.setImageDrawable(AppCompatResources.getDrawable(this,R.drawable.fotspor));
                } else {
                    view_vent_type.setImageDrawable(farge.symbolFarge(R.drawable.dyr_ekorn));
                    marger.setMargins(0, 0, 0, (int) (40 * d));
                    view_resultat_hoyre.setImageDrawable(farge.symbolFarge(R.drawable.dyr_ekorn));
                    view_resultat_venstre.setImageDrawable(farge.symbolFarge(R.drawable.dyr_ekorn));
                }
                break;
        }
        view_vent_type.setLayoutParams(marger);
        view_vent.setBackground(farge.getGradient());
        view_vent_reklame.setTextColor(farge.getFarge());

        view_tittel.setTextColor(farge.getFarge());
        view_tekst.setTextColor(farge.getFarge());
        view_dine_poeng.setTextColor(farge.getFarge());
        view_bakgrunn.setBackground(farge.getGradient());

        if (!brukerInfo.getBrukerID().equals("")) {
            view_level.setBackground(farge.symbolFarge(R.drawable.bakgrunn_midt));
            view_level_forrige.setBackground(farge.symbolFarge(R.drawable.bakgrunn_venstre));
            view_level_neste.setBackground(farge.symbolFarge(R.drawable.bakgrunn_hoyre));
        } else
            view_level.setBackground(farge.symbolFarge(R.drawable.bakgrunn));

        view_igjen.setBackgroundColor(farge.getFarge());
        view_hjem.setBackgroundColor(farge.getFarge());
        view_rekorder.setBackgroundColor(farge.getFarge());

    }

    // Reklame
    private void reklame() {
        Log.d(TAG, "reklame: " + Appodeal.isInitialized(Appodeal.INTERSTITIAL) + " " + Appodeal.isLoaded(Appodeal.INTERSTITIAL));
        Appodeal.muteVideosIfCallsMuted(true);
        view_vent_reklame.setVisibility(View.VISIBLE);

        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean isPrecache) {
                // Called when interstitial is loaded
                Log.d(TAG, "onInterstitialLoaded:");
            }

            @Override
            public void onInterstitialFailedToLoad() {
                // Called when interstitial failed to load
                view_vent.setVisibility(View.GONE);
                Log.d(TAG, "onInterstitialFailedToLoad: ");
            }

            @Override
            public void onInterstitialShown() {
                // Called when interstitial is shown
                Log.d(TAG, "onInterstitialShown: ");
                view_vent.setVisibility(View.GONE);
            }

            @Override
            public void onInterstitialShowFailed() {
                // Called when interstitial show failed
                view_vent.setVisibility(View.GONE);
                Log.d(TAG, "onInterstitialShowFailed: ");
            }

            @Override
            public void onInterstitialClicked() {
                // Called when interstitial is clicked
                view_vent.setVisibility(View.GONE);
                Log.d(TAG, "onInterstitialClicked: ");
            }

            @Override
            public void onInterstitialClosed() {
                // Called when interstitial is closed
                view_vent.setVisibility(View.GONE);
                Log.d(TAG, "onInterstitialClosed: ");
            }

            @Override
            public void onInterstitialExpired() {
                // Called when interstitial is expired
                view_vent.setVisibility(View.GONE);
                Log.d(TAG, "onInterstitialExpired: ");
            }
        });

        if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            boolean viser = Appodeal.show(this, Appodeal.INTERSTITIAL);
            view_vent.setVisibility(View.GONE);
            Log.d(TAG, "reklame: viser reklamen... " + viser);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.meny, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return menybar.sjekkMenybar(this,item.getItemId());
    }

}
