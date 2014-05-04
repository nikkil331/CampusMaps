package upenn.cis350.campusmap.Controller;

import java.util.List;
import android.os.AsyncTask;

public abstract class Searcher extends AsyncTask<String, Void, List<Building>> {
	protected MainActivity activity;
	protected Parser p;
	protected ExamLocationParser pp;

	public abstract List<Building> getBuildings(String query);

	@Override
	protected List<Building> doInBackground(String... params) {
		p = new Parser(
				"https://raw.githubusercontent.com/nikkil331/CampusMaps/master/buildings.xml");
		pp = new ExamLocationParser(
				"https://raw.githubusercontent.com/nikkil331/CampusMaps/master/CampusMap/CIS350_Final_Exams.csv");
		p.Parse();
		pp.parse();
		return getBuildings(params[0]);
	}

	@Override
	protected void onPostExecute(List<Building> results) {
		activity.receiveSearchResults(results);
	}
}
