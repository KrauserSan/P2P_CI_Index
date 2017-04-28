package com.csc573.p2p;

public class P2SRequest extends P2S {
	

	public P2SRequest(String hostname, int port) {
		super(hostname, port);
	}
	public P2SRequest() {
		// TODO Auto-generated constructor stub
		super();
		this.setMethod("ADD");
	}
	public String constructAddRequest(String RFC, String title){
		this.setMethod("ADD");
		this.setTitle(title);
		this.setNumber(RFC);
		return toString();
	}
	public String constructListRequest(){
		this.setMethod("LIST ALL");
		return toString();
		
	}
	public String constructLookupRequest(String RFC, String title){
		/*LOOKUP RFC 3457 P2P-CI/1.0
		  Host: thishost.csc.ncsu.edu
		  Port: 5678
		  Title: Requirements for IPsec Remote Access Scenarios  
		 */
		this.setMethod("LOOKUP");
		this.setTitle(title);
		this.setNumber(RFC);
		return toString();
	}
	
	public String toString() {
		/*ADD RFC 123 P2P-CI/1.0
		Host: thishost.csc.ncsu.edu
		Port: 5678
		Title: A Proferred Official ICP*/
		if(this.getMethod().equals("LIST ALL")){
			return method + " " + version + "\r\n" + "Host: " + hostname + "\r\nPort: " + new Integer(port).toString() + "\r\n\r\n";
		}
		return method + " RFC " + number + " " + version
		+ "\r\n" + "Host: " + hostname + "\r\nPort: " + new Integer(port).toString() + 
		"\r\n" + "Title: " + title + "\r\n\r\n";
	}
	
	public static P2SRequest parseRequest(String request) throws Exception { 
		P2SRequest pr = new P2SRequest();
		String lines[] = request.split("\r\n");
		if(lines.length >= 1 ){
			String tokens[] = lines[0].split(" ");
			pr.setMethod(tokens[0]);
			if("RFC".equals(tokens[1])){
				pr.setNumber(tokens[2]);
				pr.setVersion(tokens[3]);
			}
			if("ALL".equals(tokens[1])){
				pr.setMethod(tokens[0] + " " + "ALL");
				pr.setVersion(tokens[2]);
			}
			if(lines.length >= 2){
				tokens = lines[1].split(" ");
				if("Host:".equals(tokens[0])){
					pr.setHostname(tokens[1]);
				}
				if(lines.length>=3){
					tokens = lines[2].split(" ");
					if("Port:".equals(tokens[0])){
						pr.setPort(Integer.parseInt(tokens[1]));
					}
					if(lines.length >=4){ //TODO: Error in this code - assumes Title is without spaces
						tokens = lines[3].split(": ");
						if("Title".equals(tokens[0])){
							pr.setTitle(tokens[1]);
						}
					}
				}
			}
		}
		return pr;
		
	}
	
}
