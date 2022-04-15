/**
  File: Server.java
  Author: Student in Fall 2020B
  Description: Server class in package taskone.
*/

package taskone;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.json.JSONObject;
import java.util.ArrayList; 

/**
 * Class: Server
 * Description: Server tasks.
 */
class Server {
	
	public static ArrayList<Integer> nodes = new ArrayList<Integer>(); 
    public static void main(String[] args) throws Exception {
        int port;

        if (args.length != 1) {
            // gradle runServer -Pport=9099 -q --console=plain
            System.out.println("Usage: gradle runServer -Pport=9099 -q --console=plain");
            System.exit(1);
        }
        port = -1;
        try {
            port = Integer.parseInt(args[0]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be an integer");
            System.exit(2);
        }
        ServerSocket server = new ServerSocket(port);
        System.out.println("Server Started...");
        
        boolean quit = false;
        OutputStream out = null;
        InputStream in = null;
        OutputStream bankOut = null; 
        InputStream bankIn = null; 
        int amount = 0; 
        
        while (!quit) {
        	
            System.out.println("Accepting a Request...");
            Socket clientSock = server.accept();

            out = clientSock.getOutputStream();
            in = clientSock.getInputStream();
            System.out.println("Server connected to client:");
            
            byte[] messageBytes = NetworkUtils.receive(in);
            JSONObject message = JsonUtils.fromByteArray(messageBytes);
            JSONObject returnMessage = new JSONObject();
            
            if(message.get("option").equals("node"))
            {
                System.out.println("Bank#" + message.get("port") + " has been added to the network."); 
                nodes.add(message.getInt("port"));    
                returnMessage.put("greeting", "You have been added to the banking network"); 
            }
            else
            {
            	if(message.get("option").equals("credit"))
            	{
                    System.out.println("Client " + message.get("id") + " requested $" + message.get("amount") + " in credit."); 
            		amount = (message.getInt("amount")); 
            		Socket bankSocket; 
            		JSONObject bankRequest = new JSONObject(); 
            		JSONObject bankResponse = new JSONObject(); 
            		bankRequest.put("amount", amount); 
            		bankRequest.put("id", message.getInt("id")); 
            		bankRequest.put("option", "credit"); 
            		boolean consensus = true; 
            		for(int x = 0; x < nodes.size(); x++)
            		{
            			System.out.println("Sending request to bank #" + nodes.get(x)); 
            			
            			bankSocket = new Socket("localhost", nodes.get(x)); 
            			bankOut = bankSocket.getOutputStream(); 
            			bankIn = bankSocket.getInputStream(); 
            			NetworkUtils.send(bankOut, JsonUtils.toByteArray(bankRequest)); 
            			messageBytes = NetworkUtils.receive(bankIn); 
            			bankResponse = JsonUtils.fromByteArray(messageBytes); 
            			System.out.println("Bank #" + nodes.get(x) + " says " + bankResponse.get("option")); 
            			
            			if(bankResponse.getBoolean("option"))
            				consensus = false; 
            			
            			bankSocket.close();
            		}
            		System.out.println("The bank consensus was: " + consensus); 
            		
            		if(consensus)
            		{
            			int[] bankBalances = new int[nodes.size()]; 
            			bankRequest = new JSONObject(); 
                		bankResponse = new JSONObject(); 
                		bankRequest.put("option", "info"); 
            			System.out.println("Banks do not have existing loan from client. Checking cash reserves now."); 
            			for(int x = 0; x < nodes.size(); x++)
            			{
            				System.out.println("Requesting info from bank#" + nodes.get(x)); 
                			
                			bankSocket = new Socket("localhost", nodes.get(x)); 
                			bankOut = bankSocket.getOutputStream(); 
                			bankIn = bankSocket.getInputStream(); 
                			NetworkUtils.send(bankOut, JsonUtils.toByteArray(bankRequest)); 
                			messageBytes = NetworkUtils.receive(bankIn); 
                			bankResponse = JsonUtils.fromByteArray(messageBytes); 
                			bankBalances[x] = bankResponse.getInt("balance"); 
                			System.out.println("Bank#" + nodes.get(x) + " has " + bankResponse.get("balance") + " available."); 
                			bankSocket.close();
            			}
            			
            			boolean onePointFive = true; 
            			for(int x = 0; x < bankBalances.length; x++)
            			{
            				if(bankBalances[x] < (amount * 1.5))
            				{
            					onePointFive = false; 
            					
            				}
            			}
            			
            			if(onePointFive)
            			{
            				bankRequest = new JSONObject(); 
            				bankResponse = new JSONObject(); 
            				bankRequest.put("option", "amount"); 
                    		bankRequest.put("id", message.getInt("id")); 
            				System.out.println("Banks have funds for the loan. Transfering money now."); 
            				for(int x = 0; x < nodes.size(); x++)
                			{
                    			
                    			bankSocket = new Socket("localhost", nodes.get(x)); 
                    			
                    			//first bank node loans the extra amount if it doesn't split evenly
                    			if(x == 0)
                    				bankRequest.put("amount", (amount/nodes.size() + amount%nodes.size()));
                    			else
                    				bankRequest.put("amount", (amount/nodes.size()));
                    				
                    			bankOut = bankSocket.getOutputStream(); 
                    			bankIn = bankSocket.getInputStream(); 
                    			NetworkUtils.send(bankOut, JsonUtils.toByteArray(bankRequest)); 
                    			messageBytes = NetworkUtils.receive(bankIn); 
                    			bankResponse = JsonUtils.fromByteArray(messageBytes); 
                    			System.out.println("Bank#" + nodes.get(x) + " says " + bankResponse.get("response")); 
                    			bankSocket.close();
                			}
                			returnMessage.put("greeting", "Okay, your request has been granted. You now have $" + amount); 
            			}
            			else
            			{
            				returnMessage.put("greeting", "Your request was too high. The banks denied your request. Sorry."); 
            			}
            			
            		}
            		else
            		{
            			returnMessage.put("greeting", "You have an existing loan from the bank. Your request was denied. Sorry."); 
            		}
            		
            	}
            	else if(message.get("option").equals("payment"))
            	{
                    System.out.println("Client " + message.get("id") + " wants to pay back $" + message.get("amount") + "."); 
            		amount = (message.getInt("amount")); 
            		Socket bankSocket; 
            		int amountOwed = 0; 
            		JSONObject bankRequest = new JSONObject(); 
            		JSONObject bankResponse = new JSONObject(); 
        			int[] howMuchEachBankLoaned = new int[nodes.size()]; 
            		
            		bankRequest.put("option", "loanInfo"); 
            		bankRequest.put("id", message.get("id")); 
            		System.out.println("Checking how much client #" + message.get("id") + " owes.");
            		for(int x = 0; x < nodes.size(); x++)
            		{
            			bankSocket = new Socket("localhost", nodes.get(x)); 
            			bankOut = bankSocket.getOutputStream(); 
            			bankIn = bankSocket.getInputStream(); 
            			NetworkUtils.send(bankOut, JsonUtils.toByteArray(bankRequest)); 
            			messageBytes = NetworkUtils.receive(bankIn); 
            			bankResponse = JsonUtils.fromByteArray(messageBytes); 
            			
            			howMuchEachBankLoaned[x] = bankResponse.getInt("amount");
            			amountOwed += bankResponse.getInt("amount"); 
            			System.out.println("Bank #" + nodes.get(x) + " is owed " + bankResponse.get("amount")); 
            			
            			bankSocket.close();
            		}
            		if(amount > amountOwed)
            		{
            			returnMessage.put("greeting", "You cannot payback more than you were loaned. You currently only owe $" + amountOwed); 
            		}
            		else
            		{
            			int counter = 0; 
            			while(amount > 0)
            			{
            				
                    		bankRequest = new JSONObject(); 
                    		bankResponse = new JSONObject(); 
                    		bankRequest.put("id", message.get("id")); 
                    		bankRequest.put("option", "payback"); 
                    		if(howMuchEachBankLoaned[counter] > 0)
                    		{
                    			bankSocket = new Socket("localhost", nodes.get(counter)); 
                    			bankOut = bankSocket.getOutputStream(); 
                    			bankIn = bankSocket.getInputStream(); 
                        		if(howMuchEachBankLoaned[counter] < amount)
                        		{
                        			bankRequest.put("amount", howMuchEachBankLoaned[counter]); 
                        			amount -= howMuchEachBankLoaned[counter]; 
                        			amountOwed -= howMuchEachBankLoaned[counter]; 
                        		}
                        		else if(howMuchEachBankLoaned[counter] >= amount)
                        		{
                        			bankRequest.put("amount", amount); 
                        			amount -= amount; 
                        			amountOwed -= howMuchEachBankLoaned[counter];  
                        		}
                    			NetworkUtils.send(bankOut, JsonUtils.toByteArray(bankRequest)); 
                    		}
                    		counter++; 
            			}
            			returnMessage.put("greeting", "Okay, your payment went through. You now only owe $" + amountOwed); 
            		}
            	}
            	else if(message.get("option").equals("quit"))
            	{
            		returnMessage.put("greeting", "Okay, goodbye!"); 
            	}
            	else
            	{
            		returnMessage.put("greeting", "response not recognized"); 
            	}
            }
            if(returnMessage == null)
            	returnMessage.put("greeting", "I haven't set up a response message yet"); 
            byte[] output = JsonUtils.toByteArray(returnMessage);
            NetworkUtils.send(out, output);

            try {
                System.out.println("close socket of client ");
                clientSock.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
