package il.ac.shenkar.kerenor.tasksapp.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import il.ac.shenkar.kerenor.tasksapp.EnumsAndConsts.Constants;
import il.ac.shenkar.kerenor.tasksapp.Fragments.TabFragmentAllTasks;
import il.ac.shenkar.kerenor.tasksapp.Fragments.TabFragmentWaitingTasks;

/**
 * ViewPagerAdapter.java - a class that open the chosen tab (fragment)
 * @author  Keren Yakov & Or Amit
 * @version 2.0
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {
    Context context;


    public ViewPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new TabFragmentWaitingTasks();
            case 1:
                return new TabFragmentAllTasks();
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return Constants.FRAGMENTS_NUM;
    }
}
