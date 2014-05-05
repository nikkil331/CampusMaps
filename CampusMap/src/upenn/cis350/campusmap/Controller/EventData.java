package upenn.cis350.campusmap.Controller;
import java.util.HashSet;
import java.util.Set;

public class EventData {
	private Set<Event> events;
	private long timeNow;
	
	public EventData() {
		events = new HashSet<Event>();
		
		timeNow = System.currentTimeMillis();
		
		Event a = new Event("001");
		a.setName("Quadramics Presents: Hair");
		a.setDescription("Q's 2014 Fling Musical! Let the sun shine in!");
		a.setTime(plusHours(4), plusHours(7));
		a.setLatLong(39.9548, -75.196899);
		a.setVenue("Iron Gate Theatre");
		events.add(a);
		
		Event a1 = new Event("006");
		a1.setName("CIS 350 Final Project Demo");
		a1.setDescription("It's time for the final Project Demo!");
		a1.setTime(plusHours(6), plusHours(7));
		a1.setLatLong(39.952202, -75.1912);
		a1.setVenue("Levine");
		events.add(a1);
		
		Event b = new Event("002");
		b.setName("SPEC Concert Presents David Guetta");
		b.setDescription("Fling concert. Ra Ra Riot, Magic Man, Guetta.");
		b.setTime(plusHours(8), plusHours(11));
		b.setLatLong(39.9501, -75.189903);
		b.setVenue("Franklin Field");
		events.add(b);
		
		Event c = new Event("003");
		c.setName("Red Hot Chili Peppers");
		c.setDescription("I'm With You Tour");
		c.setTime(plusHours(3), plusHours(5));
		c.setVenue("Venice Beach, CA");
		c.setLatLong(33.9875155, -118.4617151);
		events.add(c);
		
		Event d = new Event("004");
		d.setName("Sunday Brunch Reunion");
		d.setDescription("The cast of Taramandal meets at Hill for brunch.");
		d.setTime(plusHours(15), plusHours(16));
		d.setLatLong(39.952999, -75.190697);
		d.setVenue("Hill College House");
		events.add(d);
		
		Event e = new Event("005");
		e.setName("FTC Presents: Rabbit Hole");
		e.setDescription("Front Row's spring 2014 show. #SoRelavent");
		e.setTime(plusHours(30), plusHours(32));
		e.setLatLong(39.951, -75.193901);
		e.setVenue("Houston Hall");
		events.add(e);
		
		Event f = new Event("006");
		f.setName("PenNaatak Load In");
		f.setDescription("Come help Naatak load in for their show!");
		f.setTime(plusHours(40), plusHours(52));
		f.setLatLong(39.9519, -75.201103);
		f.setVenue("Harrison College House");
		events.add(f);
	}
	
	public Set<Event> getEvents() {
		return events;
	}
	
	private long plusHours(double hours) {
		return timeNow + (long)(1000 * 60 * 60 * hours);
	}
}