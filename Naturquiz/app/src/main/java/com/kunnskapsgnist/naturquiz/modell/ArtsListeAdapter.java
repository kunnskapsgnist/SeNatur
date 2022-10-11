package com.kunnskapsgnist.naturquiz.modell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.kunnskapsgnist.naturquiz.R;
import com.kunnskapsgnist.naturquiz.data.AppKontroll;

import java.util.List;

public class ArtsListeAdapter extends RecyclerView.Adapter<ArtsListeAdapter.ViewHolder> {
    private final Context context;
    private List<Art> artsListe;
    private final OnArtKlikkListener onArtKlikkListener;

    public ArtsListeAdapter(Context context, List<Art> artsListe, OnArtKlikkListener onArtKlikkListener) {
        this.context = context;
        this.artsListe = artsListe;
        this.onArtKlikkListener = onArtKlikkListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meny_artene_kort, parent, false);
        return new ViewHolder(view, onArtKlikkListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Art art = artsListe.get(position);
        holder.view_bilde.setImageUrl(art.getBilde_url(), AppKontroll.getInstance().getImageLoader());
        holder.view_navn.setText(art.getArt());

        if (art.getVisInfo())
            holder.view_info.setVisibility(View.VISIBLE);
        else
            holder.view_info.setVisibility(View.GONE);
    }

    private void visSkjul(ViewHolder holder) {
        if (holder.view_info.getVisibility() == View.GONE)
            holder.view_info.setVisibility(View.VISIBLE);
        else holder.view_info.setVisibility(View.GONE);
    }


    @Override
    public int getItemCount() {
        if (artsListe == null) {
            return 0;
        }
        return artsListe.size();
    }

    public void oppdaterListe(List<Art> artsListe) {
        this.artsListe = artsListe;
        notifyItemRangeChanged(0, artsListe.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView view_navn;
        public NetworkImageView view_bilde;
        public LinearLayout view_info;
        OnArtKlikkListener onArtKlikkListener;

        public ViewHolder(@NonNull View itemView, OnArtKlikkListener onArtKlikkListener) {
            super(itemView);
            view_bilde = itemView.findViewById(R.id.artene_kort_bilde);
            view_navn = itemView.findViewById(R.id.artene_kort_navn);
            view_info = itemView.findViewById(R.id.artene_kort_info);

            this.onArtKlikkListener = onArtKlikkListener;
        }

        @Override
        public void onClick(View view) {
            String klikket;
            if (view_bilde.equals(view)) {
                klikket = "bilde";
            } else if (view_navn.equals(view)) {
                    klikket = "info";
            } else {
                klikket = "info";
            }
            onArtKlikkListener.onArtKlikk(getAdapterPosition(), klikket);
        }
    }

    public interface OnArtKlikkListener {
        void onArtKlikk(int position, String klikket);
    }

}

