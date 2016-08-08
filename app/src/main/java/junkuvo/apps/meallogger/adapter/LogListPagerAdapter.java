package junkuvo.apps.meallogger.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import junkuvo.apps.meallogger.fragment.FragmentLogList;

public class LogListPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_NUMBER = 2;

    public LogListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "タイムライン";
            case 1:
                return "test";
            default:
                throw new RuntimeException("unexpected position: " + position);
        }
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentLogList();
            case 1:
                return new FragmentLogList();
            default:
                throw new RuntimeException("unexpected position: " + position);
        }
    }
}



