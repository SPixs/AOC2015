import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day25 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input/input_day25.txt"));
		
		// To continue, please consult the code grid in the manual.  Enter the code at row 2981, column 3075.
		Pattern pattern = Pattern.compile("To continue, please consult the code grid in the manual.  Enter the code at row (\\d+), column (\\d+).");
		Matcher matcher = pattern.matcher(lines.get(0));
		matcher.find();
		int row = Integer.parseInt(matcher.group(1));
		int col = Integer.parseInt(matcher.group(2));
		
		// code number on first column of row
		int code = 1 + row*(row-1)/2;
		code = code + (col-1) * row + (col*(col-1)/2);
		
		// Part 1
		long startTime = System.nanoTime();
		long value = 20151125;
		for (int i=1;i<code;i++) {
			value *= 252533;
			value = value % 33554393;
		}
		System.out.println("Result part 1 : " + value + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}	
}
