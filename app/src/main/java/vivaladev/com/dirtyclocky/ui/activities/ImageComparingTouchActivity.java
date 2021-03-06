package vivaladev.com.dirtyclocky.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

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
        setContentView(R.layout.activity_to_comparing_touch);
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
        imageView.setOnTouchListener((view, motionEvent) -> {
            mX = motionEvent.getX();
            mY = motionEvent.getY();

            String mCoords = null;

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: // нажатие
                    if (checkTouchZone(Math.round(mX), Math.round(mY), parseCoord(coords))) {
                        Toast.makeText(this, "Done. Alarm off", Toast.LENGTH_LONG).show();
                        prepareToReturn();
                    } else {
                        Toast.makeText(this, "Not right place", Toast.LENGTH_LONG).show();
                    }

                    break;
            }

            return true;
        });
    }

    private void prepareToReturn(){
        Intent data = new Intent();
        setResult(RESULT_OK, data);
        finish();
    }

    private boolean checkTouchZone(int x, int y, List<Integer> coords) { //coords XxY

        if ((coords.get(0) + 30) <= x && ((coords.get(1) + 30) <= y)) {
            return true;
        } else if ((coords.get(0) + 30) <= x && ((coords.get(1) - 30) <= y)) {
            return true;
        } else if ((coords.get(0) - 30) <= x && ((coords.get(1) - 30) <= y)) {
            return true;
        } else if ((coords.get(0) - 30) <= x && ((coords.get(1) - 30) <= y)) {
            return true;
        }
        return false;
    }

    private List<Integer> parseCoord(String coords) {
        List<String> temp = Arrays.asList("", "");
        boolean toX = true;
        StringBuilder onX = new StringBuilder();
        StringBuilder onY = new StringBuilder();
        for (char item : coords.toCharArray()) {
            if (toX) {
                onX.append(item);
            }
            if (item == 'x') {
                toX = false;
            }
            if (!toX) {
                onY.append(item);
            }
        }
        return Arrays.asList(Integer.parseInt(onX.toString().substring(0, onX.length() - 1)), Integer.parseInt(onY.toString().substring(1, onY.length())));
    }

    private void init() {
        imageView = findViewById(R.id.comparingImage);
    }
}
