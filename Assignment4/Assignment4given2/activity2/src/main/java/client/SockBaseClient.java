package client;

import java.net.*;
import java.io.*;

import org.json.*;

import buffers.RequestProtos.Request;
import buffers.ResponseProtos.Response;
import buffers.ResponseProtos.Entry;

import java.util.*;
import java.util.stream.Collectors;

class SockBaseClient {

    public static void main (String args[]) throws Exception {
        Socket serverSock = null;
        OutputStream out = null;
        InputStream in = null;
        int i1=0, i2=0;
        int port = 9099; // default port
        boolean playing = true; 

        // Make sure two arguments are given
        if (args.length != 2) {
            System.out.println("Expected arguments: <host(String)> <port(int)>");
            System.exit(1);
        }
        String host = args[0];
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port] must be integer");
            System.exit(2);
        }

        // Ask user for username
        System.out.println("Please provide your name for the server.");
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String strToSend = stdin.readLine();

        // Build the first request object just including the name
        Request op = Request.newBuilder()
                .setOperationType(Request.OperationType.NAME)
                .setName(strToSend).build();
        Response response;
        try {
            // connect to the server
            serverSock = new Socket(host, port);

            // write to the server
            out = serverSock.getOutputStream();
            in = serverSock.getInputStream();

            op.writeDelimitedTo(out);

            // read from the server
            response = Response.parseDelimitedFrom(in);

            // print the server response. 
            System.out.println(response.getMessage() + " - message from server");
            while(playing)
            {
            	System.out.println("\nWhat would you like to do? \n 1 - to see the leader board \n 2 - to enter a game \n 3 - quit the game");
            	strToSend = stdin.readLine(); 
            	//System.out.println(strToSend + ": strToSend"); 
            	
            	//displays the leaderboard but not how it 
            	//was supposed to according to the proto file
            	if(strToSend.equals("1"))
            	{
            		Request op1 = Request.newBuilder()
            				.setOperationType(Request.OperationType.LEADER).build();
            		op1.writeDelimitedTo(out); 
            		response = Response.parseDelimitedFrom(in); 
            		System.out.println(response.getImage() + " - message from server");
            		//requestLeaderBoard(); 
            	}
            	else if(strToSend.equals("2"))
            	{
            		boolean inGame = true; 
            		int row = 0; 
            		int col = 0; 
        			Request op2 = Request.newBuilder()
        					.setOperationType(Request.OperationType.NEW).build(); 
        			op2.writeDelimitedTo(out); 
        			response = Response.parseDelimitedFrom(in); 
        			System.out.println(response.getImage()); 
        			System.out.println(response.getTask()); 
        			
            		while(inGame)
            		{
            			
            			strToSend = stdin.readLine(); 
            			
            			if(strToSend.equalsIgnoreCase("exit"))
            			{
            				System.out.println("Okay, goodbye"); 
            				playing = false; 
            				inGame = false; 
            				strToSend = "3"; 
            			}
            			else
            			{
            			try
            			{
            				row = Integer.parseInt(strToSend.substring(0, 1)); 
            				col = Integer.parseInt(strToSend.substring(1).trim()); 
            			}
            			catch(Exception e)
            			{
            				row = 1; 
            				col = 1; 
            				System.out.println("You have to enter 2 numbers between 1 and 7 to fire. (e.g. 3 5). 1 1 entered for you this time...");
            			}
            			
            			Request op3 = Request.newBuilder()
            					.setOperationType(Request.OperationType.ROWCOL)
            					.setRow(row)
            					.setColumn(col)
            					.build(); 
            			op3.writeDelimitedTo(out); 
            			response = Response.parseDelimitedFrom(in);
            			if(response.getResponseType() == Response.ResponseType.WON)
            			{
            				System.out.println(response.getImage()); 
            				System.out.println("You win!!!");
            				inGame = false;
            			}
            			else if(response.getResponseType() == Response.ResponseType.ERROR)
            			{
            				System.out.println(response.getMessage()); 
            			}
            			else
            			{
            				System.out.println(response.getImage()); 
            				System.out.println(response.getTask()); 
            			}
            		}
            		}
            	}
            	else if(strToSend.equals("3"))
            	{
            		System.out.println("ok goodbye"); 
            		playing = false; 
            	}
            	else
            	{
            		System.out.println("Please choose a valid option (1-3)"); 
            	}            	
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)   in.close();
            if (out != null)  out.close();
            if (serverSock != null) serverSock.close();
        }
    }
    
    //was going to make separate methods for these but didn't want to 
    //make new variables and everything in each so never used them.
    /*
    public void requestLeaderBoard()
    {
    	
    }
    
    public void playGame()
    {
    	
    }
    */
}


