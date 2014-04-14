package upenn.cis350.campusmap.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap;

import upenn.cis350.campusmap.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private List<Building> currResults;
	private Pin pinMark;
	//private Marker pin;
	private GPSTracker current;
	private LatLng curr;
	private TextView pinInfo;
	private Button navigate;
	private Button door;
	private GoogleMap mMap;
	private ArrayList<LatLng> markerPoints;

	private final int ResultsActivity_ID = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		pinInfo = (TextView)findViewById(R.id.InfoText1);
		navigate = (Button)findViewById(R.id.dirbutton1);
		door = (Button)findViewById(R.id.insidebutton1);
		pinInfo.setMovementMethod(new ScrollingMovementMethod());
		current = new GPSTracker(this);
		curr = new LatLng (current.getLocation().getLatitude(), 
				current.getLocation().getLongitude());
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mMap.setMyLocationEnabled(true);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr, 15));
		this.markerPoints = new ArrayList<LatLng>();
	}

	public void onSearchClick(View view){
		String text = ((EditText)findViewById(R.id.editText1)).getText().toString();
		String apikey = getString(R.string.api_key);
		String lattitude = getString(R.string.latitude_center);
		String longitude = getString(R.string.longitude_center);
		String radius = getString(R.string.radius);
		PackageManager pm = this.getPackageManager();
		boolean hasLocationSensor = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
		//I imagine this will eventually be subclass or composition of GeneralSearcher that 
		//handles more complicated searches
		Searcher searcher = new GeneralSearcher(this, apikey, hasLocationSensor, lattitude, longitude, radius);
		searcher.execute(text);	
	}


	public void receiveSearchResults(List<Building> buildings){
		Log.v("MainActivity", String.valueOf(buildings.size()));
		currResults = buildings;
		//if null, there was in issue when making the api request
		if(currResults == null){
			displayDialog("An error was encountered while making the request. Please try again later.");
			return;
		}
		//if empty, no results were found
		if(currResults.size() == 0){
			displayDialog("No results were found for that query");
			return;
		}
		//remove results not within the bounds of penn's campus
		Iterator<Building> iter = currResults.iterator();
		while(iter.hasNext()){
			Building b = iter.next();
			boolean tooNorth = b.getLatitude() > Double.parseDouble(getString(R.string.latitude_north));
			boolean tooSouth = b.getLatitude() < Double.parseDouble(getString(R.string.latitude_south));
			boolean tooWest = b.getLongitude() < Double.parseDouble(getString(R.string.longitude_west));
			boolean tooEast = b.getLongitude() > Double.parseDouble(getString(R.string.longitude_east));
			if(tooNorth || tooSouth || tooWest|| tooEast){
				iter.remove();
			}
		}
		if(currResults.size() == 0){
			handleOutOfBounds();
			return;
		}
		//don't go to results page if there's only one result
		if(currResults.size() == 1){
			pinBuilding(0);
			return;
		}
		//create bundle to sent to results view
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> addresses = new ArrayList<String>();
		int numResults = Math.min(currResults.size(), 5);
		for(int i = 0; i < numResults; i++){
			Building b = currResults.get(i);
			names.add(b.getName());
			addresses.add(b.getAddress());
		}
		Intent i = new Intent(this, ResultsActivity.class);
		Bundle bundle = new Bundle();
		bundle.putStringArrayList("addresses", addresses);
		bundle.putStringArrayList("names", names);
		i.putExtras(bundle);
		startActivityForResult(i, ResultsActivity_ID);
		return;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent){
		super.onActivityResult(requestCode, resultCode, intent);
		if(requestCode == ResultsActivity_ID){
			pinBuilding(intent.getExtras().getInt("listIndex", 0));
		}
	}

	//pins building at index in currResults 
	private void pinBuilding(int index){
		mMap.clear();
		Building b = currResults.get(index);
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		LatLng position = new LatLng(b.getLatitude(), b.getLongitude());
		if (pinMark != null) pinMark.removePin();
		pinMark = new Pin (mMap, b);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
		pinMark.addPin();
		pinInfoText();
		mMap.setMyLocationEnabled(true);
		Log.v("MainActivity", "pinned");
		//addMarkers();
	}

	private void pinInfoText() {
		this.pinInfo.setVisibility(View.VISIBLE);
		this.navigate.setVisibility(View.VISIBLE);
		this.door.setVisibility(View.VISIBLE);
		Building b = pinMark.getBuilding();
		String name = b.getName();
		String add = b.getAddress();
		String desc = b.getDescription();
		String format_HTML = "<p><b> "+name+"</b> <br><i>"+add+"</i></p><p>"+desc+"</p>";
		pinInfo.setText(Html.fromHtml(format_HTML));
	}

	//for testing
	public MarkerOptions getPin(){
		return pinMark.getMarkerOptions();
	}

	private void displayDialog(String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.dismiss();
			}
		});
		Dialog d = builder.create();
		d.show();
	}

	private void handleOutOfBounds(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your search only returned results outside of Penn's campus."
				+ "Would you like to be taken to your native maps app?");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				String uri = "https://maps.google.com/maps?f=d";
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
				startActivity(i);
			}
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int id){
				dialog.dismiss();
			}
		});
		Dialog d = builder.create();
		d.show();
	}


	//    		addMarkers();

	public void addMarkers(View v) 
	{
		MarkerOptions start = new MarkerOptions();
		curr = new LatLng (current.getLocation().getLatitude(), 
				current.getLocation().getLongitude());
		start.position(curr).title("You Are Here").icon(BitmapDescriptorFactory
				.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

		//map.addMarker(start);
		markerPoints.add(curr);
		this.mMap.addMarker(pinMark.getMarkerOptions());
		markerPoints.add(pinMark.getMarkerOptions().getPosition());



		// Adding new item to the ArrayList


		LatLng origin = curr;
		LatLng dest = pinMark.getLatLng();

		// Getting URL to the Google Directions API
		String url = getDirectionsUrl(origin, dest);

		DownloadTask downloadTask = new DownloadTask();

		// Start downloading json data from Google Directions API
		downloadTask.execute(url);

	}

	//	goInside();

	public void goInside(View v) 
	{
		displayDialog("In building navigation is not enabled for this location at this time.");
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
				data = downloadUrl(url[0]);
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
				Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
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


			// Drawing polyline in the Google Map for the i-th route
			mMap.addPolyline(lineOptions);
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