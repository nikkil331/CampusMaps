package upenn.cis350.campusmap.test;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

import upenn.cis350.campusmap.Controller.Event;
import upenn.cis350.campusmap.Controller.GetEvents;


public class GetEventsTest {

	@Test
	public void testAllEvents() {
		GetEvents ge = new GetEvents("");
		Set<Event> s = ge.allEvents();
		assertTrue(s.size() == 6);
	}
	
	@Test
	public void testSomeEvents() {
		GetEvents ge = new GetEvents("");
		Set<Event> s = ge.todayEvents();
		assertTrue(s.size() == 4);
	}

}
