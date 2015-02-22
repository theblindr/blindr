package com.mchacks.blindr.models;

import java.sql.Timestamp;
import java.util.UUID;

import com.mchacks.blindr.controllers.Controller;

public class Match extends Event{

	private Boolean isMutual;
	
	public Match(UUID id, Timestamp timestamp, IDestination destination, User user) {
		this(id, timestamp, destination, user, false);
	}
		
	public Match(UUID id, Timestamp timestamp, IDestination destination, User user, Boolean isMutual) {
		super(id, timestamp, destination, user);
		this.isMutual = isMutual;
	}
	
	public User[] getMatchUsers() {
		return new User[]{this.getUser(), (User)this.getDestination()};
	}
	
	public User getMatchedUser(){
		if(getUser().getId().equals(Controller.getInstance().getMyself().getId())){
			return getUser();
		}
		
		return (User) getDestination();
	}
	
	public Boolean isMutual() {
		return isMutual;
	}
	
}
