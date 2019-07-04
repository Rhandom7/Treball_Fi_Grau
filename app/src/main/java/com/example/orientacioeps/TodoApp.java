package com.example.orientacioeps;

import android.app.Application;

import com.estimote.proximity_sdk.api.EstimoteCloudCredentials;
import com.example.orientacioeps.rest.TodoApi;
import com.example.orientacioeps.util.Global;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TodoApp extends Application {
    TodoApi mTodoService;

    @Override
    public void onCreate() {
        super.onCreate();

        Gson gson = new GsonBuilder()
                .setLenient()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Global.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mTodoService = retrofit.create(TodoApi.class);
    }

    public TodoApi getAPI(){ return mTodoService; }

    public EstimoteCloudCredentials cloudCredentials = new EstimoteCloudCredentials("orientacioeps-1eh", "784275a4e6b12e6e4febf7d8da89cdb9");
}
