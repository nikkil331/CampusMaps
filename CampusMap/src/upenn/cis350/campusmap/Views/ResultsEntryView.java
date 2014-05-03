package upenn.cis350.campusmap.Views;

import upenn.cis350.campusmap.Controller.ResultsActivity;
import upenn.cis350.campusmap.R;
import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.EventLog.Event;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class ResultsEntryView extends LinearLayout{
	private TextView nameView;
	private TextView addressView;
	
	public ResultsEntryView(Context context, String name, String address, int id) {
		super(context);
		setId(id);
		nameView = new TextView(context);
		addressView = new TextView(context);
		//name view
		nameView.setText(name);
		nameView.setPadding(40,20,20,0);
		nameView.setTextSize(18);
		nameView.setTypeface(Typeface.SANS_SERIF);
		nameView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		addressView.setText(address);
		addressView.setPadding(40, 20, 20, 40);
		addressView.setTextSize(14);
		addressView.setTypeface(Typeface.SANS_SERIF);
		addressView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		//set up layout
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(15, 15, 15, 15);
		setLayoutParams(params);
		setOrientation(LinearLayout.VERTICAL);
		addView(nameView);
		addView(addressView);
		setBackgroundResource(R.drawable.shadow);
		
		setOnTouchListener((ResultsActivity)getContext());
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent e){
	
		if(e.getAction() == MotionEvent.ACTION_DOWN){
			Log.v("ResultsEntryView", "touch event");
			setBackgroundColor(getResources().getColor(android.R.color.holo_blue_bright));
			setAlpha((float) 0.7);
			invalidate();
		}
		return true;
	}
	
	public String getAddress(){
		return (String)addressView.getText();
	}
	
	public String getName(){
		return (String)nameView.getText();
	}
	
}
