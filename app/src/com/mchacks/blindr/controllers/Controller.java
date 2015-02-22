package com.mchacks.blindr.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.util.TypedValue;

import com.checkin.avatargenerator.AvatarGenerator;
import com.facebook.Session;
import com.mchacks.blindr.models.City;
import com.mchacks.blindr.models.Match;
import com.mchacks.blindr.models.User;

public class Controller {
	private City city;
	private HashMap<String, User> users;
	private Session session;
	private User myselfUser;
	private ArrayList<Match> matches;
	private int dimensionAvatar;
	
	private static Controller controller;

	private Controller(){
		city = new City("");
		users = new HashMap<String, User>();
		matches = new ArrayList<Match>();
	}
	
	public static Controller getInstance(){
		if(controller == null){
			controller = new Controller();
		}
		
		return controller;
	}
	
	public void setCity(City city){
		this.city = city;
	}
	
	public City getCity(){
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
	
	public void setMatches(List<Match> matches){
		this.matches.clear();
		this.matches.addAll(matches);
	}
	
	public void addMatch(Match match){
		this.matches.add(match);
	}
	
	public ArrayList<Match> getMatches(){
		return matches;
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
