package com.csc573.p2p;

public class P2S {
	String version;
	String hostname;
	int port;
	String title;
	String method;
	String number;
	
	public P2S(){
		this.hostname = "localhost";
		this.port = 7734;
		this.version = "P2P-CI/1.0";
	}
	@Override
	public String toString() {
		return "RFC " + getNumber() + " " + getTitle() + " " + getHostname() + " " + getPort();
	}
	public P2S(String hostname, int port) {
		super();
		this.hostname = hostname;
		this.port = port;
		this.version ="P2P-CI/1.0";
	}
	
	public P2S(String hostname, int port, String title, String number) {
		super();
		this.hostname = hostname;
		this.port = port;
		this.title = title;
		this.number = number;
		this.version = "P2P-CI/1.0";
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getNumber() {
		return number;
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
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
}
