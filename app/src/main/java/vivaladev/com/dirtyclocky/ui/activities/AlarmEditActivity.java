package vivaladev.com.dirtyclocky.ui.activities;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vivaladev.com.dirtyclocky.R;
import vivaladev.com.dirtyclocky.databaseProcessing.dao.DatabaseWrapper;
import vivaladev.com.dirtyclocky.databaseProcessing.entities.Alarm;

import static vivaladev.com.dirtyclocky.ui.activities.activityHelper.ActivityHelper.setStatusBar;

public class AlarmEditActivity extends AppCompatActivity {

    private ArrayList<Integer> additionTags;//del
    private ArrayList<Integer> removalTags;//del

    private Menu toolbarMenu;
    private LinearLayout llBottomSheet;
    private Button buttonUp;
    private TextView time_field;
    private EditText name_field;
    private EditText note_text_field;
    private EditText alarmOffMusic;
    private EditText alarmRepeat;
    private EditText alarmOffMethod;
    private CheckBox isIncreaseVolume;
    private Toolbar edit_note_tool_bar;
    private FloatingActionButton bottom_sheet_btn;
    private BottomSheetBehavior bottomSheetBehavior;

    private String initialTime;
    private String initialName;
    private String initialBody;
    //private String initialMusic;
    private String initialRepeatDays;
    private String initialOffMethod;
    private String initialIsIncrease;

    private int clickedAlarmId;
    private Alarm alarm;
    private String millisecondsTime;
    private int alarmHour, alarmMinute;
    private int currentBottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;
    private boolean[] mCheckedDays = {false, false, false, false, false, false, false};


    private StringBuilder repeatDaysState;

    private void init() {
        time_field = findViewById(R.id.time_field);
        name_field = findViewById(R.id.title_field);
        note_text_field = findViewById(R.id.note_text_field);
        edit_note_tool_bar = findViewById(R.id.edit_note_tool_bar);
        bottom_sheet_btn = findViewById(R.id.bottom_sheet_btn);
        buttonUp = findViewById(R.id.buttonUp);
        llBottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        alarmOffMusic = findViewById(R.id.alarmOffMusic);
        alarmRepeat = findViewById(R.id.alarmRepeat);
        alarmOffMethod = findViewById(R.id.alarmOffMethod);
        isIncreaseVolume = findViewById(R.id.alarmIncreaseVolume);
        repeatDaysState = new StringBuilder();

        //clickedAlarmId = -1;

    }

    private void setDefaultData() {
        //TODO: подтягивать аларм с бд
    }

    private void setInitialData(String time, String name, String description, String musicName, String days, String offMethod, String isIncreases) {
        initialTime = time;
        initialName = name;
        initialBody = description;
        //initialMusic = musicName;
        initialRepeatDays = days;
        initialOffMethod = offMethod;
        initialIsIncrease = isIncreases;
    }

    private void setInitialData() {
        initialTime = time_field.getText().toString();
        initialName = name_field.getText().toString();
        initialBody = note_text_field.getText().toString();
        //initialMusic = alarmOffMusic.toString(); //TODO: вернуть музыку
        initialRepeatDays = alarmRepeat.getText().toString();
        initialOffMethod = alarmOffMethod.getText().toString();
        initialIsIncrease = isIncreaseVolume.isChecked() ? "1" : "0";
    }

