package com.kunnskapsgnist.naturquiz.modell;

import android.util.Log;

public class Media {
    private static final String TAG = "Naturquiz";
    private final int id, gbif;
    private final String type, mappe, mediefil, eier, lisens, tittel, lenke;

    public Media(int id, int gbif, String type, String mappe, String mediefil, String eier, String lisens, String tittel, String lenke) {
        this.id = id;
        this.gbif = gbif;
        this.type = type;
        this.mappe = mappe;
        this.mediefil = mediefil;
        this.eier = eier;
        this.lisens = lisens;
        this.tittel = tittel;
        this.lenke = lenke;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public int getGbif() {
        return gbif;
    }

    public String getMappe() {
        return mappe;
    }

    public String getMediefil() {
        return mediefil;
    }

    public String getEier() {
        return eier;
    }

    public String getLisens() {
        return lisens;
    }

    public String getTittel() {
        return tittel;
    }

    public String getLenke() {
        return lenke;
    }

    public String getLisensLenke() {
        switch (lisens) {
            case "CC0":
                return "https://creativecommons.org/publicdomain/zero/1.0/deed.no";
            case "CC BY-SA 2.0":
                return "https://creativecommons.org/licenses/by-sa/2.0/deed.no"; // ok
            case "CC BY-SA 2.5":
                return "https://creativecommons.org/licenses/by-sa/2.5/deed.no"; // ok
            case "CC BY-SA 3.0":
                return "https://creativecommons.org/licenses/by-sa/3.0/deed.no"; // ok
            case "CC BY-SA 4.0":
                return "https://creativecommons.org/licenses/by-sa/4.0/deed.no"; // ok
            case "CC BY 2.0":
                return "https://creativecommons.org/licenses/by/2.0/deed.no"; // ok
            case "CC BY 2.5":
                return "https://creativecommons.org/licenses/by/2.5/deed.no"; // ok
            case "CC BY 3.0":
                return "https://creativecommons.org/licenses/by/3.0/deed.no"; // ok
            case "CC BY 4.0":
                return "https://creativecommons.org/licenses/by/4.0/deed.no"; // ok
            case "CC BY-SA 2.0 de":
                return "https://creativecommons.org/licenses/by-sa/2.0/de/deed.no"; // ok
            case "CC BY-SA 2.5 se":
                return "https://creativecommons.org/licenses/by-sa/2.5/se/deed.no"; // ok
            case "Offentlig eiendom":
                return "Offentlig eiendom";
            case "Tillatelse gitt":
                return "Tillatelse gitt";
            default:
                return "";
        }
    }

    public String getMediaurl() {
        String url_start = "https://raw.githubusercontent.com/kunnskapsgnist/SeNatur/main/";
        return url_start + type + "/" + mappe + "/" + mediefil;
    }
}
