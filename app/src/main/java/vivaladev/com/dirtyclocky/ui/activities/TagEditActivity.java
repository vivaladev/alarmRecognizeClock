package vivaladev.com.dirtyclocky.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import vivaladev.com.dirtyclocky.R;
import vivaladev.com.dirtyclocky.databaseProcessing.dao.DatabaseWrapper;
import vivaladev.com.dirtyclocky.databaseProcessing.entities.Note;
import vivaladev.com.dirtyclocky.databaseProcessing.entities.Tag;
import vivaladev.com.dirtyclocky.ui.fragmentProcessing.factories.NotesFactory;

public class TagEditActivity extends AppCompatActivity implements View.OnClickListener {

    private String initialName;

    private int clickedTagId;

    private Menu toolbarMenu;
    private EditText tag_edit_field;
    private Toolbar edit_tag_tool_bar;

    private static TagEditActivity instance;


    public static TagEditActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tag_edit_activity);

        instance = this;
        setActivitiesItems();
        setStatusBar(this);
        setToolBar();
    }


    @Override
    public void onClick(View view) {
        MainActivity.getInstance().initializeFragments();
        MainActivity.getInstance().getNotesFragment().setClickedNoteId(view.getId());
        Intent intent = new Intent(MainActivity.getInstance(), NoteEditActivity.class);
        startActivity(intent);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_tag_tool_bar, menu);
        toolbarMenu = menu;
        setTagData();
        return true;
    }

    @Override
    public void onBackPressed() {
        backBtnDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_btn: {
                saveChanges();
                break;
            }
            case R.id.remove_btn: {
                removeTagDialog();
                break;
            }
            case android.R.id.home: {
                backBtnDialog();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void reload() {
        LinearLayout notes_linearLayout = (LinearLayout) findViewById(R.id.notes_linearLayout);
        notes_linearLayout.removeAllViews();
        setTagData();
    }

    private TextView createInfoTV(String text) {
        TextView newTextView = new TextView(this);
        LinearLayout.LayoutParams newTextView_params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );


        newTextView.setGravity(Gravity.CENTER);
        newTextView.setText(text);
        newTextView.setLayoutParams(newTextView_params);
        return newTextView;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTagData() {
        Tag tag;
        Note[] tagNotes;
        clickedTagId = MainActivity.getInstance().getTagsFragment().getClickedTagId();
        LinearLayout notes_linearLayout = (LinearLayout) findViewById(R.id.notes_linearLayout);
        NotesFactory nf = new NotesFactory(this, notes_linearLayout, this);

        if (clickedTagId != -1) {
            toolbarMenu.findItem(R.id.remove_btn).setVisible(true);
            try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "myDB")) {
                tag = dbw.getTag(clickedTagId);
                tagNotes = dbw.getNotesByTagId(clickedTagId);
                tag_edit_field.setText(tag.getName());
                if (tagNotes.length == 0) {
                    notes_linearLayout.addView(createInfoTV("Заметок с этим тегом не найдено"));
                } else {
                    for (int i = 0; i < tagNotes.length; i++) {
                        nf.addNoteToScreen(tagNotes[i].getId(), tagNotes[i].getDate(), tagNotes[i].getTitle(), tagNotes[i].getBody(), dbw.getTagsByNoteId(tagNotes[i].getId()));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            notes_linearLayout.addView(createInfoTV("Создайте тег и присвойте заметке"));
            toolbarMenu.findItem(R.id.remove_btn).setVisible(false);
            tag_edit_field.setText("");
        }
        setInitialData();
    }

    private void backBtnDialog() {
        if (isNeedSave()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Сохранить изменения или удалить их?");
            builder.setNegativeButton("Удалить  ",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            builder.setPositiveButton("Сохранить",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            saveChanges();
                        }
                    });
            builder.setNeutralButton("Отмена", null);
            builder.show();
        } else {
            finish();
        }
    }

    private void removeTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Удалить тег?")
                .setPositiveButton("Удалить",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                removeTag();
                                finish();
                            }
                        }).setNegativeButton("Отмена", null).show();
    }

    private void removeTag() {
        try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "myDB")) {
            dbw.removeTag(clickedTagId);
            MainActivity.getInstance().getPagerAdapter().notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveChanges() {
        if (tag_edit_field.getText().toString().equals("")) {
            showMessage("Тег без имени?");
            return;
        }
        if (!isNeedSave()) {
            return;
        }
        try (DatabaseWrapper dbw = new DatabaseWrapper(MainActivity.getInstance(), "myDB")) {
            String name = tag_edit_field.getText().toString();

            if (clickedTagId != -1) {
                dbw.updateTag(clickedTagId, name);
            } else {
                dbw.addTag(name);
            }
            setInitialData();
            showMessage("Изменения сохранены");
            MainActivity.getInstance().getPagerAdapter().notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNeedSave() {
        String name = tag_edit_field.getText().toString();
        if (initialName.equals(name)) {
            return false;
        }
        return true;
    }

    private void setInitialData() {
        initialName = tag_edit_field.getText().toString();
    }


    private static void setStatusBar(Activity activity) {
        View someView = activity.findViewById(R.id.edit_tag_tool_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            someView.setSystemUiVisibility(someView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(activity, android.R.color.black));
        }
    }

    private void setActivitiesItems() {

        tag_edit_field = (EditText) findViewById(R.id.tag_edit_field);
        edit_tag_tool_bar = (Toolbar) findViewById(R.id.edit_tag_tool_bar);
    }

    private void setToolBar() {
        setSupportActionBar(edit_tag_tool_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_arrow);
        edit_tag_tool_bar.setSubtitleTextColor(getResources().getColor(R.color.theme_color));
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
