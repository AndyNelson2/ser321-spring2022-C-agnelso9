import java.net.*;
import java.io.*;
import java.util.Scanner;
import org.json.*;

/**
 * A class to demonstrate a simple client-server connection using sockets.
 * Ser321 Foundations of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Tim Lindquist Tim.Lindquist@asu.edu
 *         Software Engineering, CIDSE, IAFSE, ASU Poly
 * @version April 2020
 * 
 * @modified-by David Clements <dacleme1@asu.edu> September 2020
 * @modified-by Dr. Mehlhase Feb 2022
 */
class SockClient {
  public static void main (final String args[]) {
    Socket sock = null;
    final String host = args[1];
    final int port = Integer.parseInt(args[0]);
    final Scanner scanner = new Scanner(System.in);
    String input;

    try { 
      // open the connection
      sock = new Socket(host, port); // connect to host and socket on port 8000

      // get output channel
      final OutputStream out = sock.getOutputStream();

      // create an object output writer (Java only)
      final ObjectOutputStream os = new ObjectOutputStream(out);
      final ObjectInputStream in = new ObjectInputStream(sock.getInputStream());

      System.out.println("What would you like to do: Mocking Case (m) or Shuffle two words (s), type 'exit' to exit?");
      final String choice = scanner.nextLine();

      final JSONObject request = new JSONObject();

      if (choice.equals("m")) {
        System.out.println("What is the String you want to change?");
        input = scanner.nextLine();

        // write the whole message
        request.put("type", "mocking");
        request.put("data", input);

      } else if (choice.equals("s")) {
        System.out.println("What is your first word?");
        input = scanner.nextLine();
        
        // write the whole message
        request.put("type", "shuffle");
        request.put("data1", input);
        
        System.out.println("What is your second word?");
        input = scanner.nextLine();
        request.put("data2", input);

      } else if (choice.equals("exit")) {
        // write the whole message
        request.put("type", "exit");

      } else {
        // write the whole message
        request.put("type", "whooo");
      }

      // send JSON to server
      os.writeObject(request.toString());
      // make sure it wrote and doesn't get cached in a buffer
      os.flush();

      final String res = (String) in.readObject();
      final JSONObject response = new JSONObject(res); // what if res is not a correct JSON?

      // Basic info in case the server returned an error
      if (response.getString("type").equals("error")){
        System.out.println("There was an error: " + response.getString("message"));
      } else {
        System.out.println(response.getString("data"));
      }

      // close the complete connection after talking to the server
      sock.close(); // close socked after sending
      os.close();
      in.close();
      sock.close();
      scanner.close(); // close scanner
    } catch (final Exception e) {
      e.printStackTrace();
    }
  }
}