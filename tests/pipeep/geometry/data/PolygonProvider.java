package pipeep.geometry.data;

import pipeep.geometry.Node;
import pipeep.geometry.Polygon;
import pipeep.geometry.data.NodeProvider;

import org.testng.annotations.*;

public class PolygonProvider {
	public static Polygon getRandomCCWTriangle() {
		// get 3 random points to form the triangle
		Node[] nList = new Node[3];
		for(int i = 0; i < nList.length; ++i) {
			nList[i] = NodeProvider.getRandomNode();
		}
		
		// sort them
		
	}
}
