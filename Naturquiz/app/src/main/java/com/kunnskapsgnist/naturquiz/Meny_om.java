package com.kunnskapsgnist.naturquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.appodeal.ads.Appodeal;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;

public class Meny_om extends AppCompatActivity {
    private static final String TAG = "Naturquiz";
    Menybar menybar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meny_om);

        menybar = new Menybar("Menu_about");

        Log.d(TAG, "onCreate: om");
        if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            boolean viser = Appodeal.show(this, Appodeal.INTERSTITIAL);
            Log.d(TAG, "reklame: viser reklamen... " + viser);
        }

        // Actionbar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbake
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