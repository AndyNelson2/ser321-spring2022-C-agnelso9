package Assignment3Starter;
import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Set;
import java.util.Stack;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import java.awt.image.BufferedImage;
import java.io.*;
import org.json.*;


/**
 * A class to demonstrate a simple client-server connection using sockets.
 * Ser321 Foundations of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version August 2020
 * 
 * 
 * Describing the simple protocol here
 * 
 * request (starting the connection)
 * 	type: start
 * Response: 
 * OK
 *  type: hello
 *  image: <String> encoded image
 *  value: <String> asking for name of player
 * Error
 * 	type: error
 *  message: <String> Error message
 * 
 *
 * @modified-by David Clements <dacleme1@asu.edu> September 2020
 */
public class SockServer {
	static Stack<String> imageSource = new Stack<String>();

	public static void main (String args[]) {
		Socket sock;
		try {
			
			//opening the socket here, just hard coded since this is just a bas example
			ServerSocket serv = new ServerSocket(8888); // TODO, should not be hardcoded
			System.out.println("Server ready for connetion");

			// placeholder for the person who wants to play a game
			String name = "";
			String currentImage = ""; 
			int points = 0;
			int clientID = 0; 
			int questions = 0; 
			int answers = 0; 
			int imageState = 0; 

			Instant start = Instant.now();
			Instant end;
			Duration elapsed;
			long time;
			boolean timer = false; 
			
			// read in one object, the message. we know a string was written only by knowing what the client sent. 
			// must cast the object from Object to desired type to be useful
			while(true) {
				sock = serv.accept(); // blocking wait
				if(!timer)
				{
					start = Instant.now(); 
					timer = !timer; 
				}
				// setup the object reading channel

				// could totally use other input outpur streams here
				ObjectInputStream in = new ObjectInputStream(sock.getInputStream());
				OutputStream out = sock.getOutputStream();

				String s = (String) in.readObject();
				System.out.println(s);
				JSONObject json = new JSONObject(s); // the requests that is received

				JSONObject response = new JSONObject();

				if (json.getString("type").equals("start")){
					
					System.out.println("- Got a start");
				
					response.put("type","hello" );
					response.put("value","Hello, please tell me your name." );
					sendPic("img/hi.png", response); // calling a method that will manipulate the image and will make it send ready
				}
				else if(json.getString("type").equals("name"))
				{
					name = json.getString("name"); 
					response.put("type", "greetings"); 
					response.put("value", "Pleasure to meet you, " + name + ". How many questions do you want?");
					sendPic("img/questions.jpg", response); 
				}
				else if(json.getString("type").equals("questions"))
				{
					try 
					{
						questions = Integer.parseInt(json.getString("questions")); 
						response.put("type", "ready?"); 
						response.put("value", "Okay, " + name + ", starting a game with " + json.getString("questions") + " questions.");
						response.put("value2", "Type 'start' when you're ready to play."); 
					}
					catch(Exception e)
					{
						response.put("type", "greetings"); 
						response.put("value", "Please enter in a number."); 
					}
					
				}
				else if(json.getString("type").equals("startGame"))
				{
					if(json.getString("response").equalsIgnoreCase("start"))
					{
						response.put("type", "gameTime"); 
						response.put("value", "Guess what image this is."); 
						currentImage = sendRandomPic(response); 
						imageState = 1; 
					}
					else
					{
						response.put("type", "ready?"); 
						response.put("value", "Type 'start' when you're ready to play."); 
					}
				}
				else if(json.getString("type").equals("answer"))
				{
					if(json.getString("answer").equalsIgnoreCase(currentImage))
					{
						end = Instant.now(); 
						elapsed = Duration.between(start,  end); 
						time = elapsed.toSeconds(); 
						answers++; 
						if(answers == questions)
						{
							response.put("type", "gameOver");
							response.put("value", "correct!"); 
							response.put("value2", "You win!!!!"); 
							sendPic("img/win.jpg", response); 
						}
						else if(time < 30)
						{
							response.put("type", "gameTime");
							response.put("value", "correct!"); 
							response.put("value2", "Next image, " + (questions-answers) + " more to go.");
							currentImage = sendRandomPic(response); 
							imageState = 1; 
						}
						else
						{
							response.put("type", "gameOver");
							response.put("value", "correct!"); 
							response.put("value2", "Time is up!"); 
								response.put("value3", "You lose!"); 
								sendPic("img/lose.jpg", response);	
						}
					
					}
					else if(json.getString("answer").equalsIgnoreCase("next"))
					{
						response.put("type", "gameTime"); 
						response.put("value", "Okay, new image sent"); 
						imageState = 1; 
						currentImage = sendRandomPic(response); 
					}
					else if(json.getString("answer").equalsIgnoreCase("more"))
					{
						if(imageState < 3)
						{
							response.put("type", "gameTime"); 
							response.put("value", "More of the image shown!"); 
							imageState++; 
							sendPic("img/" + currentImage + "/" + currentImage + Integer.toString(imageState) + ".png", response); 
						}
						else
						{
							response.put("type", "gameTime"); 
							response.put("value", "Image does not zoom out more. Type 'next' to get a new image."); 
						}
					}
					else
					{
						end = Instant.now(); 
						elapsed = Duration.between(start, end); 
						time = elapsed.toSeconds(); 
						if(time > 30)
						{
							response.put("type", "gameOver"); 
							response.put("value", "Wrong!! Time's up!! You lose!"); 
							sendPic("img/lose.jpg", response); 
						}
						else if(imageState == 3)
						{
							response.put("type", "gameTime"); 
							response.put("value", "Wrong!!! Next image - " + (questions = answers) + " more to go.");
							currentImage = sendRandomPic(response); 
							imageState = 1; 
						}
						else
						{
							response.put("type", "gameTime"); 
							imageState++; 
							response.put("value", "Wrong! Image zoomed out. Guess again."); 
							sendPic("img/" + currentImage + "/" + currentImage + Integer.toString(imageState) + ".png", response); 
						}
						
					}
				}
				else if(json.getString("type").equals("restart"))
				{
					if(json.getString("restart").equalsIgnoreCase(name))
					{
						response.put("type", "greetings"); 
						response.put("value", "Pleasure to meet you, " + name + ". How many questions do you want?");
						sendPic("img/questions.jpg", response); 
						answers = 0; 
						start = Instant.now(); 
					}
					else if(json.getString("restart").equalsIgnoreCase("quit"))
					{
						response.put("type", "goodbye"); 
						response.put("value", "Okie, thanks for playing, " + name + "!"); 
					}
					else
					{
						response.put("type", "continue"); 
						response.put("value", "Type your name to start a new game or 'quit' to exit."); 
					}
				}
				else if(json.getString("type").equals("finished"))
				{
					response.put("type", "continue"); 
					response.put("value", "Oh, you're still here? Type your name to start another game"); 
				}
				else {
					System.out.println("not sure what you meant");
					response.put("type","error" );
					response.put("value","unknown response" );
				}
				PrintWriter outWrite = new PrintWriter(sock.getOutputStream(), true); // using a PrintWriter here, you could also use and ObjectOutputStream or anything you fancy
				outWrite.println(response.toString());
			}
			
		} catch(Exception e) {e.printStackTrace();}
	}

