package com.kunnskapsgnist.naturquiz.modell;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class PoengHistorie {
    private String spiller, poeng, type, gruppe, kategori, level, dato;
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    public PoengHistorie() {}

    public PoengHistorie(String spiller, String poeng, String type, String gruppe, String kategori, String level) {
        this.spiller = spiller;
        this.poeng = poeng;
        this.type = type;
        this.gruppe = gruppe;
        this.kategori = kategori;
        this.level = level;
        this.dato = LocalDateTime.now().format(dateTimeFormatter);
    }

    public String getSpiller() { return spiller; }
    public void setSpiller(String spiller) { this.spiller = spiller; }

    public String getPoeng() { return poeng; }
    public void setPoeng(String poeng) { this.poeng = poeng; }

    public String getType() {return type;}
    public void setType(String type) {this.type = type;}

    public String getGruppe() {return gruppe;}
    public void setGruppe(String gruppe) {this.gruppe = gruppe;}

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getDato() { return dato; }
    public void setDato(String dato) { this.dato = dato; }
}
