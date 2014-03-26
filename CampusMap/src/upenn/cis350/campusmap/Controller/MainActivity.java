package upenn.cis350.campusmap.Controller;

import upenn.cis350.campusmap.R;
import upenn.cis350.campusmap.R.layout;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MainActivity extends Activity {
	private boolean hasLocationSensor;
	private Searcher searcher; 
	private Context context;
	
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
}