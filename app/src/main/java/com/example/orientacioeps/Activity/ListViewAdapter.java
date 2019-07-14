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

public class ListViewAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<Espai> llistaEspais;
    private ArrayList<Espai> arraylist;

    public ListViewAdapter(Context context, List<Espai> espais) {
        // Declare Variables
        this.llistaEspais = espais;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(espais);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return llistaEspais.size();
    }

    @Override
    public Espai getItem(int position) {
        return llistaEspais.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

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

    // Filter Class
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
