package com.kunnskapsgnist.naturquiz.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.kunnskapsgnist.naturquiz.modell.Media;

import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Mediabank {
    private static final String TAG = "Naturquiz";
    ArrayList<Media> medieListe = new ArrayList<>();
    String type, familie, mappe;
    String url;

    public Mediabank(String type, String familie) {
        this.type = type;
        this.familie = familie;
        this.mappe = familie
                .replace("æ","a")
                .replace("ø","o")
                .replace("å","a")
                .replace(" ","_");
    }

    private String url() {
        url = "https://raw.githubusercontent.com/kunnskapsgnist/SeNatur/main/" + type + "/" + mappe + ".json";
        return url;
    }

    public void getMedier(final MedialisteAsynkRespons callback){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url(),
                null, response -> {

            for (int i = 0; i < response.length(); i++) {
                try {
                    // TODO: Jeg klarer fortsatt ikke å lese f.eks. ö
                    Media medie = new Media(
                            i,
                            Integer.parseInt(new String(response.getJSONArray(i).getString(0).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)),
                            type,
                            mappe,
                            new String(response.getJSONArray(i).getString(1).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8),
                            new String(response.getJSONArray(i).getString(2).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8),
                            new String(response.getJSONArray(i).getString(3).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8),
                            new String(response.getJSONArray(i).getString(4).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8),
                            new String(response.getJSONArray(i).getString(5).getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8)
                            );

                    // Adderer bilder eller lyd til listen:
                    medieListe.add(medie);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (null != callback) {
                callback.processFinished(medieListe);
            }

        }, error -> callback.processFinished(null)); // Returnerer medieListe == null dersom nettsiden ikke eksisterer

        AppKontroll.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    public interface MedialisteAsynkRespons {
        void processFinished(ArrayList<Media> mediaArrayList);
    }
}
