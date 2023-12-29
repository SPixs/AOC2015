import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day06 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day06.txt"));

		boolean[][] lights = new boolean[1000][1000];

		// Part 1
		long startTime = System.nanoTime();
		int result = 0;

		Pattern pattern = Pattern.compile("(\\d+),(\\d+) through (\\d+),(\\d+)");
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				int[] coordinates = IntStream.rangeClosed(1, 4).map(i -> Integer.parseInt(matcher.group(i))).toArray();
				if (line.startsWith("toggle")) {
					toggle(coordinates, lights);
				}
				else {
					set(coordinates, lights, line.startsWith("turn on"));
				}
			}
		}
		
		for (int x=0;x<=999;x++) {
			for (int y=0;y<=999;y++) {
				result += lights[x][y] ? 1 : 0;
			}			
		}
		
		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		int[][] luminance = new int[1000][1000];
		for (String line : lines) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				int[] coordinates = IntStream.rangeClosed(1, 4).map(i -> Integer.parseInt(matcher.group(i))).toArray();
				if (line.startsWith("toggle")) {
					increase(coordinates, luminance, 2);
				}
				else {
					increase(coordinates, luminance, line.startsWith("turn on") ? 1 : -1);
				}
			}
		}
		
		result = Arrays.stream(luminance).mapToInt(row -> Arrays.stream(row).sum()).sum();
		
		System.out.println("Result part 2 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	private static void increase(int[] coordinates, int[][] luminance, int i) {
		for (int x=coordinates[0];x<=coordinates[2];x++) {
			for (int y=coordinates[1];y<=coordinates[3];y++) {
				luminance[x][y] = Math.max(0, luminance[x][y]+i);
			}			
		}
	}

	private static void set(int[] coordinates, boolean[][] lights, boolean b) {
		for (int x=coordinates[0];x<=coordinates[2];x++) {
			for (int y=coordinates[1];y<=coordinates[3];y++) {
				lights[x][y] = b;
			}			
		}
	}

	private static void toggle(int[] coordinates, boolean[][] lights) {
		for (int x=coordinates[0];x<=coordinates[2];x++) {
			for (int y=coordinates[1];y<=coordinates[3];y++) {
				lights[x][y] = !lights[x][y];
			}			
		}
	}
}
