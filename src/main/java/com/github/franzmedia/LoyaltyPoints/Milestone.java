package com.github.franzmedia.LoyaltyPoints;

public class Milestone {
	private String name;
	private int type;
	private int amount;
	private int annonce;
	private String annonce_text;

	public Milestone(final int amount, final int type, final String name) {
		this.name = name;
		this.type = type;
		this.amount = amount;
		annonce = 0;
		annonce_text = "";

	}

	public Milestone(final int amount, final int type, final String name,
			final String annonce_text) {
		this.name = name;
		this.type = type;
		this.amount = amount;
		annonce = 1;
		this.annonce_text = annonce_text;

	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(final int type) {
		this.type = type;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
	}

	public int getAnnonce() {
		return annonce;
	}

	public void setAnnonce(final int annonce) {
		this.annonce = annonce;
	}

	public String getAnnonce_text() {
		return annonce_text;
	}

	public void setAnnonce_text(final String annonce_text) {
		this.annonce_text = annonce_text;
	}

}
