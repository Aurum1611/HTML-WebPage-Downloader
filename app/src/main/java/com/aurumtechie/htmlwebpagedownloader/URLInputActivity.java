package com.aurumtechie.htmlwebpagedownloader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class URLInputActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 4579;

    EditText urlInputEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_url_input);

        if (!(ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ) requestExternalStoragePermission();
            else requestPermissionAndOpenSettings();
        }

        urlInputEditText = findViewById(R.id.urlInputEditText);
        urlInputEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        });
    }

    public void downloadButtonClicked(View view) {
        if (!ConnectivityHelper.isConnectedToNetwork(this)) {
            Snackbar.make(view, R.string.not_connected_to_internet, Snackbar.LENGTH_SHORT).show();
            return;
        }

        String userInput = urlInputEditText.getText().toString();
        if (userInput.isEmpty()) return;

        if (userInput.matches(ConnectivityHelper.WEB_URL_REGEX)) {
            // If a protocol isn't mentioned, default it to https://
            if (!userInput.matches(ConnectivityHelper.URL_PROTOCOL_CHECK_REGEX))
                userInput = "https://" + userInput;

            Intent intent = new Intent(this, DownloaderActivity.class);
            intent.putExtra(DownloaderActivity.DOWNLOAD_URL_EXTRA, userInput);
            startActivity(intent);
        } else
            Snackbar.make(view, R.string.invalid_url_message, Snackbar.LENGTH_SHORT).show();
    }

    private void requestExternalStoragePermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                REQUEST_CODE
        );
    }

    private void requestPermissionAndOpenSettings() {
        new AlertDialog.Builder(this)
                .setMessage(R.string.permission_request)
                .setPositiveButton(R.string.show_settings, (dialog, which) -> {
                    dialog.dismiss();
                    // Open application settings to enable the user to toggle the permission settings
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.fromParts("package", getPackageName(), null));
                    startActivity(intent);
                }).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED))
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                ) // If permission was denied once before but the user wasn't informed why the permission is necessary, do so.
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.external_storage_permission_rationale)
                            .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                                dialog.dismiss();
                                requestExternalStoragePermission();
                            }).show();
                else /* If user has chosen to not be shown permission requests any longer,
                     inform the user about it's importance and redirect her/him to device settings
                     so that permissions can be given */
                    requestPermissionAndOpenSettings();
        }
    }
}
