package com.mchacks.blindr.models;

public class Message {
	private User user;
	private String message;
	
	public Message(User user, String message){
		this.user = user;
		this.message = message;
	}
	
	public String getMessage(){
		return message;
	}
	
	public User getUser(){
		return user;
	}
	
	public void addMessage(String newMessage){
		message = message + "\n" + newMessage;
	}

}
