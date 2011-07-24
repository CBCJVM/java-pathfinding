/**
 * A general purpose 2d point/node class. It is immutable by design.
 */
public class Node {
	private double x, y;
	
	public Node(double x, double y) {
		this.x = x; this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public Node add(Node other) {
		return new Node(x + other.x, y + other.y);
	}
	
	public Node subtract(Node other) {
		return new Node(x - other.x, y - other.y);
	}
	
	public double getDistance(Node other) {
		return Math.hypot(x - other.x, y - other.y);
	}
	
	public boolean equals(Node other, boolean strict) {
		return this == other ||
		       strict &&  x == other.x && y == other.y ||
		       !strict && Rounding.isEqual(x, other.x) &&
		                  Rounding.isEqual(y, other.y);
	}
	
	public boolean equals(Object other) {
		return equals((Node)other, false);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	public int hashCode() {
		return (Double.valueOf(x).hashCode() >> 13) ^
		       Double.valueOf(y).hashCode();
	}
}
