package upenn.cis350.campusmap.Controller;

import upenn.cis350.campusmap.R;
import upenn.cis350.campusmap.R.id;
import upenn.cis350.campusmap.R.layout;
import upenn.cis350.campusmap.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;


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

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class RouteActivity extends FragmentActivity
{
	GoogleMap map;
	ArrayList<LatLng> markerPoints;
	TextView tvDistanceDuration;
	LatLng curr;
	Pin dest;

	public RouteActivity (LatLng c, Pin d, GoogleMap m) {
		this.curr = c;
		this.dest = d;
		this.map = m;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);

		this.tvDistanceDuration = (TextView) this.findViewById(R.id.tv_distance_time);
		// Initializing
		this.markerPoints = new ArrayList<LatLng>();

		// Enable MyLocation Button in the Map
		this.map.setMyLocationEnabled(true);
		addMarkers();
	}

	public void addMarkers() 
	{
		MarkerOptions start = new MarkerOptions();
		start.position(curr).title("You Are Here").icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

		//map.addMarker(start);
		RouteActivity.this.markerPoints.add(curr);
		map.addMarker(dest.getMarkerOptions());
		RouteActivity.this.markerPoints.add(dest.getMarkerOptions().getPosition());



		// Adding new item to the ArrayList


		LatLng origin = RouteActivity.this.markerPoints.get(0);
		LatLng dest = RouteActivity.this.markerPoints.get(1);

		// Getting URL to the Google Directions API
		String url = RouteActivity.this.getDirectionsUrl(origin, dest);

		DownloadTask downloadTask = new DownloadTask();

		// Start downloading json data from Google Directions API
		downloadTask.execute(url);

	}


	private String getDirectionsUrl(LatLng origin, LatLng dest)
	{
		// Origin of route
		String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Mode is walking
		String mode = "mode=walking";
		
		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor +"&" + mode;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException
	{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try
		{
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
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e)
		{
			Log.d("Exception while downloading url", e.toString());
		} finally
		{
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>
	{
		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url)
		{

			// For storing data from web service
			String data = "";

			try
			{
				// Fetching the data from web service
				data = RouteActivity.this.downloadUrl(url[0]);
			} catch (Exception e)
			{
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);

		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
	{

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData)
		{
			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try
			{
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result)
		{
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();
			String distance = "";
			String duration = "";

			if (result.size() < 1)
			{
				Toast.makeText(RouteActivity.this.getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
				return;
			}

			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++)
			{
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++)
				{
					HashMap<String, String> point = path.get(j);

					if (j == 0)
					{ // Get distance from the list
						distance = point.get("distance");
						continue;
					} else if (j == 1)
					{ // Get duration from the list
						duration = point.get("duration");
						continue;
					}
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);
					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(2);
				lineOptions.color(Color.RED);
			}

			RouteActivity.this.tvDistanceDuration.setText("Distance:" + distance + ", Duration:" + duration);

			// Drawing polyline in the Google Map for the i-th route
			RouteActivity.this.map.addPolyline(lineOptions);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		this.getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
