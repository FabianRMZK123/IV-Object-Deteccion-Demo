package com.example.picassoroiobj;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterActivity extends AppCompatActivity {

    // Imagenes
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;

    // deteccion de objetos

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adapter);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Crear y establecer el adaptador
        imageAdapter = new ImageAdapter(VariablesGlobales.globalImageBytesList);
        recyclerView.setAdapter(imageAdapter);
    }

    @Override
    protected void onDestroy() {
        // Liberar recursos aquí
        super.onDestroy();
    }

}