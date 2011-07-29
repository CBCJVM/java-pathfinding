package pipeep.geometry;

import pipeep.geometry.Node;
import pipeep.geometry.data.NodeProvider;
import pipeep.geometry.data.LineProvider;
import pipeep.arithmetic.Rounding;

import org.testng.annotations.*;

import java.util.Random;

public class LineTest {
	@Test(groups={"constructors"}, dataProvider="doubleNodes",
	      dataProviderClass=NodeProvider.class)
	public void basicConstructorTest(Node a, Node b) {
		Line l = new Line(a, b);
		assert l.getNodeA() == a && l.getNodeB() == b;
	}
	
	@Test(groups={"constructors"}, dataProvider="slopeConstruction",
	      dataProviderClass=LineProvider.class)
	public void slopeConstructorTest(Node nodeA, Double slopeD,
	                                 Double distanceD) {
		double slope = slopeD.doubleValue();
		double distance = distanceD.doubleValue();
		Line l = new Line(nodeA, slope, distance);
		assert Rounding.isEqual(l.getSlope(), slope) &&
		       Rounding.isEqual(l.getLength(), Math.abs(distance)) &&
		       nodeA == l.getNodeA();
	}
	
	@Test(dependsOnMethods={"basicConstructorTest", "delta[XY]Test"},
	      dataProvider="singleLine", dataProviderClass=LineProvider.class)
	public void lengthTest(Line l) {
		double length = l.getLength();
		double dx = Math.abs(l.getDeltaX());
		double dy = Math.abs(l.getDeltaY());
		assert length > dx && length > dy && length < dx + dy;
		assert Rounding.isEqual(length, l.getNodeA().getDistance(l.getNodeB()));
	}
	
	@Test(dependsOnMethods={"basicConstructorTest", "delta[XY]Test"},
	      dataProvider="singleLine", dataProviderClass=LineProvider.class)
	public void slopeTest(Line l) {
		assert l.getDeltaY() * l.getDeltaX() < 0. == l.getSlope() < 0.;
		double dx = Math.abs(l.getDeltaX());
		double dy = Math.abs(l.getDeltaY());
		assert dy > dx == Math.abs(l.getSlope()) > 1.0;
	}
	
	@Test(dependsOnMethods={"basicConstructorTest", "slopeTest"},
	      dataProvider="singleLine", dataProviderClass=LineProvider.class)
	public void perpendicularSlopeTest(Line l) {
		assert Rounding.isEqual(l.getPerpendicularSlope(), -1./l.getSlope());
	}
	
	@Test(groups={"delta"}, dataProvider="singleLine",
	      dataProviderClass=LineProvider.class)
	public void deltaXTest(Line l) {
		assert l.getNodeA().getX() < l.getNodeB().getX() == l.getDeltaX() > 0.;
	}
	
	@Test(groups={"delta"}, dataProvider="singleLine",
	      dataProviderClass=LineProvider.class)
	public void deltaYTest(Line l) {
		assert l.getNodeA().getY() < l.getNodeB().getY() == l.getDeltaY() > 0.;
	}
	
	@Test(groups={"delta"}, dataProvider="singleLine",
	      dataProviderClass=LineProvider.class)
	public void deltaNodeTest(Line l) {
		assert l.getDeltaNode().getX() == l.getDeltaX() &&
		       l.getDeltaNode().getY() == l.getDeltaY();
		assert Rounding.isEqual(new Node(0, 0).getDistance(l.getDeltaNode()),
		                        l.getLength());
	}
	
	@Test(groups={"intersection"}, dataProvider="singleLine",
	      dataProviderClass=LineProvider.class)
	public void crossIntersectionTest(Line l) {
		Line p = new Line(new Node(l.getNodeA().getX(), l.getNodeB().getY()),
		                  new Node(l.getNodeB().getX(), l.getNodeA().getY()));
		assert l.doesIntersect(p);
	}
	
	@Test(groups={"intersection"}, dataProvider="singleLine",
	      dataProviderClass=LineProvider.class)
	public void parallelIntersectionTest(Line l) {
		Node offset = new Node(0, 1.);
		Line p = new Line(l.getNodeA().add(offset), l.getNodeB().add(offset));
		assert !l.doesIntersect(p);
	}
	
	@Test(groups={"intersection"}, dataProvider="singleLine",
	      dataProviderClass=LineProvider.class)
	public void nearParallelIntersectionTest(Line l) {
		Node offsetA = new Node(0, 1.);
		Node offsetB = new Node(0, .5);
		Line p = new Line(l.getNodeA().add(offsetA), l.getNodeB().add(offsetB));
		assert !l.doesIntersect(p);
	}
}
