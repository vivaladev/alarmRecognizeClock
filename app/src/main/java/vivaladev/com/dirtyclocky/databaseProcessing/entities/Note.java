package vivaladev.com.dirtyclocky.databaseProcessing.entities;

import java.util.ArrayList;

/**
 * Created by Anton on 06.04.2018.
 */

public class Note {
    int id;
    String date;
    String title;
    String body;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
