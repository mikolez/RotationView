package com.example.mikolez.rotationview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    private ImageView rotationView;

    private SensorManager mSensorManager;
    private SensorEventListener mSensorEventListener;

    private boolean isFirstAngle = true;
    private float firstAngle = 0;

    private long start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rotationView = findViewById(R.id.rotationView);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Load pictures from the storage
        // Pictures should be stored in the "root/Rotation/1/" directory
        // and should be named as "pic0001", "pic0002", "pic0003", "pic0004", and "pic0005"
        final Bitmap bitmap1 = loadImageFromStorage(1);
        final Bitmap bitmap2 = loadImageFromStorage(2);
        final Bitmap bitmap3 = loadImageFromStorage(3);
        final Bitmap bitmap4 = loadImageFromStorage(4);
        final Bitmap bitmap5 = loadImageFromStorage(5);

        // Set initial picture (central)
        rotationView.setImageBitmap(bitmap3);

        start = System.currentTimeMillis();

        // Start listening to the sensor
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {

                // The first angle, it helps to memorize the position in the beginning
                if (isFirstAngle) {
                    firstAngle = event.values[2];
                    isFirstAngle = false;
                }

                long update = System.currentTimeMillis();
                float magnet_x = event.values[0];
                float magnet_y = event.values[1];
                float magnet_z = event.values[2];

                // Tilt calculated
                float tilt = magnet_z - firstAngle;
                boolean isNegativeTilt = tilt < 0;
                tilt = Math.abs(tilt);

                // According to tilt, the correct picture is chosen
                if (tilt < 6) {
                    rotationView.setImageBitmap(bitmap3);
                } else if (tilt >= 6 && tilt < 16) {
                    if (isNegativeTilt) {
                        rotationView.setImageBitmap(bitmap4);
                    } else {
                        rotationView.setImageBitmap(bitmap2);
                    }
                } else {
                    if (isNegativeTilt) {
                        rotationView.setImageBitmap(bitmap5);
                    } else {
                        rotationView.setImageBitmap(bitmap1);
                    }
                }

                if ((update - start) > 3000) {
                    Log.e(TAG, "Magnet x: " + magnet_x);
                    Log.e(TAG, "Magnet y: " + magnet_y);
                    Log.e(TAG, "Magnet z: " + magnet_z);
                    start = System.currentTimeMillis();
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        mSensorManager.registerListener(mSensorEventListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View decorView = getWindow().getDecorView();
        if(hasFocus) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    private Bitmap loadImageFromStorage(int number)
    {
        Bitmap b = null;

        String root = Environment.getExternalStorageDirectory().toString();

        File rotationFolder = new File(root + "/Rotation/");

        File file = new File(rotationFolder, "pic000" + number + ".png");

        try {
            b = BitmapFactory.decodeStream(new FileInputStream(file));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        return b;

    }
}
