package com.github.franzmedia.LoyaltyPoints;



public class Milestone {
private String name;
private int type;
private int amount;
private int annonce;
private String annonce_text;
	
	public Milestone(int amount, int type, String name){
		this.name = name;
		this.type = type;
		this.amount = amount;
		annonce = 0;
		annonce_text = "";
		
	}
	
	public Milestone(int amount, int type, String name, String annonce_text){
		this.name = name;
		this.type = type;
		this.amount = amount;
		annonce = 1;
		this.annonce_text = annonce_text;
		
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getAnnonce() {
		return annonce;
	}

	public void setAnnonce(int annonce) {
		this.annonce = annonce;
	}

	public String getAnnonce_text() {
		return annonce_text;
	}

	public void setAnnonce_text(String annonce_text) {
		this.annonce_text = annonce_text;
	}
	
}
