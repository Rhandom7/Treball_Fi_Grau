package com.example.orientacioeps.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import org.w3c.dom.Text;

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
    private String el = "";
    private boolean seguintCami = false;
    private List<Indicacio> cami = new ArrayList<>();

    TextView missatge;
    private boolean primeraVegada = true;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.espai_seleccionat);
        missatge = findViewById(R.id.missatge);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();

        el = getIntent().getExtras().getString("EspaiSeleccionat");
        TextView text = findViewById(R.id.espaiSelec);
        text.setText(el);

        obtenirBeacons();
        obtenirCamins();
        //obtenirIndicacions();

        if(primeraVegada){
            //Todo: Posar una alerta que només es pugui tancar confirmant, avisant que per poder-se orientar correctament cada vegada que canvii el missatge l'usuari
            //Todo: ha d'estar davant de la indicació on digui que hi ha un beacon, per tenir l'orientació correcte.
            dialogPosicioUsuari();
            primeraVegada = false;
        }
        //startProximityContentManager();
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
                            /*if (title == null) {
                                title = "unknown";
                            }*/

                            //Todo: Posar un if per controlar que el beacon actual sigui diferent a l'anterior? I així hi ha coses que no es van fent cada vegada

                            Log.d("Aprop", title);
                            String beaconActual = proximityContext.getDeviceId();
                            Log.d("Aprop", beaconActual);
                            TextView text = findViewById(R.id.llocActual);
                            text.setText(title);
                            Log.d("Aprop", "Num de beacon: " + numBeacon(beaconActual));

                            //FET (tot i que no és necessari, quan s'arriba al final l'usuari tirarà enrrere per buscar un altre lloc o tancarà l'app
                            //Todo: Falta fer un mètode que comprovi si estic al final d'un camí per posar la variable seguintCami a fals.

                            //FET
                            //Todo: També comprovar si el beacon on estic és l'últim beacon del camí per poder indicar a l'usuari que ha arribat al lloc que buscava

                            //FET
                            //Todo: POSAR PANTALLA INDICANT QUE S'HA D'ESTAR DE CARA AL BEACON PER VEURE LA DIRECCIÓ CAP ON S'HA D'ANAR

                            //Todo: També posar les frases/text en anglès separant-ho amb una barra o algo per l'estil

                            //Todo: Provar-ho per les escales de casa a veure com funciona
                            //Todo: Canviar l'estil de la pantalla, endreçar-ho una mica i fer-ho més entenedor

                            //Todo: DOCUMENTAR EL CODI!!

                            if(!seguintCami) cami = obtenirCamiNou(numBeacon(proximityContext.getDeviceId()), el);
                            Log.d("Aprop", "El cami te mida: " + cami.size());

                            if(numBeacon(beaconActual) == obtenirUltimBeacon(cami)){
                                missatge.setText("HAS ARRIBAT, L'ESPAI QUE BUSQUES ESTÀ PER LA ZONA");
                                //Todo: Enlloc de canviar el missatge fer que salti una alerta perquè sigui més visible?
                            }
                            else mostrarIndicacions(numBeacon(beaconActual), cami, missatge);
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


    /*private void beaconsObtinguts(){
        Log.d("Dades", "BEACONS");
        for (int i = 0; i < beacons.size(); i++) {
            Log.d("Dades", beacons.get(i).codi);
        }
        Log.d("Dades", "-------------------------------");
    }*/

    /*private void caminsObtinguts(){
        Log.d("Dades", "CAMINS");
        for(Cami c : camins){
            Log.d("Dades", "Cami " + c.id);
            for(int i : c.cami){
                Log.d("Dades", Integer.toString(i));
            }
            Log.d("Dades", "=====================================");
        }
        Log.d("Dades", "-------------------------------");
    }*/

    private void obtenirBeacons(){

        Call<List<Beacon>> call = mTodoService.getBeacons();

        call.enqueue(new Callback<List<Beacon>>() {
            @Override
            public void onResponse(Call<List<Beacon>> call, Response<List<Beacon>> response) {
                if (response.isSuccessful()) {
                    beacons.addAll(response.body() != null ? response.body() : beacons);

                    //beaconsObtinguts();
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

                    //caminsObtinguts();
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

    private void mostrarIndicacions(int numBeacon, List<Indicacio> cami, TextView missatge) {
        int i = 0;
        boolean trobat = false;
        while(i < cami.size() && !trobat){
            if(cami.get(i).origen == numBeacon){
                missatge.setText(cami.get(i).missatge);
                Log.d("Aprop", "Cami a seguir: " + cami.get(i).missatge);
                trobat = true;
            }
            i++;
        }
    }

    private int obtenirUltimBeacon(List<Indicacio> cami){
        int numBeacon;
        numBeacon = cami.get(cami.size()-1).desti;
        return numBeacon;
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

    private List<Indicacio> obtenirCamiNou(int idBeaconActual, String destinacio){
        int idDestinacio = 0;
        List<Indicacio> cami = new ArrayList<>();

        idDestinacio = obtenirIdBeaconPerDestinacio(destinacio);
        int i = 0, mida = 0;
        boolean trobat = false;
        while (i < camins.size() && !trobat){
            mida = camins.get(i).cami.size();
            //Todo: Canviar-ho per contemplar el cas que estigui aprop d'un beacon del mig del camí, ara mateix només miro el cas que estigui aprop del primer beacon d'un camí
            //Todo: IMPORTANT FER AQUEST CANVI, CAL RECÓRRER EL CAMÍ QUE ESTIC MIRANT PER VEURE SI ESTIC EN UN BEACON DEL MIG DEL CAMÍ
            if(camins.get(i).cami.get(0).origen == idBeaconActual && camins.get(i).cami.get(mida-1).desti == idDestinacio){
                cami = camins.get(i).cami;
                trobat = true;
                seguintCami = true;
            }
            i++;
        }
        return cami;
    }

    private int obtenirIdBeaconPerDestinacio(String destinacio){
        int id = 0, i = 0;
        boolean trobat = false;
        while(i < beacons.size() && !trobat){
            if(beacons.get(i).espais.contains(destinacio)){
                id = beacons.get(i).id;
                trobat = true;
            }
            i++;
        }
        return id;
    }

    private void dialogPosicioUsuari(){
        new AlertDialog.Builder(this)
            .setTitle(R.string.atencio)
            .setMessage(R.string.dialog_Posicio_Usuari)
            .setCancelable(false)
            .setPositiveButton(R.string.dialog_accept, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Prompt the user once explanation has been shown
                    startProximityContentManager();
                }
            })
            .create()
            .show();
    }


}
