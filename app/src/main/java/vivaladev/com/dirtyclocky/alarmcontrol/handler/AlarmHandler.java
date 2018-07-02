package vivaladev.com.dirtyclocky.alarmcontrol.handler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vivaladev.com.dirtyclocky.databaseProcessing.dao.DatabaseWrapper;
import vivaladev.com.dirtyclocky.databaseProcessing.entities.Alarm;
import vivaladev.com.dirtyclocky.ui.activities.MainActivity;



public abstract class AlarmHandler {
    //private static AlarmManager alarmManager;
    //private static PendingIntent pendingIntent;

    public static List<Alarm> alarms = new ArrayList<>();
    public static void registerAlarm(AlarmManager alarmMgr, Context context, Alarm alarm){
        alarms.add(alarm);
        //(Intent) - это механизм для описания одной операции - выбрать фотографию, отправить письмо, сделать звонок, запустить браузер...

        Date date = getDate(alarm.getTime());

        if(date == null || date.getTime() < System.currentTimeMillis()) {return;}

        Intent intent = new Intent("ilku.ru.alarmclock.alarmcontrol.receive.ALARM");
        intent.putExtra("requestCode", alarm.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getId(), intent, 0);

        int repeatingTime = 1000 * 60;//TODO repeating 1 min


        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, date.getTime(),
                repeatingTime, pendingIntent);
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

        try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "alarmBD")) {
            Alarm[] alarms = dbw.getAllAlarms();
            for (int i = 0; i < alarms.length; i++) {
                if(alarms[i].isAlarmOn()){
                    registerAlarm(alarmMgr, context, alarms[i]);
                }
                break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Date getDate(String time){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String dateDay = dateFormat.format(date);
        String fullDate = dateDay + " " +  time.replaceAll("\\s","") +":00";


        DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        Date dates = null;
        try{
            dates = format.parse(fullDate);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return  dates;
    }
}
