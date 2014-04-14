package upenn.cis350.campusmap.test;


import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import upenn.cis350.campusmap.R;
import upenn.cis350.campusmap.Controller.MainActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

@SuppressLint("NewApi")
public class GeneralSearcherTest extends ActivityInstrumentationTestCase2<MainActivity>{

	private Solo solo;
	
	public GeneralSearcherTest() {
		super(MainActivity.class);
	}
	
	 public void setUp() throws Exception {
	        solo = new Solo(getInstrumentation(), getActivity());
	  }

	 
	 public void testOneResult() throws Exception{
		 EditText searchBox = (EditText)solo.getView(R.id.editText1);
		 solo.enterText(searchBox, "Huntsman Hall");
		 View button = solo.getView(R.id.button1);
		 solo.clickOnView(button);
		
		 solo.sleep(11000);
		 MainActivity a = (MainActivity)solo.getCurrentActivity();
		 Marker p = a.getPin();
		 LatLng position = p.getPosition();
		 assertTrue(Math.abs(position.latitude - 39.9531512) <= 0.00001);
		 assertTrue(Math.abs(position.longitude + 75.1982201) <= 0.00001);
	 }
	 
	 @Override
	   public void tearDown() throws Exception {
	        solo.finishOpenedActivities();
	  }
}
