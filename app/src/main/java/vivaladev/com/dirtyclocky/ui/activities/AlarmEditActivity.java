package vivaladev.com.dirtyclocky.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import vivaladev.com.dirtyclocky.R;
import vivaladev.com.dirtyclocky.databaseProcessing.dao.DatabaseWrapper;

public class AlarmEditActivity extends AppCompatActivity {

    String initialDate;
    String initialTitle;
    String initialBody;

    ArrayList<Integer> additionTags;
    ArrayList<Integer> removalTags;
    int clickedNoteId;

    Menu toolbarMenu;
    TextView time_field;
    EditText title_field;
    EditText note_text_field;
    LinearLayout llBottomSheet;
    Toolbar edit_note_tool_bar;

    Button buttonUp;

    private int mYear, mMonth, mDay, mHour, mMinute;

    FloatingActionButton bottom_sheet_btn;
    int currentBottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;

    BottomSheetBehavior bottomSheetBehavior;

    private void init() {
        time_field = findViewById(R.id.time_field);
        title_field = findViewById(R.id.title_field);
        note_text_field = findViewById(R.id.note_text_field);
        edit_note_tool_bar = findViewById(R.id.edit_note_tool_bar);
        bottom_sheet_btn = findViewById(R.id.bottom_sheet_btn);
        buttonUp = findViewById(R.id.buttonUp);
        llBottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        additionTags = new ArrayList<>();
        removalTags = new ArrayList<>();
    }

    private void setInitialData() {
        initialDate = time_field.getText().toString();
        initialTitle = title_field.getText().toString();
        initialBody = note_text_field.getText().toString();
        removalTags.clear();
        additionTags.clear();
    }

    private void controlProcessing() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setTimeDialog();

