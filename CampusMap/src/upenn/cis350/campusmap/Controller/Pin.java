package upenn.cis350.campusmap.Controller;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class Pin {
	private GoogleMap nMap;
	private double latitude;
	private double longitude;
	private String title;
	private boolean isOpen;
	private MarkerOptions m;
	
	public Pin (GoogleMap m, double lat, double lon) {
		this.nMap = m;
		this.latitude = lat;
		this.longitude = lon;
		title = "";
		isOpen = true;
	}
	
	public void setTitle(String t) {
		this.title = t;
	}
	
	public void setOpen(boolean o){
		this.isOpen = o;
	}
	
	private void setColor () {
		if (isOpen) m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		else m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
	}
	
	public void addPin() {
		m = new MarkerOptions();
		m.position(new LatLng(latitude, longitude));
		m.title(title);
		this.setColor();
		nMap.addMarker(m);
	}
	
	public MarkerOptions getMarkerOptions() {
		return m;
	}
}
