package upenn.cis350.campusmap.Controller;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import facebook4j.*;
import facebook4j.auth.AccessToken;

public class GetEvents {
	
	//private Facebook facebook;
	
	public GetEvents(String accessToken) { 
		/*if (accessToken == null)
			facebook = null;
		else {
		facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId("708959602495764", "94192c9abfb090f95891fa1681b0c620");
		facebook.setOAuthPermissions("email,public_profile,user_friends");
		facebook.setOAuthAccessToken(new AccessToken(accessToken, null));
		}*/
	}
	
	public String getName() {
		/*if (facebook == null)
			return null;
		try {
			return facebook.users().getMe().getFirstName() + " " +
			    facebook.users().getMe().getLastName();
		} catch (FacebookException e) {
			e.printStackTrace();
			return null;
		}*/
		return null;
	}
	
	public Set<Event> allEvents() {
		EventData data = new EventData();
		return data.getEvents();
	}
	
	public Set<Event> todayEvents() {
		EventData data = new EventData();
		Set<Event> todayE = new HashSet<Event>();
		
		for(Event e : data.getEvents()) {
			long time = e.getStartTime();
			Date d = new Date(time);
			long currentTime = System.currentTimeMillis();
			Date today = new Date(currentTime);
			
			String d1 = d.toString().substring(0, 11);
			String d2 = today.toString().substring(0, 11);
			
			if(d1.equals(d2))
				todayE.add(e);
		}

		return todayE;
	}
}