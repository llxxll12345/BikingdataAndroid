package com.example.example;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.utility.Utility;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class UploadActivity extends AppCompatActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private ImageView photoView;

    private String photoPath = "";
    private File cameraSavePath;
    private Uri uri;

    private boolean photoPathObtained = false;

    // The permission to access camera and write external storage
    private String[] permissions = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_upload);

        Button btnCamera     = findViewById(R.id.btn_camera);
        Button btnAlbum      = findViewById(R.id.btn_album);
        Button btnPermission = findViewById(R.id.btn_permission);
        photoView = findViewById(R.id.photo_view);

        btnCamera.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
        btnPermission.setOnClickListener(this);
        getPermission();

        File path = getExternalFilesDir(Environment.DIRECTORY_PICTURES);     // try .Albums if not working
        cameraSavePath = new File(path, System.currentTimeMillis() + ".jpg");
        //cameraSavePath = new File(Environment.getExternalStorageDirectory().getPath() + "/" + System.currentTimeMillis() + ".jpg");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_camera:
                goCamera();
                break;
            case R.id.btn_album:
                goPhotoAlbum();
                break;
            case R.id.btn_permission:
                getPermission();
                break;
            case R.id.btn_back:
                Log.d("Photo Upload", "back clicked");
                if (photoPathObtained) {
                    endActivity();
                } else {
                    Toast.makeText(this, "Upload a photo first", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    // End the activity
    private void endActivity() {
        Intent intent = new Intent();
        intent.putExtra("photoPath", photoPath);
        setResult(2, intent);
        Log.d("Photo Upload", intent.getStringExtra("photoPath"));
        //finish();
    }

    // Get permission
    private void getPermission() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            Toast.makeText(this, "Permission to access album acquired!", Toast.LENGTH_SHORT).show();
        } else {
            EasyPermissions.requestPermissions(this, "Need permission to access album", 1, permissions);
        }
    }


    // Activate album
    private void goPhotoAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    // Activate camera
    private void goCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Above Android N
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(UploadActivity.this, "com.example.example.fileprovider", cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(cameraSavePath);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        UploadActivity.this.startActivityForResult(intent, 1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Using easyPermission
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    // Gained permission
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Permission acquired", Toast.LENGTH_SHORT).show();
    }

    // Didn't agree
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Please give permission to access photo & album in order to use the APP!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Camera mode
            // SDK check
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoPath = String.valueOf(cameraSavePath);
            } else {
                photoPath = uri.getEncodedPath();
            }
            Log.d("Returned photo path:", photoPath);
            Glide.with(UploadActivity.this).load(photoPath).into(photoView);
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            // Album mode
            photoPath = Utility.getRealPathFromUri(this, data.getData());
            Glide.with(UploadActivity.this).load(photoPath).into(photoView);
        }
        endActivity();
        photoPathObtained = true;
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("Photo Upload", " paused");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //endActivity();
        Log.i("Photo Upload", " destroyed");
        super.onDestroy();
    }
}
