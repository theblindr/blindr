package com.mchacks.blindr.models;

import java.util.List;

public interface EventsListener {
	
	public void onEventsReceived(List<Event> events);
	
}