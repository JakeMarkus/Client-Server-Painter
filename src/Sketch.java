import java.awt.*;
import java.util.*;

/**
 * Stores the state of a drawing for a collabrative Google Docs Project, PS6
 * Can be used by either a client or a server
 *
 * @author Jake Markus, Dartmouth CS 10 student, Fall 2012, completing PS-6
 */
public class Sketch
{
    private TreeMap<Integer, Shape> shapes; //a treemap to handle depth

    public Sketch() {
        shapes = new TreeMap<>();
    }

    /**
     * Adds any shape but a polyline
     * @param type, the tag of the shape to add
     * @param x1 starting x cord
     * @param y1 starting y cord
     * @param x2 finishing x cord
     * @param y2 finishing y cord
     * @param color RGB int representation of color
     */
    public synchronized void addShapeFromData(String type, int x1, int y1, int x2, int y2, int color)
    {
        //makes the correct shape based on the tag, with the associated data
        if(type.equals("ellipse"))
        {
            addShape(new Ellipse(x1, y1, x2, y2, new Color(color)));
        }
        else if (type.equals("rectangle"))
        {
            addShape(new Rectangle(x1, y1, x2, y2, new Color(color)));
        }
        else if(type.equals("segment"))
        {
            addShape(new Segment(x1, y1, x2, y2, new Color(color)));
        }
    }

    /**
     * Makes a Polyline from a list of cords stored in a String[], plus the color as the first index
     * @param ps, the String[] of data. example: [COLOR, x1, y1, x2, y2, x3, y3, x4, y4]
     */
    public synchronized void addPolylineFromData(String[] ps)
    {
        Point[] points = new Point[(ps.length-1)/2]; //Points will combine 2 cords, but not the 1 color

        for(int i = 1; i < ps.length -1; i+=2) //jump from x cord to x cord, and make a point with the following y cord
            points[i/2] = new Point(Integer.parseInt(ps[i]), Integer.parseInt(ps[i+1]));

        Polyline line = new Polyline(points[0], new Color(Integer.parseInt(ps[0]))); //instantiate the line with the firstpoint/color
        for(int i = 1; i < points.length; i++)
        {
            line.add(points[i]); //add each point we found
        }
        addShape(line); //add the finished line

    }

    /**
     * Adds a shape to the map with the next available key
     * @param s, the Shape to add
     */
    public synchronized void addShape(Shape s)
    {
        //If we have no shapes, add it at 0
        if(shapes.keySet().size() ==0) {
            shapes.put(0, s);
            return;
        }

        //otherwise, run through each shape and fill the next open spot
        int counter = 0;
        for(int id : shapes.keySet()) {
            if(id != counter) {
                shapes.put(counter, s);
                return;
            }
            counter ++;
        }

        shapes.put(counter, s);
    }

    /**
     * Recolors the correct shape at a click
     * @param p, the point of the click
     * @param c, the color the shape should be changed to
     */
    public synchronized void recolor(Point p, Color c)
    {
        int id = getIdAtClick(p); //get the id
        if(id != -1)
            shapes.get(id).setColor(c); //only change the color if a valid id
    }

    /**
     * Gets a valid shape ID at a click point
     * @param p, the cords of the click
     * @return an int ID, -1 if no shape at click
     */
    public int getIdAtClick(Point p)
    {
        for(int i : shapes.descendingKeySet()) { //go through each shape from front to back
            if (shapes.get(i).contains(p.x, p.y)) {
                return i; //return the index if the shape contains it.
            }
        }
        return -1; //If all shapes invalid, return -1
    }

    /**
     * Moves a shape gotten by ID a certain amount
     * @param id, the number of the shape
     * @param dx, the amount to change in X
     * @param dy, the amount to change in y
     */
    public synchronized void moveBy(int id, int dx, int dy)
    {
        shapes.get(id).moveBy(dx, dy); //calls the correct shape's moveBy function
    }

    /**
     * Handles a deletion request from a user at a certain point
     * Deletes the shape, and decrements the higher IDs to fill the hole
     * All clients will get the new IDs bc they are automatically updated
     * @param p, the point clicked with delete enabled
     */
    public synchronized void deleteAtPoint(Point p)
    {
        int id = getIdAtClick(p);

        //If we've clicked on nothing, don't do anything
        if(id != -1) {

            shapes.remove(id); //first remove the shape

            //create a copy of the current keys to avoid a concurrentmodificationexception
            ArrayList<Integer> oldKeys = new ArrayList<>();
            oldKeys.addAll(shapes.navigableKeySet());

            //run through all keys and decrement if higher
            for(int i : oldKeys) {
                if(i > id)
                    shapes.put(i-1, shapes.get(i));
            }

            //if we decremented the last key/shape, then the data might still be in the last slot. Delete it
            if(id < shapes.navigableKeySet().size()-1)
                shapes.remove(shapes.navigableKeySet().last());
        }

    }

    /**
     * @return the Treemap of Shapes, int ID->shape
     */
    public TreeMap<Integer, Shape> getShapes() {
        return shapes;
    }

    /**
     * simple wipes ths shapes in this sketch. Useful for updating local sketch in clients
     */
    public void reset() {
        shapes = new TreeMap<>();
    }

    /**
     * Displays this sketch by drawing each shape from back to front
     */
    public void draw(Graphics g) {
        //NavigableKeySet to draw from first to last
        for(int i : shapes.navigableKeySet())
            shapes.get(i).draw(g);
    }

    /**
     * NOT FOR TRANSMISSION
     * Represents this entire sketch in a more viewable format.
     * @return String with each shape
     */
    @Override
    public String toString() {
        String output = "";

        for(int key : shapes.navigableKeySet())
            output += key + ": " + shapes.get(key) + "\n";
        return output;
    }

    /**
     * Represents this entire sketch in a string packet.
     * @return String with each shape seperated by "," and each data point seperated by " "
     */
    public String transmitFormat()
    {
        String output = "";

        for(int key : shapes.navigableKeySet())
            output += shapes.get(key) + ",";
        return output;
    }
}
