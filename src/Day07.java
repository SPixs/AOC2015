import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day07 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day07.txt"));

		// Part 1
		long startTime = System.nanoTime();
		int result = 0;

		Pattern patternLink = Pattern.compile("(.+?)\\s*->\\s*(.+)");
		Pattern patternLogical = Pattern.compile("([a-z0-9]+) (AND|OR|LSHIFT|RSHIFT) ([a-z0-9]+) -> ([a-z]+)");
		Pattern patternNot = Pattern.compile("NOT ([a-z0-9]+) -> ([a-z]+)");
		
		Map<String, Connector> wires = new HashMap<String, Connector>();
		
		for (String line : lines) {

			Matcher matcher = patternLink.matcher(line);
			if (!matcher.find()) { throw new IllegalStateException(line); }
			String wireName = matcher.group(2);
			String leftSideHand = matcher.group(1);
			
			matcher = patternLogical.matcher(line);
			if (matcher.find()) {
				String input1 = matcher.group(1);
				String input2 = matcher.group(3);
				switch (matcher.group(2)) {
					case "AND" : wires.put(wireName, new Gate(wireName, Logic.AND, input1, input2)); break;
					case "OR" : wires.put(wireName, new Gate(wireName, Logic.OR, input1, input2)); break;
					case "RSHIFT" : wires.put(wireName, new Shift(wireName, input1, Integer.parseInt(input2))); break;
					case "LSHIFT" : wires.put(wireName, new Shift(wireName, input1, -Integer.parseInt(input2))); break;
				}
				
			}
			else if (patternNot.matcher(line).find()) {
				matcher = patternNot.matcher(line);
				matcher.find();
				String input1 = matcher.group(1);
				wires.put(wireName, new Gate(wireName, Logic.NOT, input1, null));
			}
			else {
				String input1 = leftSideHand.trim();
				wires.put(wireName, new Direct(wireName, input1));
			}
		}

		result = wires.get("a").eval(wires);
		
		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		wires.put("b", new Direct("b", String.valueOf(result)));
		wires.values().forEach(w -> w.cached = null);
		result = wires.get("a").eval(wires);
		
		startTime = System.nanoTime();
		System.out.println("Result part 2 : " + result + " in "	+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
	
	public static abstract class Connector {
	
		private Integer cached = null;
		protected String wireName;

		public Connector(String wireName) {
			this.wireName = wireName;
		}

		public int eval(Map<String, Connector> wires) {
			if (cached == null) cached = evalImpl(wires);
			return cached;
		}
		 
		public abstract int evalImpl(Map<String, Connector> wires);
		
	}
	
	public static enum Logic {
		AND, OR, NOT
	}
	
	public static class Direct extends Connector {
		private String in;

		public Direct(String wireName, String in) {
			super(wireName);
			this.in = in;
		}

		@Override
		public int evalImpl(Map<String, Connector> wires) {
			int value = in.matches("\\d+") ? Integer.parseInt(in) : wires.get(in).eval(wires);
			return value;
		}
		
		@Override
		public String toString() {
			return in + " -> " + wireName;
		}
	}
	
	public static class Shift extends Connector {

		private int shiftValue;
		private String in;

		public Shift(String wireName, String in, int shiftValue) {
			super(wireName);
			if (in == null) throw new IllegalArgumentException();
			this.in = in;
			this.shiftValue = shiftValue;
		}
		
		@Override
		public int evalImpl(Map<String, Connector> wires) {
			int value = in.matches("\\d+") ? Integer.parseInt(in) : wires.get(in).eval(wires);
			return (shiftValue >=0) ? ((value & 0x0FFFF) >> shiftValue) : ((value << Math.abs(shiftValue)) & 0x0FFFF);
		}
		
		@Override
		public String toString() {
			if (shiftValue >=0) { return in + " RSHIFT " + shiftValue + " -> " + wireName; }
			else return in + " LSHIFT " + Math.abs(shiftValue) + " -> " + wireName; 
		}
	}

	public static class Gate extends Connector {
		
		private String in1;
		private String in2;
		private Logic logic;

		public Gate(String wireName, Logic logic, String in1, String in2) {
			super(wireName);
			this.logic = logic;
			this.in1 = in1;
			this.in2 = in2;
		}

		@Override
		public int evalImpl(Map<String, Connector> wires) {
			int value1 = in1.matches("\\d+") ? Integer.parseInt(in1) : wires.get(in1).eval(wires);
			int value2 = in2 == null ? 0 : (in2.matches("\\d+") ? Integer.parseInt(in2) : wires.get(in2).eval(wires));

			switch (logic) {
				case AND: return (value1 & value2) & 0x0FFFF;
				case OR: return (value1 | value2) & 0x0FFFF;
				case NOT:  return (~value1) & 0x0FFFF;
				default: throw new IllegalStateException();
			}
		}
		
		@Override
		public String toString() {
			switch (logic) {
				case AND: return in1 + " AND " + in2 + " -> " + wireName;
				case OR:  return in1 + " OR " + in2 + " -> " + wireName;
				case NOT:  return "NOT " + in1 + " -> " + wireName;
				default: throw new IllegalStateException();
			}
		}
	}
}
