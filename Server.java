package com.csc573.p2p;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.io.DataInputStream;
import java.io.DataOutputStream;


public class Server{
	String serverName;
	int port = 7734;
	//FileQuery file;
	private ServerSocket MyService;    //initializing the server socket
	private final ExecutorService pool; //initializing the pool for multithread processes
	private final int poolSize = 100; 	// setting the pool size for the number of threads
	private final LinkedList<peerList> peer_list = new LinkedList<peerList>(); 
	private final LinkedList<RFC> RFCList = new LinkedList<RFC>();

	//server method for setting up the server port
	public Server(String name, int port){	
		serverName = name;
		this.port = port;
		try {
			MyService = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		pool = Executors.newFixedThreadPool(poolSize);
	}
	
	//initialize method for taking in multiple requests from the clients and keeping the server open
	public void initialize() {
		try{
			while(true)
				pool.execute(new ServerThread(MyService.accept())); //the serverthread class is used to implement the runnable interface for parallel threads
		}
		catch(IOException e){
			System.out.println(e.toString());
			pool.shutdown();
		}
		
	}


//A single thread process for one client request is implemented in run method of the runnable interface
class ServerThread implements Runnable{
	Socket socket;
	String hostname;
	
	public ServerThread(Socket socket){
		this.socket = socket;
		this.hostname = "";
		
	}
		
	public void run()
	{
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
			P2S ob = new P2S();
			System.out.printf("connection terminated");
			Iterator<peerList> iter = peer_list.iterator();
			while (iter.hasNext()) {
			    peerList p = iter.next();

			    if (p.hostname.equals(this.hostname))
			        iter.remove();
			}
			Iterator<RFC> iter1 = RFCList.iterator();
			while (iter1.hasNext()) {
			    RFC r = iter1.next();

			    if (r.hostname.equals(this.hostname))
			        iter1.remove();
			}
		}
	}
	private String parseRequest(String request){
		P2SRequest pr;
		try{
			pr = P2SRequest.parseRequest(request);
		}
		catch(Exception e){
			System.out.println(e.toString());
			return new P2SResponse(400).toString();
		}
		//System.out.println(pr.toString());
		//System.out.println(pr.getMethod());
		//System.out.println(pr.getHostname());
		this.hostname = pr.getHostname();
		if(!pr.getVersion().equals("P2P-CI/1.0")){
			P2SResponse pres = new P2SResponse(505);
			return pres.toString();
		}
		P2SResponse pres = new P2SResponse(400);
		if(pr.getMethod().equals("ADD")){
			peerList newPeer = new peerList(pr.getHostname(),pr.getPort());
			boolean flag = false;
			for(peerList p:peer_list){
				if(p.equals(newPeer)){
					flag = true;
					break;
				}
			}
			if(!flag){
				peer_list.add(new peerList(pr.getHostname(),pr.getPort())); //TODO - Check for duplicates
			}
			RFCList.add(new RFC(pr.getNumber(),pr.getTitle(),pr.getHostname()));
			pres = new P2SResponse(200);
			P2S x = new P2S();
			//System.out.println(pr.getTitle()); //trial line. MUST REMOVE
			x.setNumber(pr.getNumber());
			x.setTitle(pr.getTitle());
			x.setHostname(pr.getHostname());
			x.setPort(pr.getPort());
			pres.addResponse(x);
		}
		if(pr.getMethod().equals("LOOKUP") || pr.getMethod().equals("LIST ALL")){
			if(RFCList.isEmpty()){
				pres = new P2SResponse(404);
			}
			else{
				pres = new P2SResponse(200);
			}
			for(RFC r:RFCList){
				if(r.getNumber().equals(pr.getNumber()) || pr.getMethod().equals("LIST ALL")){
					P2S p = new P2S();
					p.setNumber(r.getNumber());
					p.setTitle(r.getTitle());
					p.setHostname(r.getHostname());
					for(peerList q:peer_list){
						if(q.hostname.equals(p.getHostname())){
							p.setPort(q.clientPort);
							break;
						}
					}
					pres.addResponse(p);
				}
			}
			if(pres.responseLines.isEmpty()){
				pres = new P2SResponse(404);
			}
		}
		//TODO : add more methods there
		return pres.toString();
	}
}

}

class peerList{
	String hostname;
	int clientPort;
	public peerList(String hostname, int clientPort){
		this.hostname = hostname;
		this.clientPort = clientPort;
	}
	public boolean equals(peerList p){
		if(p.hostname.equals(hostname) && p.clientPort == clientPort){
			return true;
		}
		return false;
	}
}

