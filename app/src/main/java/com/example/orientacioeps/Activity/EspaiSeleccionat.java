package com.example.orientacioeps.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
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


/** @class EspaiSeleccionat
 * @brief Activity encarregada de gestionar el funcionament principal de l'aplicació, obtenint totes les dades del servidor i indicant les direccions a seguir per l'usuari
 * @author Genís Arumí Novellas
 */
public class EspaiSeleccionat extends AppCompatActivity {

    TodoApi mTodoService; ///< Encarregat de fer crides al servidor
    List<Beacon> beacons = new ArrayList<>(); ///< Llista de beacons obtinguts del servidor
    List<Cami> camins = new ArrayList<>(); ///< Llista de camins obtinguts del servidor

    private Context context; ///< Guarda el context d'aquesta activity
    private EstimoteCloudCredentials cloudCredentials; ///< Guarda les credencials de l'aplicació que permeten treballar amb els beacons
    private ProximityObserver.Handler proximityObserverHandler; ///< Ajuda a gestionar el comportament del servei utilitzat per detectar beacons
    private String nomEspaiSeleccionat = ""; ///< Nom de l'espai que ha seleccionat l'usuari a l'activity inicial
    private int idEspaiSeleccionat = 0; ///< Id de l'espai que ha seleccionat l'usuari a l'activity inicial
    private boolean seguintCami = false; ///< Indica si l'usuari ja està seguint un camí
    private List<Indicacio> cami = new ArrayList<>(); ///< Guarda el camí que segueix l'usuari en un moment donat

    TextView missatge; ///< Utilitzada per escriure per pantalla les indicacions a seguir per l'usuari

    private boolean mostrarAlerta = true; ///< Serveix per controlar si l'usuari vol que es mostri l'alerta d'informació


    /**
     * Crea la pantalla principal de l'aplicació, inicialitza les variables principals i obté totes les dades restants del servidor: beacons i camins
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.espai_seleccionat);
        missatge = findViewById(R.id.missatge);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();

        nomEspaiSeleccionat = getIntent().getExtras().getString("NomEspaiSeleccionat");
        idEspaiSeleccionat = getIntent().getExtras().getInt("IdEspaiSeleccionat");
        TextView text = findViewById(R.id.espaiSelec);
        text.setText(nomEspaiSeleccionat);

        obtenirBeacons();
        obtenirCamins();

        SharedPreferences preferences = getSharedPreferences("PREFS", 0);
        mostrarAlerta = preferences.getBoolean("mostrarAlerta", true);

        if(mostrarAlerta) dialogPosicioUsuari();
        else startProximityContentManager();
    }

    /**
     * Inicialitza l'encarregat de gestionar el servei que s'utilitzarà per detectar beacons i es guarda les credencials de l'aplicació per poder treballar amb els beacons
     */
    private void startProximityContentManager() {
        this.context = this;
        this.cloudCredentials = ((TodoApp) getApplication()).cloudCredentials;
        start();
    }

    /**
     * Para el servei encarregat de detectar beacons i destrueix l'activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this != null) stop();
    }

    /**
     * Inicia el comportament principal de l'aplicació que permet detectar beacons i obtenir-ne les seves dades. Mostra les indicacions a seguir per l'usuari
     * segons el beacon detectat i la destinació final escollida anteriorment per l'usuari.
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
            .forTag("orientacioEPS")
            .inCustomRange(5.0)
            .onContextChange(new Function1<Set<? extends ProximityZoneContext>, Unit>() {
                @Override
                public Unit invoke(Set<? extends ProximityZoneContext> contexts) {

                    for (ProximityZoneContext proximityContext : contexts) {
                        String beaconActual = proximityContext.getDeviceId();

                        if (!seguintCami) {
                            cami = obtenirCami(numBeacon(proximityContext.getDeviceId()), idEspaiSeleccionat);
                        }

                        if (numBeacon(beaconActual) == obtenirUltimBeacon(cami)) {
                            missatge.setText(R.string.final_cami);
                            missatge.setTextColor(Color.rgb(0, 180, 59));
                            missatge.setTypeface(null, Typeface.BOLD);
                            missatge.setGravity(Gravity.CENTER);
                        } else mostrarIndicacions(numBeacon(beaconActual), cami, missatge);
                    }
                    return null;
                }
            })
            .build();
        proximityObserverHandler = proximityObserver.startObserving(zone);
    }

    /**
     * Para el servei encarregat de gestionar beacons
     */
    public void stop() {
        if(proximityObserverHandler != null) proximityObserverHandler.stop();
    }

