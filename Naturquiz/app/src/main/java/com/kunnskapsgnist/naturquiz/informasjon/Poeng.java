package com.kunnskapsgnist.naturquiz.informasjon;

// Regner ut poeng slik at det blir mest poeng i starten
public class Poeng {
//    private static final double a = -212.0/735.0;
//    private static final double b = 5416.0/735.0;
//    private static final double c = -7768.0/147.0;
    private static final double a = 0;
    private static final double b = 6.0/5.0;
    private static final double c = -18.0;
    private static final double d = 0.0;
    private static final double e = 1000.0;

    public Poeng() {
    }

    public int faktor(double t, int antall_spm){
        if (t < 0) t = 0; // Hvis du reagerer før satt reaksjonstiden
        if (t > 10) {
            return 200;
        } else if (antall_spm == 3){
            return (int) (a * t * t * t * t + b * t * t * t + c * t * t + d * t + e);
        } else {
            return (int) ((10.0*(a * t * t * t * t + b * t * t * t + c * t * t + d * t + e))/antall_spm);
        }
    }
}
