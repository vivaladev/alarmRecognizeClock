//package vivaladev.com.dirtyclocky.alarmcontrol.handler;
//
//import android.app.AlarmManager;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public abstract class AlarmHandler {
//    public static List<Alarm> alarms = new ArrayList<>();
//    public static void registerAlarm(AlarmManager alarmMgr, Alarm alarm){
//        //alarm.setId(alarms.size());
//        alarms.add(alarm);
////        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(),
////                alarm.getRepeatTime(), alarm.getPendingIntent()); TODO: refactor this shit
//        throw new UnsupportedOperationException("Метод не реализован!");
//    }
//
//    public void unRegisterAll(AlarmManager alarmMgr){
//        for (Alarm alarm : alarms) {
//            unRegisterAlarm(alarmMgr, alarm);
//        }
//    }
//
//    public static void unRegisterAlarm(AlarmManager alarmMgr, Alarm alarm) {
//        //alarmMgr.cancel(alarm.);
//        //Удаление будильника из Alarm Manager, коллекции и базы
//        throw new UnsupportedOperationException("Метод не реализован!");
//
//    }
//
//    public static void loadAlarms() {
//        // Загрузка будильников из базы
//        throw new UnsupportedOperationException("Метод не реализован!");// Загрузка будильников из базы
//    }
//}
