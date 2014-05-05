package upenn.cis350.campusmap.Controller;

import com.google.android.gms.maps.model.LatLng;

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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class StartPointsActivity extends OurActivity implements OnTouchListener {
	String name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_points);
		name = getIntent().getStringExtra("name");
		EditText e = (EditText)findViewById(R.id.editTextTo);
		e.setText(name);
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
		Intent i = new Intent();
		i.putExtra("isCurrLocation", true);
		setResult(RESULT_OK, i);
		finish();
	}
	
	
	
	public void onSearchClick(View view){
		Log.v("StartPointsActivity", "searching...");
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
	protected void pinBuilding (int index) {
		Building bi = currResults.get(index);
		curr = new LatLng(bi.getLatitude(), bi.getLongitude());
		Intent i = new Intent();
		i.putExtra("latitude",bi.getLatitude());
		i.putExtra("longitude",bi.getLongitude());
		i.putExtra("isCurrLocation", false);
		setResult(RESULT_OK, i);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		Log.v("StartPointsActivity", "back pressed");
		Intent i = new Intent(this,MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setResult(RESULT_OK, i);
		finish();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent e) {
		// TODO Auto-generated method stub
		if(v.equals(findViewById(R.id.doDirectionsButton))){
			if(e.getAction() == MotionEvent.ACTION_DOWN){
				((Button) v).setBackgroundResource(R.drawable.buttonclk);
			}
			else if (e.getAction() == MotionEvent.ACTION_UP){
				((Button) v).setBackgroundResource(R.drawable.buttonclk);
			}
		}
		return false;
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

}