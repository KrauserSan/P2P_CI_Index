package com.csc573.p2p;

import java.util.ArrayList;
import java.util.HashMap;
import java.lang.StringBuilder;
import java.util.*;
import java.text.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.io.BufferedReader;

public class P2PResponse{
	int statusCode;
	String rfcNumber;
	static String version = "P2P-CI/1.0";
	ArrayList<P2S> responseLines;
	static final Map<Integer, String> VERSION_CODES = new HashMap<Integer, String>();
	private String fileToSend; 
	static{
		VERSION_CODES.put(200, "OK");
		VERSION_CODES.put(400, "Bad Request");
		VERSION_CODES.put(404, "Not Found");
		VERSION_CODES.put(505, "P2P-CI Version Not Supported");
	}
	public P2PResponse(int statusCode) {
		super();
		this.statusCode = statusCode;

	}
	public P2PResponse(String rfcNumber){
		this.rfcNumber = rfcNumber;
		fileToSend = Paths.get(".").toAbsolutePath().normalize().toString() + "/rfc" + rfcNumber+ ".txt";
		this.statusCode = 200;
	}
	public P2PResponse() {
		// TODO Auto-generated constructor stub
	}
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	
	
	public String lastModified(File file){
		Date d = new Date(file.lastModified());
		return new SimpleDateFormat("E',' dd MMM yyyy HH:mm:ss zzz").format(d);
	}
	
	@Override
	public String toString(){
		try{
			Date date = new Date();
			SimpleDateFormat currentDate= new SimpleDateFormat("E',' dd MMM yyyy HH:mm:ss zzz"); 
			StringBuilder s = new StringBuilder();
			s.append(version + " " + Integer.toString(statusCode) + " " + VERSION_CODES.get(statusCode) + "\r\n");
			s.append("Date: " + (currentDate.format(date)) +"\r\n");
			s.append("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version")+"\r\n");
			if(statusCode!=200){
				s.append("\r\n");
				return s.toString();
			}
			File file = new File(fileToSend);
			byte[] buffer = new byte[(int)file.length()];
			FileInputStream inputStream = new FileInputStream(file);
			inputStream.read(buffer);
			String content = new String(buffer);
			s.append("Last Modified: " + lastModified(file)+"\r\n");		//make this work
			s.append("Content-Length: " + (int)file.length()+"\r\n"); 
			s.append("Content-Type: text/plain \r\n"); 
			s.append("\r\n");
			s.append(content); // make this work
			s.append("\r\n");
			inputStream.close();
			return s.toString();
		}
	    catch(FileNotFoundException ex) {
	        System.out.println( "Unable to open file '" + fileToSend + "'");    
	        
	    }
	    catch(IOException ex) {
	        System.out.println( "Error reading file '" + fileToSend + "'");                  
	    }
		return new P2PResponse(400).toString();
	
}
	
	public static P2PResponse parseResponse(String response, int rfcNumber){
		P2PResponse pr = new P2PResponse();
		String fileToWrite = Paths.get(".").toAbsolutePath().normalize().toString()+"/rfc"+rfcNumber + ".txt";
		String lines[] = response.split("text/plain");
		String content = lines[1];
		BufferedWriter bw = null;
		FileWriter fw = null;
		try{
			fw = new FileWriter(fileToWrite);
			bw = new BufferedWriter(fw);
			bw.write(content);
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
		}
		return pr;

	}
}
