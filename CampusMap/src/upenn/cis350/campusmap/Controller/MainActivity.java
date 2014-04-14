package upenn.cis350.campusmap.Controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap;

import upenn.cis350.campusmap.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {
	private List<Building> currResults;
	private Pin pinMark;
	//private Marker pin;

	private final int ResultsActivity_ID = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    	
    	Building b = currResults.get(index);
    	GoogleMap mMap;
    	mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
    	LatLng position = new LatLng(b.getLatitude(), b.getLongitude());
    	if (pinMark != null) pinMark.removePin();
    	pinMark = new Pin (mMap, b);
    	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
    	pinMark.addPin();
    	Log.v("MainActivity", "pinned");
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
}