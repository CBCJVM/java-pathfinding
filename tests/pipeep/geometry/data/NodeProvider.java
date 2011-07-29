package pipeep.geometry.data;

import pipeep.geometry.Node;
import pipeep.data.RandomNumber;
import pipeep.data.Settings;

import org.testng.annotations.*;

public class NodeProvider {
	@DataProvider(name="singleNode")
	public static Object[][] getRandomNodes() {
		return getRandomNodes(1);
	}
	
	@DataProvider(name="doubleNodes")
	public static Object[][] getRandomNodePairs() {
		return getRandomNodes(2);
	}
	
	private static Object[][] getRandomNodes(int width) {
		return getRandomNodes(Settings.DATA_POINTS, width);
	}
	
	private static Object[][] getRandomNodes(int count, int width) {
		Node[][] data = new Node[count][width];
		for(int i = 0; i < count; ++i) {
			data[i] = new Node[width];
			for(int k = 0; k < width; ++k) {
				data[i][k] = getRandomNode();
			}
		}
		return data;
	}
	
	private static Node getRandomNode() {
		return getRandomNode(Settings.STD_DEV);
	}
	
	private static Node getRandomNode(double stdDev) {
		return new Node(RandomNumber.getDouble(stdDev),
		                RandomNumber.getDouble(stdDev));
	}
}
