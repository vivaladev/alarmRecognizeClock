package vivaladev.com.dirtyclocky.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vivaladev.com.dirtyclocky.R;

public class ImageComparingTouchActivity extends AppCompatActivity {

    private ImageView imageView;
    private float mX;
    private float mY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recognize_image_layout);
        init();

        Intent intent = getIntent();
        String imageURI = intent.getStringExtra("uriImage");
        String coords = intent.getStringExtra("coords");
        Uri uri = Uri.parse(imageURI);
        setImageFromURI(uri);
        getClickedZone(coords);
    }

    private void setImageFromURI(Uri uri) {
        imageView.setImageURI(uri);
    }

    @SuppressLint({"ClickableViewAccessibility", "ShowToast"})
    private void getClickedZone(String coords) {
        Toast.makeText(this, "Touch alarm off zone", Toast.LENGTH_LONG).show();
        imageView.setOnTouchListener((view, motionEvent) -> {
            mX = motionEvent.getX();
            mY = motionEvent.getY();

            String mCoords = null;

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: // нажатие
                    checkTouchZone(Math.round(mX), Math.round(mY), parseCoord(coords));

                    break;
            }

            return true;
        });
    }

    private void checkTouchZone(int x, int y, List<Integer> coords) { //coords XxY

        if ((coords.get(0) + 100) <= x && ((coords.get(1) + 100) <= y)) {

        } else if ((coords.get(0) + 100) <= x && ((coords.get(1) - 100) <= y)) {

        }
    }

    private List<Integer> parseCoord(String coords) {
        List<String> temp = Arrays.asList("", "");
        boolean toX = true;
        for (char item : coords.toCharArray()) {
            if (toX) {
                temp.set(0, new StringBuilder(temp.get(0)).append(item).toString());
            }
            if (item == 'x') {
                toX = false;
            }
            if (!toX) {
                temp.set(1, new StringBuilder(temp.get(1)).append(item).toString());
            }
        }
        return Arrays.asList(Integer.parseInt(temp.get(0)), Integer.parseInt(temp.get(1)));
    }

    //    private void prepareToReturnImageZone(String finalMCoords) {
//        Intent data = new Intent();
//        data.putExtra("", "");
//        setResult(RESULT_OK, data);
////        setFinalCoords(finalMCoords);
//        finish();
//    }
//
    private void init() {
        imageView = findViewById(R.id.comparingImage);
    }
}
