package com.kunnskapsgnist.naturquiz.modell;

// Henter informasjon fra Firestore om spillerne
// - Brukes i Menu_login og Highscores
// - Informasjonen skrives til brukerInfo og Lagret
public class Spiller {
    private static final String TAG = "Naturquiz";

    String brukerNavn;
    String brukerId;
    String epost;
    Boolean oppgrader;

    int Fugler_bilder_Demo, Fugler_bilder_Lett, Fugler_bilder_Medium, Fugler_bilder_Vanskelig, Fugler_bilder_Ekspert, Fugler_bilder_Ekstrem;
    int Fugler_vingespenn_Demo, Fugler_vingespenn_Lett, Fugler_vingespenn_Medium, Fugler_vingespenn_Vanskelig, Fugler_vingespenn_Ekspert, Fugler_vingespenn_Ekstrem;
    int Fugler_vekt_Demo, Fugler_vekt_Lett, Fugler_vekt_Medium, Fugler_vekt_Vanskelig, Fugler_vekt_Ekspert, Fugler_vekt_Ekstrem;
    int Fugler_lyd_Demo, Fugler_lyd_Lett, Fugler_lyd_Medium, Fugler_lyd_Vanskelig, Fugler_lyd_Ekspert, Fugler_lyd_Ekstrem;
    int Blomster_bilder_Demo, Blomster_bilder_Lett, Blomster_bilder_Medium, Blomster_bilder_Vanskelig, Blomster_bilder_Ekspert, Blomster_bilder_Ekstrem;
    int Trar_bilder_Demo, Trar_bilder_Lett, Trar_bilder_Medium, Trar_bilder_Vanskelig, Trar_bilder_Ekspert, Trar_bilder_Ekstrem;
    int Bregner_bilder_Demo, Bregner_bilder_Lett, Bregner_bilder_Medium, Bregner_bilder_Vanskelig, Bregner_bilder_Ekspert, Bregner_bilder_Ekstrem;
    int Sopp_bilder_Demo, Sopp_bilder_Lett, Sopp_bilder_Medium, Sopp_bilder_Vanskelig, Sopp_bilder_Ekspert, Sopp_bilder_Ekstrem;
    int Sommerfugler_bilder_Demo, Sommerfugler_bilder_Lett, Sommerfugler_bilder_Medium, Sommerfugler_bilder_Vanskelig, Sommerfugler_bilder_Ekspert, Sommerfugler_bilder_Ekstrem;
    int Sommerfugler_vingespenn_Demo, Sommerfugler_vingespenn_Lett, Sommerfugler_vingespenn_Medium, Sommerfugler_vingespenn_Vanskelig, Sommerfugler_vingespenn_Ekspert, Sommerfugler_vingespenn_Ekstrem;
    int Edderkopper_bilder_Demo, Edderkopper_bilder_Lett, Edderkopper_bilder_Medium, Edderkopper_bilder_Vanskelig, Edderkopper_bilder_Ekspert, Edderkopper_bilder_Ekstrem;
    int Insekter_bilder_Demo, Insekter_bilder_Lett, Insekter_bilder_Medium, Insekter_bilder_Vanskelig, Insekter_bilder_Ekspert, Insekter_bilder_Ekstrem;
    int Dyr_bilder_Demo, Dyr_bilder_Lett, Dyr_bilder_Medium, Dyr_bilder_Vanskelig, Dyr_bilder_Ekspert, Dyr_bilder_Ekstrem;
    int Dyr_lengde_Demo, Dyr_lengde_Lett, Dyr_lengde_Medium, Dyr_lengde_Vanskelig, Dyr_lengde_Ekspert, Dyr_lengde_Ekstrem;
    int Dyr_vekt_Demo, Dyr_vekt_Lett, Dyr_vekt_Medium, Dyr_vekt_Vanskelig, Dyr_vekt_Ekspert, Dyr_vekt_Ekstrem;
    int Fotspor_fotspor_Demo, Fotspor_fotspor_Lett, Fotspor_fotspor_Medium, Fotspor_fotspor_Vanskelig, Fotspor_fotspor_Ekspert, Fotspor_fotspor_Ekstrem;

