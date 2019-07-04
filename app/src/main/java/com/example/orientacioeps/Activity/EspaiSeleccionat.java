package com.example.orientacioeps.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.estimote.proximity_sdk.api.ProximityObserver;
import com.estimote.proximity_sdk.api.ProximityObserverBuilder;
import com.estimote.proximity_sdk.api.ProximityZone;
import com.estimote.proximity_sdk.api.ProximityZoneBuilder;
import com.estimote.proximity_sdk.api.ProximityZoneContext;
import com.example.orientacioeps.Entity.Beacon;
import com.example.orientacioeps.Entity.Cami;
import com.example.orientacioeps.Entity.Indicacio;
import com.example.orientacioeps.R;
import com.example.orientacioeps.TodoApp;
import com.example.orientacioeps.rest.TodoApi;

//import com.example.orientacioeps.estimote.ProximityContentManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EspaiSeleccionat extends AppCompatActivity {

    TodoApi mTodoService;
    List<Beacon> beacons = new ArrayList<>();
    List<Cami> camins = new ArrayList<>();
    List<Indicacio> indicacions = new ArrayList<>();

    //private ProximityContentManager proximityContentManager;

    private Context context;
    private EstimoteCloudCredentials cloudCredentials;
    private ProximityObserver.Handler proximityObserverHandler;



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

        startProximityContentManager();
    }

    private void startProximityContentManager() {
        //proximityContentManager = new ProximityContentManager(this, ((TodoApp) getApplication()).cloudCredentials);
        this.context = this;
        this.cloudCredentials = ((TodoApp) getApplication()).cloudCredentials;
        start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this != null) stop();
    }

    public void start() {

        ProximityObserver proximityObserver = new ProximityObserverBuilder(context, cloudCredentials)
                .onError(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        Log.e("app", "proximity observer error: " + throwable);
                        return null;
                    }
                })
                .withBalancedPowerMode()
                .build();

        ProximityZone zone = new ProximityZoneBuilder()
                .forTag("orientacioeps-1eh")
                .inCustomRange(5.0)
                .onContextChange(new Function1<Set<? extends ProximityZoneContext>, Unit>() {
                    @Override
                    public Unit invoke(Set<? extends ProximityZoneContext> contexts) {

                        for (ProximityZoneContext proximityContext : contexts) {
                            String title = proximityContext.getAttachments().get("orientacioeps-1eh/title");
                            if (title == null) {
                                title = "unknown";
                            }

                            //Toast toast = Toast.makeText(context, "Aprop de " + title, Toast.LENGTH_SHORT);
                            //toast.show();
                            Log.d("Aprop", title);
                            Log.d("Aprop", proximityContext.getDeviceId());
                            TextView text = findViewById(R.id.llocActual);
                            text.setText(title);
                            Log.d("Aprop", "Num de beacon: " + numBeacon(proximityContext.getDeviceId()));
                        }
                        return null;
                    }
                })
                .build();

        proximityObserverHandler = proximityObserver.startObserving(zone);
    }

    public void stop() {
        proximityObserverHandler.stop();
    }







    ////////////////////////////////////////////////////////////////////////////////////////////


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

    private int numBeacon(String idBeacon){
        int num = 0, i = 0;
        boolean trobat = false;
        while(i < beacons.size() && !trobat){
            if(beacons.get(i).codi.equals(idBeacon)) {
                num = beacons.get(i).id;
                trobat = true;
            }
            i++;
        }
        return num;
    }

    private boolean existeixCami(int idBeacon, String destinacio){
        int i = 0;
        boolean trobat = false;
        while(i < camins.size() && !trobat){
            //comprovar si el camí té la id de beacon i destinació final?
            //en els camins del servidor només hi tinc posades ids de beacons, per tant, quan tinc l'string del lloc on vol anar l'usuari
            //he de buscar a la llista de beacons a veure quin beacon té l'string destinació i comprovar si aquest beacon és l'últim d'algun camí
        }
        return trobat;
    }
}
