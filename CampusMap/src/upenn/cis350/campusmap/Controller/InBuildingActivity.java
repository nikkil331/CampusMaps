package upenn.cis350.campusmap.Controller;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import upenn.cis350.campusmap.R;
import upenn.cis350.campusmap.Views.ResultsView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class InBuildingActivity extends Activity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.in_building);
		
		String prefix = getIntent().getStringExtra("prefix");
		ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
		Field[] fields = R.drawable.class.getFields();
		for(Field field : fields){
			String fName = field.getName();
			if(fName.split("_")[0].equals(prefix)){
				Map<String, String> row = new HashMap<String, String>();
				row.put("image", Integer.toString(getResources().getIdentifier(fName, "drawable", getPackageName())));
				data.add(row);
			}
		}
		
		String[] from = {"image"};
		int[] to = {R.id.img};
		SimpleAdapter adapter = new SimpleAdapter(getBaseContext(), data, R.layout.floor_layout, from, to);
		ListView mylist = (ListView) findViewById(R.id.listView1);
		mylist.setAdapter(adapter);
		
		String pinInfo = getIntent().getStringExtra("info");
		TextView textView = (TextView)findViewById(R.id.InfoText1);
		textView.setText(Html.fromHtml(pinInfo));
		textView.setMovementMethod(new ScrollingMovementMethod());
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	public void goOutside(View view){
		Intent i = new Intent();
		setResult(RESULT_OK, i);
		finish();
	}
}