    int level_Fugler_bilder, level_Fugler_lyd, level_Fugler_vingespenn, level_Fugler_vekt;
    int level_Blomster_bilder, level_Trar_bilder, level_Bregner_bilder;
    int level_Sopp_bilder;
    int level_Sommerfugler_bilder, level_Sommerfugler_vingespenn, level_Edderkopper_bilder, level_Insekter_bilder;
    int level_Dyr_bilder, level_Dyr_lengde, level_Dyr_vekt, level_Fotspor_fotspor;

    // Constructor
    public Spiller(){}

    // Informasjon som skrives til brukerInfo:
    public String getBrukerNavn() { return brukerNavn; }
    public String getBrukerId() { return brukerId; }
    public String getEpost() { return epost; }

    public Boolean getOppgrader() {if (oppgrader ==null) return false; else return oppgrader;}

    // Lager en highscorene-struktur som lettere kan hentes verdier fra
    public int getRekorder(String gruppe, String kategori, String level){
        switch (gruppe+"_"+kategori+"_"+level){
            case "Fugler_bilder_Demo": return getFugler_bilder_Demo();
            case "Fugler_bilder_Lett": return getFugler_bilder_Lett();
            case "Fugler_bilder_Medium": return getFugler_bilder_Medium();
            case "Fugler_bilder_Vanskelig": return getFugler_bilder_Vanskelig();
            case "Fugler_bilder_Ekspert": return getFugler_bilder_Ekspert();
            case "Fugler_bilder_Ekstrem": return getFugler_bilder_Ekstrem();
            case "Fugler_lyd_Demo": return getFugler_lyd_Demo();
            case "Fugler_lyd_Lett": return getFugler_lyd_Lett();
            case "Fugler_lyd_Medium": return getFugler_lyd_Medium();
            case "Fugler_lyd_Vanskelig": return getFugler_lyd_Vanskelig();
            case "Fugler_lyd_Ekspert": return getFugler_lyd_Ekspert();
            case "Fugler_lyd_Ekstrem": return getFugler_lyd_Ekstrem();
            case "Fugler_vingespenn_Demo": return getFugler_vingespenn_Demo();
            case "Fugler_vingespenn_Lett": return getFugler_vingespenn_Lett();
            case "Fugler_vingespenn_Medium": return getFugler_vingespenn_Medium();
            case "Fugler_vingespenn_Vanskelig": return getFugler_vingespenn_Vanskelig();
            case "Fugler_vingespenn_Ekspert": return getFugler_vingespenn_Ekspert();
            case "Fugler_vingespenn_Ekstrem": return getFugler_vingespenn_Ekstrem();
            case "Fugler_vekt_Demo": return getFugler_vekt_Demo();
            case "Fugler_vekt_Lett": return getFugler_vekt_Lett();
            case "Fugler_vekt_Medium": return getFugler_vekt_Medium();
            case "Fugler_vekt_Vanskelig": return getFugler_vekt_Vanskelig();
            case "Fugler_vekt_Ekspert": return getFugler_vekt_Ekspert();
            case "Fugler_vekt_Ekstrem": return getFugler_vekt_Ekstrem();

            case "Blomster_bilder_Demo": return getBlomster_bilder_Demo();
            case "Blomster_bilder_Lett": return getBlomster_bilder_Lett();
            case "Blomster_bilder_Medium": return getBlomster_bilder_Medium();
            case "Blomster_bilder_Vanskelig": return getBlomster_bilder_Vanskelig();
            case "Blomster_bilder_Ekspert": return getBlomster_bilder_Ekspert();
            case "Blomster_bilder_Ekstrem": return getBlomster_bilder_Ekstrem();
            case "Trar_bilder_Demo": return getTrar_bilder_Demo();
            case "Trar_bilder_Lett": return getTrar_bilder_Lett();
            case "Trar_bilder_Medium": return getTrar_bilder_Medium();
            case "Trar_bilder_Vanskelig": return getTrar_bilder_Vanskelig();
            case "Trar_bilder_Ekspert": return getTrar_bilder_Ekspert();
            case "Trar_bilder_Ekstrem": return getTrar_bilder_Ekstrem();
            case "Bregner_bilder_Demo": return getBregner_bilder_Demo();
            case "Bregner_bilder_Lett": return getBregner_bilder_Lett();
            case "Bregner_bilder_Medium": return getBregner_bilder_Medium();
            case "Bregner_bilder_Vanskelig": return getBregner_bilder_Vanskelig();
            case "Bregner_bilder_Ekspert": return getBregner_bilder_Ekspert();
            case "Bregner_bilder_Ekstrem": return getBregner_bilder_Ekstrem();

            case "Sopp_bilder_Demo": return getSopp_bilder_Demo();
            case "Sopp_bilder_Lett": return getSopp_bilder_Lett();
            case "Sopp_bilder_Medium": return getSopp_bilder_Medium();
            case "Sopp_bilder_Vanskelig": return getSopp_bilder_Vanskelig();
            case "Sopp_bilder_Ekspert": return getSopp_bilder_Ekspert();
            case "Sopp_bilder_Ekstrem": return getSopp_bilder_Ekstrem();

            case "Sommerfugler_bilder_Demo": return getSommerfugler_bilder_Demo();
            case "Sommerfugler_bilder_Lett": return getSommerfugler_bilder_Lett();
            case "Sommerfugler_bilder_Medium": return getSommerfugler_bilder_Medium();
            case "Sommerfugler_bilder_Vanskelig": return getSommerfugler_bilder_Vanskelig();
            case "Sommerfugler_bilder_Ekspert": return getSommerfugler_bilder_Ekspert();
            case "Sommerfugler_bilder_Ekstrem": return getSommerfugler_bilder_Ekstrem();
            case "Sommerfugler_vingespenn_Demo": return getSommerfugler_vingespenn_Demo();
            case "Sommerfugler_vingespenn_Lett": return getSommerfugler_vingespenn_Lett();
            case "Sommerfugler_vingespenn_Medium": return getSommerfugler_vingespenn_Medium();
            case "Sommerfugler_vingespenn_Vanskelig": return getSommerfugler_vingespenn_Vanskelig();
            case "Sommerfugler_vingespenn_Ekspert": return getSommerfugler_vingespenn_Ekspert();
            case "Sommerfugler_vingespenn_Ekstrem": return getSommerfugler_vingespenn_Ekstrem();
            case "Edderkopper_bilder_Demo": return getEdderkopper_bilder_Demo();
            case "Edderkopper_bilder_Lett": return getEdderkopper_bilder_Lett();
            case "Edderkopper_bilder_Medium": return getEdderkopper_bilder_Medium();
            case "Edderkopper_bilder_Vanskelig": return getEdderkopper_bilder_Vanskelig();
            case "Edderkopper_bilder_Ekspert": return getEdderkopper_bilder_Ekspert();
            case "Edderkopper_bilder_Ekstrem": return getEdderkopper_bilder_Ekstrem();
            case "Insekter_bilder_Demo": return getInsekter_bilder_Demo();
            case "Insekter_bilder_Lett": return getInsekter_bilder_Lett();
            case "Insekter_bilder_Medium": return getInsekter_bilder_Medium();
            case "Insekter_bilder_Vanskelig": return getInsekter_bilder_Vanskelig();
            case "Insekter_bilder_Ekspert": return getInsekter_bilder_Ekspert();
            case "Insekter_bilder_Ekstrem": return getInsekter_bilder_Ekstrem();

            case "Dyr_bilder_Demo": return getDyr_bilder_Demo();
            case "Dyr_bilder_Lett": return getDyr_bilder_Lett();
            case "Dyr_bilder_Medium": return getDyr_bilder_Medium();
            case "Dyr_bilder_Vanskelig": return getDyr_bilder_Vanskelig();
            case "Dyr_bilder_Ekspert": return getDyr_bilder_Ekspert();
            case "Dyr_bilder_Ekstrem": return getDyr_bilder_Ekstrem();
            case "Dyr_lengde_Demo": return getDyr_lengde_Demo();
            case "Dyr_lengde_Lett": return getDyr_lengde_Lett();
            case "Dyr_lengde_Medium": return getDyr_lengde_Medium();
            case "Dyr_lengde_Vanskelig": return getDyr_lengde_Vanskelig();
            case "Dyr_lengde_Ekspert": return getDyr_lengde_Ekspert();
            case "Dyr_lengde_Ekstrem": return getDyr_lengde_Ekstrem();
            case "Dyr_vekt_Demo": return getDyr_vekt_Demo();
            case "Dyr_vekt_Lett": return getDyr_vekt_Lett();
            case "Dyr_vekt_Medium": return getDyr_vekt_Medium();
            case "Dyr_vekt_Vanskelig": return getDyr_vekt_Vanskelig();
            case "Dyr_vekt_Ekspert": return getDyr_vekt_Ekspert();
            case "Dyr_vekt_Ekstrem": return getDyr_vekt_Ekstrem();
            case "Fotspor_fotspor_Demo": return getFotspor_fotspor_Demo();
            case "Fotspor_fotspor_Lett": return getFotspor_fotspor_Lett();
            case "Fotspor_fotspor_Medium": return getFotspor_fotspor_Medium();
            case "Fotspor_fotspor_Vanskelig": return getFotspor_fotspor_Vanskelig();
            case "Fotspor_fotspor_Ekspert": return getFotspor_fotspor_Ekspert();
            case "Fotspor_fotspor_Ekstrem": return getFotspor_fotspor_Ekstrem();

            default: return 0;
        }
    }

