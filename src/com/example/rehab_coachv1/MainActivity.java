package com.example.rehab_coachv1;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.rehab_coachv1.PersonContract.PersonDbHelper;
import com.example.rehab_coachv1.PersonContract.PersonEntry;

/**
 * The Main Activity.
 * };

 * This activity starts up the RegisterActivity immediately, which communicates
 * with your App Engine backend using Cloud Endpoints. It also receives push
 * notifications from backend via Google Cloud Messaging (GCM).
 * 
 * Check out RegisterActivity.java for more details.
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PersonDbHelper mDbHelper = new PersonDbHelper(this.getBaseContext());
		
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		
		Cursor c = db.rawQuery("SELECT * FROM "+ PersonEntry.TABLE_NAME, null);
		
		
		//if no user is present, create one
		if (c.getCount() == 0){
			System.out.println("No users found.");

			//go to creation
			Intent intent = new Intent(this, CreateProfileActivity.class);
			startActivity(intent);
			// Since this is just a wrapper to start the main activity,
			// finish it after launching RegisterActivity
			finish();
			
		} else {

		//go to log in with user importing existing project info
		c.moveToFirst();
		System.out.println("User("+ c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_NAME))+") found!");

		// Start up LoginActivity right away
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("EMAIL",c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_EMAIL))); 
		intent.putExtra("PASSWORD",c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_PASSWORD)));
		startActivity(intent);
	
		finish();
		}
	}
}
