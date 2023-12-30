import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Day18 {

	static int width = 0;
	static int height = 0;
	static char[][] map;
	
	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day18.txt"));

		// Part 1
		long startTime = System.nanoTime();
		int result = 0;
		
		// Build map
		width = lines.get(0).length();
		height = lines.size();
		
		int ly = 0;
		map = new char[width][height];
		for (String line : lines) {
			for (int lx=0;lx<line.length();lx++) {
				map[lx][ly] = line.charAt(lx);
			}
			ly++;
		}
		
		for (int i=0;i<100;i++) {
			animateMap();
		}
		
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				result += map[x][y] == '#' ? 1 : 0;
			}
		}
		
		System.out.println("Result part 1 : " + result + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		
		// reset map
		ly = 0;
		for (String line : lines) {
			for (int lx=0;lx<line.length();lx++) {
				map[lx][ly] = line.charAt(lx);
			}
			ly++;
		}

		for (int i=0;i<100;i++) {
			animateMap();
			map[0][0] = map[width-1][0] = map[0][height-1] = map[width-1][height-1] = '#';
		}
		
		result = 0;
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				result += map[x][y] == '#' ? 1 : 0;
			}
		}
		
		System.out.println("Result part 2 : " + result + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	private static void animateMap() {
		char[][] newMap = new char[width][height];
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				int surroundingCount = getSurroundingCount(x, y);
				if (map[x][y] == '#') {
					newMap[x][y] = (surroundingCount == 2 || surroundingCount == 3) ? '#' : '.';
				}
				else {
					newMap[x][y] = (surroundingCount == 3) ? '#' : '.';
				}
			}
		}
		map = newMap;
	}
	
	public static int getSurroundingCount(int x, int y) {
		int count = 0;
		for (int tx=x-1;tx<=x+1;tx++) {
			for (int ty=y-1;ty<=y+1;ty++) {
				if (tx >= 0 && tx < width && ty >= 0 && ty < height && (tx != x || ty != y)) {
					count += map[tx][ty] == '#' ? 1 : 0;
				}
			}			
		}
		return count;
	}
}
