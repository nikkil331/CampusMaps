package upenn.cis350.campusmap.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ExamLocationParser {

	private BufferedReader br;
	private Map<String, String[]> examLocs;

	ExamLocationParser(String strURL) {
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strURL);
			
			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			br = new BufferedReader(new InputStreamReader(iStream));

			examLocs = new HashMap<String, String[]>();
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
	}
	
	protected Map<String,String[]> getMap(){
		return examLocs;
	}

	public void parse() {
		try {
			String line = new String();
			String[] data = new String[6];
			String temp = new String();
			while (((line = br.readLine()) != null)) {
				data = line.split(",");
				temp = data[0].substring(0, data[0].indexOf('-'));
				temp = temp.trim();
				temp = temp + data[0].substring(data[0].indexOf('-'));
				String[] locations = data[1].split(";");
				for (int i = 0; i < locations.length; i++) {
					locations[i] = locations[i].trim();
				}
				examLocs.put(temp, locations);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String[] removeDuplicates(String[] input) {
		Set<String> noDuplicates = new HashSet<String>();
		for (String x:input){
			if (!noDuplicates.contains(x)){
				noDuplicates.add(x);
			}
		}
		String[] toReturn = new String[noDuplicates.size()];
		int i=0;
		for (String x:noDuplicates){
			toReturn[i]=x;
			i++;
		}
		return toReturn;
	}

	public String[] convertToBuildingCodes(String[] input) {
		String[] output = new String[input.length];
		int i = 0;
		for (String x : input) {
			if (x.equals("VAN PELT FILM CENTER")) {
				output[i] = input[i];
			} else {
				output[i] = x.substring(0, x.indexOf(' '));
			}
			i++;
		}
		return removeDuplicates(output);
	}
}
