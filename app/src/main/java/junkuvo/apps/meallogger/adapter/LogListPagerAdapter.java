package junkuvo.apps.meallogger.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import junkuvo.apps.meallogger.R;
import junkuvo.apps.meallogger.fragment.FragmentLogList;
import junkuvo.apps.meallogger.fragment.FragmentMonthlyLogList;

public class LogListPagerAdapter extends FragmentPagerAdapter {

    private static final int PAGE_NUMBER = 2;
    private Context mContext;
    private FragmentMonthlyLogList mFragmentMonthlyLogList;

    public LogListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public LogListPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public int getCount() {
        return PAGE_NUMBER;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mContext.getString(R.string.page1_title);
            case 1:
                return mContext.getString(R.string.page2_title);
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
                mFragmentMonthlyLogList = new FragmentMonthlyLogList();
                return mFragmentMonthlyLogList;
            default:
                throw new RuntimeException("unexpected position: " + position);
        }
    }

    public FragmentMonthlyLogList getmFragmentMonthlyLogList() {
        return mFragmentMonthlyLogList;
    }
}



