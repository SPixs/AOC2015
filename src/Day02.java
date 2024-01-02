import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day02 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input/input_day02.txt"));

		int surface = 0;
		int ribbon = 0;
		
		for (String line : lines) {
			List<Integer> collect = Stream.of(line.split("x")).map(Integer::parseInt).collect(Collectors.toList());
			Collections.sort(collect);
			int w = collect.get(0);
			int h = collect.get(1);
			int l = collect.get(2);
			surface += 2*l*w + 2*w*h + 2*h*l + w * h;

			List<Integer> perimeters = new ArrayList<Integer>();
			perimeters.add(w+w+h+h);
			perimeters.add(w+w+l+l);
			perimeters.add(h+h+l+l);
			ribbon += perimeters.stream().min(Integer::compare).get();
			ribbon += w*h*l;
		}
		
		System.out.println(surface);
		System.out.println(ribbon);
	}

}
