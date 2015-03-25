package com.lesgens.blindr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lesgens.blindr.adapters.PrivateChatAdapter;
import com.lesgens.blindr.controllers.Controller;
import com.lesgens.blindr.listeners.EventsListener;
import com.lesgens.blindr.models.Event;
import com.lesgens.blindr.models.Match;
import com.lesgens.blindr.models.Message;
import com.lesgens.blindr.models.User;
import com.lesgens.blindr.network.Server;
import com.lesgens.blindr.receivers.NetworkStateReceiver;
import com.lesgens.blindr.receivers.NetworkStateReceiver.NetworkStateReceiverListener;

public class PrivateChatActivity extends Activity implements OnClickListener, EventsListener, NetworkStateReceiverListener {

	private Typeface tf;
	private ImageView sendBt;
	private PrivateChatAdapter chatAdapter;
	private StickyListHeadersListView listMessages;
	private EditText editText;
	private ScheduledExecutorService scheduler;
	private Future<?> future;
	private User remoteUser;
	private TextView tvConnectionProblem;
	private NetworkStateReceiver networkStateReceiver;

	public static void show(Context context, String tokenId, String realName){
		Intent i = new Intent(context, PrivateChatActivity.class);
		i.putExtra("tokenId", tokenId);
		i.putExtra("realName", realName);
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
		fbName.setText(getIntent().getStringExtra("realName"));

		((ImageView) findViewById(R.id.avatar)).setImageBitmap(remoteUser.getAvatar());
		findViewById(R.id.avatar).setOnClickListener(this);
		
		findViewById(R.id.photos).setOnClickListener(this);

		editText = (EditText) findViewById(R.id.editText);
		editText.clearFocus();

		sendBt = (ImageView) findViewById(R.id.send);
		sendBt.setOnClickListener(this);

		chatAdapter = new PrivateChatAdapter(this, new ArrayList<Message>());
		listMessages = (StickyListHeadersListView) findViewById(R.id.list);
		listMessages.setAdapter(chatAdapter);
		listMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

		Server.addEventsListener(this);
		scheduler = Executors.newSingleThreadScheduledExecutor();

	}

	@Override
	public void onResume(){
		super.onResume();
		Server.getUserEvents(remoteUser);
		if(scheduler != null){
			future = scheduler.scheduleAtFixedRate
					(new Runnable() {
						public void run() {
							Server.getEvents();
						}
					}, 0, 5, TimeUnit.SECONDS);
		}
		
		networkStateReceiver.addListener(this);
	    this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
	}

	@Override
	public void onPause(){
		super.onPause();
		if(future != null){
			future.cancel(true);
		}
		
		networkStateReceiver.removeListener(this);
	    this.unregisterReceiver(networkStateReceiver);
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
		} else if(v.getId() == R.id.avatar){
			onBackPressed();
		} else if(v.getId() == R.id.photos){
			SlideshowActivity.show(this, remoteUser.getId());
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
				if(((User) e.getDestination()).getId().equals(Controller.getInstance().getMyId())){
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
		chatAdapter.clear();
		Collections.sort(events, dateComparator);
		for(Event e : events){
			if(e instanceof Message && e.getDestination() instanceof User){
				if((e.getUser()).getId().equals(Controller.getInstance().getMyId())){
					((Message) e).setIsIncoming(false);
				}
				chatAdapter.addMessage((Message) e);
				chatAdapter.notifyDataSetChanged();
				scrollMyListViewToTheBottomNowWeHere();
			}
		}
	}

	@Override
	public void onUserLiked(User user, String userFakeName) {
		// TODO Auto-generated method stub
		
	}
	
	Comparator<Event> dateComparator = new Comparator<Event>()
			{
			    @Override
			    public int compare(Event lhs, Event rhs)
			    {
			        try
			        {
			            return lhs.getTimestamp().compareTo(rhs.getTimestamp());
			        }
			        catch (Exception e)
			        {
			            return 0;
			        }
			    }
			};

	@Override
	public void onNetworkAvailable() {
		tvConnectionProblem.setVisibility(View.GONE);
		sendBt.setEnabled(true);
	}

	@Override
	public void onNetworkUnavailable() {
		tvConnectionProblem.setVisibility(View.VISIBLE);
		sendBt.setEnabled(false);
	}
}
