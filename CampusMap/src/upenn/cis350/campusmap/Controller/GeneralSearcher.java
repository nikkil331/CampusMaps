package upenn.cis350.campusmap.Controller;

import java.io.*;
import java.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.*;

@SuppressLint("NewApi")
public class GeneralSearcher extends Searcher {

	private String apikey;
	private boolean hasLocationSensor;
	private String latitude;
	private String longitude;
	private String radius;
	private static String API_BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";

	/**
	 * GeneralSearcher needs apikey to query places api latitude, longitude and
	 * radius specify in what vicinity to search all strings necessary are found
	 * in res/values/strings.xml hasLocationSensor indicates whether or not the
	 * device has gps sensor
	 */
	public GeneralSearcher(MainActivity activity, String apikey,
			boolean hasLocationSensor, String latitude, String longitude,
			String radius) {
		super();
		this.apikey = apikey;
		this.hasLocationSensor = hasLocationSensor;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
		this.activity = activity;

	}

	/**
	 * returns null when an error occurred while talking to google's servers
	 */
	public List<Building> getBuildings(String query) {
		int code = queryType(query);
		switch (code) {
		case 1:
			return getBuildingFromName(query);
		case 2:
			return getBuildingFromCode(query);
		case 3:
			return getBuildingFromExam(query);
		case 4:
			return getBuildingFromClass(query);
		default:
			return getBuildingsFromGoogle(query);
		}
	}

	private List<Building> getBuildingFromClass(String query) {
		List<Building> toReturn = new ArrayList<Building>();
		String q = query.split(" ")[0].toUpperCase();
		if (clp.getMap().containsKey(q)) {
			toReturn.addAll(getBuildingFromCode(clp.getMap().get(q)));
		} else
			toReturn.addAll(getBuildingsFromGoogle(clp.getMap().get(q)));
		return toReturn;
	}

	private List<Building> getBuildingFromExam(String query) {
		// System.out.println(query.split(" ")[0]);
		List<Building> toReturn = new ArrayList<Building>();
		if (pp.getMap().containsKey(query.split(" ")[0])) {

			String[] x = pp.convertToBuildingCodes(pp.getMap().get(
					query.split(" ")[0]));

			for (String i : x) {
				toReturn.addAll(getBuildingFromCode(i));
			}
		} else if (pp.examLocsDefaults.containsKey(query.split(" ")[0])) {
			Map<String, String[]> x = (pp.examLocsDefaults
					.get(query.split(" ")[0]));

			for (String out : x.keySet()) {
				for (String i : x.get(out)) {
					String y = new String();
					if (x.equals("VAN PELT FILM CENTER")) {
						y = i;
					} else {
						y = i.substring(0, i.indexOf(' '));
					}
					List<Building> no = getBuildingFromCode(y);
					List<Building> don = new ArrayList<Building>();
					for (Building n : no) {
						don.add(new Building(n.getLongitude(), n.getLatitude(),
								n.getGooglePlaceID(), n.getIconURL(), n.getName(), n.getAddress()));
					}
					toReturn.addAll(don);
				}
				for (Building b : toReturn) {
					b.setName(b.getName() + " - For Class: " + out);
				}
			}
		} else {
			toReturn = getBuildingFromCode(query);
		}
		return toReturn;
	}

	private List<Building> getBuildingFromCode(String query) {

		HashMap<String, Building> codeMap = p.getCodeMap();
		List<Building> results = new LinkedList<Building>();
		String q = query.toLowerCase(Locale.US);
		int i = 0;
		try {
			for (String code : codeMap.keySet()) {
				if (code == null) {
					continue;
				}
				if (matchCode(code, q)) {
					results.add(codeMap.get(code));
				}
			}
			if (results.size() != 0)
				return results;

			return getBuildingFromName(query);
		} catch (NullPointerException e) {
			Log.v("General Searcher",
					" Null Pointer Exception in getBuildingFromCode");
			return new ArrayList<Building>();
		}
	}

	private boolean matchCode(String code, String q) {
		q = q.toLowerCase(Locale.US);
		String c = code.toLowerCase();
		return c.contains(q);
	}

	private boolean match(String code, String q) {
		q = q.toLowerCase(Locale.US);
		String c = code.toLowerCase(Locale.US);
		String[] terms = q.split(" ");
		double i = 0;
		for (String t : terms) {
			if (c.contains(t))
				i += 1;
		}
		double t = terms.length + 0.0;
		return i / t > 0.75;
	}

	private int queryType(String query) {
		String q = query.trim();
		String[] qq = q.split(" ");
		if (qq.length == 2) {
			if (qq[1].toLowerCase().equals("exam")) {
				return 3;
			}
		}
		if (qq.length == 2) {
			if (qq[1].toLowerCase().equals("class")) {
				return 4;
			}
		}
		if ((q.length() == 4 || q.length() == 3) && q.split(" ").length == 1) {
			return 2;
		}
		if (!q.contains(",") && q.split(" ").length <= 4)
			return 1;

		return 0;
	}

