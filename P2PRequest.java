package com.csc573.p2p;

public class P2PRequest {
	String method = "GET";
	String version = "P2P-CI/1.0";
	String hostname;
	String OS;
	String RFC;
	
	public String getRFC() {
		return RFC;
	}
	public void setRFC(String rFC) {
		RFC = rFC;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getOS() {
		return OS;
	}
	public void setOS(String oS) {
		OS = oS;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public P2PRequest(String hostname, String oS) {
		super();
		this.hostname = hostname;
		OS = oS;
	}
	public P2PRequest() {
		
	}
	public String toString(){
		/*GET RFC 1234 P2P-CI/1.0
		Host: somehost.csc.ncsu.edu
		OS: Mac OS 10.4.1 
		*/
		return method + " RFC " + RFC + " " + version + "\r\n" +
			   "Host: " + hostname + "\r\n" + 
			   "OS: "   + OS + "\r\n\r\n";
			   
	}
	public static P2PRequest parseRequest(String request) throws Exception{
		P2PRequest pr = new P2PRequest();
		String lines[] = request.split("\r\n");
		if(lines.length<3){
			throw new Exception("Bad Request");
		}
		String tokens[] = lines[0].split(" ");
		pr.setVersion(tokens[3]);
		
		if("GET".equals(tokens[0])){
			pr.setRFC(tokens[2]);
		}
		else{
			throw new Exception("Method not supported");
		}
		tokens = lines[1].split(":");
		pr.setHostname(tokens[1]);
		tokens = lines[2].split(":");
		pr.setOS(tokens[1]);
		return pr;
	}

}
