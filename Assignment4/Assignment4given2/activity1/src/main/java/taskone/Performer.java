/**
  File: Performer.java
  Author: Student in Fall 2020B
  Description: Performer class in package taskone.
*/

package taskone;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;

import org.json.JSONObject;

/**
 * Class: Performer 
 * Description: Threaded Performer for server tasks.
 */
class Performer {

    private StringList state;
    private Socket conn;
    protected Semaphore mutex = new Semaphore(1); 

    public Performer(Socket sock, StringList strings) {
        this.conn = sock;
        this.state = strings;
    }

    public JSONObject add(String str) {
    	
        JSONObject json = new JSONObject();
        json.put("datatype", 1);
        json.put("type", "add");
        
        try {
        	mutex.acquire();
        	state.add(str);
        }catch(Exception e){
        	e.printStackTrace();
        }finally {
        	mutex.release();
        }
        
        json.put("data", state.toString());
        return json;
    }
    
    public JSONObject pop()
    {
    	JSONObject json = new JSONObject(); 
    	json.put("datatype", 2); 
    	json.put("type", "pop"); 
    	json.put("data", state.pop()); 
    	return json; 
    }
    
    public JSONObject display()
    {
    	JSONObject json = new JSONObject(); 
    	json.put("datatype", 3); 
    	json.put("type", "display"); 
    	json.put("data", state.toString()); 
    	return json; 
    }
    
    public JSONObject count()
    {
    	JSONObject json = new JSONObject(); 
    	json.put("datatype", 4); 
    	json.put("type", "count"); 
    	json.put("data", Integer.toString(state.size())); 
    	return json; 
    }
    
    public JSONObject reverse(int num)
    {
    	JSONObject json = new JSONObject(); 
    	json.put("datatype", 5); 
    	json.put("type", "reverse"); 
    	if(num + 1 > state.size())
    	{
    		json.put("data", "List does not contain a " + num + " element. (remember - list starts at number 0)"); 
    	}
    	else
    	{
        	state.reverse(num); 
        	json.put("data", state.toString());   
    	}
    	return json; 
    }

    public static JSONObject error(String err) {
        JSONObject json = new JSONObject();
        json.put("error", err);
        return json;
    }

    public void doPerform() {
        boolean quit = false;
        OutputStream out = null;
        InputStream in = null;
        try {
            out = conn.getOutputStream();
            in = conn.getInputStream();
            System.out.println("Server connected to client:");
            while (!quit) {
                byte[] messageBytes = NetworkUtils.receive(in);
                JSONObject message = JsonUtils.fromByteArray(messageBytes);
                JSONObject returnMessage = new JSONObject();
   
                int choice = message.getInt("selected");
                    switch (choice) {
                    	//quit
                    	case (0): 
                    		quit = true; 
                    		break; 
                    	//add string
                        case (1):
                            String inStr = (String) message.get("data");
                            returnMessage = add(inStr);
                            break;
                        //pop
                        case (2): 
                        	returnMessage = pop(); 
                        	break;
                        //display the list
                        case (3): 
                        	returnMessage = display(); 
                        	break; 
                        //return number of elements
                        case (4): 
                        	returnMessage = count(); 
                        	break; 
                        //reserve a string
                        case (5): 
                        	int rev = Integer.parseInt((String)message.get("data"));
                        	returnMessage = reverse(rev); 
                        	break; 
                        default:
                            returnMessage = error("Invalid selection: " + choice 
                                    + " is not an option");
                            break;
                    }
                // we are converting the JSON object we have to a byte[]
                byte[] output = JsonUtils.toByteArray(returnMessage);
                NetworkUtils.send(out, output);
            }
            // close the resource
            System.out.println("close the resources of client ");
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
