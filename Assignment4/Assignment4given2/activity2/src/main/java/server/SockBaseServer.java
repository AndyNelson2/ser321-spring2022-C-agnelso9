package server;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.*;
import java.lang.*;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Logs;
import buffers.RequestProtos.Message;
import buffers.ResponseProtos.Response;
import buffers.ResponseProtos.Entry;

class SockBaseServer {
    static String logFilename = "logs.txt";

    ServerSocket serv = null;
    InputStream in = null;
    OutputStream out = null;
    Socket clientSocket = null;
    int port = 9099; // default port
    Game game;


    public SockBaseServer(Socket sock, Game game){
        this.clientSocket = sock;
        this.game = game;
        try {
            in = clientSocket.getInputStream();
            out = clientSocket.getOutputStream();
        } catch (Exception e){
            System.out.println("Error in constructor: " + e);
        }
    }

    // Handles the communication right now it just accepts one input and then is done you should make sure the server stays open
    // can handle multiple requests and does not crash when the server crashes
    // you can use this server as based or start a new one if you prefer. 
    public void start() throws IOException {
        String name = "";


        System.out.println("Ready...");
        try {
            // read the proto object and put into new objct
            Request op = Request.parseDelimitedFrom(in);
            String result = null;

            

            // if the operation is NAME (so the beginning then say there is a commention and greet the client)
            if (op.getOperationType() == Request.OperationType.NAME) {
                // get name from proto object
            name = op.getName();

            // writing a connect message to the log with name and CONNENCT
            writeToLog(name, Message.CONNECT);
                System.out.println("Got a connection and a name: " + name);
                Response response = Response.newBuilder()
                        .setResponseType(Response.ResponseType.GREETING)
                        .setMessage("Hello " + name + " and welcome. Welcome to a simple game of battleship. ")
                        .build();
                response.writeDelimitedTo(out);
            }
            
            while(true)
            {
            	op = Request.parseDelimitedFrom(in); 
            	
            	//sends leaderboard but not how it was supposed to according
            	//to the proto file
            	if(op.getOperationType() == Request.OperationType.LEADER)
            	{ 
            		
            		StringBuilder sb = new StringBuilder();
            		File lb = new File("leaderboard.txt"); 
                    BufferedReader br = new BufferedReader(new FileReader(lb));
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n"); 
                    }
                    br.close();
                    String leaderboardtext = sb.toString(); 
                    Response response1 = Response.newBuilder()
                    		.setResponseType(Response.ResponseType.LEADER)
                    		.setImage(leaderboardtext)
                    		.build(); 
                    response1.writeDelimitedTo(out); 
            	}
            	else if(op.getOperationType() == Request.OperationType.NEW)
            	{
            		game.newGame(); 
            		Response response2 = Response.newBuilder()
                            .setResponseType(Response.ResponseType.TASK)
                            .setImage(game.getImage())
                            .setTask("Enter a row and column to fire upon!.")
                            .build();
            		response2.writeDelimitedTo(out); 
            	}
            	else if(op.getOperationType() == Request.OperationType.ROWCOL)
            	{
            		boolean continueOn = true; 
            		try {
            			game.replaceOneCharacter(op.getRow()-1, op.getColumn()-1);
            		}
            		catch(Exception e)
            		{
            			Response response4 = Response.newBuilder()
            					.setResponseType(Response.ResponseType.ERROR)
            					.setMessage("You have to enter 2 numbers between 1 and 7 to fire. (e.g. 3 5)")
            					.build(); 
            			response4.writeDelimitedTo(out); 
            			continueOn = false; 
            		}
            		if(continueOn)
            		{
            		if(game.checkWon())
            		{
            			writeToLeaderBoard(name, game.getPoints()); 
            			Response response3 = Response.newBuilder()
            					.setResponseType(Response.ResponseType.WON)
            					.setImage(game.getImage())
            					.build(); 
            			game.setWon(); 
            			response3.writeDelimitedTo(out); 
            		}
            		else
            		{
            			if(game.returnHit())
            			{
            				System.out.println(op.getRow() + " " + op.getColumn() + " was a hit"); 
            			}
            			Response response2 = Response.newBuilder()
                            .setResponseType(Response.ResponseType.TASK)
                            .setImage(game.getImage())
                            .setTask("Enter a row and column to fire upon!.")
                            .build();
            			response2.writeDelimitedTo(out); 
            		}
            		}
            	}
            }
            /*
            // Example how to start a new game and how to build a response with the image which you could then send to the server
            // LINE 67-108 are just an example for Protobuf and how to work with the differnt types. They DO NOT
            // belong into this code. 
            game.newGame(); // starting a new game

            // adding the String of the game to 
            Response response2 = Response.newBuilder()
                .setResponseType(Response.ResponseType.TASK)
                .setImage(game.getImage())
                .setTask("Select a row and column.")
                .build();

            // On the client side you would receive a Response object which is the same as the one in line 70, so now you could read the fields
            System.out.println("Task: " + response2.getResponseType());
            System.out.println("Image: \n" + response2.getImage());
            System.out.println("Task: \n" + response2.getTask());

            // Creating Entry and Leader response
            Response.Builder res = Response.newBuilder()
                .setResponseType(Response.ResponseType.LEADER);

            // building an Entry for the leaderboard
            Entry leader = Entry.newBuilder()
                .setName("name")
                .setWins(0)
                .setLogins(0)
                .build();

            // building another Entry for the leaderboard
            Entry leader2 = Entry.newBuilder()
                .setName("name2")
                .setWins(1)
                .setLogins(1)
                .build();

            // adding entries to the leaderboard
            res.addLeader(leader);
            res.addLeader(leader2);

            // building the response 
            Response response3 = res.build();

            // iterating through the current leaderboard and showing the entries
            for (Entry lead: response3.getLeaderList()){
                System.out.println(lead.getName() + ": " + lead.getWins());
            }
            */

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (out != null)  out.close();
            if (in != null)   in.close();
            if (clientSocket != null) clientSocket.close();
        }
    }


    /**
     * Writing a new entry to our log
     * @param name - Name of the person logging in
     * @param message - type Message from Protobuf which is the message to be written in the log (e.g. Connect) 
     * @return String of the new hidden image
     */
    public static void writeToLog(String name, Message message){
        try {
            // read old log file 
            Logs.Builder logs = readLogFile();

            // get current time and data
            Date date = java.util.Calendar.getInstance().getTime();
            System.out.println(date);

            // we are writing a new log entry to our log
            // add a new log entry to the log list of the Protobuf object
            logs.addLog(date.toString() + ": " +  name + " - " + message);

            // open log file
            FileOutputStream output = new FileOutputStream(logFilename);
            Logs logsObj = logs.build();

            // This is only to show how you can iterate through a Logs object which is a protobuf object
            // which has a repeated field "log"

            for (String log: logsObj.getLogList()){

                System.out.println(log);
            }

            // write to log file
            logsObj.writeTo(output);
        }catch(Exception e){
            System.out.println("Issue while trying to save");
        }
    }
    
    public static void writeToLeaderBoard(String name, int points)
    {
    	try {
    	ArrayList<String> names = new ArrayList<String>(); 
    	ArrayList<Integer> pointArray = new ArrayList<Integer>(); 

		File lb = new File("leaderboard.txt"); 
        BufferedReader br = new BufferedReader(new FileReader(lb));
        String line;
        int x = 0; 
        while ((line = br.readLine()) != null) {
            names.add(line.substring(0, line.indexOf(" "))); 
            pointArray.add(Integer.parseInt(line.substring(line.indexOf(" ")).trim())); 
            System.out.println(names.get(x) + " " + pointArray.get(x)); 
            x++; 
        }
        boolean notInList = true; 
        for(x = 0; x < names.size(); x++)
        {
        	if(names.get(x).equals(name))
        	{
        		pointArray.set(x, pointArray.get(x) + points); 
        		notInList = false; 
        	}
        }
        if(notInList)
        {
        	names.add(name); 
        	pointArray.add(points); 
        }
        br.close(); 
        //FileWriter fobj = new FileWriter("leaderboard.txt"); 
        BufferedWriter bw = new BufferedWriter(new FileWriter(lb)); 
        for(x = 0; x < names.size(); x++)
        {
        	bw.write(names.get(x) + " " + pointArray.get(x)); 
        	bw.newLine();
        }
        bw.close();
    	}catch(Exception e)
    	{
    		System.out.println("Could not update leaderboard :("); 
    	}
    }

    /**
     * Reading the current log file
     * @return Logs.Builder a builder of a logs entry from protobuf
     */
    public static Logs.Builder readLogFile() throws Exception{
        Logs.Builder logs = Logs.newBuilder();

        try {
            // just read the file and put what is in it into the logs object
            return logs.mergeFrom(new FileInputStream(logFilename));
        } catch (FileNotFoundException e) {
            System.out.println(logFilename + ": File not found.  Creating a new file.");
            return logs;
        }
    }


    public static void main (String args[]) throws Exception {
        Game game = new Game();

        if (args.length != 2) {
            System.out.println("Expected arguments: <port(int)> <delay(int)>");
            System.exit(1);
        }
        int port = 9099; // default port
        int sleepDelay = 10000; // default delay
        Socket clientSocket = null;
        ServerSocket serv = null;

        try {
            port = Integer.parseInt(args[0]);
            sleepDelay = Integer.parseInt(args[1]);
        } catch (NumberFormatException nfe) {
            System.out.println("[Port|sleepDelay] must be an integer");
            System.exit(2);
        }
        try {
            serv = new ServerSocket(port);
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(2);
        }

        clientSocket = serv.accept();
        SockBaseServer server = new SockBaseServer(clientSocket, game);
        server.start();

    }
}

