package com.mchacks.blindr;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.checkin.avatargenerator.AvatarGenerator;
import com.mchacks.blindr.controllers.Controller;
import com.mchacks.blindr.models.User;

public class PublicChatActivity extends Activity implements OnClickListener {
	private Typeface tf;
	private ImageView sendBt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.public_chat);

		TextView city = (TextView) findViewById(R.id.city_name);

		tf = Typeface.createFromAsset(getAssets(), "fonts/Raleway_Thin.otf");
		city.setTypeface(tf);
		city.setText(Controller.getInstance().getCity());
		
		
		User user = new User("Stroboscope", AvatarGenerator.generate(this, 50, 50), "vagin");
		Controller.getInstance().addUser(user);
		
		
		sendBt = (ImageView) findViewById(R.id.send);
		sendBt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.send){
			
		}
	}
}
