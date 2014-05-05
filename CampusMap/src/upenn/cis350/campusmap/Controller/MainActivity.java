package upenn.cis350.campusmap.Controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity implements OnTouchListener {
	private List<Building> currResults;
	private Pin pinMark;
	private GPSTracker current;
	private LatLng curr;
	private String format_HTML;
	private GoogleMap mMap;
	private ArrayList<LatLng> markerPoints;
	private HashMap<String, String> floorPlans;
	//for testing
	public Searcher currSearcher;
	private final int ResultsActivity_ID = 1;
	private final int InBuildingActivity_ID = 2;
	private MyLocationListener mLocationListener;
	private LocationManager mLocationManager;
	private Location currLoc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
		this.mLocationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		this.mLocationListener = new MyLocationListener();
		OnStart startTask = new OnStart();
		startTask.execute(this);
	}
	
	@Override
	protected void onResume() {
		String provider = LocationManager.GPS_PROVIDER;
		long minTime = 10000;
		float minDistance = 0;
		mLocationManager.requestLocationUpdates(provider, minTime, minDistance, mLocationListener);
	}
	
	@Override
	protected void onPause() {
		mLocationManager.removeUpdates(mLocationListener);
	}
	public class MyLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			currLoc = location;
		}

		@Override
		public void onProviderDisabled(String provider) {
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
	
	private class OnStart extends AsyncTask<MainActivity, Void, Boolean>{
		private MainActivity activity;
		
		@Override
		protected Boolean doInBackground(MainActivity... args) {
			activity = args[0];
			
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			current = new GPSTracker(activity);
			if(current != null && current.getLocation() != null)
			{
				curr = new LatLng (current.getLocation().getLatitude(),current.getLocation().getLongitude());
			}
			else
			{
				curr = new LatLng(39.953183,-75.194791);
			}
			activity.markerPoints = new ArrayList<LatLng>();
			getFloorPlans();
			//show loading page
			try {
				Thread.sleep(7000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean b){
			setContentView(R.layout.activity_main);
			findViewById(R.id.button1).setOnTouchListener(activity);
			findViewById(R.id.dirbutton1).setOnTouchListener(activity);
			findViewById(R.id.insidebutton1).setOnTouchListener(activity);
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			mMap.setMyLocationEnabled(true);
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr, 15));
		}
		
	}
	
	private void getFloorPlans(){
		floorPlans = new HashMap<String, String>();
		BufferedReader addresses = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.addresses)));
		String line;
		try {
			line = addresses.readLine();
		} catch (IOException e) {
			Log.v("MainActivity", "ioexception");
			return;
		}
		while(line != null){
			String[] building = line.split(":");
			Log.v("MainActivity", line);
			String address = building[0].trim();
			String prefix = building[1].trim();
			Log.v("MainActivity", address + ":" + prefix);
			floorPlans.put(address, prefix);
			try {
				line = addresses.readLine();
			} catch (IOException e) {
				Log.v("MainActivity", "ioexception");
				return;
			}
		}
		try {
			addresses.close();
		} catch (IOException e) {}
		return;
	}

	
	public void onSearchClick(View view){
		Log.v("MainActivity", "searching...");
		String text = ((EditText)findViewById(R.id.editText1)).getText().toString();
		String apikey = getString(R.string.api_key);
		String lattitude = getString(R.string.latitude_center);
		String longitude = getString(R.string.longitude_center);
		String radius = getString(R.string.radius);
		PackageManager pm = this.getPackageManager();
		boolean hasLocationSensor = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
		if(text.length() != 0){
		Searcher searcher = new GeneralSearcher(this, apikey, hasLocationSensor, lattitude, longitude, radius);
		searcher.execute(text);	
		currSearcher = searcher;
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
		else
		{
			displayDialog("Please enter text to search.");
		}
	}


	public void receiveSearchResults(List<Building> buildings){
		Log.v("MainActivity", "receiving results...");
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
			Log.v("MainActivity", "pinning building...");
			pinBuilding(0);
			return;
		}
		//create bundle to sent to results view
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> addresses = new ArrayList<String>();
		int numResults = Math.min(currResults.size(), 7);
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
			Bundle extras = intent.getExtras();
			if(extras != null){
				pinBuilding(extras.getInt("listIndex", 0));
			}
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
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
		pinMark.addPin();
		pinInfoText();
		mMap.setMyLocationEnabled(true);
		//addMarkers();
	}

	private void pinInfoText() {
		RelativeLayout infoContainer = (RelativeLayout)findViewById(R.id.pinInfo);
		infoContainer.setVisibility(View.VISIBLE);
		Building b = pinMark.getBuilding();
		String name = b.getName();
		String add = b.getAddress();
		String desc = name + "\n" + add;
		SpannableString text = new SpannableString(desc);
		text.setSpan(new RelativeSizeSpan(1.25f), 0, name.length() + 1,
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		text.setSpan(new TextAppearanceSpan(this, Typeface.NORMAL, Color.rgb(51,51,51)), 0, name.length() + 1,
		            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		text.setSpan(new TextAppearanceSpan(this, Typeface.NORMAL, Color.rgb(90, 90, 90)), name.length() + 1, desc.length(),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		TextView pinText = (TextView)findViewById(R.id.InfoText1);
		pinText.setText(text);
	}

	//for testing
	public Pin getPin(){
		return pinMark;
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
		if(current == null){
			current = new GPSTracker(this);
		}
//		double longitude = -75.193576;
//		double latitude = 39.952641;
//		if(current.getLocation() != null){
//			longitude = current.getLocation().getLongitude();
//			latitude = current.getLocation().getLatitude();
//		}
		curr = new LatLng (this.currLoc.getLatitude(), this.currLoc.getLongitude());
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
		String address = pinMark.getBuilding().getAddress();
		Log.v("MainActivity", address);
		String prefix = floorPlans.get(address);
		if(prefix == null){
			displayDialog("In building navigation is not enabled for this location at this time.");
			return;
		}
		Intent i = new Intent(this, InBuildingActivity.class);
		Bundle b = new Bundle();
		TextView pinText = (TextView)findViewById(R.id.InfoText1);
		b.putString("info", pinText.getText().toString());
		b.putString("prefix", prefix);
		i.putExtras(b);
		startActivityForResult(i, InBuildingActivity_ID);
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
				lineOptions.width(20);
				lineOptions.color(Color.rgb(0,208,240));
				lineOptions.geodesic(true);
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

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		if(v.equals(findViewById(R.id.button1))){
			if(e.getAction() == MotionEvent.ACTION_DOWN){
				((ImageButton) v).setImageResource(R.drawable.search_focused);
			}
			else if (e.getAction() == MotionEvent.ACTION_UP){
				((ImageButton) v).setImageResource(R.drawable.search);
			}
		}
		else if(v.equals(findViewById(R.id.dirbutton1))){
			if(e.getAction() == MotionEvent.ACTION_DOWN){
				((ImageButton) v).setImageResource(R.drawable.dir_focused);
			}
			else if (e.getAction() == MotionEvent.ACTION_UP){
				((ImageButton) v).setImageResource(R.drawable.dir);
			}
		}
		else if(v.equals(findViewById(R.id.insidebutton1))){
			if(e.getAction() == MotionEvent.ACTION_DOWN){
				((ImageButton) v).setImageResource(R.drawable.door_focused);
			}
			else if (e.getAction() == MotionEvent.ACTION_UP){
				((ImageButton) v).setImageResource(R.drawable.door);
			}
		}
		return false;
	}
	
	@Override
	protected void onDestroy(){
		 super.onDestroy();
	     unbindDrawables(findViewById(R.id.main));
	     System.gc();
	}
	private void unbindDrawables(View view)
	{
	        if (view.getBackground() != null)
	        {
	                view.getBackground().setCallback(null);
	        }
	        if (view instanceof ViewGroup && !(view instanceof AdapterView))
	        {
	                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
	                {
	                        unbindDrawables(((ViewGroup) view).getChildAt(i));
	                }
	                ((ViewGroup) view).removeAllViews();
	        }
	}
}