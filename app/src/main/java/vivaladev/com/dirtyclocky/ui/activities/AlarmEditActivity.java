package vivaladev.com.dirtyclocky.ui.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import vivaladev.com.dirtyclocky.R;
import vivaladev.com.dirtyclocky.alarmcontrol.handler.AlarmHandler;
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
    private CheckBox isAlarmOnOff;
    private Toolbar edit_note_tool_bar;
    private FloatingActionButton bottom_sheet_btn;
    private BottomSheetBehavior bottomSheetBehavior;

    private String initialTime;
    private String initialName;
    private String initialBody;
    private String initialMusic;
    private String initialRepeatDays;
    private String initialOffMethod;
    private String initialIsIncrease;
    private String initialalarmOnOff;

    private int clickedAlarmId;
    private Alarm alarm;
    private String millisecondsTime;
    private int alarmHour, alarmMinute;
    private int currentBottomSheetState = BottomSheetBehavior.STATE_COLLAPSED;
    private boolean[] mCheckedDays = {false, false, false, false, false, false, false};


    private StringBuilder repeatDaysState;

    private void init() {
        time_field = findViewById(R.id.time_field);
        name_field = findViewById(R.id.name_field);
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
        isAlarmOnOff = findViewById(R.id.alarmOnOff);
        repeatDaysState = new StringBuilder();

        //clickedAlarmId = -1;

    }

    private void setDefaultData() {
        //TODO: подтягивать аларм с бд
    }

    private void setInitialData(String time, String name, String description, String musicName, String days, String offMethod, String isIncreases, String isOn) {
        initialTime = time;
        initialName = name;
        initialBody = description;
        initialMusic = musicName;
        initialRepeatDays = days;
        initialOffMethod = offMethod;
        initialIsIncrease = isIncreases;
        initialalarmOnOff = isOn;
    }

    private void setInitialData() {
        initialTime = time_field.getText().toString();
        initialName = name_field.getText().toString();
        initialBody = note_text_field.getText().toString();
        initialMusic = alarmOffMusic.getText().toString(); //TODO: вернуть музыку
        initialRepeatDays = alarmRepeat.getText().toString();
        initialOffMethod = alarmOffMethod.getText().toString();
        initialIsIncrease = isIncreaseVolume.isChecked() ? "1" : "0";
        initialalarmOnOff = isAlarmOnOff.isChecked() ? "1" : "0";
    }

    private String[] getConvertedFileName(File[] filenames) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < filenames.length; i++) {
            if (isValidateMusicFile(filenames[i])) {
                if (filenames[i].isFile()) {
                    res.add(filenames[i].getName());
                }
            }
        }
        return res.toArray(new String[res.size()]);
    }

    private boolean isValidateMusicFile(File file) {
        return Pattern.matches(".*\\.amr_nb", file.getName());
    }

    private File getFileByName(String filename) {
        return new File(filename);
    }

    private String getFileName(String userInputName) {
        return Environment.getExternalStorageDirectory() + "/" + userInputName;
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

        alarmOffMusic.setOnClickListener(view -> {
            final List<String> musicFiles = new ArrayList<>();
            File rootFolder = Environment.getExternalStorageDirectory();
            File[] filesArray = rootFolder.listFiles();
            String[] filenames = getConvertedFileName(filesArray);

            final String[] choosenFile = {""};
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose method of alarm stopping")
                    .setCancelable(false)
                    // добавляем одну кнопку для закрытия диалога
                    .setNeutralButton("Cancel",
                            (dialog, id) -> dialog.cancel())
                    .setPositiveButton("Done", (dialog, id) -> {
                        alarmOffMusic.setText(choosenFile[0]);
                        dialog.cancel();
                    })
                    // добавляем переключатели
                    .setSingleChoiceItems(filenames, -1,
                            (dialog, item) -> choosenFile[0] = filenames[item]);

            AlertDialog alert = builder.create();
            alert.setOnShowListener((dialogInterface) -> {
                ListView lv = alert.getListView();
                lv.setOnItemLongClickListener((adapterView, thisView, pos, id) -> {
                    try {
                        MediaPlayer mediaPlayer;
                        mediaPlayer = new MediaPlayer();
                        File file = new File(getFileName(choosenFile[0]));
                        mediaPlayer.setDataSource(file.getPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        TimeUnit.SECONDS.sleep(5);
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                        }
                    } catch (IOException | InterruptedException e) {
                        showMessage("Please, specify needed file");
                        e.printStackTrace();
                    }
                    return true;
                });
            });
            alert.show();
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
                        dialog.cancel();
                        if (choise.get(0).equals("Image")) {
                            alarmOffMusic.setClickable(false);
                            alarmOffMusic.setText("");
                            prepareToRecognizeImage();
                        }
                        if (choise.get(0).equals("Sound")) {
                            alarmOffMusic.setClickable(true);
                        }
                        alarmOffMethod.setText(choise.get(0));
                    })
                    // добавляем переключатели
                    .setSingleChoiceItems(methodsOff, -1,
                            (dialog, item) -> {
                                choise.set(0, methodsOff[item]);
                            });
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

    /**
     * Image processing
     */
    private static final int SELECT_PICTURE = 1;
    private static final int GOT_IMAGE_COORDS = 2;
    private String selectedImagePath;

    private void prepareToRecognizeImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();
            selectedImagePath = getPath(selectedImageUri);
            sendImageToNewActivity(selectedImageUri);
        }

        if (requestCode == GOT_IMAGE_COORDS && resultCode == RESULT_OK) {
            Intent intent = getIntent();
            String coords = ImageRecognizeActivity.getFinalCoords(); //intent.getStringExtra("imageCoords");
            alarmOffMusic.setText(selectedImagePath + "|" + coords);
        }
    }

    private void sendImageToNewActivity(Uri image) {
        Intent intent = new Intent(this, ImageRecognizeActivity.class);
        intent.putExtra("uriImage", image.toString());
        startActivityForResult(intent, GOT_IMAGE_COORDS);
    }

    public String getPath(Uri uri) {
        return uri.getPath();
    }

    /**
     * DONE
     */


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
        if (!isReadyToSave()) {
            goToBack();
            return;
        }
        if (!isLastVersionActive()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to save or delete this alarm?");
            builder.setNegativeButton(getResources().getString(R.string.en_alarm_del),
                    (dialog, which) -> {
                        finish();
                    });
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (isReadyToSave()) {
                    builder.setPositiveButton(getResources().getString(R.string.en_alarm_save),
                            (dialog, which) -> saveChanges());
                }
            }
            builder.setNeutralButton(getResources().getString(R.string.en_alarm_cancel), null);
            builder.show();
        } else {
            goToBack();
        }
    }

    private void goToBack() {
        if (getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus().
                            getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        finish();
    }

    private void removeActiveAlarm() {
        try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "alarmBD")) {
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

    private String getImageToSave(String image){
        String str = selectedImagePath + "." + image;
        return str;
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
        try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "alarmBD")) {

            String time = time_field.getText().toString();
            String name = name_field.getText().toString();
            String body = note_text_field.getText().toString();
            String music = alarmOffMusic.getText().toString();
            String offMethod = alarmOffMethod.getText().toString();
            String repeatTime = alarmRepeat.getText().toString();
            String alarmIncreaseVolume = isIncreaseVolume.isChecked() ? "1" : "0";
            String alarmOnOff = isAlarmOnOff.isChecked() ? "1" : "0";

            if (clickedAlarmId != -1) {
                dbw.updateAlarm(clickedAlarmId, time, name, body, music, repeatTime, offMethod, alarmIncreaseVolume, alarmOnOff);

                Intent intent = new Intent("ilku.ru.alarmclock.alarmcontrol.receive.ALARM");
                intent.putExtra("offAlarm", String.valueOf(clickedAlarmId));
                sendBroadcast(intent);

                Alarm al = new Alarm();
                al.setId(clickedAlarmId);
                al.setTime(time);
                al.setName(name);
                al.setMusic(music);
                al.setRepeatTime(repeatTime);
                al.setAlarmOffMethod(offMethod);
                al.setAlarmIncreaseVolume(alarmIncreaseVolume);
                al.setAlarmOnOff(alarmOnOff);
                AlarmHandler.unRegisterAlarm((AlarmManager) getSystemService(Context.ALARM_SERVICE), al, getApplicationContext());


                if ("1".equals(alarmOnOff)) {
                    AlarmHandler.registerAlarm((AlarmManager) getSystemService(Context.ALARM_SERVICE), getApplicationContext(), al);
                }
                /*for (int i = 0; i < removalTags.size(); i++) {
                    dbw.removeTagFromNote(removalTags.get(i), clickedNoteId);
                }
                for (int i = 0; i < additionTags.size(); i++) {
                    dbw.addTagToNote(additionTags.get(i), clickedNoteId);
                }*/
            } else {
                int alarmID = dbw.addAlarm(time, name, body, music, repeatTime, offMethod, alarmIncreaseVolume, alarmOnOff);
                if ("1".equals(alarmOnOff)) {
                    Alarm al = new Alarm();
                    al.setId(alarmID);
                    al.setTime(time);
                    al.setName(name);
                    al.setMusic(music); // путь к файлу музыки
                    al.setRepeatTime(repeatTime);
                    al.setAlarmOffMethod(offMethod);
                    al.setAlarmIncreaseVolume(alarmIncreaseVolume);
                    al.setAlarmOnOff(alarmOnOff);
                    AlarmHandler.registerAlarm((AlarmManager) getSystemService(Context.ALARM_SERVICE), getApplicationContext(), al);
                }
                /*for (int i = 0; i < additionTags.size(); i++) {
                    dbw.addTagToNote(additionTags.get(i), alarmID);//TODO добавление тегов к аларму
                }*/
            }
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

            setInitialData(time, name, body, music, repeatTime, offMethod, alarmIncreaseVolume, alarmOnOff);
            showMessage(getResources().getString(R.string.en_alarm_message_save_changes));


           /* SoundProcessingActivity soundProcessingActivity = SoundProcessingActivity.getInstance();
            if (soundProcessingActivity != null) {
                soundProcessingActivity.reload();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            MainActivity.getInstance().getPagerAdapter().notifyDataSetChanged();
            goToBack();
        }
    }

    private boolean isLastVersionActive() {
        String time = time_field.getText().toString();
        String name = name_field.getText().toString();
        String description = note_text_field.getText().toString();
        String music = alarmOffMusic.getText().toString();
        String repeatDays = alarmRepeat.getText().toString();
        String offMethod = alarmOffMethod.getText().toString();
        String isIncrease = isIncreaseVolume.isChecked() ? "1" : "0";
        String isOn = isAlarmOnOff.isChecked() ? "1" : "0";


        if (time.equals(initialTime) &&
                name.equals(initialName) &&
                description.equals(initialBody) &&
                music.equals(initialMusic) &&
                repeatDays.equals(initialRepeatDays) &&
                offMethod.equals(initialOffMethod) &&
                isIncrease.equals(initialIsIncrease) &&
                isOn.equals(initialalarmOnOff)) {
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
                String hour = String.valueOf(hourOfDay);
                if (hour.length() == 1) {
                    hour = "0" + hourOfDay;
                }
                String min = String.valueOf(minute);
                if (min.length() == 1) {
                    min = "0" + min;
                }
                time_field.setText(hour + ":" + min);
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
        setAlarmData();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarmData() {
        Alarm alarm;
        //Tag[] allTags;
        //Tag[] noteTags;
        clickedAlarmId = MainActivity.getInstance().getNotesFragment().getClickedNoteId();//TODO переименовать в Note Handler
        LinearLayout tags_linearLayout = (LinearLayout) findViewById(R.id.tags_linearLayout);
        //RecordFactory tg = new RecordFactory(this, tags_linearLayout, this);

        if (clickedAlarmId != -1) {
            toolbarMenu.findItem(R.id.remove_btn).setVisible(true);
            try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "alarmBD")) {
                alarm = dbw.getAlarm(clickedAlarmId);
                time_field.setText(getUnderlinedText(alarm.getTime()));
                name_field.setText(alarm.getName());
                note_text_field.setText(alarm.getBody());
                //repeatDaysState.append();//TODO ЧЕКНУТЬ
                alarmOffMusic.setText(alarm.getMusic());
                alarmRepeat.setText(alarm.getRepeatTime());
                alarmOffMethod.setText(alarm.getAlarmOffMethod());
                isIncreaseVolume.setChecked(alarm.isAlarmIncreaseVolume());
                isAlarmOnOff.setChecked(alarm.isAlarmOn());

                //TODO

                /*allTags = dbw.getAllTags();
                noteTags = dbw.getTagsByNoteId(alarm.getId());

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
            try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "alarmBD")) {
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
