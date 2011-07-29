package pipeep.geometry.data;

import pipeep.geometry.Node;
import pipeep.geometry.Line;
import pipeep.data.RandomNumber;

import org.testng.annotations.*;

import java.util.Random;

public class LineProvider {
	
	@DataProvider(name="singleLine")
	public static Object[][] getRandomLines() {
		Object[][] n = NodeProvider.getRandomNodePairs();
		Object[][] result = new Object[n.length][1];
		for(int i = 0; i < result.length; ++i) {
			result[i][0] = new Line((Node)n[i][0], (Node)n[i][1]);
		}
		return result;
	}
	
	@DataProvider(name="doubleLines")
	public static Object[][] getRandomLinePairs() {
		Object[][] a = getRandomLines();
		Object[][] b = getRandomLines();
		Object[][] result = new Object[a.length][2];
		for(int i = 0; i < result.length; ++i) {
			result[i][0] = a[i][0];
			result[i][1] = b[i][0];
		}
		return result;
	}
	
	@DataProvider(name="slopeConstruction")
	public static Object[][] getSlopeConstructionArgs() {
		Object[][] nodes = NodeProvider.getRandomNodes();
		Object[][] args = new Object[nodes.length][3];
		for(int i = 0; i < args.length; ++i) {
			args[i][0] = nodes[i][0];
			args[i][1] = new Double(RandomNumber.getDouble());
			args[i][2] = new Double(RandomNumber.getDouble());
		}
		return args;
	}
}
