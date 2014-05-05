package upenn.cis350.campusmap.test;


import com.robotium.solo.Solo;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import upenn.cis350.campusmap.R;
import upenn.cis350.campusmap.Controller.MainActivity;
import upenn.cis350.campusmap.Controller.ResultsActivity;

import com.google.android.gms.maps.model.LatLng;

@SuppressLint("NewApi")
public class GeneralSearcherTest extends ActivityInstrumentationTestCase2<MainActivity>{

	private Solo solo;
	
	public GeneralSearcherTest() {
		super(MainActivity.class);
	}
	
	 public void setUp() throws Exception {
	        solo = new Solo(getInstrumentation(), getActivity());
	  }

	 //from maps api
	 public void testOneResultGoogle() throws Exception{
		 EditText searchBox = (EditText)solo.getView(R.id.editText1);
		 solo.enterText(searchBox, "3680 Walnut St");
		 View button = solo.getView(R.id.button1);
		 solo.clickOnView(button);
		 //wait for searcher to start
		 solo.sleep(1000);
		 MainActivity m = (MainActivity)solo.getCurrentActivity();
		 if(m.currSearcher != null){
			 while(m.currSearcher.getStatus() != AsyncTask.Status.FINISHED){}
		 }
		 LatLng position = m.getPin().getLatLng();
		 assertEquals(39.9533539, position.latitude);
		 assertEquals(-75.1966123, position.longitude);
	 }
	 
	 //from buildings database
	 public void testOneResultName() throws Exception{
		 EditText searchBox = (EditText)solo.getView(R.id.editText1);
		 solo.enterText(searchBox, "Wistar");
		 View button = solo.getView(R.id.button1);
		 solo.clickOnView(button);
		 //wait for searcher to start
		 solo.sleep(1000);
		 MainActivity m = (MainActivity)solo.getCurrentActivity();
		 if(m.currSearcher != null){
			 while(m.currSearcher.getStatus() != AsyncTask.Status.FINISHED){}
		 }
		 LatLng position = m.getPin().getLatLng();
		 assertEquals(39.951401, position.latitude);
		 assertEquals(-75.195396, position.longitude);
	 }
	 //based on building code
	 public void testOneResultCode() throws Exception{
		 EditText searchBox = (EditText)solo.getView(R.id.editText1);
		 solo.enterText(searchBox, "DRLB");
		 View button = solo.getView(R.id.button1);
		 solo.clickOnView(button);
		 //wait for searcher to start
		 solo.sleep(1000);
		 MainActivity m = (MainActivity)solo.getCurrentActivity();
		 if(m.currSearcher != null){
			 while(m.currSearcher.getStatus() != AsyncTask.Status.FINISHED){}
		 }
		 LatLng position = m.getPin().getLatLng();
		 assertEquals(39.952099, position.latitude);
		 assertEquals(-75.190002, position.longitude);
	 }
	 public void testMultipleResults() throws Exception{
		 EditText searchBox = (EditText)solo.getView(R.id.editText1);
		 solo.enterText(searchBox, "annenberg");
		 View button = solo.getView(R.id.button1);
		 solo.clickOnView(button);
		 //wait for searcher to start
		 solo.sleep(1000);
		 MainActivity m = (MainActivity)solo.getCurrentActivity();
		 if(m.currSearcher != null){
			 while(m.currSearcher.getStatus() != AsyncTask.Status.FINISHED){}
		 }
		 solo.waitForActivity(ResultsActivity.class);
		 View result = solo.getView(2);
		 solo.clickOnView(result);
		 solo.waitForActivity(MainActivity.class);
		 m = (MainActivity)solo.getCurrentActivity();
		 //wait for pin to drop
		 solo.sleep(1000);
		 LatLng position = m.getPin().getLatLng();
		 assertEquals(39.953098, position.latitude);
		 assertEquals(-75.196503, position.longitude);
	 }
	 
	 public void testFirstResultChosen() throws Exception{
		 EditText searchBox = (EditText)solo.getView(R.id.editText1);
		 solo.enterText(searchBox, "vance");
		 View button = solo.getView(R.id.button1);
		 solo.clickOnView(button);
		 //wait for searcher to start
		 solo.sleep(1000);
		 MainActivity m = (MainActivity)solo.getCurrentActivity();
		 if(m.currSearcher != null){
			 while(m.currSearcher.getStatus() != AsyncTask.Status.FINISHED){}
		 }
		 solo.waitForActivity(ResultsActivity.class);
		 View result = solo.getView(0);
		 solo.clickOnView(result);
		 solo.waitForActivity(MainActivity.class);
		 m = (MainActivity)solo.getCurrentActivity();
		 //wait for pin to drop
		 solo.sleep(1000);
		 LatLng position = m.getPin().getLatLng();
		 assertEquals(39.947899, position.latitude);
		 assertEquals(-75.192497, position.longitude);
	 }
	 
	 public void testLastResultChosen() throws Exception{
		 EditText searchBox = (EditText)solo.getView(R.id.editText1);
		 solo.enterText(searchBox, "towne");
		 View button = solo.getView(R.id.button1);
		 solo.clickOnView(button);
		 //wait for searcher to start
		 solo.sleep(1000);
		 MainActivity m = (MainActivity)solo.getCurrentActivity();
		 if(m.currSearcher != null){
			 while(m.currSearcher.getStatus() != AsyncTask.Status.FINISHED){}
		 }
		 solo.waitForActivity(ResultsActivity.class);
		 View result = solo.getView(1);
		 solo.clickOnView(result);
		 solo.waitForActivity(MainActivity.class);
		 m = (MainActivity)solo.getCurrentActivity();
		 //wait for pin to drop
		 solo.sleep(1000);
		 LatLng position = m.getPin().getLatLng();
		 assertEquals(39.951698, position.latitude);
		 assertEquals(-75.191002, position.longitude);
	 }
	 
	 @Override
	   public void tearDown() throws Exception {
	        solo.finishOpenedActivities();
	  }
}
