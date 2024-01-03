package com.example.picassoroiobj;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private final AtomicReference<Mat> m_mathRoi = new AtomicReference<>();
    ImageButton roi_capture;
    private CameraBridgeViewBase mOpenCvCameraView;

    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                mOpenCvCameraView.enableView();
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    private List<Bitmap> roiBitmapList;
    private final List<Rect> roiRectList = new ArrayList<>(2);
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roi_capture = findViewById(R.id.btn_capture);

        // Aqui se configura la camara
        mOpenCvCameraView = findViewById(R.id.activity_surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK); // Usa la cámara trasera

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }

        roiBitmapList = new ArrayList<>();
        roi_capture.setOnClickListener(view -> capturarRoi());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeOpenCV();
            } else {
                Toast.makeText(this, "Se requiere permiso de la cámara para ejecutar la aplicación", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat matInput = inputFrame.rgba();
        m_mathRoi.set(matInput);
        Imgproc.cvtColor(matInput, matInput, Imgproc.COLOR_BGR2RGB);
        roiRectList.clear();
        int screenWidth = matInput.cols();
        int screenHeight = matInput.rows();
        // Configuraciones de cada rectángulo
        int separation = 65; // Puedes ajustar el valor de separación según tus necesidades

        Rect rect1 = new Rect(0, 0, screenWidth / 5, screenHeight);
        Rect rect2 = new Rect(screenWidth / 6 + separation, 0, screenWidth / 6, screenHeight);
        Rect rect3 = new Rect(2 * (screenWidth / 6 + separation), 0, screenWidth / 6, screenHeight);
        Rect rect4 = new Rect(3 * (screenWidth / 6 + separation), 0, screenWidth / 6, screenHeight);
        Rect rect5 = new Rect(4 * (screenWidth / 6 + separation), 0, screenWidth / 6, screenHeight);

        // Agregar rectangular a la lista
        roiRectList.add(rect1);
        roiRectList.add(rect2);
        roiRectList.add(rect3);
        roiRectList.add(rect4);
        roiRectList.add(rect5);

        // Dibujar rectángulos en la imagen
        Imgproc.rectangle(matInput, rect1.tl(), rect1.br(), new Scalar(245, 40, 145, 0.8), 2);
        Imgproc.rectangle(matInput, rect2.tl(), rect2.br(), new Scalar(172, 5, 213, 1), 2);
        Imgproc.rectangle(matInput, rect3.tl(), rect3.br(), new Scalar(251, 189, 10, 1), 2);
        Imgproc.rectangle(matInput, rect4.tl(), rect4.br(), new Scalar(255, 0, 0), 1);
        Imgproc.rectangle(matInput, rect5.tl(), rect5.br(), new Scalar(255, 250, 255), 1);

        Log.d(TAG, "rect1: " + rect1);
        Log.d(TAG, "rect2: " + rect2);
        Log.d(TAG, "rect3: " + rect3);
        Log.d(TAG, "rect4: " + rect4);
        Log.d(TAG, "rect5: " + rect5);

        return matInput;
    }

    private void capturarRoi() {
        Log.d(TAG, "El método capturarRoi() se está ejecutando.");

        try {
            // Obtener la matriz actualizada
            Mat m_mathRoi = this.m_mathRoi.get();

            if (m_mathRoi != null) {
                // Crear una copia de la lista para evitar ConcurrentModificationException
                List<Rect> copyOfRoiRectList = new ArrayList<>(roiRectList);

                // Limpiar la lista de imágenes antes de agregar nuevos
                roiBitmapList.clear();

                for (Rect roiRect : copyOfRoiRectList) {
                    Mat roiMat = new Mat(m_mathRoi, roiRect);

                    // Reducir resolución (ajusta estos valores según tus necesidades) 500 es el limite
                    Imgproc.resize(roiMat, roiMat, new Size(500, 450));

                    // Convertir a formato JPEG con compresión (ajusta la calidad según tus necesidades)
                    MatOfByte matOfByte = new MatOfByte();
                    Imgcodecs.imencode(".jpg", roiMat, matOfByte);  // Aqui se puede cambiar el formato a PNG

                    // Convertir MatOfByte a array de bytes
                    byte[] byteArray = matOfByte.toArray();

                    // Crear un bitmap desde el array de bytes
                    Bitmap roiBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                    // Agregar el bitmap a la lista
                    roiBitmapList.add(roiBitmap);
                }

                // Log para verificar si la lista tiene datos
                Log.d(TAG, "Número de elementos en roiBitmapList: " + roiBitmapList.size());

                // Crear un intent y pasar la lista a la actividad roi
                Intent intent = new Intent(MainActivity.this, Adapter.class);

                // Convertir las imágenes a bytes y pasarlas directamente
                for (int i = 0; i < roiBitmapList.size(); i++) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    roiBitmapList.get(i).compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();
                    intent.putExtra("roiImage" + i, byteArray);
                }
                Log.e(TAG, "Estoy en Main: " );

                startActivity(intent);
            } else {
                Log.d(TAG, "m_mathRoi es nulo. No se puede capturar el ROI.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en el método capturarRoi(): " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        // Código de inicialización si es necesario
    }

    @Override
    public void onCameraViewStopped() {
        // Liberar recursos si es necesario
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeOpenCV();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.enableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    private void initializeOpenCV() {
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "OpenCV inicialización fallida");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV inicializado correctamente");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}