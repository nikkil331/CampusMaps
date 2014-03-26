package upenn.cis350.campusmap.Controller;

import java.util.List;

public interface Searcher {
	public List<Building> getBuildings(String query);
}
