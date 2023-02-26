import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

/**
 * Handles communication between the server and one client, for SketchServer
 *
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012; revised Winter 2014 to separate SketchServerCommunicator
 * @author Jake Markus, Dartmouth CS 10 student, Fall 2012, completing PS-6
 */
public class SketchServerCommunicator extends Thread {
	private Socket sock;					// to talk with client
	private BufferedReader in;				// from client
	private PrintWriter out;				// to client
	private SketchServer server;			// handling communication for

	private int id; //the ID of this user/connection. Just for logging purposes. Increments with every connection
	public SketchServerCommunicator(Socket sock, SketchServer server, int id) {
		this.sock = sock;
		this.server = server;
		this.id = id;
	}

	/**
	 * Sends a message to the client
	 * @param msg
	 */
	public void send(String msg) {
		out.println(msg);
	}
	
	/**
	 * Keeps listening for and handling (your code) messages from the client
	 */
	public void run() {
		try {
			System.out.println("someone connected");
			
			// Communication channel
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new PrintWriter(sock.getOutputStream(), true);

			// Tell the client the current state of the world
			out.println(server.getSketch().transmitFormat());

			// Keep getting and handling messages from the client
			String line;
			while ((line = in.readLine()) != null) {

				String[] data = line.split(" "); //splits the command into components

				System.out.println("RECEIVED FROM " + id + ": " + Arrays.toString(data)); //logs the request

				if(data[0].equals("add") && data[1].equals("polyline")) //handles the: add polyline command
				{
					System.out.println("Adding Polyline!"); //passes the list of points following it to the server sketch
					server.getSketch().addPolylineFromData(Arrays.copyOfRange(data, 2, data.length));
				}
				//handles adding any other shape by passing the data to the server sketch
				else if(data[0].equals("add"))
				{
					System.out.println("Adding!");
					server.getSketch().addShapeFromData(data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5]), Integer.parseInt(data[6]));

				}
				//Same above, but with the recolor command
				if(data[0].equals("recolor"))
				{
					server.getSketch().recolor(new Point(Integer.parseInt(data[1]), Integer.parseInt(data[2])), new Color(Integer.parseInt(data[3])));
				}

				//Same above, but with the moveby command
				if(data[0].equals("moveby"))
				{
					server.getSketch().moveBy(Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]));
				}
				//same above, but with the delete command
				else if(data[0].equals("delete"))
				{
					server.getSketch().deleteAtPoint(new Point(Integer.parseInt(data[1]), Integer.parseInt(data[2])));
				}

				//logs the new version of the sketch after changes
				System.out.println("New Sketch: " + server.getSketch());
				//since we just changed the sketch, we need to send it to clients
				server.updateStateALLCLIENTS();
			}
			System.out.println("#" + id + " hung up");

			// Clean up -- note that also remove self from server's list so it doesn't broadcast here
			server.removeCommunicator(this);
			out.close();
			in.close();
			sock.close();
		}

		/**
		 * For some reason, in.readLine() throws a socket exception rather than becoming null when a client hangs up
		 * This happens in HelloClient and HelloServer too, so I know it's not a problem with my code.
		 * If the above code works for you, great. But the code below handles hang-ups on my machine by catching that error
		 * and tidying up the loose ends.
		 */
		catch (SocketException e)
		{
			//remove ourself from the server
			System.out.println("#" + id + " hung up");
			server.removeCommunicator(this);

			//Close the readers/sock if we can
			try {
				out.close();
				in.close();
				sock.close();
			}
			catch (IOException uhoh) {
				uhoh.printStackTrace();
			}
		}

		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates our client with the current sketch state
	 */
	public void updateState() {
		out.println(server.getSketch().transmitFormat());
	}
}
