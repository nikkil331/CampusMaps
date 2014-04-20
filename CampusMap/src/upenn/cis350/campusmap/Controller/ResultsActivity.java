package upenn.cis350.campusmap.Controller;

import java.util.*;

import upenn.cis350.campusmap.R;
import upenn.cis350.campusmap.Views.ResultsEntryView;
import upenn.cis350.campusmap.Views.ResultsView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ResultsActivity extends Activity implements View.OnTouchListener{
	
	List<String> names;
	List<String> addresses;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		names = getIntent().getStringArrayListExtra("names");
		addresses = getIntent().getStringArrayListExtra("addresses");
		setContentView(new ResultsView(this, getIntent().getExtras()));
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		Log.v("ResultsActivity", "result touched");
		if(v instanceof ResultsEntryView && e.getAction() == MotionEvent.ACTION_UP){
			Log.v("ResultsActivity", "entry view pressed");
			Intent i = new Intent();
			i.putExtra("listIndex", v.getId());
			setResult(RESULT_OK, i);
			finish();
			
			/*
			String address = ((ResultsEntryView) v).getAddress();
			String name = ((ResultsEntryView) v).getName();
			
			
			Log.v("ResultsActivity", name);
			
			int index = addresses.indexOf(address);
			Log.v("ResultsActivity", names.get(index));
			if(names.get(index).equals(name)){
				Log.v("ResultsActivity", "ending...");
				Intent i = new Intent();
				i.putExtra("listIndex", index);
				setResult(RESULT_OK, i);
				finish();
			}*/
			return true;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		Log.v("ResultsActivity", "back pressed");
		Intent i = new Intent(this,MainActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		setResult(RESULT_OK, i);
		finish();
	}
	

}
