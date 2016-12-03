package com.test.sun.testpermission;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.R.attr.permission;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("info", "MainActivity:onCreate----------------------");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            getPermission = true;
        }

        surfaceView = (SurfaceView) findViewById(R.id.main_surface);

    }

    @Override
    protected void onResume() {
        Log.i("info", "MainActivity:onResume----------------------");
        super.onResume();
        Log.i("info", ":" + getPermission);
        if (getPermission) {
            surfaceView.getHolder().addCallback(this);
        }
    }

    @Override
    protected void onStart() {
        Log.i("info", "MainActivity:onStart----------------------");
        super.onStart();
    }

    private boolean getPermission;

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {

        Log.i("info", "MainActivity:shouldShowRequestPermissionRationale----------------------");
        boolean b = super.shouldShowRequestPermissionRationale(permission);
        Log.i("info", ":" + b);
        return b;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("info", "MainActivity:onRequestPermissionsResult----------------------");
        for (int i = 0; i < permissions.length; i++) {
            if (Manifest.permission.CAMERA.equals(Manifest.permission.CAMERA)) {
                if (PackageManager.PERMISSION_GRANTED == grantResults[i]) {
                    getPermission = true;
                } else {
                    if (!shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        showMessageOKCancel("You need to allow access to Camera",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                                        intent.setData(Uri.parse("package:" + MainActivity.this.getPackageName()));
                                        MainActivity.this.startActivity(intent);
                                    }
                                });
                        return;
                    }
//                    this.finish();
                }
            }
        }


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initCamera(SurfaceHolder surfaceHolder) {

//        int numberOfCameras = Camera.getNumberOfCameras();
//        for (int i = 0; i < numberOfCameras; i++) {
//            Camera.CameraInfo info = new Camera.CameraInfo();
//            Camera.getCameraInfo(i, info);
//            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
//                camera = Camera.open(i);
//        }
        camera = Camera.open();
        try {
            camera.setPreviewDisplay(surfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.i("info", "MainActivity:surfaceCreated----------------------");
        initCamera(surfaceHolder);

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.i("info", "MainActivity:surfaceChanged----------------------");
        camera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.i("info", "MainActivity:surfaceDestroyed----------------------");
        camera.release();
        camera = null;
    }
}
