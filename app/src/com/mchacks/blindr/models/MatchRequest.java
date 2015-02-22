package com.mchacks.blindr.models;

public class MatchRequest {

	private User sender;
	private User liked;
	
	public MatchRequest(User sender, User liked) {
		this.sender = sender;
		this.liked = liked;
	}
	
	public User getLikedUser(){
		return liked;
	}
	
	public User getSender(){
		return sender;
	}
}