    public int getLevelPoeng(String gruppe, String kategori) {
        switch (gruppe+"_"+kategori){
            case "Fugler_bilder": return getLevel_Fugler_bilder();
            case "Fugler_lyd": return getLevel_Fugler_lyd();
            case "Fugler_vingespenn": return getLevel_Fugler_vingespenn();
            case "Fugler_vekt": return getLevel_Fugler_vekt();
            case "Blomster_bilder": return getLevel_Blomster_bilder();
            case "Trær_bilder": return getLevel_Trar_bilder();
            case "Bregner_bilder": return getLevel_Bregner_bilder();
            case "Sopp_bilder": return getLevel_Sopp_bilder();
            case "Sommerfugler_bilder": return getLevel_Sommerfugler_bilder();
            case "Sommerfugler_vingespenn": return getLevel_Sommerfugler_vingespenn();
            case "Edderkopper_bilder": return getLevel_Edderkopper_bilder();
            case "Insekter_bilder": return getLevel_Insekter_bilder();
            case "Dyr_bilder": return getLevel_Dyr_bilder();
            case "Dyr_lengde": return getLevel_Dyr_lengde();
            case "Dyr_vekt": return getLevel_Dyr_vekt();
            case "Fotspor_fotspor": return getLevel_Fotspor_fotspor();
            default: return 0;
        }
    }

