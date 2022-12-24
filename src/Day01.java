import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day01 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day01.txt"));

		String line = lines.get(0);
		long openedCount = line.chars().filter(c -> c == '(').count();
		long closedCount = line.chars().filter(c -> c == ')').count();

		System.out.println(openedCount - closedCount);
		
		int level = 0;
		int index = 0;
		while (level >= 0) {
			char c = line.charAt(index++);
			level += c == '(' ? 1 : -1;
		}
		System.out.println(index);
	}

}
