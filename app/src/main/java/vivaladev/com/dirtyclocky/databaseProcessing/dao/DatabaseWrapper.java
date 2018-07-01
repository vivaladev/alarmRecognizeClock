package vivaladev.com.dirtyclocky.databaseProcessing.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import vivaladev.com.dirtyclocky.databaseProcessing.entities.Alarm;
import vivaladev.com.dirtyclocky.databaseProcessing.entities.Tag;
import vivaladev.com.dirtyclocky.databaseProcessing.entities.TagItem;

/**
 * Created by Anton on 06.04.2018.
 */

public class DatabaseWrapper implements AutoCloseable {

    private DBHelper _connection;
    private SQLiteDatabase db;
    private String _dbName;

    public DatabaseWrapper(Context context, String dbName) {
        this._dbName = dbName;
        try {
            _connection = new DBHelper(context, dbName);
            db = _connection.getWritableDatabase();
            Log.d("myLog", "Соединение с бд (" + dbName + ") установлено.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Alarm getAlarm(int alarmId) {
        Cursor cursor;
        Alarm alarm = new Alarm();
        cursor = db.query("Alarms", null, "id = " + alarmId, null, null, null, null);
        if (cursor.moveToFirst()) {

            int idColIndex = cursor.getColumnIndex("id");
            int timeColIndex = cursor.getColumnIndex("time");
            int nameColIndex = cursor.getColumnIndex("name");
            int bodyColIndex = cursor.getColumnIndex("body");
            int musicColIndex = cursor.getColumnIndex("music");
            int repeatTimeColIndex = cursor.getColumnIndex("repeatTime");
            int alarmOffMethodColIndex = cursor.getColumnIndex("alarmOffMethod");
            int alarmIncreaseVolumeColIndex = cursor.getColumnIndex("alarmIncreaseVolume");
            alarm.setId(cursor.getInt(idColIndex));
            alarm.setTime(cursor.getString(timeColIndex));
            alarm.setName(cursor.getString(nameColIndex));
            alarm.setBody(cursor.getString(bodyColIndex));
            alarm.setMusic(cursor.getString(musicColIndex));
            alarm.setRepeatTime(cursor.getString(repeatTimeColIndex));
            alarm.setAlarmOffMethod(cursor.getString(alarmOffMethodColIndex));
            alarm.setAlarmIncreaseVolume(cursor.getString(alarmIncreaseVolumeColIndex));
            Log.d("Get alarm", String.format("Заметки с id = %1$s найдена", alarmId));
        } else {
            cursor.close();
        }
        return alarm;
    }

    public Alarm[] getAllAlarms() {
        Cursor cursor;
        ArrayList<Alarm> alarms = new ArrayList<Alarm>();

        cursor = db.query("Alarms", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            Alarm alarm;
            int idColIndex = cursor.getColumnIndex("id");
            int timeColIndex = cursor.getColumnIndex("time");
            int nameColIndex = cursor.getColumnIndex("name");
            int bodyColIndex = cursor.getColumnIndex("body");
            int musicColIndex = cursor.getColumnIndex("music");
            int repeatTimeColIndex = cursor.getColumnIndex("repeatTime");
            int alarmOffMethodColIndex = cursor.getColumnIndex("alarmOffMethod");
            int alarmIncreaseVolumeColIndex = cursor.getColumnIndex("alarmIncreaseVolume");
            do {
                alarm = new Alarm();
                alarm.setId(cursor.getInt(idColIndex));
                alarm.setTime(cursor.getString(timeColIndex));
                alarm.setName(cursor.getString(nameColIndex));
                alarm.setBody(cursor.getString(bodyColIndex));
                alarm.setMusic(cursor.getString(musicColIndex));
                alarm.setRepeatTime(cursor.getString(repeatTimeColIndex));
                alarm.setAlarmOffMethod(cursor.getString(alarmOffMethodColIndex));
                alarm.setAlarmIncreaseVolume(cursor.getString(alarmIncreaseVolumeColIndex));
                alarms.add(alarm);
            } while (cursor.moveToNext());
        } else {
            cursor.close();
        }
        Log.d("Get all alarms", String.format("Найдено %1$s alarms", alarms.size()));
        printLogAllNotes(alarms);
        return alarms.toArray(new Alarm[alarms.size()]);
    }

    public int addAlarm(String time, String name, String body, String music, String repeatTime, String alarmOffMethod, String alarmIncreaseVolume) {
        ContentValues cv = new ContentValues();

        cv.put("time", time);
        cv.put("name", name);
        cv.put("body", body);
        cv.put("music", music);
        cv.put("repeatTime", repeatTime);
        cv.put("alarmOffMethod", alarmOffMethod);
        cv.put("alarmIncreaseVolume", alarmIncreaseVolume);

        int noteId = (int) db.insert("Alarms", null, cv);

        if (noteId > 0) {
            Log.d("Add alarm", String.format("Заметка добавлена(noteId = %1$s)", noteId));
        } else {
            Log.e("Add alarm", String.format("Что-то пошло не так(noteId = %1$s)", noteId));
        }
        return noteId;
    }

    public void updateAlarm(int alarmID, String time, String name, String body, String music, String repeatTime, String alarmOffMethod, String alarmIncreaseVolume) {
        ContentValues cv = new ContentValues();

        cv.put("time", time);
        cv.put("name", name);
        cv.put("body", body);
        cv.put("music", music);
        cv.put("repeatTime", repeatTime);
        cv.put("alarmOffMethod", alarmOffMethod);
        cv.put("alarmIncreaseVolume", alarmIncreaseVolume);

        int updCount = db.update("Alarms", cv, "id = " + alarmID, null);
        Log.d("Update alarms", getAlarm(alarmID).getMusic());
        Log.d("Update alarm", String.format("Количество обновлённых строк %1$s", updCount));
    }

    public void removeNote(int alarmID) {
        //int delTagItemCount = db.delete("TagItem", "note_id = " + alarmID, null);
        int delAlarmCount = db.delete("Alarms", "id = " + alarmID, null);
        Log.d("Remove alarm", String.format("Количество удалённых строк %1$s (Alarm)", delAlarmCount));
        //Log.d("Remove alarm", String.format("Количество удалённых строк %1$s (TagItem)", delTagItemCount));
    }

    public void close() {
        try {
            _connection.close();
            _connection = null;
            Log.d("Close connection", "Соединение закрыто");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void printLogAllTagItems(ArrayList<TagItem> arrTI) {
        Log.d("Tag items", "-------------------------------------------------");
        Log.d("Tag items", "id    noteId    tagId");
        for (int i = 0; i < arrTI.size(); i++) {
            Log.d("Tag items", String.format(
                    "%1$s    %2$s    %3$s",
                    arrTI.get(i).getId(),
                    arrTI.get(i).getNoteId(),
                    arrTI.get(i).getTagId())
            );
        }
        Log.d("Tag items", "-------------------------------------------------");
    }

    private void printLogAllTags(ArrayList<Tag> arrT) {
        Log.d("Tags", "-------------------------------------------------");
        Log.d("Tags", "id    name");
        for (int i = 0; i < arrT.size(); i++) {
            Log.d("Tags", String.format(
                    "%1$s    %2$s",
                    arrT.get(i).getId(),
                    arrT.get(i).getName())
            );
        }
        Log.d("Tags", "-------------------------------------------------");
    }

    private void printLogAllNotes(ArrayList<Alarm> arrN) {
        Log.d("Notes", "-------------------------------------------------");
        Log.d("Notes", "id    date    title    body");
        for (int i = 0; i < arrN.size(); i++) {
            Log.d("Notes", String.format(
                    "%1$s    %2$s    %3$s    %4$s",
                    arrN.get(i).getId(),
                    arrN.get(i).getTime(),
                    arrN.get(i).getName(),
                    arrN.get(i).getBody())
            );
        }
        Log.d("Notes", "-------------------------------------------------");
    }

    public Alarm[] getNotesByTagId(int tagId) {//TODO К УДАЛЕНИЮ
        Cursor cursor;
        String selectionTagItem = "tag_id = " + tagId;
        ArrayList<Integer> notesId = new ArrayList<Integer>();
        cursor = db.query("TagItem", null, selectionTagItem, null, null, null, null);
        if (cursor.moveToFirst()) {
            int noteIdColIndex = cursor.getColumnIndex("note_id");
            do {
                notesId.add(cursor.getInt(noteIdColIndex));
            } while (cursor.moveToNext());
        } else {
            cursor.close();
        }
        Log.d("Get alarms by tagId", String.format("Найдено %1$s noteId с тегом(tagId = %2$s)", notesId.size(), tagId));

        Alarm[] alarms = getAllAlarms();
        ArrayList<Alarm> resAlarms = new ArrayList<Alarm>();
        for (int i = 0; i < alarms.length; i++) {
            for (int j = 0; j < notesId.size(); j++) {
                if (alarms[i].getId() == notesId.get(j)) {
                    resAlarms.add(alarms[i]);
                    break;
                }
            }
        }

        Log.d("Get alarms by tagId", String.format("Найдено %1$s заметок с тегом(id = %2$s)", resAlarms.size(), tagId));
        return resAlarms.toArray(new Alarm[resAlarms.size()]);
    }

    public Tag[] getAllTags() {//TODO К УДАЛЕНИЮ
        Cursor cursor;
        ArrayList<Tag> tags = new ArrayList<Tag>();

        cursor = db.query("Tag", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            Tag tag;
            int idColIndex = cursor.getColumnIndex("id");
            int dateColIndex = cursor.getColumnIndex("name");
            do {
                tag = new Tag();
                tag.setId(cursor.getInt(idColIndex));
                tag.setName(cursor.getString(dateColIndex));
                tags.add(tag);
            } while (cursor.moveToNext());
        } else {
            cursor.close();
        }
        Log.d("Get all tags", String.format("Найдено %1$s tags", tags.size()));
        printLogAllTags(tags);
        return tags.toArray(new Tag[tags.size()]);
    }

    public Tag getTag(int tagId) {//TODO К УДАЛЕНИЮ
        Cursor cursor;
        Tag tag = new Tag();
        cursor = db.query("Tag", null, "id = " + tagId, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("id");
            int dateColIndex = cursor.getColumnIndex("name");
            tag.setId(cursor.getInt(idColIndex));
            tag.setName(cursor.getString(dateColIndex));
            Log.d("Get tag", String.format("Тег с id = %1$s найден", tagId));
            return tag;
        } else {
            cursor.close();
        }
        return tag;
    }

    public Tag[] getTagsByNoteId(int noteId) {//TODO К УДАЛЕНИЮ
        Cursor cursor;
        String selectionTagItem = "note_id = " + noteId;
        ArrayList<Integer> tagsId = new ArrayList<Integer>();
        cursor = db.query("TagItem", null, selectionTagItem, null, null, null, null);
        if (cursor.moveToFirst()) {
            int tagIdColIndex = cursor.getColumnIndex("tag_id");
            do {
                tagsId.add(cursor.getInt(tagIdColIndex));
            } while (cursor.moveToNext());
        } else {
            cursor.close();
        }
        Log.d("Get tag by noteId", String.format("Найдено %1$s tagId с записью(noteId = %2$s)", tagsId.size(), noteId));

        Tag[] tags = getAllTags();
        ArrayList<Tag> resTag = new ArrayList<Tag>();
        for (int i = 0; i < tags.length; i++) {
            for (int j = 0; j < tagsId.size(); j++) {
                if (tags[i].getId() == tagsId.get(j)) {
                    resTag.add(tags[i]);
                    break;
                }
            }
        }

        Log.d("Get tag by noteId", String.format("Найдено %1$s тегов с записью(id = %2$s)", resTag.size(), noteId));
        return resTag.toArray(new Tag[resTag.size()]);
    }
    public int addTag(String name) {//TODO К УДАЛЕНИЮ
        ContentValues cv = new ContentValues();

        cv.put("name", name);

        int tagId = (int) db.insert("Tag", null, cv);

        if (tagId > 0) {
            Log.d("Add tag", String.format("Тег добавлен(tagId = %1$s)", tagId));
        } else {
            Log.d("Add tag", String.format("Что-то пошло не так(tagId = %1$s)", tagId));
        }
        return tagId;
    }

    public void updateTag(int tagId, String name) {//TODO К УДАЛЕНИЮ
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        int updCount = db.update("Tag", cv, "id = " + tagId, null);
        Log.d("Update tag", String.format("Количество обновлённых строк %1$s", updCount));
    }

    public void removeTag(int tagId) {//TODO К УДАЛЕНИЮ
        int delTagItemCount = db.delete("TagItem", "tag_id = " + tagId, null);
        int delCount = db.delete("Tag", "id = " + tagId, null);
        Log.d("Remove tag", String.format("Количество удалённых строк %1$s (Tag)", delCount));
        Log.d("Remove note", String.format("Количество удалённых строк %1$s (TagItem)", delTagItemCount));
    }
    public int addTagToNote(int tagId, int noteId) {//TODO К УДАЛЕНИЮ
        ContentValues cv = new ContentValues();

        cv.put("note_id", noteId);
        cv.put("tag_id", tagId);

        int insertedTagItemId = (int) db.insert("TagItem", null, cv);

        if (insertedTagItemId > 0) {
            Log.d("Add tag to note", String.format("Тег(tagId = %1$s) добавлен к заметке(noteId = %2$s)", tagId, noteId));
        } else {
            Log.d("Add tag to note", String.format("Что-то пошло не так", insertedTagItemId));
        }
        return insertedTagItemId;
    }

    public int removeTagFromNote(int tagId, int noteId) {//TODO К УДАЛЕНИЮ
        ContentValues cv = new ContentValues();

        cv.put("note_id", noteId);
        cv.put("tag_id", tagId);

        int removedTagItemId = (int) db.delete("TagItem", String.format("note_id = %1$s AND tag_id = %2$s", noteId, tagId), null);

        if (removedTagItemId > 0) {
            Log.d("Remove tag from note", String.format("Тег(tagId = %1$s) у заметки(noteId = %2$s) удалён", tagId, noteId));
        } else {
            Log.d("Remove tag from note", String.format("Что-то пошло не так", removedTagItemId));
        }
        return removedTagItemId;
    }

    public TagItem[] getAllTagItems() {//TODO К УДАЛЕНИЮ
        Cursor cursor;
        ArrayList<TagItem> tagItems = new ArrayList<TagItem>();

        cursor = db.query("TagItem", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            TagItem tagItem;
            int idColIndex = cursor.getColumnIndex("id");
            int noteIdColIndex = cursor.getColumnIndex("note_id");
            int tagIdColIndex = cursor.getColumnIndex("tag_id");
            do {
                tagItem = new TagItem();
                tagItem.setId(cursor.getInt(idColIndex));
                tagItem.setNoteId(cursor.getInt(noteIdColIndex));
                tagItem.setTagId(cursor.getInt(tagIdColIndex));
                tagItems.add(tagItem);
            } while (cursor.moveToNext());
        } else {
            cursor.close();
        }
        Log.d("Get all tag items", String.format("Найдено %1$s tag items", tagItems.size()));
        printLogAllTagItems(tagItems);
        return tagItems.toArray(new TagItem[tagItems.size()]);
    }
}

