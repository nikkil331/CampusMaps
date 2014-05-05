package upenn.cis350.campusmap.Controller;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;

public abstract class Searcher extends AsyncTask<String, Void, List<Building>> {
	protected OurActivity activity;
	protected Parser p;
	protected ExamLocationParser pp;
	protected ClassLocationParser clp;

	public abstract List<Building> getBuildings(String query);

	@Override
	protected List<Building> doInBackground(String... params) {
		p = new Parser(
				"https://raw.githubusercontent.com/nikkil331/CampusMaps/master/buildings.xml");
		pp = new ExamLocationParser(
				"https://raw.githubusercontent.com/nikkil331/CampusMaps/master/CampusMap/CIS350_Final_Exams.csv");
		clp = new ClassLocationParser(
				"https://raw.githubusercontent.com/nikkil331/CampusMaps/master/CampusMap/Class_Locations.txt");
		p.Parse();
		pp.parse();
		try {
			clp.bigparse();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getBuildings(params[0]);
	}

	@Override
	protected void onPostExecute(List<Building> results) {
		activity.receiveSearchResults(results);
	}
}
