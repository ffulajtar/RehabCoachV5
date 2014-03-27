package com.example.rehab_coachv1;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;

public class LocationSelectionActivity extends Activity implements LocationListener, LocationSource{

	AutoCompleteTextView atvPlaces;
	PlacesTask placesTask;
	ParserTask parserTask;
	Location mCurrentLocation;
	//LocationClient mLocationClient = new LocationClient(this, this, this);
	//	private static final String API_KEY = "AIzaSyBQ8Bqk6UjtkOwQVb7Mffdf2GHBXK2lkRE";

	protected GoogleMap mMap;
	protected LocationManager locationManager;
	protected OnLocationChangedListener changeListener;
	private String location;
	
	private String activity_name;
	private int activity_id;
	private String destination_latitude, destination_longitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Holo);
		setContentView(R.layout.activity_location_select);
		
		activity_id = getIntent().getIntExtra("act_id",0);
		activity_name = getIntent().getStringExtra("act");

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		if(locationManager != null)
		{
		      boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		        boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		        if(gpsIsEnabled)
		        {
		            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
		            mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		        }
		        else if(networkIsEnabled)
		        {
		            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
		            mCurrentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		        }
		        else
		        {
		            Log.d("Mike", "gps disabled");
		        }
		}
		else
		{
			Log.d("Mike", "LocationManager is null");
		}
		
		location = "location=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();
		atvPlaces = (AutoCompleteTextView) findViewById(R.id.to);
		atvPlaces.setThreshold(3);
		

		atvPlaces.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				placesTask = new PlacesTask();
				placesTask.execute(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try{
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while( ( line = br.readLine()) != null){
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		}catch(Exception e){
			Log.d("Exception while downloading url", e.toString());
		}finally{
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}


	public void goToDuringActivity(View view)
	{
		Intent currActivity = new Intent(this, DuringActivity.class);
		currActivity.putExtra("act", activity_name);
		currActivity.putExtra("act_id", activity_id);
		startActivity(currActivity);
		currActivity.putExtra("destination_lat", destination_latitude);
		currActivity.putExtra("destination_lon", destination_longitude);
	}

	// Fetches all places from GooglePlaces AutoComplete Web Service
	private class PlacesTask extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... place) {
			// For storing data from web service

			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "key=AIzaSyBQ8Bqk6UjtkOwQVb7Mffdf2GHBXK2lkRE";

			String input="";

			//place[0].replace(' ', '_');
			try {
				input = "input=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			//input.replace("+", "_");
			Log.d("Mike uri input", input);

			// Sensor enabled
			String sensor = "sensor=true";

			String radius = "radius=1000";
			// Building the parameters to the web service
			String parameters = input+"&"+sensor+"&"+key+ "&"+location + "&"+radius;
			
			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;			
			
			Log.d("Mike uri", url);

			try{
				// Fetching the data from service
				data = downloadUrl(url);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// Creating ParserTask
			parserTask = new ParserTask();

			// Starting Parsing the JSON string returned by Web Service
			parserTask.execute(result);
		}
	}
	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

		JSONObject jObject;

		@Override
		protected List<HashMap<String, String>> doInBackground(String... jsonData) {

			List<HashMap<String, String>> places = null;

			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try{
				jObject = new JSONObject(jsonData[0]);

				// Getting the parsed data as a List construct
				places = placeJsonParser.parse(jObject);

			}catch(Exception e){
				Log.d("Exception",e.toString());
			}
			Log.d("Mike autocomplete result size", Integer.toString(places.size()));
			return places;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			String[] from = new String[] { "description"};
			int[] to = new int[] { android.R.id.text1 };

			// Creating a SimpleAdapter for the AutoCompleteTextView
			SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

			// Setting the adapter
			atvPlaces.setAdapter(adapter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dark, menu);
		return super.onCreateOptionsMenu(menu);
		//        return true;
	}

	@Override
	public void activate(OnLocationChangedListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}