	private List<Building> getBuildingFromName(String query) {
		HashMap<String, Building> nameMap = p.getNameMap();
		HashMap<String, Building> nicknameMap = p.getNicknameMap();
		List<Building> results = new ArrayList<Building>();
		for (String name : nameMap.keySet()) {
			if (name == null)
				continue;
			if (match(name, query.trim())) {
				Building b = nameMap.get(name);
				Log.v("GeneralSearcher.getBuildingFromName(Name)",
						"Result added :" + b.getName());
				results.add(b);
			}

		}

		for (String name : nicknameMap.keySet()) {
			if (name == null)
				continue;
			if (match(name, query.trim())) {
				Building b = nicknameMap.get(name);
				if (!results.contains(b)) {
					results.add(b);
					Log.v("GeneralSearcher.getBuildingFromName(NKName)",
							"Result added (" + name + ") :" + b.getName());
				}
			}

		}
		if (results.size() == 0) {
			Log.v("GeneralSearcher.getBuildingFromName",
					"No results found on DB, Calling Google");
			return getBuildingsFromGoogle(query);
		}

		return results;
	}

	private List<Building> getBuildingsFromGoogle(String query) {
		// this would be where you would check the database against the various
		// query entries
		query = formatQuery(query);
		String apirequest = buildRequest(query);
		String apiresponse = makeHTTPRequest(apirequest);
		if (apiresponse.equals("")) {
			return null;
		}
		List<Building> results = new ArrayList<Building>();
		try {
			results = parseAPIResponse(apiresponse);
		} catch (IOException e) {
			Log.v("General Searcher", "IOException when reading json response");
		}
		return results;
	}

	// replaces spaces with "+" signs for api query
	private String formatQuery(String query) {
		if (query == null) {
			return "";
		} else {
			String[] queryTokens = query.split(" ");
			StringBuilder sb = new StringBuilder();
			for (String t : queryTokens) {
				sb.append(t + "+");
			}
			// get rid of last plus
			sb.delete(sb.length() - 1, sb.length());
			query = sb.toString();
			return query;
		}
	}

	// creates get request url out of given information
	private String buildRequest(String query) {
		String sensor = hasLocationSensor ? "true" : "false";
		String apirequest = API_BASE_URL + "?query=" + query + "&sensor="
				+ sensor + "&key=" + apikey + "&location=" + latitude + ","
				+ longitude + "&radius=" + radius;
		Log.v("GeneralSearcher", apirequest);
		return apirequest;
	}

	private String makeHTTPRequest(String URL) {
		Log.v("GeneralSearcher", URL);
		HttpClient httpclient = new DefaultHttpClient();

		// try executing request
		HttpResponse response = null;
		try {
			response = httpclient.execute(new HttpGet(URL));
		} catch (ClientProtocolException e) {
			Log.v("General Searcher",
					"ClientProtocolException when making request");
			return "";
		} catch (IOException e) {
			Log.v("General Searcher", "IOException when making request");
			return "";
		}

		// check if response is not an error
		StatusLine statusLine = response.getStatusLine();
		if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try {
				response.getEntity().writeTo(out);
			} catch (IOException e) {
				Log.v("General Searcher",
						"IOException when writing HTTP Response");
				return "";
			}
			try {
				out.close();
			} catch (IOException e) {
				Log.v("General Searcher",
						"IOException when writing HTTP Response");
				return "";
			}
			String responseString = out.toString();
			return responseString;
		} else {
			// Closes the connection.
			try {
				response.getEntity().getContent().close();
			} catch (IllegalStateException e) {
				Log.v("General Searcher",
						"IllegalStateException when closing connection");
			} catch (IOException e) {
				Log.v("General Searcher", "IOException when closing connection");
			}
			return "";
		}
	}

	// reads json response from places api
	private List<Building> parseAPIResponse(String response) throws IOException {
		JsonReader reader = new JsonReader(new StringReader(response));
		List<Building> buildings = new ArrayList<Building>();
		// start the parse
		reader.beginObject();
		while (reader.hasNext()) {
			String outerTag = reader.nextName();
			if (outerTag.equals("error-message")) {
				// return empty list
				return new ArrayList<Building>();
			} else if (outerTag.equals("results")) {
				buildings = parseResultsArray(reader);
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		reader.close();
		return buildings;
	}

	private List<Building> parseResultsArray(JsonReader reader)
			throws IOException {
		List<Building> buildings = new ArrayList<Building>();
		// begin results array
		reader.beginArray();
		while (reader.hasNext()) {
			reader.beginObject();
			String address = "", name = "", id = "", icon = "";
			double latitude = 0.0, longitude = 0.0;
			while (reader.hasNext()) {
				String tag = reader.nextName();
				if (tag.equals("formatted_address")) {
					address = reader.nextString();
				} else if (tag.equals("geometry")) {
					reader.beginObject();
					while (reader.hasNext()) {
						String innerTag = reader.nextName();
						if (innerTag.equals("location")) {
							reader.beginObject();
							reader.nextName();
							latitude = Double.parseDouble(reader.nextString());
							reader.nextName();
							longitude = Double.parseDouble(reader.nextString());
							reader.endObject();
						} else {
							reader.skipValue();
						}
					}
					reader.endObject();
				} else if (tag.equals("icon")) {
					icon = reader.nextString();
				} else if (tag.equals("id")) {
					id = reader.nextString();
				} else if (tag.equals("name")) {
					name = reader.nextString();
				} else {
					reader.skipValue();
				}
			}
			reader.endObject();
			Building b = new Building(longitude, latitude, id, icon, name,
					address);
			buildings.add(b);
		}
		reader.endArray();
		return buildings;
	}

}