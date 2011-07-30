package pipeep.geometry;

import pipeep.arithmetic.Rounding;

public abstract class BasePolygon {
	// Lazily evaluated
	private Line[] lines = null;
	private double perimeter = -1.;
	private Node center = null;
	
	private Node[] nodes;
	
	public BasePolygon(Node ... nodes) {
		// if the polygon was already completed for us
		if(nodes[0].equals(nodes[nodes.length - 1])) {
			Node[] n = new Node[nodes.length - 1];
			System.arraycopy(nodes, 0, n, 0, nodes.length - 1);
			nodes = n;
		}
		assert nodes.length >= 3;
		this.nodes = nodes;
	}
	
	public Node[] getNodes() {
		return nodes;
	}
	
	public Line[] getLines() {
		if(lines == null) {
			lines = new Line[nodes.length];
			for(int i = 0; i < lines.length; ++i) {
				lines[i] = new Line(nodes[i], nodes[(i + 1) % nodes.length]);
			}
		}
		return lines;
	}
	
	public double getPerimeter() {
		if(perimeter < 0) {
			perimeter = 0;
			for(int i = 0; i < getLines().length; ++i) {
				perimeter += lines[i].getLength();
			}
		}
		return perimeter;
	}
	
	/**
	 * Based on/ported from some code donated ad hoc (public domain) by asarkar
	 * of #xkcd-cs on irc.foonetic.net<p\>
	 * 
	 * Ported from java to python, and then back again because I lost the
	 * original :-P
	 */
	public boolean containsNodeInArea(Node p) {
		boolean result = false;
		for(int i = 1; i <= nodes.length; ++i) {
			Node p1 = nodes[i % nodes.length];
			Node p2 = nodes[i - 1];
			if(p1.getX() < p.getX() && p2.getX() < p.getX()) {
				// the segment is strictly to the left of the test point, so it
				// can't intersect the ray cast in the positive x direction
				continue;
			} else if(p == p2) {
				// the point is one of the vertices
				return true;
			} else if(Rounding.isEqual(p1.getY(), p.getY()) &&
			          Rounding.isEqual(p2.getY(), p.getY())) {
				// the segment is horizontal
				if(p.getX() >= Math.min(p1.getX(), p2.getX()) &&
				   p.getX() <= Math.max(p1.getX(), p2.getX())) {
					// the point is on the segment
					return true;
				}
				// otherwise, don't count the segment
			} else if(p1.getY() > p.getY() && p2.getY() <= p.getY() ||
			          p2.getY() > p.getY() && p1.getY() <= p.getY()) {
				// non-horizontal upward edges include start, exclude end;
				// non-horizontal downward edges exclude start, include end
				double det = (p1.getX() - p.getX()) * (p2.getY() - p.getY()) -
				             (p1.getY() - p.getY()) * (p2.getX() - p.getX());
				if(Rounding.isZero(det)) {
					// point is on the translated segment
					return true;
				}
				if(p2.getY() < p1.getY()) {
					det *= -1;
				}
				if(det > 0) {
					// segment crosses if the determinant is positive
					result = !result;
				}
			}
		}
		return result;
	}
	
	public String toString() {
		String s = "[";
		for(Node i: getNodes()) {
			s += i.toString() + ", ";
		}
		return s.substring(0, s.length() - 2) + "]";
	}
	
	public abstract boolean doesIntersectLine(Line l);
	
	public boolean doesIntersectPolygon(BasePolygon poly) {
		for(Line i : getLines()) {
			for(Line k : poly.getLines()) {
				if(i.doesIntersect(k, true)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Node getCenter() {
		if(center == null) {
			Node sum = new Node(0, 0);
			for(Node n : nodes) {
				sum = sum.add(n);
			}
			center = new Node(sum.getX() / nodes.length,
			                  sum.getY() / nodes.length);
		}
		return center;
	}
}
