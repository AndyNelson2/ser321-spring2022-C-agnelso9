/**
  File: Client.java
  Author: Student in Fall 2020B
  Description: Client class in package taskone.
*/

package taskone;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Base64;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.json.JSONObject;
import java.util.InputMismatchException; 
import java.net.ServerSocket;
import java.util.ArrayList; 

/**
 * Class: Client
 * Description: Client tasks.
 */
public class Node {

    public static void main(String[] args) throws IOException {
        String host;
        int leaderPort = -1;
        int myPort = -1; 
        Socket sock;
        int balance = 0; 
        ArrayList<int[]> debters = new ArrayList<int[]>(); 
        
        try {
            if (args.length != 4) {
                // gradle runClient -Phost=localhost -Pport=9099 -q --console=plain
                System.out.println("Usage: gradle runClient -Phost=localhost -Pport=9099");
                System.exit(0);
            }

            host = args[0];
            try {
                leaderPort = Integer.parseInt(args[1]);
                myPort = Integer.parseInt(args[2]); 
                balance = Integer.parseInt(args[3]); 
            } catch (NumberFormatException nfe) {
                System.out.println("[args] must be integers");
                System.exit(2);
            }
            
            
            sock = new Socket(host, leaderPort);
            OutputStream out = sock.getOutputStream();
            InputStream in = sock.getInputStream();
            JSONObject request = new JSONObject();
            
            request.put("option", "node"); 
            request.put("port", myPort); 
           
            System.out.println(request);
            NetworkUtils.send(out, JsonUtils.toByteArray(request));
            
            byte[] responseBytes = NetworkUtils.receive(in);
            JSONObject response = JsonUtils.fromByteArray(responseBytes);
                
            System.out.println(response.getString("greeting")); 
            ServerSocket server = new ServerSocket(myPort); 
            boolean hasLoan = false; 
            
            do {
            	sock = server.accept(); 
            	System.out.println("Bank connected to Node"); 
            	
            	out = sock.getOutputStream(); 
            	in = sock.getInputStream(); 
            	
            	responseBytes = NetworkUtils.receive(in); 
            	request = JsonUtils.fromByteArray(responseBytes); 
            	int[] client = null; 
            	
            	if(request.get("option").equals("credit"))
            	{
            		hasLoan = false; 
            	
            		//checks to see if client request credit already has a loan with this bank
            		for(int x = 0; x < debters.size(); x++)
            		{
            			client = debters.get(x); 
            			if(client[0] == request.getInt("id") && client[1] > 0)
            			{
            				hasLoan = true; 
            			}
            		}
            	
            		response = new JSONObject(); 
            		response.put("option", hasLoan); 
            		NetworkUtils.send(out, JsonUtils.toByteArray(response)); 
            	}
            	else if(request.get("option").equals("info"))
            	{
            		response = new JSONObject(); 
            		response.put("balance", balance); 
            		NetworkUtils.send(out, JsonUtils.toByteArray(response)); 
            	}
            	else if(request.get("option").equals("loanInfo"))
            	{
            		boolean inList = false; 
            		for(int x = 0; x < debters.size(); x++)
            		{
            			client = debters.get(x); 
            			if(client[0] == request.getInt("id"))
            			{
            				response.put("amount", client[1]); 
            				inList = true;
            				x = debters.size(); 
            			}
            		}
        			if(!inList)
        			{
        				response.put("amount", 0); 
        			}
        			NetworkUtils.send(out, JsonUtils.toByteArray(response)); 
            	}
            	else if(request.get("option").equals("amount"))
            	{
            		int[] newClient = new int[2]; 
            		newClient[0] = request.getInt("id"); 
            		newClient[1] = request.getInt("amount"); 
            		debters.add(newClient); 
            		balance -= newClient[1]; 
            		System.out.println("Bank#" + myPort + " loaned client " + newClient[0] + " $" + newClient[1] + ".");
            		System.out.println("New balance: $" + balance); 
            		response = new JSONObject(); 
            		response.put("response", "ok"); 
            		NetworkUtils.send(out, JsonUtils.toByteArray(response)); 
            	}
            	else if(request.get("option").equals("payback"))
            	{
            		int[] temp = null; 
            		for(int x = 0; x < debters.size(); x++)
            		{
            			temp = debters.get(x); 
            			if(temp[0] == request.getInt("id"))
            			{
            				client = temp; 
            				x = debters.size(); 
            			}
            		}
            		client[1] -= request.getInt("amount"); 
            		balance += request.getInt("amount"); 
            	}
            	
            } while (true);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}