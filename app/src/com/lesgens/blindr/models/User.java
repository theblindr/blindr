package com.lesgens.blindr.models;

import android.graphics.Bitmap;

public class User implements IDestination{
	private Bitmap avatar;
	private String name;
	private String tokenId;
	
	public User(String name, Bitmap avatar, String tokenId){
		this.name = name;
		this.avatar = avatar;
		this.tokenId = tokenId;
	}
	
	public Bitmap getAvatar(){
		return avatar;
	}
	
	public String getName(){
		return name;
	}

	public String getId() {
		return tokenId;
	}
}
