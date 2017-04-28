package com.csc573.p2p;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.net.*;

import com.csc573.p2p.Server.ServerThread;

public class Client implements Runnable{
	String hostname;
	int port;
	Socket sock;
	ServerSocket uploadSocket;
	private final ExecutorService pool; //initializing the pool for multithread processes
	private final int poolSize = 2; 	// setting the pool size for the number of threads
	Map <String,ArrayList<P2S>> cache = new HashMap<String,ArrayList<P2S>>();
	
	
	public Client(String hostname, int port){
		this.hostname = hostname;
		this.port = port;
		try {
			sock = new Socket("localhost", 7734);
			uploadSocket = new ServerSocket(port);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pool = Executors.newFixedThreadPool(poolSize);
		initializeUploadServer();
	}
	
	
	public Client(String hostname, int port, ServerSocket uploadSocket) {
		super();
		this.hostname = hostname;
		this.port = port;
		this.uploadSocket = uploadSocket;
		pool = Executors.newFixedThreadPool(100);
	}


/* Generic method to send the request and return back the response */
	public String sendP2SRequest(String request){
		
		try {
            	PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            	//System.out.println(request);
            	//out.println(hostname);
            	out.println(request);
            	BufferedReader input = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            	int r;
				StringBuilder s = new StringBuilder();
				while((r = input.read())!=-1){
					s.append((char)r);
					if(s.toString().endsWith("\r\n\r\n")){
						break;
					}
				}
				String response = s.toString();
				System.out.println(response);
				return response;
				//TODO- write code for the P2P protocol here
            }
         catch (IOException e) {
			e.printStackTrace();
			return "";
		} 
	    
	}
	
	public void add(String RFC, String title){
		P2SRequest pr = new P2SRequest(hostname,port);
		String request = pr.constructAddRequest(RFC, title);
		sendP2SRequest(request);
	}
	
	public void lookup(String RFC, String title){
		P2SRequest pr = new P2SRequest(hostname,port);
		String request = pr.constructLookupRequest(RFC, title);
		String response = sendP2SRequest(request);
		P2SResponse pres = P2SResponse.parseResponse(response);
		ArrayList<P2S> s = new ArrayList<P2S>();
		for(P2S p:pres.responseLines){
			s.add(p);
		}
		cache.put(RFC, s);
	}
	
	public void listall(){
		P2SRequest pr = new P2SRequest(hostname,port);
		String request = pr.constructListRequest();
		String response = sendP2SRequest(request);
		P2SResponse pres = P2SResponse.parseResponse(response);
		for(P2S p:pres.responseLines){
			if(cache.containsKey(p.getNumber())){
				cache.get(p.getNumber()).add(p);
			}
			else{
				cache.put(p.getNumber(), new ArrayList<P2S>());
				cache.get(p.getNumber()).add(p);
			}
		}
	}
	
	public void get(String RFC, String hostname){
		P2PRequest pr = new P2PRequest(hostname,System.getProperty("os.name") + " " + System.getProperty("os.version"));
		pr.setRFC(RFC);
		int port = -1;
		if(cache.containsKey(RFC)){
			for(P2S p:cache.get(RFC)){
				if(p.getHostname().equals(hostname)){
					port = p.getPort();
					break;
				}
			}
		}
		if(port == -1){
			System.out.println("First perform a lookup and then do a get");
			return;
		}
		Socket downloadSock;
		try {
			downloadSock = new Socket(hostname,port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			try {
				downloadSock = new Socket("localhost",port);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		String response = sendP2PRequest(pr.toString(),downloadSock, Integer.parseInt(RFC));
		System.out.println(response);
		try {
			downloadSock.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public String sendP2PRequest(String request, Socket downloadSock, int rfc){
		try {
            	PrintWriter out = new PrintWriter(downloadSock.getOutputStream(), true);
            	System.out.println(request);
            	//out.println(hostname);
            	out.println(request);
            	BufferedReader input = new BufferedReader(new InputStreamReader(downloadSock.getInputStream()));
            	int r;
				StringBuilder s = new StringBuilder();
				while((r = input.read())!=-1){
					s.append((char)r);
					if(s.toString().endsWith("\r\n\r\n")){
						break;
					}
				}
				String response = s.toString();
				String lines[] = response.split("\r\n");
				//System.out.println(response);
				String[] tokens = lines[4].split(": ");
				int length = Integer.parseInt(tokens[1]);
				for(int i = 0;i<length;i++){
					r = input.read();
					s.append((char)r);
				}
				response = s.toString();
				P2PResponse.parseResponse(response, rfc);
				return response;
				//TODO- write code for the P2P protocol here
            }
         catch (IOException e) {
			e.printStackTrace();
			return "";
		} 
	    
	}
	
	public void closeConnection(){
		try {
			uploadSocket.close();
			sock.close();
			pool.shutdown();
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}
	
/*------------------------------------Upload Server Part----------------------------------------*/	
	public void initializeUploadServer(){
		pool.execute(new Client(hostname, port, uploadSocket));
	}
	
	public void run(){
		try{
			while(true){
				pool.execute(new ClientThread(uploadSocket.accept())); //the serverthread class is used to implement the runnable interface for parallel threads
			}
		}
		catch(IOException e){
			pool.shutdown();
		}
	}
	
	
	class ClientThread implements Runnable{
		Socket socket;
		
		public ClientThread(Socket sock) {
			super();
			this.socket = sock;
		}

		public void run(){
			BufferedReader input = null;
			PrintStream output = null;
			try
			{
				while(true){
					input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
					output = new PrintStream(this.socket.getOutputStream());
					int r;
					StringBuilder s = new StringBuilder();
					while((r = input.read())!=-1){
						s.append((char)r);
						if(s.toString().endsWith("\r\n\r\n")){
							break;
						}
					}
					if(r == -1){
						break;
					}
					String request = s.toString();
					String response = parseRequest(request);
					output.write(response.getBytes());
				}
				
			}
				
			 catch (IOException e) 
			{
				System.out.println(e);
			}
			finally
			{
				try
				{
						input.close();
						output.close();
						this.socket.close();
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
				}
			}
		}
		private String parseRequest(String request){
			P2PRequest pr;
			try{
				pr = P2PRequest.parseRequest(request);
			}
			catch(Exception e){
				System.out.println(e.getMessage());
				return new P2PResponse(400).toString();
			}

			if(!pr.getVersion().equals("P2P-CI/1.0")){
				P2PResponse pres = new P2PResponse(505);
				return pres.toString();
			}
			P2PResponse pres;
			pres = new P2PResponse(pr.getRFC());
			
			//TODO : add more methods there
			return pres.toString();
		}
	}
		
		
}
	




