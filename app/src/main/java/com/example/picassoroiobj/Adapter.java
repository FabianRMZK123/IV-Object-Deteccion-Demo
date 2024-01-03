package com.example.picassoroiobj;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends AppCompatActivity {

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

        // Recuperar los bytes de las imágenes del intent
        Intent intent = getIntent();
        ArrayList<byte[]> imageBytesList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            byte[] byteArray = intent.getByteArrayExtra("roiImage" + i);
            if (byteArray != null) {
                imageBytesList.add(byteArray);
            }
        }
        // Crear y establecer el adaptador
        imageAdapter = new ImageAdapter(imageBytesList);
        recyclerView.setAdapter(imageAdapter);
    }

    @Override
    protected void onDestroy() {
        // Liberar recursos aquí
        super.onDestroy();
    }

}