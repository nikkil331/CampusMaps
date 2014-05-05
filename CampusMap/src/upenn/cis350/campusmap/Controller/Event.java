package upenn.cis350.campusmap.Controller;
public class Event {
	
	private String venue;
	private long startTime;
	private long endTime;
	private String name;
	private String description;
	private String id;
	
	public Event(String id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setVenue(String venue) {
		this.venue = venue;
	}
	public void setTime(long start, long end) {
		startTime = start;
		endTime = end;
	}
	
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}
	public String getVenue() {
		return venue;
	}
	public long getStartTime() {
		return startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public String getId() {
		return id;
	}
}