            bottom_sheet_btn.setOnClickListener(view -> {
                if (currentBottomSheetState == BottomSheetBehavior.STATE_EXPANDED) {
                    bottom_sheet_btn.animate().scaleX(0).scaleY(0).setDuration(140).start();
                    buttonUp.animate().alpha(1).setDuration(400).start();
                    buttonUp.setVisibility(View.VISIBLE);
                    currentBottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });

            buttonUp.setOnClickListener(view -> {
                currentBottomSheetState = BottomSheetBehavior.STATE_EXPANDED;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                buttonUp.animate().alpha(0).setDuration(50).start();
            });
        }
    }

    private void backBtnDialog() {
        if (isNeedSave()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to save or delete this alarm?");
            builder.setNegativeButton(getResources().getString(R.string.en_alarm_del),
                    (dialog, which) -> finish());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                builder.setPositiveButton(getResources().getString(R.string.en_alarm_save),
                        (dialog, which) -> saveChanges());
            }
            builder.setNeutralButton(getResources().getString(R.string.en_alarm_cancel), null);
            builder.show();
        } else {
            if (getCurrentFocus() != null) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().
                                getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
            finish();
        }
    }

    private static void setStatusBar(Activity activity) {
        View someView = activity.findViewById(R.id.edit_note_tool_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            someView.setSystemUiVisibility(someView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, android.R.color.black));
        }
    }

    @Override
    public void onBackPressed() {
        backBtnDialog();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_edit_activity);
        init();
        controlProcessing();

        setStatusBar(this);
        setToolBar();

        bottom_sheet_btn.animate()
                .scaleX(0)
                .scaleY(0)
                .setDuration(0)
                .start();
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (currentBottomSheetState == BottomSheetBehavior.STATE_EXPANDED && BottomSheetBehavior.STATE_DRAGGING == newState) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    buttonUp.setVisibility(View.INVISIBLE);
                    bottom_sheet_btn.animate().scaleX(1).scaleY(1).setDuration(140).start();
                    currentBottomSheetState = BottomSheetBehavior.STATE_EXPANDED;
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note_tool_bar, menu);
        toolbarMenu = menu;
        return true;
    }


    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_btn: {
                saveChanges();
                break;
            }
            case R.id.remove_btn: {
                removeNoteDialog();
                break;
            }
            case android.R.id.home: {
                backBtnDialog();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void removeNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.en_alarm_message_back_btn))
                .setPositiveButton(getResources().getString(R.string.en_alarm_del),
                        (dialog, which) -> {
                            removeNote();
                            finish();
                        }).setNegativeButton(getResources().getString(R.string.en_alarm_cancel), null).show();
    }

    private void removeNote() {
        try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "myDB")) {
            dbw.removeNote(clickedNoteId);
            MainActivity.getInstance().getPagerAdapter().notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void saveChanges() {
        if (!isNeedSave()) {
            return;
        }
        try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "myDB")) {
            String date = time_field.getText().toString();
            String title = title_field.getText().toString();
            String body = note_text_field.getText().toString();

            if (clickedNoteId != -1) {
                dbw.updateNote(clickedNoteId, date, title, body);
                for (int i = 0; i < removalTags.size(); i++) {
                    dbw.removeTagFromNote(removalTags.get(i), clickedNoteId);
                }
                for (int i = 0; i < additionTags.size(); i++) {
                    dbw.addTagToNote(additionTags.get(i), clickedNoteId);
                }
            } else {
                int noteId = dbw.addNote(date, title, body);
                for (int i = 0; i < additionTags.size(); i++) {
                    dbw.addTagToNote(additionTags.get(i), noteId);
                }
            }
            setInitialData();
            showMessage(getResources().getString(R.string.en_alarm_message_save_changes));
            MainActivity.getInstance().getPagerAdapter().notifyDataSetChanged();

            SoundProcessingActivity soundProcessingActivity = SoundProcessingActivity.getInstance();
            if (soundProcessingActivity != null) {
                soundProcessingActivity.reload();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNeedSave() {
        String time = time_field.getText().toString();
        String title = title_field.getText().toString();
        String body = note_text_field.getText().toString();
        if (time.equals(initialDate) &&
                initialTitle.equals(title) &&
                initialBody.equals(body) &&
                additionTags.size() == 0 &&
                removalTags.size() == 0) {
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setTimeDialog() {
//        Alarm alarm = new Alarm();

        time_field.setInputType(InputType.TYPE_NULL);
        time_field.setOnClickListener(v -> {
            final java.util.Calendar calendar = java.util.Calendar.getInstance();
            mHour = calendar.get(java.util.Calendar.HOUR_OF_DAY); // set default time by current time
            mMinute = calendar.get(java.util.Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
//                alarm.setHours(hourOfDay);
//                alarm.setMinutes(minute);

                time_field.setText(hourOfDay + " : " + minute);
            }, mHour, mMinute, true);
            timePickerDialog.show();
        });
    }

    private void additionTag(int tagId) {
        TextView tag = (TextView) findViewById(tagId);
        tag.setBackgroundColor(getResources().getColor(R.color.selected_tag));
        tag.setTextColor(getResources().getColor(android.R.color.black));

        for (int i = 0; i < removalTags.size(); i++) {
            if (tagId == removalTags.get(i)) {
                removalTags.remove(i);
                return;
            }
        }
        additionTags.add(tagId);
    }

    private void removalTag(int tagId) {
        TextView tag = (TextView) findViewById(tagId);
        tag.setBackground(null);
        tag.setTextColor(getResources().getColor(android.R.color.black));

        for (int i = 0; i < additionTags.size(); i++) {
            if (tagId == additionTags.get(i)) {
                additionTags.remove(i);
                return;
            }
        }
        removalTags.add(tagId);
    }

    private boolean isSelectedTag(View v) {
        if (v.getBackground() != null) {
            return true;
        }
        return false;
    }

    private String getCurrentDate() {
        long curTime = System.currentTimeMillis();
        return new SimpleDateFormat("dd.MM.yyyy").format(curTime);
    }

    private SpannableString getUnderlinedText(String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
    }


    private void setToolBar() {
        setSupportActionBar(edit_note_tool_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        edit_note_tool_bar.setSubtitleTextColor(getResources().getColor(R.color.theme_color));
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}


