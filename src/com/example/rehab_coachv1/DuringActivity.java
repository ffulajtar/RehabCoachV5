package com.example.rehab_coachv1;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;


public class DuringActivity extends FragmentActivity implements LocationListener, LocationSource {

	String activity_name;
	int activity_id;


	protected GoogleMap mMap;
	protected LocationManager locationManager;
	protected OnLocationChangedListener changeListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity_id = getIntent().getIntExtra("act_id", 0);
		setTheme(android.R.style.Theme_Holo);
		setContentView(R.layout.activity_during);
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		if(locationManager != null)
		{
		      boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		        boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		        if(gpsIsEnabled)
		        {
		            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
		        }
		        else if(networkIsEnabled)
		        {
		            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
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
		
		
		setUpMap();
	
//		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
//		mMap.setMyLocationEnabled(true);
		
//		mMap.addMarker(new MarkerOptions()
//		        .position(new LatLng(0, 0))
//		        .title("Hello world"));
//		activity_name = getIntent().getStringExtra("act");
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

	public void setUpMap(){
		if(mMap == null){
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			if(mMap != null){
				mMap.setMyLocationEnabled(true);
				mMap.setLocationSource(this);
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
	public void onLocationChanged(Location location) 
	{
	    if( changeListener != null )
	    {
	        changeListener.onLocationChanged( location );

	        //Move the camera to the user's location once it's available!
	        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
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
