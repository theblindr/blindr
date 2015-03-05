package com.lesgens.blindr.models;

import java.sql.Timestamp;
import java.util.UUID;

import com.lesgens.blindr.controllers.Controller;

public class Match extends Event{

	private Boolean isMutual;
	private String fakeName;
	
	public Match(UUID id, Timestamp timestamp, IDestination destination, User user) {
		this(id, timestamp, destination, user, false, null, null);
	}
		
	public Match(UUID id, Timestamp timestamp, IDestination destination, User user, Boolean isMutual, String realName, String fakeName) {
		super(id, timestamp, destination, user, realName, fakeName);
		this.isMutual = isMutual;
		this.fakeName = fakeName;
	}
	
	public User[] getMatchUsers() {
		return new User[]{this.getUser(), (User)this.getDestination()};
	}
	
	public User getMatchedUser(){
		if(!getUser().getId().equals(Controller.getInstance().getMyself().getId())){
			return getUser();
		}
		
		return (User) getDestination();
	}
	
	public Boolean isMutual() {
		return isMutual;
	}
	
	public String getFakeName(){
		return fakeName;
	}
	
}
