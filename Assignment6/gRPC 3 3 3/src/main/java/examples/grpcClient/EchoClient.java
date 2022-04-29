package example.grpcclient;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
import service.*;
import test.TestProtobuf;
import java.util.Timer; 
import java.util.concurrent.TimeUnit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import com.google.protobuf.Empty; // needed to use Empty

// just to show how to use the empty in the protobuf protocol
    // Empty empt = Empty.newBuilder().build();

/**
 * Client that requests `parrot` method from the `EchoServer`.
 */
public class EchoClient {
	
  private final EchoGrpc.EchoBlockingStub blockingStub;
  private final JokeGrpc.JokeBlockingStub blockingStub2;
  private final RockPaperScissorsGrpc.RockPaperScissorsBlockingStub blockingStub4; 
  private final TimerGrpc.TimerBlockingStub blockingStub5; 
  private final ThreeNplusOneGrpc.ThreeNplusOneBlockingStub blockingStub6; 
  private final RegistryGrpc.RegistryBlockingStub blockingStub3;
  public  String name = ""; 

  /** Construct client for accessing server using the existing channel. */
  public EchoClient(Channel channel, Channel regChannel) {
    // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's
    // responsibility to
    // shut it down.

    // Passing Channels to code makes code easier to test and makes it easier to
    // reuse Channels.
    blockingStub = EchoGrpc.newBlockingStub(channel);
    blockingStub2 = JokeGrpc.newBlockingStub(channel);
    blockingStub3 = RegistryGrpc.newBlockingStub(regChannel);
    blockingStub4 = RockPaperScissorsGrpc.newBlockingStub(channel); 
    blockingStub5 = TimerGrpc.newBlockingStub(channel); 
    blockingStub6 = ThreeNplusOneGrpc.newBlockingStub(channel); 
  }

  public void askServerToParrot(String message) {

    ClientRequest request = ClientRequest.newBuilder().setMessage(message).build();
    ServerResponse response;
    try {
      response = blockingStub.parrot(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e.getMessage());
      return;
    }
    System.out.println("Received from server: " + response.getMessage());
  }

