package upenn.cis350.campusmap.Controller;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.ParseException;

import android.util.Log;

public class Building {
	private double longitude;
	private double latitude;
	private String googlePlaceID;
	private String iconURL;
	private String name;
	private String address;
	private ArrayList<String> nicknames;
	// hours are formated as an 8 digit number
	// first four digits are opening, second four are closing
	private String reg_hours;
	private String weekend_hours;
	private String description;
	private Hours h;
	private String origName;

	public Building(double longitude, double latitude, String id, String icon,
			String name, String address) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.googlePlaceID = id;
		this.iconURL = icon;
		this.name = name;
		this.origName = name;
		this.address = address;
		this.nicknames = new ArrayList<String>();
		this.reg_hours = "";
		this.weekend_hours = "";
		this.description = "";
	}

	public void revertName() {
		this.name = this.origName;
	}
	
	class Hours {
		// internal class that checks if the building is open based on
		// the building's passed in hours
		private Calendar regOpen;
		private Calendar regClose;
		private Calendar weekOpen;
		private Calendar weekClose;
		private Calendar current;

		public Hours(String reg_hours, String week_hours) {
			try {
				// error checking
				if (reg_hours == null)
					return;
				if (reg_hours.length() < 8)
					return;
				current = Calendar.getInstance();
				String rOpen = reg_hours.substring(0, 2) + ":"
						+ reg_hours.substring(2, 4) + ":00";
				String rClose = reg_hours.substring(4, 6) + ":"
						+ reg_hours.substring(6, 8) + ":00";
				// creates a date object to store the parsed times
				Date time1 = new SimpleDateFormat("HH:mm:ss",
						java.util.Locale.getDefault()).parse(rOpen);
				regOpen = Calendar.getInstance();
				regOpen.setTime(time1);
				regOpen.set(Calendar.DATE, current.get(Calendar.DATE));
				time1 = new SimpleDateFormat("HH:mm:ss",
						java.util.Locale.getDefault()).parse(rClose);
				regClose = Calendar.getInstance();
				regClose.setTime(time1);
				regClose.set(Calendar.DATE, current.get(Calendar.DATE));

				if (week_hours == null)
					return;
				if (week_hours.length() < 8)
					return;
				String wOpen = week_hours.substring(0, 2) + ":"
						+ week_hours.substring(2, 4) + ":00";
				String wClose = week_hours.substring(4, 6) + ":"
						+ week_hours.substring(6, 8) + ":00";
				time1 = new SimpleDateFormat("HH:mm:ss",
						java.util.Locale.getDefault()).parse(wOpen);
				weekOpen = Calendar.getInstance();
				weekOpen.setTime(time1);
				weekOpen.set(Calendar.DATE, current.get(Calendar.DATE));
				time1 = new SimpleDateFormat("HH:mm:ss",
						java.util.Locale.getDefault()).parse(wClose);
				weekClose = Calendar.getInstance();
				weekClose.setTime(time1);
				weekClose.set(Calendar.DATE, current.get(Calendar.DATE));

			} catch (java.text.ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public boolean isOpen() {
			if (regOpen == null)
				return false;
			boolean op = false;
			// //Log.v("from printing building","got to main function");
			int day = current.get(Calendar.DAY_OF_WEEK);
			if (day != 0 && day != 7) {
				// //Log.v("from printing building","got the correct day of the week");
				boolean before = current.get(Calendar.HOUR_OF_DAY) < regClose
						.get(Calendar.HOUR_OF_DAY);
				before = before
						& (current.get(Calendar.MINUTE) < regClose
								.get(Calendar.MINUTE));
				// Log.v("from printing building","close hour: " +
				// regClose.get(Calendar.HOUR_OF_DAY));
				// Log.v("from printing building","current hours: " +
				// current.get(Calendar.HOUR_OF_DAY));
				boolean after = current.after(regOpen);
				// Log.v("from printing building","before bool: " + before);
				// Log.v("from printing building","after bool: " + after);
				op = (before & after);
				return op;
			}
			if (weekOpen == null)
				return false;
			if (current.get(Calendar.DAY_OF_WEEK) < 2) {
				op = (current.after(weekOpen) && current.before(weekClose));
				return op;
			}
			return op;
		}

	}

	public void printBuilding() {
		// prints out the building information for testing purposes
		Log.v("from printing building","Name: " + name);
		Log.v("from printing building","Address: " + address);
		Log.v("from printing building","Longitude: " + longitude + " and Latitude: "
				+ latitude);
		System.out.print("Nicknames: ");
		for (String x : this.nicknames)
			System.out.print(x + ", ");
		Log.v("from printing building","reg hours: " + reg_hours);
		Log.v("from printing building","weekend hours: " + weekend_hours);

	}

	public boolean isOpen() {
		h = new Hours(reg_hours, weekend_hours);
		return h.isOpen();
	}

	
	public boolean equalTo (Building b) {
		boolean lat = b.getLatitude() == this.getLatitude();
		boolean lon = b.getLongitude() == this.getLongitude();
		return lat && lon;
	}
	
	public void addNicknames(String n) {
		if (n == null)
			return;
		String[] all = n.split(",");
		for (String x : all) {
			x = x.trim();
			this.nicknames.add(x);
		}
	}

	public ArrayList<String> getNicknames() {
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

	public double getLongitude() {
		return this.longitude;
	}

	public double getLatitude() {
		return this.latitude;
	}

	public String getGooglePlaceID() {
		return this.googlePlaceID;
	}

	public String getIconURL() {
		return this.iconURL;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String Name) {
		this.name = Name;
	}

	public String getAddress() {
		return this.address;
	}
}
