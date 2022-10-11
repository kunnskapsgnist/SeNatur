package com.kunnskapsgnist.naturquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kunnskapsgnist.naturquiz.databinding.MenyLoggInnBinding;
import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Lagret;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;
import com.kunnskapsgnist.naturquiz.modell.Spiller;

import java.util.List;
import java.util.Objects;

public class Meny_logg_inn extends AppCompatActivity {
    private static final String TAG = "Naturquiz";
    MenyLoggInnBinding binding;
    Menybar menybar;

    // Firebase Auth Object.
    private FirebaseAuth firebaseAuth;

    // TextView to Show Login User Email and Name.
    EditText view_epost, view_passord;
    ConstraintLayout view_vent;

    Spiller spiller;
    BillingClient billingClient;
    BrukerInfo brukerInfo;
    Lagret lagret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.meny_logg_inn);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        menybar = new Menybar("Menu_profil");

        brukerInfo = new BrukerInfo(Meny_logg_inn.this);

        // Progress - noe foregår
        view_vent = binding.loginVent;
        view_vent.setVisibility(View.GONE);

        // Epost og passord som spilleren taster inn
        view_epost = binding.loginEmail;
        view_passord = binding.loginPassord;

        // Klasse der all info om spilleren på Firestore mellomlagres
        spiller = new Spiller();

        // Getting Firebase Auth Instance into firebaseAuth object.
        firebaseAuth = FirebaseAuth.getInstance();

        // Login
        binding.loginKnapp.setOnClickListener(this::prepareLogin);

        // Glemt passord
        binding.loginGlemtKnapp.setOnClickListener(this::sendNyttPassord);

        // Gå til lag konto
        binding.loginLagBruker.setOnClickListener(v -> {
            startActivity(new Intent(Meny_logg_inn.this,Meny_opprett_bruker.class));
            finish();
        });

        // Actionbar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbake

    }

    // Gjør klar til å logge inn bruker og kaller loginEpostPassord hvis alt er klart
    public void prepareLogin(View view){

        // Skjuler tastatur
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        if (!TextUtils.isEmpty(view_epost.getText().toString())
                && !TextUtils.isEmpty(view_passord.getText().toString())){

            String email = view_epost.getText().toString().trim();
            String password = view_passord.getText().toString().trim();

            loginEpostPassord(email, password);
        } else {
            // Markerer manglende felt
            Toast.makeText(Meny_logg_inn.this, "Tomme felt har ingen nytte", Toast.LENGTH_LONG).show();
            if (TextUtils.isEmpty(view_epost.getText().toString().trim()))
                view_epost.setHintTextColor(ContextCompat.getColor(Meny_logg_inn.this, R.color.rod_klar));
            if (TextUtils.isEmpty(view_passord.getText().toString().trim()))
                view_passord.setHintTextColor(ContextCompat.getColor(Meny_logg_inn.this, R.color.rod_klar));
        }
    }

    // Prøver å logge inn brukeren
    private void loginEpostPassord(String epost, String passord) {
        view_vent.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(epost) && !TextUtils.isEmpty(passord)) {
            firebaseAuth.signInWithEmailAndPassword(epost, passord)
                    .addOnCompleteListener(task -> {

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            FirebaseFirestore.getInstance().
                                    collection("Spillere")
                                    .document(user.getUid()).get()
                                    .addOnSuccessListener(documentSnapshot -> {

                                        spiller = documentSnapshot.toObject(Spiller.class);
                                        if (spiller != null){
                                            oppdaterLagret();
                                        }

                                        oppdaterBrukerInfo();
                                        bekreftelse();

                                    })
                                    .addOnFailureListener(e -> Toast.makeText(
                                            Meny_logg_inn.this, "Noe gikk galt " + e, Toast.LENGTH_LONG).show());
                        } else {
                            // Login feilet
                            view_vent.setVisibility(View.GONE);
                            Toast.makeText(Meny_logg_inn.this,"Galt passord eller brukeren eksisterer ikke",Toast.LENGTH_LONG).show();
                        }

                    })
                    .addOnFailureListener(e -> Toast.makeText(
                            Meny_logg_inn.this, "Noe gikk galt " + e, Toast.LENGTH_LONG).show());
        }
    }

    // Viser at brukeren er logget inn
    private void bekreftelse() {
        view_vent.setVisibility(View.GONE);
        binding.loginTitle.setVisibility(View.GONE);
        binding.loginSuksess.setVisibility(View.VISIBLE);
        binding.loginSuksessFortsett.setOnClickListener(v -> {
            startActivity(new Intent(Meny_logg_inn.this,Meny_profil.class));
            finish();
        });
    }

    // Oppdaterer settingene i "SpillerInfo" fra Firestore
    private void oppdaterBrukerInfo() {
        brukerInfo.setNavn(spiller.getBrukerNavn());
        brukerInfo.setEpost(spiller.getEpost());
        brukerInfo.setID(spiller.getBrukerId());
        brukerInfo.setOppgradering(spiller.getOppgrader());
        sjekkLevelPoeng("Fugler","bilder");
        sjekkLevelPoeng("Fugler","lyd");
        sjekkLevelPoeng("Fugler","vingespenn");
        sjekkLevelPoeng("Fugler","vekt");
        sjekkLevelPoeng("Blomster","bilder");
        sjekkLevelPoeng("Trær","bilder");
        sjekkLevelPoeng("Bregner","bilder");
        sjekkLevelPoeng("Sopp","bilder");
        sjekkLevelPoeng("Sommerfugler","bilder");
        sjekkLevelPoeng("Sommerfugler","vingespenn");
        sjekkLevelPoeng("Edderkopper","bilder");
        sjekkLevelPoeng("Insekter","bilder");
        sjekkLevelPoeng("Dyr","bilder");
        sjekkLevelPoeng("Dyr","lengde");
        sjekkLevelPoeng("Dyr","vekt");
        sjekkLevelPoeng("Fotspor","fotspor");

        // Sjekk kjøp dersom spilleren ikke har gratisvariant
        if (!spiller.getOppgrader()) sjekkKjop();
    }

    private void sjekkLevelPoeng(String gruppe, String kategori) {
        brukerInfo.setLevelPoengDirekte(gruppe, kategori,spiller.getLevelPoeng(gruppe,kategori),true);
    }

    private void sjekkKjop() {
        PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchaseList) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchaseList != null) {
                verifyPayment(purchaseList);
            }
        };

        billingClient = BillingClient
                .newBuilder(this)
                .enablePendingPurchases()
                .setListener(purchasesUpdatedListener)
                .build();

        connectGooglePlayBilling(billingClient);
    }

    private void connectGooglePlayBilling(BillingClient billingClient) {
        PurchasesResponseListener purchaseResponseListener = (billingResult, purchaseList) -> {
            if (billingResult.getResponseCode()==0) {
                girTilgang(purchaseList);
            }
        };

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                connectGooglePlayBilling(billingClient);
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK){
                    billingClient.queryPurchasesAsync(
                            QueryPurchasesParams
                                    .newBuilder()
                                    .setProductType(BillingClient.ProductType.INAPP)
                                    .build(),
                            purchaseResponseListener);
                }
            }
        });
    }


    private void verifyPayment(List<Purchase> purchaseList) {
        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = billingResult -> {
            if (billingResult.getResponseCode()==0) {
                girTilgang(purchaseList);
            }
        };

        for (Purchase purchase: purchaseList) {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                }
            }
        }
    }

    // Gir tilgang til det som er kjøpt
    private void girTilgang(List<Purchase> purchaseList) {
        for (Purchase purchase: purchaseList){
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                brukerInfo.setOppgradering(true);
            } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                Toast.makeText(this,"You have upgraded, but the payment is pending",Toast.LENGTH_LONG).show();
            }
        }
    }

    // Oppdaterer poengene i "Lagret" fra Firestore
    private void oppdaterLagret() {
        lagret = new Lagret(Meny_logg_inn.this);
        String[] levelListe = getResources().getStringArray(R.array.levelListe);
        for (String level : levelListe) {
            settRekorder("Fugl","Fugler","bilder",level);
            settRekorder("Fugl","Fugler","lyd",level);
            settRekorder("Fugl","Fugler","vingespenn",level);
            settRekorder("Fugl","Fugler","vekt",level);
            settRekorder("Plante","Blomster","bilder",level);
            settRekorder("Plante","Trær","bilder",level);
            settRekorder("Plante","Bregner","bilder",level);
            settRekorder("Sopp","Sopp","bilder",level);
            settRekorder("Insekt","Sommerfugler","bilder",level);
            settRekorder("Insekt","Sommerfugler","vingespenn",level);
            settRekorder("Insekt","Edderkopper","bilder",level);
            settRekorder("Insekt","Insekter","bilder",level);
            settRekorder("Dyr","Dyr","bilder",level);
            settRekorder("Dyr","Dyr","lengde",level);
            settRekorder("Dyr","Dyr","vekt",level);
            settRekorder("Dyr","Fotspor","fotspor",level);
        }
    }

    private void settRekorder(String type, String gruppe, String kategori, String level) {
        lagret.setHighscore(spiller.getRekorder(gruppe,kategori,level),type,gruppe,kategori,level);
    }

    // Glemt passord
    private void sendNyttPassord(View view) {

        // Skjuler tastatur
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

        // Sender nytt passord dersom eposten eksisterer
        String epost = view_epost.getText().toString().trim();
        if (epost.isEmpty()){
            Toast.makeText(Meny_logg_inn.this,"Epost er obligatorisk for å endre passord",Toast.LENGTH_LONG).show();
            view_epost.setHintTextColor(Color.RED);
        } else {
            firebaseAuth.sendPasswordResetEmail(epost)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Meny_logg_inn.this, "Vennligst sjekk eposten din", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Meny_logg_inn.this, "Det oppstod en feil under sending av epost", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(e -> Toast.makeText(Meny_logg_inn.this, "Feil oppstod under sending av epost" + e, Toast.LENGTH_LONG).show());
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