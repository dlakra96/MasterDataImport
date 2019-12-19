package com.csvprocessor;

public class Location
{
	private String country_name;
	private String state_name;
	private String city_name;
	
	public String getState() {
		return state_name;
	}
	
	public void setState(String state) {
		this.state_name = state;
	}
	
	public String getCity() {
		return city_name;
	}
	
	public void setCity(String city) {
		this.city_name = city;
	}
	
	public String getCountry() {
		return country_name;
	}
	
	public void setCountry(String country) {
		this.country_name = country;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Country:- " + this.country_name + "; State:- " + this.state_name + "; City:- " + this.city_name;
	}
	
}