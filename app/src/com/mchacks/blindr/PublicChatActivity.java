package com.mchacks.blindr;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mchacks.blindr.controllers.Controller;
import com.mchacks.blindr.models.ChatAdapter;
import com.mchacks.blindr.models.Message;
import com.mchacks.blindr.models.Server;

public class PublicChatActivity extends Activity implements OnClickListener {
	private Typeface tf;
	private ImageView sendBt;
	private ChatAdapter chatAdapter;
	private ListView listView;
	private EditText editText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.public_chat);

		TextView city = (TextView) findViewById(R.id.city_name);

		tf = Typeface.createFromAsset(getAssets(), "fonts/Raleway_Thin.otf");
		city.setTypeface(tf);
		city.setText(Controller.getInstance().getCity().getId());
		
		editText = (EditText) findViewById(R.id.editText);
		
		sendBt = (ImageView) findViewById(R.id.send);
		sendBt.setOnClickListener(this);
		
		chatAdapter = new ChatAdapter(this, new ArrayList<Message>());
		listView = (ListView) findViewById(R.id.list);
		
		listView.setAdapter(chatAdapter);
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.send){
			final String text = editText.getText().toString();
			if(!text.isEmpty()){
				Message message = new Message(Controller.getInstance().getMyself(), text);
				chatAdapter.addMessage(message);
				chatAdapter.notifyDataSetChanged();
				Server.sendPublicMessage(Controller.getInstance().getCity(), message.getMessage());
				editText.setText("");
			}
		}
	}
	
	
}
