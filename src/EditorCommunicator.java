import java.io.*;
import java.net.Socket;
import java.util.Arrays;

/**
 * Handles communication to/from the server for the editor
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author Chris Bailey-Kellogg; overall structure substantially revised Winter 2014
 * @author Travis Peters, Dartmouth CS 10, Winter 2015; remove EditorCommunicatorStandalone (use echo server for testing)
 * @author Jake Markus, Dartmouth CS 10 student, Fall 2012, completing PS-6
 */
public class EditorCommunicator extends Thread {
	private PrintWriter out;		// to server
	private BufferedReader in;		// from server
	protected Editor editor;		// handling communication for

	/**
	 * Establishes connection and in/out pair
	 */
	public EditorCommunicator(String serverIP, Editor editor) {
		this.editor = editor;
		System.out.println("connecting to " + serverIP + "...");
		try {
			Socket sock = new Socket(serverIP, 4242);
			out = new PrintWriter(sock.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			System.out.println("...connected");
		}
		catch (IOException e) {
			System.err.println("couldn't connect");
			System.exit(-1);
		}
	}

	/**
	 * Sends message to the server
	 */
	public void send(String msg) {
		out.println(msg);
	}

	/**
	 * Keeps listening for and handling (your code) messages from the server
	 */
	public void run() {
		try {
			String line;
			// Handle data, update our sketch, and display to user
			while ((line = in.readLine()) != null) {

				editor.getSketch().reset(); //wipe what's out of date
				String [] shapes = line.split(","); //all the data into each shape's data

				for(String shape : shapes)
				{
					String[] data = shape.split(" "); //for each shape get each parameter
					if(data.length != 1)
					{
						System.out.println(data.length);
						System.out.println("Received: " + Arrays.toString(data));
						if(data[0].equals("polyline")) { //add each polyline
							editor.getSketch().addPolylineFromData(Arrays.copyOfRange(data, 1, data.length));
						}
						else { //add each ellipse, rect, and segment to our local sketch. It's function handles that
							editor.getSketch().addShapeFromData(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3]), Integer.parseInt(data[4]), Integer.parseInt(data[5]));
						}
					}
				}
				System.out.println("Sketch: " + editor.getSketch());
				editor.repaint(); //paint the new sketch
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			System.out.println("server hung up");
		}
	}	

	// Send editor requests to the server

	/**
	 * Sends a msg to the server to add a new shape in the correct format
	 * @param s the shape to add
	 */
	public void requestAddShape(Shape s)
	{
		this.out.println("add " + s.toString());
		System.out.println("Requesting: " + s.toString());
	}

	/**
	 * Sends a msg to server to recolor at a point, in the correct format
	 * @param x, the x-cord of the recolor request
	 * @param y the y-cord of the recolor request
	 * @param color, the int RGB representation of the recolor pigment
	 */
	public void requestRecolor(int x, int y, int color)
	{
		this.out.println("recolor " + x + " " + y + " " + color);
	}

	/**
	 * Sends a msg to the server to move a specific shape
	 * @param id, which shape to move
	 * @param dx How much to move it by in x
	 * @param dy, How much to move it by in y
	 */
	public void requestMoveBy(int id, int dx, int dy)
	{
		this.out.println("moveby " + id + " " + dx + " " + dy);
	}

	/**
	 * Sends a msg to the server to move delete at a click
	 * @param x, the x-cord of the deletion request
	 * @param y the y-cord of the deletion request
	 */
	public void requestDelete(int x, int y)
	{
		this.out.println("delete " + x + " " + y);
	}


}
