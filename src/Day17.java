import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Day17 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day17.txt"));

		List<Integer> containers = lines.stream().map(Integer::parseInt).collect(Collectors.toList());
		
		// Part 1
		long startTime = System.nanoTime();
		int result = 0;
		AtomicInteger counter = new AtomicInteger(0);
		TreeMap<Integer, Integer> combinaisonsByContainersCount = new TreeMap<Integer, Integer>();
		recurse(containers, 150, counter, 0, combinaisonsByContainersCount);
		System.out.println("Result part 1 : " + counter + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		result = combinaisonsByContainersCount.get(combinaisonsByContainersCount.keySet().iterator().next());
		System.out.println("Result part 2 : " + result + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
	
	public static void recurse(List<Integer> containers, int remainingLiters, AtomicInteger counter, int usedContainer, TreeMap<Integer, Integer> combinaisonsByContainersCount) {
        if (containers.isEmpty() && remainingLiters == 0) {
        	counter.incrementAndGet();
        	Integer combinaisonsCount = combinaisonsByContainersCount.computeIfAbsent(usedContainer, i -> 0);
        	combinaisonsByContainersCount.put(usedContainer, combinaisonsCount+1);
        } 
        else if (containers.size() > 0) {
            for (int i = 0; i <= 1; i++) {
            	int capacity = containers.get(0);
                if (i * capacity <= remainingLiters) {
                	containers.remove(0);
                	recurse(containers, remainingLiters - i * capacity, counter, (i == 0) ? usedContainer : usedContainer + 1, combinaisonsByContainersCount);
                	containers.add(0, capacity);
                }
            }
        }
    }
}
