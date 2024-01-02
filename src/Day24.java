import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day24 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day24.txt"));
		
		List<Integer> weights = lines.stream().map(s -> Integer.parseInt(s)).collect(Collectors.toList());

		// Part 1
		long startTime = System.nanoTime();

		int targetWeigth = (weights.stream().mapToInt(i->i).sum()) / 3;
		BigInteger lowerQE = recurse(weights, new ArrayList<Integer>(), targetWeigth, 0, 0, null);
		System.out.println("Result part 1 : " + lowerQE + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		targetWeigth = (weights.stream().mapToInt(i->i).sum()) / 4;
		lowerQE = recurse(weights, new ArrayList<Integer>(), targetWeigth, 0, 0, null);
			
		System.out.println("Result part 2 : " + lowerQE + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	private static BigInteger recurse(List<Integer> weights, List<Integer> container, int targetWeigth, int index, int weightSum, BigInteger lowestQE) {
		
		
		if (weights.isEmpty()) throw new IllegalStateException();

		if (weightSum == targetWeigth) { 
			BigInteger qe = container.isEmpty() ? null : container.stream().map(v -> BigInteger.valueOf(v)).reduce(BigInteger.valueOf(1), (a,b) -> a.multiply(b));
			return qe; 
		}
		
		for (int i=index;i<weights.size();i++) {

			int weight = weights.get(i);
			weightSum += weight;
			
			if (weightSum <= targetWeigth) {
				container.add(weight);
				BigInteger newQE = recurse(weights, container, targetWeigth, i+1, weightSum, lowestQE);
				if (lowestQE == null || (newQE != null && newQE.compareTo(lowestQE) < 0)) {
					lowestQE = newQE;
				}
				container.remove(container.size()-1);
			}
			weightSum -= weight;
			
		}
		
		return lowestQE;
	}	
}
