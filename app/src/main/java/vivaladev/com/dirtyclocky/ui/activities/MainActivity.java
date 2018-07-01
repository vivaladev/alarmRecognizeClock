package vivaladev.com.dirtyclocky.ui.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import vivaladev.com.dirtyclocky.R;
import vivaladev.com.dirtyclocky.alarmcontrol.handler.AlarmHandler;
import vivaladev.com.dirtyclocky.alarmcontrol.service.AlarmService;
import vivaladev.com.dirtyclocky.ui.fragmentProcessing.fragmentAdapter.ImplFragmentPageAdapter;
import vivaladev.com.dirtyclocky.ui.fragmentProcessing.fragments.NotesFragment;
import vivaladev.com.dirtyclocky.ui.fragmentProcessing.fragments.TagsFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static MainActivity instance;

    private int screen_width;
    private ViewPager pager;
    private ImplFragmentPageAdapter pagerAdapter;

    private TagsFragment tagsFragment;
    private NotesFragment notesFragment;

    private Toolbar tool_bar;
    private Button tags;
    private Button notes;

    private FloatingActionButton edit_note_or_tag;
    private Space left_space;
    private Space right_space;
    private LinearLayout.LayoutParams left_space_params;
    private LinearLayout.LayoutParams right_space_param;

    public ImplFragmentPageAdapter getPagerAdapter() {
        return pagerAdapter;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public TagsFragment getTagsFragment() {
        return tagsFragment;
    }

    public NotesFragment getNotesFragment() {
        return notesFragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_tool_bar, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        instance = this;
        setActivitiesItems();
//        setStatusBar(this);
        setSupportActionBar(tool_bar);
        initializeScreenSize();
        initializeFragmentAdapter();

        Intent intent = new Intent(getApplicationContext(), AlarmService.class);
        startService(intent);

        AlarmHandler.loadAlarms((AlarmManager) getSystemService(Context.ALARM_SERVICE), getApplicationContext());

        /*try {
            throw new UnsupportedOperationException("Create AlarmService");
        }
        catch (UnsupportedOperationException ex){
            ex.printStackTrace();
        }*/
        //startRepeatingTimer();

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (positionOffset >= 0.001 && positionOffset <= 0.999) {
                    if (positionOffset >= 0.99) {
                        left_space_params.width = (screen_width / 2) + 1;
                        right_space_param.width = 0;
                        notes.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.darker_gray));
                        notes.setTypeface(Typeface.SANS_SERIF);
                        tags.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.theme_color));
                        tags.setTypeface(Typeface.DEFAULT_BOLD);
                    } else if (positionOffset <= 0.01) {
                        left_space_params.width = 0;
                        right_space_param.width = (screen_width / 2) + 1;
                        notes.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.theme_color));
                        notes.setTypeface(Typeface.DEFAULT_BOLD);
                        tags.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.darker_gray));
                        tags.setTypeface(Typeface.SANS_SERIF);
                    } else {
                        left_space_params.width = (int) (screen_width / 2 * positionOffset);
                        right_space_param.width = screen_width / 2 - (int) (screen_width / 2 * positionOffset);
                    }
                }
                left_space.setLayoutParams(left_space_params);
                right_space.setLayoutParams(right_space_param);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_note_or_tag: {
                initializeFragments();
                switch (pager.getCurrentItem()) {
                    case 0: {
                        newNoteClick();
                        Intent intent = new Intent(this, AlarmEditActivity.class);
                        startActivity(intent);
                        break;
                    }
                    case 1: {
                        newTagClick();
                        Intent intent = new Intent(this, SoundProcessingActivity.class);
                        startActivity(intent);
                        break;
                    }
                }
                break;
            }
            case R.id.alarms: {
                try {
                    pager.setCurrentItem(0, true);
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.tags: {
                try {
                    pager.setCurrentItem(1, true);
                } catch (Exception e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    private void newNoteClick() {
        notesFragment.setClickedNoteId(-1);
    }

    private void newTagClick() {
        tagsFragment.setClickedTagId(-1);
    }

    public void initializeFragments() {
        if (notesFragment == null && tagsFragment == null) {
            ArrayList<Fragment> tmp = pagerAdapter.getFragmentsList();
            notesFragment = (NotesFragment) tmp.get(0);
            tagsFragment = (TagsFragment) tmp.get(1);
        }
    }

    private void initializeFragmentAdapter() {
        pagerAdapter = new ImplFragmentPageAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);
    }

    private void initializeScreenSize() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        screen_width = size.x;
        right_space_param.width = screen_width / 2;
    }

    /*private static void setStatusBar(Activity activity) {
        View someView = activity.findViewById(R.id.my_toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            someView.setSystemUiVisibility(someView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            Window window = activity.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            switch (getTimeInterval(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))){ //TODO: вернуть работоспособность свитчеру картинок
                case Morning:
                    window.setBackgroundDrawableResource(R.drawable.morning);
                    break;
                case Day:
                    window.setBackgroundDrawableResource(R.drawable.day);
                    break;
                case Evening:
                    window.setBackgroundDrawableResource(R.drawable.evening);
                    break;
                case Night:
                    window.setBackgroundDrawableResource(R.drawable.night);
                    break;
                default:
                    break;
            }
        }
    }*/

    private static TimeInterval getTimeInterval(int hour){
        if(hour >= 4 && hour < 10)
            return TimeInterval.Morning;
        if(hour >= 10 && hour < 16)
            return TimeInterval.Day;
        if(hour >= 16 && hour < 22)
            return TimeInterval.Evening;
        if(hour >= 22 || hour < 4)
            return TimeInterval.Night;
        return null;
    }

    private void setActivitiesItems() {

        pager = findViewById(R.id.pager);
        //tool_bar = findViewById(R.id.my_toolbar);
        edit_note_or_tag = (FloatingActionButton) findViewById(R.id.edit_note_or_tag);
        edit_note_or_tag.setOnClickListener(this);
        notes = (Button) findViewById(R.id.alarms);
        notes.setOnClickListener(this);
        tags = (Button) findViewById(R.id.tags);
        tags.setOnClickListener(this);
        left_space = (Space) findViewById(R.id.left_space);
        right_space = (Space) findViewById(R.id.right_space);
        left_space_params = (LinearLayout.LayoutParams) left_space.getLayoutParams();
        right_space_param = (LinearLayout.LayoutParams) right_space.getLayoutParams();
    }
}

enum TimeInterval{
    Morning, Day, Evening, Night
}

