package com.kunnskapsgnist.naturquiz.modell;

import android.util.Log;

import androidx.annotation.NonNull;

public class Art {
    private static final String TAG = "Naturquiz";

    private final int id;
    private final String art;
    private final String familie;
    private double min_lengde;
    private double max_lengde;
    private double min_vekt;
    private double max_vekt;
    private String fotspor, bilde;              // Brukes kun på fotspor
    private boolean vis_info;

    public Art(int id, String art, String familie, double min_lengde, double max_lengde, double min_vekt, double max_vekt) {
        this.id = id;
        this.art = art;
        this.familie = familie;
        this.min_lengde = min_lengde;
        this.max_lengde = max_lengde;
        this.min_vekt = min_vekt;
        this.max_vekt = max_vekt;
    }

    // Brukes kun på fotspor
    public Art(int id, String art, String familie, String fotspor, String bilde) {
        this.id = id;
        this.art = art;
        this.familie = familie;
        this.fotspor = fotspor;
        this.bilde = bilde;
    }

    public int getId() {
        return id;
    }
    public String getArt() {
        return art;
    }
    public String getFamilie() {
        return familie;
    }
    public double getLengde(){ return (min_lengde + max_lengde)/2;}
    public double getVekt(){ return (min_vekt + max_vekt)/2;}

    public String getFotspor_url() {
        return "https://raw.githubusercontent.com/kunnskapsgnist/SeNatur/main/Dyr/"
                + getFamilie() + "/" + fotspor.replace("jpg","png");}

    public String getFotsporInfo_url() {
        return "https://raw.githubusercontent.com/kunnskapsgnist/SeNatur/main/Dyr/"
                + getFamilie() + "/" + fotspor.replace("98","99");}

    public String getBilde_url() {
        Log.d(TAG, "getBilde_url: bilde");
        return "https://raw.githubusercontent.com/kunnskapsgnist/SeNatur/main/Dyr/"
                + getFamilie() + "/" + bilde;}

    public double getStr(String kategori) {
        if (kategori.equals("lengde") | kategori.equals("vingespenn")) return getLengde();
        else if (kategori.equals("vekt")) return getVekt();
        else return 0;
    }

    public boolean getVisInfo() {
        return vis_info;
    }

    public void setVisInfo(boolean vis_info) {
        this.vis_info = vis_info;
    }


    @NonNull
    @Override
    public String toString() {
        return "Art{" +
                "id=" + id +
                ", art='" + art + '\'' +
                ", familie='" + familie + '\'' +
                ", lengde='" + min_lengde + "-" + max_lengde + '\'' +
                ", vekt'=" + min_vekt + "-" + max_vekt + '\'' +
                '}';
    }

}

