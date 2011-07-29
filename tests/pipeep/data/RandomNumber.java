package pipeep.data;

import java.util.Random;

public class RandomNumber {
	public static Random rand = new Random();
	
	public static double getDouble(double stdDev) {
		return rand.nextGaussian() * stdDev;
	}
	
	public static double getDouble() {
		return getDouble(Settings.STD_DEV);
	}
}
