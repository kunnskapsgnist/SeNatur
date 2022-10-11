package com.kunnskapsgnist.naturquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.common.collect.ImmutableList;
import com.kunnskapsgnist.naturquiz.databinding.MenyOppgraderingBinding;
import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Innstillinger;
import com.kunnskapsgnist.naturquiz.informasjon.Lagret;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

public class Meny_oppgradering extends AppCompatActivity {
    private static final String TAG = "Naturquiz";
    private MenyOppgraderingBinding binding;

    Button view_knapp;
    TextView view_pris;

    Lagret lagret;
    Menybar menybar;
    BrukerInfo brukerInfo;
    Innstillinger innstillinger;

    private BillingClient billingClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.meny_oppgradering);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        lagret = new Lagret(this);
        menybar = new Menybar("Meny_oppgrader");
        brukerInfo = new BrukerInfo(this);
        innstillinger = new Innstillinger(this);

        view_knapp = binding.oppgraderKnapp;
        view_pris = binding.oppgraderPris;

        // Actionbar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbake
        menybar = new Menybar("Oppgrader");
    }

    // Kontakt med billingclient
    private void sjekkKjop() {
        // Lager en listener som jeg kan bruke i billingclient
        PurchasesUpdatedListener purchasesUpdatedListener = (billingResult, purchaseList) -> {
            // To be implemented in a later section.
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchaseList != null) {
                for (Purchase purchase : purchaseList) {
                    verifyPayment(purchase);
                }
//            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                feilkoder(billingResult.getResponseCode());
            }
        };

        // Initialiserer billingclient
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        // call connectGooglePlayBilling()
        connectGooglePlayBilling();
    }

    private void verifyPayment(Purchase purchase) {
        // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.

        // Verify the purchase.
        // Ensure entitlement was not already granted for this purchaseToken.
        // Grant entitlement to the user.
        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener = billingResult -> {
            if (billingResult.getResponseCode() == 0) {
                brukerInfo.setOppgradering(true);
                startActivity(new Intent(this, Meny_oppgradering.class));
                finish();
            }
        };

        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
            }
        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            Toast.makeText(this, "Du har bestilt oppgraderingen, men betalingen pendler", Toast.LENGTH_LONG).show();
        }

    }

    private void connectGooglePlayBilling() {
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                connectGooglePlayBilling();
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    getProducts();
                }
            }
        });
    }

    // Viser tilgjengelige produkter
    private void getProducts() {
        Log.d(TAG, "getProducts: ");
        ImmutableList<QueryProductDetailsParams.Product> productList
                = ImmutableList.of(
                QueryProductDetailsParams.Product.newBuilder()
                        .setProductId("oppgradering")
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );

        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(productList)
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                (billingResult, productDetailsList) -> {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                            && productDetailsList.size() > 0) {
                        visKjop(productDetailsList);
                    }
                }
        );

    }

    private void visKjop(List<ProductDetails> productDetailsList) {
        if (brukerInfo.getBrukerID().equals("")) {
            view_knapp.setText(R.string.logg_inn);
            view_knapp.setOnClickListener(view -> startActivity(new Intent(this, Meny_logg_inn.class)));
            view_pris.setText(R.string.logg_inn_for_oppgradering);
            view_pris.setVisibility(View.VISIBLE);
        } else if (brukerInfo.getOppgradering()) {
            view_knapp.setText(R.string.du_har_oppgradert);
            view_pris.setVisibility(View.GONE);
        } else {
            ProductDetails.OneTimePurchaseOfferDetails detaljer = productDetailsList.get(0).getOneTimePurchaseOfferDetails();
            if (detaljer != null) {
                String pris = detaljer.getFormattedPrice();
                view_pris.setText(getString(R.string.pris,pris));
                view_knapp.setOnClickListener(v -> onKjopClick(productDetailsList, "oppgradering"));
                view_pris.setVisibility(View.VISIBLE);
            }
        }
    }

    // En knapp er trykket
    public void onKjopClick(List<ProductDetails> productDetailsList, String pakke) {
        ProductDetails productDetails = null;
        for (int n = 0; n < productDetailsList.size(); n++) {
            if (productDetailsList.get(n).getProductId().equals(pakke))
                productDetails = productDetailsList.get(n);
        }

        if (productDetails != null) {
            // Retrieve a value for "productDetails" by calling queryProductDetailsAsync()
            // Get the offerToken of the selected offer
            if (productDetails.getOneTimePurchaseOfferDetails() != null) {

                // Set the parameters for the offer that will be presented
                // in the billing flow creating separate productDetailsParamsList variable
                ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
                        ImmutableList.of(
                                BillingFlowParams.ProductDetailsParams.newBuilder()
                                        .setProductDetails(productDetails)
                                        .build()
                        );


                // Launch billing flow
                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(productDetailsParamsList)
                        .build();

                // Launch the billing flow
                int billingResult = billingClient
                        .launchBillingFlow(this, billingFlowParams)
                        .getResponseCode();

                if (billingResult != 0) {
                    feilkoder(billingResult);
                }
            }

        }
    }

    // Hentes fra onPurchaseListener
    private void feilkoder(int billingResult) {
        switch (billingResult) {
            case BillingClient.BillingResponseCode.USER_CANCELED: // 1
                Toast.makeText(this, "AVBRUTT", Toast.LENGTH_LONG).show();
                break;
            case BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE: // 2
                Toast.makeText(this, "SERVICE UNAVAILABLE", Toast.LENGTH_LONG).show();
                break;
            case BillingClient.BillingResponseCode.BILLING_UNAVAILABLE: // 3
                Toast.makeText(this, "BILLING UNAVAILABLE", Toast.LENGTH_LONG).show();
                break;
            case BillingClient.BillingResponseCode.ITEM_UNAVAILABLE: // 4
                Toast.makeText(this, "ITEM UNAVAILABLE", Toast.LENGTH_LONG).show();
                break;
            case BillingClient.BillingResponseCode.DEVELOPER_ERROR: // 5
                Toast.makeText(this, "DEVELOPER ERROR", Toast.LENGTH_LONG).show();
                break;
            case BillingClient.BillingResponseCode.ERROR: // 6
                Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
                break;
            case BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED: // 7
                Toast.makeText(this, "ITEM ALREADY OWNED", Toast.LENGTH_LONG).show();
                break;
            case BillingClient.BillingResponseCode.ITEM_NOT_OWNED: // 6
                Toast.makeText(this, "ITEM NOT OWNED", Toast.LENGTH_LONG).show();
                break;
            case BillingClient.BillingResponseCode.SERVICE_DISCONNECTED: // -1
                Toast.makeText(this, "SERVICE DISCONNECTED", Toast.LENGTH_LONG).show();
                break;
            case BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED: // -2
                Toast.makeText(this, "FEATURE NOT SUPPORTED", Toast.LENGTH_LONG).show();
                break;
            case BillingClient.BillingResponseCode.SERVICE_TIMEOUT: // -3
                Toast.makeText(this, "SERVICE TIMEOUT", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.meny, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return menybar.sjekkMenybar(this, item.getItemId());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (brukerInfo.getBrukerID().equals("")) {
            view_knapp.setText(R.string.logg_inn);
            view_knapp.setOnClickListener(view -> startActivity(new Intent(this, Meny_logg_inn.class)));
            view_pris.setText(R.string.logg_inn_for_oppgradering);
            view_pris.setVisibility(View.VISIBLE);
        } else {
            sjekkKjop();
        }
    }
}


