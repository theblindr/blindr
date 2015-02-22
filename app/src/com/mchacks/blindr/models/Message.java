package com.mchacks.blindr.models;

import java.sql.Timestamp;
import java.util.UUID;

public class Message extends Event{
	private String message;
	
	public Message(User user, String message){
		super(null, null, null, user);
		this.message = message;
	}
	
	public Message(UUID id, Timestamp timestamp, IDestination destination, User user, String message) {
		super(id, timestamp, destination, user);
	}
	
	public String getMessage(){
		return message;
	}
	
	public void addMessage(String newMessage){
		message = message + "\n" + newMessage;
	}

}
