package upenn.cis350.campusmap.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.EventLog.Event;
import android.view.MotionEvent;
import android.widget.TextView;
import android.view.View;

public class ResultsEntryView extends TextView{
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
		
		//addressView
		addressView.setText("address");
		addressView.setPadding(20, 0, 0, 0);
		addressView.setTextSize(16);
		addressView.setTypeface(Typeface.SANS_SERIF);
	}
	
	@Override
	public void onDraw(Canvas canvas){
		nameView.draw(canvas);
		addressView.draw(canvas);
	}
	
	public String getAddress(){
		return (String)addressView.getText();
	}
	
	public String getName(){
		return (String)nameView.getText();
	}
	
}
