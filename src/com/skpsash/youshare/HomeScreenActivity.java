package com.skpsash.youshare;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.skpsash.youshare.adapter.TabsForHomeScreen;

public class HomeScreenActivity extends FragmentActivity implements ActionBar.TabListener {

	private ViewPager viewPager;
	private TabsForHomeScreen mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Home", "GK And Happenings","Just Fun","Awareness","Movies","Personalities","Politics","Others" };

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_homescreen);
		
		viewPager = (ViewPager) findViewById(R.id.pager1);
		actionBar = getActionBar();
		mAdapter = new TabsForHomeScreen(getSupportFragmentManager());
		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);		
		AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		
//		startService(new Intent(getApplicationContext(), MyService.class));
		Intent alarmintent = new Intent(HomeScreenActivity.this, MyService.class);
		PendingIntent sender = PendingIntent.getService(getApplicationContext(), 3, alarmintent, PendingIntent.FLAG_UPDATE_CURRENT);
//		alarmManager.cancel(sender);
		
		boolean alarmup = (PendingIntent.getService(getApplicationContext(), 3, 
				new Intent(HomeScreenActivity.this, MyService.class),PendingIntent.FLAG_NO_CREATE) == null);
		
//		if (alarmup) 
//		{
////			alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(),/*24 * 60 * 60 * 1000*/10000, sender);
//			Toast.makeText(getApplicationContext(), "alarmup", Toast.LENGTH_LONG).show();
//		}
		if(!alarmup){
//			Toast.makeText(getApplicationContext(), "Not alarmup", Toast.LENGTH_LONG).show();
			alarmManager.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(),24 * 60 * 60 * 1000, sender);
		}
		
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// on tab selected
		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}
	
	@Override
	public void onBackPressed() {
		 Intent startMain = new Intent(Intent.ACTION_MAIN);
	     startMain.addCategory(Intent.CATEGORY_HOME);
	     startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	     startActivity(startMain);
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}
