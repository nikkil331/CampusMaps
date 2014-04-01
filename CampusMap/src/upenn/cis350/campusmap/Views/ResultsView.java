package upenn.cis350.campusmap.Views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class ResultsView extends View{
	List<ResultsEntryView> results = new ArrayList<ResultsEntryView>();
	public ResultsView(Context context, Bundle b) {
		super(context);
		List<String> names = b.getStringArrayList("names");
		List<String> addresses = b.getStringArrayList("addresses");
		for(int i = 0; i < names.size(); i++){
			ResultsEntryView result = new ResultsEntryView(context, names.get(i), addresses.get(i));
			results.add(result);
		}
	}
	
	public void onDraw(Canvas canvas){
		for(ResultsEntryView r : results){
			r.draw(canvas);
		}
	}


}