	/* TODO this is for you to implement, I just put a place holder here */
	public static JSONObject sendPic(String filename, JSONObject obj) throws Exception {
		File file = new File(filename);

		if (file.exists()) {
			// import image
			// I did not use the Advanced Custom protocol
			// I read in the image and translated it into basically into a string and send it back to the client where I then decoded again
			obj.put("image", filename);
		} 
		return obj;
	}
	
	public static String sendRandomPic(JSONObject obj) throws Exception
	{
		//used for randomly picking an image to send
		Random rand = new Random(); 
		int pic = rand.nextInt(6)+1; 
		String filename = ""; 
		
		switch (pic)
		{
		case 1: 
			filename = "img/car/car1.png"; 
			sendPic(filename, obj); 
			return "car"; 
		case 2: 
			filename = "img/cat/cat1.png"; 
			sendPic(filename, obj); 
			return "cat"; 
		case 3: 
			filename = "img/cucumber/cucumber1.png"; 
			sendPic(filename, obj); 
			return "cucumber"; 
		case 4: 
			filename = "img/hat/hat1.png"; 
			sendPic(filename, obj); 
			return "hat"; 
		case 5: 
			filename = "img/pug/pug1.png"; 
			sendPic(filename, obj); 
			return "pug"; 
		case 6: 
			filename = "img/puppy/puppy1.png"; 
			sendPic(filename, obj); 
			return "puppy"; 
		default:
			filename = "img/lose.jpg"; 
			sendPic(filename, obj); 
			return filename; 
		}
	}
}