    private void upSheetControl() {
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

        alarmOffMethod.setOnClickListener(view -> {
            AlertDialog.Builder builder;
            final String[] methodsOff = {"Image", "Sound"};
            final List<String> choise = Arrays.asList("");
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose method of alarm stopping")
                    .setCancelable(false)

                    // добавляем одну кнопку для закрытия диалога
                    .setNeutralButton("Cancel",
                            (dialog, id) -> dialog.cancel())
                    .setPositiveButton("Done", (dialog, id) -> {
                        alarmOffMethod.setText(choise.get(0));
                        dialog.cancel();
                    })
                    // добавляем переключатели
                    .setSingleChoiceItems(methodsOff, -1,
                            (dialog, item) -> choise.set(0, methodsOff[item]));
            AlertDialog alert = builder.create();
            alert.show();
        });

        alarmRepeat.setOnClickListener(view -> { //выбор дней повторений
            AlertDialog.Builder builder;
            final String[] daysToRepeat = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

            builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose days for repeat alarm")
                    .setCancelable(false)
                    .setMultiChoiceItems(daysToRepeat, mCheckedDays,
                            (dialog, which, isChecked) -> mCheckedDays[which] = isChecked)
                    // Добавляем кнопки
                    .setPositiveButton("Done",
                            (dialog, id) -> {
                                repeatDaysState = new StringBuilder();
                                for (int i = 0; i < daysToRepeat.length; i++) {
                                    if (mCheckedDays[i])
                                        repeatDaysState.append(1);
                                    else
                                        repeatDaysState.append(0);
                                }
                                alarmRepeat.setText("");
                                alarmRepeat.setText(formatRepeatDaysString(daysToRepeat, repeatDaysState.toString().toCharArray()));
                            })

                    .setNegativeButton("Cancel",
                            (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        });
    }

    private String formatRepeatDaysString(String[] daysToRepeat, char[] choosenDays) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < daysToRepeat.length; i++) {
            if (choosenDays[i] == '1') {
                result.append(getAbbreviationDay(daysToRepeat[i].toCharArray())).append(" ");
            }
        }
        return result.toString();
    }

