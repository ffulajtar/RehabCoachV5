//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//private static final String LOG_TAG = "ExampleApp";
//
//private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
//private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
//private static final String OUT_JSON = "/json";
//
//private static final String API_KEY = "YOUR_API_KEY";
//
//private ArrayList<String> autocomplete(String input) {
//    ArrayList<String> resultList = null;
//
//    HttpURLConnection conn = null;
//    StringBuilder jsonResults = new StringBuilder();
//    try {
//        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
//        sb.append("?sensor=false&key=" + API_KEY);
//        sb.append("&components=country:uk");
//        sb.append("&input=" + URLEncoder.encode(input, "utf8"));
//
//        URL url = new URL(sb.toString());
//        conn = (HttpURLConnection) url.openConnection();
//        InputStreamReader in = new InputStreamReader(conn.getInputStream());
//
//        // Load the results into a StringBuilder
//        int read;
//        char[] buff = new char[1024];
//        while ((read = in.read(buff)) != -1) {
//            jsonResults.append(buff, 0, read);
//        }
//    } catch (MalformedURLException e) {
//        Log.e(LOG_TAG, "Error processing Places API URL", e);
//        return resultList;
//    } catch (IOException e) {
//        Log.e(LOG_TAG, "Error connecting to Places API", e);
//        return resultList;
//    } finally {
//        if (conn != null) {
//            conn.disconnect();
//        }
//    }
//
//    try {
//        // Create a JSON object hierarchy from the results
//        JSONObject jsonObj = new JSONObject(jsonResults.toString());
//        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
//
//        // Extract the Place descriptions from the results
//        resultList = new ArrayList<String>(predsJsonArray.length());
//        for (int i = 0; i < predsJsonArray.length(); i++) {
//            resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
//        }
//    } catch (JSONException e) {
//        Log.e(LOG_TAG, "Cannot process JSON results", e);
//    }
//
//    return resultList;
//}
//
//
//
//
//
//private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
//    private ArrayList<String> resultList;
//
//    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
//        super(context, textViewResourceId);
//    }
//
//    @Override
//    public int getCount() {
//        return resultList.size();
//    }
//
//    @Override
//    public String getItem(int index) {
//        return resultList.get(index);
//    }
//
//    @Override
//    public Filter getFilter() {
//        Filter filter = new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                FilterResults filterResults = new FilterResults();
//                if (constraint != null) {
//                    // Retrieve the autocomplete results.
//                    resultList = autocomplete(constraint.toString());
//
//                    // Assign the data to the FilterResults
//                    filterResults.values = resultList;
//                    filterResults.count = resultList.size();
//                }
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                if (results != null && results.count > 0) {
//                    notifyDataSetChanged();
//                }
//                else {
//                    notifyDataSetInvalidated();
//                }
//            }};
//        return filter;
//    }
//}


package com.example.rehab_coachv1;

/*
 * TODO
 * 
 * CHECK FOR NETWORK CONNECTION ON MAP
 * -ON LOST SCREEN, NEED ANOTHER FLOW ABILITY.  LIKE "BACK" or "CLOSE"
 * -GET BETTER BITMAPS FROM CHARLIE
 * -Get AutoComplete to allow for more than 1 word***************
 * -Convert feel to miles
 * -Add buttins for transit mode
 * -Make button to reroute in during page
 * -Add trip distance and time wben directions first display
 * -Change icons
 * -Create screen before this activity to ask if they want directions
 * -use this on get me home screen and im lost screen
 * -Add key to directions API calls
 * -If GPS is shut off, it crashes  
 * - Zoom back in on location when travel begins
 * -Make Review and Reminder screens locked in portrait mode, bc they get fucked up in landscape.
 * -want to add an animation for "YOu reached your destination"
 * -Make keyboard dissapear on "done" button
 * -Sometimes map doesnt zoom, seems like it might be when it is come back to afte being created
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class DuringActivity extends FragmentActivity implements LocationListener, LocationSource {
	private static final String LOG_TAG = null;

	AutoCompleteTextView atvPlaces;

	DownloadTask placesDownloadTask;
	DownloadTask placeDetailsDownloadTask;
	ParserTask placesParserTask;
	ParserTask placeDetailsParserTask;

	
	private String PLACES_API_KEY ="AIzaSyBQ8Bqk6UjtkOwQVb7Mffdf2GHBXK2lkR";
	
	final int PLACES=0;
	final int PLACES_DETAILS=1;

	private boolean directionsInProgress = false;
	private List<DirectionsStep> directionsSteps = new ArrayList<DirectionsStep>();
	
	protected LocationManager locationManager;
	protected OnLocationChangedListener changeListener;
 
	private String activity_name;
	private int activity_id;
	private int currentStep;

	protected GoogleMap mMap;
	private Location currentLoc;
	private TextView directionText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Holo);
		setContentView(R.layout.activity_during);
		/*
		 * directionText will be the TextView used to display the navigation instructions to the user
		 */
		directionText = (TextView) findViewById(R.id.direction_display);
		boolean locationEnabled = true;

		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

		if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();

		}
		else{
			
			/*
			 * Now, we need to get the map from the view, as well as the autocompletetextview.
			 */
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			if(mMap != null){
				mMap.setMyLocationEnabled(true);
				mMap.setLocationSource(this);
			}

			activity_id = getIntent().getIntExtra("act_id",0);
			activity_name = getIntent().getStringExtra("act");


			atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
			atvPlaces.setThreshold(1);
			/*
			 * Now, add the textchanged listener to the autocomplete, so that it will attemp to autocomplete when the 
			 * user enters a character(s)
			 */
			atvPlaces.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					// Creating a DownloadTask to download Google Places matching "s"
					placesDownloadTask = new DownloadTask(PLACES);

					// Creating a Url to hit the Autocomplete server. The url uses the partially typed search form the autocomplete view
					
					/*
					 * This will return a lengthy url, similar to 
					 * https://maps.googleapis.com/maps/api/place/autocomplete/json?input=kroger+pha&
					 * 		sensor=true&key=AIzaSyBQ8Bqk6UjtkOwQVb7Mffdf2GHBXK2lkRE&location=39.9603,-83.0347&radius=600
					 */
					
					String url = getAutoCompleteUrl(s.toString());

					// execute  placesDownloadTask so that it may asynchronously fetch the most relevant places
					placesDownloadTask.execute(url);
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {}

				@Override
				public void afterTextChanged(Editable s) {}
			});

			// Setting an item click listener for the AutoCompleteTextView dropdown list
			//When any of the selections that are populated onto the dropdown list are selected, this
			//listener will come into action, using the information for the relevant selection
			atvPlaces.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int index,
						long id) {

					//    				ListView lv = (ListView) arg0;
					SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();  //***********************FUCK W THIS

					@SuppressWarnings("unchecked")
					HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);

					// Creating a DownloadTask to download Places details of the selected place
					placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

					// Getting url to the Google Places details api
					String url = getPlaceDetailsUrl(hm.get("reference"));
					
					/*
					 * The url created by getPlacesDetailsUrl takes reference, which is part of the JSON array
					 * returned by autocomplete. It returns a url like the following,
					 * 
					 * https://maps.googleapis.com/maps/api/place/details/json?reference=CoQBdwAAAD0Ds
					 * fgLjLq93ClH89FY6maXHP-x2kujRdiAIYV1HdNrb(...)hROoTqmK0qyO2C-43MMWzk2x70D1A
					 * &sensor=true&key=<YOUR KEY>
					 * I drastically shortened the reference field in that url
					 * 
					 * Now, this Url is ready to be sent to placeDetailsDownloadTask
					 * which is a downloadTask with the parameter (PLACES_DETAILS).
					 * It will asynchronously parse out the locations and get their details
					 */
					placeDetailsDownloadTask.execute(url);

				}
			});

			/*
			 * COOOMMMMEENT***************
			 */
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			if(locationManager != null)
			{
				Looper looper = Looper.getMainLooper();
				boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
				boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

				if(gpsIsEnabled)
				{
					locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, looper);
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
					locationEnabled = true;

				}
				else if(networkIsEnabled)
				{
					locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, looper);
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
					locationEnabled = true;
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
			//		setUpMap();
			if (locationEnabled == true){
				Location locToAdd = getLastBestLocation();

				currentLoc = locToAdd;
				onLocationChanged( locToAdd );

				//    		Move the camera to the user's location once it's available
				CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(locToAdd.getLatitude(), locToAdd.getLongitude()));
				CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
				mMap.animateCamera(center);
				mMap.moveCamera(zoom);
			}
		}
	}
	// Fetches data from url passed
		private class DownloadTask extends AsyncTask<String, Void, String>{

			private int downloadType=0;

			// Constructor
			public DownloadTask(int type){
				this.downloadType = type;
			}

			/*
			 * This will asynchronously make the http call and get whatever data is to be fetched from the URL
			 */
			@Override
			protected String doInBackground(String... url) {

				// For storing data from web service
				String data = "";

				try{
					// Fetching the data from web service
					data = downloadUrl(url[0]);
				}catch(Exception e){
					Log.d("Background Task",e.toString());
				}
				return data;
			}

			@Override
			protected void onPostExecute(String result) {        	
				super.onPostExecute(result);

				switch(downloadType){
				case PLACES:
					// Creating ParserTask for parsing Google Places
					placesParserTask = new ParserTask(PLACES);

					// Start parsing google places json data
					// This causes to execute doInBackground() of ParserTask class
					placesParserTask.execute(result);

					break;

				case PLACES_DETAILS :
					// Creating ParserTask for parsing Google Places
					placeDetailsParserTask = new ParserTask(PLACES_DETAILS);

					// Starting Parsing the JSON string
					// This causes to execute doInBackground() of ParserTask class
					placeDetailsParserTask.execute(result);

					break;

				default :
					
					//Directions case
					DirectionsParserTask parserTask = new DirectionsParserTask();
					PolylineParserTask lineTask = new PolylineParserTask();
					
					// Invoke the threads for parsing the JSON data
					lineTask.execute(result);
					parserTask.execute(result);
					break;
				}
			}
		}

		
		private String getPlaceDetailsUrl(String ref){

			// Obtain browser key from https://code.google.com/apis/console
			String key = "key=AIzaSyBQ8Bqk6UjtkOwQVb7Mffdf2GHBXK2lkRE";

			// reference of place
			String reference = "reference="+ref;

			// Sensor enabled
			String sensor = "sensor=true";

			// Building the parameters to the web service
			String parameters = reference+"&"+sensor+"&"+key;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/details/"+output+"?"+parameters;

			return url;
		}
	
	private String getAutoCompleteUrl(String place){

		// Obtain browser key from https://code.google.com/apis/console
		String key = "key=AIzaSyBQ8Bqk6UjtkOwQVb7Mffdf2GHBXK2lkRE";

		// place to be be searched
		String input = "input="+place;

		// Sensor enabled
		String sensor = "sensor=true";

		// Building the parameters to the web service
		String parameters = input+"&"+sensor+"&"+key;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"+output+"?"+parameters;

		return url;
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

			StringBuffer sb  = new StringBuffer();

			String line = "";
			while( ( line = br.readLine())  != null){
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

	
	
	
	
	

	/********************************************************************************************
	 * Map and Location Functions
	 */
	
	public void setUpMap(){
		if(mMap == null){
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			if(mMap != null){
				mMap.setMyLocationEnabled(true);
				mMap.setLocationSource(this);
			}
		}
	}
	
	private Location getLastBestLocation() {
		Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		long GPSLocationTime = 0;
		if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

		long NetLocationTime = 0;

		if (null != locationNet) {
			NetLocationTime = locationNet.getTime();
		}

		if ( 0 < GPSLocationTime - NetLocationTime ) {
			return locationGPS;
		}
		else {
			return locationNet;
		}
	}



	@Override
	public void onLocationChanged(Location location) 
	{
		if( changeListener != null )
		{
			changeListener.onLocationChanged( location );
			currentLoc = location;
			//Move the camera to the user's location once it's available
			CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
//			CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
			mMap.moveCamera(center);
//			mMap.moveCamera(zoom);
		
			if (directionsInProgress){
				//Check Proximity to any of the beginning points
				//If near one, set current step to that step's index
				//Display the instructions for the next step
				checkProximityToNextStep(location);
				
			}
		}
	}
	
	private void checkProximityToNextStep(Location location){
		DirectionsStep step; 
		if(currentStep < directionsSteps.size()-1) step = directionsSteps.get(currentStep+1);
		else step = directionsSteps.get(currentStep);
		//if location is within 20 meters of beginning of step, change text display
		if (getDistance(location, step.startLocation) < 40){
			//change textview
			String directions;
			currentStep = directionsSteps.indexOf(step);
			if(currentStep < directionsSteps.size()-1) 	directions = step.HtmlInstructions + " for " + step.distance + ", then " + directionsSteps.get(currentStep+1).HtmlInstructions;
			else directions = step.HtmlInstructions;
			directions.trim();
			directionText.setText(directions);
		}
	}
			
	private double rad (double x){
		return x* Math.PI/180;
	}
	
	private double getDistance(Location loc1, Location loc2){
		double R = 6378137;
		double lat = rad(loc2.getLatitude()-loc1.getLatitude());
		double lon = rad(loc2.getLongitude()-loc1.getLongitude());
		double a = Math.sin(lat/2)*Math.sin(lat/2)+ Math.cos(rad(loc1.getLatitude())) * Math.cos(rad(loc2.getLatitude())) * Math.sin(lon/2)*Math.sin(lon/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double d = R * c;
		return d;
	}
	
	@Override
	public void onProviderDisabled(String provider) 
	{
		// TODO Auto-generated method stub
		Toast.makeText(this, "provider disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider) 
	{
		// TODO Auto-generated method stub
		Toast.makeText(this, "provider enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) 
	{
		// TODO Auto-generated method stub
		Toast.makeText(this, "status changed", Toast.LENGTH_SHORT).show();
	}


	/********************************************************************************************
	 * AutoComplete Functions
	 */


	


	private String getDirectionsUrl(LatLng origin,LatLng dest){

		// Origin of route
		String str_origin = "origin="+origin.latitude+","+origin.longitude;

		// Destination of route
		String str_dest = "destination="+dest.latitude+","+dest.longitude;

		// Sensor enabled
		String sensor = "sensor=true";
		
		String mode = "mode=walking";

		// Building the parameters to the web service
		String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+mode;

		// Output format
		String output = "json";
		


		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

		Log.d("Directions URL : ", url);
		return url;
	}
	
	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	
	private ArrayList<String> autocomplete(String input) {
	    ArrayList<String> resultList = null;

	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
	        sb.append("&input=" + URLEncoder.encode(input, "utf8"));

	        URL url = new URL(sb.toString());
	        conn = (HttpURLConnection) url.openConnection();
	        InputStreamReader in = new InputStreamReader(conn.getInputStream());

	        // Load the results into a StringBuilder
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	            jsonResults.append(buff, 0, read);
	        }
	    } catch (MalformedURLException e) {
	        Log.e(LOG_TAG, "Error processing Places API URL", e);
	        return resultList;
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Error connecting to Places API", e);
	        return resultList;
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }

	    try {
	        // Create a JSON object hierarchy from the results
	        JSONObject jsonObj = new JSONObject(jsonResults.toString());
	        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

	        // Extract the Place descriptions from the results
	        resultList = new ArrayList<String>(predsJsonArray.length());
	        for (int i = 0; i < predsJsonArray.length(); i++) {
	            resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
	        }
	    } catch (JSONException e) {
	        Log.e(LOG_TAG, "Cannot process JSON results", e);
	    }

	    return resultList;
	}
	
	
	
	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	    private ArrayList<String> resultList;

	    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
	        super(context, textViewResourceId);
	    }

	    @Override
	    public int getCount() {
	        return resultList.size();
	    }

	    @Override
	    public String getItem(int index) {
	        return resultList.get(index);
	    }

	    @Override
	    public Filter getFilter() {
	        Filter filter = new Filter() {
	            @Override
	            protected FilterResults performFiltering(CharSequence constraint) {
	                FilterResults filterResults = new FilterResults();
	                if (constraint != null) {
	                    // Retrieve the autocomplete results.
	                    resultList = autocomplete(constraint.toString());

	                    // Assign the data to the FilterResults
	                    filterResults.values = resultList;
	                    filterResults.count = resultList.size();
	                }
	                return filterResults;
	            }

	            @Override
	            protected void publishResults(CharSequence constraint, FilterResults results) {
	                if (results != null && results.count > 0) {
	                    notifyDataSetChanged();
	                }
	                else {
	                    notifyDataSetInvalidated();
	                }
	            }};
	        return filter;
	    }
	}
//	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
//    private ArrayList<String> resultList;
//
//    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
//        super(context, textViewResourceId);
//    }
//
//    @Override
//    public int getCount() {
//        return resultList.size();
//    }
//
//    @Override
//    public String getItem(int index) {
//        return resultList.get(index);
//    }
//
//    @Override
//    public Filter getFilter() {
//        Filter filter = new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                FilterResults filterResults = new FilterResults();
//                if (constraint != null) {
//                    // Retrieve the autocomplete results.
//                    resultList = autocomplete(constraint.toString());
//
//                    // Assign the data to the FilterResults
//                    filterResults.values = resultList;
//                    filterResults.count = resultList.size();
//                }
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                if (results != null && results.count > 0) {
//                    notifyDataSetChanged();
//                }
//                else {
//                    notifyDataSetInvalidated();
//                }
//            }};
//        return filter;
//    }
//}
	
	
	

	
	
	/** A class to parse the Google Directions in JSON format */
	private class DirectionsParserTask extends AsyncTask<String, Integer, List<DirectionsStep> >{

		// Parsing the data in non-ui thread
		@Override
		protected List<DirectionsStep> doInBackground(String... jsonData) {

			JSONObject jObject;
			List<DirectionsStep> route0 = new ArrayList<DirectionsStep>();

			try{
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				route0 = parser.getFirstRouteDirections(jObject);

			}catch(Exception e){
				e.printStackTrace();
			}
			return route0;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<DirectionsStep> result) {
			/*
			 * Set directionsInProgress to true
			 * add steps to some local variable
			 */
			directionsSteps.clear();
			directionsSteps.addAll(result);
			directionsInProgress = true;
			if (directionsSteps.size()>1){
				String direction = directionsSteps.get(0).HtmlInstructions + " for " + directionsSteps.get(0).distance +", then " + directionsSteps.get(1).HtmlInstructions;
				direction.trim();
				directionText.setText(direction);
			}
			else directionText.setText(directionsSteps.get(0).HtmlInstructions);
		}
	}


	private class PolylineParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try{
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.getPolies(jObject);
			}catch(Exception e){
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;

			Log.d("Mike Size", Integer.toString(result.size()));
			// Traversing through all the routes
			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(5);
				lineOptions.color(Color.RED);
			}
			
			// Drawing polyline in the Google Map for the i-th route
			mMap.addPolyline(lineOptions);
		}
	}

	/*
	 * TODO
	 * Figure out how to use boundaries to zoom in to appropriate level
	 */

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

		int parserType = 0;

		public ParserTask(int type){
			this.parserType = type;
		}

		@Override
		protected List<HashMap<String, String>> doInBackground(String... jsonData) {

			JSONObject jObject;
			List<HashMap<String, String>> list = null;

			try{
				jObject = new JSONObject(jsonData[0]);

				switch(parserType){
				case PLACES :
					PlaceJSONParser placeJsonParser = new PlaceJSONParser();
					// Getting the parsed data as a List construct
					list = placeJsonParser.parse(jObject);
					break;
				case PLACES_DETAILS :
					PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
					// Getting the parsed data as a List construct
					list = placeDetailsJsonParser.parse(jObject);
				}

			}catch(Exception e){
				Log.d("Exception",e.toString());
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			switch(parserType){
			case PLACES :
				String[] from = new String[] { "description"};
				int[] to = new int[] { android.R.id.text1 };

				// Creating a SimpleAdapter for the AutoCompleteTextView
				SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), result, android.R.layout.simple_list_item_1, from, to);

				// Setting the adapter
				atvPlaces.setAdapter(adapter);
				break;
			case PLACES_DETAILS :
				HashMap<String, String> hm = result.get(0);
				// Getting latitude from the parsed data
				double latitude = Double.parseDouble(hm.get("lat"));
				// Getting longitude from the parsed data
				double longitude = Double.parseDouble(hm.get("lng"));
				// Getting reference to the SupportMapFragment of the activity_main.xml
				SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

				// Getting GoogleMap from SupportMapFragment, and clear of any old markers/polylines
				mMap = fm.getMap();
				mMap.clear();
				
				
				/*
				 * TODO
				 * Change contents of marker 
				 * make screen zoom to correct level
				 * NOTE: 15 is good for showing person's location
				 */
				
				/*z
				 * 
				 * Now, calculate the size of the route, to determine appropriate zoom level.
				 */
				
				LatLngBounds.Builder builder = new LatLngBounds.Builder();
				LatLng current = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
				builder.include(current);
				builder.include(new LatLng(latitude,longitude));
				LatLngBounds bounds = builder.build();
				int padding = 150;
				CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, padding);
				

				mMap.animateCamera(update);
				

				LatLng point = new LatLng(latitude, longitude);
				MarkerOptions options = new MarkerOptions();
				options.position(point);

				options.title("Position");
				options.snippet("Latitude:"+latitude+",Longitude:"+longitude);
				// Adding the marker in the Google Map
				mMap.addMarker(options);
				
				
				
				
				
				//Hide the Soft Keyboard
				InputMethodManager imm = (InputMethodManager)getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);

				// Adding the marker in the Google Map
				


				//*******Get Directions
				LatLng origin = new LatLng(getLastBestLocation().getLatitude(), getLastBestLocation().getLongitude());

				String url = getDirectionsUrl(origin, point);

				DownloadTask directionsDownloadTask = new DownloadTask(-1);

				// Start downloading json data from Google Directions API
				directionsDownloadTask.execute(url);

				break;
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dark, menu);
		return super.onCreateOptionsMenu(menu);
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	
	
	@Override
	public void onPause()
	{
		if(locationManager != null)
		{
			locationManager.removeUpdates(this);
		}

		super.onPause();
	}

	@Override
	public void onResume()
	{
		super.onResume();
		setUpMap();

		if(locationManager != null)
		{
			mMap.setMyLocationEnabled(true);
		}
	}

	@Override
	public void activate(OnLocationChangedListener listener) 
	{
		changeListener = listener;

	}

	@Override
	public void deactivate() 
	{
		changeListener = null;
	}

	public void openHome() {
		Intent remind = new Intent (this, HomeActivity.class);
		startActivity(remind);

	}

	public void openProfile() {
		Intent remind = new Intent (this, ProfileActivity.class);
		startActivity(remind);

	}

	public void openHelp() {
		Intent remind = new Intent (this, HelpActivity.class);
		startActivity(remind);	
	}

	public void endActivity(View view)
	{
		Intent endActivity = new Intent(this, ReviewActivity.class);
		endActivity.putExtra("act", activity_name);
		endActivity.putExtra("act_id", activity_id);
		startActivity(endActivity);
	}

}
