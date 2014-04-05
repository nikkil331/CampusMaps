package upenn.cis350.campusmap.Controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.JsonReader;
import android.util.Log;
import upenn.cis350.campusmap.R;

@SuppressLint("NewApi")
public class GeneralSearcher implements Searcher {
	
	private String apikey;
	private boolean hasLocationSensor;
	private String latitude;
	private String longitude;
	private String radius;
	private static String API_BASE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";
	
	/**
	 * GeneralSearcher needs apikey to query places api
	 * latitude, longitude and radius specify in what vicinity to search 
	 * all strings necessary are found in res/values/strings.xml
	 * hasLocationSensor indicates whether or not the device has gps sensor
	 */
	public GeneralSearcher(String apikey, boolean hasLocationSensor, String latitude, String longitude, String radius){
		super();
		this.apikey = apikey;
		this.hasLocationSensor = hasLocationSensor;
		this.latitude = latitude;
		this.longitude = longitude;
		this.radius = radius;
	}

	/**
	 * returns null when an error occurred while talking to google's servers
	 */
	public List<Building> getBuildings(String query){
		query = formatQuery(query);
		String apirequest = buildRequest(query);
		String apiresponse = makeHTTPRequest(apirequest);
		if(apiresponse.equals("")){
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
	
	//replaces spaces with "+" signs for api query
	private String formatQuery(String query){
		String[] queryTokens = query.split(" ");
		StringBuilder sb = new StringBuilder();
		for(String t: queryTokens){
			sb.append(t + "+");
		}
		//get rid of last plus
		sb.delete(sb.length() - 1, sb.length());
		query = sb.toString();
		return query;
	}
	
	//creates get request url out of given information 
	private String buildRequest(String query){
		String sensor = hasLocationSensor ? "true" : "false";
		String apirequest = API_BASE_URL + "?query=" + query + "&sensor=" + sensor + 
				"&key=" + apikey + "&location=" + latitude + "," + longitude + "&radius=" + radius;
		return apirequest;
	}
	
	private String makeHTTPRequest(String URL){
	    HttpClient httpclient = new DefaultHttpClient();
	   
	    //try executing request
	    HttpResponse response = null;
		try {
			response = httpclient.execute(new HttpGet(URL));
		} 
		catch (ClientProtocolException e) { 
			Log.v("General Searcher", "ClientProtocolException when making request");
			return "";
		} 
		catch (IOException e) { 
			Log.v("General Searcher", "IOException when making request");
			return "";
		}
		
		//check if response is not an error
	    StatusLine statusLine = response.getStatusLine();
	    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        try {
				response.getEntity().writeTo(out);
			} catch (IOException e) {
				Log.v("General Searcher", "IOException when writing HTTP Response");
				return "";
			}
	        try {
				out.close();
			} catch (IOException e) {
				Log.v("General Searcher", "IOException when writing HTTP Response");
				return "";
			}
	        String responseString = out.toString();
	        return responseString;
	    } else{
	        //Closes the connection.
	        try {
				response.getEntity().getContent().close();
			} 
	        catch (IllegalStateException e) {
	        	Log.v("General Searcher", "IllegalStateException when closing connection");
	        }
	        catch (IOException e) { 
	        	Log.v("General Searcher", "IOException when closing connection");
	        }
	        return "";
	    }
	}
	
	//reads json response from places api
	private List<Building> parseAPIResponse(String response) throws IOException{
		JsonReader reader = new JsonReader(new StringReader(response));
		List<Building> buildings = new ArrayList<Building>();
		//start the parse
		reader.beginObject();
		while(reader.hasNext()){
			String outerTag = reader.nextName();
			if(outerTag.equals("error-message")){
				//return empty list
				return new ArrayList<Building>();
			}
			else if(outerTag.equals("results")){
				buildings = parseResultsArray(reader);
			}
			else{
				reader.skipValue();
			}	
		}
		reader.endObject();
		reader.close();
		return buildings;
	}
	
	private List<Building> parseResultsArray(JsonReader reader) throws IOException{
		List<Building> buildings = new ArrayList<Building>();
		//begin results array
		reader.beginArray();
		while(reader.hasNext()){
			reader.beginObject();
			String address = "", name = "", id = "", icon = "";
			double latitude = 0.0 , longitude = 0.0;
			while(reader.hasNext()){
				String tag = reader.nextName();
				if(tag.equals("formatted_adress")){
					address = reader.nextString();
				}
				else if (tag.equals("geometry")){
					reader.beginObject();
					while(reader.hasNext()){
						String innerTag = reader.nextName();
						if(innerTag.equals("location")){
							reader.beginObject();
							reader.nextName();
							latitude = Double.parseDouble(reader.nextString());
							reader.nextName();
							longitude = Double.parseDouble(reader.nextString());
							reader.endObject();
						}
						else{ reader.skipValue();}
					}
					reader.endObject();
				}
				else if (tag.equals("icon")){
					icon = reader.nextString();
				}
				else if (tag.equals("id")){
					id = reader.nextString();
				}
				else if (tag.equals("name")){
					name = reader.nextString();
				}
				else{ reader.skipValue(); }
			}
			reader.endObject();
			Building b = new Building(longitude, latitude, id, icon, name, address);
			buildings.add(b);
		}
		reader.endArray();
		return buildings;
	}
	
}
