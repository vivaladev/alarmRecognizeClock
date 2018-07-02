package vivaladev.com.dirtyclocky.ui.activities;

import android.app.AlertDialog;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Bundle;

import vivaladev.com.dirtyclocky.R;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import vivaladev.com.dirtyclocky.alarmcontrol.handler.AlarmHandler;
import vivaladev.com.dirtyclocky.databaseProcessing.dao.DatabaseWrapper;
import vivaladev.com.dirtyclocky.databaseProcessing.entities.Alarm;
import vivaladev.com.dirtyclocky.recognizeProcessing.SoundRecognize;

public class AlarmClockActivity extends Activity {
    public static boolean isActive = false;

    private static AlarmClockActivity instance;

    private AlarmManager alarmMgr;
    private PendingIntent pendingIntent;
    private AlarmThread alarmThread;
    private Vibrator vibrator;
    private MediaPlayer mMediaPlayer;
    private int alarmID = -1;
    private Alarm alarm;
    private boolean longClick = false;

    private final static int MAX_VOLUME = 100;
    private final static int VOLUME_STEP = 15;//Шаг увеличения
    private int CURRENT_VOLUME = 10;
    private boolean soundIncrease = true;//Нарастающий звук

    //Recognizing
    private String userInputName;
    private MediaRecorder mediaRecorder;
    private File fileFromDB, fileFromRec;

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            alarmID = Integer.parseInt(extras.getString("requestCode"));
        } else {
            alarmID = -1;
        }

        try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "alarmBD")) {
            alarm = dbw.getAlarm(alarmID);
            soundIncrease = alarm.isAlarmIncreaseVolume();
        } catch (Exception e) {
            e.printStackTrace();
        }


        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "tag");
        //Осуществляем блокировку
        wl.acquire();//Это нужно чтобы не погас экран

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED// Выводим поверх экрана блокировки
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

        setContentView(R.layout.activity_alarm_clock);


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);

        //Разблокируем поток.
        wl.release();

        findViewById(R.id.buttonOff).setOnClickListener((buttonOffAlarm) -> {
            if (alarmID != -1) {
                sendToBroadcastReceiver(alarmID);
                try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "alarmBD")) {
                    Alarm alarm = dbw.getAlarm(alarmID);
                    AlarmHandler.unRegisterAlarm((AlarmManager) getSystemService(Context.ALARM_SERVICE), alarm, getApplicationContext());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            TextView textView = findViewById(R.id.textView);
            textView.setText("Do not sleep");
            //cancelAlarm();
            vibrator.vibrate(1000);
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            alarmThread.interrupt();
            finish();
        });

        findViewById(R.id.buttonDefer).setOnClickListener((buttonIgnor) -> {
            if (alarmID != -1) {
                sendToBroadcastReceiver(alarmID);
            }
            TextView textView = findViewById(R.id.textView);
            textView.setText("I'll be back");
            vibrator.vibrate(1000);
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            alarmThread.interrupt();
            finish();
        });

        findViewById(R.id.buttonOff).setOnLongClickListener(v -> {
            // TODO Auto-generated method stub
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            alarmThread.interrupt();

            if (isFile(alarm.getMusic()) && "Sound".equals(alarm.getMusic())) {
                AlertDialog.Builder builder;
                final String[] controls = {"Start Recording", "Stop Recording", "Recognize"};

                builder = new AlertDialog.Builder(this);
                builder.setTitle("Media recording")
                        .setCancelable(false)
                        .setPositiveButton("Done", (dialog, id) -> dialog.cancel())
                        .setSingleChoiceItems(controls, -1,
                                (dialog, item) -> {
                                    if (controls[item].equals("Start Recording")) {
                                        fileFromRec = new File(getFileName("RecordForRecognize"));
                                        mediaStartRec(fileFromRec);
                                    }
                                    if (controls[item].equals("Stop Recording")) {
                                        mediaStopRec();
                                    }
                                    if (controls[item].equals("Recognize")) {
                                        if (fileFromDB != null) {
                                            if (fileFromRec != null) {
                                                if (SoundRecognize.recognizeSound(fileFromDB, fileFromRec)) {
                                                    showMessage("Files are the same");
                                                } else {
                                                    showMessage("Files are different");
                                                }
                                            } else {
                                                showMessage("Specify fileFromRec");
                                            }
                                        } else
                                            showMessage("Specify fileFromDB");
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                prepareToRecognizeImageTouch(alarm.getMusic());
            }

            return true;
        });

        playSound(this, getAlarmUri());

        alarmThread = new AlarmThread();
        alarmThread.start();
    }

    private static final int GOT_IMAGE_TOUCH = 111;

    private void prepareToRecognizeImageTouch(String res) {
        Intent intent = new Intent(this, ImageComparingTouchActivity.class);
        List<String> resources = getResourcesFromDB(res);
        intent.putExtra("uriImage", resources.get(0));
        intent.putExtra("coords", resources.get(1));
        Toast.makeText(this, "uriImage " + resources.get(0) + " coords " + resources.get(1), Toast.LENGTH_LONG).show();
        startActivityForResult(intent, GOT_IMAGE_TOUCH);
    }

    private List<String> getResourcesFromDB(String fromDB) {
        Toast.makeText(this, fromDB, Toast.LENGTH_LONG).show();
        boolean toImagePath = true;
        StringBuilder path = new StringBuilder();
        StringBuilder coord = new StringBuilder();
        for (char item : fromDB.toCharArray()) {
            if (toImagePath) {
                path.append(item);
            }
            if (item == '|') {
                toImagePath = false;
            }
            if (!toImagePath) {
                coord.append(item);
            }
        }

        return Arrays.asList(path.toString().substring(0, path.length()-1), coord.toString().substring(1, coord.length()));
    }

    private boolean isFile(String method) {
        return !Pattern.matches(".*\\d[x]\\d.*]", method); // если не проходит по регексе на картинку, то это файл
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private File getFileByName(String filename) {
        File[] filesArray = Environment.getExternalStorageDirectory().listFiles();
        File res = null;

        for (File item : filesArray) {
            if (item.isFile()) {
                if (item.getName().equals(filename)) {
                    res = item;
                }
            }
        }

        return res;
    }

    private String getFileName(String userInputName) {
        return Environment.getExternalStorageDirectory() + "/" + userInputName + ".amr_nb";
    }

    private void mediaStartRec(File file) {
        try {
            releaseRecorder();

            if (file.exists()) {
                file.delete();
            }

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(file.getPath());
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
        fileFromDB = getFileByName(alarm.getMusic());
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }


    public static AlarmClockActivity getInstance() {
        return instance;
    }

    public void cancelAlarm() {
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //(Intent) - это механизм для описания одной операции - выбрать фотографию, отправить письмо, сделать звонок, запустить браузер...
        Intent intent = new Intent("ilku.ru.alarmclock.receive.ALARM");
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        alarmMgr.cancel(pendingIntent);//Отменяем будильник, связанный с интентом данного класса

        pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmMgr.cancel(pendingIntent);//Отменяем будильник, связанный с интентом данного класса

    }

    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setLooping(true);//
        setVolume(CURRENT_VOLUME);
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    public void sendToBroadcastReceiver(int alarmID) {
        Intent intent = new Intent("ilku.ru.alarmclock.alarmcontrol.receive.ALARM");
        intent.putExtra("offAlarm", String.valueOf(alarmID));
        sendBroadcast(intent);
    }

    private Uri getAlarmUri() {
        Uri alert = RingtoneManager
                .getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alert == null) {
            alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            }
        }
        return alert;
    }

    private class AlarmThread extends Thread {
        @Override
        public void run() {
            //isActive = true;
            final long[] pattern = {0, 2000, 1000};
            for (int i = 0; i < 20; i++) {
                if (!isInterrupted()) {
                    vibrator.vibrate(pattern, -1);
                    if (soundIncrease && CURRENT_VOLUME < MAX_VOLUME) {
                        CURRENT_VOLUME += VOLUME_STEP;
                        if (CURRENT_VOLUME > MAX_VOLUME) CURRENT_VOLUME = MAX_VOLUME;
                    }
                    setVolume(CURRENT_VOLUME);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        //System.out.println("Thread disable");
                        //Toast.makeText(AlarmClockActivity.this,"Поток завершен аварийно", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else return;
            }
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
            }
            if (!longClick) {
                if (alarmID != -1) {
                    sendToBroadcastReceiver(alarmID);
                }
                finish();
            }
        }
    }

    private void setVolume(int volume) {
        float vol = (float) (1 - (Math.log(MAX_VOLUME - volume) / Math.log(MAX_VOLUME)));
        mMediaPlayer.setVolume(vol, vol);
    }
}