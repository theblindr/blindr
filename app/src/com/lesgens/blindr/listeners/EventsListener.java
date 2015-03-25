package com.lesgens.blindr.listeners;

import java.util.List;

import com.lesgens.blindr.models.Event;
import com.lesgens.blindr.models.Match;
import com.lesgens.blindr.models.User;

public interface EventsListener {
	
	public void onEventsReceived(List<Event> events);
	
	public void onOldMatchesReceives(List<Match> matches);
	
	public void onUserHistoryReceived(List<Event> events);
	
	public void onUserLiked(User user, String userFakeName);
	
}
