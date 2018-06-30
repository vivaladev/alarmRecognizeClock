package vivaladev.com.dirtyclocky.ui.fragmentProcessing.fragmentAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import vivaladev.com.dirtyclocky.ui.fragmentProcessing.fragments.NotesFragment;
import vivaladev.com.dirtyclocky.ui.fragmentProcessing.fragments.TagsFragment;

public class ImplFragmentPageAdapter extends FragmentPagerAdapter {
    static final int PAGE_COUNT = 2;

    private ArrayList<Fragment> fragmentList;

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public ArrayList<Fragment> getFragmentsList() {
        return fragmentList;
    }

    public ImplFragmentPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: {
                return "Заметки";
            }
            case 1: {
                return "Теги";
            }
            default: {
                return "none";
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0: {
                fragment = (Fragment) NotesFragment.newInstance(position);
                break;
            }
            case 1: {
                fragment = (Fragment) TagsFragment.newInstance(position);
                break;
            }
            default: {
                fragment = null; // if it's execute, need add page in switch
            }
        }
        if (fragmentList == null) {
            fragmentList = new ArrayList<Fragment>();
        }
        fragmentList.add(fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}