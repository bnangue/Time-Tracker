package com.bricefamily.alex.time_tracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by bricenangue on 15/02/16.
 */
public class TabsPagerAdatpter extends FragmentPagerAdapter {


    public TabsPagerAdatpter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new UserFriendsListFragment();
            case 1:
                return new AllUsersListTabFragnmenet();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
