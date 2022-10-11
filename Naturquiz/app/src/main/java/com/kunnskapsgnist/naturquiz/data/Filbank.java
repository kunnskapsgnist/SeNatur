package com.kunnskapsgnist.naturquiz.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.kunnskapsgnist.naturquiz.modell.Fil;

import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Filbank {
    private static final String TAG = "Naturquiz";
    ArrayList<Fil> filListe = new ArrayList<>();
    String url;

    public Filbank() {}

    private String url() {
        url = "https://raw.githubusercontent.com/kunnskapsgnist/SeNatur/main/Quiz/oversikt.json";
        return url;
    }

    public void getFiler(final FilListeAsynkRespons callback){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url(),
                null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    Fil fil = new Fil(
                            new String(response.getJSONArray(i).getString(0)
                                    .getBytes(StandardCharsets.UTF_8),
                                    StandardCharsets.UTF_8),
                            response.getJSONArray(i).getInt(1),
                            response.getJSONArray(i).getInt(2),
                            response.getJSONArray(i).getInt(3),
                            response.getJSONArray(i).getInt(4),
                            response.getJSONArray(i).getInt(5),
                            response.getJSONArray(i).getInt(6)
                    );

                    // Legger arten til i listen
                    filListe.add(fil);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (null != callback) callback.processFinished(filListe);

        }, error -> Log.d(TAG, "getFiler: failed " + error));

        AppKontroll.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    public interface FilListeAsynkRespons {
        void processFinished(ArrayList<Fil> filListe);
    }
}
