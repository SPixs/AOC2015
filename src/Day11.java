import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Day11 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input/input_day11.txt"));

		String password = lines.get(0);
		
		// Part 1
		long startTime = System.nanoTime();
		
		byte[] passwordBase26 = new byte[8];
		for (int i=0;i<8;i++) {
			passwordBase26[7-i] = (byte) (password.charAt(i));
		}

		while (!valid(passwordBase26)) {
			increment(passwordBase26);
		}
		
		System.out.println("Result part 1 : " + reversedList(passwordBase26) + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		increment(passwordBase26);
		while (!valid(passwordBase26)) {
			increment(passwordBase26);
		}

		System.out.println("Result part 2 : " + reversedList(passwordBase26) + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	private static String reversedList(byte[] passwordBase26) {
		StringBuffer resultPassword = new StringBuffer();
		for (byte b : passwordBase26) {
			resultPassword.append((char)(b));
		}
		resultPassword.reverse();
		return resultPassword.toString();
	}

	private static void increment(byte[] passwordBase26) {
		boolean remind = true;
		int index = 0;
		while (remind) {
			passwordBase26[index]++;
			remind = passwordBase26[index] == 'z'+1;
			if (remind) {
				passwordBase26[index] = 'a';
			}
			index++;
		}
	}

	private static boolean valid(byte[] passwordBase26) {
		// straight of at least three letters
		boolean straight = false;
		for (int i=0;i<passwordBase26.length-2 && !straight;i++) {
			straight |= (passwordBase26[i] == (passwordBase26[i+1] + 1)) && (passwordBase26[i+1] == (passwordBase26[i+2] + 1));
		}
		if (!straight) return false;
		
		// Passwords may not contain the letters i, o, or l
		for (int c : passwordBase26) {
			if (c == 'i' || c == 'o' || c == 'l') return false;
		}
		
		// Passwords must contain at least two different, non-overlapping pairs of letters, like aa, bb, or zz.
		int groupCount = 0;
		int lastPairLetter = -1;
		for (int i=0;i<passwordBase26.length-1;i++) {
			if (passwordBase26[i] != lastPairLetter && passwordBase26[i] == passwordBase26[i+1]) {
				groupCount++;
				lastPairLetter = passwordBase26[i];
			}
		}

		return groupCount > 1;
	}
}
