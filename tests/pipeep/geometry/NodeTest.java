package pipeep.geometry;

import pipeep.geometry.data.NodeProvider;
import pipeep.arithmetic.Rounding;

import org.testng.annotations.*;

import java.util.Random;

public class NodeTest {
	@Test(dataProvider="doubleNodes", dataProviderClass=NodeProvider.class,
	      groups={"arithmetic"})
	public void subtractionTest(Node a, Node b) {
		Node subtracted = a.subtract(b);
		assert Rounding.isEqual(subtracted.getX(), a.getX() - b.getX());
		assert Rounding.isEqual(subtracted.getY(), a.getY() - b.getY());
	}
	
	@Test(dataProvider="doubleNodes", dataProviderClass=NodeProvider.class,
	      groups={"arithmetic"})
	public void additionTest(Node a, Node b) {
		Node added = a.add(b);
		assert Rounding.isEqual(added.getX(), a.getX() + b.getX());
		assert Rounding.isEqual(added.getY(), a.getY() + b.getY());
	}
	
	@Test(dataProvider="doubleNodes", dataProviderClass=NodeProvider.class,
	      dependsOnMethods={"subtractionTest"})
	public void distanceTest(Node a, Node b) {
		Node subtracted = a.subtract(b);
		double distance = a.getDistance(b);
		assert distance > 0.;
		assert distance > Math.abs(subtracted.getX());
		assert distance > Math.abs(subtracted.getY());
		assert distance < Math.abs(subtracted.getX()) +
		                  Math.abs(subtracted.getY());
	}
	
	@Test(dataProvider="singleNode", dataProviderClass=NodeProvider.class,
	      dependsOnGroups={"arithmetic"})
	public void equalityTest(Node n) {
		assert n.equals(n);
		assert n.equals(new Node(n.getX(), n.getY()));
		assert !n.equals(n.add(new Node(1., 1.)));
		assert !n.equals(n.subtract(new Node(1., 1.)));
		
		// test strictness
		Node tinyDiff = new Node(Rounding.EQUALITY_DIFFERENCE/2.,
		                         Rounding.EQUALITY_DIFFERENCE/2.);
		assert !n.equals(n.add(tinyDiff));
		assert !n.equals(n.subtract(tinyDiff));
		assert n.equals(n.add(tinyDiff), false);
		assert n.equals(n.subtract(tinyDiff), false);
	}
}
