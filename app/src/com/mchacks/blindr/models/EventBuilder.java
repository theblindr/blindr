package com.mchacks.blindr.models;

import java.sql.Timestamp;
import java.util.UUID;

import com.mchacks.blindr.controllers.Controller;
import com.mchacks.blindr.models.Message.Gender;

public class EventBuilder {
	
	public static Event buildEvent(UUID id, String type, String destination, Timestamp timestamp, String userId, String message, String genderStr, String fakeName, String realName){
		
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
			Gender gender = Gender.Custom;
			if(genderStr != null) {
				if(genderStr.toLowerCase().equals("m"))
					gender = Gender.Male;
				else if(genderStr.toLowerCase().equals("f"))
					gender = Gender.Female;
			}
			return new Message(id, timestamp, destinationObj, Controller.getInstance().getUser(userId), message, fakeName, gender, true, realName);
		} else if(type.equals("match")) {
			if (destinationObj instanceof User)
				return new Match(id, timestamp, destinationObj, Controller.getInstance().getUser(userId), false, realName);
			
		}
		
		return null;
	}

}
