package upenn.cis350.campusmap.Controller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;


public class ClassLocationParser {
	private HashMap<String, String> classToLocation;
	private BufferedReader r;
	public ClassLocationParser(String fileLocation) {
		// TODO Auto-generated constructor stub
		URL fileLoc;
		InputStream is = null;
		boolean i = false;
		if (Internet.hasActiveInternetConnection(MainActivity.c)){
			try {
				fileLoc = new URL(fileLocation);
				URLConnection uc = fileLoc.openConnection();
				is = uc.getInputStream();
				i = true;
			}
			catch (Exception e) {
				e.printStackTrace();
			}

		}
		if (!i) {
			Log.v("internet error", "There was an internet error");
			Resources R = MainActivity.c.getResources();
			is = (R.openRawResource(
					R.getIdentifier("raw/class_locations",
							"raw", MainActivity.c.getPackageName())));
		}
		classToLocation = new HashMap<String, String>();
		Reader reader = new InputStreamReader(is);
		r = new BufferedReader (reader);
	}

	public void bigparse() throws IOException {
		String x = "";
		boolean test = false;
		while (!test) {
			if (!r.ready()) return;
			x = r.readLine();
			//	if (x.contains("ACCT-102")) System.out.println("contains in bp");
			if (x.length() > 20) test = x.substring(0, 20).contains("-");
		}
		String subject = x.substring(0, x.indexOf('-')+4);
		subject = subject.replace(" ", "");
		if (!r.ready()) return;
		while(individParse(subject, r.readLine()));
		if (!r.ready()) return;
		bigparse();
	}

	public boolean individParse(String x, String nextLine) throws IOException {
		String add = x;
		boolean subsection = false;
		if (nextLine == null) {
			return false;
		}
		//	if (nextLine.contains("ACCT-102")) System.out.println("contains in first indP");
		if (nextLine.length() > 1) {
			subsection = Character.isDigit(nextLine.charAt(1));
		}
		else {
			//	System.out.println("gets to end");
			//	printMap();
			return false;
		}
		while (!subsection) {
			nextLine = r.readLine();
			if (nextLine == null) {
				return false;
			}
			//		if (nextLine.contains("ACCT-102")) System.out.println("contains in second indP");
			if (nextLine.length() > 2) {
				subsection = Character.isDigit(nextLine.charAt(1));
			}
			else return false;
		}
		String [] arr = nextLine.split(" ");
		add += "-" + arr[1];
		if (arr[1].equals("000")) return true;
		if (arr[1].length() < 2) return true;
		if (arr[5].contains("TBA")) return true;
		
		if (arr[5].length() > 0) this.classToLocation.put(add, arr[5]);

		return true;
	}

	public void printMap() {
		for (String x : this.classToLocation.keySet()) {
			String y = this.classToLocation.get(x);
			System.out.println("class: " + x + ", location: " + y);
		}
	}

	public HashMap<String, String> getMap() {
		return this.classToLocation;
	}

	public static void main (String [] args) {
		ClassLocationParser cp = new ClassLocationParser("Class_Locations.txt");
		try {
			cp.bigparse();
			cp.printMap();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

