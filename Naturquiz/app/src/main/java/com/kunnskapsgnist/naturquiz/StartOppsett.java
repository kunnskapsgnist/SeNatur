package com.kunnskapsgnist.naturquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryPurchasesParams;
import com.appodeal.ads.Appodeal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kunnskapsgnist.naturquiz.databinding.StartOppsettBinding;
import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Lagret;

import java.util.List;
import java.util.Objects;


// Sjekker tilganger og starter spillet
public class StartOppsett extends AppCompatActivity {
    private static final String TAG = "Naturquiz";
    StartOppsettBinding binding;
    BrukerInfo brukerInfo;
    Lagret lagret;
    BillingClient billingClient;
    FirebaseUser aktivSpiller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.start_oppsett);

        brukerInfo = new BrukerInfo(this);
        lagret = new Lagret(this);

        brukerInfo.setOppgradering(false);

        // Initialiserer reklamen i tilfelle brukeren ikke er logget inn eller mangler oppdateringen
        Appodeal.setTesting(true);
        Appodeal.initialize(this, "a60ec03954a3e3d0d89e79816022c4fe14491dd7359e0bf1",
                Appodeal.BANNER | Appodeal.INTERSTITIAL, list -> {});
    }

    @Override
    protected void onResume() {
        super.onResume();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        aktivSpiller = firebaseAuth.getCurrentUser();
        if (aktivSpiller != null){
            sjekkSpesialtilgang();
        } else {
            brukerInfo.setID("");
            brukerInfo.setNavn("");
            brukerInfo.setID("");
            brukerInfo.setEpost("");
            sjekkKjop();
        }
    }

    // Sjekker om brukeren har fått spesialtilgang til artene
    private void sjekkSpesialtilgang() {
        brukerInfo.setID(aktivSpiller.getUid());
        FirebaseFirestore.getInstance()
                .collection("Spillere").document(aktivSpiller.getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.get("brukerNavn") != null) brukerInfo.setNavn((String) document.get("brukerNavn"));
                        if (document.get("brukerId") != null) brukerInfo.setID((String) document.get("brukerId"));
                        if (document.get("epost") != null) brukerInfo.setEpost((String) document.get("epost"));
                        if (document.get("upgrade") != null) brukerInfo.setOppgradering((Boolean) Objects.requireNonNull(document.get("upgrade")));
                        sjekkLevel(document,"Fugler","bilder");
                        sjekkLevel(document,"Fugler","vingespenn");
                        sjekkLevel(document,"Blomster","bilder");
                        sjekkLevel(document,"Trar","bilder");
                        sjekkLevel(document,"Sommerfugler","bilder");
                        sjekkLevel(document,"Dyr","bilder");
                        sjekkKjop();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(StartOppsett.this, "Noe gikk galt " + e, Toast.LENGTH_SHORT).show());

    }

    // Sjekker level for hver kategori og sammenligner med det som ligger lokalt
    private void sjekkLevel(DocumentSnapshot document,String gruppe, String kategori) {
        String leveltekst = "level_" + gruppe + "_" + kategori;
        if (document.get(leveltekst) != null) {
            long server_poeng = (long) Objects.requireNonNull(document.get(leveltekst));
            brukerInfo.setLevelPoengDirekte(gruppe,kategori, (int) server_poeng,false);
        }
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
//                brukerInfo.setPakkeID(purchase.getProducts().get(0),true);
            } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                Toast.makeText(this,"Du har bestilt oppgraderingen",Toast.LENGTH_LONG).show();
            }
        }

        // Starter spillet
        startActivity(new Intent(StartOppsett.this,Start.class));
        billingClient.endConnection();
        finish();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }
}

