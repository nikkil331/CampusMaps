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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultsView extends View{
	LinearLayout layout;
	public ResultsView(Context context, Bundle b) {
		super(context);
		//setup outer vertical linear layout
		layout = new LinearLayout(context);
		layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		layout.setOrientation(LinearLayout.VERTICAL);
		
		//divider line between entries
		ShapeDrawable divider = new ShapeDrawable(new RectShape());
		divider.getPaint().setColor(color.darker_gray);
		divider.setBounds(10, 0, 200, 3);
		layout.setDividerDrawable(getResources().getDrawable(R.drawable.divider));
		layout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
		//get entry results
		List<String> names = b.getStringArrayList("names");
		List<String> addresses = b.getStringArrayList("addresses");
		for(int i = 0; i < names.size(); i++){
			ResultsEntryView result = new ResultsEntryView(context, names.get(i), addresses.get(i));
			layout.addView(result);
		}
		
	}
	
	public void onDraw(Canvas canvas){
		layout.draw(canvas);
	}


}
