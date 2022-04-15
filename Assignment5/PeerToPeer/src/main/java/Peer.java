import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList; 

import org.json.*;
/**
 * This is the main class for the peer2peer program.
 * It starts a client with a username and port. Next the peer can decide who to listen to. 
 * So this peer2peer application is basically a subscriber model, we can "blurt" out to anyone who wants to listen and 
 * we can decide who to listen to. We cannot limit in here who can listen to us. So we talk publicly but listen to only the other peers
 * we are interested in. 
 * 
 */

public class Peer {
	private String username;
	private BufferedReader bufferedReader;
	private ServerThread serverThread;
	ArrayList<Integer> ports = new ArrayList<Integer>(); 
	
	public Peer(BufferedReader bufReader, String username, ServerThread serverThread){
		this.username = username;
		this.bufferedReader = bufReader;
		this.serverThread = serverThread;
	}
	/**
	 * Main method saying hi and also starting the Server thread where other peers can subscribe to listen
	 *
	 * @param args[0] username
	 * @param args[1] port for server
	 */
	public static void main (String[] args) throws Exception {

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String username = args[0];
		if(Integer.valueOf(args[1]) > 7009 || Integer.valueOf(args[1]) < 7000)
		{
			System.out.println("Sorry, port number must be between 7000 and 7010!"); 
			System.exit(0);
		}
		System.out.println("Hello " + username + " and welcome! Your port will be " + args[1]);

		// starting the Server Thread, which waits for other peers to want to connect
		ServerThread serverThread = new ServerThread(args[1]);
		serverThread.start();
		Peer peer = new Peer(bufferedReader, args[0], serverThread);
		
		peer.updateListenToPeers(args[1]);
	}
	
	public void scanPorts() throws Exception
	{
		for(int x = 7000; x < 7011; x++)
		{
			if(!ports.contains(x))
			try
			{
				System.out.println(serverThread.getPortNumber() + " attempting to connect to port " + x);
				Socket socket = new Socket("localhost", x); 
				new ClientThread(socket).start(); 
				ports.add(x); 
				x = 7011; 
			}
			catch(Exception e)
			{
				
			}
		}
		System.out.println("Peers.java line 66: Ports connected to: " + ports.toString());
	}
	/**
	 * User is asked to define who they want to subscribe/listen to
	 * Basically to join the buddy group you have to be on ports 7000-7010
	 * So at the start I just have the peer scan all of those ports to say hello
	 *
	 */
	public void updateListenToPeers(String port) throws Exception {
		if(!ports.contains(Integer.valueOf(port)))
			ports.add(Integer.valueOf(port)); 
		for(int x = 7000; x < 7010; x++)
		{
			if(!ports.contains(x))
			try
			{
				Socket socket = new Socket("localhost", x); 
				new ClientThread(socket).start(); 
				ports.add(x); 
			}
			catch(Exception e)
			{
				
			}
		}
		System.out.println("Peers.java line 66: Ports connected to: " + ports.toString());
		
		/*if(serverThread.getPortNumber() != 7000)
		{
			Socket socket = new Socket("localhost", 7000); 
			new ClientThread(socket).start(); 
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		    JSONObject json = new JSONObject(bufferedReader.readLine());
		    String[] setupValue = json.getString("message").split(" "); 
		    System.out.println(json.getString("message")); 
		    if(!setupValue[0].equals(""))
		    	for(int x = 0; x < setupValue.length; x++)
		    	{
		    		System.out.println("attemping to connect to port " + setupValue[x]); 
		    		socket = new Socket("localhost", Integer.valueOf(setupValue[x])); 
		    		new ClientThread(socket).start(); 
		    	}
		}
		
		System.out.println("> Who do you want to listen to? Enter host:port");
		String input = bufferedReader.readLine();
		String[] setupValue = input.split(" ");
		for (int i = 0; i < setupValue.length; i++) {
			String[] address = setupValue[i].split(":");
			Socket socket = null;
			try {
				socket = new Socket(address[0], Integer.valueOf(address[1]));
				new ClientThread(socket).start();
			} catch (Exception c) {
				if (socket != null) {
					socket.close();
				} else {
					System.out.println("Cannot connect, wrong input");
					System.out.println("Exiting: I know really user friendly");
					System.exit(0);
				}
			}
		}
	*/
		askForInput();
	}
	
	/**
	 * Client waits for user to input their message or quit
	 *
	 * @param bufReader bufferedReader to listen for user entries
	 * @param username name of this peer
	 * @param serverThread server thread that is waiting for peers to sign up
	 */
	public void askForInput() throws Exception {
		try {
			System.out.println("> You can now start chatting (exit to exit)");
			while(true) {
				String message = bufferedReader.readLine();
				
				if (message.equals("exit")) {
					System.out.println("bye, see you next time");
					break;
				}else{
					// we are sending the message to our server thread. this one is then responsible for sending it to listening peers
					serverThread.sendMessage("{'username': '"+ username +"','message':'" + message + "'}");
				}
				if(serverThread.scanTime())
				{
					System.out.println("[System]: new member joining, wait one moment while he is added."); 
					scanPorts(); 
					System.out.println("> You can now start chatting (exit to exit)");
				}	
			}
				System.exit(0);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
