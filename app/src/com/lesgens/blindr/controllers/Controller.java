package com.lesgens.blindr.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.checkin.avatargenerator.AvatarGenerator;
import com.facebook.Session;
import com.lesgens.blindr.models.City;
import com.lesgens.blindr.models.Match;
import com.lesgens.blindr.models.User;
import com.lesgens.blindr.utils.Utils;

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

	public String getMyId(){
		return myselfUser.getId().substring(0, myselfUser.getId().indexOf("."));
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

	public void setDimensionAvatar(Context context) {
		dimensionAvatar = Utils.dpInPixels(context, 50);
	}

	public int getDimensionAvatar() {
		return dimensionAvatar;
	}

	public void addBlockPerson(Activity activity, String id){
		SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		String blocked = getBlockedPeopleString(activity);
		if(blocked.isEmpty()){
			blocked = id;
		} else{
			blocked += "," + id;
		}
		editor.putString("blockedList", blocked);
		editor.commit();
	}

	private String getBlockedPeopleString(Activity activity){
		SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
		String blocked = sharedPref.getString("blockedList", "");

		return blocked;

	}

	public ArrayList<String> getBlockedPeople(Activity activity){
		SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
		String blocked = sharedPref.getString("blockedList", "");

		ArrayList<String> blockedPeople = new ArrayList<String>();
		for(String b : blocked.split(",")){
			blockedPeople.add(b);
		}

		return blockedPeople;

	}

	public boolean containsMatch(Match match) {
		for(Match m : matches){
			if(m.getId() != null){
				if(m.getId().equals(match.getId())){
					return true;
				}
			}
		}
		return false;
	}

	public Match removeOldIfPendingMatch(Match match) {
		for(Match m : matches){
			if(!m.isMutual() && m.getMatchedUser().getId().equals(match.getMatchedUser().getId())){
				matches.remove(m);
				return m;
			}
		}
		return null;
	}

	public boolean checkIfMutualWith(String fakeName){
		if(fakeName != null){
			for(Match m : matches){
				if(m.isMutual() && fakeName.equals(m.getFakeName())){
					return true;
				}
			}
		}
		return false;
	}

}