    public int getFugler_bilder_Demo() {
        return Fugler_bilder_Demo;
    }
    public int getFugler_bilder_Lett() {
        return Fugler_bilder_Lett;
    }
    public int getFugler_bilder_Medium() {
        return Fugler_bilder_Medium;
    }
    public int getFugler_bilder_Vanskelig() {
        return Fugler_bilder_Vanskelig;
    }
    public int getFugler_bilder_Ekspert() {
        return Fugler_bilder_Ekspert;
    }
    public int getFugler_bilder_Ekstrem() {
        return Fugler_bilder_Ekstrem;
    }

    public int getFugler_lyd_Demo() {return Fugler_lyd_Demo;}
    public int getFugler_lyd_Lett() {return Fugler_lyd_Lett;}
    public int getFugler_lyd_Medium() {return Fugler_lyd_Medium;}
    public int getFugler_lyd_Vanskelig() {return Fugler_lyd_Vanskelig;}
    public int getFugler_lyd_Ekspert() {return Fugler_lyd_Ekspert;}
    public int getFugler_lyd_Ekstrem() {return Fugler_lyd_Ekstrem;}

    public int getFugler_vingespenn_Demo() {return Fugler_vingespenn_Demo;}
    public int getFugler_vingespenn_Lett() {return Fugler_vingespenn_Lett;}
    public int getFugler_vingespenn_Medium() {return Fugler_vingespenn_Medium;}
    public int getFugler_vingespenn_Vanskelig() {return Fugler_vingespenn_Vanskelig;}
    public int getFugler_vingespenn_Ekspert() {return Fugler_vingespenn_Ekspert;}
    public int getFugler_vingespenn_Ekstrem() {return Fugler_vingespenn_Ekstrem;}

    public int getFugler_vekt_Demo() {return Fugler_vekt_Demo;}
    public int getFugler_vekt_Lett() {return Fugler_vekt_Lett;}
    public int getFugler_vekt_Medium() {return Fugler_vekt_Medium;}
    public int getFugler_vekt_Vanskelig() {return Fugler_vekt_Vanskelig;}
    public int getFugler_vekt_Ekspert() {return Fugler_vekt_Ekspert;}
    public int getFugler_vekt_Ekstrem() {return Fugler_vekt_Ekstrem;}

