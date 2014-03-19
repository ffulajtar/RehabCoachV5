package com.example.rehab_coachv1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class AdminActivity extends Activity {

	int theme = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		theme = getIntent().getIntExtra("theme", 1);
		if (theme == 0)
		{
			setTheme(android.R.style.Theme_Holo_Light);
		}
		else
		{
			setTheme(android.R.style.Theme_Holo);
		}
		setContentView(R.layout.activity_admin);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.admin, menu);
		return true;
	}
	
	public void createProfile(View view) {
		Intent create = new Intent(this, CreateProfileActivity.class);
		create.putExtra("theme", theme);
		startActivity(create);
	}

	public void editActivities(View view) {
		Intent home = new Intent(this, HomeActivity.class);
		home.putExtra("theme", theme);
		startActivity(home);
	}

	public void editImpairments(View view) {
		Intent home = new Intent(this, HomeActivity.class);
		home.putExtra("theme", theme);
		startActivity(home);
	}

}
