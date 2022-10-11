package com.kunnskapsgnist.naturquiz.informasjon;

import java.util.List;

public class Randomisere {
    private static final String TAG = "Naturquiz";
    public Randomisere() {}

    // Randomiserer en int[]
    public static void randomInt(int[] rekke){

        int n, n1, n2, r0;
        int L = rekke.length;
        for (n = 0; n<10; n+=1){
            n1 = (int) (L * Math.random());
            n2 = (int) (L * Math.random());
            r0 = rekke[n1];
            rekke[n1] = rekke[n2];
            rekke[n2] = r0;
        }
    }

    // Sorterer en int[]
    public static void sortInt(int[] rekke){
        for (int i = 0; i < rekke.length; i++)
        {
            for (int j = i + 1; j < rekke.length; j++)
            {
                int tmp;
                if (rekke[i] > rekke[j])
                {
                    tmp = rekke[i];
                    rekke[i] = rekke[j];
                    rekke[j] = tmp;
                }
            }
        }
    }

    // Sorterer en float[]
    public static int[] sortFloat(float[] rekke){
        float[] rekke0 = new float[4];
        int[] nr = new int[4];
        System.arraycopy(rekke, 0, rekke0, 0, rekke.length);
        for (int i = 0; i < rekke.length; i++) {
            for (int j = i + 1; j < rekke.length; j++) {
                float tmp;
                if (rekke[i] > rekke[j])
                {
                    tmp = rekke[i];
                    rekke[i] = rekke[j];
                    rekke[j] = tmp;
                }
            }
        }

        // nr gir hvordan elementene har byttet plass
        for (int n = 0; n < rekke.length; n++){
            for (int m = 0; m < rekke.length; m++) {
                if (rekke0[m] == rekke[n]) {
                    nr[m] = n;
                    break;
                }
            }
        }
        return nr;
    }

    // Sorterer en float[]
    public static int[] sortDouble(double[] rekke){
        double[] rekke0 = new double[4];
        int[] nr = new int[4];
        System.arraycopy(rekke, 0, rekke0, 0, rekke.length);
        for (int i = 0; i < rekke.length; i++) {
            for (int j = i + 1; j < rekke.length; j++) {
                double tmp;
                if (rekke[i] > rekke[j])
                {
                    tmp = rekke[i];
                    rekke[i] = rekke[j];
                    rekke[j] = tmp;
                }
            }
        }

        // nr gir hvordan elementene har byttet plass
        for (int n = 0; n < rekke.length; n++){
            for (int m = 0; m < rekke.length; m++) {
                if (rekke0[m] == rekke[n]) {
                    nr[m] = n;
                    break;
                }
            }
        }
        return nr;
    }

}