    public int getBlomster_bilder_Demo() {
        return Blomster_bilder_Demo;
    }
    public int getBlomster_bilder_Lett() {
        return Blomster_bilder_Lett;
    }
    public int getBlomster_bilder_Medium() {
        return Blomster_bilder_Medium;
    }
    public int getBlomster_bilder_Vanskelig() {
        return Blomster_bilder_Vanskelig;
    }
    public int getBlomster_bilder_Ekspert() {
        return Blomster_bilder_Ekspert;
    }
    public int getBlomster_bilder_Ekstrem() {
        return Blomster_bilder_Ekstrem;
    }

    public int getTrar_bilder_Demo() {
        return Trar_bilder_Demo;
    }
    public int getTrar_bilder_Lett() {
        return Trar_bilder_Lett;
    }
    public int getTrar_bilder_Medium() {
        return Trar_bilder_Medium;
    }
    public int getTrar_bilder_Vanskelig() {
        return Trar_bilder_Vanskelig;
    }
    public int getTrar_bilder_Ekspert() {
        return Trar_bilder_Ekspert;
    }
    public int getTrar_bilder_Ekstrem() {
        return Trar_bilder_Ekstrem;
    }

    public int getBregner_bilder_Demo() {return Bregner_bilder_Demo;}
    public int getBregner_bilder_Lett() {return Bregner_bilder_Lett;}
    public int getBregner_bilder_Medium() {return Bregner_bilder_Medium;}
    public int getBregner_bilder_Vanskelig() {return Bregner_bilder_Vanskelig;}
    public int getBregner_bilder_Ekspert() {return Bregner_bilder_Ekspert;}
    public int getBregner_bilder_Ekstrem() {return Bregner_bilder_Ekstrem;}

    public int getSopp_bilder_Demo() {return Sopp_bilder_Demo;}
    public int getSopp_bilder_Lett() {return Sopp_bilder_Lett;}
    public int getSopp_bilder_Medium() {return Sopp_bilder_Medium;}
    public int getSopp_bilder_Vanskelig() {return Sopp_bilder_Vanskelig;}
    public int getSopp_bilder_Ekspert() {return Sopp_bilder_Ekspert;}
    public int getSopp_bilder_Ekstrem() {return Sopp_bilder_Ekstrem;}

    public int getSommerfugler_bilder_Demo() {
        return Sommerfugler_bilder_Demo;
    }
    public int getSommerfugler_bilder_Lett() {
        return Sommerfugler_bilder_Lett;
    }
    public int getSommerfugler_bilder_Medium() {
        return Sommerfugler_bilder_Medium;
    }
    public int getSommerfugler_bilder_Vanskelig() {
        return Sommerfugler_bilder_Vanskelig;
    }
    public int getSommerfugler_bilder_Ekspert() {
        return Sommerfugler_bilder_Ekspert;
    }
    public int getSommerfugler_bilder_Ekstrem() {
        return Sommerfugler_bilder_Ekstrem;
    }

    public int getSommerfugler_vingespenn_Demo() {return Sommerfugler_vingespenn_Demo;}
    public int getSommerfugler_vingespenn_Lett() {return Sommerfugler_vingespenn_Lett;}
    public int getSommerfugler_vingespenn_Medium() {return Sommerfugler_vingespenn_Medium;}
    public int getSommerfugler_vingespenn_Vanskelig() {return Sommerfugler_vingespenn_Vanskelig;}
    public int getSommerfugler_vingespenn_Ekspert() {return Sommerfugler_vingespenn_Ekspert;}
    public int getSommerfugler_vingespenn_Ekstrem() {return Sommerfugler_vingespenn_Ekstrem;}

    public int getEdderkopper_bilder_Demo() {return Edderkopper_bilder_Demo;}
    public int getEdderkopper_bilder_Lett() {return Edderkopper_bilder_Lett;}
    public int getEdderkopper_bilder_Medium() {return Edderkopper_bilder_Medium;}
    public int getEdderkopper_bilder_Vanskelig() {return Edderkopper_bilder_Vanskelig;}
    public int getEdderkopper_bilder_Ekspert() {return Edderkopper_bilder_Ekspert;}
    public int getEdderkopper_bilder_Ekstrem() {return Edderkopper_bilder_Ekstrem;}

