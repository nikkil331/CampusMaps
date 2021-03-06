package upenn.cis350.campusmap.Controller;

import java.lang.reflect.Field;
import java.util.ArrayList;

import upenn.cis350.campusmap.R;
import upenn.cis350.campusmap.Views.TouchImageView;
import android.app.Activity;
import android.content.Intent;
import android.view.GestureDetector;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher.ViewFactory;

public class InBuildingActivity extends Activity implements OnTouchListener, OnGestureListener{
	private ImageSwitcher imageSwitcher;
	private GestureDetector gest;
	private ArrayList<String> picNames = new ArrayList<String>();
	private int currIndex = 0;
	private String currImageName;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.in_building);
		
		String prefix = getIntent().getStringExtra("prefix");
		Field[] fields = R.drawable.class.getFields();
		for(Field field : fields){
			String fName = field.getName();
			if(fName.split("_")[0].equals(prefix)){
				picNames.add(fName);
			}
		}
		
		
		imageSwitcher = (ImageSwitcher)findViewById(R.id.imageSwitcher1);
		imageSwitcher.setFactory(new ViewFactory() {
			@Override
			public View makeView() {
				TouchImageView myView = new TouchImageView(getApplicationContext());
				myView.setAdjustViewBounds(true);
				myView.setLayoutParams(new ImageSwitcher.LayoutParams(ImageSwitcher.LayoutParams.
						MATCH_PARENT, ImageSwitcher.LayoutParams.MATCH_PARENT));
				myView.setTag(picNames.get(currIndex));
				return myView;
			}

		});
		currImageName = picNames.get(currIndex);
		int imageCode = getResources().getIdentifier(currImageName, "drawable", getPackageName());
		BitmapFactory.Options options=new BitmapFactory.Options();// Create object of bitmapfactory's option method for further option use
        options.inPurgeable = true; // inPurgeable is used to free up memory while required
        Bitmap image = BitmapFactory.decodeResource(getResources(), imageCode, options);
        Bitmap image1 = Bitmap.createScaledBitmap(image, image.getWidth(), image.getHeight(), true);
		imageSwitcher.setImageDrawable(new BitmapDrawable(image1));
		
		LinearLayout dots = (LinearLayout)findViewById(R.id.dots);
		for(int i = 0; i < picNames.size(); i++){
			ImageView dot = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(10, 10, 10, 10);
			dot.setLayoutParams(params);
			dot.setId(i);
			dot.setTag("highlighted");
			if(i == 0) { dot.setImageResource(R.drawable.circle_chosen); }
			else{ dot.setImageResource(R.drawable.circle); }
			dots.addView(dot);
		}
		
		String pinInfo = getIntent().getStringExtra("info");
		TextView textView = (TextView)findViewById(R.id.InfoText1);
		SpannableString text = new SpannableString(pinInfo);
		text.setSpan(new RelativeSizeSpan(1.25f), 0, pinInfo.indexOf("\n"),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		text.setSpan(new TextAppearanceSpan(this, Typeface.NORMAL, Color.rgb(51,51,51)), 0, pinInfo.indexOf("\n"),
		            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		text.setSpan(new TextAppearanceSpan(this, Typeface.NORMAL, Color.rgb(90, 90, 90)), pinInfo.indexOf("\n") + 1, pinInfo.length(),
	            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		textView.setText(text);
		
		gest = new GestureDetector(this, this);
		imageSwitcher.setOnTouchListener(this);
		findViewById(R.id.outsidebutton1).setOnTouchListener(this);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	public void goOutside(View view){
		Intent i = new Intent();
		setResult(RESULT_OK, i);
		finish();
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent e) {
		if(v.equals(findViewById(R.id.outsidebutton1))){
			if(e.getAction() == MotionEvent.ACTION_DOWN){
				((ImageButton) v).setImageResource(R.drawable.door_focused);
			}
			else if (e.getAction() == MotionEvent.ACTION_UP){
				((ImageButton) v).setImageResource(R.drawable.door);
			}
		}
		else{
			Log.v("InBuilding", "something touched");
			return gest.onTouchEvent(e);
		}
		return false;
	}
	
	@Override
    public boolean dispatchTouchEvent(MotionEvent e)
    {
        super.dispatchTouchEvent(e);
        return gest.onTouchEvent(e);
    }

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velx,
			float vely) {
		ImageView currDot;
		//if swipe up
		if((int)e1.getY() - (int)e2.getY() > 0)	{
			Log.v("InBuildingActivity", "flung up");
			if(currIndex == picNames.size() - 1){ return false; }
			currDot = (ImageView)findViewById(currIndex);
			currDot.setImageResource(R.drawable.circle);
			currDot.setTag(null);
			currIndex++;
			Animation in = AnimationUtils.loadAnimation(this,
				      R.anim.slide_up_in);
			Animation out = AnimationUtils.loadAnimation(this, R.anim.slide_up_out);
			animate(in, out);
		}
		
		//if swipe down
		else{
			Log.v("InBuildingActivity", "flung down");
			if(currIndex == 0) { return false; }
			currDot = (ImageView)findViewById(currIndex);
			currDot.setImageResource(R.drawable.circle);
			currDot.setTag(null);
			currIndex--;
			Animation in = AnimationUtils.loadAnimation(this,
				      R.anim.slide_down_in);
			Animation out = AnimationUtils.loadAnimation(this, R.anim.slide_down_out);
			animate(in, out);
		}
		currDot = (ImageView)findViewById(currIndex); 
		currDot.setImageResource(R.drawable.circle_chosen);
		currDot.setTag("highlighted");
		return true;
	}
	
	
	private void animate(Animation in, Animation out){
		ImageView v = (ImageView)imageSwitcher.getNextView(); 
		BitmapDrawable bd = (BitmapDrawable) v.getDrawable();
		if (bd != null) 
		{
			Log.v("InBuildingActivity", "recycling");
		    Bitmap b = bd.getBitmap();
		    b.recycle();
		}
		imageSwitcher.setInAnimation(in);
		imageSwitcher.setOutAnimation(out);
		currImageName = picNames.get(currIndex);
		int imageCode = getResources().getIdentifier(currImageName, "drawable", getPackageName());
		BitmapFactory.Options options=new BitmapFactory.Options();// Create object of bitmapfactory's option method for further option use
        options.inPurgeable = true; // inPurgeable is used to free up memory while required
        Bitmap image = BitmapFactory.decodeResource(getResources(), imageCode, options);
        Bitmap image1 = Bitmap.createScaledBitmap(image, image.getWidth(), image.getHeight(), true);
        BitmapDrawable nextImage = new BitmapDrawable(image1);
		imageSwitcher.setImageDrawable(nextImage);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	@Override
	public void onStop(){
		ImageView v = (ImageView)imageSwitcher.getCurrentView(); 
		BitmapDrawable bd = (BitmapDrawable) v.getDrawable();
		if (bd != null) 
		{
		    Bitmap b = bd.getBitmap();
		    b.recycle();
		}
		v = (ImageView)imageSwitcher.getNextView(); 
		bd = (BitmapDrawable) v.getDrawable();
		if (bd != null) 
		{
		    Bitmap b = bd.getBitmap();
		    b.recycle();
		}
		super.onStop();
	}
	
	@Override
	protected void onDestroy(){
		 super.onDestroy();
	     unbindDrawables(findViewById(R.id.inBuildingLayout));
	     System.gc();
	}
	private void unbindDrawables(View view)
	{
	        if (view.getBackground() != null)
	        {
	                view.getBackground().setCallback(null);
	        }
	        if (view instanceof ViewGroup && !(view instanceof AdapterView))
	        {
	                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
	                {
	                        unbindDrawables(((ViewGroup) view).getChildAt(i));
	                }
	                ((ViewGroup) view).removeAllViews();
	        }
	}
	
	//for testing
	public String getImageName(){
		return currImageName;
	}
}
