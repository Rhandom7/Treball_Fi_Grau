package com.example.orientacioeps.rest;

import retrofit2.Call;
import retrofit2.http.*;

import com.example.orientacioeps.Entity.Espai;
import java.util.List;


public interface TodoApi {
    //@GET("http://private-ae02a7-tfgorientacioeps.apiary-mock.com/espais")
    @GET("/espais")
    Call<List<Espai>> getEspais();
}

