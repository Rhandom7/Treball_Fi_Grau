package com.example.orientacioeps.Activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.orientacioeps.Entity.Espai;
import com.example.orientacioeps.R;
import com.example.orientacioeps.TodoApp;
import com.example.orientacioeps.rest.TodoApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;


/** @class MainActivity
 * @brief Activity inicial que s'encarrega de mostrar el llistat d'espais a cercar i a filtrar i de demanar permisos a l'usuari per accedir i activar el Bluetooth i la Localitació.
 * @author Genís Arumí Novellas
 */
public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback {

    private TodoApi mTodoService; ///< Encarregat de fer crides al servidor
    private ListView list; ///< Controla la llista d'espais
    private ListViewAdapter adapter; ///< Adaptador de la llista d'espais
    private SearchView editSearch; ///< Controla la barra de filtrat
    private Button infoButton; ///< Botó per accedir a la pantalla d'informació de l'aplicació

    List<Espai> llistaEspais = new ArrayList<>(); ///< Llista d'espais a cercar

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1; ///< Permís de la localització
    private static final int PERMISSION_REQUEST_COARSE_BL = 2; ///< Permís de bluetooth

    protected GoogleApiClient mGoogleApiClient; ///< Per demanar l'activació de la localització
    protected LocationRequest locationRequest; ///< Per crear la petició per demanar l'activació de la localització
    int REQUEST_CHECK_SETTINGS = 100; ///< Valor per defecte per controlar el resultat quan es demana per activar bluetooth i localització

    /**
     * Crea la pantalla inicial de l'aplicació, inicialitza les variables principals, demana a l'usuari activar Bluetooth i/o Localització i obté la llista d'espais disponibles
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = findViewById(R.id.llistaEspais);
        infoButton = findViewById(R.id.botoInformacio);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();

        accesALocalitzacio();
        activacioBluetooth();
        activacioLocalitzacio();

        obtenirEspais();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, EspaiSeleccionat.class);
                i.putExtra("NomEspaiSeleccionat", llistaEspais.get(position).nom);
                i.putExtra("BeaconEspai", llistaEspais.get(position).beacon);
                MainActivity.this.startActivity(i);
            }
        });

        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Informacio.class);
                MainActivity.this.startActivity(i);
            }
        });
    }

    /**
     * Obté la llista d'espais disponibles del servidor
     */
    private void obtenirEspais(){
        Call<List<Espai>> call = mTodoService.getEspais();

        call.enqueue(new Callback<List<Espai>>() {
            @Override
            public void onResponse(Call<List<Espai>> call, Response<List<Espai>> response) {
                if (response.isSuccessful()) {
                    llistaEspais.addAll(response.body() != null ? response.body() : llistaEspais);
                    adapter = new ListViewAdapter(MainActivity.this, llistaEspais);
                    list.setAdapter(adapter);

                    editSearch = findViewById(R.id.search);
                    editSearch.setOnQueryTextListener(MainActivity.this);
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "Error intentant obtenir els espais", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            @Override
            public void onFailure(Call<List<Espai>> call, Throwable t) {
                Toast toast = Toast.makeText(MainActivity.this, "Error intentant obtenir els espais", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    /**
     * Informa a l'adaptador de la llista d'espais el text que ha escrit l'usuari per filtrar la llista, després de prémer la lupa
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filter(query);
        return false;
    }

    /**
     * Informa a l'adaptador de la llista d'espais el text que ha escrit l'usuari per filtrar la llista, sense necessitat de prémer la lupa
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.filter(newText);
        return false;
    }

    /**
     * Demana a l'usuari si vol activar la Localització amb una alerta
     */
    private void activacioLocalitzacio(){
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
    }

    /**
     * Demana a l'usuari si vol activar el Bluetooth
     */
    private void activacioBluetooth(){
        if (!getBaseContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {//Comprova si el hardware del dispositiu suporta Bluetooth
            //Si el dispositiu no suporta Bluetooth, s'informa a l'usuari
            Toast.makeText(this, "BLUETOOTH NOT SUPPORTED!", Toast.LENGTH_SHORT).show();
        }
        if (!getBaseContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {//Comprova si el hardware del dispositiu suporta Bluetooth Low Energy (BLE)
            //Si el dispositiu no suporta BLE, s'informa a l'usuari
            Toast.makeText(this, "BLE NOT SUPPORTED!", Toast.LENGTH_SHORT).show();
        }
        else {//Si suporta BLE, agafa l'adaptador de Bluetooth
            BluetoothAdapter mBTAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBTAdapter == null) {//Si intentant agafar l'adaptador retorna un error, s'informa a l'usuari
                Toast.makeText(this, "ERROR GETTING BLUETOOTH ADAPTER!", Toast.LENGTH_SHORT).show();
            }
            else{
                //Es comprova si el Bluetooth està activat. Per fer-ho, es necessita haver especificat els permisos de Bluetooth al document Manifest
                if (!mBTAdapter.isEnabled()) {
                    //Si no està activat, es demana a l'usuari que l'activi amb una alerta. La resposta de l'usuari es capturarà al mètode onActivityResult
                    Intent enableBTintent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBTintent, PERMISSION_REQUEST_COARSE_BL);
                }
            }
        }
    }

    /**
     * Demana a l'usuari si vol permetre que l'aplicació accedeixi a la Localització
     */
    private void accesALocalitzacio(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
        }
    }

    /**
     * Es crida després que l'usuari hagi acceptat o rebutjat els permisos de Localització. Si ha rebutjat es mostra un missatge informant sobre la necessitat dels permisos
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                list.setOnItemClickListener(null);
                mostrarDialogPermissions(R.string.dialog_Location_Permission);
            }
        }
    }

    /**
     * S'invoca després que l'usuari hagi acceptat o rebutjat l'activació del Bluetooth i/o la Localització
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS || requestCode == PERMISSION_REQUEST_COARSE_BL) {
            if (resultCode == RESULT_OK) {
                //Toast.makeText(getApplicationContext(), "Bluetooth i/o Localització activats", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(getApplicationContext(), "Bluetooth i/o Localització no activats", Toast.LENGTH_LONG).show();
                list.setOnItemClickListener(null);
                mostrarDialogPermissions(R.string.dialog_Location_Bluetooth);
            }
        }
    }

    /**
     * Mètode que es crida quan es posa l'aplicació en foreground, torna a comprovar si es vol activar la Localització, Bluetooth i l'accés a la Localització
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Mètode que es crida quan es posa l'aplicació en background
     */
    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * Comprova que pugui accedir a la configuració de la Localització del mòbil i envia la resposta al mètoe onResult
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(this);
    }


    /**
     * Mètode necessari posar-lo degut a la llibreria de Google. No s'utilitza.
     */
    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Mètode necessari posar-lo degut a la llibreria de Google. No s'utilitza.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /**
     * Després de comprovar la configuració de la Localització del mòbil, segons la configuració trobada, actua d'una manera o una altra
     */
    @Override
    public void onResult(@NonNull Result result) {
        final Status status = result.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS: //Si la Localització ja està activada, entra aquí i no és necessari fer res.
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED://Si la Localització no estava activada, es crea la petició a l'usuari i la resposta obtinguda és capturada pel mètode onActivityResult().
                try {
                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    //Cas que no s'hagi pogut mostrar la petició per algun motiu.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE://Si no s'ha pogut accedir a la configuració de la Localització entra aquí.
                Toast.makeText(getApplicationContext(), "No s'ha pogut accedir a la configuració de la Localització", Toast.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Crea una finestra amb un text i un botó d'acceptar, que informa a l'usuari per què es necessita el Bluetooth i la Localització
     */
    private void mostrarDialogPermissions(int id){
        new AlertDialog.Builder(this)
            .setMessage(id)
            .setCancelable(false)
            .setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            })
            .create()
            .show();
    }
}
