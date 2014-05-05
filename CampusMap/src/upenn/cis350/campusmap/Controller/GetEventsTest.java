package upenn.cis350.campusmap.Controller;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;


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
