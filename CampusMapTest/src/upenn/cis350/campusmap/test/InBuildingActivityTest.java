package upenn.cis350.campusmap.test;

import junit.framework.TestCase;
import android.os.AsyncTask;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import upenn.cis350.campusmap.R;
import upenn.cis350.campusmap.Controller.Building;
import upenn.cis350.campusmap.Controller.InBuildingActivity;
import upenn.cis350.campusmap.Controller.MainActivity;
import upenn.cis350.campusmap.Controller.ResultsActivity;

/**
 * The class <code>InBuildingActivityTest</code> contains tests for the class
 * {@link <code>InBuildingActivity</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 5/4/14 2:12 PM
 *
 * @author nicolelimtiaco
 *
 * @version $Revision$
 */
public class InBuildingActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {
	private Solo solo;
	
	public InBuildingActivityTest() {
		super(MainActivity.class);
	}
	
	@Override
	public void setUp() throws Exception {
		solo = new Solo(getInstrumentation(), getActivity());
	}
	 
	@Override
	   public void tearDown() throws Exception {
	        solo.finishOpenedActivities();
	  }
	 
	 public void testGoInBuilding() throws Exception{
		 searchBuilding("ARCH");
		 goInBuilding();
		 
		 //check that the correct image is displayed
		 ImageSwitcher imageSwitcher = (ImageSwitcher)solo.getView(R.id.imageSwitcher1);
		 ImageView image = (ImageView) imageSwitcher.getCurrentView();
		 String actualPicName = (String)image.getTag();
		 String expectedPicName = "arch_200304_000000a3";
		 assertEquals(expectedPicName, actualPicName);
		 
		 //check that the correct building name is displayed
		 TextView infoText = (TextView)solo.getView(R.id.InfoText1);
		 String buildingDesc = infoText.getText().toString();
		 String actualBuildingName = buildingDesc.substring(0, buildingDesc.indexOf("\n")).trim();
		 String expectedBuildingName = "The ARCH";
		 assertEquals(expectedBuildingName, actualBuildingName);
		 
		 //check that the correct address is displayed
		 String actualAddress = buildingDesc.substring(buildingDesc.indexOf("\n") + 1, buildingDesc.length());
		 String expectedAddress = "3601 Locust Walk, Philadelphia PA, 19104";
		 assertEquals(expectedAddress, actualAddress);	 
		 
		 leaveBuilding();
	 }
	 
	 public void testLeaveBuilding(){
		 searchBuilding("lrsm");
		 goInBuilding();
		 leaveBuilding();
		 
		 //check that searched building is still pinned
		 MainActivity m = (MainActivity)solo.getCurrentActivity();
		 Building pinned = m.getPin().getBuilding();
		 String actualAddress = pinned.getAddress();
		 String expectedAddress = "3231 Walnut Street, Philadelphia PA, 19104";
		 assertEquals(expectedAddress, actualAddress);
		 
		 //check that building info is still displayed
		 TextView infoText = (TextView)solo.getView(R.id.InfoText1);
		 String buildingDesc = infoText.getText().toString();
		 String actualBuildingName = buildingDesc.substring(0, buildingDesc.indexOf("\n")).trim();
		 String expectedBuildingName = "Laboratory for Research on the Structure of Matter";
		 assertEquals(expectedBuildingName, actualBuildingName);
		 
		 actualAddress = buildingDesc.substring(buildingDesc.indexOf("\n") + 1, buildingDesc.length());
		 expectedAddress = "3231 Walnut Street, Philadelphia PA, 19104";
		 assertEquals(expectedAddress, actualAddress);	 
	 }
	 
	 public void testSwipePicturesUp(){
		 searchBuilding("williams");
		 goInBuilding();
		 InBuildingActivity a = (InBuildingActivity)solo.getCurrentActivity();
		 
		 String[] expectedPicNames = {
			 "williams_201009_000000a1", "williams_201009_000000a2", "williams_201009_000000a3",
			 "williams_201009_000000a4", "williams_201009_000000a6", "williams_201009_000000a7",
			 "williams_201009_000000a8"
		 };
		 //check that the correct image is displayed
		 ImageSwitcher imageSwitcher = (ImageSwitcher)solo.getView(R.id.imageSwitcher1);
		 
		for(int i = 0; i < 7; i++){
			 swipeUp();
			 View v = imageSwitcher.getNextView();
			 solo.waitForView(v);
			 String actualPicName = a.getImageName();
			 assertEquals(expectedPicNames[i], actualPicName);	 
		}
		
		leaveBuilding();
	 }
	 
