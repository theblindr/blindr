package com.lesgens.blindr;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.lesgens.blindr.controllers.Controller;
import com.lesgens.blindr.models.City;
import com.lesgens.blindr.models.Event;
import com.lesgens.blindr.models.EventsListener;
import com.lesgens.blindr.models.Match;
import com.lesgens.blindr.models.MatchAdapter;
import com.lesgens.blindr.models.Message;
import com.lesgens.blindr.models.PublicChatAdapter;
import com.lesgens.blindr.models.Server;
import com.lesgens.blindr.models.User;
import com.lesgens.blindr.R;

public class PublicChatActivity extends Activity implements OnClickListener, EventsListener, OnItemClickListener, OnOpenListener {
	private Typeface tf;
	private ImageView sendBt;
	private PublicChatAdapter chatAdapter;
	private ListView listMessages;
	private EditText editText;
	private ImageView menuPrivate;
	private SlidingMenu slidingMenu;
	private ListView listPrivate;
	private MatchAdapter matchAdapter;
	private ScheduledExecutorService scheduler;
	private Future<?> future;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.public_chat_container);

		TextView city = (TextView) findViewById(R.id.city_name);

		tf = Typeface.createFromAsset(getAssets(), "fonts/Raleway_Thin.otf");
		city.setTypeface(tf);
		city.setText(Controller.getInstance().getCity().getId());
		editText = (EditText) findViewById(R.id.editText);
		editText.clearFocus();

		slidingMenu = (SlidingMenu) findViewById(R.id.slidingmenulayout);
		slidingMenu.setOnOpenListener(this);

		sendBt = (ImageView) findViewById(R.id.send);
		sendBt.setOnClickListener(this);

		menuPrivate = (ImageView) findViewById(R.id.menu_private);
		menuPrivate.setOnClickListener(this);

		chatAdapter = new PublicChatAdapter(this, new ArrayList<Message>());
		listMessages = (ListView) findViewById(R.id.list);
		listMessages.setAdapter(chatAdapter);
		listMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
		listMessages.setStackFromBottom(true);

		matchAdapter = new MatchAdapter(this, new ArrayList<Match>());
		listPrivate = (ListView) findViewById(R.id.list_private);
		listPrivate.setAdapter(matchAdapter);
		listPrivate.setOnItemClickListener(this);


		Server.addEventsListener(this);
		Server.getMatches();

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
				Message message = new Message(Controller.getInstance().getMyself(), text, false);
				chatAdapter.addMessage(message);
				chatAdapter.notifyDataSetChanged();
				Server.sendPublicMessage(Controller.getInstance().getCity(), message.getMessage());
				editText.setText("");
				scrollMyListViewToBottom();
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

	private void scrollMyListViewToBottom() {
		listMessages.post(new Runnable() {
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				listMessages.setSelection(chatAdapter.getCount() - 1);
			}
		});
	}


	private void hideKeyboard(){
		InputMethodManager imm = (InputMethodManager)getSystemService(
				Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	@Override
	public void onEventsReceived(List<Event> events) {
		boolean alreadyCalledNewMatches = false;
		for(Event e : events){
			if(e instanceof Message && e.getDestination() instanceof City){
				if(!Controller.getInstance().getBlockedPeople(this).contains(((Message) e).getUser().getId())){
					chatAdapter.addMessage((Message) e);
					chatAdapter.notifyDataSetChanged();
					scrollMyListViewToBottom();
				}
			} else if(e instanceof Match){
				if(!alreadyCalledNewMatches){
					Server.getMatches();
					alreadyCalledNewMatches = true;
				}
			}
		}

	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
		Match m = ((Match) adapter.getItemAtPosition(position));
		if(m.isMutual()){
			PrivateChatActivity.show(this, m.getMatchedUser().getId(), m.getRealName());
			slidingMenu.toggle(true);
		}

	}

	public void onOldMatchesReceives(List<Match> matches) {
		Controller.getInstance().setMatches(matches);
		matchAdapter.clear();

		for(Match match : Controller.getInstance().getMatches()){
			matchAdapter.add(match);
		}
		matchAdapter.notifyDataSetChanged();
	}

	@Override
	public void onUserHistoryReceived(List<Event> events) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onUserLiked(User user, String userFakeName) {
		Match match = new Match(UUID.randomUUID(), null, user, Controller.getInstance().getMyself(), false, null, userFakeName);
		matchAdapter.add(match);
		matchAdapter.notifyDataSetChanged();
	}

	@Override
	public void onOpen() {
		hideKeyboard();
	}
}
