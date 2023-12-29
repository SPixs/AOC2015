import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day13 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day13.txt"));

		// Part 1
		long startTime = System.nanoTime();
		int result = 0;
		
		Map<String, Attendee> attendees = new HashMap<String, Attendee>();
		
		Pattern pattern = Pattern.compile("([a-zA-Z]+) would (gain|lose) ([0-9]+) happiness units by sitting next to ([a-zA-Z]+).");
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			matcher.find();
			String attendeeName = matcher.group(1);
			boolean gain = "gain".equals(matcher.group(2));
			int happiness = Integer.parseInt(matcher.group(3)) * (gain ? 1 : -1);
			String neighbour = matcher.group(4);
			
			Attendee attendee = attendees.computeIfAbsent(attendeeName, n -> new Attendee(n));
			attendee.addHappiness(attendees.computeIfAbsent(neighbour, n -> new Attendee(n)), happiness);
		}

		List<Attendee> candidates = new ArrayList<Attendee>(attendees.values());
		List<Attendee> selection = new ArrayList<Attendee>();
		int bestHappiness = recurse(selection, candidates);
		
		System.out.println("Result part 1 : " + bestHappiness + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		
		Attendee me = new Attendee("me");
		candidates.add(me);
		bestHappiness = recurse(selection, candidates);
		
		System.out.println("Result part 2 : " + bestHappiness + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
	
	private static int recurse(List<Attendee> selection, List<Attendee> candidates) {
		if (candidates.isEmpty()) {
			int happiness = 0;
			int size = selection.size();
			for (int i=0;i<size;i++) {
				Attendee attendee = selection.get(i);
				Attendee neighbour1 = selection.get((((i-1) % size) + size) % size);
				Attendee neighbour2 = selection.get((((i+1) % size) + size) % size);
				happiness += attendee.getHappiness(neighbour1);
				happiness += attendee.getHappiness(neighbour2);
			}
			return happiness;
		}
		int bestHappiness = Integer.MIN_VALUE;
		for (Attendee candidate : new ArrayList<Attendee>(candidates)) {
			candidates.remove(candidate);
			selection.add(candidate);
			bestHappiness = Math.max(bestHappiness, recurse(selection, candidates));
			selection.remove(candidate);
			candidates.add(candidate);
		}
		return bestHappiness;
	}

	public static class Attendee {
		
		public String name;
		public Map<Attendee, Integer> happiness = new HashMap<Attendee, Integer>();

		public Attendee(String name) {
			this.name = name;
		}

		public int getHappiness(Attendee neighbour) {
			return happiness.getOrDefault(neighbour, 0);
		}

		public void addHappiness(Attendee neighbour, int happiness) {
			this.happiness.put(neighbour, happiness);
		}
	}
}
