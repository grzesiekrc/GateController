package com.gatecontroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ClientRequests httpRequests = new ClientRequests("http://192.168.92.131/");
    private TextView txtResult;

    private static final int REQUEST_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResult = findViewById(R.id.txtResult);

        Handler handler = new Handler();

        findViewById(R.id.btnExecute).setOnClickListener(
                (View v) -> {
                    new Thread(
                            () -> {
                                try {
                                    String response = httpRequests.getData("gateopen");
                                    handler.post(() -> txtResult.setText("OK: " + response));
                                } catch (IOException e) {
                                    handler.post(() -> txtResult.setText("Błąd: " + e.getMessage()));
                                }
                            }).start();
                });

        checkPermissions();
    }

    private void checkPermissions() {
        String[] permissionsToCheck = new String[] {
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        };
        List<String> permissionsNotGranted = new ArrayList<>();

        for(String permission : permissionsToCheck) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNotGranted.add(permission);
            }
        }

        if(!permissionsNotGranted.isEmpty()) {
            String[] notGrantedArray = permissionsNotGranted.toArray(new String[1]);

            ActivityCompat.requestPermissions(this,
                    notGrantedArray,
                    REQUEST_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSIONS) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Permission Denied for: " + permissions[i]);
                    Toast.makeText(getApplicationContext(), "Udmówiono uprawnienia: " + permissions[i], Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}