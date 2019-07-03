package com.example.orientacioeps.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orientacioeps.Entity.Beacon;
import com.example.orientacioeps.Entity.Cami;
import com.example.orientacioeps.Entity.Indicacio;
import com.example.orientacioeps.R;
import com.example.orientacioeps.TodoApp;
import com.example.orientacioeps.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EspaiSeleccionat extends AppCompatActivity {

    TodoApi mTodoService;
    List<Beacon> beacons = new ArrayList<>();
    List<Cami> camins = new ArrayList<>();
    List<Indicacio> indicacions = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.espai_seleccionat);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();

        final String el = getIntent().getExtras().getString("EspaiSeleccionat");
        TextView text = findViewById(R.id.espaiSelec);
        text.setText(el);

        obtenirBeacons();
        obtenirCamins();
        obtenirIndicacions();
    }

    private void beaconsObtinguts(){
        Log.d("Dades", "BEACONS");
        for (int i = 0; i < beacons.size(); i++) {
            Log.d("Dades", beacons.get(i).codi);
        }
        Log.d("Dades", "-------------------------------");
    }

    private void caminsObtinguts(){
        Log.d("Dades", "CAMINS");
        for(Cami c : camins){
            Log.d("Dades", "Cami " + c.id);
            for(int i : c.cami){
                Log.d("Dades", Integer.toString(i));
            }
            Log.d("Dades", "=====================================");
        }
        Log.d("Dades", "-------------------------------");
    }

    private void indicacionsObtingudes(){
        Log.d("Dades", "INDICACIONS");
        for(Indicacio i : indicacions){
            Log.d("Dades", i.missatge);
        }
        Log.d("Dades", "-------------------------------");
    }

    private void obtenirBeacons(){

        Call<List<Beacon>> call = mTodoService.getBeacons();

        call.enqueue(new Callback<List<Beacon>>() {
            @Override
            public void onResponse(Call<List<Beacon>> call, Response<List<Beacon>> response) {
                if (response.isSuccessful()) {
                    beacons.addAll(response.body() != null ? response.body() : beacons);

                    beaconsObtinguts();
                }
                else {
                    Toast toast = Toast.makeText(EspaiSeleccionat.this, "Error intentant obtenir els beacons", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onFailure(Call<List<Beacon>> call, Throwable t) {
                Toast toast = Toast.makeText(EspaiSeleccionat.this, "Error intentant obtenir els beacons", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void obtenirCamins(){
        Call<List<Cami>> call = mTodoService.getCamins();

        call.enqueue(new Callback<List<Cami>>() {
            @Override
            public void onResponse(Call<List<Cami>> call, Response<List<Cami>> response) {
                if (response.isSuccessful()) {
                    camins.addAll(response.body() != null ? response.body() : camins);

                    caminsObtinguts();
                }
                else {
                    Toast toast = Toast.makeText(EspaiSeleccionat.this, "Error intentant obtenir els camins", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            @Override
            public void onFailure(Call<List<Cami>> call, Throwable t) {
                Toast toast = Toast.makeText(EspaiSeleccionat.this, "Error intentant obtenir els camins", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void obtenirIndicacions(){
        Call<List<Indicacio>> call = mTodoService.getIndicacions();

        call.enqueue(new Callback<List<Indicacio>>() {
            @Override
            public void onResponse(Call<List<Indicacio>> call, Response<List<Indicacio>> response) {
                if (response.isSuccessful()) {
                    indicacions.addAll(response.body() != null ? response.body() : indicacions);

                    indicacionsObtingudes();
                }
                else {
                    Toast toast = Toast.makeText(EspaiSeleccionat.this, "Error intentant obtenir les indicacions", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            @Override
            public void onFailure(Call<List<Indicacio>> call, Throwable t) {
                Toast toast = Toast.makeText(EspaiSeleccionat.this, "Error intentant obtenir les indicacions", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
}
