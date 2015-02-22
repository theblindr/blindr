package com.mchacks.blindr.models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.UUID;

public class Message extends Event{
	private String message;
	private ArrayList<UUID> idsMessage;
	private boolean isIncoming;

	public Message(User user, String message, boolean isIncoming){
		super(null, null, null, user);
		this.message = message;
		this.isIncoming = isIncoming;
		idsMessage = new ArrayList<UUID>();
	}

	public Message(UUID id, Timestamp timestamp, IDestination destination, User user, String message, boolean isIncoming) {
		super(id, timestamp, destination, user);
		this.message = message;
		this.isIncoming = isIncoming;
		idsMessage = new ArrayList<UUID>();
		idsMessage.add(id);
	}
	
	public boolean isIncoming(){
		return isIncoming;
	}

	public String getMessage(){
		return message;
	}

	public void addMessage(String newMessage, UUID id){
		message = message + "\n" + newMessage;
		idsMessage.add(id);
	}

	public ArrayList<UUID> getIdsMessage(){
		return idsMessage;
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof Message){
			Message other = (Message) o;
			if(idsMessage.contains(other.getId())){
				return true;
			}
		}

		return false;
	}



}
