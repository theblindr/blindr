package com.mchacks.blindr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.mchacks.blindr.models.Event;
import com.mchacks.blindr.models.EventsListener;
import com.mchacks.blindr.models.FacebookProfileListener;
import com.mchacks.blindr.models.Match;
import com.mchacks.blindr.models.Message;
import com.mchacks.blindr.models.Server;
import com.mchacks.blindr.models.User;

public class PrivateChatActivity extends Activity implements OnClickListener, EventsListener, FacebookProfileListener {

	private Typeface tf;
	private ImageView sendBt;
	private ChatAdapter chatAdapter;
	private ListView listMessages;
	private EditText editText;
	private ScheduledExecutorService scheduler;
	private Future<?> future;
	private User remoteUser;

	public static void show(Context context, String tokenId){
		Intent i = new Intent(context, PrivateChatActivity.class);
		i.putExtra("tokenId", tokenId);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.private_chat);

		remoteUser = Controller.getInstance().getUser(getIntent().getStringExtra("tokenId"));
		TextView fbName = (TextView) findViewById(R.id.fbName);

		tf = Typeface.createFromAsset(getAssets(), "fonts/Raleway_Thin.otf");
		fbName.setTypeface(tf);
		fbName.setText(remoteUser.getName());

		((ImageView) findViewById(R.id.avatar)).setImageBitmap(remoteUser.getAvatar());

		editText = (EditText) findViewById(R.id.editText);
		editText.clearFocus();

		sendBt = (ImageView) findViewById(R.id.send);
		sendBt.setOnClickListener(this);

		chatAdapter = new ChatAdapter(this, new ArrayList<Message>());
		listMessages = (ListView) findViewById(R.id.list);
		listMessages.setAdapter(chatAdapter);
		listMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		listMessages.setStackFromBottom(true);

		Server.addEventsListener(this);

		scheduler = Executors.newSingleThreadScheduledExecutor();

	}

	@Override
	public void onResume(){
		super.onResume();
		if(scheduler != null){
			future = scheduler.scheduleAtFixedRate
					(new Runnable() {
						public void run() {
							Server.getEvents();
						}
					}, 0, 5, TimeUnit.SECONDS);
		}
	}

	@Override
	public void onPause(){
		super.onPause();
		if(future != null){
			future.cancel(true);
		}
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		if(scheduler != null){
			scheduler.shutdownNow();
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.send){
			final String text = editText.getText().toString();
			if(!text.isEmpty()){
				Message message = new Message(Controller.getInstance().getMyself(), text, false);
				chatAdapter.addMessage(message);
				chatAdapter.notifyDataSetChanged();
				Server.sendPrivateMessage(remoteUser, message.getMessage());
				editText.setText("");
				scrollMyListViewToTheBottomNowWeHere();
			}
		}
	}

	private void scrollMyListViewToTheBottomNowWeHere() {
		listMessages.post(new Runnable() {
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				listMessages.setSelection(chatAdapter.getCount() - 1);
			}
		});
	}

	@Override
	public void onEventsReceived(List<Event> events) {
		for(Event e : events){
			android.util.Log.i("Blindr", "New event=" + e);
			if(e instanceof Message && e.getDestination() instanceof User){
				if(((User) e.getDestination()).getId().equals(remoteUser.getId())){
					chatAdapter.addMessage((Message) e);
					chatAdapter.notifyDataSetChanged();
					scrollMyListViewToTheBottomNowWeHere();
				}
			}
		}

	}

	@Override
	public void onOldMatchesReceives(List<Match> matches) {
		Controller.getInstance().setMatches(matches);
	}

	@Override
	public void onUserHistoryReceived(List<Event> events) {
		for(Event e : events){
			android.util.Log.i("Blindr", "New event=" + e);
			if(e instanceof Message && e.getDestination() instanceof User){
				if(((User) e.getDestination()).getId().equals(remoteUser.getId())){
					chatAdapter.addMessage((Message) e);
					chatAdapter.notifyDataSetChanged();
					scrollMyListViewToTheBottomNowWeHere();
				}
			}
		}
	}

	@Override
	public void onProfilePicturesReceived(List<String> pictures) {
		// TODO Auto-generated method stub

	}
}
