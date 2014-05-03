package upenn.cis350.campusmap.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ExamLocationParser {

	private BufferedReader br;
	private Map<String, String[]> examLocs;

	ExamLocationParser(String x) {
		try {
			URL url = new URL(x);
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			examLocs = new HashMap<String, String[]>();
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("I/O Error: " + e.getMessage());
		}
	}

	public void parse() {
		try {
			String line = new String();
			String[] data = new String[6];
			String temp = new String();
			while (((line = br.readLine()) != null)) {
				data = line.split(",");
				temp = data[1].substring(0, data[0].indexOf('-'));
				temp = temp.trim();
				temp = temp + data[1].substring(data[0].indexOf('-'));
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
		return output;
	}
}
