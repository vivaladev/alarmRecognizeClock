package vivaladev.com.dirtyclocky.alarmcontrol.service;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import vivaladev.com.dirtyclocky.alarmcontrol.handler.AlarmHandler;
import vivaladev.com.dirtyclocky.alarmcontrol.receive.AlarmReceiver;
import vivaladev.com.dirtyclocky.databaseProcessing.entities.Alarm;


/**
 * Needed for background work
 */
public class AlarmService extends Service {

    private AlarmReceiver alarmReceiver;

    @Override
    public void onCreate() {
        System.out.println("Create AlarmService");
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter();//Регистрация AlarmReceiver
        intentFilter.addAction("ilku.ru.alarmclock.alarmcontrol.receive.ALARM");
        intentFilter.setPriority(100);
        alarmReceiver = new AlarmReceiver();
        registerReceiver(alarmReceiver, intentFilter);
        //Toast.makeText(this, "service onCreate", Toast.LENGTH_SHORT).show();
        //AlarmHandler.loadAlarms((AlarmManager) getSystemService(Context.ALARM_SERVICE), getApplicationContext());
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        //Toast.makeText(this, "service onStartCommand", Toast.LENGTH_SHORT).show();
        return START_STICKY;//Сервис будет перезапущен после того как был убит системой
    }

    @Override
    public void onDestroy() {
//        Toast.makeText(this, "Destroy AlarmService", Toast.LENGTH_SHORT).show();
        super.onDestroy();
        if (alarmReceiver != null) {
            unregisterReceiver(alarmReceiver);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    /*public AlarmService() {
        super("AlarmService");
    }*/

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//
//        *//*startRepeatingTimer();
//
//        while (true){
//            synchronized (this){
//                try {
//                    wait(5000);
//                    Vibrator v;
//                    v=(Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
//                    v.vibrate(1000);
//                    System.out.println("HW ANDROID SERVICE");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        long endTime = getAlarmDate().getTime();
//        System.out.println("endTime = " + endTime);
//        System.out.println("system time = " + System.currentTimeMillis());
//        System.out.println("Date time = " + Calendar.getInstance().getTime());
//
//        while (System.currentTimeMillis() < endTime) {
//            synchronized (this) {
//                try {
//                    wait(endTime - System.currentTimeMillis());
//                    System.out.println("HW ANDROID SERVICE");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    //TODO We take from the alarm
    private Date getAlarmDate() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 20);
        c.set(Calendar.MINUTE, 11);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }
}
