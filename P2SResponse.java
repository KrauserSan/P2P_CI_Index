package com.csc573.p2p;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.StringBuilder;
import java.util.Map;



public class P2SResponse{
	int statusCode;
	static String version = "P2P-CI/1.0";
	ArrayList<P2S> responseLines;
	static final Map<Integer, String> VERSION_CODES = new HashMap<Integer, String>();
	static{
		VERSION_CODES.put(200, "OK");
		VERSION_CODES.put(400, "Bad Request");
		VERSION_CODES.put(404, "Not Found");
		VERSION_CODES.put(505, "P2P-CI Version Not Supported");
	}
	public P2SResponse(int statusCode) {
		super();
		this.statusCode = statusCode;
		responseLines = new ArrayList<P2S>();

	}
	public P2SResponse() {
		responseLines = new ArrayList<P2S>();
		// TODO Auto-generated constructor stub
	}
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public void addResponse(P2S obj){
		responseLines.add(obj);
	}
	@Override
	public String toString(){
		/*version <sp> status code <sp> phrase <cr> <lf>
		<cr> <lf>
		RFC number <sp> RFC title <sp> hostname <sp> upload port number<cr><lf>
		RFC number <sp> RFC title <sp> hostname <sp> upload port number<cr><lf>
		...
		<cr><lf>
		*/
		StringBuilder s = new StringBuilder();
		s.append(version + " " + Integer.toString(statusCode) + " " + VERSION_CODES.get(statusCode) + "\r\n");
		for(P2S pr: responseLines){
			s.append(pr.toString());
			s.append("\r\n");
		}
		s.append("\r\n");
		return s.toString();
	}
	
	public static P2SResponse parseResponse(String response){
		P2SResponse pr = new P2SResponse();
		String lines[] = response.split("\r\n");
		String tokens[] = lines[0].split(" ");
		if(version.equals(tokens[0])){
			pr.setStatusCode(Integer.parseInt(tokens[1]));
		}
		for(int i = 1;i<lines.length;i++){
			tokens = lines[i].split(" ");
			P2S p = new P2S(tokens[tokens.length - 2],Integer.parseInt(tokens[tokens.length - 1]));
			p.setNumber(tokens[1]);
			StringBuilder s = new StringBuilder();
			for(int j = 2; j<tokens.length-2;j++){
				s.append(tokens[j] + " ");
			}
			s.deleteCharAt(s.length()-1);
			p.setTitle(s.toString());
			pr.addResponse(p);
		}
		return pr;
	}
	
}
