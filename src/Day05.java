import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Day05 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day05.txt"));

		// Part 1
		long startTime = System.nanoTime();
		
		final Character[] vowels = new Character[] { 'a', 'e', 'i', 'o', 'u' };
		final List<String> rejected = Arrays.asList( "ab", "cd", "pq", "xy");
		Stream<String> filtered = lines.stream().filter(l -> l.chars().filter(c -> Arrays.stream(vowels).anyMatch(element -> element == c)).count() > 2);
		filtered = filtered.filter(l -> {
			for (int i=0;i<l.length()-1;i++) { if (l.charAt(i) == l.charAt(i+1)) return true; } return false;
		});
		filtered = filtered.filter(l -> {
			for (String reject : rejected) { if (l.contains(reject)) return false; } return true;
		});
		
		
		System.out.println("Result part 1 : " + filtered.count() + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		
		filtered = lines.stream().filter(l -> {
			for (int i=0;i<l.length()-1;i++) {
				String s = l.substring(i, i+2);
				if (l.substring(i+2).indexOf(s) >= 0) return true;
			}
			return false;
		});
		filtered = filtered.filter(l -> {
			for (int i=0;i<l.length()-2;i++) { if (l.charAt(i) == l.charAt(i+2)) return true; } return false;
		});
		
		System.out.println("Result part 2 : " + filtered.count() + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
}
