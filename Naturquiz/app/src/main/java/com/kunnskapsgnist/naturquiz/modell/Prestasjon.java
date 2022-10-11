package com.kunnskapsgnist.naturquiz.modell;

import android.app.Activity;
import android.util.Log;

import com.kunnskapsgnist.naturquiz.informasjon.BrukerInfo;
import com.kunnskapsgnist.naturquiz.informasjon.Lagret;

public class Prestasjon {
    private static final String TAG = "Naturquiz";
    private String type, gruppe, kategori, level, nestelevel;
    private int poeng, levelpoeng, nestelevelpoeng, rekord;

    public Prestasjon(Activity activity, String type, String gruppe, String kategori) {
        this.type = type;
        this.gruppe = gruppe;
        this.kategori = kategori;

        BrukerInfo brukerInfo = new BrukerInfo(activity);
        this.level = brukerInfo.getLevel(gruppe,kategori,"Ekstrem");
        this.nestelevel = brukerInfo.getNesteLevel(gruppe,kategori);
        this.levelpoeng = brukerInfo.getLevelPoeng(gruppe,kategori);
        this.nestelevelpoeng = brukerInfo.getNesteLevelPoeng(gruppe,kategori,nestelevel);

        Lagret lagret = new Lagret(activity);
        this.rekord = lagret.getRekord(type,gruppe,kategori,level);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGruppe() {
        return gruppe;
    }

    public void setGruppe(String gruppe) {
        this.gruppe = gruppe;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getNestelevel() {
        return nestelevel;
    }

    public void setNestelevel(String nestelevel) {
        this.nestelevel = nestelevel;
    }

    public int getPoeng() {
        return poeng;
    }

    public void setPoeng(int poeng) {
        this.poeng = poeng;
    }

    public int getLevelpoeng() {
        return levelpoeng;
    }

    public void setLevelpoeng(int levelpoeng) {
        this.levelpoeng = levelpoeng;
    }

    public int getNestelevelpoeng() {
        return nestelevelpoeng;
    }

    public void setNestelevelpoeng(int nestelevelpoeng) {
        this.nestelevelpoeng = nestelevelpoeng;
    }

    public int getRekord() {
        return rekord;
    }

    public void setRekord(int rekord) {
        this.rekord = rekord;
    }
}
