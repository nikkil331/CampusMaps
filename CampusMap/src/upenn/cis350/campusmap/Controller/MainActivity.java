package upenn.cis350.campusmap.Controller;

import java.util.ArrayList;
import java.util.List;

import upenn.cis350.campusmap.R;
import upenn.cis350.campusmap.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

public class MainActivity extends Activity {
	private Searcher searcher; 
	private List<Building> currResults;

	private final int ResultsActivity_ID = 1;
	private final int SERVER_ERROR = 1;
	private final int NO_RESULTS_ERROR = 2;
	private final int OUT_OF_BOUNDS_ERROR = 3;
	private final int SINGLE_RESULT = 4;
	private final int MULTIPLE_RESULTS = 5;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        String apikey = getString(R.string.api_key);
        String lattitude = getString(R.string.latitude_center);
        String longitude = getString(R.string.longitude_center);
        String radius = getString(R.string.radius);
        PackageManager pm = this.getPackageManager();
        boolean hasLocationSensor = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        //I imagine this will eventually be subclass or composition of GeneralSearcher that 
        //handles more complicated searches
        searcher = new GeneralSearcher(apikey, hasLocationSensor, lattitude, longitude, radius);
    }
    
    //should be called when MainActivity's search button is clicked
    public int search(String query){
    	currResults = searcher.getBuildings(query);
    	//if null, there was in issue when making the api request
    	if(currResults == null){
    		return SERVER_ERROR;
    	}
    	//if empty, no results were found
    	if(currResults.size() == 0){
    		return NO_RESULTS_ERROR;
    	}
    	//remove results not within the bounds of penn's campus
    	for(Building b : currResults){
    		if(b.latitude > R.string.latitude_north || b.latitude < R.string.latitude_south
    		|| b.longitude > R.string.longitude_east || b.longitude < R.string.longitude_west){
    			currResults.remove(b);
    		}
    	}
    	if(currResults.size() == 0){
    		return OUT_OF_BOUNDS_ERROR;
    	}
    	//don't go to results page if there's only one result
    	if(currResults.size() == 1){
    		pinBuilding(0);
    		return SINGLE_RESULT;
    	}
    	//create bundle to sent to results view
    	ArrayList<String> names = new ArrayList<String>();
    	ArrayList<String> addresses = new ArrayList<String>();
    	for(Building b : currResults){
    		names.add(b.getName());
    		addresses.add(b.getAddress());
    	}
    	Intent i = new Intent(this, ResultsActivity.class);
    	Bundle bundle = new Bundle();
    	bundle.putStringArrayList("addresses", addresses);
    	bundle.putStringArrayList("names", names);
    	i.putExtras(bundle);
    	startActivityForResult(i, ResultsActivity_ID);
    	return MULTIPLE_RESULTS;
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
    	//pin b
    }
    
    protected void onSearchClick(){
    	//TODO: this is a placeholder. need to actually get text from search box
    	String text  = "";
    	int response = search(text);
    	switch(response){
    		case SERVER_ERROR:
    			displayDialog("An error was encountered while making the request. Please try again later.");
    		
    		case NO_RESULTS_ERROR:
    			displayDialog("No results were found for that query");
    			
    		case OUT_OF_BOUNDS_ERROR:
    			handleOutOfBounds();
    			
    		default: return;
    	}	
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