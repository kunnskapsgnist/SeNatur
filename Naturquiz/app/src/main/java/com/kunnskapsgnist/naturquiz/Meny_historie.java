package com.kunnskapsgnist.naturquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

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

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerView;
import com.kunnskapsgnist.naturquiz.databinding.MenyHistorieBinding;
import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Farge;
import com.kunnskapsgnist.naturquiz.informasjon.Innstillinger;
import com.kunnskapsgnist.naturquiz.informasjon.Menybar;
import com.kunnskapsgnist.naturquiz.modell.PoengAdapter;
import com.kunnskapsgnist.naturquiz.modell.PoengHistorie;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// https://stackoverflow.com/questions/34518421/adding-a-scoreboard-to-an-android-studio-application
public class Meny_historie extends AppCompatActivity {
    private static final String TAG = "BoardgameBattle";
    MenyHistorieBinding binding;

    TextView view_tittel, view_level;
    ListView view_liste;
    Button view_sorter_dato, view_sorter_poeng;
    ImageView view_level_forrige, view_level_neste, view_fugl, view_plante, view_sopp, view_insekt, view_dyr;
    BannerView view_reklame;
    ConstraintLayout view_bakgrunn;

    private List<PoengHistorie> poengHistorieListe = new ArrayList<>();
    String sorter = "dato";
    int nl;
    String[] levelListe;
    boolean reverser = false;

