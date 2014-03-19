package com.example.rehab_coachv1;

import java.util.ArrayList;
import java.util.Locale;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ReminderActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private String activity_name;
	private int activity_id;
	int theme = 0;
	
	private SQLiteDatabase database;
	ArrayList<String> remind_list = new ArrayList<String>();
	
	public void getReminders(int activity_id){
		ExternalDbOpenHelper dbHelper = new ExternalDbOpenHelper(this, "rehab_coach");
		database = dbHelper.openDataBase();
		
		Log.d("MIKE", "act id = " + activity_id);
		
		Cursor prereqCursor = database.rawQuery("select name from prerequisite where activity_id = ?", new String[]{Integer.toString(activity_id)});
		prereqCursor.moveToFirst();
		
		while(!prereqCursor.isAfterLast()){
			remind_list.add(prereqCursor.getString(prereqCursor.getColumnIndex("name")));
			prereqCursor.moveToNext();
		}
		Log.d("MIKE", "reminder_size = " + remind_list.size());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		theme = getIntent().getIntExtra("theme", 0);
		activity_id = getIntent().getIntExtra("act_id",0);
		if (theme == 0)
		{
			setTheme(android.R.style.Theme_Holo_Light);
		}
		else
		{
			setTheme(android.R.style.Theme_Holo);
		}
		setContentView(R.layout.activity_reminder);
				
		getReminders(activity_id);
		
		activity_name = getIntent().getStringExtra("act");
		TextView name = (TextView) findViewById(R.id.title);
		name.setText(activity_name);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), remind_list);

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		if (theme == 1)
		{
			getMenuInflater().inflate(R.menu.dark, menu);
		}
		else
		{
			getMenuInflater().inflate(R.menu.light, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {

	        case R.id.home_screen:
	            openHome();
	            return true;
	        case R.id.profile_screen:
	            openProfile();
	            return true;
	        case R.id.help_screen:
	            openHelp();
	            return true;
	        case R.id.settings_screen:
	            openSettings();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void openHome() {
		Intent remind = new Intent (this, HomeActivity.class);
		remind.putExtra("theme", theme);
		startActivity(remind);
		
	}

	public void openProfile() {
		Intent remind = new Intent (this, ProfileActivity.class);
		remind.putExtra("theme", theme);
		startActivity(remind);
		
	}

	public void openHelp() {
		Intent remind = new Intent (this, HelpActivity.class);
		remind.putExtra("theme", theme);
		startActivity(remind);	
	}
	
	public void openSettings() {
		Intent remind = new Intent (this, SettingsActivity.class);
		remind.putExtra("theme", theme);
		startActivity(remind);	
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		ArrayList<String> reminders = new ArrayList<String>();
		
		public SectionsPagerAdapter(FragmentManager fm,  ArrayList<String> remind) {
			super(fm);
			reminders = remind;
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			if (position < 4)
			{
				Fragment fragment = new DummySectionFragment();
				Bundle args = new Bundle();
				args.putString(DummySectionFragment.ARG_SECTION_NUMBER, reminders.get(position));
				args.putInt("current_page", position);
				fragment.setArguments(args);
				return fragment;
			}
			else
			{
				Fragment fragment = new LastFragment();
				Bundle args = new Bundle();
				args.putString(LastFragment.ARG_SECTION_NUMBER, reminders.get(position));
				fragment.setArguments(args);
				return fragment;
			}
		}

		@Override
		public int getCount() {
			// Show 5 total pages.
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			case 4:
				return getString(R.string.title_section5).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_reminder_dummy,
					container, false);
			TextView remember = (TextView) rootView
					.findViewById(R.id.remember);
			remember.setText(R.string.remember);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.reminder_text);
			dummyTextView.setText(getArguments().getString(
					ARG_SECTION_NUMBER));
			switch (getArguments().getInt("current_page")) {
			case 0:
				ImageView image_1 = (ImageView) rootView.findViewById(R.id.page_1);
				image_1.setVisibility(View.VISIBLE);
				break;
			case 1:
				ImageView image_2 = (ImageView) rootView.findViewById(R.id.page_2);
				image_2.setVisibility(View.VISIBLE);
				break;
			case 2:
				ImageView image_3 = (ImageView) rootView.findViewById(R.id.page_3);
				image_3.setVisibility(View.VISIBLE);
				break;
			case 3:
				ImageView image_4 = (ImageView) rootView.findViewById(R.id.page_4);
				image_4.setVisibility(View.VISIBLE);
				break;
			}
			return rootView;
		}
	}
	
	public static class LastFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public LastFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_reminder_dummy,
					container, false);
			TextView remember = (TextView) rootView
					.findViewById(R.id.remember);
			remember.setText(R.string.remember);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.reminder_text);
			dummyTextView.setText(getArguments().getString(
					ARG_SECTION_NUMBER));
			ImageView image = (ImageView) rootView.findViewById(R.id.page_5);
			image.setVisibility(View.VISIBLE);
			Button start = (Button) rootView.findViewById(R.id.start_button);
			start.setVisibility(View.VISIBLE);
			return rootView;
		}
	}
	

	public void startActivity(View view)
	{
		Intent currActivity = new Intent(this, DuringActivity.class);
		currActivity.putExtra("act", activity_name);
		currActivity.putExtra("act_id", activity_id);
		currActivity.putExtra("theme", theme);
		startActivity(currActivity);
	}
}
