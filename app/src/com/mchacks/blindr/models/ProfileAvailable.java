package com.mchacks.blindr.models;

import java.sql.Timestamp;
import java.util.UUID;

public class ProfileAvailable extends Event{

	public ProfileAvailable(UUID id, Timestamp timestamp, IDestination destination, User user) {
		super(id, timestamp, destination, user);
	}

}
