package upenn.cis350.campusmap.Controller;


import upenn.cis350.campusmap.R;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class Pin {
	private GoogleMap nMap;
	private double latitude;
	private double longitude;
	private String title;
	private boolean isOpen;
	private MarkerOptions m;
	private String description;
	private Marker mark;
	private Building build;
	private LatLng loc;
	
	public Pin (GoogleMap m, Building b) {
		this.nMap = m;
		if (b == null) {
			latitude = 0;
			longitude = 0;
			title = "This is not a real pin, the building passed was null";
			isOpen = false;
			description = "";
			loc = new LatLng (0,0);
			return;
		}
		this.latitude = b.getLatitude();
		this.longitude = b.getLongitude();
		this.loc = new LatLng(this.latitude, this.longitude);
		this.title = b.getName();
		this.isOpen = b.isOpen();
		this.description = b.getDescription();
		this.build = b;
	}
	
	public void setLatLng (LatLng x) {
		this.loc = x;
		this.latitude = x.latitude;
		this.longitude = x.longitude;
	}
	
	public LatLng getLatLng() {
		return this.loc;
	}
	
	public Building getBuilding() {
		return this.build;
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
	
	private void setColor (boolean b) {
		if (b) {
			m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
		}
		if (isOpen) m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
		else m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
		
	}
	
	public void addPin(boolean b) {
		if (m != null) {
			m.visible(false);
		}
		m = new MarkerOptions();
		m.position(new LatLng(latitude, longitude));
		m.title(title);
		this.setColor(b);
		m.draggable(false);
		if (b) m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
		mark = nMap.addMarker(m);
	}
	
	public void addEventPin() {
		if (m != null) {
			m.visible(false);
		}
		m = new MarkerOptions();
		m.position(new LatLng(latitude, longitude));
		m.title(title);
		this.setColor(false);
		m.icon(BitmapDescriptorFactory.fromResource(R.drawable.event));
		m.draggable(false);
		mark = nMap.addMarker(m);
	}
	
	public void removePin() {
		m.visible(false);
		mark.remove();
	}
	
	public MarkerOptions getMarkerOptions() {
		return m;
	}
}
