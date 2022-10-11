package com.kunnskapsgnist.naturquiz.informasjon;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.kunnskapsgnist.naturquiz.R;

import java.util.Objects;

public class Farge {
    private static final String TAG = "Naturquiz";
    Context context;
    String type;
    int farge, fargelys, fargeblass;
    Drawable gradient, gradient_blank, fremdrift, nedtrekk;

    public Farge(Context context, String type) {
        this.context = context;
        this.type = type;

        switch (type){
            case "Fugl":
                farge = ContextCompat.getColor(context, R.color.bla_mork);
                fargeblass = ContextCompat.getColor(context, R.color.bla);
                fargelys = ContextCompat.getColor(context, R.color.bla_lys);
                gradient = ContextCompat.getDrawable(context, R.drawable.gradient_bla);
                gradient_blank = ContextCompat.getDrawable(context, R.drawable.gradient_bla_blank);
                fremdrift = ContextCompat.getDrawable(context, R.drawable.fremdrift_bla);
                nedtrekk = ContextCompat.getDrawable(context, R.drawable.nedtrekksmeny_bla);
                break;
            case "Plante":
                farge = ContextCompat.getColor(context, R.color.gronn_mork);
                fargeblass = ContextCompat.getColor(context, R.color.gronn);
                fargelys = ContextCompat.getColor(context, R.color.gronn_lys);
                gradient = ContextCompat.getDrawable(context, R.drawable.gradient_gronn);
                gradient_blank = ContextCompat.getDrawable(context, R.drawable.gradient_gronn_blank);
                fremdrift = ContextCompat.getDrawable(context, R.drawable.fremdrift_gronn);
                nedtrekk = ContextCompat.getDrawable(context, R.drawable.nedtrekksmeny_gronn);
                break;
            case "Sopp":
                farge = ContextCompat.getColor(context, R.color.lilla_mork);
                fargeblass = ContextCompat.getColor(context, R.color.lilla);
                fargelys = ContextCompat.getColor(context, R.color.lilla_lys);
                gradient = ContextCompat.getDrawable(context, R.drawable.gradient_lilla);
                gradient_blank = ContextCompat.getDrawable(context, R.drawable.gradient_lilla_blank);
                fremdrift = ContextCompat.getDrawable(context, R.drawable.fremdrift_lilla);
                nedtrekk = ContextCompat.getDrawable(context, R.drawable.nedtrekksmeny_lilla);
                break;
            case "Insekt":
                farge = ContextCompat.getColor(context, R.color.rod_mork);
                fargeblass = ContextCompat.getColor(context, R.color.rod);
                fargelys = ContextCompat.getColor(context, R.color.rod_lys);
                gradient = ContextCompat.getDrawable(context, R.drawable.gradient_rod);
                gradient_blank = ContextCompat.getDrawable(context, R.drawable.gradient_rod_blank);
                fremdrift = ContextCompat.getDrawable(context, R.drawable.fremdrift_rod);
                nedtrekk = ContextCompat.getDrawable(context, R.drawable.nedtrekksmeny_rod);
                break;
            case "Dyr":
                farge = ContextCompat.getColor(context, R.color.brun_mork);
                fargelys = ContextCompat.getColor(context, R.color.brun_lys);
                fargeblass = ContextCompat.getColor(context, R.color.brun);
                gradient = ContextCompat.getDrawable(context, R.drawable.gradient_brun);
                gradient_blank = ContextCompat.getDrawable(context, R.drawable.gradient_brun_blank);
                fremdrift = ContextCompat.getDrawable(context, R.drawable.fremdrift_brun);
                nedtrekk = ContextCompat.getDrawable(context, R.drawable.nedtrekksmeny_brun);
                break;
            case "Alle":
                farge = ContextCompat.getColor(context, R.color.svart);
                fargelys = ContextCompat.getColor(context, R.color.hvit);
                fargeblass = ContextCompat.getColor(context, R.color.gra);
                gradient = ContextCompat.getDrawable(context, R.drawable.gradient_lys);
                gradient_blank = ContextCompat.getDrawable(context, R.drawable.gradient_bla_blank);
                fremdrift = ContextCompat.getDrawable(context, R.drawable.fremdrift_bla);
                nedtrekk = ContextCompat.getDrawable(context, R.drawable.nedtrekksmeny_bla);
                break;
            default:
                farge = ContextCompat.getColor(context, R.color.bla_mork);
                fargelys = ContextCompat.getColor(context, R.color.bla_lys);
                fargeblass = ContextCompat.getColor(context, R.color.bla);
                gradient = ContextCompat.getDrawable(context, R.drawable.gradient_bla);
                gradient_blank = ContextCompat.getDrawable(context, R.drawable.gradient_bla_blank);
                fremdrift = ContextCompat.getDrawable(context, R.drawable.fremdrift_bla);
                nedtrekk = ContextCompat.getDrawable(context, R.drawable.nedtrekksmeny_bla);
        }
    }

