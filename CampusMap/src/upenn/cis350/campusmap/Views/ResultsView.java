package upenn.cis350.campusmap.Views;

import java.util.ArrayList;
import java.util.List;



import upenn.cis350.campusmap.R;
import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultsView extends LinearLayout{

	public ResultsView(Context context, Bundle b) {
		super(context);
		
		setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		setOrientation(LinearLayout.VERTICAL);
		
		//divider line between entries
		setDividerDrawable(getResources().getDrawable(R.drawable.divider));
		setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
		//get entry results
		List<String> names = b.getStringArrayList("names");
		List<String> addresses = b.getStringArrayList("addresses");
		for(int i = 0; i < names.size(); i++){
			String name = names.get(i);
			String address = addresses.get(i);
			ResultsEntryView result = new ResultsEntryView(context, name, address, i);
			addView(result);
		}
		
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
