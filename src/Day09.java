import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day09 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input/input_day09.txt"));

		// Part 1
		long startTime = System.nanoTime();
		
		Map<Pair<String, String>, Integer> distances = new HashMap<Pair<String, String>, Integer>();

		Pattern patternLogical = Pattern.compile("([a-zA-Z]+) to ([a-zA-Z]+) = ([0-9]+)");
		Set<String> allTowns = new HashSet<String>();
		for (String line : lines) {
			Matcher matcher = patternLogical.matcher(line);
			if (matcher.find()) {
				String from = matcher.group(1);
				String to = matcher.group(2);
				int distance = Integer.parseInt(matcher.group(3));
				distances.put(Pair.create(from, to), distance);
				distances.put(Pair.create(to, from), distance);
				allTowns.add(from);
				allTowns.add(to);
			}
		}
		
		int result = recurse(new ArrayList<String>(), new ArrayList<String>(allTowns), distances);

		System.out.println("Result part 1 : " + result + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		result = recursePart2(new ArrayList<String>(), new ArrayList<String>(allTowns), distances);
		System.out.println("Result part 2 : " + result + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
	
	public static int recurse(List<String> towns, List<String> candidates, Map<Pair<String, String>, Integer> distances) {
		if (candidates.isEmpty()) {
			int distance = 0;
			for (int i=0;i<towns.size()-1;i++) distance += distances.get(Pair.create(towns.get(i), towns.get(i+1)));
			return distance;
		}
		int distance = Integer.MAX_VALUE;
		for (String candidate : new ArrayList<String>(candidates)) {
			towns.add(candidate);
			candidates.remove(candidate);
			distance = Math.min(distance, recurse(towns, candidates, distances));
			candidates.add(candidate);
			towns.remove(candidate);
		}
		return distance;
	}
	
	public static int recursePart2(List<String> towns, List<String> candidates, Map<Pair<String, String>, Integer> distances) {
		if (candidates.isEmpty()) {
			int distance = 0;
			for (int i=0;i<towns.size()-1;i++) distance += distances.get(Pair.create(towns.get(i), towns.get(i+1)));
			return distance;
		}
		int distance = Integer.MIN_VALUE;
		for (String candidate : new ArrayList<String>(candidates)) {
			towns.add(candidate);
			candidates.remove(candidate);
			distance = Math.max(distance, recursePart2(towns, candidates, distances));
			candidates.add(candidate);
			towns.remove(candidate);
		}
		return distance;
	}

	public static class Pair<K, V> {
	    
	    private final K key;
	    private final V value;

	    public Pair(K k, V v) {
	        key = k;
	        value = v;
	    }
	    public K getKey() { return key; }
	    public V getValue() { return value; }

	    public boolean equals(Object o) {
	        if (this == o) {
	            return true;
	        }
	        if (!(o instanceof Pair)) {
	            return false;
	        } else {
	            Pair<?, ?> oP = (Pair<?, ?>) o;
	            return (key == null ?
	                    oP.key == null :
	                    key.equals(oP.key)) &&
	                (value == null ?
	                 oP.value == null :
	                 value.equals(oP.value));
	        }
	    }

	    public int hashCode() {
	        int result = key == null ? 0 : key.hashCode();

	        final int h = value == null ? 0 : value.hashCode();
	        result = 37 * result + h ^ (h >>> 16);

	        return result;
	    }

	    @Override
	    public String toString() {
	        return "[" + getKey() + ", " + getValue() + "]";
	    }

	    public static <K, V> Pair<K, V> create(K k, V v) {
	        return new Pair<K, V>(k, v);
	    }
	}
}
