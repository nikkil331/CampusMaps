package upenn.cis350.campusmap.Controller;

import java.util.List;

import android.os.AsyncTask;

public abstract class Searcher extends AsyncTask<String, Void, List<Building>>{
	protected MainActivity activity; 
	Parser p = new Parser("buildings.xml");
	
	public abstract List<Building> getBuildings(String query);
	
	@Override
	protected List<Building> doInBackground(String... params) {
		p.Parse();
		return getBuildings(params[0]);
	}
	
	@Override
	protected void onPostExecute(List<Building> results){
		activity.receiveSearchResults(results);
	}
}