    public Drawable symbolFarge(int symbol) {
        Drawable symbol_original = AppCompatResources.getDrawable(context, symbol);
        Drawable symbol_innpakket = DrawableCompat.wrap(Objects.requireNonNull(symbol_original));
        DrawableCompat.setTint(symbol_innpakket, farge);

        return symbol_innpakket;
    }

    public Drawable getBakgrunn() {
        Drawable bakgrunn_original = AppCompatResources.getDrawable(context, R.drawable.bakgrunn_knapp);
        Drawable bakgrunn_innpakket = DrawableCompat.wrap(Objects.requireNonNull(bakgrunn_original));
        DrawableCompat.setTint(bakgrunn_innpakket, farge);

        return bakgrunn_innpakket;
    }

    public Drawable getProgress() {return fremdrift;}

    public Drawable getNedtrekk() {return nedtrekk;}

    public Drawable symbolLysFarge(int symbol) {
        Drawable symbol_original = AppCompatResources.getDrawable(context, symbol);
        Drawable symbol_innpakket = DrawableCompat.wrap(Objects.requireNonNull(symbol_original));
        DrawableCompat.setTint(symbol_innpakket, fargelys);

        return symbol_innpakket;
    }

    public Drawable symbolHvit(int symbol) {
        Drawable symbol_original = AppCompatResources.getDrawable(context, symbol);
        Drawable symbol_innpakket = DrawableCompat.wrap(Objects.requireNonNull(symbol_original));
        DrawableCompat.setTint(symbol_innpakket,ContextCompat.getColor(context, R.color.hvit));

        return symbol_innpakket;
    }

    public Drawable setFarge(int symbol, int farge) {
        Drawable symbol_original = AppCompatResources.getDrawable(context, symbol);
        Drawable symbol_innpakket = DrawableCompat.wrap(Objects.requireNonNull(symbol_original));
        DrawableCompat.setTint(symbol_innpakket,ContextCompat.getColor(context, farge));

        return symbol_innpakket;
    }

    public Drawable symbolFargeStr(int symbol, double faktor) {
        Drawable symbol_original = AppCompatResources.getDrawable(context, symbol);
        Drawable symbol_innpakket = DrawableCompat.wrap(Objects.requireNonNull(symbol_original));
        DrawableCompat.setTint(symbol_innpakket, farge);

        double bredde = faktor*symbol_innpakket.getIntrinsicWidth();
        double hoyde = faktor*symbol_innpakket.getIntrinsicHeight();
        symbol_innpakket.setBounds(0,0, (int) bredde,(int) hoyde);

        return symbol_innpakket;
    }

        /*
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(symbol_innpakket.getIntrinsicWidth()/3, symbol_innpakket.getIntrinsicHeight()/3, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        symbol_innpakket.setBounds(0,0, symbol_innpakket.getIntrinsicWidth()/3, symbol_innpakket.getIntrinsicHeight()/3);
        symbol_innpakket.draw(canvas);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
        return (Drawable) bitmapDescriptor.toD;

        return symbol_innpakket;
         */

