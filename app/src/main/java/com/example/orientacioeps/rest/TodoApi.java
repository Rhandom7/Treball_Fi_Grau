package com.example.orientacioeps.rest;

import retrofit2.Call;
import retrofit2.http.*;

import com.example.orientacioeps.Entity.Espai;
import com.example.orientacioeps.Entity.Beacon;
import com.example.orientacioeps.Entity.Cami;
import java.util.List;

/** @class TodoApi
 * @brief Conté les crides al servidor ("http://private-ae02a7-tfgorientacioeps.apiary-mock.com/EL_QUE_ES_VULGUI_OBTENIR")
 * @author Genís Arumí Novellas
 */
public interface TodoApi {
    @GET("/espais")
    Call<List<Espai>> getEspais();

    @GET("/beacons")
    Call<List<Beacon>> getBeacons();

    @GET("/camins")
    Call<List<Cami>> getCamins();
}

