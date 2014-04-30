package upenn.cis350.campusmap.Views;

import upenn.cis350.campusmap.Controller.ResultsActivity;
import android.R;
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
		nameView.setPadding(50,20,50,0);
		nameView.setTextSize(22);
		nameView.setTypeface(Typeface.SANS_SERIF);
		nameView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		addressView.setText(address);
		addressView.setPadding(50, 20, 50, 0);
		addressView.setTextSize(16);
		addressView.setTypeface(Typeface.SANS_SERIF);
		addressView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		//set up layout
		setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		setOrientation(LinearLayout.VERTICAL);
		setPadding(0,0,0,30);
		addView(nameView);
		addView(addressView);
		
		setOnTouchListener((ResultsActivity)getContext());
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent e){
	
		if(e.getAction() == MotionEvent.ACTION_DOWN){
			Log.v("ResultsEntryView", "touch event");
			setBackgroundColor(getResources().getColor(R.color.holo_blue_bright));
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
