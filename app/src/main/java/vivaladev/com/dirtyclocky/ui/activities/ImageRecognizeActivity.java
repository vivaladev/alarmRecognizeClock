package vivaladev.com.dirtyclocky.ui.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import vivaladev.com.dirtyclocky.R;

public class ImageRecognizeActivity extends AppCompatActivity {

    private ImageView imageView;
    private float mX;
    private float mY;
    private static final String TAG = "MyApp";

    public static String urik;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recognize_image_layout);
        init();

        Intent intent = getIntent();
        String imageURI = intent.getStringExtra("uriImage");
        Toast.makeText(this, "Uri:" + imageURI + "|", Toast.LENGTH_SHORT).show();
        Uri uri = Uri.parse(imageURI);
        urik = imageURI;
        setImageFromURI(uri);
        getClickedZone();
    }

    private void setImageFromURI(Uri uri) {
        imageView.setImageURI(uri);
    }

    @SuppressLint({"ClickableViewAccessibility", "ShowToast"})
    private void getClickedZone() {
        Toast.makeText(this, "Touch alarm off zone", Toast.LENGTH_LONG).show();
        imageView.setOnTouchListener((view, motionEvent) -> {
            mX = motionEvent.getX();
            mY = motionEvent.getY();

            String mCoords = null;

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: // нажатие
                    mCoords = Math.round(mX)+ "x" + Math.round(mY);

                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(this);
                    String finalMCoords = mCoords;
                    builder.setTitle("Are you sure you want to select this zone?")
                            .setCancelable(false)
                            // добавляем одну кнопку для закрытия диалога
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", (dialog, id) -> {
                                prepareToReturnImageZone(finalMCoords);
                                dialog.cancel();
                            });
                    AlertDialog alert = builder.create();
                    alert.show();

                    break;
            }

            return true;
        });
    }

    private static String finalCoords;

    public static String getFinalCoords() {
        return finalCoords;
    }

    public static void setFinalCoords(String finalCoords) {
        ImageRecognizeActivity.finalCoords = finalCoords;
    }

    private void prepareToReturnImageZone(String finalMCoords) {
        Intent data = new Intent();
        data.putExtra("imageCoords", finalMCoords);
        setResult(RESULT_OK, data);
        setFinalCoords(finalMCoords);
        finish();
    }

    private void init() {
        imageView = findViewById(R.id.recognizeImage);
    }
}
