package vivaladev.com.dirtyclocky.databaseProcessing.entities;

/**
 * Created by Anton on 06.04.2018.
 */

public class TagItem {
    int id;
    int note_id;
    int tag_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNoteId() {
        return note_id;
    }

    public void setNoteId(int note_id) {
        this.note_id = note_id;
    }

    public int getTagId() {
        return tag_id;
    }

    public void setTagId(int tag_id) {
        this.tag_id = tag_id;
    }
}
