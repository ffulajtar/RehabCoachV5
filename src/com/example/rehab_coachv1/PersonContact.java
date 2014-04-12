///**
// * 
// */
//package com.example.rehab_coachv1;
//
//import android.content.Context;
//import android.database.sqlite.*;
//import android.provider.BaseColumns;
//
///**
// * @author ryan
// *
// */
//public class PersonContact {
//	public PersonContact(){}
//
//	public static abstract class PersonEntry implements BaseColumns {
//		public static final String TABLE_NAME = "person";
//		public static final String COLUMN_NAME_PERSON_ID = "id";
//		public static final String COLUMN_NAME_NAME = "name";
//		public static final String COLUMN_NAME_PASSWORD = "password";
//		public static final String COLUMN_NAME_ADDRESS = "address";
//		public static final String COLUMN_NAME_EMAIL = "email";
//		public static final String COLUMN_NAME_HOME_PHONE = "home";
//		public static final String COLUMN_NAME_MOBILE_PHONE = "mobile";		
//	}
//
//	private static final String TEXT_TYPE = " TEXT";
//	private static final String COMMA_SEP = ",";
//	private static final String SQL_CREATE_ENTRIES =
//			"CREATE TABLE " + PersonEntry.TABLE_NAME + " (" +
//					PersonEntry._ID + " INTEGER PRIMARY KEY," +
//					PersonEntry.COLUMN_NAME_PERSON_ID + TEXT_TYPE + COMMA_SEP +
//					PersonEntry.COLUMN_NAME_PASSWORD + TEXT_TYPE + COMMA_SEP +
//					PersonEntry.COLUMN_NAME_NAME + TEXT_TYPE + COMMA_SEP +
//					PersonEntry.COLUMN_NAME_ADDRESS + TEXT_TYPE + COMMA_SEP +
//					PersonEntry.COLUMN_NAME_EMAIL+ TEXT_TYPE + COMMA_SEP +
//					PersonEntry.COLUMN_NAME_HOME_PHONE + TEXT_TYPE + COMMA_SEP +
//					PersonEntry.COLUMN_NAME_MOBILE_PHONE + TEXT_TYPE +
//					")";
//
//	private static final String SQL_DELETE_ENTRIES =
//			"DROP TABLE IF EXISTS " + PersonEntry.TABLE_NAME;
//
//	public static class PersonDbHelper extends SQLiteOpenHelper {
//		// If you change the database schema, you must increment the database version.
//		public static final int DATABASE_VERSION = 1;
//		public static final String DATABASE_NAME = "Person.db";
//
//		public PersonDbHelper(Context context) {
//			super(context, DATABASE_NAME, null, DATABASE_VERSION);
//		}
//		public void onCreate(SQLiteDatabase db) {
//			db.execSQL(SQL_CREATE_ENTRIES);
//		}
//		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			// This database is only a cache for online data, so its upgrade policy is
//			// to simply to discard the data and start over
//
//			//will change this policy later, this is just for toying atm.
//
//			db.execSQL(SQL_DELETE_ENTRIES);
//			onCreate(db);
//		}
//		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//			onUpgrade(db, oldVersion, newVersion);
//		}
//	}
//
//}

