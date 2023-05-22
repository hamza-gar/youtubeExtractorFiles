package com.example.youtubedownloader;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import at.huber.youtubeExtractor.YouTubeUriExtractor;
import at.huber.youtubeExtractor.YtFile;

public class downloadActivity extends AppCompatActivity {
    private Button downloadButton;
    private EditText urlInput;
    private String downloadUrl;
    private String SelectedType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        downloadButton = findViewById(R.id.download);
        urlInput = findViewById(R.id.urlInput);

        String [] items = {"video","audio"};
        AutoCompleteTextView autoCompleteTextView;
        ArrayAdapter<String> adapterItems;

        autoCompleteTextView = findViewById(R.id.auto_complete_text);
        adapterItems = new ArrayAdapter<String>(this,R.layout.list_item, items);

        autoCompleteTextView.setAdapter(adapterItems);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
//                Toast.makeText(downloadActivity.this, "Item"+ item, Toast.LENGTH_SHORT).show();
                SelectedType = item;
            }
        });

        YouTubeUriExtractor  somxt = new YouTubeUriExtractor(this) {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {

                if(ytFiles != null){
                    int itag = 22;

                    try{
                        downloadUrl = ytFiles.get(itag).getUrl();
                        if (downloadUrl !=null){
                            Toast.makeText(downloadActivity.this, "Download started ..", Toast.LENGTH_SHORT).show();

                            downloadVideo(downloadUrl);
//                              downloadAudio(downloadUrl);
                            Log.d("DOWNLOAD URL", "URL :-" +downloadUrl);
                        }
                    }catch (Exception e){
                        Toast.makeText(downloadActivity.this, "download url could not be fetched !!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        YouTubeUriExtractor  audiodownload = new YouTubeUriExtractor(this) {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onUrisAvailable(String videoId, String videoTitle, SparseArray<YtFile> ytFiles) {

                if(ytFiles != null){
                    int itag = 22;

                    try{
                        downloadUrl = ytFiles.get(itag).getUrl();
                        if (downloadUrl !=null){
                            Toast.makeText(downloadActivity.this, "Download started ..", Toast.LENGTH_SHORT).show();

                              downloadAudio(downloadUrl);
                            Log.d("DOWNLOAD URL", "URL :-" +downloadUrl);
                        }
                    }catch (Exception e){
                        Toast.makeText(downloadActivity.this, "download url could not be fetched !!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = urlInput.getText().toString();
                if(url == null){
                    Toast.makeText(downloadActivity.this, "you should enter a link", Toast.LENGTH_SHORT).show();
                }else{
                    if(SelectedType == "audio"){
                        if(url!=null){
                            audiodownload.extract(url);
                        }
                    }else if(SelectedType == "video"){
                        if(url!=null){
                            somxt.extract(url);
                        }
                    }else{
                        Toast.makeText(downloadActivity.this, "you should select type", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    void downloadVideo(String Url){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("download Video");
        request.setDescription("Your video file is downloading");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(downloadActivity.this, Environment.DIRECTORY_DOWNLOADS,"testing.mp4");

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    void downloadAudio(String url) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download Audio");
        request.setDescription("Your audio file is downloading");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("audio/mpeg");
        request.setDestinationInExternalFilesDir(downloadActivity.this, Environment.DIRECTORY_DOWNLOADS, "testing.mp3");

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }


    String getFileTitleFromUrl(String url) {
        Uri uri = Uri.parse(url);
        String lastPathSegment = uri.getLastPathSegment();
        if (lastPathSegment != null) {
            int dotIndex = lastPathSegment.lastIndexOf(".");
            if (dotIndex != -1) {
                return lastPathSegment.substring(0, dotIndex);
            } else {
                return lastPathSegment;
            }
        }
        return null;
    }

}