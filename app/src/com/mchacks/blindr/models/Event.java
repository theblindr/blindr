package com.mchacks.blindr.models;

import java.sql.Timestamp;
import java.util.UUID;

public abstract class Event {
	
	
	private UUID id;
	private Timestamp timestamp;
	private IDestination destination;
	private User sourceUser;
	private String realName;
	
	public Event(UUID id, Timestamp timestamp, IDestination destination, User user, String realName) {
		this.id = id;
		this.timestamp = timestamp;
		this.destination = destination;
		this.sourceUser = user;
		this.realName = realName;
	}
	
	public UUID getId(){
		return id;
	}
	
	public Timestamp getTimestamp(){
		return timestamp;
	}
	
	public IDestination getDestination(){
		return destination;
	}
	
	public User getUser(){
		return sourceUser;
	}
	
	public String getRealName() {
		return realName;
	}
	
}
