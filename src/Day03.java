import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day03 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day03.txt"));

		String line = lines.get(0);
		Point point = new Point(0, 0);
		Set<Point> visited = new HashSet<Point>();
		visited.add(point);
		
		for (char c : line.toCharArray()) {
			switch (c) {
				case '<':
					point = new Point(point.x-1, point.y);
					visited.add(point);
					break;
				case '>':
					point = new Point(point.x+1, point.y);
					visited.add(point);
					break;
				case '^':
					point = new Point(point.x, point.y-1);
					visited.add(point);
					break;
				case 'v':
					point = new Point(point.x, point.y+1);
					visited.add(point);
					break;
				default:
					throw new IllegalStateException();
			}
		}
		System.out.println(visited.size());
		
		visited.clear();
		Point[] pointPair = new Point[] { new Point(0, 0), new Point(0, 0) };
		visited.add(new Point(0, 0));
		int index = 0;
		for (char c : line.toCharArray()) {
			point = pointPair[index];
			switch (c) {
				case '<':
					pointPair[index] = new Point(point.x-1, point.y);
					visited.add(pointPair[index]);
					break;
				case '>':
					pointPair[index] = new Point(point.x+1, point.y);
					visited.add(pointPair[index]);
					break;
				case '^':
					pointPair[index] = new Point(point.x, point.y-1);
					visited.add(pointPair[index]);
					break;
				case 'v':
					pointPair[index] = new Point(point.x, point.y+1);
					visited.add(pointPair[index]);
					break;
				default:
					throw new IllegalStateException();
			}
			index = 1-index;
		}
		System.out.println(visited.size());
	}
	

	public static class Point {
		int x;
		int y;
		
		public Point(int x, int y) {
			super();
			this.x = x;
			this.y = y;
		}

		public void move(int dx, int dy) {
			x += dx;
			y += dy;
		}

		public Point() {
		}

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}
	
}
