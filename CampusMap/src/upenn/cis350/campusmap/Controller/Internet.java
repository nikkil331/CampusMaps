package upenn.cis350.campusmap.Controller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Internet {
	public static boolean hasActiveInternetConnection(Context context) {
		String LOG_TAG = "Tried to get internet";
		if (isNetworkAvailable(context)) {
			try {
				HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
				urlc.setRequestProperty("User-Agent", "Test");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(1500); 
				urlc.connect();
				return (urlc.getResponseCode() == 200);
			} catch (IOException e) {
				Log.e(LOG_TAG , "Error checking internet connection", e);
			}
		} else {
			Log.d(LOG_TAG, "No network available!");
		}
		return false;
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
}