    private String getAbbreviationDay(char[] day) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            res.append(day[i]);
        }
        return res.toString();
    }

    private void controlProcessing() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            setTimeDialog();
            upSheetControl();
        }
    }

    private void backBtnDialog() {
        if (!isLastVersionActive()) {
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

    private void removeActiveAlarm() {
        try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "alarmDB")) {
            dbw.removeNote(clickedAlarmId);
            MainActivity.getInstance().getPagerAdapter().notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isReadyToSave() {
        if (isEmpty(time_field) || isEmpty(name_field)) {
            return false;
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void saveChanges() {
        if (!isReadyToSave()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.en_alarm_message_fill_fields))
                    .setNegativeButton(getResources().getString(R.string.en_alarm_ok), null).show();
            return;
        }

        Toast.makeText(this, "Saving", Toast.LENGTH_SHORT).show();
        try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "alarmDB")) {

            String time = time_field.getText().toString();
            String name = name_field.getText().toString();
            String body = note_text_field.getText().toString();
            String music = alarmOffMusic.toString(); //TODO: вернуть музыку
            String repeatTime = alarmRepeat.getText().toString();
            String offMethod = alarmOffMethod.getText().toString();
            String alarmIncreaseVolume = isIncreaseVolume.isChecked() ? "1" : "0";

            if (clickedAlarmId != -1) {
                dbw.updateAlarm(clickedAlarmId, time, name, body, music, repeatTime, offMethod, alarmIncreaseVolume);
                /*for (int i = 0; i < removalTags.size(); i++) {
                    dbw.removeTagFromNote(removalTags.get(i), clickedNoteId);
                }
                for (int i = 0; i < additionTags.size(); i++) {
                    dbw.addTagToNote(additionTags.get(i), clickedNoteId);
                }*/
            } else {
                int alarmID = dbw.addAlarm(time, name, body, music, repeatTime, offMethod, alarmIncreaseVolume);
                try{
                    throw new RuntimeException("noteId = " + alarmID);
                }
                catch (RuntimeException e){
                    e.printStackTrace();
                }
                /*for (int i = 0; i < additionTags.size(); i++) {
                    dbw.addTagToNote(additionTags.get(i), alarmID);//TODO добавление тегов к аларму
                }*/
            }
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

            setInitialData(time, name, body, music, repeatTime, offMethod, alarmIncreaseVolume);
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

    private boolean isLastVersionActive() {
        String time = time_field.getText().toString();
        String name = name_field.getText().toString();
        String description = note_text_field.getText().toString();
        //String music = alarmOffMusic.getText().toString();
        String repeatDays = alarmRepeat.getText().toString();
        String offMethod = alarmOffMethod.getText().toString();
//        String isIncrease = isIncreaseVolume.isChecked() ? "1" : "0";

        try{
            throw new RuntimeException(
                            "time = " + time.equals(initialTime) +
                            "name = " + name.equals(initialName) +
                            "description = " + description.equals(initialBody) +
                            //"music = " + music.equals(initialMusic) +
                            "repeatDays = " + repeatDays.equals(initialRepeatDays) +
                            "offMethod = " + offMethod.equals(initialOffMethod)
            );
        }
        catch (RuntimeException e){
            e.printStackTrace();
        }

        if (time.equals(initialTime) &&
                name.equals(initialName) &&
                description.equals(initialBody) &&
                //music.equals(initialMusic) &&
                repeatDays.equals(initialRepeatDays) &&
                offMethod.equals(initialOffMethod)) {
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void setTimeDialog() {
        final java.util.Calendar calendar = java.util.Calendar.getInstance();

        time_field.setInputType(InputType.TYPE_NULL);
        time_field.setOnClickListener(v -> {
            alarmHour = calendar.get(java.util.Calendar.HOUR_OF_DAY); // set default time by current time
            alarmMinute = calendar.get(java.util.Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                millisecondsTime = String.valueOf(calendar.getTimeInMillis());
                time_field.setText(hourOfDay + " : " + minute);
            }, alarmHour, alarmMinute, true);
            timePickerDialog.show();
        });
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

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_note_tool_bar, menu);
        toolbarMenu = menu;
        setNoteData();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setNoteData() {
        Alarm note;
        //Tag[] allTags;
        //Tag[] noteTags;
        clickedAlarmId = MainActivity.getInstance().getNotesFragment().getClickedNoteId();//TODO переименовать в Note Handler
        LinearLayout tags_linearLayout = (LinearLayout) findViewById(R.id.tags_linearLayout);
        //TagsFactory tg = new TagsFactory(this, tags_linearLayout, this);

        if (clickedAlarmId != -1) {
            toolbarMenu.findItem(R.id.remove_btn).setVisible(true);
            try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "alarmDB")) {
                note = dbw.getAlarm(clickedAlarmId);
                time_field.setText(getUnderlinedText(note.getTime()));
                name_field.setText(note.getName());
                note_text_field.setText(note.getBody());
                //TODO

                /*allTags = dbw.getAllTags();
                noteTags = dbw.getTagsByNoteId(note.getId());

                for (int i = 0; i < allTags.length; i++) {
                    if (isTagBelongNote(allTags[i].getId(), noteTags)) {
                        tg.addTagToScreen(allTags[i].getId(), allTags[i].getName(), true);
                    } else {
                        tg.addTagToScreen(allTags[i].getId(), allTags[i].getName(), false);
                    }
                }*/

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            toolbarMenu.findItem(R.id.remove_btn).setVisible(false);
            try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "alarmDB")) {
                time_field.setText("");
                name_field.setText("");
                note_text_field.setText("");
                //TODO

                /*allTags = dbw.getAllTags();
                for (int i = 0; i < allTags.length; i++) {
                    tg.addTagToScreen(allTags[i].getId(), allTags[i].getName(), false);
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setInitialData();
    }

    private SpannableString getUnderlinedText(String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.en_alarm_message_back_btn))
                        .setPositiveButton(getResources().getString(R.string.en_alarm_del),
                                (dialog, which) -> {
                                    removeActiveAlarm();
                                    finish();
                                }).setNegativeButton(getResources().getString(R.string.en_alarm_cancel), null).show();
                break;
            }
            case android.R.id.home: {
                backBtnDialog();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
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
        setDefaultData(); // default values
        setStatusBar(this, findViewById(R.id.edit_note_tool_bar));
        setToolBar();
        controlProcessing();

    }

    private static boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    private static boolean isEmpty(TextView etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }
}


