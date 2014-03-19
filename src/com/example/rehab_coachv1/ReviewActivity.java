 package com.example.rehab_coachv1;

import java.util.ArrayList;
import java.util.Locale;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ReviewActivity extends FragmentActivity {

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

	String activity_name;
	int activity_id;
	int theme = 0;
	static int val1 = 0;
	static int val2 = 0;
	static int val3 = 0;
	static int val4 = 0;
	static int val5 = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		theme = getIntent().getIntExtra("theme", 0);
		activity_name = getIntent().getStringExtra("act");
		activity_id = getIntent().getIntExtra("act_id", 0);
		if (theme == 0)
		{
			setTheme(android.R.style.Theme_Holo_Light);
		}
		else
		{
			setTheme(android.R.style.Theme_Holo);
		}
		setContentView(R.layout.activity_review);
		ArrayList <String> review_list = new ArrayList<String>(); 
		review_list.add("How comfortable were you during this activity?");
		review_list.add("How social were you?");
		review_list.add("How successful was this activity?");
		review_list.add("How easily did you find transportation?");
		review_list.add("How likely are you to do this activity again?");
		TextView name = (TextView) findViewById(R.id.title);
		name.setText(activity_name);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), review_list);

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

		ArrayList<String> review_list = new ArrayList<String>();
		
		public SectionsPagerAdapter(FragmentManager fm, ArrayList<String> review) {
			super(fm);
			review_list = review;
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
				args.putString(DummySectionFragment.ARG_SECTION_NUMBER, review_list.get(position));
				args.putInt("current_page", position);
				fragment.setArguments(args);
				return fragment;
			}
			else
			{
				Fragment fragment = new LastFragment();
				Bundle args = new Bundle();
				args.putString(LastFragment.ARG_SECTION_NUMBER, review_list.get(position));
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
	public static class DummySectionFragment extends Fragment implements OnSeekBarChangeListener{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		public TextView progress;
		
		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_review_dummy,
					container, false);
			TextView remember = (TextView) rootView
					.findViewById(R.id.review);
			remember.setText(R.string.review);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.review_text);
			dummyTextView.setText(getArguments().getString(
					ARG_SECTION_NUMBER));	
			progress = (TextView) rootView.findViewById(R.id.progress);
			switch (getArguments().getInt("current_page")) {
			case 0:
				ImageView image_1 = (ImageView) rootView.findViewById(R.id.page_1);
				image_1.setVisibility(View.VISIBLE);
				SeekBar seek_1 = (SeekBar) rootView.findViewById(R.id.seek_1);
				image_1.setVisibility(View.VISIBLE);
				seek_1.setVisibility(View.VISIBLE);
				seek_1.setOnSeekBarChangeListener(this);
				break;
			case 1:
				ImageView image_2 = (ImageView) rootView.findViewById(R.id.page_2);
				image_2.setVisibility(View.VISIBLE);
				SeekBar seek_2 = (SeekBar) rootView.findViewById(R.id.seek_2);
				seek_2.setVisibility(View.VISIBLE);
				seek_2.setOnSeekBarChangeListener(this);
				break;
			case 2:
				ImageView image_3 = (ImageView) rootView.findViewById(R.id.page_3);
				image_3.setVisibility(View.VISIBLE);
				SeekBar seek_3 = (SeekBar) rootView.findViewById(R.id.seek_3);
				seek_3.setVisibility(View.VISIBLE);
				seek_3.setOnSeekBarChangeListener(this);
				break;
			case 3:
				ImageView image_4 = (ImageView) rootView.findViewById(R.id.page_4);
				image_4.setVisibility(View.VISIBLE);
				SeekBar seek_4 = (SeekBar) rootView.findViewById(R.id.seek_4);
				seek_4.setVisibility(View.VISIBLE);
				seek_4.setOnSeekBarChangeListener(this);
				break;
			}
			return rootView;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			switch(seekBar.getId()){
			case R.id.seek_1:
				val1 = progress;
				break;
			case R.id.seek_2:
				val2 = progress;
				break;
			case R.id.seek_3:
				val3 = progress;
				break;
			case R.id.seek_4:
				val4 = progress;
				break;
			}
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			switch(seekBar.getId()){
			case R.id.seek_1:
				progress.setText(val1 + "/" + seekBar.getMax());
				break;
			case R.id.seek_2:
				progress.setText(val2 + "/" + seekBar.getMax());
				break;
			case R.id.seek_3:
				progress.setText(val3 + "/" + seekBar.getMax());
				break;
			case R.id.seek_4:
				progress.setText(val4 + "/" + seekBar.getMax());
				break;
			}
		}
	}
	
	public static class LastFragment extends Fragment implements OnSeekBarChangeListener{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		public TextView progress;
		
		public LastFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_review_dummy,
					container, false);
			TextView remember = (TextView) rootView
					.findViewById(R.id.review);
			remember.setText(R.string.review);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.review_text);
			dummyTextView.setText(getArguments().getString(
					ARG_SECTION_NUMBER));
			Button end_button = (Button) rootView.findViewById(R.id.end_review);
			end_button.setVisibility(View.VISIBLE);
			progress = (TextView) rootView.findViewById(R.id.progress);
			ImageView image = (ImageView) rootView.findViewById(R.id.page_5);
			image.setVisibility(View.VISIBLE);
			SeekBar seek_5 = (SeekBar) rootView.findViewById(R.id.seek_5);
			seek_5.setVisibility(View.VISIBLE);
			seek_5.setOnSeekBarChangeListener(this);
			return rootView;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			val5 = progress;
			
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
				progress.setText(val5 + "/" + seekBar.getMax());			
		}

	}
	

	public void endActivity(View view)
	{
		Intent currActivity = new Intent(this, EndActivity.class);
		currActivity.putExtra("theme",  theme);
		currActivity.putExtra("val1",  val1);
		currActivity.putExtra("val2",  val2);
		currActivity.putExtra("val3",  val3);
		currActivity.putExtra("val4",  val4);
		currActivity.putExtra("val5",  val5);
		currActivity.putExtra("act_id", activity_id);
		startActivity(currActivity);
	}
}
