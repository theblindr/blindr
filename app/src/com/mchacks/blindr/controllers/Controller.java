package com.mchacks.blindr.controllers;

public class Controller {
	private String city;
	
	private static Controller controller;
	
	private Controller(){
		city = "";
	}
	
	public Controller getInstance(){
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
}
