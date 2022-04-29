package example.grpcclient;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.ServerMethodDefinition;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import service.*;
import java.util.Stack;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.*;
import java.lang.*;

import buffers.RequestProtos.Request;
import buffers.RequestProtos.Request.RequestType;
import buffers.ResponseProtos.Response;
import com.google.protobuf.Empty;



// Implement the rockpaperscissor service. It has two sevices play and leaderboard
class RockPaperScissorsImpl extends RockPaperScissorsGrpc.RockPaperScissorsImplBase {
    
    public RockPaperScissorsImpl(){
        super();
    }
    
    @Override
    public void play(PlayReq req, StreamObserver<PlayRes> responseObserver) {
        
    	System.out.println("Received from client: " + req.getPlay()); 
    	PlayRes.Builder response = PlayRes.newBuilder(); 
    	Random rand = new Random(); 
    	
    	//computer makes its guess. 0 = rock, 1 = paper, 2 = scissors
    	int play = rand.nextInt(3); 
    	boolean win = false; 
    	
    	//there was probably a more elegant way to do this with %2 or something
    	//basically just runs through all the combinations of rock, paper, scissors
    	switch(req.getPlay())
    	{
    	case ROCK: 
    		if(play == 0)
    		{
    			response.setIsSuccess(true); 
    			response.setWin(false); 
    			response.setMessage("You and the server both played rock. It's a tie!");
    		}
    		else if(play == 1)
    		{
    			response.setIsSuccess(true); 
    			response.setWin(false); 
    			response.setMessage("The server chose paper, you lose!");  
    		}
    		else
    		{
    			response.setIsSuccess(true); 
    			response.setWin(true); 
    			response.setMessage("The server played scissors. You win!!");
    			win = true; 
    		}
    		break; 
    	case PAPER: 
    		if(play == 0)
    		{
    			response.setIsSuccess(true); 
    			response.setWin(true); 
    			response.setMessage("The server played rock. You win!");
    			win = true; 
    		}
    		else if(play == 1)
    		{
    			response.setIsSuccess(true); 
    			response.setWin(false); 
    			response.setMessage("The server chose paper. It's a tie!");    			
    		}
    		else
    		{
    			response.setIsSuccess(true); 
    			response.setWin(false); 
    			response.setMessage("The server played scissors. You lose!");
    		}
    		break; 
    	case SCISSORS: 
    		if(play == 0)
    		{
    			response.setIsSuccess(true); 
    			response.setWin(false); 
    			response.setMessage("The server played rock. You lose!");
    		}
    		else if(play == 1)
    		{
    			response.setIsSuccess(true); 
    			response.setWin(true); 
    			response.setMessage("The server chose paper. You win!!");   
    			win = true;  			
    		}
    		else
    		{
    			response.setIsSuccess(true); 
    			response.setWin(false); 
    			response.setMessage("The server played scissors. It's a tie!");
    		}
    		break; 
    		default: 
    			response.setIsSuccess(false); 
    			response.setWin(false); 
    			response.setMessage("Something went wrong!"); 
    	}
    	
    	//updates leader board
    	writeToLeaderBoard(req.getName(), response.getWin()); 
    	
    	PlayRes resp = response.build(); 
    	responseObserver.onNext(resp); 
        responseObserver.onCompleted();
    	
    }
    
    @Override
    public void leaderboard(Empty empt, StreamObserver<LeaderboardRes> responseObserver) {
        
    	LeaderboardRes.Builder response = LeaderboardRes.newBuilder(); 
    	response.setIsSuccess(true);
    	try
    	{
    		
		File lb = new File("leaderboard.txt"); 
		BufferedReader br = new BufferedReader(new FileReader(lb));
        String line;
        String[] words; 
        int counter = 1; 
        while ((line = br.readLine()) != null) {
        	LeaderboardEntry.Builder entry = LeaderboardEntry.newBuilder(); 
        	
        	//leaderboard format is NAME WINS LOSSES so this makes an array with this three variables
        	words = line.split(" "); 
        	entry.setName(words[0]); 
        	entry.setRank(counter); 
        	entry.setWins(Integer.valueOf(words[1])); 
        	entry.setLost(Integer.valueOf(words[2]));
        	entry.build(); 
        	counter++; 
        	
        	response.addLeaderboard(entry); 
        }
        br.close();
    	}catch(Exception e)
    	{
    		
    	}
    	LeaderboardRes resp = response.build(); 
    	responseObserver.onNext(resp); 
    	responseObserver.onCompleted(); 
    }
    
    public void writeToLeaderBoard(String name, boolean win)
    {
    	try {
    	ArrayList<String> names = new ArrayList<String>(); 
    	ArrayList<Integer> winArray = new ArrayList<Integer>(); 
    	ArrayList<Integer> lossArray = new ArrayList<Integer>(); 

		File lb = new File("leaderboard.txt"); 
        BufferedReader br = new BufferedReader(new FileReader(lb));
        String line;
        String[] words;
        int x = 0; 
        
        //reads in the leaderboard and puts it into the arraylists
        while ((line = br.readLine()) != null) {
        	words = line.split(" "); 
            names.add(words[0]); 
            winArray.add(Integer.valueOf(words[1])); 
            lossArray.add(Integer.valueOf(words[2]));
        }
        boolean notInList = true; 
        //checks to see if the player is already in the list
        //if so just updates his existing score
        for(x = 0; x < names.size(); x++)
        {
        	if(names.get(x).equals(name))
        	{
        		if(win)
        			winArray.set(x,  winArray.get(x) + 1); 
        		else
        			lossArray.set(x,  lossArray.get(x) + 1); 
        		
        		notInList = false; 
        	}
        }
        //if player isn't in the list he is added with either 1 win and 0 losses or the other way around
        if(notInList)
        {
        	names.add(name); 
        	if(win)
        	{
        		winArray.add(1); 
        		lossArray.add(0); 
        	}
        	else
        	{
        		lossArray.add(1); 
        		winArray.add(0); 
        	}
        }
        br.close(); 
        //writes the updated leaderboard data back to the .txt file
        BufferedWriter bw = new BufferedWriter(new FileWriter(lb)); 
        for(x = 0; x < names.size(); x++)
        {
        	bw.write(names.get(x) + " " + winArray.get(x) + " " + lossArray.get(x)); 
        	bw.newLine();
        }
        bw.close();
    	}catch(Exception e)
    	{
    		System.out.println("Could not update leaderboard :("); 
    	}
    }
}