        /*
        symbol_original.setBounds(0, 0, (int)(symbol_original.getIntrinsicWidth()*0.5),
                (int)(symbol_original.getIntrinsicHeight()*0.5));
        ScaleDrawable sd = new ScaleDrawable(symbol_original, 0, 200,200);

        return sd.getDrawable();
         */
        /*
        symbol_innpakket.setBounds(0, 0, (int)(symbol_innpakket.getIntrinsicWidth()*2.0),
                (int)(symbol_innpakket.getIntrinsicHeight()*2.0));
        symbol_innpakket.setLevel(1000);

        ScaleDrawable d = new ScaleDrawable(symbol_innpakket, Gravity.TOP, 200, 200);
//        d.setBounds(100, 0, 100, 200);
        d.setLevel(100);
        return d;
         */
 //       ScaleDrawable scaledImg = new ScaleDrawable(symbol_innpakket, 0, 200, 200);

//        return scaledImg.getDrawable();
/*
        Drawable drawable = getResources().getDrawable(R.drawable.s_vit);
        drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth()*0.5),
                (int)(drawable.getIntrinsicHeight()*0.5));
        ScaleDrawable sd = new ScaleDrawable(drawable, 0, scaleWidth, scaleHeight);
         */
        //        Log.d(TAG, "symbolFargeStor: " + symbol_original.getIntrinsicWidth());
//        ScaleDrawable scaleDrawable = new ScaleDrawable(symbol_innpakket, Gravity.CENTER,200%,2);
 //       Log.d(TAG, "symbolFargeStor: " + scaleDrawable.getIntrinsicWidth());
 //       scaleDrawable.setLevel(1);
 //       return scaleDrawable;


    public Drawable bakgrunnFarge(int bakgrunn) {
        Drawable bakgrunn_original = AppCompatResources.getDrawable(context, bakgrunn);
        Drawable bakgrunn_innpakket = DrawableCompat.wrap(Objects.requireNonNull(bakgrunn_original));
        DrawableCompat.setTint(bakgrunn_innpakket, fargelys);

        return bakgrunn_innpakket;
    }

    // Slider thumbs og active slider trac
    public ColorStateList getFargeListe(){
        return new ColorStateList(
                new int[][]{ new int[]{},},
                new int[]{ getFarge()}
        );
    }

    // Inactive slider track
    public ColorStateList getFargeLysListe(){
        return new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_enabled},},
                new int[]{ getFargeLys()}
        );
    }

    // Toggle bakgrunn
    public ColorStateList getFargeToggleListe(){
        return new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_checked},{-android.R.attr.state_checked},{}},
                new int[]{ getFarge(),getFargeLys(),getHvit()}
        );
    }

    // Toggle tekst
    public ColorStateList getFargeToggleTekstListe(){
        return new ColorStateList(
                new int[][]{ new int[]{-android.R.attr.state_checked},{android.R.attr.state_checked},{}},
                new int[]{ getFarge(),getHvit(),getHvit()}
        );
    }

    // GPS thumb
    public ColorStateList getFargeGPSThumbListe(){
        return new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_checked},{}},
                new int[]{ getFarge(),getHvit()}
        );
    }

    // GPS thumb
    public ColorStateList getFargeGPSTrackListe(){
        return new ColorStateList(
                new int[][]{{}},
                new int[]{getFarge()}
        );
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFarge() {return farge;}

    public int getFargeLys() { return fargelys; }

    public int getFargeblass() {return fargeblass;}

    public int getBlank() { return ContextCompat.getColor(context, R.color.blank); }

    public int getHvit() { return ContextCompat.getColor(context, R.color.hvit); }

    public int getGra() { return ContextCompat.getColor(context, R.color.gra_lys); }

    public int getRod() { return ContextCompat.getColor(context, R.color.rod_klar); }

    public int getSvart() { return ContextCompat.getColor(context, R.color.svart); }

    public Drawable getGradient() {return gradient;}

    public Drawable getGradientBlank() {return gradient_blank;}

}
