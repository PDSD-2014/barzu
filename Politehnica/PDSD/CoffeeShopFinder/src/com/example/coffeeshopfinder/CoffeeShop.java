package com.example.coffeeshopfinder;

public class CoffeeShop {
	private String name;
	private String address;
	private String distance;
	
	public CoffeeShop(String name, String address, String distance) {
		this.name = name;
		this.address = address;
		this.distance = distance;
	}
	
	public String GetName() {
		return this.name;
	}
	
	public String GetAddress() {
		return this.address;
	}
	
	public String GetDistance() {
		return this.distance;
	}
}
