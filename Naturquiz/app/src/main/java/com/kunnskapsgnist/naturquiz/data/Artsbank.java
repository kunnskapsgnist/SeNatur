package com.kunnskapsgnist.naturquiz.data;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.kunnskapsgnist.naturquiz.modell.Art;

import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

public class Artsbank {
    private static final String TAG = "Naturquiz";
    ArrayList<Art> artListe = new ArrayList<>();
    String type, level, gruppe;
    String url;

    public Artsbank(String level, String type, String gruppe) {
        this.type = type;
        this.level = level;
        this.gruppe = gruppe
                .toLowerCase(Locale.ROOT)
                .replace("æ","a")
                .replace("ø","o")
                .replace("å","a")
                .replace("é","e")
                .replace(" ","_");
    }

    private String url() {
        url = "https://raw.githubusercontent.com/kunnskapsgnist/SeNatur/main/Quiz/" + gruppe + level + ".json";
        return url;
    }

    public void getArter(final ArtListeAsynkRespons callback){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url(),
                null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    Art art;
                    if (gruppe.equals("fotspor"))
                        art = new Art(
                                response.getJSONArray(i).getInt(0),
                                new String(response.getJSONArray(i).getString(1)
                                        .getBytes(StandardCharsets.UTF_8),
                                        StandardCharsets.UTF_8),
                                new String(response.getJSONArray(i).getString(2)
                                        .getBytes(StandardCharsets.UTF_8),
                                        StandardCharsets.UTF_8),
                                new String(response.getJSONArray(i).getString(3)
                                        .getBytes(StandardCharsets.UTF_8),
                                        StandardCharsets.UTF_8),
                                new String(response.getJSONArray(i).getString(4)
                                        .getBytes(StandardCharsets.UTF_8),
                                        StandardCharsets.UTF_8)
                        );
                    else
                        art = new Art(
                                response.getJSONArray(i).getInt(0),
                                new String(response.getJSONArray(i).getString(1)
                                        .getBytes(StandardCharsets.UTF_8),
                                        StandardCharsets.UTF_8),
                                new String(response.getJSONArray(i).getString(2)
                                        .getBytes(StandardCharsets.UTF_8),
                                        StandardCharsets.UTF_8),
                                response.getJSONArray(i).getJSONArray(3).getDouble(0),
                                response.getJSONArray(i).getJSONArray(3).getDouble(1),
                                response.getJSONArray(i).getJSONArray(4).getDouble(0),
                                response.getJSONArray(i).getJSONArray(4).getDouble(1)
                        );

                    // Legger arten til i listen
                    artListe.add(art);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (null != callback) callback.processFinished(artListe);

        }, error -> {});

        AppKontroll.getInstance().addToRequestQueue(jsonArrayRequest);

    }

    public interface ArtListeAsynkRespons {
        void processFinished(ArrayList<Art> artListe);
    }
}
