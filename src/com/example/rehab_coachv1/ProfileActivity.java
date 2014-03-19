package com.example.rehab_coachv1;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ProfileActivity extends Activity {

	int theme = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		theme = getIntent().getIntExtra("theme", 0);
		if (theme == 0)
		{
			setTheme(android.R.style.Theme_Holo_Light);
		}
		else
		{
			setTheme(android.R.style.Theme_Holo);
		}
		setContentView(R.layout.activity_profile);
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
			Intent remind = new Intent (this, LoginActivity.class);
			remind.putExtra("theme", theme);
			startActivity(remind);
		}
		else
		{
			theme = 0;
			Intent remind = new Intent (this, LoginActivity.class);
			remind.putExtra("theme", theme);
			startActivity(remind);
		}
	}

}
