package com.example.rehab_coachv1;

/*
 * TODO
 * -Get AutoComplete to allow for more than 1 word
 * -Make directions interactive -> at least have them give updates
 * 		If this is not possible, may need to launch a google maps intent
 * -Change icons
 * -Create screen before this activity to ask if they want directions
 * -use this on get me home screen and im lost screen
 * -Add key to directions API calls
 * -Maybe add travel modes, if necessary 
 * -If GPS is shut off, it crashes
 * -Make Review and Reminder screens locked in portrait mode, bc they get fucked up in landscape.
 */


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
import android.widget.AutoCompleteTextView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class DuringActivity extends FragmentActivity implements LocationListener, LocationSource {
    AutoCompleteTextView atvPlaces;
    
    DownloadTask placesDownloadTask;
    DownloadTask placeDetailsDownloadTask;
    ParserTask placesParserTask;
    ParserTask placeDetailsParserTask;
 
    final int PLACES=0;
    final int PLACES_DETAILS=1;
    
	protected LocationManager locationManager;
	protected OnLocationChangedListener changeListener;

	private String activity_name;
	private int activity_id;

	protected GoogleMap mMap;
	private Location currentLoc;
	
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
    protected void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_during);
    	boolean locationEnabled = false;

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

    		activity_id = getIntent().getIntExtra("act_id",0);
    		activity_name = getIntent().getStringExtra("act");

    		// Getting a reference to the AutoCompleteTextView
    		atvPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
    		atvPlaces.setThreshold(1);

    		// Adding textchange listener
    		atvPlaces.addTextChangedListener(new TextWatcher() {

    			@Override
    			public void onTextChanged(CharSequence s, int start, int before, int count) {
    				// Creating a DownloadTask to download Google Places matching "s"
    				placesDownloadTask = new DownloadTask(PLACES);

    				// Getting url to the Google Places Autocomplete api
    				String url = getAutoCompleteUrl(s.toString());

    				// Start downloading Google Places
    				// This causes to execute doInBackground() of DownloadTask class
    				placesDownloadTask.execute(url);
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

    		// Setting an item click listener for the AutoCompleteTextView dropdown list
    		atvPlaces.setOnItemClickListener(new OnItemClickListener() {
    			@Override
    			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
    					long id) {

//    				ListView lv = (ListView) arg0;
    				SimpleAdapter adapter = (SimpleAdapter) arg0.getAdapter();

    				@SuppressWarnings("unchecked")
					HashMap<String, String> hm = (HashMap<String, String>) adapter.getItem(index);

    				// Creating a DownloadTask to download Places details of the selected place
    				placeDetailsDownloadTask = new DownloadTask(PLACES_DETAILS);

    				// Getting url to the Google Places details api
    				String url = getPlaceDetailsUrl(hm.get("reference"));

    				// Start downloading Google Place Details
    				// This causes to execute doInBackground() of DownloadTask class
    				placeDetailsDownloadTask.execute(url);

    			}
    		});

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
    			CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
    			mMap.moveCamera(center);
    			mMap.moveCamera(zoom);
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
 
    private String getDirectionsUrl(LatLng origin,LatLng dest){
    	 
        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;
 
        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;
 
        // Sensor enabled
        String sensor = "sensor=false";
 
        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;
 
        // Output format
        String output = "json";
 
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
 
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
                
                // Invokes the thread for parsing the JSON data
                parserTask.execute(result);
            	break;
            }
        }
    }
    
    /** A class to parse the Google Directions in JSON format */
    private class DirectionsParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
 
        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
 
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
 
            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
 
                // Starts parsing data
                routes = parser.parse(jObject);
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
 
                // Getting GoogleMap from SupportMapFragment
                mMap = fm.getMap();
 
                /*
                 * Calculate the focal point for map, so that center of route is in center of the map
                 */
                LatLng point = new LatLng(latitude, longitude);
 
                double focalLat = (latitude + currentLoc.getLatitude())/2;
                double focalLon = (longitude + currentLoc.getLongitude())/2;
                LatLng focalPoint = new LatLng(focalLat, focalLon);
                		
                /*
                 * Now, calculate the size of the route, to determine appropriate zoom level.
                 */
                int zoomLevel = 14;
                double latitudeDifference = Math.abs(latitude - currentLoc.getLatitude());
                double longitudeDifference = Math.abs(longitude - currentLoc.getLongitude());
                if(longitudeDifference > .06){
                	zoomLevel = 12;
                }
                else if(longitudeDifference > .02){
                	zoomLevel = 13;
                }

                CameraUpdate cameraPosition = CameraUpdateFactory.newLatLng(focalPoint);
                //Make this halfway between person and destination, or find some function to show both on screen
                CameraUpdate cameraZoom = CameraUpdateFactory.zoomTo(zoomLevel);
 
                // Showing the user input location in the Google Map
                mMap.moveCamera(cameraPosition);
                mMap.animateCamera(cameraZoom);
 
                MarkerOptions options = new MarkerOptions();
                options.position(point);
                options.title("Position");
                options.snippet("Latitude:"+latitude+",Longitude:"+longitude);
 
                //Hide the Soft Keyboard
            	InputMethodManager imm = (InputMethodManager)getSystemService(
          		      Context.INPUT_METHOD_SERVICE);
          		imm.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);
                
                // Adding the marker in the Google Map
                mMap.addMarker(options);
                
                
                //*******Get Directions
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dark, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
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
	public void onLocationChanged(Location location) 
	{
	    if( changeListener != null )
	    {
	        changeListener.onLocationChanged( location );
	        currentLoc = location;
	        //Move the camera to the user's location once it's available
	        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
	        CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
	        mMap.moveCamera(center);
	        mMap.moveCamera(zoom);
	    }
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

}
