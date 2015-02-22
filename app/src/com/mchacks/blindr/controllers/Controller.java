package com.mchacks.blindr.controllers;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.TypedValue;

import com.checkin.avatargenerator.AvatarGenerator;
import com.facebook.Session;
import com.mchacks.blindr.models.User;

public class Controller {
	private String city;
	private HashMap<String, User> users;
	private Session session;
	private User myselfUser;
	private ArrayList<String> privateChats;
	private int dimensionAvatar;
	
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
		users.put(user.getId(), user);
	}
	
	public User getUser(String tokenId){
		if(users.get(tokenId) == null){
			users.put(tokenId, new User("user" + tokenId, AvatarGenerator.generate(dimensionAvatar, dimensionAvatar), tokenId));
		}
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
	
	public void setMutualChats(ArrayList<String> mutualChats){
		this.privateChats = mutualChats;
	}
	
	public ArrayList<String> getMutualChats(){
		return privateChats;
	}
	
	private static int dpInPixels(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources()
		.getDisplayMetrics());
	}
	
	public void setDimensionAvatar(Context context) {
		dimensionAvatar = dpInPixels(context, 50);
	}
	
	public int getDimensionAvatar() {
		return dimensionAvatar;
	}
	
}
