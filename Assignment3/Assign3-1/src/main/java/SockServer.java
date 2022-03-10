import java.net.*;
import java.io.*;
import org.json.*;

import java.util.Random;

/**
 * A class to demonstrate a simple client-server connection using sockets.
 * Ser321 Foundations of Distributed Software Systems see
 * http://pooh.poly.asu.edu/Ser321
 * 
 * @author Tim Lindquist Tim.Lindquist@asu.edu Software Engineering, CIDSE,
 *         IAFSE, ASU Poly
 * @version August 2020
 * 
 * @modified-by David Clements <dacleme1@asu.edu> September 2020
 */

// This code is just very rought and you can keep it that way. It is only
// supposed to give you a rough skeleton with very rough error handling to make
// sure
// the server will not crash. Should in theory be done a bit cleaner but the
// main pupose here is to practice creating a protocol so I want to keep it as
// simple
// as possible.

public class SockServer {
  public static void main(final String args[]) {
    Socket sock;
    final int port = Integer.parseInt(args[0]); // no error handling on input arguments, you can assume they are correct
                                                // here

    try { // basic try catch just to catch exceptions
      // open socket
      final ServerSocket serv = new ServerSocket(port); // create server socket on port 8888
      while (true) {
        try { // this is not very pretty BUT it will keep the server running even if the
              // client sends a bad request which you might not handle in the server. The
              // server will print the
              // stacktrace and then wait for a new connection. The client will just stay in
              // wait for a response which it might never get

          System.out.println("Server ready for connections");

          sock = serv.accept(); // blocking wait

          // setup the object reading channel
          final ObjectInputStream in = new ObjectInputStream(sock.getInputStream());

          // get output channel
          final OutputStream out = sock.getOutputStream();
          // create an object output writer (Java only)
          final ObjectOutputStream os = new ObjectOutputStream(out);

          // Read request
          final String s = (String) in.readObject();

          // QUESTION: What could you do to make sure the server does not crash if the
          // client does not send a valid JSON?
          final JSONObject request = new JSONObject(s);

          System.out.println("Received the JSON " + request);

          final JSONObject response = new JSONObject();

          // this is the part where you would add your own service. Replace the two
          // services here with your services and try to handle the part where the client
          // might send a wrong request
          if (request.getString("type").equals("mocking")) {

            /*
             * Here I am very nit picky with the echo request, checking that it has a "data"
             * field, and checking that that field is actually a String. And returning an
             * appropriate message. If you send a wrong request in the "reverse" one, e.g.
             * you send an int instead of a String the try/catch block will catch it and the
             * client will just be in limbo since they will not get a reply. Not great way
             * to handle wrong requests.
             */

            if (request.has("data")) {
              if (request.get("data").getClass().getSimpleName().equals("String")) { // ok case
                response.put("type", "mocking");
                String info = request.getString("data"); 
                char[] mocker = new char[info.length()];
                String result = ""; 
                
                //puts the input string into a char array
                for(int x = 0; x < info.length(); x++)
                {
                	mocker[x] = info.charAt(x); 
                }
                mocker[0] = Character.toLowerCase(mocker[0]); 
                
                //makes every other letter uppercase
                for(int x = 0; x < mocker.length; x++)
                {
                	if(x%2==0)
                		mocker[x] = Character.toLowerCase(mocker[x]);
                	else
                		mocker[x] = Character.toUpperCase(mocker[x]);                		
                }
                
                //turns char array back into a string
                for(int x = 0; x < mocker.length; x++)
                {
                	result += mocker[x]; 
                }
                response.put("data", result);
              } else {
                response.put("type", "error");
                response.put("message", "no String");
              }
            } else {
              response.put("type", "error");
              response.put("message", "Data missing");
            }

          } else if (request.getString("type").equals("shuffle")) { 
        	  
        	  if (request.has("data1") && request.has("data2")) {
        		  
        		  if (request.get("data1").getClass().getSimpleName().equals("String") && request.get("data2").getClass().getSimpleName().equals("String")) { // ok case
        			    response.put("type", "shuffle"); 
        	            
        			    String string1 = request.getString("data1"); 
        			    String string2 = request.getString("data2");
        			    int string1Counter = 0; 
        			    int string2Counter = 0; 
        			    int decider = 0; 
        			    String result = ""; 
        			    Random rand = new Random(); 
        	            for(int x = 0; x < string1.length() + string2.length(); x++)
        	            {
        	            	decider = rand.nextInt(2); 
        	            	//will pick from the first word if there are remaining letters that
        	            	//haven't been used yet or the second word has no letters left over
        	            	if(decider == 0 && string1Counter < string1.length() || string2Counter == string2.length())
        	            	{
        	            		result += string1.charAt(string1Counter); 
        	            		string1Counter++; 
        	            	}
        	            	else
        	            	{
        	            		result += string2.charAt(string2Counter); 
        	            		string2Counter++; 
        	            	}
        	            }
        	            response.put("data", result);
        	            
                } else {
                  response.put("type", "error");
                  response.put("message", "no String");
                }
              } else {
                response.put("type", "error");
                response.put("message", "Data missing");
              }


          } else if (request.getString("type").equals("exit")) {
            response.put("type", "exit");
            response.put("data", "Good bye!");
            // write the whole message
            os.writeObject(response.toString());
            // make sure it wrote and doesn't get cached in a buffer
            os.flush();

            // closing off the connection
            // sock.close();
            // in.close();
            // out.close();
            // serv.close();
            // break;
          } else { // very basic error handling here and one type or error message if request type
                   // is not known
            response.put("type", "error");
            response.put("message", "Request type not known");
          }

          // write the whole message
          os.writeObject(response.toString());
          // make sure it wrote and doesn't get cached in a buffer
          os.flush();

        } catch (final Exception e) { // this is in case something in your protocol goes wrong then the server
                                      // connection should still stay open
          e.printStackTrace();
        }
      }

    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}