package vivaladev.com.dirtyclocky.databaseProcessing.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import vivaladev.com.dirtyclocky.databaseProcessing.entities.Note;
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

    public int addTagToNote(int tagId, int noteId) {
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

    public int removeTagFromNote(int tagId, int noteId) {
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

    public TagItem[] getAllTagItems() {
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

    public Note getNote(int noteId) {
        Cursor cursor;
        Note note = new Note();
        cursor = db.query("Note", null, "id = " + noteId, null, null, null, null);
        if (cursor.moveToFirst()) {

            int idColIndex = cursor.getColumnIndex("id");
            int dateColIndex = cursor.getColumnIndex("date");
            int titleColIndex = cursor.getColumnIndex("title");
            int bodyColIndex = cursor.getColumnIndex("body");
            note.setId(cursor.getInt(idColIndex));
            note.setDate(cursor.getString(dateColIndex));
            note.setTitle(cursor.getString(titleColIndex));
            note.setBody(cursor.getString(bodyColIndex));
            Log.d("Get note", String.format("Заметки с id = %1$s найдена", noteId));
        } else {
            cursor.close();
        }
        return note;
    }

    public Note[] getAllNotes() {
        Cursor cursor;
        ArrayList<Note> notes = new ArrayList<Note>();

        cursor = db.query("Note", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            Note note;
            int idColIndex = cursor.getColumnIndex("id");
            int dateColIndex = cursor.getColumnIndex("date");
            int titleColIndex = cursor.getColumnIndex("title");
            int bodyColIndex = cursor.getColumnIndex("body");
            do {
                note = new Note();
                note.setId(cursor.getInt(idColIndex));
                note.setDate(cursor.getString(dateColIndex));
                note.setTitle(cursor.getString(titleColIndex));
                note.setBody(cursor.getString(bodyColIndex));
                notes.add(note);
            } while (cursor.moveToNext());
        } else {
            cursor.close();
        }
        Log.d("Get all notes", String.format("Найдено %1$s notes", notes.size()));
        printLogAllNotes(notes);
        return notes.toArray(new Note[notes.size()]);
    }

    public Note[] getNotesByTagId(int tagId) {
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
        Log.d("Get notes by tagId", String.format("Найдено %1$s noteId с тегом(tagId = %2$s)", notesId.size(), tagId));

        Note[] notes = getAllNotes();
        ArrayList<Note> resNotes = new ArrayList<Note>();
        for (int i = 0; i < notes.length; i++) {
            for (int j = 0; j < notesId.size(); j++) {
                if (notes[i].getId() == notesId.get(j)) {
                    resNotes.add(notes[i]);
                    break;
                }
            }
        }

        Log.d("Get notes by tagId", String.format("Найдено %1$s заметок с тегом(id = %2$s)", resNotes.size(), tagId));
        return resNotes.toArray(new Note[resNotes.size()]);
    }

    public Tag[] getAllTags() {
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

    public Tag getTag(int tagId) {
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

    public Tag[] getTagsByNoteId(int noteId) {
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

    public int addNote(String date, String title, String body) {
        ContentValues cv = new ContentValues();

        cv.put("date", date);
        cv.put("title", title);
        cv.put("body", body);

        int noteId = (int) db.insert("Note", null, cv);

        if (noteId > 0) {
            Log.d("Add note", String.format("Заметка добавлена(noteId = %1$s)", noteId));
        } else {
            Log.e("Add note", String.format("Что-то пошло не так(noteId = %1$s)", noteId));
        }
        return noteId;
    }

    public void updateNote(int noteId, String date, String title, String body) {
        ContentValues cv = new ContentValues();

        cv.put("date", date);
        cv.put("title", title);
        cv.put("body", body);
        int updCount = db.update("Note", cv, "id = " + noteId, null);
        Log.d("Update note", String.format("Количество обновлённых строк %1$s", updCount));
    }

    public void removeNote(int noteId) {
        int delTagItemCount = db.delete("TagItem", "note_id = " + noteId, null);
        int delNoteCount = db.delete("Note", "id = " + noteId, null);
        Log.d("Remove note", String.format("Количество удалённых строк %1$s (Note)", delNoteCount));
        Log.d("Remove note", String.format("Количество удалённых строк %1$s (TagItem)", delTagItemCount));
    }

    public int addTag(String name) {
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

    public void updateTag(int tagId, String name) {
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        int updCount = db.update("Tag", cv, "id = " + tagId, null);
        Log.d("Update tag", String.format("Количество обновлённых строк %1$s", updCount));
    }

    public void removeTag(int tagId) {
        int delTagItemCount = db.delete("TagItem", "tag_id = " + tagId, null);
        int delCount = db.delete("Tag", "id = " + tagId, null);
        Log.d("Remove tag", String.format("Количество удалённых строк %1$s (Tag)", delCount));
        Log.d("Remove note", String.format("Количество удалённых строк %1$s (TagItem)", delTagItemCount));
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

    private void printLogAllNotes(ArrayList<Note> arrN) {
        Log.d("Notes", "-------------------------------------------------");
        Log.d("Notes", "id    date    title    body");
        for (int i = 0; i < arrN.size(); i++) {
            Log.d("Notes", String.format(
                    "%1$s    %2$s    %3$s    %4$s",
                    arrN.get(i).getId(),
                    arrN.get(i).getDate(),
                    arrN.get(i).getTitle(),
                    arrN.get(i).getBody())
            );
        }
        Log.d("Notes", "-------------------------------------------------");
    }
}
