package com.example.rehab_coachv1;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class EndActivity extends Activity {

	int val1 = 0;
	int val2 = 0;
	int val3 = 0;
	int val4 = 0;
	int val5 = 0;
	int activity_id;
	
	private SQLiteDatabase database;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		val1 = getIntent().getIntExtra("val1", 99);
		val2 = getIntent().getIntExtra("val2", 99);
		val3 = getIntent().getIntExtra("val3", 99);
		val4 = getIntent().getIntExtra("val4", 99);
		val5 = getIntent().getIntExtra("val5", 99);
		activity_id = getIntent().getIntExtra("act_id", 0);
		//updateCompletedActivity();
		setTheme(android.R.style.Theme_Holo);
		setContentView(R.layout.activity_end);
		rotate();	
	}

	private void updateCompletedActivity(){
		ExternalDbOpenHelper dbHelper = new ExternalDbOpenHelper(this, "rehab_coach");
		database = dbHelper.openDataBase();
		//Get current miliseconds since epoch
		long new_most_recent_completion = System.currentTimeMillis();
		
		//Get old times completed and increment
		Cursor activityCursor = database.rawQuery("select times_complete from activity where _id = ?", new String[]{Integer.toString(activity_id)});
		activityCursor.moveToFirst();
		int times_complete = activityCursor.getInt(activityCursor.getColumnIndex("times_complete"));
		times_complete++;
		
		//place these values in approriate place
		
		ContentValues cv = new ContentValues();
		cv.put("times_complete", times_complete);
		cv.put("last_time_completed", new_most_recent_completion);
		database.update("activity", cv, "_id = ?", new String[]{Integer.toString(activity_id)});
		
//		Cursor changeCursor = database.rawQuery("update activity set times_complete = ? where _id = ?", new String[]{Integer.toString(times_complete), Integer.toString(activity_id)});
//		changeCursor = database.rawQuery("update activity set last_time_completed = ? where _id = ?", new String[]{Long.toString(new_most_recent_completion), Integer.toString(activity_id)}).;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dark, menu);
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
		startActivity(remind);
		
	}

	public void openProfile() {
		Intent remind = new Intent (this, ProfileActivity.class);
		startActivity(remind);
		
	}

	public void openHelp() {
		Intent remind = new Intent (this, HelpActivity.class);
		startActivity(remind);	
	}
	
	public void openSettings() {
		Intent remind = new Intent (this, SettingsActivity.class);
		startActivity(remind);	
	}
	
	public void endActivity(View view)
	{
		Intent currActivity = new Intent(this, HomeActivity.class);
		startActivity(currActivity);
	}
	
	public void rotate()
	{
		ImageView image = (ImageView) findViewById(R.id.imageView1);
		Animation hyperspaceUp = AnimationUtils.loadAnimation(this, R.anim.sunspin);
		hyperspaceUp.setRepeatCount(4);
		image.startAnimation(hyperspaceUp);
	}
}
