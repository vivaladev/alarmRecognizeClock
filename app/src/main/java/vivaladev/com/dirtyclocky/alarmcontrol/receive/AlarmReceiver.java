package vivaladev.com.dirtyclocky.alarmcontrol.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;

import vivaladev.com.dirtyclocky.ui.activities.AlarmClockActivity;


public class AlarmReceiver extends BroadcastReceiver {
    public static ArrayList<MiniAlarm> alarmsRunning = new ArrayList<MiniAlarm>();
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getExtras().getString("offAlarm") != null){
            String alarmID = intent.getExtras().getString("offAlarm");
            setAlarmsOffByID(Integer.parseInt(alarmID));
            return;
        }

        int alarmID= intent.getIntExtra("requestCode", 1);

        Toast.makeText(context, "containts + "  + containsAlarmById(alarmID), Toast.LENGTH_LONG).show();

        if(isNotRunningAlarm(alarmID) && !AlarmClockActivity.isActive && !isRunningAlarms()){
            if(!containsAlarmById(alarmID)){
                alarmsRunning.add(new MiniAlarm(alarmID));
            }
            Intent intentone = new Intent(context.getApplicationContext(), AlarmClockActivity.class);//Старт активти
            intentone.putExtra("requestCode", String.valueOf(alarmID));
            intentone.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentone);
        }
    }
    public boolean isNotRunningAlarm(int alarmID){
        for(MiniAlarm alarm: alarmsRunning){
            if(alarm.getId() == alarmID && alarm.isRun()) {
                return false;
            }
        }
        return true;
    }
    public class MiniAlarm{
        private boolean run = true;
        private int id;

        public MiniAlarm(int id) {
            this.id = id;
        }

        public boolean isRun() {
            return run;
        }

        public void setRun(boolean run) {
            this.run = run;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static void setAlarmsOffByID(int alarmID){
        for(MiniAlarm alarm: alarmsRunning){
            if(alarm.getId() == alarmID){
                alarm.setRun(false);
                break;
            }
        }
    }
    public static boolean isRunningAlarms(){
        for(MiniAlarm alarm: alarmsRunning){
            if(alarm.isRun()) {
                return true;
            }
        }
        return false;
    }
    public static boolean containsAlarmById(int alarmID){
        for(MiniAlarm alarm: alarmsRunning){
            if(alarm.getId() == alarmID){
                return true;
            }
        }
        return false;
    }

    public static MiniAlarm getAlarmById(int alarmID){
        for(MiniAlarm alarm: alarmsRunning){
            if(alarm.getId() == alarmID){
                return alarm;
            }
        }
        return null;
    }
}
