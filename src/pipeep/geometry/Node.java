package pipeep.geometry;

import pipeep.arithmetic.Rounding;

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
		return new Node(getX() + other.getX(), getY() + other.getY());
	}
	
	public Node subtract(Node other) {
		return new Node(getX() - other.getX(), getY() - other.getY());
	}
	
	public double getDistance(Node other) {
		return Math.hypot(getX() - other.getX(), getY() - other.getY());
	}
	
	public boolean equals(Node other, boolean strict) {
		return this == other ||
		       strict &&  getX() == other.getX() && getY() == other.getY() ||
		       !strict && Rounding.isEqual(getX(), other.getX()) &&
		                  Rounding.isEqual(getY(), other.getY());
	}
	
	public boolean equals(Object other) {
		return equals((Node)other, true);
	}
	
	public String toString() {
		return "(" + getX() + ", " + getY() + ")";
	}
	
	public int hashCode() {
		return (Double.valueOf(getX()).hashCode() >> 13) ^
		       Double.valueOf(getY()).hashCode();
	}
}
