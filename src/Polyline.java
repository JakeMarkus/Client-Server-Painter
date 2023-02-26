import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

/**
 * A multi-segment Shape, with straight lines connecting "joint" points -- (x1,y1) to (x2,y2) to (x3,y3) ...
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2016
 * @author CBK, updated Fall 2016
 * @author Jake Markus, Dartmouth CS 10 student, Fall 2012, completing PS-6
 */
public class Polyline implements Shape {
	// TODO: YOUR CODE HERE
	Color color;
	ArrayList<Segment> segments; //Line is made up of many segments

	/**
	 * Makes a polyline with a start and a color. Will not be visable until next segment added
	 */
	public Polyline(Point p, Color c)
	{
		color = c;
		segments = new ArrayList<>();
		segments.add(new Segment(p.x, p.y, p.x, p.y, color));
	}


	/**
	 * Moves the entire line by moving each segment
	 * @param dx the amount to shift in x
	 * @param dy the amount to shift in y
	 */
	@Override
	public void moveBy(int dx, int dy) {
		for(Segment s : segments)
			s.moveBy(dx, dy);
	}


	/**
	 * Continues drawing this line by making a new segment from where we left off to this next point
	 * @param p the point we are drawing to
	 */
	public void add(Point p)
	{
		Point lastpoint = segments.get(segments.size()-1).getEnd(); //get the end point of the last segment
		segments.add(new Segment(lastpoint.x, lastpoint.y, p.x, p.y, color)); //add a new line with that and p
	}

	@Override
	public Color getColor() {
		return color;
	}

	/**
	 * Changes our color, plus each of our segments color
	 * @param color the color to change to
	 */
	@Override
	public void setColor(Color color) {
		this.color = color;
		for(Segment s : segments)
			s.setColor(color);
	}

	/**
	 * Checks if this polyline by checking if any of our segments contains it
	 * Runs through them all
	 * @param x the x cord of the click
	 * @param y the y cord of the click
	 */
	@Override
	public boolean contains(int x, int y) {

		for(Segment s : segments)
		{
			if(s.contains(x, y))
				return true; //if the segment contains it we don't have to check the rest
		}
		return false; //if none of our segments contains the click, then it's outside the polyline
	}

	/**
	 * Draws the polyline by having each segment draw itself
	 */
	@Override
	public void draw(Graphics g) {
		if(segments.size() == 0) return;
		g.setColor(color);

		for(Segment s : segments)
			s.draw(g);

	}

	/**
	 * @return a string representation for debugging
	 */
	@Override
	public String toString() {
		String out = "polyline " + color.getRGB() + " ";
		for (Segment s: segments)
			out += s.cordsString() + " ";

		return out;
	}
}
