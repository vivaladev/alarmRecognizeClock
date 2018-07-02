package vivaladev.com.dirtyclocky.ui.fragmentProcessing.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import vivaladev.com.dirtyclocky.R;
import vivaladev.com.dirtyclocky.ui.activities.MainActivity;
import vivaladev.com.dirtyclocky.ui.activities.SoundProcessingActivity;
import vivaladev.com.dirtyclocky.ui.fragmentProcessing.factories.RecordFactory;

public class RecordFragment extends Fragment implements View.OnClickListener {

    private static final String ARGUMENT_PAGE_NUMBER = "page_number";

    private int clickedFileName = -1;

    private List<String> filenames = new ArrayList<>();

    private int pageNumber;

    public int getClickedFileName() {
        return clickedFileName;
    }

    public void setClickedFileName(int clickedFileName) {
        this.clickedFileName = clickedFileName;
    }

    public static RecordFragment newInstance(int pageNum) {
        RecordFragment pageFragment = new RecordFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, pageNum);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View currentView = inflater.inflate(R.layout.all_sound_fragment, null);
        LinearLayout tags_linearLayout = currentView.findViewById(R.id.tags_linearLayout);
        RecordFactory tf = new RecordFactory(this.getContext(), tags_linearLayout, this);

        filenames = Arrays.asList(getConvertedFileName(Environment.getExternalStorageDirectory().listFiles()));

        for (int i = 0; i < filenames.size(); i++) {
            tf.addTagToScreen(i+10, filenames.get(i), false);
        }
        return currentView;
    }



    private String[] getConvertedFileName(File[] filenames) {
        List<String> res = new ArrayList<>();
        for (int i = 0; i < filenames.length; i++) {
            if (isValidateMusicFile(filenames[i])) {
                if (filenames[i].isFile()) {
                    res.add(getRecordName(filenames[i].getName().toCharArray()));
                }
            }
        }
        return res.toArray(new String[res.size()]);
    }

    private boolean isValidateMusicFile(File file) {
        return Pattern.matches(".*\\.amr_nb", file.getName());
    }

    private String getRecordName(char[] name) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < name.length - 7; i++) {
            res.append(name[i]);
        }
        return res.toString();
    }

    @Override
    public void onClick(View view) {

        MainActivity.getInstance().initializeFragments();
        setClickedFileName(view.getId());
        Intent intent = new Intent(MainActivity.getInstance(), SoundProcessingActivity.class);
        startActivity(intent);
    }
}