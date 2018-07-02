package vivaladev.com.dirtyclocky.ui.fragmentProcessing.factories;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import vivaladev.com.dirtyclocky.R;

public class RecordFactory {
    Context context;
    private int currentLineLengthDP;
    private LinearLayout currentLinearLayout;
    private LinearLayout tags_linearLayout;
    private View.OnClickListener clickListener;

    public RecordFactory(Context context, LinearLayout tags_linearLayout, View.OnClickListener clickListener) {
        this.context = context;
        this.currentLineLengthDP = currentLineLengthDP;
        this.currentLinearLayout = currentLinearLayout;
        this.tags_linearLayout = tags_linearLayout;
        this.clickListener = clickListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addTagToScreen(int id, String tag, boolean selected) {
        final int tag_left_marginDP = 5;
        final int tag_right_marginDP = 5;
        final int tag_left_paddingDP = 5;
        final int tag_right_paddingDP = 5;
        int tagLength = tag_left_paddingDP + tag.length() * 10 + tag_right_paddingDP;

        if (currentLinearLayout == null) {
            currentLineLengthDP = tag_left_marginDP + tag_right_marginDP;
            currentLinearLayout = createLinearLayout();
            tags_linearLayout.addView(currentLinearLayout);
        }

        TextView newTV = createTextView(id, tag, selected);
        CardView cardView = createCardView();
        cardView.addView(newTV);

        if (convertDpToPixel(currentLineLengthDP + tagLength) >= getScreenWidth()) {
            currentLinearLayout = createLinearLayout();
            tags_linearLayout.addView(currentLinearLayout);
            currentLineLengthDP = tag_left_marginDP + tag_right_marginDP + tagLength;
        } else {
            currentLineLengthDP += tagLength;
        }
        currentLinearLayout.addView(cardView);
    }

    private LinearLayout createLinearLayout() {
        LinearLayout newLLayout = new LinearLayout(context);
        LinearLayout.LayoutParams newLLayout_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        0
                );
        newLLayout.setOrientation(LinearLayout.HORIZONTAL);
        newLLayout.setLayoutParams(newLLayout_params);
        return newLLayout;
    }

    private CardView createCardView() {
        int PXmargin_top_bottom = convertDpToPixel(5);
        int PXmargin = convertDpToPixel(10);

        CardView noteCardView = new CardView(context);
        LinearLayout.LayoutParams noteLayout_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        0
                );
        noteCardView.setRadius(convertDpToPixel(4));
        noteCardView.setCardElevation(convertDpToPixel(2));
        noteLayout_params.setMargins(PXmargin, PXmargin_top_bottom, 0, PXmargin_top_bottom);
        noteCardView.setLayoutParams(noteLayout_params);
        return noteCardView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private TextView createTextView(int id, String text, boolean selected) {
        int PXpadding = convertDpToPixel(5);
        TextView newTextView = new TextView(context);
        RelativeLayout.LayoutParams newTextView_params =
                new RelativeLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        if (selected) {
            newTextView.setBackgroundColor(context.getResources().getColor(R.color.selected_tag));
            newTextView.setTextColor(context.getResources().getColor(android.R.color.black));
        } else {
            newTextView.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        }
        newTextView.setId(id);
        newTextView.setFocusable(true);
        newTextView.setClickable(true);
        newTextView.setGravity(Gravity.CENTER | Gravity.LEFT);
        newTextView.setForeground(ContextCompat.getDrawable(context, R.drawable.note_tag_ripple_effect));
        newTextView.setPadding(PXpadding, PXpadding, PXpadding, PXpadding);
        newTextView.setText(text);
        newTextView.setTextSize(16);
        newTextView.setOnClickListener(clickListener);
        newTextView.setLayoutParams(newTextView_params);
        return newTextView;
    }

    private int convertDpToPixel(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private int getScreenWidth() {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }
}
