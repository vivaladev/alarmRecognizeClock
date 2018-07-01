package vivaladev.com.dirtyclocky.alarmcontrol.handler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import vivaladev.com.dirtyclocky.databaseProcessing.dao.DatabaseWrapper;
import vivaladev.com.dirtyclocky.databaseProcessing.entities.Alarm;
import vivaladev.com.dirtyclocky.databaseProcessing.entities.Tag;
import vivaladev.com.dirtyclocky.ui.activities.MainActivity;



public abstract class AlarmHandler {
    //private static AlarmManager alarmManager;
    //private static PendingIntent pendingIntent;

    public static List<Alarm> alarms = new ArrayList<>();
    public static void registerAlarm(AlarmManager alarmMgr, Context context, Alarm alarm){
        //alarm.setId(alarms.size());
        alarms.add(alarm);
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(),
//                alarm.getRepeatTime(), alarm.getPendingIntent()); TODO: refactor this shit
        //(Intent) - это механизм для описания одной операции - выбрать фотографию, отправить письмо, сделать звонок, запустить браузер...

        Intent intent = new Intent("ilku.ru.alarmclock.alarmcontrol.receive.ALARM");
        intent.putExtra("requestCode", /*alarm.getId()*/ 5);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, /*alarm.getId()*/ 5, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + 5000);

        int repeatingTime = 1000 * 60;//TODO repeating

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                repeatingTime, pendingIntent);

        Toast.makeText(context, "AlarmHandler registerAlarm", Toast.LENGTH_SHORT).show();
        /*alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                repeatingTime+10000, p2);*/

        try{
            throw new RuntimeException("AlarmHandler registerAlarm");
        }
        catch (RuntimeException e){
            e.printStackTrace();
        }
    }

    public void unRegisterAll(AlarmManager alarmMgr, Context context){
        for (Alarm alarm : alarms) {
            unRegisterAlarm(alarmMgr, alarm, context);
        }
    }

    public static void unRegisterAlarm(AlarmManager alarmMgr, Alarm alarm, Context context) {
        //alarmMgr.cancel(alarm.);
        //Удаление будильника из Alarm Manager, коллекции и базы
        Intent intent = new Intent("ilku.ru.alarmclock.alarmcontrol.receive.ALARM");
        intent.putExtra("requestCode", alarm.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getId(), intent, 0);
        alarmMgr.cancel(pendingIntent);
    }

    public static void loadAlarms(AlarmManager alarmMgr, Context context) {
        // Загрузка будильников из базы

        try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "alarmDB")) {
            Alarm[] alarms = dbw.getAllAlarms();
            for (int i = 0; i < alarms.length; i++) {
                registerAlarm(alarmMgr, context, alarms[i]);
                break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
