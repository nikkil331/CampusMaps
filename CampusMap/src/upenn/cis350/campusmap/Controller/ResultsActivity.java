package upenn.cis350.campusmap.Controller;

import java.util.ArrayList;
import java.util.List;

import upenn.cis350.campusmap.R;
import upenn.cis350.campusmap.Views.ResultsEntryView;
import upenn.cis350.campusmap.Views.ResultsView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ResultsActivity extends Activity implements View.OnClickListener{
	
	List<String> names;
	List<String> addresses;

	protected void onCreate(Bundle savedInstanceState) {
		names = savedInstanceState.getStringArrayList("names");
		addresses = savedInstanceState.getStringArrayList("addresses");
		setContentView(new ResultsView(this, savedInstanceState));
	}

	@Override
	public void onClick(View v) {
		if(v instanceof ResultsEntryView){
			String address = ((ResultsEntryView) v).getAddress();
			String name = ((ResultsEntryView) v).getName();
			int index = addresses.indexOf(address);
			if(names.get(index).equals(name)){
				Intent i = new Intent();
				i.putExtra("listIndex", index);
				setResult(RESULT_OK, i);
				finish();
			}
		}
		
	}
	
	

}
