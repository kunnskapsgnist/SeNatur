package com.kunnskapsgnist.naturquiz.modell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kunnskapsgnist.naturquiz.R;
import com.kunnskapsgnist.naturquiz.informasjon.Farge;

import java.text.MessageFormat;
import java.util.List;

public class PrestasjonAdapter extends RecyclerView.Adapter<PrestasjonAdapter.ViewHolder> {
    private static final String TAG = "Naturquiz";

    private final Context context;
    private List<Prestasjon> prestasjonListe;

    Farge farge;

    public PrestasjonAdapter(Context context, List<Prestasjon> prestasjonListe) {
        this.context = context;
        this.prestasjonListe = prestasjonListe;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.enkeltprestasjoner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Prestasjon p = prestasjonListe.get(position);
        holder.view_level.setText(p.getLevel());
        holder.view_poeng.setText(MessageFormat.format("{0}/{1}", p.getLevelpoeng(), p.getNestelevelpoeng()));
        holder.view_poeng.setText(String.format(context.getString(R.string.min_rekord), p.getRekord()));

        farge = new Farge(context,p.getType());
        holder.view_kort.setCardBackgroundColor(farge.getFargeLys());
        holder.view_progress.setProgressDrawable(farge.getProgress());
        holder.view_progress.setProgress(p.getNestelevelpoeng());
        holder.view_poeng.setTextColor(farge.getFarge());

        // Level
        int id_fra = context.getResources().getIdentifier("level_"+p.getLevel().toLowerCase(), "drawable", context.getPackageName());
        int id_til = context.getResources().getIdentifier("level_"+p.getNestelevel().toLowerCase(), "drawable", context.getPackageName());
        holder.view_level.setCompoundDrawablesWithIntrinsicBounds(farge.symbolHvit(id_fra),null,farge.symbolHvit(id_til),null);

        // Symbol
        holder.view_symbol.setRotation(0);
        switch (p.getGruppe()) {
            case "Fugler":
                switch (p.getKategori()) {
                    case "bilder":
                        holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.bilde_fugl));
                        break;
                    case "vingespenn":
                        holder.view_symbol.setRotation(90);
                        holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.fugl_vingespenn));
                        break;
                    case "vekt":
                        holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.vekt_bla));
                        break;
                }
                break;
            case "Blomster":
                holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.bilde_blomst));
                break;
            case "Trær":
                holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.bilde_tre));
                break;
            case "Bregner":
                holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.bilde_bregne));
                break;
            case "Sopp":
                holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.bilde_sopp));
                break;
            case "Sommerfugler":
                if (p.getKategori().equals("bilder"))
                    holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.bilde_sommerfugl));
                else if (p.getKategori().equals("vingespenn"))
                    holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.sommerfugl_vingespenn));
                break;
            case "Edderkopper":
                holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.bilde_edderkopp));
                break;
            case "Insekter":
                holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.bilde_insekt));
                break;
            case "Dyr":
                switch (p.getKategori()) {
                    case "bilder":
                        holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.bilde_dyr));
                        break;
                    case "lengde":
                        holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.dyr_lengde));
                        break;
                    case "vekt":
                        holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.vekt_brun));
                        break;
                }
                break;
            case "Fotspor":
                holder.view_symbol.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.dyr_fotspor));
                break;
        }

    }

    @Override
    public int getItemCount() {
        if (prestasjonListe == null) {
            return 0;
        }
        return prestasjonListe.size();
    }

    public void oppdaterListe(List<Prestasjon> prestasjonListe) {
        this.prestasjonListe = prestasjonListe;
        notifyItemRangeChanged(0, prestasjonListe.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  {
        CardView view_kort;
        ImageView view_symbol;
        TextView view_level, view_poeng;
        ProgressBar view_progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view_kort = itemView.findViewById(R.id.prestasjon_kort);
            view_symbol = itemView.findViewById(R.id.prestasjon_symbol);
            view_level = itemView.findViewById(R.id.prestasjon_level);
            view_progress = itemView.findViewById(R.id.prestasjon_progress_bar);
            view_poeng = itemView.findViewById(R.id.prestasjon_poeng);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
