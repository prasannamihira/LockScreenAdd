package com.crowderia.mytoz.adapter;

/**
 * Created by crowderia on 11/1/16.
 */
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.crowderia.mytoz.fragment.AdvertiesmentFragment;
import com.crowderia.mytoz.fragment.CategoryFragment;
import com.crowderia.mytoz.fragment.ProfileFragment;

public class TabAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public TabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AdvertiesmentFragment tab1 = new AdvertiesmentFragment();
                return tab1;
            case 1:
                CategoryFragment tab2 = new CategoryFragment();
                return tab2;
            case 2:
                ProfileFragment tab3 = new ProfileFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
