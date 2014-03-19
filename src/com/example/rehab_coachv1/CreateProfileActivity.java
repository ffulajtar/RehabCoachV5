package com.example.rehab_coachv1;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class CreateProfileActivity extends Activity {

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
		setContentView(R.layout.activity_create_profile);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_profile, menu);
		return true;
	}

}
