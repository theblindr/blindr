package com.mchacks.blindr.models;

import java.sql.Timestamp;
import java.util.UUID;

public class Match extends Event{

	public Match(UUID id, Timestamp timestamp, IDestination destination, User user) {
		super(id, timestamp, destination, user);
	}
	
	public User[] getMatchUsers() {
		return new User[]{this.getUser(), (User)this.getDestination()};
	}
	
}
