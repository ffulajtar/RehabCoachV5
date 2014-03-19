package com.example.rehab_coachv1;

import android.app.Activity;
import android.content.ClipData.Item;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class LoginActivity extends Activity {

	int theme = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		theme = getIntent().getIntExtra("theme", 1);
		if (theme == 0)
		{
			setTheme(android.R.style.Theme_Holo_Light);
			setContentView(R.layout.activity_login_dark);
		}
		else
		{
			setTheme(android.R.style.Theme_Holo);
			setContentView(R.layout.activity_login_light);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.login, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	
	public void loginToApp(View view)
	{
		Intent home = new Intent(this, HomeActivity.class);
		home.putExtra("theme", theme);
		startActivity(home);
	}
	
	public void adminMode(View view)
	{
		Intent admin = new Intent(this, AdminActivity.class);
		admin.putExtra("theme", theme);
		startActivity(admin);
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
