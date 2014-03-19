package com.example.rehab_coachv1;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AllActivitiesActivity extends Activity {

	ArrayList<String> all_activity_names_list = new ArrayList<String>();
	ArrayList<Integer> all_activity_ids_list = new ArrayList<Integer>();
	
	
	int theme = 0;
	private SQLiteDatabase database;
	
	
	private void getAllActivities(){
		ExternalDbOpenHelper dbHelper = new ExternalDbOpenHelper(this, "rehab_coach");
		database = dbHelper.openDataBase();
				
		Cursor activityCursor = database.query("activity", new String[]{"_id","name"}, null,null,null,null,null);
		activityCursor.moveToFirst();
		
		while(!activityCursor.isAfterLast()){
			
			/*This is a hacky way to keep track of what names and id's go with the activities that are being represented on the screen.
			Because the ArrayAdapter needed an array of Strings, it was easier to do this than to make an array of tuples and try to decompose them for the String ArrayAdapter */
			all_activity_names_list.add(activityCursor.getString(activityCursor.getColumnIndex("name")));
			all_activity_ids_list.add(activityCursor.getInt(activityCursor.getColumnIndex("_id")));

			activityCursor.moveToNext();
		}
	}
	
	public void goHome(View view)
	{
		Intent home = new Intent(this, HomeActivity.class);
		home.putExtra("theme", theme);
		startActivity(home);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getAllActivities();
		
		theme = getIntent().getIntExtra("theme", 0);
		if (theme == 0)
		{
			setTheme(android.R.style.Theme_Holo_Light);
		}
		else
		{
			setTheme(android.R.style.Theme_Holo);
		}
		setContentView(R.layout.activity_all);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.home_list_layout, all_activity_names_list);

		ListView listView = (ListView) findViewById(R.id.list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent remind = new Intent (AllActivitiesActivity.this, ReminderActivity.class);

				remind.putExtra("act", all_activity_names_list.get(position));
				
				remind.putExtra("theme", theme);
				
				remind.putExtra("act_id", all_activity_ids_list.get(position));
				
				
				startActivity(remind);
			}
		});
	}
	
	public void showAllActivities(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		getAllActivities();
		
		theme = getIntent().getIntExtra("theme", 0);
		if (theme == 0)
		{
			setTheme(android.R.style.Theme_Holo_Light);
		}
		else
		{
			setTheme(android.R.style.Theme_Holo);
		}
		setContentView(R.layout.activity_home);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.home_list_layout, all_activity_names_list);

		ListView listView = (ListView) findViewById(R.id.list);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Intent remind = new Intent (AllActivitiesActivity.this, ReminderActivity.class);
				
				remind.putExtra("act", all_activity_names_list.get(position));
				
				remind.putExtra("theme", theme);
				
				remind.putExtra("act_id", all_activity_ids_list.get(position));
				
				
				startActivity(remind);
			}
		});
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

	public void changetheme(View view)
	{
		if (theme == 0)
		{
			theme = 1;
			Intent remind = new Intent (this, HomeActivity.class);
			remind.putExtra("theme", theme);
			startActivity(remind);
		}
		else
		{
			theme = 0;
			Intent remind = new Intent (this, HomeActivity.class);
			remind.putExtra("theme", theme);
			startActivity(remind);
		}
	}

}
