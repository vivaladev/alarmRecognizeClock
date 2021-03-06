package vivaladev.com.dirtyclocky.databaseProcessing.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Anton on 06.04.2018.
 */

class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context, String dbName) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Alarm ("
                + "id integer primary key autoincrement,"
                + "time text,"
                + "name text,"
                + "body text" + ");");
        db.execSQL("create table Tag ("
                + "id integer primary key autoincrement,"
                + "name text" + ");");
        db.execSQL("create table TagItem ("
                + "id integer primary key autoincrement,"
                + "note_id integer,"
                + "tag_id integer" + ");");



        db.execSQL("create table Alarms ("
                + "id integer primary key autoincrement,"
                + "time text,"
                + "name text,"
                + "body text,"
                + "music text,"
                + "repeatTime text,"
                + "alarmOffMethod text,"
                + "alarmIncreaseVolume text,"
                + "alarmOnOff text" + ");"

        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}