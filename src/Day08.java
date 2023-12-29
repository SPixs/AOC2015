import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Day08 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day08.txt"));

		// Part 1
		long startTime = System.nanoTime();
		int codeCharactersCount = lines.stream().mapToInt(l -> l.trim().length()).sum();
		int memoryCharactersCount = lines.stream().mapToInt(l -> unescape(l.substring(1, l.length() - 1)).length()).sum();

		int result = codeCharactersCount - memoryCharactersCount;

		System.out.println("Result part 1 : " + result + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();

		int encodedCharactersCount = lines.stream().mapToInt(l -> escape(l, true).length()).sum();
		result = encodedCharactersCount - codeCharactersCount;
		
		System.out.println("Result part 2 : " + result + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	public static String escape(String input, boolean inQuotes) {
		if (input == null) {
			return null;
		}
		StringBuffer output = new StringBuffer();
		if (inQuotes) output.append("\"");
		int size = input.length();
		for (int i = 0; i < size; i++) {
			char ch = input.charAt(i);
			switch (ch) {
				case '\"': output.append("\\\""); break;
				case '\\': output.append("\\\\"); break;
				default: output.append(ch); break;
			}
		}
		if (inQuotes) output.append("\"");
		return output.toString();
	}

	/**
	 * Based on Apache StringEscapeUtils
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static String unescape(String input) {
		if (input == null) {
			return null;
		}
		StringBuffer output = new StringBuffer();
		int size = input.length();
		StringBuilder hexacode = new StringBuilder(2);
		boolean hadSlash = false;
		boolean inHexacode = false;
		for (int i = 0; i < size; i++) {
			char ch = input.charAt(i);
			if (inHexacode) {
				// if in ASCII code escape, then we're building hexadecimal value of character
				hexacode.append(ch);
				if (hexacode.length() == 2) {
					// hexacode now contains the 2 hex digits which represents our ASCII character
					try {
						int value = Integer.parseInt(hexacode.toString(), 16);
						output.append((char) value);
						hexacode.setLength(0);
						inHexacode = false;
						hadSlash = false;
					} catch (NumberFormatException nfe) {
						System.err.println("Unable to parse ASCII value: " + hexacode);
						return null;
					}
				}
				continue;
			}
			if (hadSlash) {
				// handle an escaped value
				hadSlash = false;
				switch (ch) {
				case '\\':
					output.append('\\');
					break;
				case '\"':
					output.append('"');
					break;
				case 'x':
					inHexacode = true;
					break;
				default:
					output.append(ch);
					break;
				}
				continue;
			} else if (ch == '\\') {
				hadSlash = true;
				continue;
			}
			output.append(ch);
		}
		if (hadSlash) {
			// then we're in the weird case of a \ at the end of the string, let's output it
			// anyway.
			output.append('\\');
		}
		return output.toString();
	}
}
