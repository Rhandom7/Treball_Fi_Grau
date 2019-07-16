package com.example.orientacioeps.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.orientacioeps.Entity.Espai;
import com.example.orientacioeps.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/** @class ListViewAdapter
 * @brief Classe encarregada de gestionar la llista d'espais, tant per omplir-la com per filtrar-la
 * @author Genís Arumí Novellas
 */
public class ListViewAdapter extends BaseAdapter {

    private LayoutInflater inflater; ///< Utilitzat per omplir la llista
    private List<Espai> llistaEspais; ///< Llista d'espais
    private ArrayList<Espai> arraylist; ///< Llista d'espais auxiliar, principalment per l'hora de filtrar

    /**
     * Inicialitza les variables principals
     */
    public ListViewAdapter(Context context, List<Espai> espais) {
        this.llistaEspais = espais;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(espais);
    }

    /**
     * Crea una variable per identificar cada element de la llista, d'aquesta manera es pot utilitzar per assignar el valor de cada element de la llista
     */
    public class ViewHolder {
        TextView name;
    }

    /**
     * Retorna el número d'elements de la llista
     */
    @Override
    public int getCount() {
        return llistaEspais.size();
    }

    /**
     * Retorna un espai de la llista segons la posició indicada
     */
    @Override
    public Espai getItem(int position) {
        return llistaEspais.get(position);
    }

    /**
     * --
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * S'encarrega d'omplir la llista, a cada element de la llista li va assignant el nom d'un espai
     */
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.list_view_items, null);
            // Locate the TextViews in list_view_items.xml
            holder.name = view.findViewById(R.id.espai);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(llistaEspais.get(position).nom);
        return view;
    }

    /**
     * Filtra la llista d'espais segons l'string que ha escrit l'usuari
     */
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        llistaEspais.clear();
        if (charText.length() == 0) {
            llistaEspais.addAll(arraylist);
        } else {
            for (Espai e : arraylist) {
                if (e.nom.toLowerCase(Locale.getDefault()).contains(charText)) {
                    llistaEspais.add(e);
                }
            }
        }
        notifyDataSetChanged();
    }

}
