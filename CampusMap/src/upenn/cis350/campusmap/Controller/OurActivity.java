package upenn.cis350.campusmap.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

import upenn.cis350.campusmap.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OurActivity extends Activity{
	protected List<Building> currResults;
	protected Pin pinMark;
	protected GPSTracker current;
	protected LatLng curr;
	protected String format_HTML;
	protected GoogleMap mMap;
	protected ArrayList<LatLng> markerPoints;
	protected HashMap<String, String> floorPlans;
	//for testing
	public Searcher currSearcher;
	protected final int ResultsActivity_ID = 1;
	protected final int InBuildingActivity_ID = 2;
	protected final int StartPointsActivity_ID = 3;
	protected LocationManager mLocationManager;
	protected Location currLoc;
	public static Context c;
	public ImageButton fbButton;
	
	
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
	
	protected void displayDialog(String message){
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
	
	protected void handleOutOfBounds(){
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
	
	protected void pinBuilding(int index){
		mMap.clear();
		Building b = currResults.get(index);
		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		LatLng position = new LatLng(b.getLatitude(), b.getLongitude());
		if (pinMark != null) pinMark.removePin();
		pinMark = new Pin (mMap, b);
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 16));
		pinMark.addPin(false);
		pinInfoText();
		mMap.setMyLocationEnabled(true);
		//addMarkers();
	}
	
	protected void pinInfoText() {
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
}
