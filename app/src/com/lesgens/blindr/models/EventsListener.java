package com.lesgens.blindr.models;

import java.util.List;

public interface EventsListener {
	
	public void onEventsReceived(List<Event> events);
	
	public void onOldMatchesReceives(List<Match> matches);
	
	public void onUserHistoryReceived(List<Event> events);
	
	public void onUserLiked(User user, String userFakeName);
	
}
