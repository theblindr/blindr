package com.mchacks.blindr.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import com.mchacks.blindr.models.User;

public class Controller {
	private String city;
	private HashMap<String, User> users;
	private User myselfUser;
	private ArrayList<String> privateChats;
	
	private static Controller controller;
	
	private Controller(){
		city = "";
		users = new HashMap<String, User>();
		privateChats = new ArrayList<String>();
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
	
	public void setMyOwnUser(User user){
		this.myselfUser = user;
	}
	
	public User getMyself(){
		return myselfUser;
	}
	
	public void setMutualChats(ArrayList<String> mutualChats){
		this.privateChats = mutualChats;
	}
	
	public ArrayList<String> getMutualChats(){
		return privateChats;
	}
}
