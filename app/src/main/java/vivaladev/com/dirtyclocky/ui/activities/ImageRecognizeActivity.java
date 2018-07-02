package vivaladev.com.dirtyclocky.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import vivaladev.com.dirtyclocky.R;

public class ImageRecognizeActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recognize_image_layout);
        init();

        Intent intent = getIntent();
        String imageURI = intent.getStringExtra("uriImage");
        Uri uri = Uri.parse(imageURI);
        setImageFromURI(uri);
        getClickedZone();
    }

    private void setImageFromURI(Uri uri) {
        imageView.setImageURI(uri);
    }

    private void getClickedZone() {
    }

    private void init() {
        imageView = findViewById(R.id.recognizeImage);
    }
}
