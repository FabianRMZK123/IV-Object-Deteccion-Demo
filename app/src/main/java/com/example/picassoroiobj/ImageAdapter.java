package com.example.picassoroiobj;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private final ArrayList<byte[]> imageBytesList;

    public ImageAdapter(ArrayList<byte[]> imageBytesList) {
        this.imageBytesList = imageBytesList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    // Este metodo va ser para la comparacion de los productos con las casillas

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        byte[] imageBytes = imageBytesList.get(position);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        /*
        https://stackoverflow.com/questions/23202130/android-convert-byte-array-from-camera-api-to-color-mat-object-opencv

        Con un código así se puede transformar de negativo a colores normales

        Mat orig = new Mat(bitmap.getHeight(),bitmap.getWidth(), CvType.CV_8UC1);
        Bitmap myBitmap32 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(myBitmap32, orig);
        Imgproc.cvtColor(orig, orig, Imgproc.COLOR_BGR2RGB,4);
        */

        holder.imageView.setImageBitmap(bitmap);

        // Obtener el número actual
        int imageNumber = position + 1;

        // Actualizar el número en el TextView
        holder.textViewNumber.setText(imageNumber + ".");

        // Agregar OnClickListener a la imagen
        holder.imageView.setOnClickListener(view -> {
            // Obtener la imagen en pantalla completa
            Intent intent = new Intent(view.getContext(), FullScreenImageActivity.class);
            intent.putExtra("imageBytes", imageBytes);
            intent.putExtra("imageNumber", imageNumber); // Agregar el número como extra
            view.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return imageBytesList.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNumber, nombreprod;
        public ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewNumber = itemView.findViewById(R.id.textViewNumber);
            imageView = itemView.findViewById(R.id.imageView);
            nombreprod = itemView.findViewById(R.id.Nombre);

        }
    }
}