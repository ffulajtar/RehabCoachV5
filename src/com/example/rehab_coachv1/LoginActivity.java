package com.example.rehab_coachv1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class LoginActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Holo);
		setContentView(R.layout.activity_login_light);

		
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
		startActivity(home);
	}
	
	public void adminMode(View view)
	{
		Intent admin = new Intent(this, AdminActivity.class);
		startActivity(admin);
	}
	
}
