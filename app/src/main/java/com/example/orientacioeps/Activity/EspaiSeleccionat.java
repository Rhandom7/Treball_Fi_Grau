package com.example.orientacioeps.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** @file Alien.java
 * @brief Classe Alien
 */

/** @class Alien
 * @brief Tipus de personatge que té com a objectiu eliminar humans i fugir de trolls. Només pot recollir claus.
 * @author Genís Arumí Novellas
 */
public class EspaiSeleccionat extends AppCompatActivity {

    TodoApi mTodoService; ///<
    List<Beacon> beacons = new ArrayList<>(); ///<
    List<Cami> camins = new ArrayList<>(); ///<

    private Context context; ///<
    private EstimoteCloudCredentials cloudCredentials; ///<
    private ProximityObserver.Handler proximityObserverHandler; ///<
    private String espaiSeleccionat = ""; ///<
    private boolean seguintCami = false; ///<
    private List<Indicacio> cami = new ArrayList<>(); ///<

    TextView missatge; ///<
    private boolean primeraVegada = true; ///<


    /**
     * @brief
     * @pre
     * @post
     * @param
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.espai_seleccionat);
        missatge = findViewById(R.id.missatge);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();

        espaiSeleccionat = getIntent().getExtras().getString("EspaiSeleccionat");
        TextView text = findViewById(R.id.espaiSelec);
        text.setText(espaiSeleccionat);
        text.setTextColor(Color.rgb(0, 0, 0));

        obtenirBeacons();
        obtenirCamins();

        if(primeraVegada){
            //Todo: Posar una alerta que només es pugui tancar confirmant, avisant que per poder-se orientar correctament cada vegada que canviï el missatge l'usuari
            //Todo: ha d'estar davant de la indicació on digui que hi ha un beacon, per tenir l'orientació correcte.

            //Todo: POSAR BOTÓ DE NEVER SHOW AGAIN?
            dialogPosicioUsuari();
            primeraVegada = false;
        }
        //startProximityContentManager();
    }

    /**
     * @brief
     * @pre
     * @post
     * @param
     */
    private void startProximityContentManager() {
        this.context = this;
        this.cloudCredentials = ((TodoApp) getApplication()).cloudCredentials;
        start();
    }

    /**
     * @brief
     * @pre
     * @post
     * @param
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this != null) stop();
    }

    /**
     * @brief
     * @pre
     * @post
     * @param
     */
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

                            Log.d("Aprop", "Beacon: " + title);
                            String beaconActual = proximityContext.getDeviceId();
                            Log.d("Aprop", "Id beacon " + beaconActual);
                            //TextView text = findViewById(R.id.llocActual);
                            //text.setText(title);
                            Log.d("Aprop", "Num de beacon: " + numBeacon(beaconActual));

                            //FET (tot i que no és necessari, quan s'arriba al final l'usuari tirarà enrrere per buscar un altre lloc o tancarà l'app
                            //Todo: Falta fer un mètode que comprovi si estic al final d'un camí per posar la variable seguintCami a fals.

                            //FET
                            //Todo: També comprovar si el beacon on estic és l'últim beacon del camí per poder indicar a l'usuari que ha arribat al lloc que buscava

                            //FET
                            //Todo: POSAR PANTALLA INDICANT QUE S'HA D'ESTAR DE CARA AL BEACON PER VEURE LA DIRECCIÓ CAP ON S'HA D'ANAR

                            //FET (tornar a repassar?)
                            //Todo: Canviar l'estil de la pantalla, endreçar-ho una mica i fer-ho més entenedor

                            //A mitges
                            //Todo: També posar les frases/text en anglès separant-ho amb una barra o algo per l'estil

                            //No crec que sigui necessari
                            //Todo: Provar-ho per les escales de casa a veure com funciona


                            //Todo: Aclarir el missatge que indica als usuaris que s'han de posar de cara a la senyal per obtenir les direccions correctes


                            //Todo: DOCUMENTAR EL CODI!!

                            if(!seguintCami) {
                                cami = obtenirCamiNou(numBeacon(proximityContext.getDeviceId()), espaiSeleccionat);
                                Log.d("Aprop", "El cami te mida: " + cami.size());
                            }

                            if(numBeacon(beaconActual) == obtenirUltimBeacon(cami)){
                                missatge.setText("HAS ARRIBAT, L'ESPAI QUE BUSQUES ESTÀ PER LA ZONA!");
                                missatge.setTextColor(Color.rgb(0,180,59));
                            }
                            else mostrarIndicacions(numBeacon(beaconActual), cami, missatge);
                        }
                        return null;
                    }
                })
                .build();
        proximityObserverHandler = proximityObserver.startObserving(zone);
    }

    /**
     * @brief
     * @pre
     * @post
     * @param
     */
    public void stop() {
        proximityObserverHandler.stop();
    }

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

    /**
     * @brief
     * @pre
     * @post
     * @param
     */
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

    /**
     * @brief
     * @pre
     * @post
     * @param
     */
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

    /**
     * @brief
     * @pre
     * @post
     * @param
     */
    private void mostrarIndicacions(int numBeacon, List<Indicacio> cami, TextView missatge) {
        int i = 0;
        boolean trobat = false;
        while(i < cami.size() && !trobat){
            if(cami.get(i).origen == numBeacon){
                missatge.setText(cami.get(i).missatge);
                missatge.setTextColor(Color.rgb(0, 0, 0));
                Log.d("Aprop", "Cami a seguir: " + cami.get(i).missatge);
                trobat = true;
            }
            i++;
        }
    }

    /**
     * @brief
     * @pre
     * @post
     * @param
     */
    private int obtenirUltimBeacon(List<Indicacio> cami){
        int numBeacon;
        numBeacon = cami.get(cami.size()-1).desti;
        return numBeacon;
    }

    /**
     * @brief
     * @pre
     * @post
     * @param
     */
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

    /**
     * @brief
     * @pre
     * @post
     * @param
     */
    private List<Indicacio> obtenirCamiNou(int idBeaconActual, String destinacio){
        int idDestinacio = 0;
        int i = 0, j = 0, midaCami = 0;
        boolean trobat = false;
        List<Indicacio> cami = new ArrayList<>();

        idDestinacio = obtenirIdBeaconPerDestinacio(destinacio);

        while (i < camins.size() && !trobat){
            midaCami = camins.get(i).cami.size();

            while(j < midaCami && !trobat){
                if(camins.get(i).cami.get(j).origen == idBeaconActual && camins.get(i).cami.get(midaCami-1).desti == idDestinacio){
                    cami = camins.get(i).cami;
                    trobat = true;
                    seguintCami = true;
                }
                j++;
            }
            i++;
        }
        return cami;
    }

    /**
     * @brief
     * @pre
     * @post
     * @param
     */
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

    /**
     * @brief
     * @pre
     * @post
     * @param
     */
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
