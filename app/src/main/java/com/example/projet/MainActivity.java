package com.example.projet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.projet.ml.FruitClassifier;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_GALLERY_PERMISSION = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 300;
    private static final int REQUEST_PICK_IMAGE = 400;
    private static final int IMAGE_WIDTH = 100;
    private static final int IMAGE_HEIGHT = 100;

    private int maxProbIndex = 0;

    private Button camera;
    private Button galery;
    private Button inf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera = findViewById(R.id.сamera);
        galery = findViewById(R.id.galery);
        inf = findViewById(R.id.inf);

        camera.setOnClickListener(new View.OnClickListener() {                                                      //камера
            @Override
            public void onClick(View v) {
                requestCameraPermission();
            }
        });

        galery.setOnClickListener(new View.OnClickListener() {                                                     //галерея
            @Override
            public void onClick(View v) {
                requestGalleryPermission();
            }
        });

        inf.setOnClickListener(new View.OnClickListener() {                                                         // информация
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(MainActivity.this, MainActivity2.class);
                startActivities(new Intent[]{intent});
            }
        });

    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            openCamera();
        }
    }

    private void requestGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY_PERMISSION);
        } else {
            openGallery();
        }
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                // Получение миниатюры изображения
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");

                try {
                    FruitClassifier model = FruitClassifier.newInstance(MainActivity.this);

                    if (imageBitmap == null) {
                        throw new IllegalArgumentException("Error: Bitmap is null");
                    }

                    Bitmap rescaledImage = Bitmap.createScaledBitmap(imageBitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);

                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(rescaledImage);

                    float[] output = model
                            .process(tensorImage.getTensorBuffer())
                            .getOutputFeature0AsTensorBuffer()
                            .getFloatArray();

                    model.close();

                    float maxOutput = Float.MIN_VALUE;
                    for (int i = 1; i < output.length; i++) {
                        if (output[i] > maxOutput) {
                            maxOutput = output[i];
                            maxProbIndex = i;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                    switch (maxProbIndex) {
                        case 0:
                            // Вывод текста "Яблоко"
                            Toast.makeText(MainActivity.this, "Яблоко", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            // Вывод текста "Банан"
                            Toast.makeText(MainActivity.this, "Банан", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            // Вывод текста "Груша"
                            Toast.makeText(MainActivity.this, "Груша", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            // Вывод текста "хз"
                            Toast.makeText(MainActivity.this, "Хз", Toast.LENGTH_SHORT).show();
                            break;
                    }

        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            // Обработка выбранной фотографии из галереи
            Uri imageUri = data.getData();
            Bitmap imageBitmap;
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                FruitClassifier model = FruitClassifier.newInstance(MainActivity.this);

                if (imageBitmap == null) {
                    throw new IllegalArgumentException("Error: Bitmap is null");
                }

                Bitmap rescaledImage = Bitmap.createScaledBitmap(imageBitmap, IMAGE_WIDTH, IMAGE_HEIGHT, true);

                TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                tensorImage.load(rescaledImage);

                float[] output = model
                        .process(tensorImage.getTensorBuffer())
                        .getOutputFeature0AsTensorBuffer()
                        .getFloatArray();

                model.close();

                float maxOutput = Float.MIN_VALUE;
                for (int i = 1; i < output.length; i++) {
                    if (output[i] > maxOutput) {
                        maxOutput = output[i];
                        maxProbIndex = i;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            switch (maxProbIndex) {
                case 0:
                    // Вывод текста "Яблоко"
                    Toast.makeText(MainActivity.this, "Авокадо", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    // Вывод текста "Банан"
                    Toast.makeText(MainActivity.this, "Банан", Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    // Вывод текста "Груша"
                    Toast.makeText(MainActivity.this, "Лимон", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    // Вывод текста "хз"
                    Toast.makeText(MainActivity.this, "Яблоко", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}