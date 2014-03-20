package com.example.rehab_coachv1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class AdminActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		startActivity(create);
	}

	public void editActivities(View view) {
		//Intent home = new Intent(this, HomeActivity.class);
		//startActivity(home);
	}

}
