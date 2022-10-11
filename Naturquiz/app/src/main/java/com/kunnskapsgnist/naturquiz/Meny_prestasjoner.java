package com.kunnskapsgnist.naturquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerView;
import com.kunnskapsgnist.naturquiz.databinding.MenyPrestasjonerBinding;
import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Innstillinger;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;
import com.kunnskapsgnist.naturquiz.modell.Prestasjon;
import com.kunnskapsgnist.naturquiz.modell.PrestasjonAdapter;

import java.util.ArrayList;
import java.util.List;

public class Meny_prestasjoner extends AppCompatActivity {
    private static final String TAG = "Naturquiz";
    MenyPrestasjonerBinding binding;

    RecyclerView view_prestasjoner;
    BannerView view_reklame;

    Menybar menybar;
    Innstillinger innstillinger;
    BrukerInfo brukerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.meny_prestasjoner);

        view_prestasjoner = binding.prestasjonPrestasjoner;
        view_reklame = binding.prestasjonReklame;

        innstillinger = new Innstillinger(this);
        menybar = new Menybar("Prestasjoner");
        brukerInfo = new BrukerInfo(this);

        visPrestasjoner();

        // Reklame
        if (brukerInfo.getOppgradering() & !brukerInfo.getBrukerID().equals("")) view_reklame.setVisibility(View.GONE);
        else reklame();

        // Actionbar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbake
    }

    private void visPrestasjoner() {
        view_prestasjoner.setHasFixedSize(true);
        view_prestasjoner.setLayoutManager(new LinearLayoutManager(this));

        List<Prestasjon> prestasjonListe = new ArrayList<>();
        prestasjonListe.add(new Prestasjon(this,"Fugl","Fugler","bilder"));
        prestasjonListe.add(new Prestasjon(this,"Fugl","Fugler","vingespenn"));
        prestasjonListe.add(new Prestasjon(this,"Fugl","Fugler","vekt"));
        prestasjonListe.add(new Prestasjon(this,"Plante","Blomster","bilder"));
        prestasjonListe.add(new Prestasjon(this,"Plante","Trær","bilder"));
        prestasjonListe.add(new Prestasjon(this,"Plante","Bregner","bilder"));
        prestasjonListe.add(new Prestasjon(this,"Sopp","Sopp","bilder"));
        prestasjonListe.add(new Prestasjon(this,"Insekt","Sommerfugler","bilder"));
        prestasjonListe.add(new Prestasjon(this,"Insekt","Sommerfugler","vingespenn"));
        prestasjonListe.add(new Prestasjon(this,"Insekt","Edderkopper","bilder"));
        prestasjonListe.add(new Prestasjon(this,"Insekt","Insekter","bilder"));
        prestasjonListe.add(new Prestasjon(this,"Dyr","Dyr","bilder"));
        prestasjonListe.add(new Prestasjon(this,"Dyr","Fotspor","fotspor"));
        prestasjonListe.add(new Prestasjon(this,"Dyr","Dyr","lengde"));
        prestasjonListe.add(new Prestasjon(this,"Dyr","Dyr","vekt"));

        PrestasjonAdapter prestasjonAdapter = new PrestasjonAdapter(this, prestasjonListe);
        view_prestasjoner.setAdapter(prestasjonAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.meny, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return menybar.sjekkMenybar(this, item.getItemId());
    }

    // Reklame
    private void reklame() {
        Appodeal.setBannerViewId(R.id.prestasjon_reklame);
        if (Appodeal.isInitialized(Appodeal.BANNER))
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

}