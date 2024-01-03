package com.example.picassoroiobj;

import static org.tensorflow.lite.task.vision.detector.ObjectDetector.createFromFileAndOptions;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;
import org.tensorflow.lite.task.vision.detector.Detection;
import org.tensorflow.lite.task.vision.detector.ObjectDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FullScreenImageActivity extends AppCompatActivity {

    private static final float MAX_FONT_SIZE = 96F;
    private ImageView inputImageView;
    private TextView tvPro, tvdes;
    int RP = 0;
    int DP = 0;
    private String m ;
    private final List<Detection> results;  // Variable miembro para almacenar los resultados

    public FullScreenImageActivity() {
        this.results = new ArrayList<>();
    }

    @SuppressLint("MissingInflatedId")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        // Inicializar vistas
        inputImageView = findViewById(R.id.fullScreenImageView);
        tvPro = findViewById(R.id.resultPrdo);
        tvdes = findViewById(R.id.resultdesc);

        // Obtener datos de la intención
        byte[] imageBytes = getIntent().getByteArrayExtra("imageBytes");
        int imageNumber = getIntent().getIntExtra("imageNumber", 0);

        // Log para depuración
        Log.d("DEBUG_TAG", "Valor de imageNumber antes de establecer m: " + imageNumber);

        // Decodificar bytes en un bitmap y establecerlo en el ImageView
        assert imageBytes != null;
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        inputImageView.setImageBitmap(bitmap);

        // Configurar la escala del ImageView
        inputImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        Log.d("DEBUG_TAG", "Valor de imageNumber obtenido de la intención: " + imageNumber);

        // Establecer el valor de "m" basado en "imageNumber"
        if (imageNumber == 1) {
            m = "DoritosNegros";
        } else if (imageNumber == 2) {
            m = "ChipsMoradas";
        } else if (imageNumber == 3) {
            m = "ChettosFlamitHot";
        } else if (imageNumber == 4) {
            m = "ChettosNaranjas";
        } else if (imageNumber == 5) {
            m = "TostitosMorados";
        } else if (imageNumber == 6) {
            m = "TostitosVerdes";
        } else if (imageNumber == 7) {
            m = "Palomitas";
        } else if (imageNumber == 8) {
            m = "ChettosVerdes";
        } else if (imageNumber == 9) {
            m = "ChipsAmarillas";
        } else {
            Log.e("DEBUG_TAG", "Valor inesperado de imageNumber: " + imageNumber);
        }

        try {
            // Ejecutar la detección de objetos automáticamente al abrir la actividad
            runObjectDetection(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Llamar a debugPrint después de establecer el valor de "m"
        debugPrint(results);
    }

    private void runObjectDetection(Bitmap bitmap) throws IOException {
        if (bitmap != null) {
            TensorImage image = TensorImage.fromBitmap(bitmap);

            ObjectDetector.ObjectDetectorOptions options = ObjectDetector.ObjectDetectorOptions.builder()
                    .setMaxResults(14)
                    .setScoreThreshold(0.45f) // 0.45 funciona bien para los doritos y chettos Naranjas
                    .build();
            ObjectDetector detector = createFromFileAndOptions(
                    this,
                    "pincOpen.tflite",
                    options
            );

            // silletrained_model
            // pincOpen.tflite
            //Makinatrained_model

            // Realizar la detección de objetos
            List<Detection> results = detector.detect(image);

            // Procesar los resultados para mostrar en la imagen
            List<DetectionResult> resultToDisplay = new ArrayList<>();
            for (Detection detection : results) {
                if (detection.getCategories() != null && !detection.getCategories().isEmpty()) {
                    Category category = detection.getCategories().get(0);
                    String text = category.getLabel() + ", " + (int) (category.getScore() * 100) + "%";
                    resultToDisplay.add(new DetectionResult(detection.getBoundingBox(), text));
                }
            }

            // Dibujar los resultados en la imagen y mostrarla en el hilo de la interfaz de usuario
            Bitmap imgWithResult = drawDetectionResult(bitmap, resultToDisplay);
            runOnUiThread(() -> {
                inputImageView.setImageBitmap(imgWithResult);
                debugPrint(results);  // Llamar a debugPrint después de mostrar la imagen con resultados
            });
            // Liberar recursos del detector
            detector.close();
        } else {
            // Manejar el caso en que la imagen es nula
            Log.e("DEBUG_TAG", "La imagen es nula");
        }
    }
    // En este metodo se cuenta y se imprime el contador
    @SuppressLint("SetTextI18n")
    private void debugPrint(List<Detection> results) {
        if (results != null) {
            DP = 0;
            RP = 0;
            for (int i = 0; i < results.size(); i++) {
                Detection obj = results.get(i);
                List<Category> categories = obj.getCategories();
                if (categories != null) {
                    for (int j = 0; j < categories.size(); j++) {
                        Category category = categories.get(j);
                        Log.d("DEBUG_TAG", "category.getLabel(): " + category.getLabel());
                        Log.d("DEBUG_TAG", "m: " + m);
                        if (category.getLabel().trim().equals(m)) {
                            RP++;
                            runOnUiThread(() -> tvPro.setText(RP + " " + m));
                        } else {
                            DP++;
                            runOnUiThread(() -> tvdes.setText(String.valueOf(DP)));
                        }

                    }

                } else {
                    Log.e("DEBUG_TAG", "La lista de categorías (categories) es nula");
                }
            }
            Log.d("DEBUG_TAG", "RP: " + RP + ", DP: " + DP);
        } else {
            Log.e("DEBUG_TAG", "La lista de resultados (results) es nula");
        }
    }

    // hasta Aqui hay un posible fallo

    private Bitmap drawDetectionResult(Bitmap bitmap, List<DetectionResult> detectionResults) {
        Bitmap outputBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(outputBitmap);
        Paint pen = new Paint();
        pen.setTextAlign(Paint.Align.LEFT);

        for (DetectionResult result : detectionResults) {
            // Dibujar el cuadro delimitador
            pen.setColor(Color.RED);
            pen.setStrokeWidth(8F);
            pen.setStyle(Paint.Style.STROKE);
            RectF box = result.getBoundingBox();
            canvas.drawRect(box, pen);

            Rect tagSize = new Rect(0, 0, 0, 0);

            // Calcular el tamaño correcto de la fuente
            pen.setStyle(Paint.Style.FILL_AND_STROKE);
            pen.setColor(Color.YELLOW);
            pen.setStrokeWidth(2F);

            pen.setTextSize(MAX_FONT_SIZE);
            pen.getTextBounds(result.getText(), 0, result.getText().length(), tagSize);
            float fontSize = pen.getTextSize() * box.width() / tagSize.width();

            // Ajustar el tamaño de la fuente para que el texto esté dentro del cuadro delimitador
            if (fontSize < pen.getTextSize()) pen.setTextSize(fontSize);

            float margin = (box.width() - tagSize.width()) / 2.0F;
            if (margin < 0F) margin = 0F;
            canvas.drawText(
                    result.getText(), box.left + margin,
                    box.top + tagSize.height() * 1F, pen
            );
        }
        return outputBitmap;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}

class DetectionResult {
    private final RectF boundingBox;
    private final String text;
    public DetectionResult(RectF boundingBox, String text) {
        this.boundingBox = boundingBox;
        this.text = text;
    }
    public RectF getBoundingBox() {
        return boundingBox;
    }
    public String getText() {
        return text;
    }
}