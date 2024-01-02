import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Day10 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input/input_day10.txt"));

		String sequence = lines.get(0);
		
		// Part 1
		long startTime = System.nanoTime();
		
		for (int i=0;i<40;i++) {
			sequence = lookAndSay(sequence);
		}
		
		int result = sequence.length();

		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		sequence = lines.get(0);
		for (int i=0;i<50;i++) {
			sequence = lookAndSay(sequence);
		}
		result = sequence.length();
		System.out.println("Result part 2 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	private static String lookAndSay(String sequence) {
		char lastChar = '#';
		int counter = 0;
		StringBuffer output = new StringBuffer();
		for (int i=0;i<sequence.length();i++) {
			char newChar = sequence.charAt(i);
			if (i==0) {
				lastChar = newChar;
				counter = 1;
			}
			else {
				if (newChar == lastChar) counter++;
				else {
					output.append(counter);
					output.append(lastChar);
					lastChar = newChar;
					counter = 1;
				}
			}
		}
		output.append(counter);
		output.append(lastChar);
		return output.toString();
	}
}