  public void askForJokes(int num) {
    JokeReq request = JokeReq.newBuilder().setNumber(num).build();
    JokeRes response;


    try {
      response = blockingStub2.getJoke(request);
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
    System.out.println("Your jokes: ");
    for (String joke : response.getJokeList()) {
      System.out.println("--- " + joke);
    }
  }

  public void setJoke(String joke) {
    JokeSetReq request = JokeSetReq.newBuilder().setJoke(joke).build();
    JokeSetRes response;

    try {
      response = blockingStub2.setJoke(request);
      System.out.println(response.getOk());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void playRPS(int rock)
  {
	  
	  PlayReq request; 
	  
	  switch(rock)
	  {
	  case 1: 
		  request  = PlayReq.newBuilder().setName(name).setPlay(PlayReq.Played.ROCK).build();  
		  break; 
	  case 2:
		  request  = PlayReq.newBuilder().setName(name).setPlay(PlayReq.Played.PAPER).build(); 
		  break; 
	  case 3: 
		  request  = PlayReq.newBuilder().setName(name).setPlay(PlayReq.Played.SCISSORS).build();
		  break; 
      default: 
		  request  = PlayReq.newBuilder().setName(name).setPlay(PlayReq.Played.ROCK).build();
		  System.out.println("Invalid input, rock chosen for you."); 
		  break;  
	  }
	  
	  
	  PlayRes response; 
	  
	  try
	  {
		  response = blockingStub4.play(request); 
		  System.out.println(response.getMessage()); 
	  } catch(Exception e)
	  {
		  System.err.println("playRPS failed: " + e); 
		  return; 
	  }
  }
  
  public void getRPSleaderboard()
  {
	  LeaderboardRes response; 
	  try
	  {
		  Empty empt = Empty.newBuilder().build();
		  response = blockingStub4.leaderboard(empt); 
		  
		  System.out.println("Leaderboard: ");
		    for (LeaderboardEntry entry : response.getLeaderboardList()) {
		      System.out.println(entry.getRank() + ". " + entry.getName() + " " + entry.getWins() + " wins - " + entry.getLost() + " losses.");
		    }
	  } catch(Exception e)
	  {
		  System.err.println("playRPS failed: " + e); 
		  return; 
	  }
  }
  
  public void startTimer(String name)
  {
	  TimerRequest request = TimerRequest.newBuilder().setName(name).build(); 
	  TimerResponse response; 
	  
	  try
	  {
		  response = blockingStub5.start(request); 
		  if(response.getIsSuccess())
			  System.out.println("Timer " + name + " has been created."); 
		  else
			  System.out.println("Timer '" + name + "' already exists. Has been going for " + response.getTimer().getSecondsPassed() + " seconds."); 
	  }catch(Exception e)
	  {
		  System.out.println("whoopsies in startTimer " + e); 
	  } 
  }
  
  public void checkTimer(String name)
  {
	  TimerRequest request = TimerRequest.newBuilder().setName(name).build(); 
	  TimerResponse response; 
	  
	  try
	  {
		  response = blockingStub5.check(request); 
		  if(response.getIsSuccess())
			  System.out.println("Timer " + name + " has been going for " + response.getTimer().getSecondsPassed() + " seconds."); 
		  else
			  System.out.println(response.getError()); 
	  }catch(Exception e)
	  {
		  System.out.println("whoopsies in checkTimer " + e); 
	  } 
  }
  
  public void stopTimer(String name)
  {
	  TimerRequest request = TimerRequest.newBuilder().setName(name).build(); 
	  TimerResponse response; 
	  
	  try
	  {
		  response = blockingStub5.close(request); 
		  if(response.getIsSuccess())
			  System.out.println("Timer " + name + " has been stopped after running for " + response.getTimer().getSecondsPassed() + " seconds."); 
		  else
			  System.out.println(response.getError()); 
	  }catch(Exception e)
	  {
		  System.out.println("whoopsies in checkTimer " + e); 
	  } 
  }
  
  public void showTimerList()
  {
	  try
	  {
		  Empty empt = Empty.newBuilder().build();
		  TimerList response; 
		  
		  response = blockingStub5.list(empt); 
		  System.out.println("Here are all running timers: "); 
		  for (Time timothy : response.getTimersList()) {
		      System.out.println(timothy.getName() + " " + timothy.getSecondsPassed() + " seconds.");
		    }
		  
	  }catch(Exception e)
	  {
		  System.out.println("Whoopsies in showTimerList " + e); 
	  }
  }
  
  public void noPath(int number)
  {
	  ThreeReq request = ThreeReq.newBuilder().setNumber(number).build(); 
	  ThreeRes response; 
	  
	  try {
		  response = blockingStub6.getResult(request); 
		  System.out.println("Your number " + number + " took " + response.getAnswer() + " calculations to complete."); 
	  }catch(Exception e)
	  {
		  System.out.println("Whoopsies in noPath " + e);
	  }
  }
  
  public void withPath(int number)
  {
	  ThreeReq request = ThreeReq.newBuilder().setNumber(number).build(); 
	  ThreePathRes response; 
	  
	  try {
		  response = blockingStub6.getPath(request); 
		  System.out.println("Here is the full path for the number " + number); 
		  System.out.print(number);
		  for (int x : response.getResultList()) {
		      System.out.print(" > " + x);
		    }
		  System.out.println(""); 
	  }catch(Exception e)
	  {
		  System.out.println("Whoopsies in noPath " + e);
	  }
  }

  public void getServices() {
    GetServicesReq request = GetServicesReq.newBuilder().build();
    ServicesListRes response;
    try {
      response = blockingStub3.getServices(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void findServer(String name) {
    FindServerReq request = FindServerReq.newBuilder().setServiceName(name).build();
    SingleServerRes response;
    try {
      response = blockingStub3.findServer(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public void findServers(String name) {
    FindServersReq request = FindServersReq.newBuilder().setServiceName(name).build();
    ServerListRes response;
    try {
      response = blockingStub3.findServers(request);
      System.out.println(response.toString());
    } catch (Exception e) {
      System.err.println("RPC failed: " + e);
      return;
    }
  }

  public static void main(String[] args) throws Exception {
	  
	  if (args.length > 7 || args.length < 6) {
      System.out
          .println("Expected arguments: <host(String)> <port(int)> <regHost(string)> <regPort(int)> <message(String)> <regOn(bool)>");
      System.exit(1);
    }
    int port = 9099;
    int regPort = 9003;
    String host = args[0];
    String regHost = args[2];
    String message = args[4];
    try {
      port = Integer.parseInt(args[1]);
      regPort = Integer.parseInt(args[3]);
    } catch (NumberFormatException nfe) {
      System.out.println("[Port] must be an integer");
      System.exit(2);
    }
    // Create a communication channel to the server, known as a Channel. Channels
    // are thread-safe
    // and reusable. It is common to create channels at the beginning of your
    // application and reuse
    // them until the application shuts down.
    String target = host + ":" + port;
    ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
        // Channels are secure by default (via SSL/TLS). For the example we disable TLS
        // to avoid
        // needing certificates.
        .usePlaintext().build();

    String regTarget = regHost + ":" + regPort;
    ManagedChannel regChannel = ManagedChannelBuilder.forTarget(regTarget).usePlaintext().build();
    try {

      // ##############################################################################
      // ## Assume we know the port here from the service node it is basically set through Gradle
      // here.
      // In your version you should first contact the registry to check which services
      // are available and what the port
      // etc is.

      /**
       * Your client should start off with 
       * 1. contacting the Registry to check for the available services
       * 2. List the services in the terminal and the client can
       *    choose one (preferably through numbering) 
       * 3. Based on what the client chooses
       *    the terminal should ask for input, eg. a new sentence, a sorting array or
       *    whatever the request needs 
       * 4. The request should be sent to one of the
       *    available services (client should call the registry again and ask for a
       *    Server providing the chosen service) should send the request to this service and
       *    return the response in a good way to the client
       * 
       * You should make sure your client does not crash in case the service node
       * crashes or went offline.
       */

      // Just doing some hard coded calls to the service node without using the
      // registry
      // create client
    	if(args.length == 7)
    	{
    		if(Integer.valueOf(args[6]) == 1)
    		{
	    		EchoClient client = new EchoClient(channel, regChannel);
	    		client.name = "auto_player"; 
    			System.out.println("Auto mode chosen. I will now give an example of each service.");
    			System.out.println("I put a small delay (5s) between each one so they don't all print out instantly"); 
    			System.out.println("First, echo service with string 'Howdy'");
    			client.askServerToParrot("Howdy"); 
    			TimeUnit.SECONDS.sleep(5); 
    			System.out.println("");
    			System.out.println("Next, requesting 2 jokes.");
    			client.askForJokes(2); 
    			TimeUnit.SECONDS.sleep(5); 
    			System.out.println("");
    			System.out.println("Next, playing a rock, paper, scissors game and choosing paper.");
    			client.playRPS(2); 
    			TimeUnit.SECONDS.sleep(5); 
    			System.out.println("");
    			System.out.println("Now let's check the rock, paper, scissors leaderboard");
    			client.getRPSleaderboard(); 
    			TimeUnit.SECONDS.sleep(5); 
    			System.out.println("");
    			System.out.println("Now I'll start 4 timers (named Andy, Jack, Katy, and Lucy) then request the timer list");
    			client.startTimer("Andy");
    			TimeUnit.SECONDS.sleep(2); 
    			client.startTimer("Jack");
    			TimeUnit.SECONDS.sleep(3); 
    			client.startTimer("Katy");
    			TimeUnit.SECONDS.sleep(1); 
    			client.startTimer("Lucy");
    			TimeUnit.SECONDS.sleep(5); 
    			client.showTimerList();
    			TimeUnit.SECONDS.sleep(5); 
    			System.out.println("");
    			System.out.println("Now we'll check on just the Katy timer.");
    			client.checkTimer("Katy"); 
    			TimeUnit.SECONDS.sleep(5); 
    			System.out.println("");
    			System.out.println("Last one for the timer service, let's shut down the Andy timer.");
    			client.stopTimer("Andy"); 
    			TimeUnit.SECONDS.sleep(5); 
    			System.out.println("Now I'll show the 3n+1 path for the number 15");
    			client.withPath(15);
    			TimeUnit.SECONDS.sleep(5); 
    			System.out.println("");
    			System.out.println("I hope you enjoyed these examples! :)");
    			
    		}
    		else
    			System.out.println("Have Pauto = 1 if you want to automatic version."); 
    	}
    	else
	    {
	    	boolean quit = false; 
	    	
	    	while(!quit)
	    	{
	    		
	    		System.out.println("Howdy, and welcome to Andy's Service Emporium."); 
	    		System.out.println("Here are your options!");
	    		System.out.println("[1] - Echo");
	    		System.out.println("[2] - Joke");
	    		System.out.println("[3] - Rock, Paper, Scissors");
	    		System.out.println("[4] - Timer"); 
	    		System.out.println("[5] - 3n + 1"); 
	    		System.out.println("[0] - quit"); 
	    		EchoClient client = new EchoClient(channel, regChannel);
	    		
	    		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	
	    		//gets the users choice of service
	    		String choiceS = reader.readLine();
	    		int choice = Integer.valueOf(choiceS); 
	    		switch(choice)
	    		{
	    		case 1: 
	    			System.out.println("Please enter a message."); 
	    			message = reader.readLine(); 
	    			client.askServerToParrot(message); 
	    			break; 
	    		case 2: 
	    			System.out.println("Would you like to [1] hear a joke or [2] send in a new joke?");
	    			choiceS = reader.readLine(); 
	    			choice = Integer.valueOf(choiceS); 
	    			if(choice == 1)
	    			{
	    				System.out.println("How many jokes would you like to hear?"); 
	    				choiceS = reader.readLine(); 
	    				client.askForJokes(Integer.valueOf(choiceS)); 
	    			}
	    			else
	    			{
	    				System.out.println("Please enter your new joke"); 
	    				choiceS = reader.readLine(); 
	    				client.setJoke(choiceS); 
	    				System.out.println("Please enter your new joke"); 
	    				
	    			}
	    			break; 
	    		case 3: 
	    			if(client.name.equals(""))
	    			{
	    				System.out.println("What is your name?"); 
	    				client.name = reader.readLine(); 
	    			}
	    			
	    			System.out.println("[1] rock, [2] paper, or [3] scissors?"); 
	    			choiceS = reader.readLine(); 
	    			client.playRPS(Integer.valueOf(choiceS)); 
	    			
	    			client.getRPSleaderboard(); 
	    			    			
	    			break; 
	    		case 4:
	    			System.out.println("Would you like to [1] start a timer, [2] check on a timer, [3] stop a timer, or [4] see available timers?");
	    			choiceS = reader.readLine(); 
	    			choice = Integer.valueOf(choiceS); 
	    			switch(choice)
	    			{
	    			case 1: 
	    				System.out.println("Okay, give your timer a name");
	    				choiceS = reader.readLine(); 
	    				client.startTimer(choiceS); 
	    				break; 
	    			case 2: 
	    				System.out.println("What timer do you want to check?"); 
	    				choiceS = reader.readLine(); 
	    				client.checkTimer(choiceS); 
	    				break; 
	    			case 3: 
	    				System.out.println("What timer do you want to stop?"); 
	    				choiceS = reader.readLine(); 
	    				client.stopTimer(choiceS); 
	    				break; 
	    			case 4: 
	    				client.showTimerList(); 
	    				break; 
	    			default: 
	    				System.out.println("Must choose a number between 1-4"); 
	    			}
	    			break; 
	    		case 5: 
	    			System.out.println("What number do you want to test?"); 
	    			choiceS = reader.readLine(); 
	    			choice = Integer.valueOf(choiceS); 
	    			System.out.println("And do you want to see the [1] full path or just the [2] result?");
	    			choiceS = reader.readLine(); 
	    			if(Integer.valueOf(choiceS) == 1)
	    				client.withPath(choice); 
	    			else if(Integer.valueOf(choiceS) == 2)
	    				client.noPath(choice); 
	    			else 
	    				System.out.println("Must choose 1 or 2."); 
	    			break; 
	    		case 0: 
	    			quit = true; 
	    			break; 
	    		default: 
	    			System.out.println("Please enter one of the listed options (e.g. 1, 2, or 3)"); 
	    		}
	    		// Reading data using readLine
	    		/*
	    		System.out.println("How many jokes would you like?"); // NO ERROR handling of wrong input here.
	    		String num = reader.readLine();
	
	    		// calling the joked service from the server with num from user input
	    		client.askForJokes(Integer.valueOf(num));
		
	    		// adding a joke to the server
	    		client.setJoke("I made a pencil with two erasers. It was pointless.");
		
	    		// showing 6 joked
	    		client.askForJokes(Integer.valueOf(6));
		
	    		// ############### Contacting the registry just so you see how it can be done
		
	    		if (args[5].equals("true")) { 
		 	      // Comment these last Service calls while in Activity 1 Task 1, they are not needed and wil throw issues without the Registry running
	    			// get thread's services
	    			client.getServices(); // get all registered services 
		
	    			// get parrot
	    			client.findServer("services.Echo/parrot"); // get ONE server that provides the parrot service
		        
	    			// get all setJoke
	    			client.findServers("services.Joke/setJoke"); // get ALL servers that provide the setJoke service
		        
	    			// get getJoke
	    			client.findServer("services.Joke/getJoke"); // get ALL servers that provide the getJoke service
		
		      	 // does not exist
	    			client.findServer("random"); // shows the output if the server does not find a given service
	    			*/
		      //}
    		}//end of while loop on line 363
    	}
    } finally {
      // ManagedChannels use resources like threads and TCP connections. To prevent
      // leaking these
      // resources the channel should be shut down when it will no longer be used. If
      // it may be used
      // again leave it running.
      channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
      regChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }
  }
}
