package com.kunnskapsgnist.naturquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kunnskapsgnist.naturquiz.databinding.MenyProfilBinding;
import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Innstillinger;
import com.kunnskapsgnist.naturquiz.informasjon.Lagret;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;

import java.util.Objects;

public class Meny_profil extends AppCompatActivity {
    private MenyProfilBinding binding;
    private static final String TAG = "Naturquiz";

    Innstillinger innstillinger;

    Button knapp_glemt_passord, knapp_endre_brukernavn, knapp_loggut,
            knapp_logginn, knapp_opprett_bruker, knapp_nytt_navn;
    TextView view_tittel, view_brukernavn, view_epost, view_nytt_navn;
    ConstraintLayout view_bytt_brukernavn;
    LinearLayout view_ikke_logget_inn;
    ImageView view_bytt_brukernavn_avbryt;
    BannerView view_reklame;

    Lagret lagret;
    Menybar menybar;
    BrukerInfo brukerInfo;

    // Firebase Auth Object.
    private FirebaseAuth firebaseAuth;
    private FirebaseUser aktivSpiller;
    private FirebaseAuth.AuthStateListener authStateListener;

    // Firestore forbindelse
    private final FirebaseFirestore fireDb = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.meny_profil);
        innstillinger = new Innstillinger(this);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        lagret = new Lagret(this);
        menybar = new Menybar("Menu_profil");
        brukerInfo = new BrukerInfo(this);

        view_tittel = binding.profilTittel;
        view_brukernavn = binding.profilBrukernavn;
        view_bytt_brukernavn = binding.profilByttBrukernavn;
        view_epost = binding.profilEpost;
        knapp_logginn = binding.profilLoginButton;
        knapp_loggut = binding.profilLoggUt;
        knapp_opprett_bruker = binding.profilOpprettBruker;
        knapp_glemt_passord = binding.profilGlemtPassord;
        knapp_endre_brukernavn = binding.profilEndreBrukernavn;
        view_ikke_logget_inn = binding.profilIkkeLoggetInn;
        view_nytt_navn = binding.profilNyttBrukernavn;
        knapp_nytt_navn = binding.profilByttBrukernavnKnapp;
        view_bytt_brukernavn_avbryt = binding.profilByttBrukernavnAvbryt;
        view_reklame = binding.profilReklame;

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> {
            aktivSpiller = firebaseAuth.getCurrentUser();
            if (aktivSpiller == null) {
                view_ikke_logget_inn.setVisibility(View.VISIBLE);

                knapp_logginn.setOnClickListener(v -> {
                    startActivity(new Intent(Meny_profil.this, Meny_logg_inn.class));
                    finish();
                });
                knapp_opprett_bruker.setOnClickListener(v -> {
                    startActivity(new Intent(Meny_profil.this, Meny_opprett_bruker.class));
                    finish();
                });
            }
        };

        view_brukernavn.setText(brukerInfo.getBrukernavn());

        view_epost.setText(brukerInfo.getBrukerEpost());

        knapp_glemt_passord.setOnClickListener(v -> sendNyttPassord());
        knapp_endre_brukernavn.setOnClickListener(v -> {
            view_bytt_brukernavn.setVisibility(View.VISIBLE);
            byttBrukernavn();
        });

        knapp_loggut.setOnClickListener(v -> {
            if (aktivSpiller!= null && firebaseAuth != null){
                firebaseAuth.signOut();
                fjernBrukerinfo();
            }
            startActivity(new Intent(Meny_profil.this,Meny_logg_inn.class));
            finish();
        });

        if (brukerInfo.getOppgradering() & !brukerInfo.getBrukerID().equals("")) view_reklame.setVisibility(View.GONE);
        else reklame();

        // Actionbar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbake
    }

    // Fjern brukerInfo
    private void fjernBrukerinfo() {
        brukerInfo.setNavn("");
        brukerInfo.setID("");
        brukerInfo.setEpost("");
        brukerInfo.setOppgradering(false);
        slettLevelPoeng("Fugler","bilder");
        slettLevelPoeng("Fugler","lyd");
        slettLevelPoeng("Fugler","vingespenn");
        slettLevelPoeng("Fugler","vekt");
        slettLevelPoeng("Blomster","bilder");
        slettLevelPoeng("Trær","bilder");
        slettLevelPoeng("Bregner","bilder");
        slettLevelPoeng("Sopp","bilder");
        slettLevelPoeng("Sommerfugler","bilder");
        slettLevelPoeng("Sommerfugler","vingespenn");
        slettLevelPoeng("Edderkopper","bilder");
        slettLevelPoeng("Insekter","bilder");
        slettLevelPoeng("Dyr","bilder");
        slettLevelPoeng("Dyr","lengde");
        slettLevelPoeng("Dyr","vekt");
        slettLevelPoeng("Fotspor","fotspor");

        // Fjerner poeng fra SharedPreferences
        String[] levelListe = getResources().getStringArray(R.array.levelListe);
        for (String level : levelListe) {
            slettRekorder("Fugl", "Fugler", "bilder", level);
            slettRekorder("Fugl", "Fugler", "lyd", level);
            slettRekorder("Fugl", "Fugler", "vingespenn", level);
            slettRekorder("Fugl", "Fugler", "vekt", level);
            slettRekorder("Plante", "Blomster", "bilder", level);
            slettRekorder("Plante", "Trær", "bilder", level);
            slettRekorder("Plante", "Bregner", "bilder", level);
            slettRekorder("Sopp", "Sopp", "bilder", level);
            slettRekorder("Insekt", "Sommerfugler", "bilder", level);
            slettRekorder("Insekt", "Sommerfugler", "vingespenn", level);
            slettRekorder("Insekt", "Edderkopper", "bilder", level);
            slettRekorder("Insekt", "Insekter", "bilder", level);
            slettRekorder("Dyr", "Dyr", "bilder", level);
            slettRekorder("Dyr", "Dyr", "lengde", level);
            slettRekorder("Dyr", "Dyr", "vekt", level);
            slettRekorder("Dyr", "Fotspor", "fotspor", level);
        }
    }

    private void slettLevelPoeng(String gruppe, String kategori) {
        brukerInfo.setLevelPoengDirekte(gruppe, kategori,0,true);
    }

    private void slettRekorder(String type, String gruppe, String kategori, String level) {
        lagret.setHighscore(0,type,gruppe,kategori,level);
    }

    // Skjuler tastatur
    private void skjulTastatur(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // Reklame
    private void reklame() {
        Appodeal.setBannerViewId(R.id.profil_reklame);
        if (!brukerInfo.getOppgradering() | brukerInfo.getBrukerID().equals(""))
            Appodeal.show(this, Appodeal.BANNER_VIEW);
    }

    private void byttBrukernavn() {
        view_bytt_brukernavn_avbryt.setOnClickListener(v -> view_bytt_brukernavn.setVisibility(View.GONE));

        knapp_nytt_navn.setOnClickListener(v -> {
            String nytt_navn = view_nytt_navn.getText().toString().trim();

            // Sjekker først om noen andre har det brukernavnet
            fireDb
                    .collection("Spillere")
                    .whereEqualTo("brukerNavn",nytt_navn)
                    .get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()){

                            for (DocumentSnapshot documentSnapshot : task.getResult()){
                                String brukernavn = documentSnapshot.getString("brukerNavn");

                                // brukernavnet er brukt fra før
                                if (brukernavn != null) {
                                    if (brukernavn.equals(nytt_navn)) {
                                        Toast.makeText(Meny_profil.this, "Brukernavnet er allerede i bruk", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            // Alt ok: Gi nytt navn
                            if(task.getResult().size() == 0 ){
                                FirebaseUser aktivBruker;
                                aktivBruker = FirebaseAuth.getInstance().getCurrentUser();
                                if (aktivBruker != null) {
                                    brukerInfo.setNavn(nytt_navn);
                                    view_brukernavn.setText(brukerInfo.getBrukernavn());
                                    fireDb
                                            .collection("Spillere")
                                            .document(aktivBruker.getUid())
                                            .update("brukerNavn", nytt_navn);
                                } else Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();

                                skjulTastatur(knapp_nytt_navn);
                                view_bytt_brukernavn.setVisibility(View.GONE);
                            }
                        }
                    }).addOnFailureListener(e -> Toast.makeText(Meny_profil.this,"Noe gikk galt" + e,Toast.LENGTH_LONG).show());
        });
    }

    private void sendNyttPassord() {
        String epost = aktivSpiller.getEmail();
        assert epost != null;
        firebaseAuth.sendPasswordResetEmail(epost)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(Meny_profil.this,"Sjekk eposten din", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(Meny_profil.this,"Det oppstod en feil under sending av epost",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(Meny_profil.this,"Det oppstod en feil under sending av epost",Toast.LENGTH_LONG).show());
    }

    @Override
    protected void onStart() {
        super.onStart();
        aktivSpiller = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
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
        getMenuInflater().inflate(R.menu.meny, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return menybar.sjekkMenybar(this,item.getItemId());
    }

}