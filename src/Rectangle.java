import java.awt.Color;
import java.awt.Graphics;

/**
 * A rectangle-shaped Shape
 * Defined by an upper-left corner (x1,y1) and a lower-right corner (x2,y2)
 * with x1<=x2 and y1<=y2
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Fall 2012
 * @author CBK, updated Fall 2016
 * @author Jake Markus, Dartmouth CS 10 student, Fall 2012, completing PS-6
 */
public class Rectangle implements Shape {
	private Color color;
	private int x1, y1, x2, y2; //Representing two points that define a rectangle, think of upper left/bottom right


	/**
	 * Initial one corner rectangle at point
	 */
	public Rectangle(int x1, int y1, Color color) {
		this.x1 = x1; this.x2 = x1;
		this.y1 = y1; this.y2 = y1;
		this.color = color;
	}

	/**
	 * Make a full rectange with two points and a color
	 */
	public Rectangle(int x1, int y1, int x2, int y2, Color color) {
		setCorners(x1, y1, x2, y2);
		this.color = color;
	}

	/**
	 * Moves this segment by adjusting the begin and end coords
	 * @param dx the amount to shift in x
	 * @param dy the amount to shift in y
	 */
	@Override
	public void moveBy(int dx, int dy) {
		x1 += dx; y1 += dy;
		x2 += dx; y2 += dy;
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Checks if a click contains this point
	 * @param x the x coord of the click
	 * @param y the y coord of this click
	 * @return a boolean, true if contained and false if not
	 */
	@Override
	public boolean contains(int x, int y) {
		//Is this point in between the two X coords, AND in between the two y coords?
		return ( Math.min(x1, x2) <= x && x <= Math.max(x1, x2) ) && (Math.min(y1, y2) <= y && y <= Math.max(y1, y2));
	}

	/**
	 * @draws this segment to the screen
	 */
	@Override
	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect(x1, y1, x2-x1, y2-y1);
	}

	/**
	 * @return a string representation
	 */
	public String toString() {
		return "rectangle "+x1+" "+y1+" "+x2+" "+y2+" "+color.getRGB();
	}

	/**
	 * Sets the two points which define this rectange to x/y coord pairs
	 */
	public void setCorners(int x1, int y1, int x2, int y2) {
		// Ensure correct upper left and lower right. Not that it changes anything but my soul
		this.x1 = Math.min(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.x2 = Math.max(x1, x2);
		this.y2 = Math.max(y1, y2);
	}

}
