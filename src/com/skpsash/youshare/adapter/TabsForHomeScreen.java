package com.skpsash.youshare.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.skpsash.youshare.AwarenessScreenFragment;
import com.skpsash.youshare.GkScreenFragment;
import com.skpsash.youshare.HomeScreenFragment;
import com.skpsash.youshare.JustFunScreenFragment;
import com.skpsash.youshare.MoviesScreenFragment;
import com.skpsash.youshare.OthersScreenFragment;
import com.skpsash.youshare.PersonalitiesScreenFragment;
import com.skpsash.youshare.PoliticsScreenFragment;

public class TabsForHomeScreen extends FragmentPagerAdapter {

	public TabsForHomeScreen(FragmentManager fm) {
		super(fm);
		}

	@Override
	public Fragment getItem(int index) {
		switch (index) {
		case 0:
			return new HomeScreenFragment();
		case 1:
			return new GkScreenFragment();
		case 2:
			return new JustFunScreenFragment();
		case 3:
			return new AwarenessScreenFragment();
		case 4:
			return new MoviesScreenFragment();
		case 5:
			return new PersonalitiesScreenFragment();	
		case 6:
			return new PoliticsScreenFragment();
		case 7:
			return new OthersScreenFragment();	
		
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
				return 8;
	}

}
