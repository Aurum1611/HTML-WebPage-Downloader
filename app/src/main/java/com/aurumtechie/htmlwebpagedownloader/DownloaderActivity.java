package com.aurumtechie.htmlwebpagedownloader;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static com.aurumtechie.htmlwebpagedownloader.DownloadWebPageKt.downloadWebPage;

public class DownloaderActivity extends AppCompatActivity {

    public static final String DOWNLOAD_URL_EXTRA = "Download Url Extra";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);

        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra(DOWNLOAD_URL_EXTRA);
            if (url != null)
                downloadWebPage(
                        url,
                        (TextView) findViewById(R.id.progress_textview),
                        (ProgressBar) findViewById(R.id.progress_circular)
                );
        }
    }

    public void messageBtnClicked(View view) {
        Toast.makeText(view.getContext(), R.string.message_button_clicked, Toast.LENGTH_SHORT).show();
    }
}
