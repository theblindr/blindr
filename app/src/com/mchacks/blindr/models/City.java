package com.mchacks.blindr.models;

public class City implements IDestination{

	private String name;
	
	public City(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public String getId(){
		return name;
	}
}
