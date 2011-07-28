package pipeep.geometry;

import pipeep.arithmetic.Rounding;

/**
 * In graph theory, the "edge" of a graph. This immutable class consists of two
 * <code>Node</code> objects, forming a line segment. <em>Despite what the class
 * name may suggest, this is not an infinitely extending line, only a
 * segment.</em>
 */
public class Line {
	private Node nodeA, nodeB;
	
	/**
	 * Creates a line consisting of two <code>Node</code>s in the specified
	 * order. (from a strict equality standpoint, <code>new Line(a, b)</code> is
	 * not the same as <code>new Line(b, a)</code>. <code>nodeB</code> can often
	 * be seen as the endpoint, while <code>nodeA</code> can often be seen as
	 * where the line starts.
	 * 
	 * @param  nodeA  The starting point of the line segment.
	 * @param  nodeB  The ending point of the line segment.
	 */
	public Line(Node nodeA, Node nodeB) {
		this.nodeA = nodeA; this.nodeB = nodeB;
	}
	
	/**
	 * Creates a line given one fixed node as node <code>a</code>, and
	 * information about another node extending off of it. The other node,
	 * <code>b</code> is calculated from <code>a</code> and the additional
	 * information given.<p/>
	 * 
	 * Everything is fairly straightforward about this constructor's arguments,
	 * except for the sign of the distance. <code>nodeB</code> will be to the
	 * right of <code>nodeA</code> if it is positive, otherwise it will be to
	 * the left.
	 * 
	 * @param  nodeA     The fixed node to base things off of.
	 * @param  slope     The resultant slope of this <code>Line</code> object
	 * @param  distance  The resultant distance of this <code>Line</code>
	 *                   object, with the notable difference of a sign. If this
	 *                   is <code>&gt; 0</code>, <code>nodeB</code> will be to
	 *                   the right; if this is <code>&lt; 0</code>, it will be
	 *                   to the left of <code>nodeA</code>.
	 */
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
	
	/**
	 * A utility method of the two slope and distance based constructors.
	 */
	private static Node getOtherNode(Node n, double slope, double distance) {
		double ang = Math.atan(slope);
		return new Node(n.getX() + distance * Math.cos(ang),
		                n.getY() + distance * Math.sin(ang));
	}
	
	/**
	 * @return  The first node of two composing the line segment.
	 */
	public Node getNodeA() {
		return nodeA;
	}
	
	/**
	 * @return  The second node of two composing the line segment.
	 */
	public Node getNodeB() {
		return nodeB;
	}
	
	/**
	 * Gives the distance between the two nodes on the board, or the length of
	 * the line segment.
	 * 
	 * @return  The length of this line segment. Is always positive.
	 */
	public double getLength() {
		return nodeA.getDistance(nodeB);
	}
	
	/**
	 * Gives the node embodying the distances traveled by the node, horizontally
	 * and vertically. As a note,
	 * <code>new Node(0, 0).getDistance(l.getDeltaNode())</code> should give the
	 * same result as <code>l.getDistance()</code>, with possible rounding
	 * errors. Additionally, <code>l.getDeltaNode()</code> should give the same
	 * result as <code>new Node(l.getDeltaX(), l.getDeltaY())</code>.
	 */
	public Node getDeltaNode() {
		return nodeB.subtract(nodeA);
	}
	
	/**
	 * Gives the displacement traveled by the line horizontally to get from
	 * <code>nodeA</code> to <code>nodeB</code>. The result should be equivalent
	 * <code>getDeltaNode().getX()</code>. You can think of this as the "width"
	 * of the line segment.
	 * 
	 * @return  The horizontal displacement traveled by the line to get from
	 *          <code>nodeA</code> to <code>nodeB</code>. May be positive or
	 *          negative.
	 */
	public double getDeltaX() {
		return nodeB.getX() - nodeA.getX();
	}
	
	/**
	 * Gives the displacement traveled by the line vertically to get from
	 * <code>nodeA</code> to <code>nodeB</code>. The result should be equivalent
	 * <code>getDeltaNode().getY()</code>. You can think of this as the "height"
	 * of the line segment.
	 * 
	 * @return  The vertical displacement traveled by the line to get from
	 *          <code>nodeA</code> to <code>nodeB</code>. May be positive or
	 *          negative.
	 */
	public double getDeltaY() {
		return nodeB.getY() - nodeA.getY();
	}
	
	/**
	 * Solves for the slope of the line upon which this line segment is located.
	 * 
	 * @return  The slope of the line upon which this line segment is located.
	 *          In the case of a vertical line, the result may be
	 *          <code>Double.POSITIVE_INFINITY</code> or
	 *          <code>Double.NEGATIVE_INFINITY</code>, depending on the
	 *          direction of the line when traveling from <code>nodeA</code> to
	 *          <code>nodeB</code> being upwards or downwards, respectively.
	 */
	public double getSlope() {
		if(Rounding.isZero(getDeltaX())) {
			if(getDeltaY() > 0) {
				return Double.POSITIVE_INFINITY;
			}
			return Double.NEGATIVE_INFINITY;
		}
		return getDeltaY() / getDeltaX();
	}
	
	/**
	 * Finds a point on the line segment (excluding rounding errors) halfway
	 * between <code>nodeA</code> and <code>nodeB</code>.
	 * 
	 * @return  The midpoint of the line segment.
	 */
	public Node getMidpoint() {
		return new Node((getNodeA().getX() + getNodeB().getX()) * .5,
		                (getNodeA().getY() + getNodeB().getY()) * .5);
	}
	
	/**
	 * Gives the slope of a line perpendicular to the line that this segment is
	 * on.
	 */
	public double getPerpendicularSlope() {
		if(Rounding.isZero(getDeltaY())) {
			if(getDeltaX() > 0) {
				return Double.POSITIVE_INFINITY;
			}
			return Double.NEGATIVE_INFINITY;
		}
		return getDeltaY() / getDeltaX();
	}
	
	/**
	 * Solves for line intersection counting vertexes.
	 * 
	 * @return  <code>true</code> if there is an intersection,
	 *          <code>false</code> otherwise.
	 */
	public boolean doesIntersect(Line other) {
		return doesIntersect(other, true);
	}
	
	/**
	 * Uses the algorithm defined <a href=
	 * "http://www.bryceboe.com/2006/10/23/line-segment-intersection-algorithm/"
	 * >here,</code>
	 * along with some special handlers for edge-cases (no pun intended).
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
	
	/**
	 * A utility method for <code>doesIntersect</code>.
	 */
	private boolean ccw(Node a, Node b, Node c) {
		return (c.getY() - a.getY()) * (b.getX() - a.getX()) >
		       (b.getY() - a.getY()) * (c.getX() - a.getX());
	}
	
	/**
	 * Performs a test for equality between this line segment and another.
	 * 
	 * @param  other   The object to test equality against.
	 * @param  strict  If <code>true</code>, the lines are seen as equal only if
	 *                 their corresponding nodes are exactly identical.
	 *                 Otherwise, in a non-strict mode, node ordering is not
	 *                 required, and values can be slightly off, within a
	 *                 rounding window.
	 */
	public boolean equals(Line other, boolean strict) {
		if(this == other) { return true; }
		return nodeA.equals(other.getNodeA(), strict) &&
		       nodeB.equals(other.getNodeB(), strict) ||
		       !strict && nodeA.equals(other.getNodeB(), false) &&
		                  nodeB.equals(other.getNodeA(), false);
	}
	
	/**
	 * Performs a strict equality check, as of Java's guidelines for such a
	 * function.
	 */
	public boolean equals(Line other) {
		return equals(other, true);
	}
}
