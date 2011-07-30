package pipeep.geometry;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;

/**
 * A polygon is created with a list of Nodes, and is internally represented
 * by list of Nodes and a list of lines, in the order that they are given.<p\>
 * 
 * Contains some ported code donated ad hoc (public domain) by asarkar of
 * #xkcd-cs on irc.foonetic.net
 */

public class Polygon extends BasePolygon{
	// lazily evaluated
	private Triangle[] triangles = null;
	private HashSet<Line> triangleLines = null;
	
	private boolean isCCW;
	
	/**
	 * Constructs a <code>Polygon</code> from a bunch of Nodes specified in ccw
	 * order
	 */
	public Polygon(Node ... nodes) {
		this(nodes, findIsCCW(nodes));
	}
	
	public Polygon(Node[] nodes, boolean isCCW) {
		super(makeCCW(nodes, isCCW));
	}
	
	private static boolean findIsCCW(Node[] nodes) {
		Node center = new Polygon(nodes).getCenter();
		double avgAngle = 0.;
		for(Node n : nodes) {
			Node diff = n.subtract(center);
			avgAngle += Math.atan2(diff.getX(), diff.getY());
		}
		return avgAngle > 0.;
	}
	
	private static Node[] makeCCW(Node[] nodes, boolean isCCW) {
		if(!isCCW) {
			// flip the Node array
			Node[] n = new Node[nodes.length];
			for(int i = 0; i < nodes.length; ++i) {
				n[nodes.length - i - 1] = nodes[i];
			}
			nodes = n;
		}
		return nodes;
	}
	
	public Triangle[] getTriangles() {
		if(triangles != null) { return triangles; }
		
		LinkedList<Node> nodesList = new LinkedList<Node>(
			Arrays.asList(getNodes())
		);
		LinkedList<Triangle> trianglesList = new LinkedList<Triangle>();
		// perform triangulation
		while(nodesList.size() >= 3) {
			Triangle t = new Triangle(nodesList.get(0), nodesList.get(1),
			                          nodesList.get(2));
			boolean isEar = t.isCCW() &&
				containsNodeInArea(
					new Line(nodesList.get(0), nodesList.get(2)
				).getMidpoint());
			for(int i = 3; isEar && i < nodesList.size(); ++i) {
				if(t.containsNodeInArea(nodesList.get(i))) {
					isEar = false;
				}
			}
			if(isEar) {
				trianglesList.add(t);
				nodesList.remove(1);
			} else {
				nodesList.add(nodesList.removeFirst());
			}
		}
		triangles = trianglesList.toArray(new Triangle[trianglesList.size()]);
		return triangles;
	}
	
	public HashSet<Line> getTriangleLines() {
		if(triangleLines != null) {
			return triangleLines;
		}
		
		triangleLines = new HashSet<Line>(getTriangles().length * 2 - 1);
		for(Triangle t : getTriangles()) {
			triangleLines.addAll(Arrays.asList(t.getLines()));
		}
		triangleLines.removeAll(Arrays.asList(getLines()));
		
		return triangleLines;
	}
	
	@Override
	public boolean doesIntersectLine(Line line) {
		return doesIntersectLine(line, false);
	}
	
	public boolean doesIntersectLine(Line line, boolean forceColinearityTest) {
		for(Line l : getLines()) {
			if(l.doesIntersect(line, false)) {
				return true;
			}
		}
		
		// see if we have to address colinearity
		List nodesList = Arrays.asList(getNodes());
		if(!forceColinearityTest && nodesList.contains(line.getNodeA()) &&
		   nodesList.contains(line.getNodeB())) {
			return false;
		}
		if(getTriangleLines().contains(line)) {
			return true;
		}
		for(Triangle t: getTriangles()) {
			if(t.doesIntersectLine(line)) {
				return true;
			}
		}
		return false;
	}
	
	public Polygon getExpanded(double outset) {
		Line[] lines = getLines();
		Node[] points = new Node[getNodes().length];
		double[] slopes = new double[getNodes().length];
		for(int i = 0; i < getNodes().length; ++i) {
			Line l = lines[i];
			double s = l.getSlope();
			double ps = l.getPerpendicularSlope();
			// Perpendicular Line
			// Solve for quads, we deal with y on line because that corresponds
			// with the sign of x on the perp
			Line pl = new Line(l.getMidpoint(), ps,
			                   l.getDeltaY() < 0 ? outset : -outset);
			points[i] = pl.getNodeB();
			slopes[i] = s;
		}
		// convert point_slopes to a series of points
		Node[] nodes = new Node[points.length];
		for(int i = 0; i < points.length; ++i) {
			int k = (i + 1) % points.length;
			nodes[i] = findIntersectionPoint(points[i], slopes[i],
			                                 points[k], slopes[k]);
		}
		return new Polygon(nodes);
	}
	
	/**
	 * Point in line a, slope of line a, point in line b, slope of line b
	 */
	private Node findIntersectionPoint(Node pa, double sa, Node pb,
	                                   double sb) {
		double inf = Double.POSITIVE_INFINITY;
		if(Math.abs(sa) == inf && Math.abs(sb) == inf || sa == sb) {
			throw new ArithmeticException("Parallel Lines");
		}
		if(Math.abs(sa) == inf) {
			return findIntersectionPointInf(pa, pb, sb);
		}
		if(Math.abs(sb) == inf) {
			return findIntersectionPointInf(pb, pa, sa);
		}
		double ba = pa.getY() - pa.getX() * sa;
		double bb = pb.getY() - pb.getX() * sb;
		double x = (bb - ba) / (sa - sb);
		return new Node(x, sa * x + ba);
	}
	
	private Node findIntersectionPointInf(Node pa, Node pb, double sb) {
		return new Node(pa.getX(), (pa.getX() - pb.getX()) * sb + pb.getY());
	}
	
	private Node getNearestOutterNode(Node n) throws Exception {
		throw new Exception("not yet implemented");
	}
}
