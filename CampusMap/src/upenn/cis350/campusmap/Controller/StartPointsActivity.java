package upenn.cis350.campusmap.Controller;

import upenn.cis350.campusmap.R;
import upenn.cis350.campusmap.R.layout;
import upenn.cis350.campusmap.R.menu;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

public class StartPointsActivity extends OurActivity implements OnTouchListener {

	public Searcher currSearcher;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_points);
		OnStart startTask = new OnStart();
		startTask.execute(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start_points, menu);
		return true;
	}
	
	
	private class OnStart extends AsyncTask<StartPointsActivity, Void, Boolean>{
		private StartPointsActivity activity;
		@Override
		protected Boolean doInBackground(StartPointsActivity... args) {
			// TODO Auto-generated method stub
			activity = args[0];
			return null;
		}
		
		@Override
		protected void onPostExecute(Boolean b){
			findViewById(R.id.doDirectionsButton).setOnTouchListener(activity);
		}
	}
	
	// function to send back to main activity and draw the route
	public void f (View v) {
		
	}
	
	
	
	public void onSearchClick(View view){
		Log.v("MainActivity", "searching...");
		String text = ((EditText)findViewById(R.id.editTextFrom)).getText().toString();
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

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		// TODO Auto-generated method stub
		if(v.equals(findViewById(R.id.doDirectionsButton))){
			if(e.getAction() == MotionEvent.ACTION_DOWN){
				((ImageButton) v).setImageResource(R.drawable.swap);
			}
			else if (e.getAction() == MotionEvent.ACTION_UP){
				((ImageButton) v).setImageResource(R.drawable.swap);
			}
		}
		return false;
	}
	

}