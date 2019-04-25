package com.example.orientacioeps.Activity;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    // Declare Variables
    TodoApi mTodoService;
    ListView list;
    ListViewAdapter adapter;
    SearchView editsearch;

    List<Espai> llistaEspais = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Locate the ListView in activity_main.xml
        list = (ListView) findViewById(R.id.listview);

        mTodoService = ((TodoApp)this.getApplication()).getAPI();
        Call<List<Espai>> call = mTodoService.getEspais();

        call.enqueue(new Callback<List<Espai>>() {
            @Override
            public void onResponse(Call<List<Espai>> call, Response<List<Espai>> response) {
                if (response.isSuccessful()) {
                    for(Espai e : response.body()){
                        llistaEspais.add(e);
                    }
                    adapter = new ListViewAdapter(MainActivity.this, llistaEspais);
                    list.setAdapter(adapter);
                    editsearch = (SearchView) findViewById(R.id.search);
                    editsearch.setOnQueryTextListener(MainActivity.this);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        adapter.filter(text);
        return false;
    }
}
