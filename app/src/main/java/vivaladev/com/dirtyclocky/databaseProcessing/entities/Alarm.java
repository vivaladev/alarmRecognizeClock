package vivaladev.com.dirtyclocky.databaseProcessing.entities;

public class Alarm {
    private int id;
    private String time;
    private String name;
    private String body;
    private String music;
    private String repeatTime;
    private String alarmOffMethod;
    private boolean alarmIncreaseVolume;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getRepeatTime() {
        return repeatTime;
    }

    public void setRepeatTime(String repeatTime) {
        this.repeatTime = repeatTime;
    }

    public String getAlarmOffMethod() {
        return alarmOffMethod;
    }

    public void setAlarmOffMethod(String alarmOffMethod) {
        this.alarmOffMethod = alarmOffMethod;
    }

    public boolean isAlarmIncreaseVolume() {
        return alarmIncreaseVolume;
    }

    public void setAlarmIncreaseVolume(boolean alarmIncreaseVolume) {
        this.alarmIncreaseVolume = alarmIncreaseVolume;
    }
}
