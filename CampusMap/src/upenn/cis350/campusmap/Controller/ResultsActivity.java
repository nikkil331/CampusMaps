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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class ResultsActivity extends Activity implements View.OnTouchListener{
	
	List<String> names;
	List<String> addresses;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		names = getIntent().getStringArrayListExtra("names");
		addresses = getIntent().getStringArrayListExtra("addresses");
		ResultsView view = new ResultsView(this, getIntent().getExtras());
		setContentView(view);
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		Log.v("ResultsActivity", "result touched");
		if(v instanceof ResultsEntryView && e.getAction() == MotionEvent.ACTION_UP){
			Intent i = new Intent();
			i.putExtra("listIndex", v.getId());
			setResult(RESULT_OK, i);
			finish();
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
