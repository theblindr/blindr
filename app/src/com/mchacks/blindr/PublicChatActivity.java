package com.mchacks.blindr;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.checkin.avatargenerator.AvatarGenerator;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.mchacks.blindr.controllers.Controller;
import com.mchacks.blindr.models.ChatAdapter;
import com.mchacks.blindr.models.Message;
import com.mchacks.blindr.models.PrivateChatAdapter;
import com.mchacks.blindr.models.User;

public class PublicChatActivity extends Activity implements OnClickListener {
	private Typeface tf;
	private ImageView sendBt;
	private ChatAdapter chatAdapter;
	private ListView listMessages;
	private EditText editText;
	private ImageView menuPrivate;
	private SlidingMenu slidingMenu;
	private ListView listPrivate;
	private PrivateChatAdapter privateChatAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.public_chat_container);

		TextView city = (TextView) findViewById(R.id.city_name);

		tf = Typeface.createFromAsset(getAssets(), "fonts/Raleway_Thin.otf");
		city.setTypeface(tf);
		city.setText(Controller.getInstance().getCity());

		editText = (EditText) findViewById(R.id.editText);
		editText.clearFocus();

		slidingMenu = (SlidingMenu) findViewById(R.id.slidingmenulayout);

		sendBt = (ImageView) findViewById(R.id.send);
		sendBt.setOnClickListener(this);

		menuPrivate = (ImageView) findViewById(R.id.menu_private);
		menuPrivate.setOnClickListener(this);

		chatAdapter = new ChatAdapter(this, new ArrayList<Message>());
		listMessages = (ListView) findViewById(R.id.list);
		listMessages.setAdapter(chatAdapter);
		User user1 = new User("Triz", AvatarGenerator.generate(this, 50, 50), "auth1");
		User user2 = new User("Geisha", AvatarGenerator.generate(this, 50, 50), "auth2");
		chatAdapter.addMessage(new Message(user1, "Allo"));
		chatAdapter.addMessage(new Message(user1, "Ca va?"));
		chatAdapter.addMessage(new Message(user2, "Allo"));
		chatAdapter.addMessage(new Message(user1, "Tu fais quoi?"));
		chatAdapter.notifyDataSetChanged();

		ArrayList<String> names = new ArrayList<String>();
		names.add("Geisha");
		names.add("Triz maker");
		privateChatAdapter = new PrivateChatAdapter(this, names);
		listPrivate = (ListView) findViewById(R.id.list_private);
		listPrivate.setAdapter(privateChatAdapter);

	}

	@Override
	public void onBackPressed(){
		if(slidingMenu.isMenuShowing()){
			slidingMenu.toggle(true);
		} else if(chatAdapter != null && chatAdapter.isOneOpened()) {
			chatAdapter.closeAllSwipeLayout();
		} else{
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.send){
			final String text = editText.getText().toString();
			if(!text.isEmpty()){
				Message message = new Message(Controller.getInstance().getMyself(), text);
				chatAdapter.addMessage(message);
				chatAdapter.notifyDataSetChanged();
				editText.setText("");
			}
		} else if(v.getId() == R.id.menu_private){
			hideKeyboard();
			new Handler(getMainLooper()).postDelayed(new Runnable(){

				@Override
				public void run() {
					slidingMenu.toggle(true);
				}}, 200);

		}
	}


	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

}
