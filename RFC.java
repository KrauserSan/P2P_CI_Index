package com.csc573.p2p;

public class RFC {
	String number;
	String title;
	String hostname;
	public String getNumber() {
		return number;
	}
	public RFC(String number, String title, String hostname) {
		super();
		this.number = number;
		this.title = title;
		this.hostname = hostname;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String toString() {
		return "RFC [number=" + number + ", title=" + title + ", hostname="
				+ hostname + "]";
	}
	
	
}
