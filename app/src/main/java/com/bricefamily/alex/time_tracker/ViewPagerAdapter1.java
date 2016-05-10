package com.bricefamily.alex.time_tracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by bricenangue on 16/02/16.
 */
public class ViewPagerAdapter1 extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    private final ArrayList<Fragment> fragmentList = new ArrayList<>();
    private final ArrayList<String> fragmentTitleList = new ArrayList<>();

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter1(FragmentManager fm) {
        super(fm);

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {

        Fragment frag=null;
        switch (position){
            case 0:
                frag=new FragmentAllEvents();
                break;
            case 1:
                frag=new FragmentMyEvent();
                break;
            case 2:
                frag=new FragmentCategoryBusiness();
                break;
            case 3:
                frag=new FragmentCategoryBirthdays();
                break;
            case 4:
                frag=new FragmentCategoryShopping();
                break;
            case 5:
                frag=new FragmentCategoryWorkPlan();
                break;
        }
        return frag;
       // return  fragmentList.get(position);

    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        String title=" ";
        switch (position){
            case 0:
                title="All event";
                break;
            case 1:
                title="my event";
                break;
            case 2:
                title="business";
                break;
            case 3:
                title="birthday";
                break;
            case 4:
                title="shopping";
                break;
            case 5:
                title="work plan";
                break;
        }

        return title;

        //return fragmentTitleList.get(position);
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return 6;
        //return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}