	 public void testSwipePicturesDown(){
		 searchBuilding("Claudia Cohen");
		 goInBuilding();
		 InBuildingActivity a = (InBuildingActivity)solo.getCurrentActivity();
		 
		 String[] expectedPicNames = {
				 "cohen_201005_000000a2", "cohen_201005_000000a4", "cohen_201005_000000a5",
				 "cohen_201005_000000a6"
		 };
		 //check that the correct image is displayed
		 ImageSwitcher imageSwitcher = (ImageSwitcher)solo.getView(R.id.imageSwitcher1);
		 
		for(int i = 0; i < 4; i++){
			 swipeUp();
			 View v = imageSwitcher.getNextView();
			 solo.waitForView(v);
		}

		for(int i = 0; i < 3; i++){
			 swipeDown();
			 View v = imageSwitcher.getNextView();
			 solo.waitForView(v);
			 String actualPicName = a.getImageName();
			 assertEquals(expectedPicNames[3 - i - 1], actualPicName);	 
		}
		leaveBuilding();
	 }
	 
	 public void testHighlightedDots(){
		 searchBuilding("Lerner center");
		 goInBuilding(); 
		 ImageSwitcher imageSwitcher = (ImageSwitcher)solo.getView(R.id.imageSwitcher1);
		 for(int i = 0; i < 2; i++){
			 swipeUp();
			 View v = imageSwitcher.getNextView();
			 solo.waitForView(v);
			 View dot = solo.getView(i);
			 Object tag = dot.getTag();
			 if(tag != null && tag instanceof String){
				 assertEquals("highlighted", tag);
			 }
		}
		
		 swipeDown();
		 View v = imageSwitcher.getNextView();
		 solo.waitForView(v);
		 View dot = solo.getView(0);
		 Object tag = dot.getTag();
		 if(tag != null && tag instanceof String){
			 assertEquals("highlighted", tag);
		 }
		 
		 leaveBuilding();
	 }
	 
	 private void searchBuilding(String buildingName){
		 //search arch
		 EditText searchBox = (EditText)solo.getView(R.id.editText1);
		 solo.enterText(searchBox, buildingName);
		 View button = solo.getView(R.id.button1);
		 solo.clickOnView(button);
		 //wait for searcher to start
		 solo.sleep(1000);
		 MainActivity m = (MainActivity)solo.getCurrentActivity();
		 if(m.currSearcher != null){
			 while(m.currSearcher.getStatus() != AsyncTask.Status.FINISHED){}
		 }
	 }
	 
	 private void chooseBuilding(int index){
		 solo.waitForActivity(ResultsActivity.class);
		 View result = solo.getView(index);
		 solo.clickOnView(result);
		 solo.waitForActivity(MainActivity.class);
	 }
	 
	 private void goInBuilding(){
		//go inside
		 View insideButton = solo.getView(R.id.insidebutton1);
		 solo.clickOnView(insideButton);
		 solo.waitForActivity(InBuildingActivity.class); 
	 }
	 
	 private void leaveBuilding(){
		 View outsideButton = solo.getView(R.id.outsidebutton1);
		 solo.clickOnView(outsideButton);
		 solo.waitForActivity(MainActivity.class);
	 }
	 
	 private void swipeUp() {
		 	View switcher = solo.getView(R.id.imageSwitcher1);
		    int width = switcher.getWidth();
		    int height = switcher.getHeight();
		    int[] location = new int[2];
		    switcher.getLocationInWindow(location);
		    float yStart = location[1] + height - 10;
		    float yEnd = location[1] + 10;
		    solo.drag(width / 2, width / 2, yStart, yEnd, 10);
		}

		private void swipeDown() {
			View switcher = solo.getView(R.id.imageSwitcher1);
		    int width = switcher.getWidth();
		    int height = switcher.getHeight();
		    int[] location = new int[2];
		    switcher.getLocationInWindow(location);
		    float yStart = location[1] + 10;
		    float yEnd = location[1] + height - 10;
		    solo.drag(width / 2, width / 2, yStart, yEnd, 10);
		}
			 
	 
	
}
