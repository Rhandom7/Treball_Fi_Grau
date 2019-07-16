package com.example.orientacioeps.rest;

import retrofit2.Call;
import retrofit2.http.*;

import com.example.orientacioeps.Entity.Espai;
import com.example.orientacioeps.Entity.Beacon;
import com.example.orientacioeps.Entity.Cami;
import java.util.List;


/** @file Alien.java
 * @brief Classe Alien
 */

/** @class Alien
 * @brief Tipus de personatge que té com a objectiu eliminar humans i fugir de trolls. Només pot recollir claus.
 * @author Genís Arumí Novellas
 */
public interface TodoApi {
    //@GET("http://private-ae02a7-tfgorientacioeps.apiary-mock.com/espais")
    @GET("/espais")
    Call<List<Espai>> getEspais();

    @GET("/beacons")
    Call<List<Beacon>> getBeacons();

    @GET("/camins")
    Call<List<Cami>> getCamins();
}

