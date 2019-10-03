package sf;

import java.util.Random;
import java.util.stream.IntStream;

public class Generate {
	static Random rd= new Random(System.currentTimeMillis());
	public static int[] intArray(int length) {
		return IntStream.generate(()->rd.nextInt(1000)+1).limit(length).toArray();
	}
}
