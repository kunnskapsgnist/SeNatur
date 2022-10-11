package com.kunnskapsgnist.naturquiz.informasjon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Lagret {
    private static final String TAG = "Naturquiz";
    SharedPreferences lagret;
    private final String NR_SPM = "NrSpm";
    private final String POENG = "Poeng";

    public Lagret(Activity context) {
        this.lagret = context.getSharedPreferences("Lagret",Context.MODE_PRIVATE);
    }

    // NrSpm og Poeng brukes underveis i spillene
    public void setNrSpm(int NrSpm){
        lagret.edit().putInt(NR_SPM,NrSpm).apply();
    }
    public void setPoeng(int Poeng){
        lagret.edit().putInt(POENG,Poeng).apply();
    }

    public int getNrSpm(){
        return lagret.getInt(NR_SPM,1);
    }
    public int getPoeng(){
        return lagret.getInt(POENG,0);
    }

    // Lagrer og henter highscore i alle kateogrier, level og antall spørsmål
    public boolean lagreRekord(int nye_poeng, String type, String gruppe, String kategori, String level) {
        boolean ny = false;

        // Sjekker om det er flere poeng nå enn før og lagrer hvis brukeren er logget inn
        int lagret_poeng = getRekord(type, gruppe, kategori, level);
        if (lagret_poeng < nye_poeng) {
            ny = true;
            lagret.edit().putInt(getKombinasjon(type,gruppe,kategori,level), nye_poeng).apply();
        }

        return ny;
    }

    // Når highscorene hentes fra Firebase ved oppstart og innlogging, lagres de her
    public void setHighscore(int poeng, String type, String gruppe, String kategori, String level){
        lagret.edit().putInt(getKombinasjon(type,gruppe,kategori,level), poeng).apply();
    }

    public int getRekord(String type, String gruppe, String kategori, String level){
        return lagret.getInt(getKombinasjon(type,gruppe,kategori,level),0);
    }

    public String getKombinasjon(String type, String gruppe, String kategori, String level){
        String kombinasjon = type + "_" + gruppe + "_" + kategori + "_" + level;
        return kombinasjon.replace("æ","a").replace("ø","o").replace("å","a");
    }

    public String getFireKombinasjon(String gruppe, String kategori, String level) {
        String kombinasjon = gruppe + "_" + kategori + "_" + level;
        return kombinasjon.replace("æ","a").replace("ø","o").replace("å","a");
    }
}
