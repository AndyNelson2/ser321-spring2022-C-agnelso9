/**
  File: Server.java
  Author: Student in Spring 2022
  Description: ThreadedServer class in package taskone.
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

/**
 * Class: Server
 * Description: Server tasks.
 */
class ThreadedServer extends Thread{

	private Socket socket; 
	private int id; 
	private Performer perform; 
	public ThreadedServer(Socket sock, int serverNumber, Performer performer)
	{
		this.socket = sock; 
		this.id = serverNumber; 
		this.perform = performer;
	}
    public static void main(String[] args) throws Exception {
    	
        int port;
        int threadNumber = 1; 
        Socket sock = null; 
        StringList strings = new StringList();

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
        try {
        	ServerSocket server = new ServerSocket(port);
        	System.out.println("Server Started...");
        	while (true) 
        	{
        		System.out.println("Accepting a Request...");
        		sock = server.accept();

        		Performer performer = new Performer(sock, strings);
        		ThreadedServer task = new ThreadedServer(sock, threadNumber, performer); 
        		task.start(); 
        		threadNumber++; 
        	}
            } catch (Exception e) {
                System.out.println("close socket of client ");
                sock.close();
                e.printStackTrace();
            } finally {
            	if(sock !=null) sock.close(); 
            }
    }
    
    public void run()
    {
    	this.perform.doPerform(); 
    }
}
