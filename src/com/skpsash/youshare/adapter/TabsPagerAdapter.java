package com.skpsash.youshare.adapter;

import com.skpsash.youshare.AboutYoushareFragment;
import com.skpsash.youshare.LoginFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:
			// Login fragment activity
			return new LoginFragment();
		case 1:
			// About fragment activity
			return new AboutYoushareFragment();
//		case 2:
//			// Suggest ideas fragment activity
//			return new SuggestFragment();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 2;
	}

}
