public class Line {
	private Node nodeA, nodeB;
	
	public Line(Node nodeA, Node nodeB) {
		this.nodeA = nodeA; this.nodeB = nodeB;
	}
	
	public Line(Node nodeA, double slope, double distance) {
		this(nodeA, getOtherNode(nodeA, slope, distance));
	}
	
	/**
	 * This alternate constructor is only useful if you care about the order of
	 * the points inputted, which you probably don't.
	 */
	public Line(double slope, double distance, Node nodeB) {
		this(getOtherNode(nodeB, slope, distance), nodeB);
	}
	
	private static Node getOtherNode(Node n, double slope, double distance) {
		double ang = Math.atan(slope);
		return new Node(n.getX() + distance * Math.cos(ang),
		                n.getY() + distance * Math.sin(ang));
	}
	
	public Node getNodeA() {
		return nodeA;
	}
	
	public Node getNodeB() {
		return nodeB;
	}
	
	public double getLength() {
		return nodeA.getDistance(nodeB);
	}
	
	public Node getDeltaPoint() {
		return nodeA.subtract(nodeB);
	}
	
	public double getDeltaX() {
		return nodeA.getX() - nodeB.getX();
	}
	
	public double getDeltaY() {
		return nodeA.getY() - nodeB.getY();
	}
	
	public double getSlope() {
		if(Rounding.isZero(getDeltaX())) {
			if(getDeltaY() > 0) {
				return Double.POSITIVE_INFINITY;
			}
			return Double.NEGATIVE_INFINITY;
		}
		return getDeltaY() / getDeltaX();
	}
	
	public Node getMidpoint() {
		return new Node((getNodeA().getX() + getNodeB().getX()) * .5,
		                (getNodeA().getY() + getNodeB().getY()) * .5);
	}
	
	public double getPerpendicularSlope() {
		if(Rounding.isZero(getDeltaY())) {
			if(getDeltaX() > 0) {
				return Double.POSITIVE_INFINITY;
			}
			return Double.NEGATIVE_INFINITY;
		}
		return getDeltaY() / getDeltaX();
	}
	
	public boolean doesIntersect(Line other) {
		return doesIntersect(other, true);
	}
	
	/**
	 * Uses the algorithm defined here:
	 * http://www.bryceboe.com/2006/10/23/line-segment-intersection-algorithm/
	 * Along with some special handlers for edge-cases (no pun intended).
	 */
	public boolean doesIntersect(Line other, boolean vertexesCount) {
		// the algorithm doesn't handle parallel lines, so check that first
		// we'll cross-multiply the slopes, and then compare them
		if(Rounding.isEqual(getDeltaX() * other.getDeltaY(),
		                    getDeltaY() * other.getDeltaX())) {
			return false;
		}
		// define our shorthand
		Node a = nodeA;
		Node b = nodeB;
		Node c = other.getNodeA();
		Node d = other.getNodeB();
		return ccw(a, c, d) != ccw(b, c, d) && ccw(a, b, c) != ccw(a, b, d) &&
			   (vertexesCount || (a != c && a != d && b != c && b != d));
	}
	
	private boolean ccw(Node a, Node b, Node c) {
		return (c.getY() - a.getY()) * (b.getX() - a.getX()) >
		       (b.getY() - a.getY()) * (c.getX() - a.getX());
	}
	
	public boolean equals(Line other, boolean strict) {
		return nodeA.equals(other.getNodeA(), strict) &&
		       nodeB.equals(other.getNodeB(), strict) ||
		       !strict && nodeA.equals(other.getNodeB(), false) &&
		                  nodeB.equals(other.getNodeA(), false);
	}
	
	public boolean equals(Line other) {
		return equals(other, false);
	}
}
