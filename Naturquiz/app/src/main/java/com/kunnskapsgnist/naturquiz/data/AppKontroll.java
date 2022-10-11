package com.kunnskapsgnist.naturquiz.data;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class AppKontroll extends Application {
    private static AppKontroll instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Application context;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        context = this;
    }

    // Returnerer context
    public static Context getContext() {
        return context.getApplicationContext();
    }

    // Hvis instance er brukt, lages en ny. Hvis ikke, returnres den eksisterende
    public static synchronized AppKontroll getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (imageLoader == null) {
            imageLoader = new ImageLoader(this.requestQueue,
                    new LruBitmapCache());
        }
        return this.imageLoader;
    }

}
