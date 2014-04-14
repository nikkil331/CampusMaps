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
	private String description;
	
	public Pin (GoogleMap m, Building b) {
		this.nMap = m;
		if (b == null) return;
		this.latitude = b.getLatitude();
		this.longitude = b.getLongitude();
		this.title = b.getName();
		this.isOpen = b.isOpen();
		this.description = b.getDescription();
	}
	
	public String getDescription() {
		return this.description;
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
		m.draggable(false);
		nMap.addMarker(m);
	}
	
	public MarkerOptions getMarkerOptions() {
		return m;
	}
}
