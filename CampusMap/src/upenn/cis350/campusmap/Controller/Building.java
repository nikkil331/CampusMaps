package upenn.cis350.campusmap.Controller;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.ParseException;

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
	private Hours h;

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

	class Hours {
		// internal class that checks if the building is open based on
		// the building's passed in hours
		private Calendar regOpen;
		private Calendar regClose;
		private Calendar weekOpen;
		private Calendar weekClose;
		private Calendar current;

		public Hours (String reg_hours, String week_hours) {
			try {
				// error checking
				if (reg_hours == null) return;
				if (reg_hours.length() < 8) return;
				String rOpen = reg_hours.substring(0,2) + ":" + reg_hours.substring(2,4) + ":00";
				String rClose = reg_hours.substring(4,6) + ":" + reg_hours.substring(6,8) + ":00";
				// creates a date object to store the parsed times
				Date time1 = new SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).parse(rOpen);
				regOpen = Calendar.getInstance();
				regOpen.setTime(time1);
				time1 = new SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).parse(rClose);
				regClose = Calendar.getInstance();
				regClose.setTime(time1);
				
				if (week_hours == null) return;
				if (week_hours.length() < 8) return;
				String wOpen = week_hours.substring(0,2) + ":" + week_hours.substring(2,4) + ":00";
				String wClose = week_hours.substring(4,6) + ":" + week_hours.substring(6,8) + ":00";
				time1 = new SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).parse(wOpen);
				weekOpen = Calendar.getInstance();
				weekOpen.setTime(time1);
				time1 = new SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).parse(wClose);
				weekClose = Calendar.getInstance();
				weekClose.setTime(time1);
				
				current = Calendar.getInstance();
			}
			catch(ParseException e) {
				e.printStackTrace();
			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public boolean isOpen() {
			if (regOpen == null) return false;
			boolean op = false;
			if (current.get(Calendar.DAY_OF_WEEK) > 2){
				op = (current.after(regOpen) && current.before(regClose));
				return op;
			}
			if (weekOpen == null) return false;
			if (current.get(Calendar.DAY_OF_WEEK) < 2) {
				op = (current.after(weekOpen) && current.before(weekClose));
				return op;
			}
			return op;
		}
		
		
	}

	public void printBuilding() {
		// prints out the building information for testing purposes
		System.out.println("Name: " + name);
		System.out.println("Address: " + address);
		System.out.println("Longitude: " + longitude + " and Latitude: " + latitude);
		System.out.print("Nicknames: ");
		for (String x : this.nicknames) System.out.print(x + " ");
		System.out.println();
	}
	
	public boolean isOpen() {
		h = new Hours (reg_hours, weekend_hours);
		return h.isOpen();
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
