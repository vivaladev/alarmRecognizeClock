package vivaladev.com.dirtyclocky.ui.fragmentProcessing.factories;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Arrays;

import vivaladev.com.dirtyclocky.R;
import vivaladev.com.dirtyclocky.databaseProcessing.entities.Tag;

public class AlarmFactory {

    Context context;
    private LinearLayout notes_linearLayout;
    private View.OnClickListener clickListener;

    public AlarmFactory(Context context, LinearLayout notes_linearLayout, View.OnClickListener clickListener) {
        this.context = context;
        this.notes_linearLayout = notes_linearLayout;
        this.clickListener = clickListener;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addNoteToScreen(int id, String date, String title, String body, Tag[] tags) {

        CardView cardView = createCardView();
        LinearLayout noteLayout = createLinearLayout(id);
        TextView dateTV = createDateTextView(date);
        TextView titleTV = createTitleTextView(title);
        TextView bodyTV = createBodyTextView(body);
        TextView tagsTV = createTagsTextView(tags);

        cardView.addView(noteLayout);
        noteLayout.addView(dateTV);
        noteLayout.addView(titleTV);
        noteLayout.addView(bodyTV);
        noteLayout.addView(tagsTV);

        notes_linearLayout.addView(cardView);
    }

    private CardView createCardView() {
        int PXmargin_top_bottom = convertDpToPixel(5);
        int PXmargin = convertDpToPixel(10);

        CardView noteCardView = new CardView(context);
        LinearLayout.LayoutParams noteLayout_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        convertDpToPixel(182),
                        1
                );
        noteCardView.setRadius(convertDpToPixel(4));
        noteCardView.setCardElevation(convertDpToPixel(2));
        noteLayout_params.setMargins(PXmargin, PXmargin_top_bottom, PXmargin, PXmargin_top_bottom);
        noteCardView.setLayoutParams(noteLayout_params);
        return noteCardView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private LinearLayout createLinearLayout(int id) {
        LinearLayout noteLayout = new LinearLayout(context);
        LinearLayout.LayoutParams noteLayout_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0
                );
        noteLayout.setId(id);
        noteLayout.setFocusable(true);
        noteLayout.setClickable(true);
        noteLayout.setForeground(ContextCompat.getDrawable(context, R.drawable.note_tag_ripple_effect)); //need set Focusble and Clickble
        noteLayout.setOrientation(LinearLayout.VERTICAL);
        noteLayout.setLayoutParams(noteLayout_params);
        noteLayout.setOnClickListener(clickListener);
        return noteLayout;
    }

    private TextView createDateTextView(String date) {
        int PXpadding = convertDpToPixel(5);
        int PXmargin = convertDpToPixel(10);

        TextView newTextView = new TextView(context);
        LinearLayout.LayoutParams newTextView_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                );
        newTextView.setTypeface(Typeface.SANS_SERIF);
        newTextView.setTextSize(12);
        newTextView.setBackground(ContextCompat.getDrawable(context, R.drawable.bottom_border));
        newTextView.setGravity(Gravity.CENTER | Gravity.LEFT);
        newTextView_params.setMargins(PXmargin, PXmargin, PXmargin, PXmargin);
        newTextView.setPadding(PXpadding, 0, 0, 0);
        newTextView.setText(date);

        newTextView.setLayoutParams(newTextView_params);
        return newTextView;
    }

    private TextView createTitleTextView(String text) {
        int PXpadding = convertDpToPixel(5);
        int PXmargin_bottom = PXpadding;
        int PXmargin_left_right = convertDpToPixel(10);

        TextView newTextView = new TextView(context);
        LinearLayout.LayoutParams newTextView_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                );
        newTextView.setEllipsize(TextUtils.TruncateAt.END);
        newTextView.setMaxLines(1);
        newTextView.setTypeface(null, Typeface.BOLD);
        newTextView.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        newTextView.setGravity(Gravity.CENTER | Gravity.LEFT);
        newTextView_params.setMargins(PXmargin_left_right, 0, PXmargin_left_right, PXmargin_bottom);
        newTextView.setPadding(PXpadding, 0, 0, 0);
        newTextView.setText(text);

        newTextView.setLayoutParams(newTextView_params);
        return newTextView;
    }

    private TextView createBodyTextView(String text) {
        int PXpadding = convertDpToPixel(5);
        int PXmargin = convertDpToPixel(10);

        TextView newTextView = new TextView(context);
        LinearLayout.LayoutParams newTextView_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        3
                );
        newTextView.setTextColor(ContextCompat.getColor(context, android.R.color.black));
        newTextView.setEllipsize(TextUtils.TruncateAt.END);
        newTextView.setMaxLines(4);
        newTextView_params.setMargins(PXmargin, 0, PXmargin, 0);
        newTextView.setPadding(PXpadding, 0, 0, 0);
        newTextView.setText(text);

        newTextView.setLayoutParams(newTextView_params);
        return newTextView;
    }

    private TextView createTagsTextView(Tag[] tags) {
        int PXpadding = convertDpToPixel(5);
        int PXmargin = convertDpToPixel(10);
        int PXmargin_bottom = convertDpToPixel(7);

        TextView newTextView = new TextView(context);
        LinearLayout.LayoutParams newTextView_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0,
                        1
                );
        newTextView.setTypeface(Typeface.SANS_SERIF);
        newTextView.setTextSize(12);
        newTextView.setEllipsize(TextUtils.TruncateAt.END);
        newTextView.setMaxLines(1);
        newTextView.setBackground(ContextCompat.getDrawable(context, R.drawable.top_border));
        newTextView.setGravity(Gravity.CENTER | Gravity.LEFT);
        newTextView_params.setMargins(PXmargin, PXmargin, PXmargin, PXmargin_bottom);
        newTextView.setPadding(PXpadding, 0, 0, 0);
        newTextView.setText(getTagsString(tags));//TODO удалить

        newTextView.setLayoutParams(newTextView_params);
        return newTextView;
    }

    private int convertDpToPixel(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private String getTagsString(Tag[] tags) {//TODO удалить
        String[] tagsArrStr = new String[tags.length];
        for (int i = 0; i < tags.length; i++) {
            tagsArrStr[i] = tags[i].getName();
        }
        String tagsStr = Arrays.toString(tagsArrStr);
        return "Теги: " + tagsStr.substring(1, tagsStr.length() - 1) + ".";
    }
}
