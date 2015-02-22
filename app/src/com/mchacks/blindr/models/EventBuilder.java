package com.mchacks.blindr.models;

import java.sql.Timestamp;
import java.util.UUID;

import com.mchacks.blindr.controllers.Controller;

public class EventBuilder {
	
	public static Event buildEvent(UUID id, String type, String destination, Timestamp timestamp, String userId, String message){
		
		IDestination destinationObj;
		String[] parts = destination.split(":");
		if (parts[0].equals("user")) {
			destinationObj = Controller.getInstance().getUser(parts[1]);
		} else if(destination.startsWith("city")) {
			destinationObj = new City(parts[1]);
		}
		else {
			return null;
		}
		
		if(type.equals("message")) {
			return new Message(id, timestamp, destinationObj, Controller.getInstance().getUser(userId), message, true);
		} else if(type.equals("match")) {
			if (destinationObj instanceof User)
				return new Match(id, timestamp, destinationObj, Controller.getInstance().getUser(userId));
			
		} else if(type.equals("profile_available")) {
			if (destinationObj instanceof User)
				return new Match(id, timestamp, destinationObj, Controller.getInstance().getUser(userId));
		}
		
		return null;
	}

}
