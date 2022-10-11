package com.kunnskapsgnist.naturquiz.modell;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kunnskapsgnist.naturquiz.R;
import com.kunnskapsgnist.naturquiz.informasjon.Farge;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PoengAdapter extends BaseAdapter {
    private static final String TAG = "Naturquiz";

    private final Context mContext;
    private LayoutInflater inflater;
    private final List<PoengHistorie> poengHistorieListe;
    private final int versjon;

    Farge farge;

    public PoengAdapter(Context context, List<PoengHistorie> poengHistorieListe, String type, int versjon) {
        this.mContext = context;
        this.poengHistorieListe = poengHistorieListe;
        this.versjon = versjon;

        farge = new Farge(context,type);
    }

    @Override
    public int getCount() {
        return poengHistorieListe.size();
    }

    @Override
    public Object getItem(int location) {
        return poengHistorieListe.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View scoreView, ViewGroup parent) {
        HistorieView holder;
        if (inflater == null) {
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (scoreView == null) {
            scoreView = inflater.inflate(R.layout.meny_historie_poeng, parent, false);
            holder = new HistorieView();
            holder.poeng = scoreView.findViewById(R.id.historie_poeng);
            holder.spiller = scoreView.findViewById(R.id.historie_spiller);
            holder.gruppe = scoreView.findViewById(R.id.historie_gruppe);
            holder.kategori = scoreView.findViewById(R.id.historie_kategori);
            holder.level = scoreView.findViewById(R.id.historie_level);
            holder.dato = scoreView.findViewById(R.id.historie_dato);

            scoreView.setTag(holder);

        } else {
            holder = (HistorieView) scoreView.getTag();
        }

        final PoengHistorie m = poengHistorieListe.get(position);
        holder.gruppe.setText(String.format("%s%s", m.getGruppe().substring(0, 1).toUpperCase(), m.getGruppe().substring(1)));
        holder.spiller.setText(m.getSpiller());
        holder.poeng.setText(m.getPoeng());

        LocalDateTime dato = LocalDateTime.parse(m.getDato(), DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        holder.dato.setText(dato.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

        holder.gruppe.setTextColor(farge.getFarge());
        holder.dato.setTextColor(farge.getFarge());
        holder.spiller.setTextColor(farge.getFarge());
        holder.poeng.setTextColor(farge.getFarge());

        switch (m.getKategori()){
            case "bilder":
                holder.kategori.setImageDrawable(farge.symbolFarge(R.drawable.symbol_bilde));
                break;
            case "lyd":
                holder.kategori.setImageDrawable(farge.symbolFarge(R.drawable.symbol_lyd));
                break;
            case "lengde":
            case "vingespenn":
                holder.kategori.setImageDrawable(farge.symbolFarge(R.drawable.symbol_lengde));
                break;
            case "vekt":
                holder.kategori.setImageDrawable(farge.symbolFarge(R.drawable.symbol_vekt));
                break;
        }

        switch (m.getLevel()){
            case "Demo":
                holder.level.setImageDrawable(farge.symbolFarge(R.drawable.level_demo));
                break;
            case "Lett":
                holder.level.setImageDrawable(farge.symbolFarge(R.drawable.level_lett));
                break;
            case "Medium":
                holder.level.setImageDrawable(farge.symbolFarge(R.drawable.level_medium));
                break;
            case "Vanskelig":
                holder.level.setImageDrawable(farge.symbolFarge(R.drawable.level_vanskelig));
                break;
            case "Ekspert":
                holder.level.setImageDrawable(farge.symbolFarge(R.drawable.level_ekspert));
                break;
        }

        switch (versjon){
            case 0: // Fjerner navn (historie)
                holder.spiller.setVisibility(View.GONE);
                break;
            case 1: // Fjerner navn, spill og level (resultat, personlig highscore)
                holder.spiller.setVisibility(View.GONE);
                holder.gruppe.setVisibility(View.GONE);
                holder.kategori.setVisibility(View.GONE);
                holder.level.setVisibility(View.GONE);
                break;
            case 2: // Fjerner spill, level og dato (gruppe highscore)
                holder.gruppe.setVisibility(View.GONE);
                holder.kategori.setVisibility(View.GONE);
                holder.level.setVisibility(View.GONE);
                holder.dato.setVisibility(View.GONE);
                break;
        }

        return scoreView;
    }

    static class HistorieView {
        TextView spiller;
        ImageView level;
        TextView dato;
        TextView poeng;
        ImageView kategori;
        TextView gruppe;
    }
}
