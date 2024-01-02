import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day14 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input/input_day14.txt"));

		// Part 1
		long startTime = System.nanoTime();
		
		Map<String, Reindeer> reindeers = new HashMap<String, Reindeer>();

		Pattern pattern = Pattern.compile("([a-zA-Z]+) can fly ([0-9]+) km/s for ([0-9]+) seconds, but then must rest for ([0-9]+) seconds.");
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			matcher.find();
			String name = matcher.group(1);
			Reindeer attendee = reindeers.computeIfAbsent(name, n -> new Reindeer(n));
			attendee.setSpeed(Integer.parseInt(matcher.group(2)));
			attendee.setFlyingTime(Integer.parseInt(matcher.group(3)));
			attendee.setRestTime(Integer.parseInt(matcher.group(4)));
		}

		// 945 too low
		int result = reindeers.values().stream().mapToInt(r -> r.getTraveled(2503)).max().orElseThrow();

		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		
		List<Reindeer> allReindeers = new ArrayList<Reindeer>(reindeers.values());
		for (int i=1;i<=2503;i++) {
			final int elapsed = i;
			Map<Integer, List<Reindeer>> reindeerByDistance = allReindeers.stream().collect(Collectors.groupingBy(r -> r.getTraveled(elapsed)));
			List<Reindeer> leaders = reindeerByDistance.get(Collections.max(reindeerByDistance.keySet()));
			for (Reindeer reindeer : leaders) {
				reindeer.awardPoint();
			}
		}
		
		result = allReindeers.stream().mapToInt(Reindeer::getScore).max().orElseThrow();
		
		System.out.println("Result part 2 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	public static class Reindeer {

		private String name;
		private int speed;
		private int restTime;
		private int flyingTime;
		private int score;

		public Reindeer(String name) {
			this.name = name;
		}

		public int getScore() {
			return score;
		}

		public void awardPoint() {
			this.score++;
		}

		public int getTraveled(int elapsed) {
			int traveled = (elapsed / (restTime + flyingTime)) * (flyingTime * speed);
			elapsed = elapsed % (restTime + flyingTime);
			traveled += Math.min(flyingTime, elapsed) * speed;
			return traveled;
		}

		public void setRestTime(int restTime) {
			this.restTime = restTime;
		}

		public void setFlyingTime(int flyingTime) {
			this.flyingTime = flyingTime;
		}

		public void setSpeed(int speed) {
			this.speed = speed;
		}
	}
}
