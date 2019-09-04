package com.dntutty.rtmp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_CALL_PHONE = 101;
    private CameraHelper cameraHelper;
    private SurfaceView surfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        surfaceView = findViewById(R.id.surface_view);
        cameraHelper = new CameraHelper(this, Camera.CameraInfo.CAMERA_FACING_BACK, 480, 800);
        cameraHelper.setPreviewDisplay(surfaceView.getHolder());
    }

    public void stopLive(View view) {
    }

    public void startLive(View view) {
    }

    public void switchCamera(View view) {
        cameraHelper.switchCamera();
    }

    private void requestPermission() {
        String[] permissions = new String[]{
                Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};
        ArrayList<String> noGranted = new ArrayList<>();
        for (String permission : permissions) {
            int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                noGranted.add(permission);
            }
        }
        if (noGranted.size() > 0) {
            ActivityCompat.requestPermissions(this, noGranted.toArray(new String[noGranted.size()]), REQUEST_CODE_ASK_CALL_PHONE);
            return;
        }
    }
}