    Menybar menybar;
    BrukerInfo brukerInfo;
    Innstillinger innstillinger;
    Farge farge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.meny_historie);

        view_tittel = binding.historieTittel;
        view_level = binding.historieLevel;
        view_level_forrige = binding.historieLevelForrige;
        view_level_neste = binding.historieLevelNeste;
        view_sorter_dato = binding.historieSorterDato;
        view_sorter_poeng = binding.historieSorterPoeng;
        view_liste = binding.historieListe;
        view_fugl = binding.historieFugl;
        view_plante = binding.historiePlante;
        view_sopp = binding.historieSopp;
        view_insekt = binding.historieInsekt;
        view_dyr = binding.historieDyr;
        view_reklame = binding.historieReklame;
        view_bakgrunn = binding.historieBakgrunn;

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        menybar = new Menybar("Historie");
        brukerInfo = new BrukerInfo(this);
        innstillinger = new Innstillinger(this);

        fargelegg();
        if (brukerInfo.getOppgradering() & !brukerInfo.getBrukerID().equals("")) view_reklame.setVisibility(View.GONE);
        else reklame();

        // Level
        nl = 0;
        levelListe = getResources().getStringArray(R.array.levelListeAlle);
        view_level.setText(String.format("%s %s", getString(R.string.level), levelListe[0]));
        visHistorie(levelListe[nl]);
        view_level_neste.setOnClickListener(v -> {
            nl = (nl + 1) % levelListe.length;
            visHistorie(levelListe[nl]);
            view_level.setText(String.format("%s %s", getString(R.string.level), levelListe[nl]));
        });
        view_level.setOnClickListener(v -> {
            nl = (nl + 1) % levelListe.length;
            visHistorie(levelListe[nl]);
            view_level.setText(String.format("%s %s", getString(R.string.level), levelListe[nl]));
        });
        view_level_forrige.setOnClickListener(v -> {
            nl = (levelListe.length + nl - 1) % levelListe.length;
            visHistorie(levelListe[nl]);
            view_level.setText(String.format("%s %s", getString(R.string.level), levelListe[nl]));
        });

        // Sorter
        view_sorter_poeng.setOnClickListener(v -> {
            if (sorter.equals("poeng")) reverser = !reverser;
            else reverser = false;
            sorter = "poeng";
            fargeleggSorter(view_sorter_poeng);
            visHistorie(levelListe[nl]);
        });

        view_sorter_dato.setOnClickListener(v -> {
            if (sorter.equals("dato")) reverser = !reverser;
            else reverser = false;
            sorter = "dato";
            fargeleggSorter(view_sorter_dato);
            visHistorie(levelListe[nl]);
        });

        // Typer
        view_fugl.setOnClickListener(view -> {
            innstillinger.setType("Fugl");
            fargelegg();
            visHistorie(levelListe[nl]);
        });

        view_plante.setOnClickListener(view -> {
            innstillinger.setType("Plante");
            fargelegg();
            visHistorie(levelListe[nl]);
        });

        view_sopp.setOnClickListener(view -> {
            innstillinger.setType("Sopp");
            fargelegg();
            visHistorie(levelListe[nl]);
        });

        view_insekt.setOnClickListener(view -> {
            innstillinger.setType("Insekt");
            fargelegg();
            visHistorie(levelListe[nl]);
        });

        view_dyr.setOnClickListener(view -> {
            innstillinger.setType("Dyr");
            fargelegg();
            visHistorie(levelListe[nl]);
        });

        // Actionbar
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true); // tilbake
    }

    private void fargeleggSorter(Button view_valgt) {
        view_sorter_poeng.setBackgroundColor(farge.getFargeblass());
        view_sorter_dato.setBackgroundColor(farge.getFargeblass());

        view_sorter_poeng.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        view_sorter_dato.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        view_valgt.setBackgroundColor(farge.getFarge());
        if (reverser)
            view_valgt.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(this, R.drawable.pil_opp), null);
        else
            view_valgt.setCompoundDrawablesWithIntrinsicBounds(null, null, AppCompatResources.getDrawable(this, R.drawable.pil_ned), null);
    }

    private void visHistorie(String level) {
        poengHistorieListe = new ArrayList<>();
        SQLiteDatabase myDB = null;

        try {
            //Create a Database if doesnt exist otherwise Open It
            myDB = this.openOrCreateDatabase("poenghistorie", MODE_PRIVATE, null);

            //Create table in database if it doesnt exist already
            myDB.execSQL("CREATE TABLE IF NOT EXISTS historie (poeng INT, type TEXT, gruppe TEXT, kategori TEXT, level TEXT, dato TEXT);");

        } finally {

            //Initialize and create a new adapter with layout named list found in activity_main layout
            PoengAdapter poengAdapter = new PoengAdapter(this, poengHistorieListe, innstillinger.getType(), 0);
            view_liste.setAdapter(poengAdapter);
            view_liste.setOnItemClickListener(null);

            StringBuilder sql_kall = new StringBuilder();
            sql_kall.append("SELECT * FROM historie ");
            sql_kall.append("WHERE type = \"").append(innstillinger.getType()).append("\" ");

            // Level
            if (!level.equals("Alle"))
                sql_kall.append(" AND level = \"").append(level).append("\" ");

            // Sortering
            sql_kall.append(" ORDER BY \"").append(sorter).append("\"");
            if (reverser) sql_kall.append(" DESC");
            else sql_kall.append(" ASC");

            Cursor cursor = Objects.requireNonNull(myDB).rawQuery(sql_kall.toString(),null);
            if (cursor.moveToLast()) {

                //read all rows from the database and add to the Items array
                while (!cursor.isBeforeFirst()) {

                    PoengHistorie poengHistorie = new PoengHistorie();

                    poengHistorie.setPoeng(cursor.getString(0));
                    poengHistorie.setType(cursor.getString(1));
                    poengHistorie.setGruppe(cursor.getString(2));
                    poengHistorie.setKategori(cursor.getString(3));
                    poengHistorie.setLevel(cursor.getString(4));
                    poengHistorie.setDato(cursor.getString(5));

                    poengHistorieListe.add(poengHistorie);
                    cursor.moveToPrevious();
                }
            }

            //All done, so notify the adapter to populate the list using the Items Array
            poengAdapter.notifyDataSetChanged();

            cursor.close();
            myDB.close();
        }
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
        Farge lilla = new Farge(this,"Sopp");
        view_sopp.setImageDrawable(lilla.symbolFarge(R.drawable.sopp));
        Farge rod = new Farge(this,"Insekt");
        view_insekt.setImageDrawable(rod.symbolFarge(R.drawable.insekt_sommerfugl));
        Farge brun = new Farge(this,"Dyr");
        view_dyr.setImageDrawable(brun.symbolFarge(R.drawable.dyr_ekorn));

        // Bakgrunn og spørsmålsbokser
        view_tittel.setTextColor(farge.getFarge());
        view_level.setBackground(farge.symbolFarge(R.drawable.bakgrunn_midt));
        view_level_forrige.setBackground(farge.symbolFarge(R.drawable.bakgrunn_venstre));
        view_level_neste.setBackground(farge.symbolFarge(R.drawable.bakgrunn_hoyre));
        view_sorter_poeng.setBackgroundColor(farge.getFarge());
        view_sorter_dato.setBackgroundColor(farge.getFarge());
        view_bakgrunn.setBackground(farge.getGradient());
        view_reklame.setBackground(farge.getGradientBlank());

        // Sorteringsbokser
        if (sorter.equals("poeng")) fargeleggSorter(view_sorter_poeng);
        else fargeleggSorter(view_sorter_dato);
    }

    // Reklame
    private void reklame() {
        Appodeal.setBannerViewId(R.id.historie_reklame);
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