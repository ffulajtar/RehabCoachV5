package com.example.rehab_coachv1;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;


public class DirectionsJSONParser {
	
	public Pair<LatLng, LatLng> boundaries(JSONObject object){

		JSONArray routes = null;
//		LatLng northEast, southWest;
		double neLat, neLon, swLat, swLon;
		Pair<LatLng, LatLng> returnPair = new Pair<LatLng, LatLng>(new LatLng(0.0, 0.0), new LatLng(0.0, 0.0));

		try {

			routes = object.getJSONArray("routes");

			neLat = Double.parseDouble((String)((JSONObject)((JSONObject)routes.get(0)).get("northeast")).get("lat"));
			neLon = Double.parseDouble((String)((JSONObject)((JSONObject)routes.get(0)).get("northeast")).get("lon"));
			swLat = Double.parseDouble((String)((JSONObject)((JSONObject)routes.get(0)).get("southwest")).get("lat"));
			swLon = Double.parseDouble((String)((JSONObject)((JSONObject)routes.get(0)).get("southwest")).get("lon"));
			return new Pair<LatLng, LatLng>(new LatLng(neLat,neLon), new LatLng(swLat, swLon));

		}catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e){
		}
		return returnPair;

	}
	
	public List<DirectionsStep> getFirstRouteDirections(JSONObject jObject){
		List<DirectionsStep> result = new ArrayList<DirectionsStep>();

		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;

		try {

			jRoutes = jObject.getJSONArray("routes");

			jLegs = ( (JSONObject)jRoutes.get(0)).getJSONArray("legs");
			
			/** Traversing all legs */
			for(int j=0;j<jLegs.length();j++){
				jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
				
				/** Traversing all steps */
				for(int k=0;k<jSteps.length();k++){
					String HtmlInstructions;
					String distance;
					String duration;
					Location startLocation;
					Location endLocation;
					double latHolder, lonHolder;

					HtmlInstructions = (String)((JSONObject)jSteps.get(k)).get("html_instructions");
					distance = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("distance")).get("text");
					duration = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("duration")).get("text");


					
					latHolder = ((JSONObject)((JSONObject)jSteps.get(k)).get("end_location")).getDouble("lat");
					lonHolder = ((JSONObject)((JSONObject)jSteps.get(k)).get("end_location")).getDouble("lng");
					endLocation = new Location(LocationManager.GPS_PROVIDER);
					endLocation.setLatitude(latHolder);
					endLocation.setLongitude(lonHolder);
					
					latHolder = ((JSONObject)((JSONObject)jSteps.get(k)).get("start_location")).getDouble("lat");
					lonHolder = ((JSONObject)((JSONObject)jSteps.get(k)).get("start_location")).getDouble("lng");
					startLocation = new Location(LocationManager.GPS_PROVIDER);
					startLocation.setLatitude(latHolder);
					startLocation.setLongitude(lonHolder);
					

					
					DirectionsStep stepToAdd = new DirectionsStep();
					stepToAdd.distance = distance;
					stepToAdd.duration = duration;
					stepToAdd.endLocation = endLocation;
					stepToAdd.startLocation = startLocation;
					stepToAdd.HtmlInstructions = android.text.Html.fromHtml(HtmlInstructions).toString();
					result.add(stepToAdd);
					Log.d("Mike result size :", Integer.toString(result.size()));
				}

			}

		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e){
		}


		return result;
	}

   /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
   public List<List<HashMap<String,String>>> getPolies(JSONObject jObject){

       List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
       JSONArray jRoutes = null;
       JSONArray jLegs = null;
       JSONArray jSteps = null;

       try {

           jRoutes = jObject.getJSONArray("routes");

           /** Traversing all routes */
           for(int i=0;i<jRoutes.length();i++){
               jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
               List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

               /** Traversing all legs */
               for(int j=0;j<jLegs.length();j++){
                   jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                   /** Traversing all steps */
                   for(int k=0;k<jSteps.length();k++){
                       String polyline = "";
                       polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                       List<LatLng> list = decodePoly(polyline);

                       /** Traversing all points */
                       for(int l=0;l<list.size();l++){
                           HashMap<String, String> hm = new HashMap<String, String>();
                           hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                           hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                           path.add(hm);
                       }
                   }
                   routes.add(path);
               }
           }

       } catch (JSONException e) {
           e.printStackTrace();
       }catch (Exception e){
       }
       return routes;
   }

   /**
   * Method to decode polyline points
   * Courtesy : jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
   * */
   private List<LatLng> decodePoly(String encoded) {

       List<LatLng> poly = new ArrayList<LatLng>();
       int index = 0, len = encoded.length();
       int lat = 0, lng = 0;

       while (index < len) {
           int b, shift = 0, result = 0;
           do {
               b = encoded.charAt(index++) - 63;
               result |= (b & 0x1f) << shift;
               shift += 5;
           } while (b >= 0x20);
           int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
           lat += dlat;

           shift = 0;
           result = 0;
           do {
               b = encoded.charAt(index++) - 63;
               result |= (b & 0x1f) << shift;
               shift += 5;
           } while (b >= 0x20);
           int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
           lng += dlng;

           LatLng p = new LatLng((((double) lat / 1E5)),
                                (((double) lng / 1E5)));
           poly.add(p);
       }
       return poly;
   }
}