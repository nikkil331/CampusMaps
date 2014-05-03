package upenn.cis350.campusmap.Views;

import java.util.ArrayList;
import java.util.List;






import upenn.cis350.campusmap.R;
import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultsView extends FrameLayout{

	public ResultsView(Context context, Bundle b) {
		super(context);
		
		setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		setBackgroundColor(Color.rgb(242,242,242));
		getBackground().setAlpha(128);
		setPadding(30, 30, 30, 0);
		
		LinearLayout linear = new LinearLayout(context);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		linear.setLayoutParams(layoutParams);
		linear.setOrientation(LinearLayout.VERTICAL);
		linear.setBackgroundResource(R.drawable.trans_container);
		
		//get entry results
		List<String> names = b.getStringArrayList("names");
		List<String> addresses = b.getStringArrayList("addresses");
		for(int i = 0; i < names.size(); i++){
			String name = names.get(i);
			String address = addresses.get(i);
			ResultsEntryView result = new ResultsEntryView(context, name, address, i);
			linear.addView(result);
		}
		
		addView(linear);
		
		ImageView logo = new ImageView(context);
		logo.setImageResource(R.drawable.ic_launcher_web);
		FrameLayout.LayoutParams logoParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT,  Gravity.RIGHT | Gravity.BOTTOM);
		logo.setLayoutParams(logoParams);	
		logo.setPadding(0, 0, -40, 25);
		addView(logo,0);

	}
	
	@Override
	public boolean onTouchEvent(MotionEvent e){
		
		if(e.getAction() == MotionEvent.ACTION_DOWN){
			Log.v("ResultsView", "touch event");
			return false;
		}
		return true;
	}
}
