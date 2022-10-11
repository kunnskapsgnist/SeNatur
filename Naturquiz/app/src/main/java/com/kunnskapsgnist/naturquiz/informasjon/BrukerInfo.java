package com.kunnskapsgnist.naturquiz.informasjon;

import static com.google.common.primitives.Ints.min;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class BrukerInfo {
    private static final String TAG = "Naturquiz";
    SharedPreferences brukerinfo;
    private final String NAVN = "brukernavn";                   // Settes ved innlogging
    private final String ID = "brukerid";                       // Hentes fra Firebase Auth ved kjop
    private final String EPOST = "brukerepost";                 // Settes ved innlogging
    private final String OPPGRADERING = "oppgradering";         // false, endres ved kjop

    int poeng_lett = 1000, poeng_medium = 76000, poeng_vanskelig = 226000, poeng_ekspert = 451000, poeng_ekstrem = 751000;

    public BrukerInfo(Activity context) {
        this.brukerinfo = context.getSharedPreferences("BrukerInfo", Context.MODE_PRIVATE);
    }

    public void setNavn(String brukernavn){ brukerinfo.edit().putString(NAVN,brukernavn).apply(); }
    public void setID(String brukerid){ brukerinfo.edit().putString(ID,brukerid).apply(); }
    public void setEpost(String brukerepost){ brukerinfo.edit().putString(EPOST,brukerepost).apply(); }

    public void setOppgradering(boolean oppgradering){
        brukerinfo.edit().putBoolean(OPPGRADERING,oppgradering).apply();
    }

    public String getBrukernavn(){ return brukerinfo.getString(NAVN,"");}
    public String getBrukerID(){ return brukerinfo.getString(ID,"");}
    public String getBrukerEpost(){ return brukerinfo.getString(EPOST,"");}

    public boolean getOppgradering(){
        return brukerinfo.getBoolean(OPPGRADERING,false);
    }

    // Lagrer nye poeng
    // utput = nytt_level dersom poengene er nok til å åpne neste nivå
    public String setLevelPoeng(int poeng, String gruppe, String kategori, String level) {
        String nytt_level = "";
        int gamle_poeng = getLevelPoeng(gruppe,kategori);
        int nye_poeng = gamle_poeng;

        if (gamle_poeng < poeng_lett & level.equals("Demo")) {
            nye_poeng = min(poeng + gamle_poeng, poeng_lett);
            if (nye_poeng == poeng_lett) nytt_level = "Lett";
        } else if (gamle_poeng < poeng_medium & level.equals("Lett")){
            nye_poeng = min(poeng + gamle_poeng, poeng_medium);
            if (nye_poeng == poeng_medium) nytt_level = "Medium";
        } else if (gamle_poeng < poeng_vanskelig & level.equals("Medium")){
            nye_poeng = min(poeng + gamle_poeng, poeng_vanskelig);
            if (nye_poeng == poeng_vanskelig) nytt_level = "Vanskelig";
        } else if (gamle_poeng < poeng_ekspert & level.equals("Vanskelig")){
            nye_poeng = min(poeng + gamle_poeng, poeng_ekspert);
            if (nye_poeng == poeng_ekspert) nytt_level = "Ekspert";
        } else if (gamle_poeng < poeng_ekstrem & level.equals("Ekspert")){
            nye_poeng = min(poeng + gamle_poeng, poeng_ekstrem);
            if (nye_poeng == poeng_ekstrem) nytt_level = "Ekstrem";
        }

        if (nye_poeng != gamle_poeng){
            brukerinfo.edit().putInt("LEVEL_"+gruppe.toUpperCase()+"_"+kategori.toUpperCase(), nye_poeng).apply();
        }
        return nytt_level;
    }

    // Henter poengene i gitt kategori
    public int getLevelPoeng(String gruppe, String kategori){
        return brukerinfo.getInt("LEVEL_"+gruppe.toUpperCase()+"_"+kategori.toUpperCase(),0);
    }

    // Negativt tall -> level er låst
    // Tall mellom 0 og 100 -> progressjon til level
    // Tall over 100 -> level åpent
    public int getNesteLevelPoeng(String gruppe, String kategori, String level){
        int poeng = getLevelPoeng(gruppe,kategori);

        switch (level) {
            case "Demo":
                return 100;
            case "Lett":
                return (100 * min(poeng, poeng_lett)) / poeng_lett;
            case "Medium":
                return (100 * min(poeng - poeng_lett, poeng_medium - poeng_lett)) / (poeng_medium - poeng_lett);
            case "Vanskelig":
                return (100 * min(poeng - poeng_medium, poeng_vanskelig - poeng_medium)) / (poeng_vanskelig - poeng_medium);
            case "Ekspert":
                return (100 * min(poeng - poeng_vanskelig, poeng_ekspert - poeng_vanskelig)) / (poeng_ekspert - poeng_vanskelig);
            case "Ekstrem":
                return (100 * min(poeng - poeng_ekspert, poeng_ekstrem - poeng_ekspert)) / (poeng_ekstrem - poeng_ekspert);
            default:
                return 0;
        }
    }

    public String getLevel(String gruppe, String kategori, String level){
        int poeng = getLevelPoeng(gruppe,kategori);

        if (level.equals("Ekstrem") & poeng < poeng_ekstrem) level = "Ekspert";
        if (level.equals("Ekspert") & poeng < poeng_ekspert) level = "Vanskelig";
        if (level.equals("Vanskelig") & poeng < poeng_vanskelig) level = "Medium";
        if (level.equals("Medium") & poeng < poeng_medium) level = "Lett";
        if (level.equals("Lett") & poeng < poeng_lett) level = "Demo";

        return level;
    }

    public String getNesteLevel(String gruppe, String kategori){
        int poeng = getLevelPoeng(gruppe,kategori);

        String level = "Lett";
        if (poeng >= poeng_lett) level = "Medium";
        if (poeng >= poeng_medium) level = "Vanskelig";
        if (poeng >= poeng_vanskelig) level = "Ekspert";
        if (poeng >= poeng_ekspert) level = "Ekstrem";

        return level;
    }

    public void setLevelPoengDirekte(String gruppe, String kategori, int poeng, boolean ignorer) {
        Log.d(TAG, "setLevelPoengDirekte: " + gruppe + " " + kategori + " " + poeng);
        int gamle_poeng = getLevelPoeng(gruppe, kategori);
        if (ignorer) gamle_poeng = 0; // Ignorerer tidligere poeng, f.eks. når brukeren logger ut
        if (gamle_poeng < poeng){
            brukerinfo.edit().putInt("LEVEL_"+gruppe.toUpperCase()+"_"+kategori.toUpperCase(),poeng).apply();
        }
    }

}
