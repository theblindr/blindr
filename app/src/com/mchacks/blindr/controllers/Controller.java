package com.mchacks.blindr.controllers;

import java.util.HashMap;

import com.facebook.Session;
import com.mchacks.blindr.models.User;

public class Controller {
	private String city;
	private HashMap<String, User> users;
	private Session session;
	private User myselfUser;
	
	private static Controller controller;

	private Controller(){
		city = "";
		users = new HashMap<String, User>();
	}
	
	public static Controller getInstance(){
		if(controller == null){
			controller = new Controller();
		}
		
		return controller;
	}
	
	public void setCity(String city){
		this.city = city;
	}
	
	public String getCity(){
		return city;
	}
	
	public void addUser(User user){
		users.put(user.getTokenId(), user);
	}
	
	public User getUser(String tokenId){
		return users.get(tokenId);
	}
	
	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	public void setMyOwnUser(User user){
		this.myselfUser = user;
	}
	
	public User getMyself(){
		return myselfUser;
	}
}
