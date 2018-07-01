package vivaladev.com.dirtyclocky.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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

import java.io.File;
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

    private String initialDate;
    private String initialTitle;
    private String initialBody;

    private int clickedAlarmId;
    private Alarm alarm;
    private String millisecondsTime;
    private int alarmHour, alarmMinute;
    private int currentBottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;
    private boolean[] mCheckedDays = {false, false, false, false, false, false, false};

    final int REQUEST_AUDIO_PERMISSION_RESULT = 1; // requestCode
    private String fileName;
    private StringBuilder repeatDaysState;


    //    Recording
    private AudioRecord audioRecord;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;


    /* CHECK PERMISSION MODULE */
    int checkPermRecord() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
    }

    int checkWriteSD() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    void getPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                REQUEST_AUDIO_PERMISSION_RESULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_RESULT:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //perm granted
                    fileName = Environment.getExternalStorageDirectory() + "/record.amr_nb";
                    recordingControls();
                } else {
                    getPermission();
                }
                return;
        }
    }

    private void recordingControls() {
        alarmOffMusic.setOnClickListener(view -> {
            AlertDialog.Builder builder;
            final String[] controls = {"Start Recording", "Stop Recording", "Play record", "Stop playing"};

            builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose method of alarm stopping")
                    .setCancelable(false)

                    // добавляем одну кнопку для закрытия диалога
                    .setNeutralButton("Cancel",
                            (dialog, id) -> dialog.cancel())
                    .setPositiveButton("Done", (dialog, id) -> {
                        alarmOffMusic.setText("Record №" + alarm.getId());
                        dialog.cancel();
                    })
                    // добавляем переключатели
                    .setSingleChoiceItems(controls, -1,
                            (dialog, item) -> {
                                if (controls[item].equals("Start Recording")) {
                                    mediaStartRec();
                                }
                                if (controls[item].equals("Stop Recording")) {
                                    mediaStopRec();
                                }
                                if (controls[item].equals("Play record")) {
                                    mediaStartRead();
                                }
                                if (controls[item].equals("Stop playing")) {
                                    mediaStopRead();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        });
    }


    void createAudioRecorder() {
        int sampleRate = 8000;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

        int minInternalBufferSize = AudioRecord.getMinBufferSize(sampleRate,
                channelConfig, audioFormat);
        int internalBufferSize = minInternalBufferSize * 4;

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                sampleRate, channelConfig, audioFormat, internalBufferSize);
    }


    private void mediaStartRec() {
        try {
            releaseRecorder();

            File outFile = new File(fileName);
            if (outFile.exists()) {
                outFile.delete();
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(fileName);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(this, "Recording started", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mediaStopRec() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            Toast.makeText(this, "Recording stoped", Toast.LENGTH_SHORT).show();
        }
    }

    private void mediaStartRead() {
        try {
            releasePlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fileName);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "Playing started", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mediaStopRead() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            Toast.makeText(this, "Playing stoped", Toast.LENGTH_SHORT).show();
        }
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        releaseRecorder();
    }

    /*END PERMISSION MODULE*/

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

    }

    private void setDefaultData() {
        //TODO: подтягивать аларм с бд
    }

    private void setInitialData() {
        initialDate = time_field.getText().toString();
        initialTitle = name_field.getText().toString();
        initialBody = note_text_field.getText().toString();
        removalTags.clear();
        additionTags.clear();
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
        if (isLastVersionActive()) {
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
        try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "myDB")) {
            dbw.removeNote(clickedAlarmId);
            MainActivity.getInstance().getPagerAdapter().notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isReadyToSave() {
        if ("".equals(time_field.getText()) && "".equals(name_field.getText())) {
            return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void saveChanges() {
        if (!isReadyToSave()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.en_alarm_message_fill_fields))
                    .setNegativeButton(getResources().getString(R.string.en_alarm_ok), null).show();
            return;
        }

        String time = millisecondsTime;
        String name = name_field.getText().toString();
        String body = note_text_field.getText().toString();
        String musicNameOnSDCard = fileName;
        String repeatDaysState = alarmRepeat.getText().toString();
        String offMethod = alarmOffMethod.getText().toString();
        boolean isIncrease = isIncreaseVolume.isChecked();

        alarm.setTime(time);
        alarm.setName(name);
        alarm.setBody(body);


//        try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "myDB")) {
//
//            String music = "";//TODO ЗАПОЛНИТЬ
//            String repeatTime = "";//TODO ЗАПОЛНИТЬ
//            String alarmOffMethod = "";//TODO ЗАПОЛНИТЬ
//            String alarmIncreaseVolume = "";//TODO ЗАПОЛНИТЬ
//
//            if (clickedAlarmId != -1) {
//                dbw.updateAlarm(clickedAlarmId, date, title, body, music, repeatTime, alarmOffMethod, alarmIncreaseVolume);
//                /*for (int i = 0; i < removalTags.size(); i++) {
//                    dbw.removeTagFromNote(removalTags.get(i), clickedNoteId);
//                }
//                for (int i = 0; i < additionTags.size(); i++) {
//                    dbw.addTagToNote(additionTags.get(i), clickedNoteId);
//                }*/
//            } else {
//                int alarmID = dbw.addAlarm(date, title, body, music, repeatTime, alarmOffMethod, alarmIncreaseVolume);
//                for (int i = 0; i < additionTags.size(); i++) {
//                    dbw.addTagToNote(additionTags.get(i), alarmID);
//                }
//            }
//            setInitialData();
//            showMessage(getResources().getString(R.string.en_alarm_message_save_changes));
//            MainActivity.getInstance().getPagerAdapter().notifyDataSetChanged();
//
//            SoundProcessingActivity soundProcessingActivity = SoundProcessingActivity.getInstance();
//            if (soundProcessingActivity != null) {
//                soundProcessingActivity.reload();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private boolean isLastVersionActive() {
        String time = time_field.getText().toString();
        String name = name_field.getText().toString();
        String description = note_text_field.getText().toString();

        if (time.equals(initialDate) &&
                initialTitle.equals(name) &&
                initialBody.equals(description) &&
                additionTags.size() == 0 &&
                removalTags.size() == 0) {
            return false;
        }
        return true;
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
        createAudioRecorder();
        setStatusBar(this, findViewById(R.id.edit_note_tool_bar));
        setToolBar();
        controlProcessing();
        if (checkPermRecord() == PackageManager.PERMISSION_GRANTED && checkWriteSD() == PackageManager.PERMISSION_GRANTED) {
            // perm granted
            fileName = Environment.getExternalStorageDirectory() + "/record.amr_nb";
            recordingControls();
        } else {
            Toast.makeText(this, "Permission is not granted. Some function may have errors", Toast.LENGTH_LONG).show();
            getPermission();
        }
    }
}


