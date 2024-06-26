package com.db.plantlyf;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.db.plantlyf.AiModelHandler.SoilTypeClassifier;
import com.db.plantlyf.databinding.ActivityMainBinding;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SoilTypeClassifier soilTypeClassifier;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            // Define the input image dimensions and class labels
            int inputImageWidth = 256;
            int inputImageHeight = 256;
            String[] labels = {"Alluvial", "Black", "Clay", "Red"}; // Replace with your class labels

            // Create an instance of ImageClassifier
            soilTypeClassifier = new SoilTypeClassifier(this, inputImageWidth, inputImageHeight, labels);

            // Load the image from the device storage
            int resourceId = R.raw.claysoiltemp;
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);

            if (bitmap != null) {
                String predictedLabel = soilTypeClassifier.classifyImage(bitmap);

                Toast.makeText(this, "It is " + predictedLabel + " soil", Toast.LENGTH_SHORT).show();
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Load the image from the device storage
                    int resourceId = R.raw.blacksoiltemp;
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);

                    if (bitmap != null) {
                        String predictedLabel = soilTypeClassifier.classifyImage(bitmap);

                        Toast.makeText(getApplicationContext(), "It is " + predictedLabel + " soil", Toast.LENGTH_SHORT).show();
                    }
                }
            },5000);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Load the image from the device storage
                    int resourceId = R.raw.alluvialsoiltemp;
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);

                    if (bitmap != null) {
                        String predictedLabel = soilTypeClassifier.classifyImage(bitmap);

                        Toast.makeText(getApplicationContext(), "It is " + predictedLabel + " soil", Toast.LENGTH_SHORT).show();
                    }
                }
            },10000);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Load the image from the device storage
                    int resourceId = R.raw.redsoiltemp;
                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resourceId);

                    if (bitmap != null) {
                        String predictedLabel = soilTypeClassifier.classifyImage(bitmap);

                        Toast.makeText(getApplicationContext(), "It is " + predictedLabel + " soil", Toast.LENGTH_SHORT).show();
                    }
                }
            },15000);

        } catch (IOException e) {
           Log.e("Error",e+"");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soilTypeClassifier != null) {
            soilTypeClassifier.close();
        }
    }

}


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//    }