package upenn.cis350.campusmap.Controller;

import java.io.StringReader;
import java.util.ArrayList;

import android.util.JsonReader;


public class Building {
	private double longitude;
	private double latitude;
	private String googlePlaceID;
	private String iconURL;
	private String name;
	private String address;
	private ArrayList <String> nicknames;
	// hours are formated as an 8 digit number
	// first four digits are opening, second four are closing
	private String reg_hours;
	private String weekend_hours;
	private String description;
	
	public Building(double longitude, double latitude, String id, String icon, String name, String address){
		this.longitude = longitude;
		this.latitude = latitude;
		this.googlePlaceID = id;
		this.iconURL = icon;
		this.name = name;
		this.address = address;
		this.nicknames = new ArrayList <String>();
		this.reg_hours = "";
		this.weekend_hours = "";
		this.description = "";
	}
	
	public void printBuilding() {
		System.out.println("Name: " + name);
		System.out.println("Address: " + address);
		System.out.println("Longitude: " + longitude + " and Latitude: " + latitude);
		System.out.print("Nicknames: ");
		for (String x : this.nicknames) System.out.print(x + " ");
		System.out.println();
	}
	
	public void addNicknames(String n) {
		if (n == null) return;
		String[] all = n.split(",");
		for (String x : all) {
			x = x.trim();
			this.nicknames.add(x);
		}
	}
	
	public ArrayList <String> getNicknames() {
		return this.nicknames;
	}
	
	public void setDescription(String d) {
		this.description = d;
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public void setRegHours(String hours) {
		this.reg_hours = hours;
	}
	
	public String getRegHours() {
		return this.reg_hours;
	}
	
	public void setWeekendHours(String hours) {
		this.weekend_hours = hours;
	}
	
	public String getWeekendHours() {
		return this.weekend_hours;
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