    public int getInsekter_bilder_Demo() {return Insekter_bilder_Demo;}
    public int getInsekter_bilder_Lett() {return Insekter_bilder_Lett;}
    public int getInsekter_bilder_Medium() {return Insekter_bilder_Medium;}
    public int getInsekter_bilder_Vanskelig() {return Insekter_bilder_Vanskelig;}
    public int getInsekter_bilder_Ekspert() {return Insekter_bilder_Ekspert;}
    public int getInsekter_bilder_Ekstrem() {return Insekter_bilder_Ekstrem;}

    public int getDyr_bilder_Demo() {
        return Dyr_bilder_Demo;
    }
    public int getDyr_bilder_Lett() {
        return Dyr_bilder_Lett;
    }
    public int getDyr_bilder_Medium() {
        return Dyr_bilder_Medium;
    }
    public int getDyr_bilder_Vanskelig() {
        return Dyr_bilder_Vanskelig;
    }
    public int getDyr_bilder_Ekspert() {
        return Dyr_bilder_Ekspert;
    }
    public int getDyr_bilder_Ekstrem() {
        return Dyr_bilder_Ekstrem;
    }

    public int getDyr_lengde_Demo() {return Dyr_lengde_Demo;}
    public int getDyr_lengde_Lett() {return Dyr_lengde_Lett;}
    public int getDyr_lengde_Medium() {return Dyr_lengde_Medium;}
    public int getDyr_lengde_Vanskelig() {return Dyr_lengde_Vanskelig;}
    public int getDyr_lengde_Ekspert() {return Dyr_lengde_Ekspert;}
    public int getDyr_lengde_Ekstrem() {return Dyr_lengde_Ekstrem;}

    public int getDyr_vekt_Demo() {return Dyr_vekt_Demo;}
    public int getDyr_vekt_Lett() {return Dyr_vekt_Lett;}
    public int getDyr_vekt_Medium() {return Dyr_vekt_Medium;}
    public int getDyr_vekt_Vanskelig() {return Dyr_vekt_Vanskelig;}
    public int getDyr_vekt_Ekspert() {return Dyr_vekt_Ekspert;}
    public int getDyr_vekt_Ekstrem() {return Dyr_vekt_Ekstrem;}

    public int getFotspor_fotspor_Demo() {return Fotspor_fotspor_Demo;}
    public int getFotspor_fotspor_Lett() {return Fotspor_fotspor_Lett;}
    public int getFotspor_fotspor_Medium() {return Fotspor_fotspor_Medium;}
    public int getFotspor_fotspor_Vanskelig() {return Fotspor_fotspor_Vanskelig;}
    public int getFotspor_fotspor_Ekspert() {return Fotspor_fotspor_Ekspert;}
    public int getFotspor_fotspor_Ekstrem() {return Fotspor_fotspor_Ekstrem;}

    public int getLevel_Fugler_bilder() {
        return level_Fugler_bilder;
    }

    public int getLevel_Fugler_lyd() {
        return level_Fugler_lyd;
    }

    public int getLevel_Fugler_vingespenn() {
        return level_Fugler_vingespenn;
    }

    public int getLevel_Fugler_vekt() {
        return level_Fugler_vekt;
    }

    public int getLevel_Blomster_bilder() {
        return level_Blomster_bilder;
    }

    public int getLevel_Trar_bilder() {
        return level_Trar_bilder;
    }

    public int getLevel_Bregner_bilder() {
        return level_Bregner_bilder;
    }

    public int getLevel_Sopp_bilder() {
        return level_Sopp_bilder;
    }

    public int getLevel_Sommerfugler_bilder() {
        return level_Sommerfugler_bilder;
    }

    public int getLevel_Sommerfugler_vingespenn() {
        return level_Sommerfugler_vingespenn;
    }

    public int getLevel_Edderkopper_bilder() {
        return level_Edderkopper_bilder;
    }

    public int getLevel_Insekter_bilder() {
        return level_Insekter_bilder;
    }

    public int getLevel_Dyr_bilder() {
        return level_Dyr_bilder;
    }

    public int getLevel_Dyr_lengde() {
        return level_Dyr_lengde;
    }

    public int getLevel_Dyr_vekt() {
        return level_Dyr_vekt;
    }

    public int getLevel_Fotspor_fotspor() {
        return level_Fotspor_fotspor;
    }
}
