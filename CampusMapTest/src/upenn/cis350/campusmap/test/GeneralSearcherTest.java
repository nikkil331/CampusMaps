package upenn.cis350.campusmap.test;

import java.util.List;

import junit.framework.TestCase;
import android.util.Log;
import upenn.cis350.campusmap.Controller.Building;
import upenn.cis350.campusmap.Controller.GeneralSearcher;

public class GeneralSearcherTest extends TestCase {
	public void testFormatQuery(){
		GeneralSearcher s = new GeneralSearcher("AIzaSyAx2hhIPLs2n1UiJB8q4LFvph37ZPWxEbY", false, "39.9539", "-75.1930", "16000");
		String result = s.formatQuery("Huntsman Hall");
		assertTrue(result.equals("Huntsman+Hall"));
	}
	public void testBuildRequest(){
		GeneralSearcher s = new GeneralSearcher("AIzaSyAx2hhIPLs2n1UiJB8q4LFvph37ZPWxEbY", false, "39.9539", "-75.1930", "16000");
		String result = s.buildRequest("Huntsman+Hall");
		System.out.println(result);
		assertTrue(result.equals("https://maps.googleapis.com/maps/api/place/textsearch/json?query=Huntsman+Hall&sensor=false&key=AIzaSyAx2hhIPLs2n1UiJB8q4LFvph37ZPWxEbY&location=39.9539,-75.1930&radius=16000"));
	}
	
	public void testGetBuildingBasic(){
		GeneralSearcher s = new GeneralSearcher("AIzaSyAx2hhIPLs2n1UiJB8q4LFvph37ZPWxEbY", false, "39.9539", "-75.1930", "16000");
		List<Building> bs = s.getBuildings("Huntsman Hall");
		assertFalse(bs.size() == 0);
		Building result = bs.get(0);
		assertTrue(result.getName().equals("Jon M. Huntsman Hall"));
		assertTrue(Math.abs(result.getLongitude() + 75.19822) <= 0.00001);
		assertTrue(Math.abs(result.getLatitude() - 39.95315) <= 0.00001);
		assertTrue(result.getGooglePlaceID().equals("d93b3e7e31eafea258cf7acae94f7d9e7c3d4dec"));
		assertTrue(result.getIconURL().equals("http://maps.gstatic.com/mapfiles/place_api/icons/geocode-71.png")); 
	}
}
