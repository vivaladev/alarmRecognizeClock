package vivaladev.com.dirtyclocky.ui.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.util.Random;

import vivaladev.com.dirtyclocky.R;
import vivaladev.com.dirtyclocky.recognizeProcessing.SoundRecognize;

import static vivaladev.com.dirtyclocky.ui.activities.activityHelper.ActivityHelper.setStatusBar;

public class SoundProcessingActivity extends AppCompatActivity implements View.OnClickListener {

    private String initialName;

    private int clickedTagId;

    private Menu toolbarMenu;
    private EditText musicEditField;
    private Toolbar edit_tag_tool_bar;

    private static SoundProcessingActivity instance;


    //    Recording
    private String fileNameInCD;
    private String userInputName;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    final int REQUEST_AUDIO_PERMISSION_RESULT = 1; // requestCode


    public static SoundProcessingActivity getInstance() {
        return instance;
    }

    /* CHECK PERMISSION MODULE */
    int checkPermRecord() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
    }

    int checkWriteSD() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    int checkReadSD() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    void getPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                REQUEST_AUDIO_PERMISSION_RESULT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_RESULT:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && checkReadSD() == PackageManager.PERMISSION_DENIED) {
                    //perm granted
                    recordingControls();
                } else {
                    getPermission();
                }
                return;
        }
    }

    private void recordingControls() {
        musicEditField.setOnClickListener(view -> {
            AlertDialog.Builder builder;
            final String[] controls = {"Start Recording", "Stop Recording", "Play record", "Stop playing"};

            builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose method of alarm stopping")
                    .setCancelable(false)
                    .setPositiveButton("Done", (dialog, id) -> dialog.cancel())
                    .setSingleChoiceItems(controls, -1,
                            (dialog, item) -> {
                                if (controls[item].equals("Start Recording")) {
                                    getFileName();
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

    private String getFileName(String userInputName) {
        return Environment.getExternalStorageDirectory() + "/" + userInputName + ".amr_nb";
    }

    private void getFileName() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.alert_music_get_name, null);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);
        mDialogBuilder.setView(promptsView);
        final EditText userInput = promptsView.findViewById(R.id.input_text);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Done",
                        (dialog1, id1) -> {
                            userInputName = userInput.getText().toString();
                            musicEditField.setText(userInputName);
                            mediaStartRec(getFileName(userInputName));
                            fileNameInCD = userInputName;
                        })
                .setNegativeButton("Cancel",
                        (dialog2, id2) -> {
                            userInputName = "Record" + new Random().nextInt(100000);
                            Toast.makeText(this, "Recording will be save with default name " + userInputName,
                                    Toast.LENGTH_LONG).show();
                            musicEditField.setText(userInputName);
                            dialog2.cancel();
                            mediaStartRec(getFileName(userInputName));
                            fileNameInCD = userInputName;
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    private void mediaStartRec(String fileName) {
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
            mediaPlayer.setDataSource(getFileName(fileNameInCD));
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(this, "Playing started", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, "Nothing to played. Record something :)", Toast.LENGTH_LONG).show();
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

    private void backBtnDialog() {
        if (isNeedSave()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want save or delete this record?");
            builder.setNegativeButton(getResources().getString(R.string.en_alarm_del),
                    (dialog, which) -> finish());
            builder.setNeutralButton(getResources().getString(R.string.en_alarm_cancel), null);
            builder.show();
        } else {
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        MainActivity.getInstance().initializeFragments();
        MainActivity.getInstance().getNotesFragment().setClickedNoteId(view.getId());
        Intent intent = new Intent(MainActivity.getInstance(), AlarmEditActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        backBtnDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_btn: {
                removeRecordDialog();
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
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_tag_tool_bar, menu);
        toolbarMenu = menu;
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sound_control_activity);

        instance = this;
        setActivitiesItems();
        setStatusBar(this, findViewById(R.id.edit_tag_tool_bar));
        setToolBar();

        if (checkPermRecord() == PackageManager.PERMISSION_GRANTED && checkWriteSD() == PackageManager.PERMISSION_GRANTED && checkReadSD() == PackageManager.PERMISSION_GRANTED) {
            // perm granted
            recordingControls();
        } else {
            Toast.makeText(this, "Permission is not granted. Some function may have errors", Toast.LENGTH_LONG).show();
            getPermission();
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void reload() {
        LinearLayout notes_linearLayout = findViewById(R.id.notes_linearLayout);
        notes_linearLayout.removeAllViews();
    }

    private boolean isNeedSave() {
        String name = musicEditField.getText().toString();
        if (name.equals(initialName)) {
            return false;
        }
        return true;
    }

    private void setActivitiesItems() {

        musicEditField = findViewById(R.id.tag_edit_field);
        edit_tag_tool_bar = findViewById(R.id.edit_tag_tool_bar);
    }

    private void setToolBar() {
        setSupportActionBar(edit_tag_tool_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        edit_tag_tool_bar.setSubtitleTextColor(getResources().getColor(R.color.theme_color));
    }

    private void removeRecordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.en_alarm_del_tag_message))
                .setPositiveButton(getResources().getString(R.string.en_alarm_del),
                        (dialog, which) -> {
                            removeRecord();
                            finish();
                        }).setNegativeButton(getResources().getString(R.string.en_alarm_cancel), null).show();
    }

    private void removeRecord() {
        File filename = new File(getFileName(fileNameInCD));
        if (filename.exists()) {
            filename.delete();
        }
    }

    private void setInitialData() {
        initialName = musicEditField.getText().toString();
    }
}
