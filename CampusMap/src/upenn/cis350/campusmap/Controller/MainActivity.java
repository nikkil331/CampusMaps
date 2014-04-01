package upenn.cis350.campusmap.Controller;

import java.util.ArrayList;
import java.util.List;

import upenn.cis350.campusmap.R;
import upenn.cis350.campusmap.R.layout;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MainActivity extends Activity {
	private boolean hasLocationSensor;
	private Searcher searcher; 
	private Context context;
	
	private static int ResultsActivity_ID = 1;
	List<Building> currResults;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        String apikey = getString(R.string.api_key);
        String lattitude = getString(R.string.latitude);
        String longitude = getString(R.string.longitude);
        String radius = getString(R.string.radius);
        PackageManager pm = this.getPackageManager();
        boolean hasLocationSensor = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        searcher = new GeneralSearcher(apikey, hasLocationSensor, lattitude, longitude, radius);
    }
    
    public void search(String query){
    	currResults = searcher.getBuildings(query);
    	if(currResults.size() == 0){
    		//show error message saying that no results were found
    	}
    	if(currResults.size() == 1){
    		Building building = currResults.get(0);
    		//tell map to pin building;
    	}
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
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
    	super.onActivityResult(requestCode, resultCode, intent);
    	if(requestCode == ResultsActivity_ID){
    		retrievedBuildingIndex(intent.getExtras().getInt("listIndex", 0));
    	}
    }
    
    private void retrievedBuildingIndex(int index){
    	Building building = currResults.get(index);
    	//tell map to pin this building
    }
}