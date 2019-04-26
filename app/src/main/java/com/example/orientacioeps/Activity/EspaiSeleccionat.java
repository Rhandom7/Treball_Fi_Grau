package com.example.orientacioeps.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.orientacioeps.R;

public class EspaiSeleccionat extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_prova);
        final String el = getIntent().getExtras().getString("Element");
        Log.d("Element seleccionat", el);
    }
}
