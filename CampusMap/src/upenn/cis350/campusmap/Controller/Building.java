package upenn.cis350.campusmap.Controller;

import java.io.StringReader;
import java.util.ArrayList;

import android.util.JsonReader;


public class Building {
	double longitude;
	double latitude;
	String googlePlaceID;
	String iconURL;
	String name;
	String address;
	ArrayList <String> nicknames;
	
	public Building(double longitude, double latitude, String id, String icon, String name, String address){
		this.longitude = longitude;
		this.latitude = latitude;
		this.googlePlaceID = id;
		this.iconURL = icon;
		this.name = name;
		this.address = address;
	}
	
	public double getLongitude(){
		return this.longitude;
	}
	public double getLatitude(){
		return this.latitude;
	}
	public String getGooglePlaceID(){
		return this.googlePlaceID;
	}
	public String getIconURL(){
		return this.iconURL;
	}
	public String getName(){
		return this.name;
	}
	public String getAddress(){
		return this.address;
	}
}
