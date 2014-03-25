package com.example.rehab_coachv1;

import com.example.rehab_coachv1.PersonContract.PersonDbHelper;
import com.example.rehab_coachv1.PersonContract.PersonEntry;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;

public class CreateProfileActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Holo);
		setContentView(R.layout.activity_create_profile);
		PersonDbHelper mDbHelper = new PersonDbHelper(this.getBaseContext());
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
		String id = "1";
		String name = "Ryan Gibson";
		
		String address = "7854 Solitude Drive";
		String hPhone = "3303283265";
		String mPhone = "3303283265";
		String email = "rgibson518@gmail.com";
		String password = "password";
		
		ContentValues values = new ContentValues();
		values.put(PersonEntry.COLUMN_NAME_PERSON_ID, id);
		values.put(PersonEntry.COLUMN_NAME_NAME, name);
		values.put(PersonEntry.COLUMN_NAME_ADDRESS, address);
		values.put(PersonEntry.COLUMN_NAME_HOME_PHONE, hPhone);
		values.put(PersonEntry.COLUMN_NAME_EMAIL, email);
		values.put(PersonEntry.COLUMN_NAME_MOBILE_PHONE, mPhone);
		values.put(PersonEntry.COLUMN_NAME_PASSWORD, password);

		// Insert the new row, returning the primary key value of the new row
		long newRowId;
		newRowId = db.insert(
		         PersonEntry.TABLE_NAME,
		         null,
		         values);
		
		System.out.println("Added user. Key == " + newRowId);
		
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_profile, menu);
		return true;
	}

}
