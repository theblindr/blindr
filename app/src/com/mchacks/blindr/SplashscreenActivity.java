package com.mchacks.blindr;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class SplashscreenActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 //Remove title bar
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);

	    //Remove notification bar
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
		setContentView(R.layout.activity_main);
		
		TextView tv = (TextView) findViewById(R.id.splash_text);
		
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Raleway_Thin.otf");
		tv.setTypeface(tf);
	}
}
