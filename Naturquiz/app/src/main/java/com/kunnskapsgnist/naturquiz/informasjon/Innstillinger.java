package com.kunnskapsgnist.naturquiz.informasjon;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class Innstillinger {
    SharedPreferences innstillinger;
    private final String NR_SPM_TOTALT = "nrSpmTotalt";
    private final String LEVEL = "level";
    private final String TYPE = "type";
    private final String GRUPPE = "gruppe";
    private final String KATEGORI = "kategori";

    public Innstillinger(Activity context) {
        this.innstillinger = context.getSharedPreferences("Innstillinger",Context.MODE_PRIVATE);
    }

    public void setAntallSpmTotalt(int nrSpmTotalt){
        innstillinger.edit().putInt(NR_SPM_TOTALT,nrSpmTotalt).apply();
    }

    public void setLevel(String level){
        innstillinger.edit().putString(LEVEL,level).apply();
    }

    public void setGruppe(String gruppe){
        innstillinger.edit().putString(GRUPPE,gruppe.replace("Demo: ","")).apply();
    }

    public void setType(String type){
        innstillinger.edit().putString(TYPE,type).apply();
    }

    public void setKategori(String kategori){
        innstillinger.edit().putString(KATEGORI,kategori).apply();
    }

    public int getAntallSpmTotalt(){
        return innstillinger.getInt(NR_SPM_TOTALT,10);
    }
    public String getLevel(){ return innstillinger.getString(LEVEL,"Demo"); }                   // Demo, Lett, Medium, Vanskelig, Ekspert
    public String getType(){ return innstillinger.getString(TYPE,"Fugl"); }                     // type, plante, sopp, insekt, dyr
    public String getGruppe(){ return innstillinger.getString(GRUPPE,"Fugler"); }               // fugler, blomster, trær
    public String getKategori(){ return innstillinger.getString(KATEGORI,"Spm_bilder"); }           // bilder, lyd, str, vekt
    public String getKategoriFire(){ return getKategori().toLowerCase().replace(" ",""); }

}
