
//private static final String LOG_TAG = "ExampleApp";
/*
 * Need to do a check that the person has address stored, before trying to display
 */


package com.example.rehab_coachv1;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class LostActivity extends FragmentActivity implements LocationListener, LocationSource {

	DownloadTask placesDownloadTask;
	DownloadTask placeDetailsDownloadTask;
	ParserTask placesParserTask;
	ParserTask placeDetailsParserTask;

	final int PLACES=0;
	final int PLACES_DETAILS=1;
	private LatLng currentLoc;
	private LatLng homeLatLng;

	private boolean directionsInProgress = false;
	private List<DirectionsStep> directionsSteps = new ArrayList<DirectionsStep>();

	protected LocationManager locationManager;
	protected OnLocationChangedListener changeListener;

	private String activity_name;
	private int activity_id;
	private int currentStep;

	protected GoogleMap mMap;
	private TextView directionText;
	private Geocoder geocoder;
	protected SQLiteDatabase database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Holo);
		setContentView(R.layout.activity_lost);
		directionText = (TextView) findViewById(R.id.direction_display);
		boolean locationEnabled = true;

		geocoder = new Geocoder(this); 


		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

		if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available

			int requestCode = 10;
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
			dialog.show();

		}
		else{
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			if(mMap != null){
				mMap.setMyLocationEnabled(true);
				mMap.setLocationSource(this);
			}

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

			if (locationEnabled == true){
				//Now, asyncronously plot markers for the user's location, and the user's home.
				//Then, draw a route between them, and start navigating
				MarkHomeAndUserGetDirections placeMarkers = new MarkHomeAndUserGetDirections(this);
				placeMarkers.execute();
			}
		}
	}
	
	
	
	
	
	

	private class MarkHomeAndUserGetDirections extends AsyncTask<Void, Void, List<MarkerOptions>>{//List<MarkerOptions>> {

		protected SQLiteDatabase database;
		//		public GoogleMap map;

		Context c;
		public MarkHomeAndUserGetDirections(Context context){//, GoogleMap map){
			c = context;
//						this.map = map;
		}

		@Override
		protected List<MarkerOptions> doInBackground(Void... arg0) {
			List<MarkerOptions> listToReturn = new ArrayList<MarkerOptions>();

			ExternalDbOpenHelper dbHelper = new ExternalDbOpenHelper(c, "rehab_coach");
			Log.d("Mike DBOpen", dbHelper.getDatabaseName());
			database = dbHelper.openDataBase();
			/*
			 * This query is still assuming that the user is the only person stored in person
			 */
			Cursor addressCursor = database.rawQuery("select address from person where _id = ?", new String[]{"1"});
			Log.d("Mike Cursor", Integer.toString(addressCursor.getCount()));
			addressCursor.moveToFirst();
			String address =  addressCursor.getString( addressCursor.getColumnIndex("address"));


			List<Address> addresses = new ArrayList<Address>();
			try {
				addresses = geocoder.getFromLocationName(address, 1);

				if(addresses.size() > 0) {
					double latitude= addresses.get(0).getLatitude();
					double longitude= addresses.get(0).getLongitude();


					homeLatLng = new LatLng(latitude, longitude);
					MarkerOptions homeOptions = new MarkerOptions();
					homeOptions.position(homeLatLng);
					homeOptions.title("Your Home");
					homeOptions.snippet(address);
					homeOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_home));
					// Adding the marker in the list
					listToReturn.add(homeOptions);

					Location userLoc = getLastBestLocation();
					LatLng userPoint = new LatLng(userLoc.getLatitude(), userLoc.getLongitude());
					MarkerOptions userOptions = new MarkerOptions();
					userOptions.position(userPoint);
					userOptions.title("Your Location");
					//					userOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_you_are_here));
					// Adding the marker in the list
					listToReturn.add(userOptions);

				}	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			database.close();
			addressCursor.close();
			return listToReturn;
		}

		@Override
		protected void onPostExecute(List<MarkerOptions> result){
			//			SupportMapFragment fm = new SupportMapFragment();
			//			fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

			// Getting GoogleMap from SupportMapFragment, and clear of any old markers/polylines
			//			mMap = fm.getMap();
			//			mMap.clear();

			Log.d("Mike result size", Integer.toString(result.size()));

			MarkerOptions marker1 = new MarkerOptions();
			marker1 = result.get(0);
			MarkerOptions marker2 = new MarkerOptions();
			marker2 = result.get(1);
			mMap.addMarker(marker1);
			mMap.addMarker(marker2);
			
			
			
			/*
			 * Now get the directions to home, and place route on the map
			 */
			
			LatLng origin = new LatLng(getLastBestLocation().getLatitude(), getLastBestLocation().getLongitude());

			String url = getDirectionsUrl(origin, homeLatLng);
			DownloadTask directionsDownloadTask = new DownloadTask(-1);
			directionsDownloadTask.execute(url);
			

			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			builder.include(origin);
			builder.include(homeLatLng);
			LatLngBounds bounds = builder.build();
			int padding = 150;
			CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, padding);
			mMap.animateCamera(update);		
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


	@Override
	public void onLocationChanged(Location location) 
	{
		if( changeListener != null )
		{
			changeListener.onLocationChanged( location );
			currentLoc = new LatLng(location.getLatitude(),location.getLongitude());
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

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>{

		private int downloadType=0;

		// Constructor
		public DownloadTask(int type){
			this.downloadType = type;
		}

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
				break;
			case PLACES_DETAILS :
				HashMap<String, String> hm = result.get(0);

				// Getting latitude from the parsed data
				double latitude = Double.parseDouble(hm.get("lat"));

				// Getting longitude from the parsed data
				double longitude = Double.parseDouble(hm.get("lng"));


				LatLng point = new LatLng(latitude, longitude);
				MarkerOptions options = new MarkerOptions();
				options.position(point);

				options.title("Position");
				options.snippet("Latitude:"+latitude+",Longitude:"+longitude);
				// Adding the marker in the Google Map
				mMap.addMarker(options);


				//Get Directions
				LatLng origin = new LatLng(getLastBestLocation().getLatitude(), getLastBestLocation().getLongitude());

				String url = getDirectionsUrl(origin, point);

				DownloadTask downloadTask = new DownloadTask(-1);

				// Start downloading json data from Google Directions API
				downloadTask.execute(url);

				break;
			}
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
		//setUpMap();

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



	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

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
}


















