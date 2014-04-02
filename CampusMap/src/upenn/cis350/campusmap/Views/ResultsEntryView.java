package upenn.cis350.campusmap.Views;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.EventLog.Event;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class ResultsEntryView extends TextView{
	private LinearLayout layout;
	private TextView nameView;
	private TextView addressView;
	
	public ResultsEntryView(Context context, String name, String address) {
		super(context);
		nameView = new TextView(context);
		addressView = new TextView(context);
		//name view
		nameView.setText("name");
		nameView.setPadding(20,10,0,0);
		nameView.setTextSize(24);
		nameView.setTypeface(Typeface.SANS_SERIF);
		nameView.setTextColor(555);
		nameView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		//addressView
		addressView.setText("address");
		addressView.setPadding(20, 0, 0, 10);
		addressView.setTextSize(16);
		addressView.setTypeface(Typeface.SANS_SERIF);
		addressView.setTextColor(color.darker_gray);
		addressView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		
		//set up layout
		layout = new LinearLayout(context);
		layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.addView(nameView);
		layout.addView(addressView);
		
	}
	
	@Override
	public void onDraw(Canvas canvas){
		nameView.draw(canvas);
		addressView.draw(canvas);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e){
		if(e.getAction() == MotionEvent.ACTION_DOWN){
			layout.setBackgroundColor(color.holo_blue_bright);
			layout.setAlpha((float) 0.7);
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
