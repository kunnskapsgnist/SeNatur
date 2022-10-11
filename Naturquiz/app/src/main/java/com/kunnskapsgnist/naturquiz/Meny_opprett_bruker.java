package com.kunnskapsgnist.naturquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kunnskapsgnist.naturquiz.databinding.MenyOpprettBrukerBinding;
import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Lagret;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Meny_opprett_bruker extends AppCompatActivity {

    private static final String TAG = "Naturquiz";
    MenyOpprettBrukerBinding binding;

    BrukerInfo brukerInfo;
    Lagret lagret;
    Menybar menybar;

    // Firestore connection
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReference = db.collection("Spillere");

    // Firebase Auth Object.
    private FirebaseAuth firebaseAuth;
    private FirebaseUser aktivBruker;

    // TextView to Show Login User Email and Name.
    TextView view_navn, view_epost, view_passord, view_passord2;
    ConstraintLayout view_vent;
    Button view_loginknapp, view_opprettknapp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.meny_opprett_bruker);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        menybar = new Menybar("Menu_profil");
        brukerInfo = new BrukerInfo(this);
        lagret = new Lagret(this);

        view_vent = binding.opprettVent;
        view_vent.setVisibility(View.GONE);

        // Getting Firebase Auth Instance into firebaseAuth object.
        firebaseAuth = FirebaseAuth.getInstance();

        view_navn = binding.opprettBrukernavn;
        view_epost = binding.opprettEpost;
        view_passord = binding.opprettPassord;
        view_passord2 = binding.opprettPassord2;
        view_loginknapp = binding.opprettLoginKnapp;
        view_opprettknapp = binding.opprettKnapp;

        view_loginknapp.setOnClickListener(v -> {
            startActivity(new Intent(Meny_opprett_bruker.this,Meny_logg_inn.class));
            finish();
        });

        // Create account med e-post og passord
        view_opprettknapp.setOnClickListener(this::forberedOpprett);

        // Actionbar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbake

    }

    // Skjuler tastatur
    private void skjulTastatur(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // Gjør klar til å opprette bruker
    public void forberedOpprett(View view){
        skjulTastatur(view);
        view_vent.setVisibility(View.VISIBLE);
        view_navn.setTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.bla_mork));
        view_epost.setTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.bla_mork));
        view_passord.setTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.bla_mork));
        view_passord2.setTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.bla_mork));

        // Skjuler tastatur
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        if (!TextUtils.isEmpty(view_navn.getText().toString())
                && !TextUtils.isEmpty(view_epost.getText().toString())
                && !TextUtils.isEmpty(view_passord.getText().toString())
                && !TextUtils.isEmpty(view_passord2.getText().toString())
        ){
            String username = view_navn.getText().toString().trim();
            String email = view_epost.getText().toString().trim();
            String password = view_passord.getText().toString().trim();
            String password2 = view_passord2.getText().toString().trim();

            // Sjekker passord
            if (password.length() > 5){
                if (password.equals(password2)) {

                    // Sjekker om noen har brukt den epostadressen tidligere
                    collectionReference.whereEqualTo("epost",email)
                            .get().addOnCompleteListener(task -> {
                                if (task.isSuccessful()){
                                    for (DocumentSnapshot documentSnapshot : task.getResult()){
                                        String epost = documentSnapshot.getString("epost");

                                        // Eposten er brukt fra før
                                        if (epost != null) {
                                            if (epost.equals(email)) {
                                                Toast.makeText(Meny_opprett_bruker.this, "Eposten er allerede i bruk", Toast.LENGTH_LONG).show();
                                                view_epost.setTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.rod_klar));
                                                view_vent.setVisibility(View.GONE);
                                            }
                                        }
                                    }

                                    // Ingen med den eposten fra før: Opprett ny spiller
                                    if(task.getResult().size() == 0 ){
                                        sjekkBrukerNavn(email, password, username);
                                    }
                                }
                            }).addOnFailureListener(e -> Toast.makeText(Meny_opprett_bruker.this,"Noe gikk galt " + e,Toast.LENGTH_LONG).show());
                } else {
                    Toast.makeText(Meny_opprett_bruker.this,
                            "Passordene er ulike",Toast.LENGTH_LONG).show();
                    view_passord.setHintTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.rod_klar));
                    view_passord2.setHintTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.rod_klar));
                    view_vent.setVisibility(View.GONE);
                }
            } else {
                Toast.makeText(Meny_opprett_bruker.this,
                        "Passordet er ugyldig. Passord må ha minst 6 tegn",Toast.LENGTH_LONG).show();
                view_passord.setHintTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.rod_klar));
                view_passord2.setHintTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.rod_klar));
                view_vent.setVisibility(View.GONE);
            }
        } else {
            // Markerer manglende felt
            Toast.makeText(Meny_opprett_bruker.this, "Tomme felt har ingen nytte", Toast.LENGTH_LONG).show();
            if (TextUtils.isEmpty(view_navn.getText().toString().trim()))
                view_navn.setHintTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.rod_klar));
            if (TextUtils.isEmpty(view_epost.getText().toString().trim()))
                view_epost.setHintTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.rod_klar));
            if (TextUtils.isEmpty(view_passord.getText().toString().trim()))
                view_passord.setHintTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.rod_klar));
            if (TextUtils.isEmpty(view_passord2.getText().toString().trim()))
                view_passord2.setHintTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.rod_klar));
            view_vent.setVisibility(View.GONE);
        }
    }

    private void sjekkBrukerNavn(String email, String password, String username) {
        // Sjekker om noen har brukt det brukernavnet tidligere
        collectionReference.whereEqualTo("brukerNavn",username)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()){

                        for (DocumentSnapshot documentSnapshot : task.getResult()){
                            String brukernavn = documentSnapshot.getString("brukerNavn");

                            // brukernavnet er brukt fra før
                            if (brukernavn != null) {
                                if (brukernavn.equals(username)) {
                                    Toast.makeText(Meny_opprett_bruker.this, "Brukernavnet er allerede i bruk", Toast.LENGTH_LONG).show();
                                    view_navn.setTextColor(ContextCompat.getColor(Meny_opprett_bruker.this, R.color.rod_klar));
                                    view_vent.setVisibility(View.GONE);
                                }
                            }
                        }

                        // Alt ok: Opprett ny spiller
                        if(task.getResult().size() == 0 ){
                            createUserEmailAccount(email, password, username);
                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(Meny_opprett_bruker.this,"Noe gikk galt " + e,Toast.LENGTH_LONG).show());
    }

    // Oppretter bruker med epost og passord
    private void createUserEmailAccount(String email, String password, String username) {

        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(username)){

            firebaseAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            aktivBruker = firebaseAuth.getCurrentUser();
                            assert aktivBruker != null;
                            String aktivBrukerId = aktivBruker.getUid();

                            leggTilFirebase(aktivBrukerId,username,email);

                        } else {
                            Toast.makeText(Meny_opprett_bruker.this, "Noe gikk galt", Toast.LENGTH_LONG).show();
                            view_vent.setVisibility(View.GONE);
                        }

                    })
                    .addOnFailureListener(error -> {
                        Toast.makeText(Meny_opprett_bruker.this, "Din konto ble ikke opprettet: "+error, Toast.LENGTH_LONG).show();
                        view_vent.setVisibility(View.GONE);
                    });
        } else {
            Toast.makeText(Meny_opprett_bruker.this, "Brukernavn, epost og/eller passord mangler", Toast.LENGTH_LONG).show();
        }
    }

    // Legger brukeren til i Firebase der også poengene samles
    private void leggTilFirebase(String aktivBrukerId, String username, String email) {

        DocumentReference brukerFire = collectionReference.document(aktivBrukerId);
        Map<String,String> brukerObj = new HashMap<>();
        brukerObj.put("brukerId",aktivBrukerId);
        brukerObj.put("brukerNavn", username);
        brukerObj.put("epost",email);

        brukerFire.set(brukerObj)
                .addOnSuccessListener(aVoid -> {
                    // Legger brukeren inn i Sharedpreferences
                    brukerInfo.setNavn(username);
                    brukerInfo.setID(aktivBrukerId);
                    brukerInfo.setEpost(email);

                    view_vent.setVisibility(View.INVISIBLE);

                    // Viser at brukeren er logget inn
                    binding.opprettTittel.setVisibility(View.GONE);
                    binding.opprettSuksess.setVisibility(View.VISIBLE);
                    binding.opprettSuksessFortsett.setOnClickListener(v -> {
                        startActivity(new Intent(Meny_opprett_bruker.this,Start.class));
                        finish();
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(Meny_opprett_bruker.this, "" + e, Toast.LENGTH_LONG).show());
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