    /**
     * Obté la llista de beacons del servidor i els guarda
     */
    private void obtenirBeacons(){
        Call<List<Beacon>> call = mTodoService.getBeacons();

        call.enqueue(new Callback<List<Beacon>>() {
            @Override
            public void onResponse(Call<List<Beacon>> call, Response<List<Beacon>> response) {
                if (response.isSuccessful()) {
                    beacons.addAll(response.body() != null ? response.body() : beacons);
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
     * Obté la llista de camins del servidor i els guarda
     */
    private void obtenirCamins(){
        Call<List<Cami>> call = mTodoService.getCamins();

        call.enqueue(new Callback<List<Cami>>() {
            @Override
            public void onResponse(Call<List<Cami>> call, Response<List<Cami>> response) {
                if (response.isSuccessful()) {
                    camins.addAll(response.body() != null ? response.body() : camins);
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
     * Mostra per pantalla les indicacions que ha de seguir l'usuari per arribar a la destinació escollida
     */
    private void mostrarIndicacions(int idBeaconActual, List<Indicacio> cami, TextView missatge) {
        int i = 0;
        boolean trobat = false;
        while(i < cami.size() && !trobat){
            if(cami.get(i).origen == idBeaconActual){
                missatge.setText(cami.get(i).missatge);
                missatge.setTextColor(Color.rgb(0, 0, 0));
                trobat = true;
            }
            i++;
        }
    }

    /**
     * Obté l'últim beacon del camí especificat
     */
    private int obtenirUltimBeacon(List<Indicacio> cami){
        int numBeacon;
        numBeacon = cami.get(cami.size()-1).desti;
        return numBeacon;
    }

    /**
     * Obté la id interna del beacon al servidor segons la seva id obtiguda amb l'escaneig
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
     * Obté el camí que ha de seguir l'usuari segons el beacon on es troba i la destinació que ha escollit
     */
    private List<Indicacio> obtenirCami(int idBeaconActual, int destinacio){
        int idDestinacio;
        int i = 0, j = 0, midaCami;
        boolean trobat = false;
        List<Indicacio> cami = new ArrayList<>();

        idDestinacio = obtenirIdBeaconSegonsDestinacio(destinacio);

        while (i < camins.size() && !trobat){
            midaCami = camins.get(i).indicacions.size();

            while(j < midaCami && !trobat){
                if(camins.get(i).indicacions.get(j).origen == idBeaconActual && camins.get(i).indicacions.get(midaCami-1).desti == idDestinacio){
                    cami = camins.get(i).indicacions;
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
     * Obté la id del beacon al servidor segons la destinació (espai) especificada
     */
    private int obtenirIdBeaconSegonsDestinacio(int destinacio){
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
     * Mostra per pantalla un missatge que explica a l'usuari com ha d'utilitzar l'aplicació i col·locar-se per tal de poder seguir correctament les indicacions
     */
    private void dialogPosicioUsuari(){
        new AlertDialog.Builder(this)
            .setTitle(R.string.atencio)
            .setMessage(R.string.missatge_alerta)
            .setCancelable(false)
            .setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    startProximityContentManager();
                }
            }).setNeutralButton("Never show again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                SharedPreferences preferences = getSharedPreferences("PREFS", 0);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("mostrarAlerta", false);
                editor.apply();
            }
            })
            .create()
            .show();
    }
}
