package com.kunnskapsgnist.naturquiz.modell;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Fil {
    private static final String TAG = "Naturquiz";

    private final String gruppe;
    private final int antall_demo, antall_lett, antall_medium, antall_vanskelig, antall_ekspert, antall_ekstrem;

    public Fil(String gruppe, int antall_demo, int antall_lett, int antall_medium, int antall_vanskelig, int antall_ekspert, int antall_ekstrem) {
        this.gruppe = gruppe;
        this.antall_demo = antall_demo;
        this.antall_lett = antall_lett;
        this.antall_medium = antall_medium;
        this.antall_vanskelig = antall_vanskelig;
        this.antall_ekspert = antall_ekspert;
        this.antall_ekstrem = antall_ekstrem;
    }

    public String getGruppe() {
        return gruppe;
    }

    public int getAntall_demo() {
        return antall_demo;
    }

    public int getAntall_lett() {
        return antall_lett;
    }

    public int getAntall_medium() {
        return antall_medium;
    }

    public int getAntall_vanskelig() {
        return antall_vanskelig;
    }

    public int getAntall_ekspert() {
        return antall_ekspert;
    }

    public int getAntall_ekstrem() {
        return antall_ekstrem;
    }

    public ArrayList<String> hentLevel(){
        ArrayList<String> listLevel = new ArrayList<>();
        listLevel.add("Demo");
        if (antall_lett > 0) listLevel.add("Lett");
        if (antall_medium > 0) listLevel.add("Medium");
        if (antall_vanskelig > 0) listLevel.add("Vanskelig");
        if (antall_ekspert > 0) listLevel.add("Ekspert");
        if (antall_ekstrem > 0) listLevel.add("Ekstrem");
        return listLevel;
    }

    public String antallArter() {
        StringBuilder tekst = new StringBuilder();
        tekst.append("Demo:   ").append(antall_demo).append(" arter");
        if (antall_lett > 0)
            tekst.append("\nLett:   ").append(antall_lett).append(" arter");
        else
            tekst.append("\nFlere nivåer kommer i nær fremtid");
        if (antall_medium > 0)
            tekst.append("\nMedium: ").append(antall_medium).append(" arter");
        if (antall_vanskelig > 0)
            tekst.append("\nVanskelig: ").append(antall_vanskelig).append(" arter");
        if (antall_ekspert > 0)
            tekst.append("\nEkspert: ").append(antall_ekspert).append(" arter");
        if (antall_ekstrem > 0)
            tekst.append("\nEkstrem: ").append(antall_ekstrem).append(" arter");

        return tekst.toString();
    }

}

