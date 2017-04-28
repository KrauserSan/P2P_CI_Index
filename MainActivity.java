package com.csc573.p2p;

import java.util.*;

public class MainActivity {
	public static void main(String[] args){
		if("server".equals(args[0])){
			Server s = new Server("mohit",7734);
			s.initialize();
			System.out.println("Server up at port 7734");
		}
		if("client".equals(args[0])){
			Scanner sc = new Scanner(System.in);
			System.out.println("Enter Hostname");
			String hostname = sc.next();
			System.out.println("Enter Upload Port");
			int port = sc.nextInt();
			sc.nextLine();
			Client c = new Client(hostname, port);
			while (true){
				System.out.println("Enter \n1: To add \n2: To lookup\n3: To ListAll\n4: To Get\n5: To Exit");
				String inp = sc.nextLine();
				if("5".equals(inp)){
                	break;
				}
				else if ("1".equals(inp)){
					System.out.println("Enter RFC number to add");
					String rfcNum = sc.nextLine();
					System.out.println("enter RFC title to add");
					String rfcTitle = sc.nextLine();
					c.add(rfcNum,rfcTitle);
				}
				else if ("2".equals(inp)){
					System.out.println("Enter RFC number to lookup");
					String rfcNum = sc.nextLine();
					System.out.println("enter RFC title to lookup");
					String rfcTitle = sc.nextLine();
					c.lookup(rfcNum,rfcTitle);
				}
				else if ("3".equals(inp)){
					c.listall();
				}
				else if ("4".equals(inp)){
					System.out.println("Enter RFC number to get");
					String rfcNum = sc.nextLine();
					System.out.println("enter Hostname to get");
					String hostnamep = sc.nextLine();
					c.get(rfcNum,hostnamep);
				}
				else if ("6".equals(inp)){
					c.add("1234", "title");
					c.lookup("1234", "title");
					c.get("1234", c.hostname);
				}
				
			}
			c.closeConnection();
			
		}
	}

}
